package com.ddenfi.mystoryapp.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText

class MyEmailEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Do notihing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, end: Int, count: Int) {
                fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
                if ((start > 4) && s.toString().isNotEmpty()) {
                        error = if (s.isValidEmail()) null else "Invalid Email"
                    }
            }

            override fun afterTextChanged(p0: Editable?) {
                //Do nothing
            }

        })
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        hint = "someone@example.com"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }


}