package com.rickyslash.storyapp.customview.edittext

import android.content.Context
import android.graphics.Typeface
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EditTextPassword @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.editTextStyle
): TextInputEditText(context, attrs, defStyleAttr) {

    private val passwordRegex = Regex("^.{8,}$")

    init {
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        typeface = Typeface.DEFAULT
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (!isValidPassword(s.toString())) {
                    (parent.parent as? TextInputLayout)?.apply {
                        error = "Password must be at least 8 characters long."
                        isErrorEnabled = true
                        errorIconDrawable = null
                    }
                } else {
                    (parent.parent as? TextInputLayout)?.apply {
                        error = null
                        isErrorEnabled = false
                    }

                }
            }
        })
    }

    private fun isValidPassword(password: String): Boolean {
        return passwordRegex.matches(password)
    }

}