package ru.zalutskii.google.test

import ru.zalutskii.google.test.parser.list.TestListLexerImpl
import ru.zalutskii.google.test.parser.list.TestListParserImpl
import ru.zalutskii.google.test.parser.log.TestLogParserImpl
import ru.zalutskii.google.test.service.TestService
import ru.zalutskii.google.test.service.log.performer.LogPerformer
import ru.zalutskii.google.test.service.process.TestProcessFactoryImpl
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

    val processFactory = TestProcessFactoryImpl

    val listLexer = TestListLexerImpl()
    val parser = TestListParserImpl(listLexer)

    val service = TestService(parser, processFactory)

    val logLexer = TestLogParserImpl()
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
