package com.fromscratch.firebase_test_authentication_realtimedatabase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.firebase.ui.auth.AuthUI;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button signInButton;
    Button signOutButton;
    TextView usernameTxV;
    FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Map<String, Object> userDocument = new HashMap<>();

    final View.OnClickListener buttonListener = new View.OnClickListener() {// final method means it cannot be overwritten by subclass
        @Override
        public void onClick(View view) {
            authObject = FirebaseAuth.getInstance();
            Log.v("tes button :", "before button pressed : +conditionCode");
            if(view.getId() == R.id.buttonSignIn){
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.PhoneBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build());
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        RC_SIGN_IN);
            }
            else if(view.getId() == R.id.buttonSignOut){
                AuthUI.getInstance()
                        .signOut(getApplicationContext())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                user = FirebaseAuth.getInstance().getCurrentUser();
                                usernameTxV.setText("User: Null");
                            }
                        });
                Log.v("tes logout", "tessucc");
            };
        }
    };
    static final int RC_SIGN_IN = 1;
    private FirebaseAuth authObject;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v("3 STEP 3", "3 STEP 3");

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
//                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                user = FirebaseAuth.getInstance().getCurrentUser();//this is the important bit, after this what
                if(user==null){
                    usernameTxV.setText("User: Null");
                    Log.v("dude", "no login/login fail");
                }
                if(user!=null){
                    usernameTxV.setText(user.getDisplayName());
                    Log.v("enter this?", "wogin/wogin succ");
                    db.collection("users").document(user.getUid())
                            .set(userDocument)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.v("Document ga", "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.v("Document ga", "Error writing document", e);
                                }
                            });

                }
//                try {
//                    // Google Sign In was successful, authenticate with Firebase
//                    GoogleSignInAccount account = task.getResult(ApiException.class);
//                    firebaseAuthWithGoogle(account);
//                } catch (ApiException e) {
//                    // Google Sign In failed, update UI appropriately
//                    Log.d("SignIN 1 FAIL", "Google sign in failed", e);
//                    // ...
//                }
                // ...
            }
            else {
                Log.d("tes", "login FAIL");
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        authObject = FirebaseAuth.getInstance();
        Log.v("1 STEP 1", "1 STEP 1");
        //-----------------LOGIN UI------------------------------------------------
//        List<AuthUI.IdpConfig> providers = Arrays.asList(
//                new AuthUI.IdpConfig.EmailBuilder().build(),
//                new AuthUI.IdpConfig.PhoneBuilder().build(),
//                new AuthUI.IdpConfig.GoogleBuilder().build());
//IMPORTANT

//                new AuthUI.IdpConfig.FacebookBuilder().build(),
//                new AuthUI.IdpConfig.TwitterBuilder().build());
        Log.v("2 STEP 2", "2 STEP 2");
// Create and launch sign-in intent
        usernameTxV = findViewById(R.id.usernameTextView);
        signInButton =  findViewById(R.id.buttonSignIn);
        signOutButton = findViewById(R.id.buttonSignOut);
        signInButton.setOnClickListener(buttonListener);
        signOutButton.setOnClickListener(buttonListener);
        userDocument.put("name", "Los Angeles");
        userDocument.put("state", "CA");
        userDocument.put("country", "USA");

        if(user==null){
            usernameTxV.setText("User: Null");
            Log.v("dude", "no login/login fail");
        }
        if(user!=null){
            usernameTxV.setText(user.getDisplayName());
            Log.v("user!=null", "but display name fail");
        }

        AuthUI.getInstance()
                .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                            }
                        });
//        startActivityForResult(
//                AuthUI.getInstance()
//                        .createSignInIntentBuilder()
//                        .setAvailableProviders(providers)
//                        .build(),
//                RC_SIGN_IN);//This is the RC_SIGN_IN
        //IMPORTANT
        //----------------------LOGIN UI END-------------------------------------------------------------
    }
}





//    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
//        Log.d("acoount name","ID LUL: " + acct.getId());
//
//        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
//        authObject.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if(task.isSuccessful()){
//                    Log.d("Login success","Login Success");
//                }
//                else{
//                    Log.d("Login Fail", "fail why");
//                }
//            }
//        });
//    }