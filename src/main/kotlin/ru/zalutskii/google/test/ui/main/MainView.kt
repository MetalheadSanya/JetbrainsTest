package ru.zalutskii.google.test.ui.main

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Toolkit
import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel


class MainView : MainViewInput, CoroutineScope by CoroutineScope(Dispatchers.Swing) {

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

    private val toast = JPanel()
    private val toastLabel = JLabel()
    private val toastTimer: Timer

    var output: MainViewOutput? = null

    var treeModel: DefaultTreeModel? = null

    init {
        toastTimer = Timer(3000) {
            hideToast()
        }
        toastTimer.isRepeats = false

        createUi()
    }

    private fun hideToast() {
        launch {
            frame.glassPane.isVisible = false
        }
    }

    fun show() {
        launch {
            frame.isVisible = true
        }
    }

    override fun setTreeNode(treeNode: DefaultMutableTreeNode) {
        launch {
            treeModel = DefaultTreeModel(treeNode)
            testTree.model = treeModel
            for (i in testTree.rowCount - 1 downTo 0) {
                testTree.expandRow(i)
            }
        }
    }

    override fun updateTreeNode(treeNode: DefaultMutableTreeNode) {
        val currentList = mutableListOf<DefaultMutableTreeNode>()
        val newList = mutableListOf<DefaultMutableTreeNode>()

        val currentTree = treeModel?.root as? DefaultMutableTreeNode
        if (currentTree == null) {
            setTreeNode(treeNode)
            return
        }

        currentList.add(currentTree)
        newList.add(treeNode)

        val changedNodes = mutableListOf<DefaultMutableTreeNode>()

        while (currentList.isNotEmpty()) {
            val currentNode = currentList.removeAt(0)
            val newNode = newList.removeAt(0)

            currentList.addAll(currentNode.children().asSequence().map { it as DefaultMutableTreeNode })
            newList.addAll(newNode.children().asSequence().map { it as DefaultMutableTreeNode })

            val currentModel = currentNode.userObject as TestViewModel
            val newModel = newNode.userObject as TestViewModel

            if (currentModel != newModel) {
                currentNode.userObject = newModel
                changedNodes.add(currentNode)
            }
        }

        launch {
            changedNodes.forEach { treeModel?.nodeChanged(it) }
        }
    }

    override fun setOpenActionEnabled(enabled: Boolean) {
        launch {
            openItem.isEnabled = enabled
            openButton.isEnabled = enabled
        }
    }

    override fun setRunActionEnabled(enabled: Boolean) {
        launch {
            runItem.isEnabled = enabled
            runButton.isEnabled = enabled
        }
    }

    override fun setStopActionEnabled(enabled: Boolean) {
        launch {
            stopItem.isEnabled = enabled
            stopButton.isEnabled = enabled
        }
    }

    override fun setRunFailedActionEnabled(enabled: Boolean) {
        launch {
            runFailedItem.isEnabled = enabled
            runFailedButton.isEnabled = enabled
        }
    }

    override fun setLog(log: String) {
        launch {
            logArea.text = log
        }
    }

    override fun showToast(text: String) {
        launch {
            toastLabel.text = text
            frame.glassPane.isVisible = true
            toastTimer.stop()
            toastTimer.start()
        }
    }

    private fun createUi() {
        frame.title = "GoogleTestUI"
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.size = Dimension(600, 400)
        frame.setLocationRelativeTo(null)

        val glassPane = JPanel()
        frame.glassPane = glassPane
        val glassLayout = SpringLayout()
        glassPane.layout = glassLayout
        glassPane.isOpaque = false

        frame.jMenuBar = createMenuBar()

        val layout = SpringLayout()
        frame.contentPane.layout = layout

        val toolbar = createToolBar()
        frame.contentPane.add(toolbar)

        testTree.model = null
        testTree.border = BorderFactory.createEmptyBorder(4, 4, 4, 4)
        testTree.addTreeSelectionListener {
            if (it.newLeadSelectionPath != null) {
                output?.didSelectTest(it.newLeadSelectionPath)
            } else {
                output?.didSelectTest(testTree.getPathForRow(0))
            }
        }
        testTree.cellRenderer = TestCellRenderer()


        val treeScrollPane = JScrollPane(testTree)
        treeScrollPane.setViewportView(testTree)

        val logScrollPane = JScrollPane(logArea)
        logArea.disabledTextColor = Color.BLACK
        logArea.font = Font("monospaced", Font.PLAIN, 12)
        logArea.isEnabled = false

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

        toastLabel.foreground = Color.WHITE

        toast.border = BorderFactory.createEmptyBorder(4, 4, 4, 4)
        toast.background = Color.DARK_GRAY
        toast.add(toastLabel)

        glassPane.add(toast)
        glassLayout.putConstraint(SpringLayout.EAST, toast, -8, SpringLayout.EAST, glassPane)
        glassLayout.putConstraint(SpringLayout.SOUTH, toast, -8, SpringLayout.SOUTH, glassPane)
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