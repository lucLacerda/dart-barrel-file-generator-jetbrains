package com.dbfg.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.vfs.VirtualFile
import com.dbfg.utils.BarrelStatistics
import com.dbfg.utils.NotificationUtil

/**
 * Action to generate a barrel file including files from subfolders.
 */
class GenerateBarrelWithSubfoldersAction : BaseBarrelAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val directory = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        
        if (!directory.isDirectory) return
        
        // Initialize statistics
        val statistics = BarrelStatistics()
        
        // Collect valid Dart files in the current directory and subdirectories
        val dartFiles = mutableListOf<String>()
        val subdirectories = mutableMapOf<String, List<String>>()
        
        // Process the current directory
        processDirectory(directory, dartFiles, subdirectories, statistics)
        
        // Generate barrel file content
        val content = generateBarrelContent(directory, dartFiles, subdirectories)
        
        // Create barrel file
        createBarrelFile(directory, content, project)
        statistics.incrementBarrelFilesGenerated()
        
        // Show statistics to the user
        NotificationUtil.showInfo(
            project,
            "Dart Barrel File Generator",
            statistics.getSummary()
        )
    }
    
    /**
     * Process a directory and collect valid Dart files.
     */
    private fun processDirectory(
        directory: VirtualFile,
        dartFiles: MutableList<String>,
        subdirectories: MutableMap<String, List<String>>,
        statistics: BarrelStatistics
    ) {
        statistics.incrementDirectoriesProcessed()
        
        // Check if the directory should be excluded
        val dirPath = toSystemPath(directory).toString()
        if (shouldExcludeDirectory(dirPath)) {
            statistics.incrementDirectoriesExcluded()
            return
        }
        
        // Process files in the current directory
        for (file in directory.children) {
            if (file.isDirectory) {
                // Process subdirectory recursively
                val subdirName = file.name
                val subdirFiles = mutableListOf<String>()
                
                // Collect valid Dart files in the subdirectory
                for (subFile in file.children) {
                    if (!subFile.isDirectory) {
                        val fileName = subFile.name
                        val filePath = toSystemPath(subFile).toString()
                        
                        statistics.incrementFilesProcessed()
                        
                        // Verificar se é um arquivo Dart válido e não é o próprio arquivo barrel
                        if (isDartFile(fileName) && 
                            !isBarrelFile(file.name, fileName) && 
                            !shouldExcludeFile(fileName, filePath)) {
                            subdirFiles.add(fileName)
                            statistics.incrementFilesExported()
                        } else if (isDartFile(fileName)) {
                            statistics.incrementFilesExcluded()
                        }
                    }
                }
                
                // Adicionar arquivos do subdiretório se houver algum
                if (subdirFiles.isNotEmpty()) {
                    subdirectories[subdirName] = subdirFiles
                }
            } else {
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
    }
}
