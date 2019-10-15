package ru.zalutskii.google.test.ui.main

import javax.swing.tree.TreeModel
import javax.swing.tree.TreePath

interface MainViewInput {
    fun setTreeModel(treeModel: TreeModel)
    fun updateTreeNode()

    fun setOpenActionEnabled(enabled: Boolean)
    fun setRunActionEnabled(enabled: Boolean)
    fun setStopActionEnabled(enabled: Boolean)
    fun setRunFailedActionEnabled(enabled: Boolean)

    fun setLog(log: String)
    fun showToast(text: String)
}

interface MainViewOutput {
    fun didPressOpenFile()
    fun didPressRun()
    fun didPressRunFailed()
    fun didPressStop()

    fun didSelectTest(path: TreePath)
}