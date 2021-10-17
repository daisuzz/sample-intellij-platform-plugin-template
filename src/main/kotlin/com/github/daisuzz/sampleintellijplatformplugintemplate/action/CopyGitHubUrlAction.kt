package com.github.daisuzz.sampleintellijplatformplugintemplate.action

import com.github.daisuzz.sampleintellijplatformplugintemplate.UrlGenerator
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.Project
import java.awt.datatransfer.StringSelection

class CopyGitHubUrlAction : AnAction() {

    // actionPerformedメソッドはアクションを実行したときの処理を制御するメソッド
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getRequiredData(CommonDataKeys.PROJECT)
        val editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val virtualFile = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE)

        val urlGenerator = UrlGenerator(project, editor, virtualFile)

        if (urlGenerator.hasRepository()) {
            val url = urlGenerator.generate()
            CopyPasteManager.getInstance().setContents(StringSelection(url))
            showNotification(project, NotificationType.INFORMATION, "Copied permalink.", "<a href='$url'>$url</a>.")
            return
        }
        showNotification(
            project,
            NotificationType.ERROR,
            "Error",
            "Copy failed.\n You will need to set up a GitHub remote repository to run it."
        )
    }

    // updateメソッドはactionの有効/無効と可視/不可視を制御するメソッド
    override fun update(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)
        val editor = e.getData(CommonDataKeys.EDITOR)

        e.presentation.isEnabled = project != null && editor != null
    }

    private fun showNotification(project: Project, type: NotificationType, title: String, body: String) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Sample intellij platform plugin template")
            .createNotification(title, body, type)
            .setListener(NotificationListener.URL_OPENING_LISTENER)
            .notify(project)
    }
}
