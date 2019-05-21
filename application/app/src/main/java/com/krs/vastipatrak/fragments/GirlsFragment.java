package com.krs.vastipatrak.fragments;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.krs.vastipatrak.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GirlsFragment extends Fragment /*implements OnPageChangeListener, OnLoadCompleteListener*/ {
    public static final String SAMPLE_FILE = "girls.pdf";
    //PDFView pdfView;
    Integer pageNumber = 0;
    String pdfFileName;
    private String TAG = GirlsFragment.class.getName();
    private TextView tv_header;

    WebView myPersonalsite;
    String URL;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pdf, container, false);
        //pdfView = rootView.findViewById(R.id.pdfView);
        tv_header = rootView.findViewById(R.id.tv_header);
        /*TextView txt_marquee = rootView.findViewById(R.id.txt_marquee);
        txt_marquee.setSelected(true);*/
        //CopyReadAssets();
        performClick();
        //    displayFromAsset(SAMPLE_FILE);


        URL = "file:///android_asset/srinot/main.php";
        myPersonalsite = rootView.findViewById(R.id.myPersonalsite);

        myPersonalsite.getSettings().setJavaScriptEnabled(true);
        myPersonalsite.loadUrl(URL);

        return rootView;
    }


    private void CopyReadAssets() {
        AssetManager assetManager = getActivity().getAssets();

        InputStream in = null;
        OutputStream out = null;
        File file = new File(getActivity().getFilesDir(), "boys_part1.pdf");
        try {
            in = assetManager.open("boys_part1.pdf");
            out = getActivity().openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);

            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(
                Uri.parse("file://" + getActivity().getFilesDir() + "/boys_part1.pdf"),
                "application/pdf");

        startActivity(intent);
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    private File createFileFromInputStream(InputStream inputStream) {

        try {
            File tempFile = File.createTempFile("boys", ".pdf");
            //tempFile.deleteOnExit();
            //File f = new File("boys_part1.pdf");
            OutputStream outputStream = new FileOutputStream(tempFile);
            byte buffer[] = new byte[1024];
            int length = 0;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return tempFile;
        } catch (IOException e) {
            //Logging exception
        }

        return null;
    }

    private void performClick() {

        try {
            InputStream is = getResources().getAssets().open("boys_part1.pdf");
            File file = createFileFromInputStream(is);


            // File pdfFile = new File("//assets/boys_part1.pdf");
            try {
                if (file.exists()) {
                    Uri path = Uri.fromFile(file);
                    Intent objIntent = new Intent(Intent.ACTION_VIEW);
                    objIntent.setDataAndType(path, "application/pdf");
                    objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(objIntent);
                } else {
                    Toast.makeText(getActivity(), "File NotFound", Toast.LENGTH_SHORT).show();
                }
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getActivity(), "No Viewer Application Found", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   /* private void displayFromAsset(String assetFileName) {
        pdfFileName = assetFileName;
        pdfView.fromAsset(SAMPLE_FILE).defaultPage(pageNumber).enableSwipe(true)
                .swipeHorizontal(false).onPageChange(this).enableAnnotationRendering(true).onLoad(this).scrollHandle(new DefaultScrollHandle(getContext())).load();
    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        printBookmarksTree(pdfView.getTableOfContents(), "-");
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        //getActivity().setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
        tv_header.setText(String.format("%s / %s", page + 1, pageCount));
    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {
            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));
            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }*/
}
