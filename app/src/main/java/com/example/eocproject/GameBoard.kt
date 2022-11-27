package com.example.eocproject

import android.content.Context

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
        return x > 0 && x < wireGrid.size && y > 0 && y < wireGrid[0].size
    }

    fun powerOn(x: Int, y: Int) {
        //within bounds and a wire
        if (withinBounds(x,y) && wireGrid[x][y].first && !wireGrid[x][y].second) {
            wireGrid[x][y] = Pair(true, true)
            powerOn(x-1, y)
            powerOn(x, y-1)
            powerOn(x+1, y)
            powerOn(x, y+1)

            when (grid[x][y].type) {
                Item.ItemType.LIGHT -> lightOn(x, y)
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

    fun lightOn(x: Int, y: Int) {
        assert(grid[x][y].type == Item.ItemType.LIGHT)

        scanDirection(x, y, Item.ItemType.PANEL, Item.Direction.UP)
        scanDirection(x, y, Item.ItemType.PANEL, Item.Direction.RIGHT)
        scanDirection(x, y, Item.ItemType.PANEL, Item.Direction.DOWN)
        scanDirection(x, y, Item.ItemType.PANEL, Item.Direction.LEFT)
    }

    fun scanDirection(x: Int, y: Int, goal: Item.ItemType, direction: Item.Direction) {
        when (direction) {
            Item.Direction.RIGHT -> {
                for (i in x until grid.size) {
                    if (grid[i][y].type == goal) {
                        powerOn(i, y)
                    }
                    if (grid[i][y].type != Item.ItemType.EMPTY) {
                        break
                    }
                }
            }
            Item.Direction.DOWN -> {
                for (i in y until grid[x].size) {
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
}