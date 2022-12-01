package com.example.eocproject.Game

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.io.File
import com.example.eocproject.MainActivity

class GameBoard(context: Context,
                rows: Int,
                cols: Int, viewModel: GameViewModel
) : GameBoardView(context, rows, cols, viewModel) {

    fun startGame() {
        viewModel.setIsRunning(true)
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                if (grid[i][j].type == ItemType.SOURCE) {
                    powerOn(i, j)
                }
            }
        }
        invalidate()
    }

    fun endGame() {
        clearPower()
        viewModel.setIsRunning(false)
    }

    private fun withinBounds(x: Int, y: Int) : Boolean {
        return x >= 0 && x < wireGrid.wireGrid.size && y >= 0 && y < wireGrid[0].size
    }

    //Powers each connecting tile with a wire
    private fun powerOn(x: Int, y: Int) {
        //within bounds and a wire
        if (!wireGrid[x][y].isPowered) {
            wireGrid[x][y].isPowered = true

            if (wireGrid[x][y].isWire) {
                if (withinBounds(x - 1, y) && wireGrid[x - 1][y].isWire)
                    powerOn(x - 1, y)
                if (withinBounds(x, y - 1) && wireGrid[x][y - 1].isWire)
                    powerOn(x, y - 1)
                if (withinBounds(x + 1, y) && wireGrid[x + 1][y].isWire)
                    powerOn(x + 1, y)
                if (withinBounds(x, y + 1) && wireGrid[x][y + 1].isWire)
                    powerOn(x, y + 1)
            }

            when (grid[x][y].type) { //Functionality of signalers
                ItemType.LIGHT -> lightOn(x, y)
                ItemType.DESTINATION -> {
                    if (checkDestsPowered()) {
                        winGame()
                    }
                }
                else -> {}
            }
        }
    }

    fun clearPower() {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                wireGrid[i][j].isPowered = false
            }
        }
        invalidate()
    }

    //Used when a light on x, y is powered
    private fun lightOn(x: Int, y: Int) {
        assert(grid[x][y].type == ItemType.LIGHT)

        scanDirection(x, y, ItemType.PANEL, Direction.UP)
        scanDirection(x, y, ItemType.PANEL, Direction.RIGHT)
        scanDirection(x, y, ItemType.PANEL, Direction.DOWN)
        scanDirection(x, y, ItemType.PANEL, Direction.LEFT)
    }

    //Checks tiles in direction until a non empty item is found, powers the last tile if it is a goal
    private fun scanDirection(x: Int, y: Int, goal: ItemType, direction: Direction) {
        when (direction) {
            Direction.RIGHT -> {
                for (i in x+1 until grid.grid.size) {
                    if (grid[i][y].type == goal) {
                        powerOn(i, y)
                    }
                    if (grid[i][y].type != ItemType.EMPTY) {
                        break
                    }
                }
            }
            Direction.DOWN -> {
                for (i in y+1 until grid[x].size) {
                    if (grid[x][i].type == goal) {
                        powerOn(x, i)
                    }
                    if (grid[x][i].type != ItemType.EMPTY) {
                        break
                    }
                }
            }
            Direction.LEFT -> {
                for (i in x-1 downTo 0) {
                    if (grid[i][y].type == goal) {
                        powerOn(i, y)
                    }
                    if (grid[i][y].type != ItemType.EMPTY) {
                        break
                    }
                }
            }
            Direction.UP -> {
                for (i in y-1 downTo 0) {
                    if (grid[x][i].type == goal) {
                        powerOn(x, i)
                    }
                    if (grid[x][i].type != ItemType.EMPTY) {
                        break
                    }
                }
            }
        }
    }

    private fun winGame() {
        val toast = Toast(context)
        toast.setText("You Win!")
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
        if (!viewModel.isCreative) {
            viewModel.setDemoCleared(true)
        }
    }

    // Checks each destination is powered.
    private fun checkDestsPowered() : Boolean {
        if (viewModel.getInventory()[ItemBag.itemTypeList.indexOf(ItemType.DESTINATION)] != 0) {
            return false
        }
        for (i in 0 until grid.grid.size) {
            for (j in 0 until grid[0].size) {
                if (grid[i][j].type == ItemType.DESTINATION && !wireGrid[i][j].isPowered) {
                    return false
                }
            }
        }
        return true
    }

    //https://stackoverflow.com/a/67229929
    private fun write4BytesToBuffer(data: Int) : ByteArray {
        val buffer = ByteArray(4)
        buffer[0] = (data shr 0).toByte()
        buffer[1] = (data shr 8).toByte()
        buffer[2] = (data shr 16).toByte()
        buffer[3] = (data shr 24).toByte()
        return buffer
    }

    private fun read4BytesFromBuffer(buffer: ByteArray, offset: Int): Int {
        return (buffer[offset + 3].toInt() shl 24) or
                (buffer[offset + 2].toInt() and 0xff shl 16) or
                (buffer[offset + 1].toInt() and 0xff shl 8) or
                (buffer[offset + 0].toInt() and 0xff)
    }

    fun exportGrid(uuid: String) : File {
        val fileGrid = MainActivity.localLevelFile(uuid)
        fileGrid.writeBytes(write4BytesToBuffer(grid.grid.size))
        fileGrid.writeBytes(write4BytesToBuffer(grid[0].size))
        for (i in 0 until grid.grid.size) {
            for (j in 0 until grid[0].size) {
                if (grid[i][j].type != ItemType.EMPTY &&  grid[i][j].origin == Origin.GAMEBOARD) {
                    fileGrid.writeBytes(write4BytesToBuffer(i))
                    fileGrid.writeBytes(write4BytesToBuffer(j))
                    fileGrid.writeBytes(write4BytesToBuffer(grid[i][j].type.value))
                    fileGrid.writeBytes(write4BytesToBuffer(grid[i][j].direction.value))
                }
            }
        }
        return fileGrid
    }

    fun exportWireGrid(uuid: String) : File {
        val fileWire = MainActivity.localLevelFile(uuid)
        for (i in 0 until wireGrid.wireGrid.size) {
            for (j in 0 until wireGrid[0].size) {
                if (wireGrid[i][j].isWire && wireGrid[i][j].origin == Origin.GAMEBOARD) {
                    fileWire.writeBytes(write4BytesToBuffer(i))
                    fileWire.writeBytes(write4BytesToBuffer(j))
                }
            }
        }
        return fileWire
    }

    fun returnInv() {
        for (i in 0 until grid.grid.size) {
            for (j in 0 until grid[0].size) {
                if (grid[i][j].origin == Origin.USER) {
                    clearAtCell(i, j)
                }
                if (wireGrid[i][j].origin == Origin.USER) {
                    viewModel.addItem(ItemType.CABLE)
                    wireGrid[i][j] = BLANK_WIRE
                }
            }
        }
    }

    fun getGrid() : Grid {
        return grid
    }

    fun getWireGrid() : WireGrid {
        return wireGrid
    }

    fun setGridState(grid: Grid, wireGrid: WireGrid) {
        this.grid = grid
        this.wireGrid = wireGrid
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun unpack(bundle: Bundle) {
        grid = bundle.getParcelable(PlayGame.gridTag, Grid::class.java)!!

        invalidate()
        wireGrid = bundle.getParcelable(PlayGame.wireTag, WireGrid::class.java)!!
        invalidate()
    }
}