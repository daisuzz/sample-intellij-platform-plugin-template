package com.github.daisuzz.sampleintellijplatformplugintemplate

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryManager

class UrlGenerator(
    private val project: Project,
    private val editor: Editor,
    private val virtualFile: VirtualFile,
) {

    private val repository = GitRepositoryManager.getInstance(project).getRepositoryForFileQuick(virtualFile)

    fun hasRepository() = repository != null

    fun generate(): String {

        requireNotNull(repository)

        val repositoryPath = getGitRepositoryPath(repository)

        val relativeFilePath = getRelativeFilePath(project, virtualFile)

        val lineFragment = getGitLineFragment(editor)

        return "https://github.dev/$repositoryPath/blob/${repository.currentRevision}$relativeFilePath$lineFragment"
    }

    private fun getGitRepositoryPath(gitRepository: GitRepository): String {
        val result = Regex(".*(github|gitlab|bitbucket)\\.(com|org).([^/]+/[^/]+).*\\.git")
            .matchEntire(gitRepository.remotes.first()?.firstUrl ?: "")
        return result?.groupValues?.get(3) ?: ""
    }

    private fun getRelativeFilePath(project: Project, virtualFile: VirtualFile): String {
        val basePath = project.basePath ?: ""
        return virtualFile.path.replace(basePath, "")
    }

    private fun getGitLineFragment(editor: Editor): String {
        val caret = editor.caretModel.primaryCaret
        val logicalStartPosition = editor.visualToLogicalPosition(caret.selectionStartPosition)
        val logicalEndPosition = editor.visualToLogicalPosition(caret.selectionEndPosition)
        val startLine = logicalStartPosition.line + 1
        val endLine = if (logicalEndPosition.column == 0 && logicalStartPosition.line != logicalEndPosition.line)
            logicalEndPosition.line
        else
            logicalEndPosition.line + 1
        return if (startLine == endLine) "#L$startLine" else "#L$startLine-L$endLine"
    }
}
