package ru.zalutskii.google.test.ui.openFile

import java.awt.FileDialog
import java.io.File
import javax.swing.JFrame

class OpenFileView(parent: JFrame) {

    var output: OpenFileModuleOutput? = null

    private val fileDialog = FileDialog(parent)

    init {
        setupUi()
    }

    private fun setupUi() {
        fileDialog.mode = FileDialog.LOAD
        fileDialog.setFilenameFilter { dir, name ->
            val file = File(dir, name)
            file.canExecute()
        }
        fileDialog.isMultipleMode = false
    }

    fun show() {
        fileDialog.isVisible = true
        if (fileDialog.file != null && fileDialog.directory != null) {
            output?.didOpenFile(File(fileDialog.directory, fileDialog.file))
        }
    }
}
