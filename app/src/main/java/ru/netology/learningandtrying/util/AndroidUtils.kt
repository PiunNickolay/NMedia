package ru.netology.learningandtrying.util

import android.content.Context
import android.view.View
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnWindowFocusChangeListener
import android.view.inputmethod.InputMethodManager

object AndroidUtils {
    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    fun View.focusAndShowKeyboard(){
        fun View.showTheKeyboardNow(){
            if (isFocused){
                post{
                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
                }
            }
        }
        requestFocus()
        if(hasWindowFocus()){
            showTheKeyboardNow()
        }else{
            viewTreeObserver.addOnWindowFocusChangeListener(
                object : ViewTreeObserver.OnWindowFocusChangeListener{
                    override fun onWindowFocusChanged(hasFocus: Boolean) {
                        if(hasFocus){
                            this@focusAndShowKeyboard.showTheKeyboardNow()
                            viewTreeObserver.removeOnWindowFocusChangeListener(this)
                        }
                    }
                })
        }
    }
}