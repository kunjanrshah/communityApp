package com.krs.community.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.utils.AESUtils

class ScanQRCodeActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner
    private val RECORD_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qrcode)
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        setupPermissions()
        codeScanner = CodeScanner(this, scannerView)
        setScreenLayout()

        val mApp = applicationContext as AppController
        mApp.firebaseAnalytics(this@ScanQRCodeActivity, "ScanQRCode Activity")
        mApp.facebookAnalytics(this@ScanQRCodeActivity, "ScanQRCode Activity")

    }

    private fun setScreenLayout() {

        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
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
                    finish()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
        codeScanner.startPreview()
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


}