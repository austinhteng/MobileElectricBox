package com.example.eocproject

class Cell (var type : ItemType, var direction : Direction) {
    enum class Direction {
        LEFT, UP, RIGHT, DOWN
    }
    enum class ItemType {
        EMPTY, LIGHT
    }
}