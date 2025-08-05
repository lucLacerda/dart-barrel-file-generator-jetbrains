package com.dbfg.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.vfs.VirtualFile
import com.dbfg.utils.BarrelStatistics
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
        
        // Processar subdiretórios
        val children = directory.children
        for (child in children) {
            if (child.isDirectory) {
                // Generate barrel for the subdirectory recursively
                generateBarrelFilesRecursively(child, statistics, project)
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
        
        // Generate barrel file for the current directory if there are valid Dart files
        if (dartFiles.isNotEmpty() || !settings.skipEmpty) {
            val content = generateBarrelContent(directory, dartFiles)
            createBarrelFile(directory, content, project)
            statistics.incrementBarrelFilesGenerated()
        }
    }
}
