package com.example.eocproject

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView

class Item (var type : ItemType, var direction : Direction, context: Context) : androidx.appcompat.widget.AppCompatImageView(context){
    enum class Direction {
        LEFT, UP, RIGHT, DOWN
    }
    enum class ItemType {
        EMPTY, LIGHT, SOURCE, DESTINATION, CABLE, PANEL
    }
}