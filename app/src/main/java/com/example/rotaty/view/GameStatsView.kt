package com.example.rotaty.view

import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color

class GameStatsView(context: Context) : LinearLayout(context) {
    private val accuracyTextView: TextView
    private val bestTimeTextView: TextView

    init {
        orientation = VERTICAL
        setPadding(32, 16, 32, 16)

        accuracyTextView = TextView(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
            textSize = 16f
            setTextColor(Color.GRAY)
        }

        bestTimeTextView = TextView(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
            textSize = 16f
            setTextColor(Color.GRAY)
        }

        addView(accuracyTextView)
        addView(bestTimeTextView)
    }


    }

