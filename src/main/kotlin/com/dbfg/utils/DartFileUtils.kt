package com.dbfg.utils

import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Utility for operations with Dart files.
 */
object DartFileUtils {
    private val DART_FILE_REGEX = Regex(".+(\\.dart)$")
    private val FREEZED_FILE_REGEX = Regex(".+(\\.freezed\\.dart)$")
    private val GENERATED_FILE_REGEX = Regex(".+(\\.g\\.dart)$")

    /**
     * Checks if a file is a valid Dart file.
     *
     * @param fileName File name
     * @return true if the file is a Dart file
     */
    fun isDartFile(fileName: String): Boolean {
        return DART_FILE_REGEX.matches(fileName)
    }

    /**
     * Verifica se um arquivo é um arquivo barrel.
     *
     * @param dirName Nome do diretório
     * @param fileName Nome do arquivo
     * @return true se o arquivo for um arquivo barrel
     */
    fun isBarrelFile(dirName: String, fileName: String): Boolean {
        return fileName == "$dirName.dart"
    }

    /**
     * Verifica se um arquivo é um arquivo .freezed.dart.
     *
     * @param fileName Nome do arquivo
     * @return true se o arquivo for um arquivo .freezed.dart
     */
    fun isFreezedFile(fileName: String): Boolean {
        return FREEZED_FILE_REGEX.matches(fileName)
    }

    /**
     * Verifica se um arquivo é um arquivo .g.dart.
     *
     * @param fileName Nome do arquivo
     * @return true se o arquivo for um arquivo .g.dart
     */
    fun isGeneratedFile(fileName: String): Boolean {
        return GENERATED_FILE_REGEX.matches(fileName)
    }

    /**
     * Verifica se um diretório é a pasta lib de um projeto Dart.
     *
     * @param directory Diretório a ser verificado
     * @return true se o diretório for a pasta lib
     */
    fun isLibDirectory(directory: VirtualFile): Boolean {
        return directory.name == "lib"
    }

    /**
     * Converts a virtual path to a file system path.
     *
     * @param file Arquivo virtual
     * @return Caminho do sistema de arquivos
     */
    fun toSystemPath(file: VirtualFile): Path {
        return Paths.get(file.path)
    }

    /**
     * Obtém o nome do pacote a partir do arquivo pubspec.yaml.
     *
     * @param directory Diretório do projeto
     * @return Nome do pacote ou null se não encontrado
     */
    fun getPackageName(directory: VirtualFile): String? {
        // Navegar para o diretório raiz do projeto (onde está o pubspec.yaml)
        var currentDir = directory
        while (currentDir.parent != null) {
            val pubspecFile = currentDir.findChild("pubspec.yaml")
            if (pubspecFile != null && !pubspecFile.isDirectory) {
                // Encontrou o pubspec.yaml, extrair o nome do pacote
                val content = String(pubspecFile.contentsToByteArray())
                val nameMatch = Regex("name:\\s*([^\\s]+)").find(content)
                return nameMatch?.groupValues?.get(1)
            }
            currentDir = currentDir.parent
        }
        return null
    }

    /**
     * Formata um caminho de exportação para usar o formato package: se estiver na pasta lib.
     *
     * @param directory Diretório atual
     * @param exportPath Caminho de exportação
     * @return Caminho formatado
     */
    fun formatExportPath(directory: VirtualFile, exportPath: String): String {
        if (!directory.path.contains("/lib/")) {
            return exportPath
        }

        val packageName = getPackageName(directory)
        if (packageName != null) {
            // Encontrar o caminho relativo à pasta lib
            val libPath = directory.path.substringAfter("/lib/")
            return if (libPath.isEmpty()) {
                "package:$packageName/$exportPath"
            } else {
                "package:$packageName/$libPath/$exportPath"
            }
        }

        return exportPath
    }
}
