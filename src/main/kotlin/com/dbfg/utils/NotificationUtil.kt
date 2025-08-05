package com.dbfg.utils

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

/**
 * Utility for displaying notifications to the user.
 */
object NotificationUtil {
    private const val GROUP_ID = "Dart Barrel File Generator"

    /**
     * Displays an information notification.
     *
     * @param project Current project
     * @param title Title of the notification
     * @param content Content of the notification
     */
    fun showInfo(project: Project, title: String, content: String) {
        notify(project, title, content, NotificationType.INFORMATION)
    }

    /**
     * Displays a success notification.
     *
     * @param project Projeto atual
     * @param title Título da notificação
     * @param content Conteúdo da notificação
     */
    fun showSuccess(project: Project, title: String, content: String) {
        notify(project, title, content, NotificationType.INFORMATION)
    }

    /**
     * Displays an error notification.
     *
     * @param project Projeto atual
     * @param title Título da notificação
     * @param content Conteúdo da notificação
     */
    fun showError(project: Project, title: String, content: String) {
        notify(project, title, content, NotificationType.ERROR)
    }

    /**
     * Displays a warning notification.
     *
     * @param project Projeto atual
     * @param title Título da notificação
     * @param content Conteúdo da notificação
     */
    fun showWarning(project: Project, title: String, content: String) {
        notify(project, title, content, NotificationType.WARNING)
    }

    /**
     * Exibe uma notificação.
     *
     * @param project Projeto atual
     * @param title Título da notificação
     * @param content Conteúdo da notificação
     * @param type Tipo da notificação
     */
    private fun notify(project: Project, title: String, content: String, type: NotificationType) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(GROUP_ID)
            .createNotification(title, content, type)
            .notify(project)
    }
}
