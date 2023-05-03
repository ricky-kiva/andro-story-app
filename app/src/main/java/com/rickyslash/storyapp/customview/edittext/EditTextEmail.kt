package com.rickyslash.storyapp.customview.edittext

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.R as res
import com.rickyslash.storyapp.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EditTextEmail @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = res.attr.editTextStyle
): TextInputEditText(context, attrs, defStyleAttr) {

    private val emailRegex = Regex("^[\\w+_.-]+@(?:[a-z\\d-]+\\.)+[a-z]{2,}\$")

    init {
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (!isValidEmail(s.toString())) {
                    (parent.parent as? TextInputLayout)?.apply {
                        error = context.getString(R.string.wrong_format_email)
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

    private fun isValidEmail(email: String): Boolean {
        return emailRegex.matches(email)
    }
}