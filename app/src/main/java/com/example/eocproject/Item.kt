package com.example.eocproject

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class Item (var type : ItemType, var direction : Direction) {
    enum class Direction {
        LEFT, UP, RIGHT, DOWN
    }
    enum class ItemType {
        EMPTY, LIGHT
    }
}