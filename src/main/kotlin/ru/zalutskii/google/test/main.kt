package ru.zalutskii.google.test

import ru.zalutskii.google.test.parser.testList.TestListLexer
import ru.zalutskii.google.test.parser.testList.TestListParser
import ru.zalutskii.google.test.parser.testLog.TestLogLexer
import ru.zalutskii.google.test.service.TestService
import ru.zalutskii.google.test.service.logPerformer.LogPerformer
import ru.zalutskii.google.test.service.process.TestProcessImpl
import ru.zalutskii.google.test.ui.main.MainPresenter
import ru.zalutskii.google.test.ui.main.MainRouter
import ru.zalutskii.google.test.ui.main.MainView
import java.awt.EventQueue
import javax.swing.JDialog
import javax.swing.JFrame

fun createAndShowGui() {
    val view = MainView()
    val presenter = MainPresenter()
    val router = MainRouter()

    val process = TestProcessImpl()

    val listLexer = TestListLexer()
    val parser = TestListParser(listLexer)

    val service = TestService(parser, process)

    val logLexer = TestLogLexer()
    val logPerformer = LogPerformer(logLexer)

    logPerformer.output = service

    router.parentFrame = view.frame

    view.output = presenter

    presenter.router = router
    presenter.service = service
    presenter.view = view

    service.logPerformer = logPerformer
    service.output = presenter

    view.show()
}

fun main(args: Array<String>) {
    System.setProperty("apple.laf.useScreenMenuBar", "true")
    System.setProperty("apple.awt.textantialiasing", "true")

    JFrame.setDefaultLookAndFeelDecorated(true)
    JDialog.setDefaultLookAndFeelDecorated(true)

    EventQueue.invokeLater(::createAndShowGui)
}
