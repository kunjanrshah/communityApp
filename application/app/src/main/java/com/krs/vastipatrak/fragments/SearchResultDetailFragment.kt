package com.krs.vastipatrak.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.krs.vastipatrak.utils.AppConstants.EXTRA_COORDINATES
import com.krs.vastipatrak.utils.AppConstants.EXTRA_POSITION

class SearchResultDetailFragment : Fragment() {

    private lateinit var coordinates: FloatArray

    companion object {

        const val TAG = "SearchResultDetailFragment"
        fun newInstance(coordinates: FloatArray, adapterPosition: Int): SearchResultDetailFragment {
            val bundle = Bundle().apply {

                putFloatArray(EXTRA_COORDINATES, coordinates)
                putInt(EXTRA_POSITION, adapterPosition)
            }

            return SearchResultDetailFragment().apply { arguments = bundle }
        }
    }
}
