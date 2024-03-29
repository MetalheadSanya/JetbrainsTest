package ru.zalutskii.google.test.ui.main

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.zalutskii.google.test.parser.list.TestTree
import ru.zalutskii.google.test.service.TestServiceInput
import ru.zalutskii.google.test.service.TestServiceOutput
import ru.zalutskii.google.test.ui.openFile.OpenFileModuleOutput
import java.io.File
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreePath

class MainPresenter : MainViewOutput, OpenFileModuleOutput, TestServiceOutput,
    CoroutineScope by CoroutineScope(Dispatchers.IO) {

    var view: MainViewInput? = null
    var router: MainRouterInput? = null
    var service: TestServiceInput? = null

    override fun didPressOpenFile() {
        router?.showOpenFile(this)
    }

    override fun didPressRun() {
        view?.setOpenActionEnabled(false)
        view?.setRunActionEnabled(false)
        view?.setRunFailedActionEnabled(false)
        view?.setStopActionEnabled(true)

        launch {
            service?.run()
        }
    }

    override fun didPressRunFailed() {
        view?.setOpenActionEnabled(false)
        view?.setRunActionEnabled(false)
        view?.setRunFailedActionEnabled(false)
        view?.setStopActionEnabled(true)

        launch {
            service?.rerunFailedTests()
        }
    }

    override fun didPressStop() {
        launch {
            service?.stop()
        }
    }

    override fun didSelectTest(path: TreePath) {
        val viewModelPath = extractViewModels(path)
        when (viewModelPath.size) {
            3 -> launch {
                service?.changeLogTo(viewModelPath[1].name, viewModelPath[2].name)
            }
            2 -> launch {
                service?.changeLogTo(viewModelPath[1].name)
            }
            else -> launch {
                service?.changeLogTo()
            }
        }
    }

    private fun extractViewModels(path: TreePath): List<TestViewModel> {
        if (path.pathCount < 1) return listOf()
        val root = extractViewModel(path.path[0])
        if (path.pathCount < 2) return listOf(root)
        val suite = extractViewModel(path.path[1])
        if (path.pathCount < 3) return listOf(root, suite)
        val function = extractViewModel(path.path[2])
        return listOf(root, suite, function)
    }

    private fun extractViewModel(data: Any): TestViewModel =
        (data as DefaultMutableTreeNode).userObject as TestViewModel

    override fun didOpenFile(file: File) {
        view?.setOpenActionEnabled(false)
        view?.setRunActionEnabled(false)
        view?.setRunFailedActionEnabled(false)
        view?.setStopActionEnabled(false)

        view?.setLog("")

        launch {
            service?.open(file)
        }
    }

    override fun didLoadTestTree(tree: TestTree) {
        val rootNode = defaultMutableTreeNode(tree)

        view?.setTreeNode(rootNode)
        view?.setOpenActionEnabled(true)
        view?.setRunActionEnabled(true)
        view?.setRunFailedActionEnabled(false)
        view?.setStopActionEnabled(false)
    }

    override fun didUpdateTestTree(tree: TestTree) {
        val rootNode = defaultMutableTreeNode(tree)
        view?.updateTreeNode(rootNode)
    }

    private fun defaultMutableTreeNode(tree: TestTree): DefaultMutableTreeNode {
        val rootViewModel = TestViewModel(
            "All suites",
            makeViewModelStatus(tree.status),
            null
        )
        val rootNode = DefaultMutableTreeNode(rootViewModel)

        for (case in tree.suites) {

            val caseViewModel = TestViewModel(
                case.name,
                makeViewModelStatus(case.status),
                case.time?.let(::makeTimeText)
            )
            val caseNode = DefaultMutableTreeNode(caseViewModel)

            for (function in case.functions) {
                val functionViewModel = TestViewModel(
                    function.name,
                    makeViewModelStatus(function.status),
                    function.time?.let(::makeTimeText)
                )
                val functionNode = DefaultMutableTreeNode(functionViewModel)
                caseNode.add(functionNode)
            }

            rootNode.add(caseNode)
        }
        return rootNode
    }

    private fun makeViewModelStatus(status: TestTree.Status) = when (status) {
        TestTree.Status.READY -> null
        TestTree.Status.QUEUE -> TestViewModel.Status.QUEUED
        TestTree.Status.RUN -> TestViewModel.Status.RUN
        TestTree.Status.SUCCESS -> TestViewModel.Status.SUCCESS
        TestTree.Status.FAIL -> TestViewModel.Status.FAIL
    }

    private fun makeTimeText(time: String) = "($time ms)"

    override fun didProcessOutput(log: String) {
        view?.setLog(log)
    }

    override fun didFinishRun(status: TestTree.Status) {
        val toastText = "Test run finished"

        view?.setOpenActionEnabled(true)
        view?.setRunActionEnabled(true)
        view?.setRunFailedActionEnabled(status == TestTree.Status.FAIL)
        view?.setStopActionEnabled(false)
        view?.showToast(toastText)
    }
}
