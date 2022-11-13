package com.example.eocproject

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

open class GameBoardView(context: Context, val rows: Int, val cols: Int) :
    View(context) {
    private var grid: ArrayList<ArrayList<Cell>> = ArrayList()
    private var border: Paint
    private var borderWidth: Int = 0
    protected var cellSz: Float = 0F
    private var backgroundPaint = Paint().apply {
        style = Paint.Style.FILL
    }

    init {
        border = Paint(Color.BLACK)
        borderWidth = 5


        for (i in 0 until this.height) {
            val nextRow = java.util.ArrayList<Cell>()
            for (j in 0 until this.width) {
                nextRow.add(Cell(Cell.ItemType.EMPTY, Cell.Direction.UP))
            }
            grid.add(nextRow)
        }
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

    }
}