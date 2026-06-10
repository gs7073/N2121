package com.example.n2121;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Utility class to hold Firebase references.
 * Centralizes all database, authentication, and storage pointers to avoid code duplication.
 */
public class FBRef {

    /** Firebase Authentication instance for managing users */
    public static FirebaseAuth refAuth = FirebaseAuth.getInstance();

    /** Firebase Realtime Database instance */
    public static FirebaseDatabase database = FirebaseDatabase.getInstance();

    /** Reference to the "Expenses" node in the realtime database */
    public static DatabaseReference refExpenses = database.getReference("Expenses");

    /** Firebase Storage instance for uploading and downloading files */
    public static FirebaseStorage storage = FirebaseStorage.getInstance();

    /** Reference to the "images" folder in Firebase Storage */
    public static StorageReference refImages = storage.getReference("images/");
}