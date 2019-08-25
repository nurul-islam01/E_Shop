package com.nurul.e_shop.views.fragment;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nurul.e_shop.R;
import com.nurul.e_shop.views.activity.MainActivity;
import com.nurul.e_shop.views.activity.Regestration;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.GraphRequest.TAG;


public class AccountFragment extends Fragment {

    private Context context;

    private LinearLayout afterLoginL, withoutSignupL;
    private CircleImageView profile_image;
    private TextView userNameTV;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account, container, false);
        TextView regestrationTV = v.findViewById(R.id.regestrationTV);

        afterLoginL = v.findViewById(R.id.afterLoginL);
        withoutSignupL = v.findViewById(R.id.withoutSignupL);
        profile_image = v.findViewById(R.id.profile_image);
        userNameTV = v.findViewById(R.id.userNameTV);

        ImageButton logoutBtn = v.findViewById(R.id.logoutBtn);
        regestrationTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, Regestration.class));
                getActivity().finish();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (FirebaseAuth.getInstance().getCurrentUser() != null){
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(context, "Log out", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "Already Log out", Toast.LENGTH_SHORT).show();
                }

            }
        });

        userStateListener();

        return v;
    }

    private void userStateListener(){
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null){
                    withoutSignupL.setVisibility(View.GONE);
                    afterLoginL.setVisibility(View.VISIBLE);
                    try {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        userNameTV.setText(user.getDisplayName());
                        Picasso.get().load(user.getPhotoUrl()).error(R.drawable.com_facebook_profile_picture_blank_square)
                                .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                                .into(profile_image);
                    }catch (Exception e){
                        Log.d(TAG, "onAuthStateChanged: " + e.getMessage());
                    }

                }else {
                    withoutSignupL.setVisibility(View.VISIBLE);
                    afterLoginL.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        userStateListener();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

}
