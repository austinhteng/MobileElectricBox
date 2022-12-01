package com.example.eocproject

import android.os.Parcel
import android.os.Parcelable

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
        while (parcel.dataAvail() != 0) {
            val x = parcel.readInt()
            val y = parcel.readInt()
            val type = parcel.readInt()
            val direction = parcel.readInt()
            grid[x][y].type = ItemType.fromInt(type)
            grid[x][y].direction = Direction.fromInt(direction)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(grid.size)
        parcel.writeInt(grid[0].size)
        for (i in 0 until grid.size) {
            for (j in 0 until grid[0].size) {
                if (grid[i][j].type != ItemType.EMPTY &&  grid[i][j].origin == Origin.GAMEBOARD) {
                    parcel.writeInt(i)
                    parcel.writeInt(j)
                    parcel.writeInt(grid[i][j].type.value)
                    parcel.writeInt(grid[i][j].direction.value)
                }
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
    }
}

class WireGrid() : Parcelable {
    var wireGrid : ArrayList<ArrayList<GameBoardView.WireInfo>> = ArrayList()
    constructor(parcel: Parcel) : this() {
        val rows = parcel.readInt()
        val cols = parcel.readInt()
        initGrid(rows, cols)
        while (parcel.dataAvail() != 0) {
            val x = parcel.readInt()
            val y = parcel.readInt()
            wireGrid[x][y].isWire = true
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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(wireGrid.size)
        parcel.writeInt(wireGrid[0].size)
        for (i in 0 until wireGrid.size) {
            for (j in 0 until wireGrid[0].size) {
                if (wireGrid[i][j].isWire && wireGrid[i][j].origin == Origin.GAMEBOARD) {
                    parcel.writeInt(i)
                    parcel.writeInt(j)
                }
            }
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WireGrid> {
        override fun createFromParcel(parcel: Parcel): WireGrid {
            return WireGrid(parcel)
        }

        override fun newArray(size: Int): Array<WireGrid?> {
            return arrayOfNulls(size)
        }
    }

}