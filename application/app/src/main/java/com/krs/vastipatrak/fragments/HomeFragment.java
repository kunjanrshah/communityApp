package com.krs.vastipatrak.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.krs.vastipatrak.R;

import java.io.File;

/**
 * Created by TecStub on 22-Sep-16.
 */
public class HomeFragment extends Activity {

    WebView wv_home;
    String wv_url = "";
    private static final int FILECHOOSER_RESULTCODE   = 2888;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);

        //View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        wv_home = (WebView) findViewById(R.id.wv_home);
        wv_home.getSettings().setJavaScriptEnabled(true);
        // Other webview options
        wv_home.getSettings().setLoadWithOverviewMode(true);
        wv_home.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        wv_home.setScrollbarFadingEnabled(false);
        wv_home.getSettings().setBuiltInZoomControls(true);
        wv_home.getSettings().setPluginState(WebSettings.PluginState.ON);
        wv_home.getSettings().setAllowFileAccess(true);
        wv_home.getSettings().setSupportZoom(true);
        wv_home.getSettings().setLoadWithOverviewMode(true);
        //wv_url = "http://www.superbinstruments.com/WEB/photoes.php?username=admin";
        wv_url = "http://www.androidexample.com/media/webview/details.html";

        wv_home.loadUrl(wv_url);



        startWebView();
    }

/*    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        wv_home = (WebView) rootView.findViewById(R.id.wv_home);
        wv_home.getSettings().setJavaScriptEnabled(true);
        // Other webview options
        wv_home.getSettings().setLoadWithOverviewMode(true);
        wv_home.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        wv_home.setScrollbarFadingEnabled(false);
        wv_home.getSettings().setBuiltInZoomControls(true);
        wv_home.getSettings().setPluginState(WebSettings.PluginState.ON);
        wv_home.getSettings().setAllowFileAccess(true);
        wv_home.getSettings().setSupportZoom(true);
        wv_home.getSettings().setLoadWithOverviewMode(true);
        wv_url = "http://www.superbinstruments.com/WEB/photoes.php?username=admin";
        wv_home.loadUrl(wv_url);



        startWebView();

        return rootView;
    }*/




    private void startWebView() {

        //Create new webview Client to show progress dialog
        //Called When opening a url or click on link

        wv_home.setWebViewClient(new WebViewClient() {
            ProgressDialog progressDialog;

            //If you will not use this method url links are open in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                // Check if Url contains ExternalLinks string in url
                // then open url in new browser
                // else all webview links will open in webview browser
                if(url.contains("ExternalLinks")){

                    // Could be cleverer and use a regex
                    //Open links in new browser
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                    // Here we can open new activity

                    return true;

                } else {

                    // Stay within this webview and load url
                    view.loadUrl(url);
                    return true;
                }

            }



            //Show loader on url load
            public void onLoadResource (WebView view, String url) {

                // if url contains string androidexample
                // Then show progress  Dialog
                if (progressDialog == null && url.contains("androidexample")
                        ) {

                    // in standard case YourActivity.this
                    progressDialog = new ProgressDialog(HomeFragment.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                }
            }

            // Called when all page resources loaded
            public void onPageFinished(WebView view, String url) {

                try{
                    // Close progressDialog
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                }catch(Exception exception){
                    exception.printStackTrace();
                }
            }

        });



        // implement WebChromeClient inner class
        // we will define openFileChooser for select file from camera
        wv_home.setWebChromeClient(new WebChromeClient() {

            // openFileChooser for Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType){
                /**updated, out of the IF **/
                mUploadMessage = uploadMsg;
                /**updated, out of the IF **/



                try{
                    File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AndroidExampleFolder");
                    if (!imageStorageDir.exists()) {
                        imageStorageDir.mkdirs();
                    }
                    File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                    mCapturedImageURI = Uri.fromFile(file); // save to the private variable

                    final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                    // captureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("image/*");

                    Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[] { captureIntent });

                    startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
                }
                catch(Exception e){
                    Toast.makeText(HomeFragment.this, "Camera Exception:"+e, Toast.LENGTH_LONG).show();
                }
                //}
            }

            // openFileChooser for Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg){
                openFileChooser(uploadMsg, "");
            }

            //openFileChooser for other Android versions
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg, acceptType);
            }



            /** Added code to clarify chooser. **/

            //The webPage has 2 filechoosers and will send a console message informing what action to perform, taking a photo or updating the file
            public boolean onConsoleMessage(ConsoleMessage cm) {
                onConsoleMessage(cm.message(), cm.lineNumber(), cm.sourceId());
                //Toast.makeText(getBaseContext(), cm.message()+" :message", Toast.LENGTH_LONG).show();
                return true;
            }
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                //Log.d("androidruntime", "Per c�nsola: " + message);
                //Toast.makeText(getBaseContext(), message+":message", Toast.LENGTH_LONG).show();
                //if(message.endsWith("foto")){ boolFileChooser= true; }
                //else if(message.endsWith("pujada")){ boolFileChooser= false; }
            }
            /** Added code to clarify chooser. **/

        });
    }

/*    private class MyWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }*/


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
  /*      if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) return;
            Uri result = intent == null || resultCode != getActivity().RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }*/

        if(requestCode==FILECHOOSER_RESULTCODE)
        {

            if (null == this.mUploadMessage) {
                return;
            }

            Uri result=null;

            try{
                if (resultCode != RESULT_OK) {

                    result = null;

                } else {

                    // retrieve from the private variable if the intent is null
                    result = intent == null ? mCapturedImageURI : intent.getData();
                }
            }
            catch(Exception e)
            {
                Toast.makeText(HomeFragment.this, "activity :"+e, Toast.LENGTH_LONG).show();
            }

            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        }

    }
}
