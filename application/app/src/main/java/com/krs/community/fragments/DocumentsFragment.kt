package com.krs.community.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.listeners.ByDocumentListener
import com.krs.community.repositories.DocumentListRepository
import com.krs.community.responses.DocumentListResponse
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.DocumentsListModel
import com.krs.community.viewmodel.SmartFilterViewModel
import com.krs.community.viewmodelfactory.DocumentListViewModelFactory
import com.krs.community.viewmodelfactory.SmartFilterViewModelFactory
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest
import okhttp3.internal.Internal.instance
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class DocumentsFragment() : Fragment(), KodeinAware, ByDocumentListener {
    override val kodein by kodein()
    var btnupload: Button? = null

    private lateinit var rvDocuments: RecyclerView
    private lateinit var documentsListModel: DocumentsListModel
    private val documentListViewModelFactory: DocumentListViewModelFactory by instance()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_documents, container, false)

        val mApp = (activity as AppCompatActivity).applicationContext as AppController
        mApp.FirebaseAnalytics(context, DocumentsFragment::class.simpleName)

        documentsListModel = ViewModelProvider(this, documentListViewModelFactory).get(DocumentsListModel::class.java)

        documentsListModel.mByFilterListener = this

        btnupload = root.findViewById<View>(R.id.btnupload) as Button
        rvDocuments = root.findViewById(R.id.rv_documents)
        rvDocuments.layoutManager = LinearLayoutManager(activity)
        rvDocuments.setHasFixedSize(true)

        btnupload!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }
            startActivityForResult(intent, Utility.PICK_GALLERY_REQUEST)
        }


        documentsListModel.getInActiveRecords()


        return root
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Utility.PICK_GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            data.let {
                onFilePicked(it?.data.toString())
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun onFilePicked(filePath: String) {
        MultipartUploadRequest(activity!!, serverUrl = "https://www.muslimghanchisamaj.in/API/UploadFiles")
                .setMethod("POST")
                .addFileToUpload(
                        filePath = filePath,
                        parameterName = "uploaded_file"
                )
                .addParameter("filename","bhai have thai ja")
                .addHeader("Apikey", "q1fgdfggfw2e2rt3y5u6i8iug12fh123yhhddaf")
                .startUpload()
    }

    override fun getMembers(response: DocumentListResponse) {

        Log.e("",""+response.message)
    }

    override suspend fun getFailure(message: String) {
        Log.e("",""+message)

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}