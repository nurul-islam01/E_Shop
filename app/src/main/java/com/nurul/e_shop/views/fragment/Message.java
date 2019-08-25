package com.nurul.e_shop.views.fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nurul.e_shop.R;
import com.nurul.e_shop.views.activity.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class Message extends Fragment {

    private Context context;
    
    public Message() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity){
            this.context = context;
        }
    }


}
