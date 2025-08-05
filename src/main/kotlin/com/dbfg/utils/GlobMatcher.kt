package com.dbfg.utils

import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.PathMatcher

/**
 * Utilitário para correspondência de padrões glob.
 */
object GlobMatcher {
    /**
     * Verifica se um caminho corresponde a um padrão glob.
     *
     * @param path O caminho a ser verificado
     * @param globPattern O padrão glob
     * @return true se o caminho corresponder ao padrão
     */
    fun matches(path: String, globPattern: String): Boolean {
        val pathMatcher = createPathMatcher(globPattern)
        val pathObj = Path.of(path)
        return pathMatcher.matches(pathObj)
    }

    /**
     * Verifica se um caminho corresponde a qualquer um dos padrões glob fornecidos.
     *
     * @param path O caminho a ser verificado
     * @param globPatterns Lista de padrões glob
     * @return true se o caminho corresponder a qualquer um dos padrões
     */
    fun matchesAny(path: String, globPatterns: List<String>): Boolean {
        return globPatterns.any { matches(path, it) }
    }

    /**
     * Cria um PathMatcher para o padrão glob fornecido.
     *
     * @param globPattern O padrão glob
     * @return Um PathMatcher para o padrão
     */
    private fun createPathMatcher(globPattern: String): PathMatcher {
        val syntaxAndPattern = "glob:$globPattern"
        return FileSystems.getDefault().getPathMatcher(syntaxAndPattern)
    }
}
