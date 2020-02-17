package com.krs.community.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import com.krs.community.R
import com.krs.community.utils.AESUtils
import com.wessam.library.NetworkChecker


@SuppressLint("ByteOrderMark")
class ScanQRCodeActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner
    private val RECORD_REQUEST_CODE = 101
    private var mNetworkReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mNetworkReceiver = NetworkChangeReceiver()

        registerNetworkBroadcastForNougat()

        if (NetworkChecker.isNetworkConnected(this)) {

            setScreenLayout()

        } else {
            setNoInternetLayout()
        }

    }

    private fun setNoInternetLayout() {

        setContentView(R.layout.no_internet_layout)
        //val binding = DataBindingUtil.setContentView<ActivityLoginwithBinding>(this@LoginActivity, R.layout.no_internet_layout)
        val retryButton: AppCompatButton = findViewById(R.id.retry_button)
        retryButton.setOnClickListener { v: View? -> setScreenLayout() }
    }

    private fun setScreenLayout() {
        if (NetworkChecker.isNetworkConnected(this)) {
            setContentView(R.layout.activity_scan_qrcode)
            val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)

            setupPermissions()

            codeScanner = CodeScanner(this, scannerView)

            // Parameters (default values)
            codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
            codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
            // ex. listOf(BarcodeFormat.QR_CODE)
            codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
            codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
            codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
            codeScanner.isFlashEnabled = false // Whether to enable flash or not

            // Callbacks
            codeScanner.decodeCallback = DecodeCallback {
                runOnUiThread {
                    try {
                        val decrypted = AESUtils.decrypt(it.text)
                        val mIntent = Intent(this, ProfileDetailActivity::class.java)
                        mIntent.putExtra(getString(R.string.scanId), decrypted)
                        startActivity(mIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
            codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
                runOnUiThread {
                    // Toast.makeText(this, "Camera initialization error: ${it.message}", Toast.LENGTH_LONG).show()
                }
            }

            scannerView.setOnClickListener {
                codeScanner.startPreview()
            }
        }
    }

    inner class NetworkChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                if (NetworkChecker.isNetworkConnected(context)) {
                    setScreenLayout()
                } else {
                    setNoInternetLayout()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver,  IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }
    }

    private fun unregisterNetworkBroadcastForNougat() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                unregisterReceiver(mNetworkReceiver)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                unregisterReceiver(mNetworkReceiver)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i("", "Permission has been denied by user")
                } else {
                    Log.i("", "Permission has been granted by user")

                }
            }
        }
    }
    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.e("", "Permission to record denied")
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                RECORD_REQUEST_CODE)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkBroadcastForNougat()

    }
}