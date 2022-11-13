package com.example.eocproject

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.View

open class GameBoardView(context: Context, val rows: Int, val cols: Int) :
    View(context) {
    internal var grid: ArrayList<ArrayList<Item>> = ArrayList()
    private var border: Paint
    private var borderWidth: Int = 0
    protected var cellSz: Float = 0F
    private var backgroundPaint = Paint().apply {
        style = Paint.Style.FILL
    }

    init {
        border = Paint(Color.BLACK)
        borderWidth = 5


        for (i in 0 until rows) {
            val nextRow = java.util.ArrayList<Item>()
            for (j in 0 until cols) {
                nextRow.add(Item(context, Item.ItemType.EMPTY, Item.Direction.UP))
            }
            grid.add(nextRow)
        }
        invalidate()
        Log.d("XXX", "init Gameboard")
    }

    private fun drawCell(canvas: Canvas, paint: Paint, x: Int, y: Int) {
        // XXX Draw a cell at the right location, which is a bordered square
        drawBorderedSquare(canvas, paint, x * cellSz, y * cellSz, cellSz)
    }

    private fun drawBorderedSquare(canvas: Canvas, paint: Paint, i: Float, j: Float, size: Float) {
        // XXX Draw a bordered square
        drawSquare(canvas, paint, i, j, size)
        canvas.drawRect(i, j, i + size, j + borderWidth, border) // top
        canvas.drawRect(i + size - borderWidth, j, i + size, j + size, border) // right
        canvas.drawRect(i, j + size - borderWidth, i + size, j + size, border) // bot
        canvas.drawRect(i, j, i + borderWidth, j + size, border) // left
    }

    private fun drawSquare(canvas: Canvas, paint: Paint?, i: Float, j: Float, size: Float) {
        // XXX Draw a square
        var squareColor = if (paint == null) Paint().apply { color = Color.WHITE } else paint
        canvas.drawRect(i, j, i + size, j + size, squareColor)
    }

    override fun onSizeChanged(w: Int, h: Int, oldh: Int, oldw: Int) {
        cellSz = if (h >= w)
            w.toFloat() / cols.toFloat()
        else
            h.toFloat() / rows.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        backgroundPaint.color = Color.WHITE
        canvas.drawPaint(backgroundPaint)

        for (i in 0 until grid.size) {
            for (j in 0 until grid[0].size) {
                val item = grid[i][j]
                val type = item.type
                val direction = item.direction

                drawCell(canvas, backgroundPaint, i, j)
            }
        }
        Log.d("XXX", "Game on draw")
        Log.d("XXX", grid.size.toString())
    }
}