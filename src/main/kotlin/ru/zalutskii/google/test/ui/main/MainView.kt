package ru.zalutskii.google.test.ui.main

import java.awt.*
import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeNode


class MainView : MainViewInput {

    val frame: JFrame = JFrame()

    // Items

    private val openItem = JMenuItem("Open")
    private val runItem = JMenuItem("Run")
    private val runFailedItem = JMenuItem("Run Failed")
    private val stopItem = JMenuItem("Stop")

    // Toolbar buttons

    private val stopButton = JButton("Stop")
    private val openButton = JButton("Open")
    private val runButton = JButton("Run")
    private val runFailedButton = JButton("Run Failed")

    // Main views

    private val testTree = JTree()
    private val logArea = JTextArea("Test")

    var output: MainViewOutput? = null

    init {
        createUi()
    }

    fun show() {
        frame.isVisible = true
    }

    override fun setTreeNode(treeNode: TreeNode) {
        val model = DefaultTreeModel(treeNode)
        testTree.model = model
        for (i in testTree.rowCount - 1 downTo 0) {
            testTree.expandRow(i)
        }
    }

    override fun setOpenActionEnabled(enabled: Boolean) {
        openItem.isEnabled = enabled
        openButton.isEnabled = enabled
    }

    override fun setRunActionEnabled(enabled: Boolean) {
        runItem.isEnabled = enabled
        runButton.isEnabled = enabled
    }

    override fun setStopActionEnabled(enabled: Boolean) {
        stopItem.isEnabled = enabled
        stopButton.isEnabled = enabled
    }

    override fun setRunFailedActionEnabled(enabled: Boolean) {
        runFailedItem.isEnabled = enabled
        runFailedButton.isEnabled = enabled
    }

    override fun setLog(log: String) {
        logArea.text = log
    }

    private fun createUi() {
        frame.title = "GoogleTestUI"
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.size = Dimension(600, 400)
        frame.setLocationRelativeTo(null)

        frame.jMenuBar = createMenuBar()

        val layout = SpringLayout()
        frame.contentPane.layout = layout

        val toolbar = createToolBar()
        frame.contentPane.add(toolbar)

        testTree.model = null
        testTree.isRootVisible = false
        testTree.border = BorderFactory.createEmptyBorder(4, 4, 4, 4)


        val treeScrollPane = JScrollPane(testTree)
        treeScrollPane.setViewportView(testTree)

        val logScrollPane = JScrollPane(logArea)
        logArea.font = Font("monospaced", Font.PLAIN, 12)

        val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, logScrollPane)
        splitPane.dividerLocation = 200
        frame.contentPane.add(splitPane)

        layout.putConstraint(SpringLayout.NORTH, toolbar, 0, SpringLayout.NORTH, frame.contentPane)
        layout.putConstraint(SpringLayout.WEST, toolbar, 0, SpringLayout.WEST, frame.contentPane)
        layout.putConstraint(SpringLayout.EAST, toolbar, 0, SpringLayout.EAST, frame.contentPane)

        layout.putConstraint(SpringLayout.NORTH, splitPane, 0, SpringLayout.SOUTH, toolbar)
        layout.putConstraint(SpringLayout.WEST, splitPane, 0, SpringLayout.WEST, frame.contentPane)
        layout.putConstraint(SpringLayout.EAST, splitPane, 0, SpringLayout.EAST, frame.contentPane)
        layout.putConstraint(SpringLayout.SOUTH, splitPane, 0, SpringLayout.SOUTH, frame.contentPane)
    }

    private fun createToolBar(): JToolBar {
        val toolbar = JToolBar()
        toolbar.isFloatable = false

        openButton.addActionListener {
            output?.didPressOpenFile()
        }

        runButton.addActionListener {
            output?.didPressRun()
        }
        runButton.isEnabled = false

        runFailedButton.addActionListener {
            output?.didPressRunFailed()
        }
        runFailedButton.isEnabled = false


        stopButton.addActionListener {
            output?.didPressStop()
        }
        stopButton.isEnabled = false

        toolbar.add(openButton)
        toolbar.addSeparator()
        toolbar.add(runButton)
        toolbar.add(runFailedButton)
        toolbar.add(stopButton)
        return toolbar
    }

    private fun createMenuBar(): JMenuBar {
        val menuBar = JMenuBar()

        val fileMenu = JMenu("File")

        openItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_O,
            Toolkit.getDefaultToolkit().menuShortcutKeyMaskEx
        )
        openItem.addActionListener {
            output?.didPressOpenFile()
        }
        fileMenu.add(openItem)

        val runMenu = JMenu("Run")

        runItem.isEnabled = false
        runItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_R,
            Toolkit.getDefaultToolkit().menuShortcutKeyMaskEx
        )
        runItem.addActionListener {
            output?.didPressRun()
        }
        runMenu.add(runItem)


        runFailedItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_D,
            Toolkit.getDefaultToolkit().menuShortcutKeyMaskEx
        )
        runFailedItem.addActionListener {
            output?.didPressRunFailed()
        }
        runFailedItem.isEnabled = false
        runMenu.add(runFailedItem)


        stopItem.accelerator = KeyStroke.getKeyStroke(
            KeyEvent.VK_F2,
            Toolkit.getDefaultToolkit().menuShortcutKeyMaskEx
        )
        stopItem.addActionListener {
            output?.didPressStop()
        }
        stopItem.isEnabled = false
        runMenu.add(stopItem)

        menuBar.add(fileMenu)
        menuBar.add(runMenu)

        return menuBar
    }
}