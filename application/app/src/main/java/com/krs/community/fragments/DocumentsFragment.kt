package com.krs.community.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.krs.community.R
import com.krs.community.listeners.ByDocumentListener
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.responses.UploadedFile
import com.krs.community.responses.UploadedFilesResponse
import com.krs.community.utils.AppConstants
import com.krs.community.utils.AppConstants.UPLOAD_DOCUMENT
import com.krs.community.utils.MovableFloatingActionButton
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.DocumentsListModel
import com.krs.community.viewmodelfactory.DocumentListViewModelFactory
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class DocumentsFragment() : Fragment(), KodeinAware, ByDocumentListener {
    override val kodein by kodein()

    private lateinit var btnupload: MovableFloatingActionButton
    private lateinit var rvDocuments: RecyclerView
    private lateinit var documentsListModel: DocumentsListModel
    private val documentListViewModelFactory: DocumentListViewModelFactory by instance()
    private val listUpload = ArrayList<UploadedFile>()
    private lateinit var adapter: ParallaxRecyclerAdapter<UploadedFile>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_documents, container, false)

        documentsListModel = ViewModelProvider(this, documentListViewModelFactory).get(DocumentsListModel::class.java)
        documentsListModel.byDocumentListener = this

        btnupload = root.findViewById<View>(R.id.btnupload) as MovableFloatingActionButton
        rvDocuments = root.findViewById(R.id.rv_documents)
        rvDocuments.layoutManager = LinearLayoutManager(activity)
        rvDocuments.setHasFixedSize(true)

        btnupload.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }
            startActivityForResult(intent, Utility.PICK_GALLERY_REQUEST)
        }

        documentsListModel.getUploadedFiles()

        adapter = object : ParallaxRecyclerAdapter<UploadedFile>(listUpload) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder?, adapter: ParallaxRecyclerAdapter<UploadedFile>?, i: Int) {
                val uploadfile = listUpload[i]


            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup?, adapter: ParallaxRecyclerAdapter<UploadedFile>?, i: Int): RecyclerView.ViewHolder {
                return MyViewHolder(LayoutInflater.from(viewGroup?.context).inflate(R.layout.list_row_uploaded, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<UploadedFile>?): Int {
                return listUpload.size
            }
        }

        return root
    }

    inner class MyViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var llDownload: LinearLayout = view.findViewById(R.id.ll_download)
        var tvFile: LinearLayout = view.findViewById(R.id.tv_file)
        var tvDate: LinearLayout = view.findViewById(R.id.tv_date)


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
        MultipartUploadRequest(activity!!, serverUrl = UPLOAD_DOCUMENT)
                .setMethod("POST")
                .addFileToUpload(
                        filePath = filePath,
                        parameterName = getString(R.string.uploaded_file)
                )
                .addParameter(getString(R.string.filename), "file name")
                .addHeader(getString(R.string.apikey), AppConstants.API_KEY_VALUE)
                .startUpload()
    }

    override fun getMembers(response: UploadedFilesResponse) {
        if (response.success) {
            listUpload.clear()
            listUpload.addAll(response.data)

        }
    }

    override suspend fun getFailure(message: String) {
    }

}