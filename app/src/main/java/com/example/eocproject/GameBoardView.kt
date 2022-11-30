package com.example.eocproject

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import java.util.TreeMap


open class GameBoardView(context: Context, val rows: Int, val cols: Int, val viewModel: GameViewModel) :
    View(context) {
    internal var grid: ArrayList<ArrayList<ItemData>> = ArrayList()
    internal var wireGrid: ArrayList<ArrayList<WireInfo>> = ArrayList() //wire, powered
    private var border: Paint
    private var borderWidth: Int = 0
    protected var cellSz: Float = 0F
    private var backgroundPaint = Paint().apply {
        style = Paint.Style.FILL
    }
    companion object var storedBitmaps: MutableMap<ItemType, Pair<Bitmap, Bitmap>> = HashMap()
    private val BLANK_ITEM = ItemData(ItemType.EMPTY, Direction.UP, Origin.GAMEBOARD)

    class WireInfo(var isWire: Boolean, var isPowered: Boolean, var origin: Origin)
    private val BLANK_WIRE = WireInfo(false, false, Origin.GAMEBOARD)

    init {
        border = Paint(Color.BLACK)
        borderWidth = 5

        initBoard()         //Empty state
        initDragListener()  //Receive drag drops from item bag
        initTouchListener() //Clear item & rotate logic

        invalidate()
        Log.d("XXX", "init Gameboard")
    }

    private fun initBoard() {
        for (i in 0 until rows) {
            val nextRow = java.util.ArrayList<ItemData>()
            val nextWireRow = ArrayList<WireInfo>()
            for (j in 0 until cols) {
                nextRow.add(BLANK_ITEM)
                nextWireRow.add(BLANK_WIRE)
            }
            grid.add(nextRow)
            wireGrid.add(nextWireRow)
        }
    }

    private fun initDragListener() {
        this.setOnDragListener { v, event ->
            if (event.action == DragEvent.ACTION_DROP) {
                val x = (event.x / cellSz).toInt()
                val y = (event.y / cellSz).toInt()

                val data = event.localState as ItemData
                val itemType = data.type
                val direction = data.direction
                val origin = data.origin
                Log.d("XXX", String.format("Drop received %d %d", x, y))
                Log.d("xxx", itemType.toString())

                if (itemType != ItemType.CABLE) {
                    //Check if empty or replacing another item
                    grid[x][y] = ItemData(itemType, direction, origin)
                } else {
                    wireGrid[x][y] = WireInfo(true, false, origin)
                }

                invalidate()
            }
            true
        }
    }

    private fun initTouchListener() {
        this.setOnTouchListener { v, event ->
            if (viewModel.getIsRunning()) { //Can't modify state while game is running
                return@setOnTouchListener true
            }
            if (event.action == MotionEvent.ACTION_DOWN) {
                val x = (event.x / cellSz).toInt()
                val y = (event.y / cellSz).toInt()

                val itemType = grid[x][y].type
                val direction = grid[x][y].direction

                if (viewModel.getClearMode()) {
                    if (itemType != ItemType.EMPTY) {
                        if (grid[x][y].origin == Origin.USER) {//Return to inventory
                            viewModel.addItem(itemType)
                        }
                        grid[x][y] = BLANK_ITEM
                        invalidate()
                    } else {
                        if (wireGrid[x][y].isWire) {
                            if (wireGrid[x][y].origin == Origin.USER) {//Return to inventory
                                viewModel.addItem(ItemType.CABLE)
                            }

                            wireGrid[x][y] = BLANK_WIRE
                            invalidate()
                        }
                    }
                } else {    //Rotation mode TODO: this affects all the cells at the same time.
                    if (itemType != ItemType.EMPTY) {
                        if (viewModel.getCreative()) {
                            when (direction) {
                                Direction.UP -> grid[x][y].direction = Direction.RIGHT
                                Direction.DOWN -> grid[x][y].direction = Direction.LEFT
                                Direction.LEFT -> grid[x][y].direction = Direction.UP
                                Direction.RIGHT -> grid[x][y].direction = Direction.DOWN
                            }
                        } else {
                            when (direction) {
                                Direction.UP -> grid[x][y].direction = Direction.DOWN
                                Direction.DOWN -> grid[x][y].direction = Direction.UP
                                Direction.LEFT -> grid[x][y].direction = Direction.RIGHT
                                Direction.RIGHT -> grid[x][y].direction = Direction.LEFT
                            }
                        }
                        invalidate()
                    }
                }
            }
            true
        }
    }

    private fun initBitmaps() {
        for (i in 0 until ItemBag.itemTypeList.size) {
            val base = BitmapFactory.decodeResource(resources, ItemBag.sourceList[i])
            val unpowered = Bitmap.createScaledBitmap(base, cellSz.toInt(), cellSz.toInt(), true)
            val powered = when (ItemBag.itemTypeList[i]) {
                ItemType.SOURCE, ItemType.DESTINATION, ItemType.PANEL -> {
                    changeBitmapColor(unpowered, Color.GREEN) {pixel -> pixel != 0}
                }
                ItemType.LIGHT -> {
                    changeBitmapColor(unpowered, Color.YELLOW) {pixel -> pixel == 0}
                }
                else -> unpowered
            }
            storedBitmaps.put(ItemBag.itemTypeList[i], Pair(unpowered, powered))
        }
    }

    private fun changeBitmapColor(bitmap: Bitmap, color: Int, targetPixels: (pixel: Int) -> Boolean) : Bitmap {
        var output = bitmap.copy(bitmap.config, true)
        var pixels: IntArray = IntArray(output.width * output.height)
        output.getPixels(pixels, 0, output.width, 0, 0, output.width, output.height)
        for (i in 0 until pixels.size) {
            if (targetPixels(pixels[i]))
                pixels[i] = color
        }
        output.setPixels(pixels, 0, output.width, 0, 0, output.width, output.height)
        return output
    }

    private fun drawCell(canvas: Canvas, paint: Paint, x: Int, y: Int) {
        // XXX Draw a cell at the right location, which is a bordered square
        drawBorderedSquare(canvas, paint, x * cellSz, y * cellSz, cellSz)

        val item = grid[x][y]
        val type = item.type
        val direction = item.direction

        if (wireGrid[x][y].isWire) {
            val wireColor = if (wireGrid[x][y].isPowered) Paint().apply {color = Color.GREEN} else Paint().apply {color = Color.BLUE}
            drawWire(canvas, x, y, wireColor) //Different color if powered
        }

        when (type) {
            ItemType.EMPTY -> {
            }
            else -> { //TODO: Maybe not redraw everytime; for alt colors just use another bitmap.
                val bitmap = if (wireGrid[x][y].isPowered) storedBitmaps.get(type)!!.second
                            else storedBitmaps.get(type)!!.first
               val rotatedBitmap = when (direction) {
                    Direction.LEFT -> {RotateBitmap(bitmap, 270f)}
                    Direction.UP -> {bitmap}
                    Direction.RIGHT -> {RotateBitmap(bitmap, 90f)}
                    Direction.DOWN -> {RotateBitmap(bitmap, 180f)}
                }
                canvas.drawBitmap(rotatedBitmap, x * cellSz, y * cellSz, null)}
        }
    }

    fun RotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
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

    private fun drawWire(canvas: Canvas, x: Int, y: Int, color: Paint) {
        val outletSize = cellSz / 3
        val wireSize = cellSz / 7
        val outletOffset = (cellSz - outletSize) / 2
        val wireOffset = (cellSz - wireSize) / 2

        val cellLeftWall = x * cellSz
        val cellTopWall = y * cellSz
        var adjCount = 0

        if (x > 0 && wireGrid[x-1][y].isWire) {
            canvas.drawRect(cellLeftWall, cellTopWall + wireOffset,
                cellLeftWall + (cellSz / 2) + wireSize / 2, cellTopWall + cellSz - wireOffset, color)
            adjCount++
        }
        if (x < wireGrid.size-1 && wireGrid[x+1][y].isWire) {
            canvas.drawRect(cellLeftWall + (cellSz / 2) - wireSize / 2, cellTopWall + wireOffset,
                cellLeftWall + cellSz, cellTopWall + cellSz - wireOffset, color)
            adjCount++
        }
        if (y > 0 && wireGrid[x][y-1].isWire) {
            canvas.drawRect(cellLeftWall + wireOffset, cellTopWall,
                cellLeftWall + cellSz - wireOffset, cellTopWall + (cellSz / 2) + wireSize / 2, color)
            adjCount++
        }
        if (y < wireGrid[x].size-1 && wireGrid[x][y+1].isWire) {
            canvas.drawRect(cellLeftWall + wireOffset, cellTopWall + (cellSz / 2) - wireSize / 2,
                cellLeftWall + cellSz - wireOffset, cellTopWall + cellSz, color)
            adjCount++
        }

        if (adjCount < 2) {
            canvas.drawRect(
                cellLeftWall + outletOffset, cellTopWall + outletOffset,
                cellLeftWall + cellSz - outletOffset, cellTopWall + cellSz - outletOffset, color
            )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldh: Int, oldw: Int) {
        cellSz = if (h >= w)
            w.toFloat() / cols.toFloat()
        else
            h.toFloat() / rows.toFloat()
        initBitmaps()
    }

    override fun onDraw(canvas: Canvas) { //TODO: This should be done on seperate thread, skipping frames.
        super.onDraw(canvas)
        backgroundPaint.color = Color.LTGRAY
        canvas.drawPaint(backgroundPaint)

        for (i in 0 until grid.size) {
            for (j in 0 until grid[0].size) {
                drawCell(canvas, backgroundPaint, i, j)
            }
        }
    }
}