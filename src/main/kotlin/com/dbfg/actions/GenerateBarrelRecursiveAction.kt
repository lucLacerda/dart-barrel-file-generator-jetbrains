package com.dbfg.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.vfs.VirtualFile
import com.dbfg.utils.BarrelStatistics
import com.dbfg.utils.DartFileUtils
import com.dbfg.utils.NotificationUtil

/**
 * Action to generate barrel files recursively for the selected folder and its subfolders.
 */
class GenerateBarrelRecursiveAction : BaseBarrelAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val directory = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        
        if (!directory.isDirectory) return
        
        // Inicializar estatísticas
        val statistics = BarrelStatistics()
        
        // Generate barrel files recursively
        generateBarrelFilesRecursively(directory, statistics, project)
        
        // Mostrar estatísticas ao usuário
        NotificationUtil.showInfo(
            project,
            "Dart Barrel File Generator",
            statistics.getSummary()
        )
    }
    
    /**
     * Gera arquivos barrel recursivamente para o diretório e seus subdiretórios.
     */
    private fun generateBarrelFilesRecursively(directory: VirtualFile, statistics: BarrelStatistics, project: com.intellij.openapi.project.Project) {
        statistics.incrementDirectoriesProcessed()
        
        // Verificar se o diretório deve ser excluído
        val dirPath = toSystemPath(directory).toString()
        if (shouldExcludeDirectory(dirPath)) {
            statistics.incrementDirectoriesExcluded()
            return
        }
        
        // Coletar arquivos Dart válidos no diretório atual
        val dartFiles = mutableListOf<String>()
        // Coletar arquivos barrel das subpastas
        val subdirectoryBarrels = mutableListOf<String>()
        
        // Processar subdiretórios
        val children = directory.children
        for (child in children) {
            if (child.isDirectory) {
                val childDirPath = toSystemPath(child).toString()
                if (!shouldExcludeDirectory(childDirPath)) {
                    // Generate barrel for the subdirectory recursively
                    generateBarrelFilesRecursively(child, statistics, project)
                    
                    // Verificar se o arquivo barrel realmente existe e é um barrel válido na subpasta
                    val barrelFileName = settings.getBarrelFileName(child)
                    val barrelFile = child.findChild(barrelFileName)
                    
                    if (barrelFile != null && barrelFile.exists() && isExistingFileABarrel(barrelFile)) {
                        val barrelPath = "${child.name}/$barrelFileName"
                        subdirectoryBarrels.add(barrelPath)
                    }
                }
            } else {
                val fileName = child.name
                val filePath = toSystemPath(child).toString()
                
                statistics.incrementFilesProcessed()
                
                // Check if it's a valid Dart file and not the barrel file itself
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
        
        // Generate barrel file for the current directory if there are valid Dart files or subdirectory barrels
        if (dartFiles.isNotEmpty() || subdirectoryBarrels.isNotEmpty() || !settings.skipEmpty) {
            val content = generateBarrelContentWithSubdirectories(directory, dartFiles, subdirectoryBarrels)
            createBarrelFile(directory, content, project)
            statistics.incrementBarrelFilesGenerated()
        }
    }
    
    /**
     * Gera o conteúdo do arquivo barrel incluindo arquivos das subpastas.
     */
    private fun generateBarrelContentWithSubdirectories(
        directory: VirtualFile,
        dartFiles: List<String>,
        subdirectoryBarrels: List<String>
    ): String {
        val sb = StringBuilder()
        val allExports = mutableListOf<String>()
        
        // Processar arquivos Dart do diretório atual
        dartFiles.forEach { fileName ->
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
            
            allExports.add(finalPath)
        }
        
        // Processar arquivos barrel das subpastas
        subdirectoryBarrels.forEach { barrelPath ->
            val exportPath = if (settings.prependPackageToLibExport && DartFileUtils.isLibDirectory(directory)) {
                DartFileUtils.formatExportPath(directory, barrelPath)
            } else {
                barrelPath
            }
            
            allExports.add(exportPath)
        }
        
        // Ordenar todas as exportações alfabeticamente e gerar o conteúdo
        allExports.sorted().forEach { exportPath ->
            sb.appendLine("export '$exportPath';")
        }
        
        return sb.toString()
    }
    
    /**
     * Verifica se um arquivo existente é um barrel.
     * Um arquivo é considerado barrel se:
     * 1. Contém apenas declarações export
     * 2. Não contém classes, funções, variáveis ou outros códigos Dart
     */
    private fun isExistingFileABarrel(file: VirtualFile): Boolean {
        try {
            val content = String(file.contentsToByteArray())
            val lines = content.lines()
                .map { it.trim() }
                .filter { it.isNotEmpty() && !it.startsWith("//") && !it.startsWith("/*") && !it.startsWith("*") }
            
            // Se o arquivo está vazio ou só tem comentários, pode ser considerado barrel
            if (lines.isEmpty()) {
                return true
            }
            
            // Verificar se todas as linhas não-vazias são exports
            return lines.all { line ->
                line.startsWith("export ") && line.endsWith(";")
            }
        } catch (e: Exception) {
            // Em caso de erro ao ler o arquivo, assumir que não é um barrel para segurança
            return false
        }
    }
}
