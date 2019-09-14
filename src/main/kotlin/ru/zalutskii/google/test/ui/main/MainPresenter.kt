package ru.zalutskii.google.test.ui.main

import kotlinx.coroutines.*
import ru.zalutskii.google.test.service.TestServiceInput
import ru.zalutskii.google.test.ui.openFile.OpenFileModuleOutput
import java.io.File
import kotlinx.coroutines.swing.Swing
import ru.zalutskii.google.test.parser.testList.TestTree
import ru.zalutskii.google.test.service.TestServiceOutput
import javax.swing.tree.DefaultMutableTreeNode

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

    override fun didOpenFile(file: File) {
        view?.setOpenActionEnabled(false)
        view?.setRunActionEnabled(false)
        view?.setRunFailedActionEnabled(false)
        view?.setStopActionEnabled(false)

        launch {
            service?.open(file)
        }
    }

    override suspend fun didLoadTestTree(tree: TestTree) {
        val rootNode = DefaultMutableTreeNode("Root")

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
                view?.setOpenActionEnabled(false)
                view?.setRunActionEnabled(false)
                view?.setRunFailedActionEnabled(false)
                view?.setStopActionEnabled(true)
                view?.setLog(log)
            }
        }
    }

    override suspend fun didFinishRun() {
        launch(Dispatchers.Swing) {
            view?.setOpenActionEnabled(true)
            view?.setRunActionEnabled(true)
            view?.setRunFailedActionEnabled(false)
            view?.setStopActionEnabled(false)
        }
    }
}