package com.nurul.e_shop.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseConfig {

    private static FirebaseDatabase database;
    private static FirebaseAuth mAuth;
    private static StorageReference storageReference;

    public static String getUserId(){
        mAuth = FirebaseAuth.getInstance();
        return mAuth.getCurrentUser().getUid();
    }

    public static StorageReference getFirebaseStorageReference(){
        storageReference = FirebaseStorage.getInstance().getReference();
        return storageReference;
    }

    public static DatabaseReference getFirebaseDatabaseRefrenece(){
         database = FirebaseDatabase.getInstance();
         return database.getReference();
    }

    public static DatabaseReference saveUserToDatabase(String userId){
        return getFirebaseDatabaseRefrenece().child("ALL_USER").child(userId);
    }
    public static DatabaseReference saveProductCard(String pushkey){
        return getFirebaseDatabaseRefrenece().child("CARD").child(pushkey);
    }

    public static StorageReference imageUpload(String filname){
        return getFirebaseStorageReference().child("images").child(filname);
    }

}
