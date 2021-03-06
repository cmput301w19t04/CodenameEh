package com.example.codenameeh.activities;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.codenameeh.R;
import com.example.codenameeh.classes.Book;
import com.example.codenameeh.classes.Booklist;
import com.example.codenameeh.classes.CurrentUser;
import com.example.codenameeh.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * @author Cole Boytinck
 * @version 1.0
 * Login activity is the activity that get started on launch.
 * This activity allows the user to login to their accounr
 * The credentials are checked against the firebase auth
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private EditText viewUsername;
    private EditText viewPassword;
    private FirebaseAuth mAuth;

    /**
     * onStart can give the functionality in the future to keep
     * people logged in even after they close the app
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null)
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            updateUI(currentUser.getEmail().replace("@codenameeh.ca", ""));
            Log.d("TEST", currentUser.getDisplayName());
        }
    }

    /**
     * onCrease set the login form, and the login and register buttons
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // Set up the login form.
        viewUsername = findViewById(R.id.username);
        viewPassword = findViewById(R.id.password);

        Button signIn = findViewById(R.id.sign_in);
        signIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button register = findViewById(R.id.register);
        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Attempt login checks the login credentials against the firebase auth
     */
    private void attemptLogin() {
        final String username = viewUsername.getText().toString() + "@codenameeh.ca";
        final String password = viewPassword.getText().toString();
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(viewUsername.getText().toString());
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * updateUI sends the current user to the main screen of the app
     * @param username User that was validated
     */
    private void updateUI(String username) {
        //Get user information from firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(username);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    //Set current user singleton
                    CurrentUser.setInstance(user);
                    CurrentUser.getInstance().setNotifications(user.getNotifications());
                    CurrentUser.getInstance().setOwning(user.getOwning());
                    CurrentUser.getInstance().setBorrowing(user.getBorrowing());
                    CurrentUser.getInstance().setRequesting(user.getRequesting());
                    CurrentUser.getInstance().setBorrowedHistory(user.getBorrowedHistory());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
        // Listen for changes, if any.
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    Log.e("User Listen failed", e.toString());
                    return;
                }
                if(documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    //Set current user singleton
                    CurrentUser.setInstance(user);
                    CurrentUser.getInstance().setOwning(user.getOwning());
                    CurrentUser.getInstance().setBorrowing(user.getBorrowing());
                    CurrentUser.getInstance().setRequesting(user.getRequesting());
                    CurrentUser.getInstance().setBorrowedHistory(user.getBorrowedHistory());
                }
            }
        });
        CollectionReference bookRef = db.collection("All Books");
        bookRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ArrayList<Book> books = new ArrayList<>();
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        books.add(document.toObject(Book.class));
                    }
                    Booklist.setInstance(books);
                }
            }
        });
        bookRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    Log.e("Listen failed.", e.toString());
                    return;
                }
                ArrayList<Book> books = new ArrayList<>();
                for(QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    final Book newBook = document.toObject(Book.class);
                    // Check for a successful confirmation, and make the swap
                    if(newBook.isConfirmed()){
                        if(newBook.isBorrowed()){
                            final DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(newBook.getBorrower());
                            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        User user = documentSnapshot.toObject(User.class);
                                        user.removeBorrow(newBook);
                                        docRef.set(user);
                                        FirebaseFirestore.getInstance().collection("All Books").document(newBook.getUuid())
                                                .set(newBook);
                                    }
                                }
                            });
                        } else{
                            final DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(newBook.getBorrower());
                            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        User user = documentSnapshot.toObject(User.class);
                                        user.newBorrow(newBook);
                                        docRef.set(user);
                                        FirebaseFirestore.getInstance().collection("All Books").document(newBook.getUuid())
                                                .set(newBook);
                                    }
                                }
                            });
                        }
                    }
                    books.add(newBook);
                }
                Booklist.setInstance(books);
            }
        });

    }
}

