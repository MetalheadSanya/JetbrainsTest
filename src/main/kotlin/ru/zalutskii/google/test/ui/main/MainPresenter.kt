package ru.zalutskii.google.test.ui.main

import kotlinx.coroutines.*
import ru.zalutskii.google.test.service.TestServiceInput
import ru.zalutskii.google.test.ui.openFile.OpenFileModuleOutput
import java.io.File
import kotlinx.coroutines.swing.Swing
import ru.zalutskii.google.test.parser.testList.TestTree
import ru.zalutskii.google.test.service.TestServiceOutput
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreePath

class MainPresenter: MainViewOutput, OpenFileModuleOutput, TestServiceOutput, CoroutineScope by CoroutineScope(Dispatchers.IO) {

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
        view?.setTestSelectionEnabled(false)

        launch {
            service?.run()
        }
    }

    override fun didPressRunFailed() {
        println("Run > Run Failed")
    }

    override fun didPressStop() {
        launch {
            service?.stop()
        }
    }

    override fun didSelectTest(path: TreePath) {
        if (path.pathCount == 3) {
            launch {
                service?.showLog(path.path[1].toString(), path.path[2].toString())
            }
        } else if (path.pathCount == 2) {
            launch {
                service?.showLog(path.path[1].toString())
            }
        } else {
            launch {
                service?.showLog()
            }
        }
    }

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

    override suspend fun didLoadTestTree(tree: TestTree) {
        val rootNode = DefaultMutableTreeNode("All suites")

        for (case in tree.cases) {
            val caseNode = DefaultMutableTreeNode(case.name)

            for (function in case.functions) {
                val functionNode = DefaultMutableTreeNode(function.name)
                caseNode.add(functionNode)
            }

            rootNode.add(caseNode)
        }

        coroutineScope {
            launch(Dispatchers.Swing) {
                view?.setTreeNode(rootNode)
                view?.setOpenActionEnabled(true)
                view?.setRunActionEnabled(true)
                view?.setRunFailedActionEnabled(false)
                view?.setStopActionEnabled(false)
            }
        }
    }

    override suspend fun didProcessOutput(log: String) {
        coroutineScope {
            launch(Dispatchers.Swing) {
                view?.setLog(log)
            }
        }
    }

    override suspend fun didFinishRun() {
        coroutineScope {
            launch(Dispatchers.Swing) {
                view?.setOpenActionEnabled(true)
                view?.setRunActionEnabled(true)
                view?.setRunFailedActionEnabled(false)
                view?.setStopActionEnabled(false)
                view?.setTestSelectionEnabled(true)
            }
        }
    }
}