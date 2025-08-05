package com.dbfg.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.xmlb.XmlSerializerUtil
import java.util.ArrayList

/**
 * Persistent settings class for the Dart Barrel File Generator plugin.
 */
@State(
    name = "com.dbfg.settings.DartBarrelSettings",
    storages = [Storage("DartBarrelFileGeneratorSettings.xml")]
)
class DartBarrelSettings : PersistentStateComponent<DartBarrelSettings> {
    // Prompt for barrel file name to the user
    var promptName: Boolean = false
    
    // Default name for barrel file (if not specified, uses the folder name)
    var defaultBarrelName: String? = null
    
    // Skip empty folders when generating barrel files recursively
    var skipEmpty: Boolean = false

    // Exclude .freezed.dart files
    var excludeFreezed: Boolean = true

    // Exclude .g.dart files
    var excludeGenerated: Boolean = true
    
    /**
     * Generates the barrel file name based on the directory
     */
    fun getBarrelFileName(directory: VirtualFile): String {
        return "${directory.name}.dart"
    }
    
    // Add prefix of the folder name to export paths
    var prependFolderName: Boolean = false
    
    // Add suffix of the folder name to export paths
    var appendFolderName: Boolean = false
    
    // Use package: format for exports in the lib folder
    var prependPackageToLibExport: Boolean = false

    // Glob patterns to exclude directories
    var excludeDirList: MutableList<String> = ArrayList(listOf(
        "**/build/**",
        "**/test/**",
        "**/.dart_tool/**"
    ))

    // Glob patterns to exclude files
    var excludeFileList: MutableList<String> = ArrayList(listOf(
        "**/*.freezed.dart",
        "**/*.g.dart",
        "**/*.mocks.dart"
    ))

    override fun getState(): DartBarrelSettings = this

    override fun loadState(state: DartBarrelSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
