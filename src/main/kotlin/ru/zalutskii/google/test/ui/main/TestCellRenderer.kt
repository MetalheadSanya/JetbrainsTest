package ru.zalutskii.google.test.ui.main

import java.awt.Component
import java.awt.FlowLayout
import javax.swing.*
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.TreeCellRenderer

class TestCellRenderer : TreeCellRenderer {

    private val cell = JPanel()

    private val nameLabel = JLabel()

    private val timeLabel = JLabel()

    private val defaultRenderer = DefaultTreeCellRenderer()

    private val backgroundSelectionColor = defaultRenderer.backgroundSelectionColor

    private val backgroundNonSelectionColor = defaultRenderer.backgroundNonSelectionColor

    init {
        nameLabel.verticalTextPosition = SwingConstants.CENTER

        (cell.layout as? FlowLayout)?.vgap = 0

        cell.add(nameLabel)
        cell.add(timeLabel)
    }


    override fun getTreeCellRendererComponent(
        tree: JTree?,
        data: Any?,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ): Component {
        val model = extractViewModel(data)
        return model?.let { configureCell(it, selected, tree) } ?: defaultRenderer.getTreeCellRendererComponent(
            tree,
            data,
            selected,
            expanded,
            leaf,
            row,
            hasFocus
        )
    }

    private fun extractViewModel(data: Any?): TestViewModel? =
        when (data) {
            is DefaultMutableTreeNode -> {
                when (val userObject = data.userObject) {
                    is TestViewModel -> {
                        userObject
                    }
                    else -> null
                }
            }
            else -> null
        }


    private fun configureCell(
        userObject: TestViewModel,
        selected: Boolean,
        tree: JTree?
    ): JPanel {
        nameLabel.text = userObject.name

        nameLabel.icon = when (userObject.status) {
            null -> null
            TestViewModel.Status.QUEUED -> getImage("queued_status.png")
            TestViewModel.Status.RUN -> getImage("run_status.png")
            TestViewModel.Status.FAIL -> getImage("fail_status.png")
            TestViewModel.Status.SUCCESS -> getImage("success_status.png")
        }

        timeLabel.text = when (val time = userObject.time) {
            null -> ""
            else -> time
        }

        when (selected) {
            true -> cell.background = backgroundSelectionColor
            false -> cell.background = backgroundNonSelectionColor
        }
        cell.isEnabled = tree?.isEnabled ?: false
        return cell
    }

    private fun getImage(name: String): ImageIcon =
        ImageIcon(javaClass.getResource(name))
}