package com.iammert.library.ui.multisearchviewlib.extensions

import android.animation.Animator
import android.animation.ValueAnimator
import com.iammert.library.ui.multisearchviewlib.helper.SimpleAnimationListener

fun ValueAnimator.endListener(onAnimationEnd: ()->Unit){
    addListener(object : SimpleAnimationListener(){
        override fun onAnimationRepeat(animation: Animator) {
            TODO("Not yet implemented")
        }

        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            onAnimationEnd.invoke()
        }

        override fun onAnimationCancel(animation: Animator) {
            TODO("Not yet implemented")
        }

        override fun onAnimationStart(animation: Animator) {
            TODO("Not yet implemented")
        }
    })
}