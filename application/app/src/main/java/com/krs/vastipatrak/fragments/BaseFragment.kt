package com.krs.vastipatrak.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.krs.vastipatrak.R
import com.krs.vastipatrak.utils.getStatusBarHeight

/**
 * Created by Alexander Kolpakov on 26.07.2018
 */
open class BaseFragment() : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupToolbarMargin(view)
    }

    private fun setupToolbarMargin(view: View) {
        val myAppBar = view.findViewById<View?>(R.id.myAppBar)
        myAppBar?.let {
            val statusBarHeight = context?.getStatusBarHeight()
            val lp = it.layoutParams as ViewGroup.LayoutParams
            lp.height += statusBarHeight as Int
            if (it is CardView) {
                it.setContentPadding(0, statusBarHeight, 0, 0)
            } else {
                it.updatePadding(top = statusBarHeight)
            }
        }
    }
}