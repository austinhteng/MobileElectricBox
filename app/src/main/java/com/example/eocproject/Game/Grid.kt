package com.example.eocproject.Game

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import java.io.File

class Grid() : Parcelable{
    var grid : ArrayList<ArrayList<ItemData>> = ArrayList()

    fun initGrid(rows: Int, cols: Int) {
        for (i in 0 until rows) {
            val nextRow = java.util.ArrayList<ItemData>()
//            val nextWireRow = ArrayList<GameBoardView.WireInfo>()
            for (j in 0 until cols) {
                nextRow.add(GameBoardView.BLANK_ITEM)
//                nextWireRow.add(GameBoardView.BLANK_WIRE)
            }
            grid.add(nextRow)
//            wireGrid.add(nextWireRow)
        }
    }

    operator fun get(x: Int) : ArrayList<ItemData>{
        return grid[x]
    }

    constructor(parcel: Parcel) : this() {
        val rows = parcel.readInt()
        val cols = parcel.readInt()
        initGrid(rows, cols)
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val type = parcel.readInt()
                val direction = parcel.readInt()
                grid[i][j] = ItemData(ItemType.fromInt(type), Direction.fromInt(direction), Origin.GAMEBOARD)
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(grid.size)
        parcel.writeInt(grid[0].size)
        for (i in 0 until grid.size) {
            for (j in 0 until grid[0].size) {
                parcel.writeInt(grid[i][j].type.value)
                parcel.writeInt(grid[i][j].direction.value)
            }
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Grid> {
        override fun createFromParcel(parcel: Parcel): Grid {
            return Grid(parcel)
        }

        override fun newArray(size: Int): Array<Grid?> {
            return arrayOfNulls(size)
        }

        fun loadGrid(gridFile: File) : Grid{
            val gridBuffer = gridFile.inputStream()

            val rows = gridBuffer.read()
            val cols = gridBuffer.read()
            var grid = Grid().apply { initGrid(rows, cols) }
            for (x in 0 until rows) { // 1st 4 for bytes of int, 2nd 4 for 4 ints per item
                for (y in 0 until cols) {
                    val type = gridBuffer.read()
                    val direction = gridBuffer.read()
                    grid[x][y] = ItemData(ItemType.fromInt(type), Direction.fromInt(direction), Origin.GAMEBOARD)
                }
            }
            return grid
        }
    }
}

class WireGrid() : Parcelable {
    var wireGrid : ArrayList<ArrayList<GameBoardView.WireInfo>> = ArrayList()
    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(parcel: Parcel) : this() {
        val rows = parcel.readInt()
        val cols = parcel.readInt()
        initGrid(rows, cols)
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                wireGrid[i][j] =
                    GameBoardView.WireInfo(parcel.readBoolean(), false, Origin.GAMEBOARD)
            }
        }
    }

    fun initGrid(rows: Int, cols: Int) {
        for (i in 0 until rows) {
            val nextWireRow = ArrayList<GameBoardView.WireInfo>()
            for (j in 0 until cols) {
                nextWireRow.add(GameBoardView.BLANK_WIRE)
            }
            wireGrid.add(nextWireRow)
        }
    }

    operator fun get(x: Int) : ArrayList<GameBoardView.WireInfo>{
        return wireGrid[x]
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(wireGrid.size)
        parcel.writeInt(wireGrid[0].size)
        for (i in 0 until wireGrid.size) {
            for (j in 0 until wireGrid[0].size) {
                parcel.writeBoolean(wireGrid[i][j].isWire)
            }
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WireGrid> {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun createFromParcel(parcel: Parcel): WireGrid {
            return WireGrid(parcel)
        }

        override fun newArray(size: Int): Array<WireGrid?> {
            return arrayOfNulls(size)
        }

        fun loadWire(wireFile: File) : WireGrid {
            return WireGrid().apply { initGrid(10,10) }
        }
    }
}