package com.example.tapbuy.utils

import android.app.Activity
import android.os.Build
import android.text.Editable
import android.widget.EditText
import java.io.Serializable

class Utils {
    companion object{
        fun EditText.setEditableText(text:String){
            this.text = Editable.Factory.getInstance().newEditable(text)
        }

        fun <T : Serializable?> getSerializable(activity: Activity, name: String, clazz: Class<T>): T
        {
            return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                activity.intent.getSerializableExtra(name, clazz)!!
            else
                activity.intent.getSerializableExtra(name) as T
        }
    }

}