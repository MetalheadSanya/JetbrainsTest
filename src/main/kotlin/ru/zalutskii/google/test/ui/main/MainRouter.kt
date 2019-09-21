package ru.zalutskii.google.test.ui.main

import ru.zalutskii.google.test.ui.openFile.OpenFileModuleOutput
import ru.zalutskii.google.test.ui.openFile.OpenFileView
import javax.swing.JFrame

class MainRouter : MainRouterInput {

    var parentFrame: JFrame? = null

    override fun showOpenFile(moduleOutput: OpenFileModuleOutput) {
        parentFrame?.also { frame ->
            val view = OpenFileView(frame)
            view.output = moduleOutput
            view.show()
        }
    }
}
