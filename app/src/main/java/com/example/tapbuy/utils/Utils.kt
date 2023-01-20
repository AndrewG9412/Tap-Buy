package com.example.tapbuy.utils

import android.text.Editable
import android.widget.EditText

class Utils {
    companion object{
        fun EditText.setEditableText(text:String){
            this.text = Editable.Factory.getInstance().newEditable(text)
        }
    }

}