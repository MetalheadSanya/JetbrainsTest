package ru.zalutskii.google.test.ui.main

import javax.swing.tree.TreeNode
import javax.swing.tree.TreePath

interface MainViewInput {
    fun setTreeNode(treeNode: TreeNode)
    fun setOpenActionEnabled(enabled: Boolean)
    fun setRunActionEnabled(enabled: Boolean)
    fun setStopActionEnabled(enabled: Boolean)
    fun setRunFailedActionEnabled(enabled: Boolean)

    fun setTestSelectionEnabled(enabled: Boolean)

    fun setLog(log: String)
}

interface MainViewOutput {
    fun didPressOpenFile()
    fun didPressRun()
    fun didPressRunFailed()
    fun didPressStop()

    fun didSelectTest(path: TreePath)
}