package com.krs.community.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.krs.community.R
import com.krs.community.app.SearchLiveo
import com.krs.community.databinding.ActivityFavoriteBinding

class FavoriteProfileActivity : BaseActivity() , SearchLiveo.OnSearchListener {

    lateinit var mBinding:ActivityFavoriteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        onInitView()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
    }


    override fun changedSearch(text: CharSequence?) {

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_favorite, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            mBinding.searchLiveo.show()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            if (requestCode == SearchLiveo.REQUEST_CODE_SPEECH_INPUT) {
                mBinding.searchLiveo.resultVoice(requestCode, resultCode, data)
            }
        }
    }

    private fun onInitView(){
        mBinding=  DataBindingUtil.setContentView(this,R.layout.activity_favorite)
        this.onInitToolbar(mBinding.toolbar,resources.getString(R.string.app_name))

        mBinding.searchLiveo.with(this).removeMinToSearch().removeSearchDelay().build()

        if (mBinding.includeFavorite != null) {
            mBinding.includeFavorite.recyclerView.setHasFixedSize(true)
            mBinding.includeFavorite.recyclerView.layoutManager = LinearLayoutManager(this)
            mBinding.includeFavorite.swipeContainer.isEnabled = false
            mBinding.includeFavorite.swipeContainer.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimary, R.color.colorAccent)
        }
    }

    private fun onInitToolbar(toolBar: Toolbar?, title: String?, icon: Int=-1, displayHome: Boolean=false) {
        if (toolBar != null) {
            setSupportActionBar(toolBar)
            val actionBar: ActionBar? = supportActionBar
            if (actionBar != null) {
                actionBar.setTitle(title)
                actionBar.setDisplayShowHomeEnabled(displayHome)
                actionBar.setDisplayHomeAsUpEnabled(displayHome)
                if (icon != -1 && displayHome) {
                    toolBar.setNavigationIcon(ContextCompat.getDrawable(this, icon))
                }
            }
        }
    }
}