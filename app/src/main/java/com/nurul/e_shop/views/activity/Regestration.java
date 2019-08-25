package com.nurul.e_shop.views.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hbb20.CountryCodePicker;
import com.nurul.e_shop.R;
import com.nurul.e_shop.config.FirebaseConfig;
import com.nurul.e_shop.model.User;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Regestration extends AppCompatActivity {

    private static final String TAG = Regestration.class.getSimpleName();
    private static final int GOOGLE_REQUEST_CODE = 7228;
    public static GoogleSignInAccount account;
    private String mPhoneNumber, verificationId;
    private boolean isNumberValide, isLoging = false;


    List<AuthUI.IdpConfig> providers;
    private SignInButton googleSigninButton;
    private LoginButton facebookLogin_button;
    private GoogleSignInClient googleSignInClient;
    private CallbackManager callbackManager;


    private EditText signUpPhoneET, signUpSMSCodeET, loginSMSCodeET, loginPhoneET, signUpNameET, signUpEmailET;
    private TextView signUpTV, loginTV, signupCOdeSendTV, loginSMSCodeTV;
    private CountryCodePicker signUpCountryCodePicker, loginCountyCodePicker;
    private Button loginBT, signUpBT;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private String name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.activity_regestration);
        mAuth = FirebaseAuth.getInstance();

        callbackManager = CallbackManager.Factory.create();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");

        final CardView loginCard = findViewById(R.id.loginCard);
        final CardView signupCard = findViewById(R.id.signupCard);
        signUpNameET = findViewById(R.id.signUpNameET);
        signUpEmailET = findViewById(R.id.signUpEmailET);
        signUpPhoneET = findViewById(R.id.signUpPhoneET);
        signUpSMSCodeET = findViewById(R.id.signUpSMSCodeET);
        signUpTV = findViewById(R.id.signUpTV);
        loginTV = findViewById(R.id.loginTV);
        signupCOdeSendTV = findViewById(R.id.signupCOdeSendTV);
        loginSMSCodeTV = findViewById(R.id.loginSMSCodeTV);
        signUpCountryCodePicker = findViewById(R.id.ccpSignUp);
        loginCountyCodePicker = findViewById(R.id.loginCCP);
        loginSMSCodeET = findViewById(R.id.loginSMSCodeET);
        loginPhoneET = findViewById(R.id.loginPhoneET);
        loginBT = findViewById(R.id.loginBT);
        signUpBT = findViewById(R.id.signUpBT);
        googleSigninButton = findViewById(R.id.googleSignInButton);

        loginCard.setVisibility(View.VISIBLE);
        signupCard.setVisibility(View.GONE);
        signUpTV.setVisibility(View.VISIBLE);
        loginTV.setVisibility(View.GONE);

        signUpCountryCodePicker.registerCarrierNumberEditText(signUpPhoneET);
        loginCountyCodePicker.registerCarrierNumberEditText(loginPhoneET);

        signUpTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginCard.getVisibility() == View.VISIBLE) {
                    loginCard.setVisibility(View.GONE);
                    signupCard.setVisibility(View.VISIBLE);
                    loginTV.setVisibility(View.VISIBLE);
                    signUpTV.setVisibility(View.GONE);
                }
            }
        });

        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginCard.getVisibility() == View.GONE) {
                    loginCard.setVisibility(View.VISIBLE);
                    signupCard.setVisibility(View.GONE);
                    loginTV.setVisibility(View.GONE);
                    signUpTV.setVisibility(View.VISIBLE);
                }
            }
        });

        facebookLogin_button = findViewById(R.id.facebookLogin_button);
        facebookLogin_button.setReadPermissions("email", "public_profile");
        facebookLogin_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                firebaseAuthWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(Regestration.this, "Facebook Signin Canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(Regestration.this, "Facebook Signin Canceled", Toast.LENGTH_SHORT).show();
            }
        });

        googleSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            googleSignin();
                        }
                    });
                } else {
                    Toast.makeText(Regestration.this, "Already Login", Toast.LENGTH_SHORT).show();
                }

            }
        });

        signUpCountryCodePicker.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                if (isValidNumber){
                    try {
                        isNumberValide = isValidNumber;
                        mPhoneNumber = signUpCountryCodePicker.getFullNumberWithPlus();
                    }catch (Exception e){
                        Log.d(TAG, "onValidityChanged: "+ e.getMessage());
                    }
                }else {
                    isNumberValide = isValidNumber;
                }
            }
        });

        loginCountyCodePicker.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                if (isValidNumber){
                    try {
                        isNumberValide = isValidNumber;
                        mPhoneNumber = loginCountyCodePicker.getFullNumberWithPlus();
                    }catch (Exception e){
                        Log.d(TAG, "onValidityChanged: "+ e.getMessage());
                    }
                }else {
                    isNumberValide = isValidNumber;
                }
            }
        });

        loginSMSCodeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mPhoneNumber = loginCountyCodePicker.getFullNumberWithPlus();
                    if (mPhoneNumber != null && isNumberValide) {
                        sendVerificationCode();
                        isLoging = true;
                    } else {
                        Toast.makeText(Regestration.this, "Please Enter your phone number Correctly", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.d(TAG, "onClick: " + e.getMessage());

                }
            }
        });

        signupCOdeSendTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mPhoneNumber = signUpCountryCodePicker.getFullNumberWithPlus();
                    signupCheckWithValue();
                    if (mPhoneNumber != null && isNumberValide) {
                            sendVerificationCode();
                            isLoging = false;
                    } else {
                        Toast.makeText(Regestration.this, "Please Enter your phone number Correctly", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.d(TAG, "onClick: " + e.getMessage());

                }
            }
        });

        signUpBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String code = signUpSMSCodeET.getText().toString().trim();
                    signupCheckWithValue();
                    if (code != null){
                        verifyCode(code);
                        isLoging = false;
                        progressDialog.show();
                    }else {
                        signUpSMSCodeET.setError("Please Enter SMS Code");
                        Toast.makeText(Regestration.this, "Please Enter SMS Code", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Log.d(TAG, "onClick: " + e.getMessage());
                }

            }
        });

        loginBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String code = loginSMSCodeET.getText().toString().trim();
                    if (code != null){
                        verifyCode(code);
                        isLoging = true;
                        progressDialog.show();
                    }else {
                        signUpSMSCodeET.setError("Please Enter SMS Code");
                        Toast.makeText(Regestration.this, "Please Enter SMS Code", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }catch (Exception e){
                    Log.d(TAG, "onClick: " + e.getMessage());
                }
            }
        });
        userStateListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        userStateListener();

    }

    private void userStateListener(){
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null){
                    if (firebaseAuth.getUid() == "Fg3VGyXvy4Xh5axnOFVVhaFHbxa2"){
                        Intent intent = new Intent(Regestration.this, AdminActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Intent intent = new Intent(Regestration.this, MainActivity.class);
                        intent.putExtra("regestration", "regestration");
                        startActivity(intent);
                        finish();
                    }

                }
            }
        });
    }

    private void signupCheckWithValue(){
        name = signUpNameET.getText().toString().trim();
        email = signUpEmailET.getText().toString().trim();
        if (TextUtils.isEmpty(name)){
            signUpNameET.setError("Please Enter name");
            return;
        }else if (TextUtils.isEmpty(email) && isEmailValide(email)){
            signUpEmailET.setError("Please Enter Email Correctly");
            return;
        }
    }

    private boolean isEmailValide(String email){
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    private void sendVerificationCode(){
        try {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    mPhoneNumber,
                    100,
                    TimeUnit.SECONDS,
                    TaskExecutors.MAIN_THREAD,
                    mCallback
            );

        }catch (Exception e){
            Log.d(TAG, "sendVerificationCode: "+e.getMessage());
            Toast.makeText(this, "SendVerificationCode Error", Toast.LENGTH_SHORT).show();
        }

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            Toast.makeText(Regestration.this, "Code Sent", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                try {
                    if (isLoging){
                        loginSMSCodeET.setText(code);
                    }else {
                        signUpSMSCodeET.setText(code);
                    }

                }catch (Exception e){
                    Log.d(TAG, "onVerificationCompleted: " + e.getMessage());
                    Toast.makeText(Regestration.this, "onVerificationCompleted error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.d(TAG, "onVerificationFailed: " + e.getMessage());
            Toast.makeText(Regestration.this, "Verification send Faiel Message : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signinWithCredential(credential);
    }

    private void signinWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            if (isLoging){
                                isSignUp(mAuth.getUid());
                                startActivity(new Intent(Regestration.this, MainActivity.class));
                                finish();
                            }else {
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                User user = new User(mAuth.getUid(), name, email, mPhoneNumber, null, null, "byer");
                                isSignUp(firebaseUser.getUid());
                                startActivity(new Intent(Regestration.this, MainActivity.class));
                                finish();
                            }
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(Regestration.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Regestration.this, "Verification Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void googleSignin(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent googleIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(googleIntent, GOOGLE_REQUEST_CODE);
    }

    private void isSignUp(String userId){
        FirebaseConfig.saveUserToDatabase(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!isLoging) {
                    if (dataSnapshot.getValue(User.class) != null) {
                        Toast.makeText(Regestration.this, "Aready Sign up", Toast.LENGTH_SHORT).show();
                        Toast.makeText(Regestration.this, "Please Login", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        return;
                    }
                }else {
                    if (dataSnapshot.getValue(User.class) == null){
                        Toast.makeText(Regestration.this, "Not Sign up", Toast.LENGTH_SHORT).show();
                        Toast.makeText(Regestration.this, "SPlease Signup", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            switch (requestCode){
                case GOOGLE_REQUEST_CODE:
                    if (data != null){
                        googleSignResult(data);
                    }
                    break;
            }
        }
    }

    private void googleSignResult(Intent data){
        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle();
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }catch (Exception e){
            Log.d(TAG, "googleSignResult: " + e.getMessage());
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle() {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Regestration.this, ""+ user.getUid(), Toast.LENGTH_SHORT).show();
                            User newUser = new User(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhoneNumber(), user.getProviderId(), user.getPhotoUrl().toString(), "byer");
                            userStore(newUser);

                        } else {
                            Toast.makeText(Regestration.this, "Signup not compelete", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }

                    }
                });
    }

    private void firebaseAuthWithFacebook(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            User user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail(), firebaseUser.getPhoneNumber(), firebaseUser.getProviderId(), firebaseUser.getPhotoUrl().toString(), "byer");
                            userStore(user);

                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Regestration.this, "Facebook login failed", Toast.LENGTH_SHORT).show();

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
                Toast.makeText(Regestration.this, "Facebook login failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void userStore(User user){
        if (TextUtils.equals(user.getId(), "Fg3VGyXvy4Xh5axnOFVVhaFHbxa2")){
            startActivity(new Intent(Regestration.this, AdminActivity.class));
            finish();
        }else {
            FirebaseConfig.saveUserToDatabase(mAuth.getUid()).setValue(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Regestration.this, "Signup Complete", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Regestration.this, MainActivity.class));
                                finish();
                            }
                        }
                    });
        }

    }

}