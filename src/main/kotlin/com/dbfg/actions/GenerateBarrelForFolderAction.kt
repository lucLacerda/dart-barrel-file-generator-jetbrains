package com.dbfg.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.vfs.VirtualFile
import com.dbfg.utils.BarrelStatistics
import com.dbfg.utils.NotificationUtil

/**
 * Action to generate a barrel file for the selected folder.
 */
class GenerateBarrelForFolderAction : BaseBarrelAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val directory = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        
        if (!directory.isDirectory) return
        
        // Inicializar estatísticas
        val statistics = BarrelStatistics()
        statistics.incrementDirectoriesProcessed()
        
        // Verificar se o diretório deve ser excluído
        val dirPath = toSystemPath(directory).toString()
        if (shouldExcludeDirectory(dirPath)) {
            statistics.incrementDirectoriesExcluded()
            NotificationUtil.showWarning(
                project,
                "Diretório excluído",
                "O diretório ${directory.path} foi excluído conforme configurações."
            )
            return
        }
        
        // Coletar arquivos Dart válidos no diretório atual
        val dartFiles = mutableListOf<String>()
        
        // Processar arquivos do diretório atual
        for (file in directory.children) {
            if (!file.isDirectory) {
                val fileName = file.name
                val filePath = toSystemPath(file).toString()
                
                statistics.incrementFilesProcessed()
                
                // Verificar se é um arquivo Dart válido e não é o próprio arquivo barrel
                if (isDartFile(fileName) && 
                    !isBarrelFile(directory.name, fileName) && 
                    !shouldExcludeFile(fileName, filePath)) {
                    dartFiles.add(fileName)
                    statistics.incrementFilesExported()
                } else if (isDartFile(fileName)) {
                    statistics.incrementFilesExcluded()
                }
            }
        }
        
        // Check if there are files to export
        if (dartFiles.isEmpty()) {
            if (settings.skipEmpty) {
                NotificationUtil.showWarning(
                    project,
                    "No files to export",
                    "No valid Dart files were found to export in ${directory.path}."
                )
                return
            }
        }
        
        // Generate barrel file content
        val content = generateBarrelContent(directory, dartFiles)
        
        // Criar arquivo barrel
        createBarrelFile(directory, content, project)
        statistics.incrementBarrelFilesGenerated()
        
        // Mostrar estatísticas ao usuário
        NotificationUtil.showInfo(
            project,
            "Dart Barrel File Generator",
            statistics.getSummary()
        )
    }
}
