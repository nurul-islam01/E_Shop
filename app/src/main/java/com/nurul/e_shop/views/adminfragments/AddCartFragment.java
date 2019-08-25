package com.nurul.e_shop.views.adminfragments;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;
import com.nurul.e_shop.R;
import com.nurul.e_shop.config.FirebaseConfig;
import com.nurul.e_shop.model.Product;
import com.nurul.e_shop.utlis.ImageCompress;
import com.nurul.e_shop.utlis.ImageCompressTask;
import com.nurul.e_shop.utlis.ImageCompressTaskListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddCartFragment extends Fragment {

    private static final String TAG = AddCartFragment.class.getSimpleName();
    private final int ADDS_IMAGE = 11;
    private final int CART_IMAGE = 12;
    private static final int REQUEST_STORAGE_PERMISSION = 100;

    private Context context;

    private ProgressDialog progressDialog;
    private EditText productTitleET, productDescriptionET, recentPriceET, lastPriceET;
    private Spinner categorySP;
    private Button submitBT;
    private ImageView cardImageIV, adsImage;
    private File adsFile, cardFile;
    private int imageType;
    private String image1, image2;

    private ExecutorService mExecutorService = Executors.newFixedThreadPool(1);

    private ImageCompressTask imageCompressTask;

    public AddCartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        View v = inflater.inflate(R.layout.fragment_add_cart, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        productTitleET = v.findViewById(R.id.productTitleET);
        productDescriptionET = v.findViewById(R.id.productDescriptionET);
        recentPriceET = v.findViewById(R.id.recentPriceET);
        lastPriceET = v.findViewById(R.id.lastPriceET);
        categorySP = v.findViewById(R.id.categorySP);
        submitBT = v.findViewById(R.id.submitBT);
        cardImageIV = v.findViewById(R.id.cardImageIV);
        adsImage = v.findViewById(R.id.adsImage);

        adsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageType = 0;
                requestPermission(ADDS_IMAGE);
            }
        });

        cardImageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageType = 1;
                requestPermission(CART_IMAGE);
            }
        });

        submitBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                productUpload();
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode == ADDS_IMAGE || requestCode == CART_IMAGE) && resultCode == RESULT_OK &&
                data != null) {
            Uri uri = data.getData();
            imageCompressTask = new ImageCompressTask(context, getRealPathFromURI(uri), iImageCompressTaskListener);
            mExecutorService.execute(imageCompressTask);

        }
    }
    private String getRealPathFromURI(Uri uri) {
        String filePath = "";

        Pattern p = Pattern.compile("(\\d+)$");
        Matcher m = p.matcher(uri.toString());
        if (!m.find()) {
            return filePath;
        }
        String imgId = m.group();

        String[] column = { MediaStore.Images.Media.DATA };
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ imgId }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();

        return filePath;
    }



    private ImageCompressTaskListener iImageCompressTaskListener = new ImageCompressTaskListener() {
        @Override
        public void onComplete(List<File> compressed) {

            File file = compressed.get(0);
            Log.d("ImageCompressor", "New photo size ==> " + file.length());
            if (imageType == 0) {
                adsFile = file;
                adsImage.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            }else if (imageType == 1){
                cardFile = file;
                cardImageIV.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            }
        }

        @Override
        public void onError(Throwable error) {
            Log.wtf("ImageCompressor", "Error occurred", error);
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

   private void requestPermission(int REQUEST_PICK_PHOTO) {

        if(PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_PERMISSION);
            }else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_PERMISSION);
            }
        }else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_PICK_PHOTO);
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mExecutorService.shutdown();
        mExecutorService = null;
        imageCompressTask = null;
    }

    private void productUpload(){
        String title = null;
        String description = null;
        String category = null;
        float recentPrice = 0;
        float lastPrice = 0;
        try {
            title = productTitleET.getText().toString().trim();
            description = productDescriptionET.getText().toString().trim();
            recentPrice = Float.parseFloat(recentPriceET.getText().toString().trim());
            lastPrice = Float.parseFloat(lastPriceET.getText().toString().trim());
            category = categorySP.getSelectedItem().toString();
        }catch (Exception e){
            progressDialog.dismiss();
            Log.d(TAG, "fileUpload: " + e.getMessage());
        }
        if (title == null){
            progressDialog.dismiss();
            productTitleET.setError("Title is Empty");
            return;
        }else if (description == null){
            progressDialog.dismiss();
            productDescriptionET.setError("Description is Empty");
            return;
        }else if (recentPrice == 0.0f){
            progressDialog.dismiss();
            recentPriceET.setError("Recent Price is not set");
            return;
        }else if (lastPrice == 0.0f){
            progressDialog.dismiss();
            lastPriceET.setError("Last Price is not set");
            return;
        }else if (adsFile == null){
            progressDialog.dismiss();
            Toast.makeText(context, "Ads image not set", Toast.LENGTH_SHORT).show();
            return;
        }else if (cardFile == null){
            progressDialog.dismiss();
            Toast.makeText(context, "Card image not set", Toast.LENGTH_SHORT).show();
            return;
        }else if (!title.isEmpty() && !description.isEmpty() && recentPrice <= 0f && lastPrice <= 0f && adsFile != null && cardFile != null){
            Uri uri = Uri.fromFile(adsFile);
            Uri uri1 = Uri.fromFile(cardFile);
            UploadTask uploadTask = FirebaseConfig.imageUpload(adsFile.getName()).putFile(uri);
            final UploadTask uploadTask2 = FirebaseConfig.imageUpload(cardFile.getName()).putFile(uri1);
            final String finalTitle = title;
            final String finalDescription = description;
            final float finalRecentPrice = recentPrice;
            final float finalLastPrice = lastPrice;
            final String finalCategory = category;
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Firest Image Upload Failed", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "then: " + task.getException().getMessage());
                        throw task.getException();
                    }
                    progressDialog.dismiss();
                    return FirebaseConfig.getFirebaseStorageReference().getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        image1 = task.getResult().toString();
                        uploadTask2.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Second Image Upload Failed", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "then: " + task.getException().getMessage());
                                    throw task.getException();
                                }
                                progressDialog.dismiss();
                                return FirebaseConfig.getFirebaseStorageReference().getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    image2 = task.getResult().toString();
                                    final String pushKey = FirebaseConfig.getFirebaseDatabaseRefrenece().push().getKey();
                                    FirebaseConfig.saveProductCard(pushKey).setValue(new Product(pushKey, finalTitle, finalDescription, finalRecentPrice, finalLastPrice, finalCategory, 0, image1, image2));
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Complete", Toast.LENGTH_SHORT).show();

                                }else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Second Image Upload Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Firest Image Upload Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }


    }
}
