package com.dbfg.utils

/**
 * Class for tracking barrel file generation statistics.
 */
data class BarrelStatistics(
    var filesProcessed: Int = 0,
    var filesExported: Int = 0,
    var filesExcluded: Int = 0,
    var directoriesProcessed: Int = 0,
    var directoriesExcluded: Int = 0,
    var barrelFilesGenerated: Int = 0
) {
    /**
     * Increment the number of files processed.
     */
    fun incrementFilesProcessed() {
        filesProcessed++
    }

    /**
     * Increment the number of files exported.
     */
    fun incrementFilesExported() {
        filesExported++
    }

    /**
     * Increment the number of files excluded.
     */
    fun incrementFilesExcluded() {
        filesExcluded++
    }

    /**
     * Increment the number of directories processed.
     */
    fun incrementDirectoriesProcessed() {
        directoriesProcessed++
    }

    /**
     * Increment the number of directories excluded.
     */
    fun incrementDirectoriesExcluded() {
        directoriesExcluded++
    }

    /**
     * Increment the number of barrel files generated.
     */
    fun incrementBarrelFilesGenerated() {
        barrelFilesGenerated++
    }

    /**
     * Returns a summary of the statistics.
     */
    fun getSummary(): String {
        return """
            Barrel file generation statistics:
            - Directories processed: $directoriesProcessed
            - Directories excluded: $directoriesExcluded
            - Files processed: $filesProcessed
            - Files exported: $filesExported
            - Files excluded: $filesExcluded
            - Barrel files generated: $barrelFilesGenerated
        """.trimIndent()
    }
}
