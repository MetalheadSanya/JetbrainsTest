package ru.zalutskii.google.test.ui.main

import ru.zalutskii.google.test.ui.openFile.OpenFileModuleOutput

interface MainRouterInput {
    fun showOpenFile(moduleOutput: OpenFileModuleOutput)
}