package com.krs.community.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.InputType
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cn.pedant.SweetAlert.SweetAlertDialog
import com.krs.community.R
import com.krs.community.databinding.ActivityRegisterBinding
import com.krs.community.model.RBStates
import com.krs.community.utils.CountryData
import com.krs.community.utils.Logger
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.RegisterViewModel
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCrop.*
import com.yalantis.ucrop.UCropFragment
import com.yalantis.ucrop.UCropFragmentCallback
import com.yalantis.ucrop.model.AspectRatio
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Callback
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import java.io.File
import java.io.IOException

class RegisterActivty : BaseActivity(), UCropFragmentCallback {

    private var str_profile_hash = ""
    private var isShow = true
    private var isShow1 = true
    //private var add_new: String? = ""
    private var mShowLoader: Boolean = false
    private val PICK_GALLERY_REQUEST = 1
    private lateinit var logger: Logger

    private lateinit var registerViewModel: RegisterViewModel

    companion object {
        private val SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage"
        private val TAG = RegisterActivty::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger = Logger(TAG)
        registerViewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)
        registerViewModel.init()

        val binding:ActivityRegisterBinding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        binding.lifecycleOwner = this
        binding.registerviewmodel = registerViewModel


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(this, R.color.colorBG, false)
        }

        /*val mBundle = intent.extras
        if (mBundle != null) {
            add_new = mBundle.getString(AppConstants.SCREEN)
        }*/

        Memory_Allocation()

        btn_register?.setOnClickListener { registerViewModel.onRegisterButtonClick(this) }

        txt_already?.setOnClickListener { registerViewModel.onTextAlreadyClicked(this) }

        txt_how_register.setOnClickListener { registerViewModel.onHowRegisterClicked(this) }

        img_cancel.setOnClickListener {
            img_profile.setImageResource(R.drawable.man_reg)
            img_cancel.visibility = View.GONE
            val icon = BitmapFactory.decodeResource(resources, R.drawable.man_reg)
            if (icon != null) {
                str_profile_hash = Utility.getBase64(icon)
            }
        }

        edt_password.setOnTouchListener(fun(_: View, event: MotionEvent): Boolean {
            val DRAWABLE_RIGHT = 2

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= edt_password!!.right - edt_password!!.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                    if (isShow) {
                        edt_password!!.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.pwd_show, 0)
                        edt_password!!.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

                        isShow = false
                    } else {
                        edt_password!!.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.pwd_hide, 0)
                        edt_password!!.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD

                        isShow = true
                    }
                    edt_password!!.setSelection(edt_password!!.length())

                    return true
                }
            }
            return false
        })

        edt_cpassword.setOnTouchListener(fun(v: View, event: MotionEvent): Boolean {

            val DRAWABLE_RIGHT = 2

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= edt_cpassword!!.right - edt_cpassword!!.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                    if (isShow1) {
                        edt_cpassword!!.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.pwd_show, 0)
                        edt_cpassword!!.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        isShow1 = false
                    } else {
                        edt_cpassword!!.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.pwd_hide, 0)
                        edt_cpassword!!.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                        isShow1 = true
                    }
                    try {
                        edt_cpassword!!.setSelection(edt_cpassword!!.length())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    return true
                }
            }
            return false
        })

        val arrayAdapter = ArrayAdapter(this, R.layout.my_spinner_style, CountryData.countryNames)
        spinnerCountries.adapter = arrayAdapter

        registerViewModel.getUserStates().observe(this, Observer {
            if (it.success) {
                val lstState = Array<String?>(it.data.size) { null }
                for ((index, stateData) in it.data.withIndex()) {
                    lstState[index] = stateData.state
                }
                spinnerStates.setItems(lstState)
                spinnerStates.setExpandTint(R.color.black)
            }
        })

        spinnerStates.setOnItemClickListener {
            Utility.startProgress(this)
            registerViewModel.fetchCitiesForStateId(it + 1)
        }

        registerViewModel.lstCities.observe(this, Observer {
            if (it == null) {
                return@Observer
            }
            if (it.success) {
                val lstCity = Array<String?>(it.data.size) { null }
                for ((index, cityData) in it.data.withIndex()) {
                    lstCity[index] = cityData.city
                }
                spinnerCities.clear()
                spinnerCities.setTitle("Select ${spinnerStates.text}'s City")
                spinnerCities.setItems(lstCity)
                spinnerCities.setExpandTint(R.color.black)
            }
            if(Utility.dialog!=null && Utility.dialog.isShowing) {
                Utility.dialog.dismissWithAnimation()
            }

        })

       registerViewModel.getLstSubCommunity().observe(this, Observer {
            if (it.success) {
                val lstSubCom = Array<String?>(it.data.size) { null }

                for ((index, subData) in it.data.withIndex()) {
                    lstSubCom[index] = subData.name
                }
                spinnerSub.setItems(lstSubCom)
            }
        })

        spinnerSub.setOnItemClickListener {
            Utility.startProgress(this)
            registerViewModel.getLstLocalCommunity(it + 1)
        }

        registerViewModel.lstLocalComm.observe(this, Observer {
            if (it == null) {
                return@Observer
            }
            if (it.success) {
                val lstLocal = Array<String?>(it.data.size) { null }
                for ((index, LocalData) in it.data.withIndex()) {
                    lstLocal[index] = LocalData.name
                }
                spinnerLocal.clear()
                spinnerLocal.setTitle("Select ${spinnerSub.text}'s Local Community")
                spinnerLocal.setItems(lstLocal)
                spinnerLocal.setExpandTint(R.color.black)
            }
            if(Utility.dialog!=null && Utility.dialog.isShowing) {
                Utility.dialog.dismissWithAnimation()
            }
        })


        registerViewModel.getUserLastName().observe(this, Observer {
            if (it.success) {
                val lstLastname = Array<String?>(it.data.size) { null }
                for ((index, stateData) in it.data.withIndex()) {
                    lstLastname[index] = stateData.name
                }
                spinnerLname.setItems(lstLastname)
                spinnerLname.setExpandTint(R.color.black)
            }
        })

        val registerPrompt = MaterialTapTargetPrompt.Builder(this@RegisterActivty)
                .setTarget(R.id.btn_register)
                .setAutoFinish(false)
                .setAutoDismiss(true)
                .setBackButtonDismissEnabled(false)
                .setBackgroundColour(resources.getColor(R.color.colorPrimary))
                .setPrimaryText(getString(R.string.register_new_family))
                .setSecondaryText(getString(R.string.click_on_register))
                .setPromptStateChangeListener { prompt, state ->
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                        prompt.dismiss()
                    }
                }.create()

        val photoPrompt = MaterialTapTargetPrompt.Builder(this@RegisterActivty)
                .setTarget(R.id.img_profile)
                .setAutoFinish(false)
                .setAutoDismiss(false)
                .setBackButtonDismissEnabled(false)
                .setBackgroundColour(resources.getColor(R.color.colorPrimary))
                .setPrimaryText(getString(R.string.upload_photo))
                .setSecondaryText(getString(R.string.select_photo))
                .setPromptStateChangeListener { prompt, state ->
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                        prompt.dismiss()
                        assert(registerPrompt != null)
                        registerPrompt!!.show()
                    }
                }
                .show()

        img_profile.setOnClickListener { v ->
            if (photoPrompt!!.state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                pickFromGallery()
            }
        }


    }


    private fun pickFromGallery() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= 23) {
                prompt_read_permission()
            }
        } else {
            val intent = Intent(Intent.ACTION_GET_CONTENT).setType("image/*").addCategory(Intent.CATEGORY_OPENABLE)
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_GALLERY_REQUEST)
        }
    }


    private fun prompt_read_permission() {
        if (!Utility.hasPermission(this, "READ_EXTERNAL_STORAGE")) {
            SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText("Storage read Permission")
                    .setContentText("Permission is needed to pick image from gallery for your Profile")
                    .setConfirmText("Yes, please!")
                    .setCancelText("No!")
                    .showCancelButton(true)
                    .setConfirmClickListener { sDialog ->
                        sDialog.dismiss()
                        ActivityCompat.requestPermissions(this@RegisterActivty, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), BaseActivity.REQUEST_STORAGE_READ_ACCESS_PERMISSION)
                        //requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, "Storage read permission is needed to pick files.", REQUEST_STORAGE_READ_ACCESS_PERMISSION);
                    }
                    .show()
        }
    }


    private fun Memory_Allocation() {

        val str = resources.getString(R.string.already_have_a_account_sign_in) + "<b>" + " " + getString(R.string.login) + "</b>"
        txt_already?.text = Html.fromHtml(str)
    }

    /*private fun cropImageActivity() {
        if (Utility.hasPermission(this@RegisterActivty, Manifest.permission.WRITE_EXTERNAL_STORAGE) && Utility.hasPermission(this@RegisterActivty, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            CropImage.startPickImageActivity(this@RegisterActivty)
        }
    }*/

    /* private fun startCropImageActivity(imageUri: Uri) {
         CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).setMultiTouchEnabled(true).start(this@RegisterActivty)
     }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_GALLERY_REQUEST) {
                val selectedUri = data!!.data
                if (selectedUri != null) {
                    startCrop(selectedUri)
                } else {
                    Toast.makeText(this@RegisterActivty, "Cannot retrieve selected image", Toast.LENGTH_SHORT).show()
                }
            } else if (requestCode == REQUEST_CROP) {
                handleCropResult(data!!)
            }
        }
        if (resultCode == RESULT_ERROR) {
            handleCropError(data!!)
        }

    }

    private fun handleCropResult(result: Intent) {
        val resultUri = getOutput(result)
        if (resultUri != null) {

            logger.debug("resultUri: $resultUri")

            try {

                val f = File(resultUri.path.toString())

                runOnUiThread {
                    var bmp1: Bitmap? = null
                    try {
                        bmp1 = Utility.getBitmap(this, f)
                        str_profile_hash = Utility.getBase64(bmp1!!)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                //Bitmap bmp= decodeFile(f);
                //runOnUiThread(() -> str_profile_hash = Utility.getBase64(bmp));

                img_cancel!!.visibility = View.VISIBLE
                img_profile!!.setImageURI(resultUri)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            Toast.makeText(this@RegisterActivty, "Cannot retrieve cropped image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleCropError(result: Intent) {
        val cropError = getError(result)
        if (cropError != null) {
            logger.error(cropError)
            Toast.makeText(this@RegisterActivty, cropError.message, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this@RegisterActivty, "Unexpected error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCrop(uri: Uri) {
        val destinationFileName = "$SAMPLE_CROPPED_IMAGE_NAME.jpg"
        var uCrop = of(uri, Uri.fromFile(File(cacheDir, destinationFileName)))
        uCrop = advancedConfig(uCrop)
        uCrop.start(this@RegisterActivty)
    }

    private fun advancedConfig(uCrop: UCrop): UCrop {
        val options = Options()
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG)

        options.setCompressionQuality(100)

        options.setHideBottomControls(false)
        options.setFreeStyleCropEnabled(true)

        options.setBrightnessEnabled(true)
        options.setContrastEnabled(true)
        options.setSaturationEnabled(true)
        options.setSharpnessEnabled(true)

        options.setImageToCropBoundsAnimDuration(666)
        //  options.setDimmedLayerColor(getResources().getColor(R.color.colorPrimary));
        //options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.white))
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.colorPrimary))
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.colorPrimary))
        //options.setRootViewBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

        // Aspect ratio options
        options.setAspectRatioOptions(1,
                AspectRatio("WOW", 1f, 2f),
                AspectRatio("MUCH", 3f, 4f),
                AspectRatio("RATIO", 0f, 0f),
                AspectRatio("SO", 16f, 9f),
                AspectRatio("ASPECT", 1f, 1f))

        return uCrop.withOptions(options)
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            BaseActivity.REQUEST_STORAGE_READ_ACCESS_PERMISSION ->

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery()
                } else if (!shouldShowRequestPermissionRationale(permissions[0])) {
                    displayNeverAskAgainDialog()
                } else {
                    prompt_read_permission()
                }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    private fun displayNeverAskAgainDialog() {

        SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Storage read Permission")
                .setContentText("Permission is needed to pick image from gallery for your Profile. Please permit the permission through " + "Settings screen.\n\nSelect Permissions -> Enable permission")
                .setConfirmText("Permit Manually")
                .setCancelText("Cancel")
                .showCancelButton(true)
                .setConfirmClickListener { sDialog ->
                    sDialog.dismiss()
                    val intent = Intent()
                    intent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
                .show()
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
      //  menu.findItem(R.id.menu_crop).isVisible = !mShowLoader
       // menu.findItem(R.id.menu_loader).isVisible = mShowLoader
        return super.onPrepareOptionsMenu(menu)
    }


    override fun loadingProgress(showLoader: Boolean) {
        mShowLoader = showLoader
        //  supportInvalidateOptionsMenu()
    }

    override fun onCropFinish(result: UCropFragment.UCropResult) {
        when (result.mResultCode) {
            RESULT_OK -> handleCropResult(result.mResultData)
            RESULT_ERROR -> handleCropError(result.mResultData)
        }
    }
}

private operator fun AdapterView.OnItemSelectedListener.invoke(callback: Callback<RBStates>) {


}
