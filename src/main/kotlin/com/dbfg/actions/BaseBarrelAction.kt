package com.dbfg.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import com.dbfg.settings.DartBarrelSettings
import com.dbfg.utils.DartFileUtils
import com.dbfg.utils.GlobMatcher
import com.dbfg.utils.NotificationUtil
import java.io.IOException

/**
 * Base class for barrel file generation actions.
 */
abstract class BaseBarrelAction : AnAction() {
    protected val settings = service<DartBarrelSettings>()

    /**
     * Verifica se a ação deve estar habilitada com base no contexto atual.
     */
    @Override
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
    
    override fun update(e: AnActionEvent) {
        val project = e.project
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        
        e.presentation.isEnabledAndVisible = project != null && file != null && file.isDirectory
    }

    /**
     * Obtém o nome do arquivo barrel com base nas configurações.
     * Se promptName estiver habilitado, solicita o nome ao usuário.
     */
    protected fun getBarrelFileName(directory: VirtualFile, project: Project): String? {
        val dirName = directory.name
        
        return if (settings.promptName) {
            Messages.showInputDialog(
                project,
                "Digite o nome do arquivo barrel (sem a extensão .dart):",
                "Nome do Arquivo Barrel",
                null,
                settings.defaultBarrelName ?: dirName,
                null
            )?.takeIf { it.isNotBlank() }?.plus(".dart")
        } else {
            (settings.defaultBarrelName ?: dirName).plus(".dart")
        }
    }

    /**
     * Checks if a file is a valid Dart file for export.
     */
    protected fun isDartFile(fileName: String): Boolean {
        return DartFileUtils.isDartFile(fileName)
    }

    /**
     * Verifica se um arquivo é o próprio arquivo barrel.
     */
    protected fun isBarrelFile(dirName: String, fileName: String): Boolean {
        return DartFileUtils.isBarrelFile(dirName, fileName)
    }

    /**
     * Verifica se um arquivo deve ser excluído com base nas configurações.
     */
    protected fun shouldExcludeFile(fileName: String, filePath: String): Boolean {
        // Verificar arquivos .freezed.dart
        if (DartFileUtils.isFreezedFile(fileName) && settings.excludeFreezed) {
            return true
        }
        
        // Verificar arquivos .g.dart
        if (DartFileUtils.isGeneratedFile(fileName) && settings.excludeGenerated) {
            return true
        }
        
        // Verificar padrões de exclusão personalizados
        return GlobMatcher.matchesAny(filePath, settings.excludeFileList)
    }

    /**
     * Verifica se um diretório deve ser excluído com base nas configurações.
     */
    protected fun shouldExcludeDirectory(dirPath: String): Boolean {
        return GlobMatcher.matchesAny(dirPath, settings.excludeDirList)
    }

    /**
     * Gera o conteúdo do arquivo barrel.
     */
    protected fun generateBarrelContent(
        directory: VirtualFile,
        files: List<String>,
        subdirectories: Map<String, List<String>> = emptyMap()
    ): String {
        val sb = StringBuilder()

        // Exportar arquivos do diretório atual
        files.sorted().forEach { fileName ->
            val exportPath = if (settings.prependPackageToLibExport && DartFileUtils.isLibDirectory(directory)) {
                DartFileUtils.formatExportPath(directory, fileName)
            } else {
                fileName
            }
            
            // Adicionar prefixo/sufixo do nome da pasta se configurado
            val finalPath = when {
                settings.prependFolderName -> "${directory.name}/$exportPath"
                settings.appendFolderName -> "$exportPath.${directory.name}"
                else -> exportPath
            }
            
            sb.appendLine("export '$finalPath';")
        }
        
        // Exportar arquivos de subdiretórios
        if (subdirectories.isNotEmpty()) {
            sb.appendLine()
            subdirectories.entries.sortedBy { it.key }.forEach { (subdir, subFiles) ->
                if (subFiles.isNotEmpty()) {
                    subFiles.sorted().forEach { fileName ->
                        val exportPath = "$subdir/$fileName"
                        val formattedPath = if (settings.prependPackageToLibExport && DartFileUtils.isLibDirectory(directory)) {
                            DartFileUtils.formatExportPath(directory, exportPath)
                        } else {
                            exportPath
                        }
                        sb.appendLine("export '$formattedPath';")
                    }
                }
            }
        }
        
        return sb.toString()
    }

    /**
     * Cria um arquivo barrel no diretório especificado.
     */
    protected fun createBarrelFile(directory: VirtualFile, content: String, project: Project) {
        try {
            val barrelFileName = settings.getBarrelFileName(directory)
            
            // Use runWriteAction to modify the VFS
            runWriteAction {
                val barrelFile = directory.findChild(barrelFileName) ?: directory.createChildData(this, barrelFileName)
                barrelFile.setBinaryContent(content.toByteArray())
            }
            
            NotificationUtil.showSuccess(
                project,
                "Dart Barrel File Generator",
                "Barrel file created successfully in ${directory.path}"
            )
        } catch (e: IOException) {
            NotificationUtil.showError(
                project,
                "Error",
                "Error creating barrel file: ${e.message}"
            )
        }
    }

    /**
     * Converts a virtual path to a file system path.
     */
    protected fun toSystemPath(file: VirtualFile): java.nio.file.Path {
        return DartFileUtils.toSystemPath(file)
    }
}
