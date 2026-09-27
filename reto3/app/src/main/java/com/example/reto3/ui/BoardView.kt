package com.example.reto3.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.reto3.R
import com.example.reto3.logic.TicTacToeGame

object BoardViewDefaults {
    val GRID_WIDTH: Dp = 6.dp
    val GRID_COLOR: Color = Color.LightGray
}

/**
 * Custom BoardView composable that renders the Tic-Tac-Toe grid and X/O graphics using Canvas drawing,
 * and detects touches to calculate the tapped board cell (row * 3 + col).
 */
@Composable
fun BoardView(
    board: List<Char>,
    onCellTouched: (Int) -> Unit,
    modifier: Modifier = Modifier,
    gridWidth: Dp = BoardViewDefaults.GRID_WIDTH,
    gridColor: Color = BoardViewDefaults.GRID_COLOR
) {
    val humanPainter = painterResource(id = R.drawable.x_img)
    val computerPainter = painterResource(id = R.drawable.o_img)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val cellWidth = size.width / 3f
                    val cellHeight = size.height / 3f
                    val col = (offset.x / cellWidth).toInt().coerceIn(0, 2)
                    val row = (offset.y / cellHeight).toInt().coerceIn(0, 2)
                    val pos = row * 3 + col
                    if (pos in 0 until TicTacToeGame.BOARD_SIZE) {
                        onCellTouched(pos)
                    }
                }
            }
    ) {
        val boardWidth = size.width
        val boardHeight = size.height
        val cellWidth = boardWidth / 3f
        val cellHeight = boardHeight / 3f
        val strokeWidthPx = gridWidth.toPx()

        // 1. Draw 2 Vertical lines and 2 Horizontal lines
        drawGridLines(
            boardWidth = boardWidth,
            boardHeight = boardHeight,
            cellWidth = cellWidth,
            cellHeight = cellHeight,
            strokeWidthPx = strokeWidthPx,
            gridColor = gridColor
        )

        // 2. Draw all X and O images inside destination cell rectangles
        for (i in 0 until TicTacToeGame.BOARD_SIZE) {
            val col = i % 3
            val row = i / 3

            val padding = strokeWidthPx * 1.5f
            val left = col * cellWidth + padding
            val top = row * cellHeight + padding
            val right = (col + 1) * cellWidth - padding
            val bottom = (row + 1) * cellHeight - padding

            val occupant = board[i]
            val painter: Painter? = when (occupant) {
                TicTacToeGame.HUMAN_SYMBOL -> humanPainter
                TicTacToeGame.COMPUTER_SYMBOL -> computerPainter
                else -> null
            }

            painter?.let { p ->
                val destSize = Size(width = right - left, height = bottom - top)
                if (destSize.width > 0 && destSize.height > 0) {
                    drawContext.canvas.save()
                    drawContext.transform.translate(left = left, top = top)
                    with(p) {
                        draw(size = destSize)
                    }
                    drawContext.canvas.restore()
                }
            }
        }
    }
}

private fun DrawScope.drawGridLines(
    boardWidth: Float,
    boardHeight: Float,
    cellWidth: Float,
    cellHeight: Float,
    strokeWidthPx: Float,
    gridColor: Color
) {
    // Two vertical lines
    drawLine(
        color = gridColor,
        start = Offset(cellWidth, 0f),
        end = Offset(cellWidth, boardHeight),
        strokeWidth = strokeWidthPx,
        cap = StrokeCap.Round
    )
    drawLine(
        color = gridColor,
        start = Offset(cellWidth * 2f, 0f),
        end = Offset(cellWidth * 2f, boardHeight),
        strokeWidth = strokeWidthPx,
        cap = StrokeCap.Round
    )

    // Two horizontal lines
    drawLine(
        color = gridColor,
        start = Offset(0f, cellHeight),
        end = Offset(boardWidth, cellHeight),
        strokeWidth = strokeWidthPx,
        cap = StrokeCap.Round
    )
    drawLine(
        color = gridColor,
        start = Offset(0f, cellHeight * 2f),
        end = Offset(boardWidth, cellHeight * 2f),
        strokeWidth = strokeWidthPx,
        cap = StrokeCap.Round
    )
}
