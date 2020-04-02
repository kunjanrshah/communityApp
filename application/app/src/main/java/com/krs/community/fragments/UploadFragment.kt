package com.krs.community.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.github.squti.guru.Guru
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.adapter.UploadDialogAdapter
import com.krs.community.app.AppController
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.listeners.ByDocumentListener
import com.krs.community.model.Member
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.responses.UploadedFile
import com.krs.community.responses.UploadedFilesResponse
import com.krs.community.utils.*
import com.krs.community.utils.AppConstants.UPLOAD_DOCUMENT
import com.krs.community.viewmodel.DocumentsListModel
import com.krs.community.viewmodelfactory.DocumentListViewModelFactory
import com.orhanobut.dialogplus.DialogPlus

import lumenghz.com.pullrefresh.PullToRefreshView
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class UploadFragment : Fragment(), KodeinAware, ByDocumentListener, UploadDialogAdapter.UploadFileListner {
    override val kodein by kodein()

    private lateinit var btnupload: MovableFloatingActionButton
    private lateinit var rvDocuments: RecyclerView
    private lateinit var documentsListModel: DocumentsListModel
    private val documentListViewModelFactory: DocumentListViewModelFactory by instance()
    private val listUpload = ArrayList<UploadedFile>()
    private lateinit var adapter: ParallaxRecyclerAdapter<UploadedFile>
    private var uploadDialog: DialogPlus? = null
    private var uploadedFileName = ""
    private lateinit var tvCount: TextView
    private lateinit var ivNotFound: ImageView
    private lateinit var pullToRefreshView: PullToRefreshView

    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_documents, container, false)

        documentsListModel = ViewModelProvider(this, documentListViewModelFactory).get(DocumentsListModel::class.java)
        documentsListModel.byDocumentListener = this
        btnupload = root.findViewById<View>(R.id.btnupload) as MovableFloatingActionButton
        pullToRefreshView = root.findViewById(R.id.pull_to_refresh)
        rvDocuments = root.findViewById(R.id.rv_documents)
        ivNotFound = root.findViewById(R.id.iv_not_found)
        val loginuser = Guru.getString(getString(R.string.loginMember), "")
        val loginMember = Gson().fromJson<Member>(loginuser, Member::class.java)
        if (loginMember?.role.equals(getString(R.string.User))) {
            btnupload.visibility = View.GONE
        } else {
            btnupload.visibility = View.VISIBLE
        }
        btnupload.setOnClickListener {

            val adapter = UploadDialogAdapter(context)
            adapter.setListener(this@UploadFragment)
            uploadDialog = DialogPlus.newDialog(context)
                    .setAdapter(adapter)
                    .setGravity(Gravity.BOTTOM)
                    .setCancelable(true)
                    .setOnCancelListener {
                        it.dismiss()
                    }
                    .setExpanded(true, 900)
                    .setContentBackgroundResource(R.drawable.popup_top_corner)
                    .create()
            uploadDialog?.show()
        }
        pullToRefreshView.setOnRefreshListener {

            pullToRefreshView.postDelayed(Runnable {
                pullToRefreshView.setRefreshing(false)
            }, 5000)

            Coroutines.io {
                Coroutines.main {
                    Utility.startSweetProgress(context!!, getString(R.string.app_name), getString(R.string.fetchinguploadedfiles))
                }
                val jsonObject = JSONObject()
                jsonObject.put(getString(R.string.id), Guru.getString(getString(R.string.user_id), ""))

                val updated = JsonParser().parse(jsonObject.toString()) as JsonObject
                documentsListModel.getUploadedFiles(updated)
            }
        }
        adapter = object : ParallaxRecyclerAdapter<UploadedFile>(listUpload as MutableList<UploadedFile>?) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder?, adapter: ParallaxRecyclerAdapter<UploadedFile>?, i: Int) {
                val uploadfile = listUpload[i]
                val holder = viewHolder as MyViewHolder
                holder.tvFile.text = uploadfile.name
                holder.tvName.text = uploadfile.first_name + " " + uploadfile.last_name
                holder.tvDate.text = Utility.changeDateFormat(uploadfile.createdAt, Utility.yyyy_MM_dd_TIME, Utility.dd_MM_yyyy_TIME)
                holder.llDownload.setOnClickListener {
                    if (Utility.checkExternalStoragePermission(activity)) {
                        PRDownloader.download(uploadfile.fileUrl, Utility.getPath(), uploadfile.filename).build()
                                .setOnStartOrResumeListener {
                                    Utility.startSweetDialog(activity, SweetAlertDialog.PROGRESS_TYPE, getString(R.string.uploads), getString(R.string.download))
                                }
                                .start(object : OnDownloadListener {
                                    override fun onDownloadComplete() {
                                        Utility.startSweetDialog(activity, SweetAlertDialog.SUCCESS_TYPE, getString(R.string.uploads), getString(R.string.filedownloded) + AppController.mApplication.getString(R.string.app_name) + getString(R.string.folder))
                                    }

                                    override fun onError(error: Error?) {
                                        Utility.startSweetDialog(activity, SweetAlertDialog.ERROR_TYPE, getString(R.string.uploads), getString(R.string.somethingwrong))
                                        Log.d("PRDownloader", "onError: $error")
                                    }
                                })
                    } else {
                        Utility.requestStoragePermission(activity as AppCompatActivity)
                    }
                    rvDocuments.snackbar(getString(R.string.coming_soon), Snackbar.LENGTH_SHORT)
                }
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup?, adapter: ParallaxRecyclerAdapter<UploadedFile>?, i: Int): RecyclerView.ViewHolder {
                return MyViewHolder(LayoutInflater.from(viewGroup?.context).inflate(R.layout.list_row_uploaded, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<UploadedFile>?): Int {
                return listUpload.size
            }
        }

        rvDocuments.layoutManager = LinearLayoutManager(activity!!)
        rvDocuments.setHasFixedSize(true)

        val header = LayoutInflater.from(activity).inflate(R.layout.header_nonactives, container, false)
        val tvTitle = header.findViewById<TextView>(R.id.tv_title)
        tvTitle.text = getString(R.string.uploads)
        tvCount = header.findViewById(R.id.tv_count)

        val ivCancel = header.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel.setOnClickListener { v: View? -> Utility.backNavigation(activity) }
        adapter.setParallaxHeader(header, rvDocuments)
        rvDocuments.adapter = adapter
        Coroutines.io {
            Coroutines.main {
                Utility.startSweetProgress(context!!, getString(R.string.app_name), getString(R.string.fetchingfiles))
            }
            val jsonObject = JSONObject()
            jsonObject.put(getString(R.string.id), Guru.getString(getString(R.string.user_id), ""))

            val updated = JsonParser().parse(jsonObject.toString()) as JsonObject
            documentsListModel.getUploadedFiles(updated)
        }

        if (!Utility.checkExternalStoragePermission(activity)) {
            Utility.requestStoragePermission(activity as AppCompatActivity)
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun upload(name: String) {
        uploadedFileName = name
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        startActivityForResult(intent, Utility.PICK_GALLERY_REQUEST)
    }

    override fun cancelDialog() {
        uploadDialog?.dismiss()
        Utility.hideKeyboard(activity)
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

        SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.uploadfile))
                .setContentText(getString(R.string.doyouupload) + "$uploadedFileName?")
                .setConfirmText(getString(R.string.uploadnow))
                .setConfirmClickListener {
                    it.dismissWithAnimation()
                    if (isNetworkConnected(activity as AppCompatActivity)) {
                        MultipartUploadRequest(activity!!, serverUrl = UPLOAD_DOCUMENT)
                                .setMethod("POST")
                                .addFileToUpload(
                                        filePath = filePath,
                                        parameterName = "uploaded_file"
                                )
                                .addParameter(getString(R.string.filename), uploadedFileName)
                                .addParameter(getString(R.string.id), Guru.getString(getString(R.string.member_id), "").toString())
                                .addHeader(getString(R.string.apikey), AppConstants.API_KEY_VALUE)
                                .startUpload()
                        rvDocuments.snackbar(getString(R.string.uploadingstart), Snackbar.LENGTH_INDEFINITE)
                    }
                }
                .setCancelText("Later")
                .setCancelClickListener {
                    it.dismissWithAnimation()
                }
                .show()
    }

    override fun getDocuments(response: UploadedFilesResponse) {
        pullToRefreshView.setRefreshing(false)
        Utility.hideSweetProgress()
        if (response.success) {
            listUpload.clear()
            tvCount.text = getString(R.string.totalrecord) + response.data.size
            listUpload.addAll(response.data)
            adapter.notifyDataSetChanged()
            ivNotFound.visibility = View.GONE
        } else {
            Coroutines.main {
                ivNotFound.visibility = View.VISIBLE
            }
        }
    }

    override suspend fun getFailure(message: String) {
        Coroutines.main {
            ivNotFound.visibility = View.VISIBLE
            Utility.hideSweetProgress()
        }
    }

    inner class MyViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var llDownload: LinearLayout = view.findViewById(R.id.ll_download)
        var tvFile: TextView = view.findViewById(R.id.tv_file)
        var tvDate: TextView = view.findViewById(R.id.tv_date)
        var tvName: TextView = view.findViewById(R.id.tv_name)
    }
}