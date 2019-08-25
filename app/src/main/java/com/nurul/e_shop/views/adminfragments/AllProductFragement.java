package com.nurul.e_shop.views.adminfragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nurul.e_shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllProductFragement extends Fragment {


    public AllProductFragement() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_product_fragement, container, false);
    }

}
