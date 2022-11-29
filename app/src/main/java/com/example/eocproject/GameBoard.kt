package com.example.eocproject

import android.content.Context
import android.widget.Toast

class GameBoard(context: Context,
                rows: Int,
                cols: Int, viewModel: GameViewModel) : GameBoardView(context, rows, cols, viewModel) {

    fun startGame() {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                if (grid[i][j].type == Item.ItemType.SOURCE) {
                    powerOn(i, j)
                }
            }
        }
        invalidate()
    }

    private fun withinBounds(x: Int, y: Int) : Boolean {
        return x >= 0 && x < wireGrid.size && y >= 0 && y < wireGrid[0].size
    }

    //Powers each connecting tile with a wire
    private fun powerOn(x: Int, y: Int) {
        //within bounds and a wire
        if (!wireGrid[x][y].second) {
            wireGrid[x][y] = Pair(wireGrid[x][y].first, true)

            if (wireGrid[x][y].first) {
                if (withinBounds(x - 1, y) && wireGrid[x - 1][y].first)
                    powerOn(x - 1, y)
                if (withinBounds(x, y - 1) && wireGrid[x][y - 1].first)
                    powerOn(x, y - 1)
                if (withinBounds(x + 1, y) && wireGrid[x + 1][y].first)
                    powerOn(x + 1, y)
                if (withinBounds(x, y + 1) && wireGrid[x][y + 1].first)
                    powerOn(x, y + 1)
            }

            when (grid[x][y].type) { //Functionality of signalers
                Item.ItemType.LIGHT -> lightOn(x, y)
                Item.ItemType.DESTINATION -> {
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
                wireGrid[i][j] = Pair(wireGrid[i][j].first, false)
            }
        }
        invalidate()
    }

    //Used when a light on x, y is powered
    private fun lightOn(x: Int, y: Int) {
        assert(grid[x][y].type == Item.ItemType.LIGHT)

        scanDirection(x, y, Item.ItemType.PANEL, Item.Direction.UP)
        scanDirection(x, y, Item.ItemType.PANEL, Item.Direction.RIGHT)
        scanDirection(x, y, Item.ItemType.PANEL, Item.Direction.DOWN)
        scanDirection(x, y, Item.ItemType.PANEL, Item.Direction.LEFT)
    }

    //Checks tiles in direction until a non empty item is found, powers the last tile if it is a goal
    private fun scanDirection(x: Int, y: Int, goal: Item.ItemType, direction: Item.Direction) {
        when (direction) {
            Item.Direction.RIGHT -> {
                for (i in x+1 until grid.size) {
                    if (grid[i][y].type == goal) {
                        powerOn(i, y)
                    }
                    if (grid[i][y].type != Item.ItemType.EMPTY) {
                        break
                    }
                }
            }
            Item.Direction.DOWN -> {
                for (i in y+1 until grid[x].size) {
                    if (grid[x][i].type == goal) {
                        powerOn(x, i)
                    }
                    if (grid[x][i].type != Item.ItemType.EMPTY) {
                        break
                    }
                }
            }
            Item.Direction.LEFT -> {
                for (i in x-1 downTo 0) {
                    if (grid[i][y].type == goal) {
                        powerOn(i, y)
                    }
                    if (grid[i][y].type != Item.ItemType.EMPTY) {
                        break
                    }
                }
            }
            Item.Direction.UP -> {
                for (i in y-1 downTo 0) {
                    if (grid[x][i].type == goal) {
                        powerOn(x, i)
                    }
                    if (grid[x][i].type != Item.ItemType.EMPTY) {
                        break
                    }
                }
            }
        }
    }

    private fun winGame() {
        val toast = Toast(context)
        toast.setText("You Win!")
        toast.duration = Toast.LENGTH_LONG
        toast.show()
    }

    // Checks each destination is powered.
    private fun checkDestsPowered() : Boolean {
        for (i in 0 until grid.size) {
            for (j in 0 until grid[0].size) {
                if (grid[i][j].type == Item.ItemType.DESTINATION && !wireGrid[i][j].second) {
                    return false
                }
            }
        }
        return true
    }
}