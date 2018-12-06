package me.samuki.musicandspeed.customviews

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import kotlinx.android.synthetic.main.floating_text_action_button.view.*
import me.samuki.musicandspeed.R
import me.samuki.musicandspeed.extensions.dpToPx


class FloatingTextActionButton(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    var buttonColor: Int = R.color.colorAccent
    set(value) {
        field = value
        manageBackgroundColor()
        invalidate()
    }

    var buttonIcon: Int = 0
    set(value) {
        field = value
        manageIcon()
        invalidate()
    }

    var text: String? = null
    set(value) {
        field = value
        manageText()
        invalidate()
    }

    var showText: Boolean = false
    set(value) {
        field = value
        manageMargins()
        invalidate()
    }

    init {
        inflate(context, R.layout.floating_text_action_button, this)
        context.theme.obtainStyledAttributes(attrs, R.styleable.FloatingTextActionButton,
                0, 0).apply {
            try {
                buttonColor = getColor(R.styleable.FloatingTextActionButton_buttonColor,
                        ContextCompat.getColor(context, R.color.colorAccent))
                buttonIcon = getResourceId(R.styleable.FloatingTextActionButton_buttonIcon, 0)
                showText = getBoolean(R.styleable.FloatingTextActionButton_showText, false)
                text = getString(R.styleable.FloatingTextActionButton_text)
            } finally {
                recycle()
            }
        }
        isClickable = true
        isFocusable = true
    }

    private fun manageBackgroundColor() {
        val drawable = buttonContainer.background as GradientDrawable
        drawable.setColor(buttonColor)
        buttonContainer.background = drawable
    }

    private fun manageMargins() {
        val imageViewParams = buttonImageView.layoutParams as ConstraintLayout.LayoutParams
        if (showText) {
            imageViewParams.setMargins(
                    16.dpToPx(context),
                    8.dpToPx(context),
                    8.dpToPx(context),
                    8.dpToPx(context)
            )
            imageViewParams.marginStart = 16.dpToPx(context)
            imageViewParams.marginEnd = 8.dpToPx(context)
            buttonTextView.visibility = View.VISIBLE
            buttonTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            imageViewParams.setMargins(
                    16.dpToPx(context),
                    16.dpToPx(context),
                    16.dpToPx(context),
                    16.dpToPx(context)
            )
            imageViewParams.marginStart = 16.dpToPx(context)
            imageViewParams.marginEnd = 16.dpToPx(context)
            buttonTextView.visibility = View.GONE
            buttonTextView.setTextColor(ContextCompat.getColor(context, android.R.color.transparent))
        }
        buttonImageView.layoutParams = imageViewParams
    }

    private fun manageIcon() {
        buttonImageView.setImageDrawable(ContextCompat.getDrawable(context, buttonIcon))
    }

    private fun manageText() {
        buttonTextView.text = text
    }

}