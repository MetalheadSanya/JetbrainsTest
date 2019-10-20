package ru.zalutskii.google.test.ui.main

import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreePath

interface MainViewInput {
    fun setTreeNode(treeNode: DefaultMutableTreeNode)
    fun updateTreeNode(treeNode: DefaultMutableTreeNode)

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