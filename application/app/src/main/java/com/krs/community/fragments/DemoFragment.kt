package com.krs.community.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.listeners.ILoginListener
import com.krs.community.model.LoginResponse
import com.krs.community.utils.Coroutines
import com.krs.community.viewmodel.DemoViewModel
import com.krs.community.viewmodel.SmartFilterViewModel
import com.krs.community.viewmodelfactory.DemoViewModelFactory
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class DemoFragment: Fragment() , KodeinAware, ILoginListener {
    override fun userLogin(response: LoginResponse) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getFailure(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override val kodein by kodein()
    private lateinit var demoViewModel:DemoViewModel
    private val filterViewModelFactory: DemoViewModelFactory by instance()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)


        demoViewModel = ViewModelProviders.of(this,filterViewModelFactory).get(DemoViewModel::class.java)
        demoViewModel.mStatisticsListener=this

        val json=JSONObject()
        json.put("","")
        val updated=  JsonParser().parse(json.toString()) as JsonObject
      //  demoViewModel.getLoginAPI(updated)


       Coroutines.main {
         //val name=  demoViewModel.getRelationName(id)
       }


    }
}