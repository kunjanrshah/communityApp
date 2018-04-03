package com.krs.vastipatrak.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.krs.vastipatrak.R;
import com.shockwave.pdfium.PdfDocument;

import java.util.List;

public class GirlsFragment extends Fragment implements OnPageChangeListener, OnLoadCompleteListener {

    public static final String SAMPLE_FILE = "girls.pdf";
    PDFView pdfView;
    Integer pageNumber = 0;
    String pdfFileName;
    private String TAG = GirlsFragment.class.getName();
    private TextView tv_header;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pdf, container, false);


        pdfView = rootView.findViewById(R.id.pdfView);
        tv_header = rootView.findViewById(R.id.tv_header);
        TextView txt_marquee = rootView.findViewById(R.id.txt_marquee);
        txt_marquee.setSelected(true);
        displayFromAsset(SAMPLE_FILE);

        return rootView;
    }

    private void displayFromAsset(String assetFileName) {
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
    }
}
