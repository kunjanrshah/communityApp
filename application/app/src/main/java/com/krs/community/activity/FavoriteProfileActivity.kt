package com.krs.community.activity

import android.os.Bundle
import android.view.WindowManager
import com.krs.community.R

class FavoriteProfileActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        setContentView(R.layout.activity_favorite)

    }


}