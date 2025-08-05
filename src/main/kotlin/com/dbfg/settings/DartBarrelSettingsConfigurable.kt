package com.dbfg.settings

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.Messages
import com.intellij.ui.CollectionListModel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

/**
 * Configurable for the Dart Barrel File Generator plugin settings.
 */
class DartBarrelSettingsConfigurable : Configurable {
    private val settings = service<DartBarrelSettings>()
    private var panel: DialogPanel? = null

    override fun getDisplayName(): String = "Dart Barrel File Generator"

    override fun createComponent(): JComponent {
        panel = panel {
            group("General Settings") {
                row {
                    text("The barrel file name will be the same as the selected directory (e.g., 'home' folder generates 'home.dart')")
                }
            }

            group("Export Options") {
                row {
                    checkBox("Add folder name at the beginning")
                        .bindSelected(settings::prependFolderName)
                        .comment("Adds the folder name as a prefix to exports")
                }
                row {
                    checkBox("Add folder name at the end")
                        .bindSelected(settings::appendFolderName)
                        .comment("Adds the folder name as a suffix to exports")
                }
                row {
                    checkBox("Use package: format for exports in the lib folder")
                        .bindSelected(settings::prependPackageToLibExport)
                        .comment("Uses the package: format for exports within the lib folder")
                }
            }
            
            group("Exclusions") {
                row {
                    checkBox("Exclude .freezed.dart files")
                        .bindSelected(settings::excludeFreezed)
                }
                row {
                    checkBox("Exclude .g.dart files (generated)")
                        .bindSelected(settings::excludeGenerated)
                }
                
                row("Glob patterns to exclude directories:") {}
                row {
                    val excludeDirModel = CollectionListModel(settings.excludeDirList)
                    val excludeDirList = JBList(excludeDirModel)
                    val excludeDirPanel = ToolbarDecorator.createDecorator(excludeDirList)
                        .setAddAction { _ ->
                            val pattern = Messages.showInputDialog(
                                "Enter a glob pattern to exclude directories:",
                                "Adicionar Padrão de Exclusão",
                                null
                            )
                            if (!pattern.isNullOrBlank()) {
                                excludeDirModel.add(pattern)
                            }
                        }
                        .setRemoveAction { _ ->
                            val selectedIndex = excludeDirList.selectedIndex
                            if (selectedIndex >= 0) {
                                excludeDirModel.remove(selectedIndex)
                            }
                        }
                        .createPanel()
                    cell(excludeDirPanel).resizableColumn()
                }
                
                row("Glob patterns to exclude files:") {}
                row {
                    val excludeFileModel = CollectionListModel(settings.excludeFileList)
                    val excludeFileList = JBList(excludeFileModel)
                    val excludeFilePanel = ToolbarDecorator.createDecorator(excludeFileList)
                        .setAddAction { _ ->
                            val pattern = Messages.showInputDialog(
                                "Enter a glob pattern to exclude files:",
                                "Adicionar Padrão de Exclusão",
                                null
                            )
                            if (!pattern.isNullOrBlank()) {
                                excludeFileModel.add(pattern)
                            }
                        }
                        .setRemoveAction { _ ->
                            val selectedIndex = excludeFileList.selectedIndex
                            if (selectedIndex >= 0) {
                                excludeFileModel.remove(selectedIndex)
                            }
                        }
                        .createPanel()
                    cell(excludeFilePanel).resizableColumn()
                }
            }
        }
        return panel!!
    }

    override fun isModified(): Boolean {
        return panel?.isModified() ?: false
    }

    override fun apply() {
        panel?.apply()
    }

    override fun reset() {
        panel?.reset()
    }
}
