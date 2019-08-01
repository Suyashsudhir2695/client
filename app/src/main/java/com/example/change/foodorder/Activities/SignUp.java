package com.example.change.foodorder.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.Model.User;
import com.example.change.foodorder.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.thekhaeng.pushdownanim.PushDownAnim;

import dmax.dialog.SpotsDialog;

import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_SCALE;

public class SignUp extends AppCompatActivity {
    Button btnSignUp,btnSelect;

    private EditText mEditTextPass, mEditTextPhone, mEditTextName, editSecureCode,editEmail;
    Uri saveUri;
    private final int PICK_IMAGE_REQUEST = 71;
    FirebaseStorage storage;
    StorageReference storageReference;
    User mUser;
    SignInButton googleSignInBtn;
    private static final String TAG = "SignUp";
    Button mBtnSignIn;
    RelativeLayout relativeLayout;
    public static final int RC_SIGN_IN_REQUEST = 101;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    FirebaseDatabase db;
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");
        mEditTextName =  findViewById(R.id.editNameSignUp);
        mEditTextPass = findViewById(R.id.editPassSignUp);

        editEmail =  findViewById(R.id.editEmail);
        mBtnSignIn = findViewById(R.id.btnLoginSignUp);
        googleSignInBtn = findViewById(R.id.googleSignInBtn);
        relativeLayout = findViewById(R.id.relativeLayout);

        db = FirebaseDatabase.getInstance();
        mDatabaseReference = db.getReference("user");
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/googlesans.ttf");










        //Initialize firebase

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mUser = new User();
        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Common.isConnected(SignUp.this)) {
                    Intent intent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(intent, RC_SIGN_IN_REQUEST);
                } else {
                    Snackbar.make(relativeLayout, "Check Your Internet Connection", Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.WHITE)
                            .setAction("Settings", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
                                    return;
                                }
                            }).show();

                }
            }
        });





        PushDownAnim.setPushDownAnimTo(mBtnSignIn)
        .setScale( MODE_SCALE ,
                PushDownAnim.DEFAULT_PUSH_SCALE)
                .setDurationPush( PushDownAnim.DEFAULT_PUSH_DURATION )
                .setDurationRelease( PushDownAnim.DEFAULT_RELEASE_DURATION )
                .setInterpolatorPush( PushDownAnim.DEFAULT_INTERPOLATOR )
                .setInterpolatorRelease( PushDownAnim.DEFAULT_INTERPOLATOR );


        mBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnected(getBaseContext())) {

                    final String pass = mEditTextPass.getText().toString();
                    final String name = mEditTextName.getText().toString();

                    final String email = editEmail.getText().toString();



                    if (email.isEmpty()) {
                        Toast.makeText(SignUp.this, "Fill in the Email!", Toast.LENGTH_SHORT).show();
                        return;

                    } else if (pass.isEmpty()) {
                        Toast.makeText(SignUp.this, "Fill in the Password!", Toast.LENGTH_SHORT).show();
                        return;

                    } else if (name.isEmpty()) {
                        Toast.makeText(SignUp.this, "Fill in the Name!", Toast.LENGTH_SHORT).show();
                        return;

                    } else {

                        final ProgressDialog mProgressDialog = new ProgressDialog(SignUp.this);
                        mProgressDialog.setMessage("Hang On! Signing You Up ...");
                        mProgressDialog.show();
                        mDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.child(email.replace(".","_")).exists()) {

                                    Toast.makeText(SignUp.this, "This Email Exists Already", Toast.LENGTH_SHORT).show();
                                } else {
                                    mUser = new User();

                                    mUser.setName(mEditTextName.getText().toString());
                                    mUser.setPhone("");
                                    mUser.setSecureCode("");
                                    mUser.setPassword(mEditTextPass.getText().toString());
                                    mUser.setEmail(editEmail.getText().toString());
                                    //mUser.setImage(saveUri.toString());






                                    mDatabaseReference.child(email.replace(".","_")).setValue(mUser);
                                    Toast.makeText(SignUp.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(SignUp.this, SignIn.class));
                                }
                                mProgressDialog.dismiss();


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled: " + databaseError.getMessage());

                            }
                        });
                    }

                }
                else{
                    Snackbar.make(relativeLayout,"Check Your Internet Connection", Snackbar.LENGTH_INDEFINITE)
                            .setActionTextColor(Color.WHITE)
                            .setAction("Settings", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivityForResult(new Intent(Settings.ACTION_SETTINGS),0);
                                }
                            }).show();

                }
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN_REQUEST){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignIn(task);
        }
    }

    private void handleSignIn(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            updateUI(account);

        } catch (ApiException e) {
            e.printStackTrace();
        }

    }
    private void updateUI(GoogleSignInAccount account) {
        if (account != null){
            final String email = account.getEmail();
            final String emailKey = email.replace(".","_");
            final String name = account.getDisplayName();
            final String profilePicture;
            if (account.getPhotoUrl() != null){
                profilePicture = account.getPhotoUrl().toString();
            }
            else {
                profilePicture = "https://firebasestorage.googleapis.com/v0/b/foodorder-6d868.appspot.com/o/profilePicture%2Fname.png?alt=media&token=87110786-d23c-43f8-9c51-d1aa057c5956";
            }


            mDatabaseReference.orderByKey().equalTo(emailKey)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (! dataSnapshot.child(emailKey).exists()){
                                User newUser = new User();
                                newUser.setEmail(email);
                                newUser.setName(name);
                                newUser.setPassword("");
                                newUser.setIsStaff("false");
                                newUser.setImage(profilePicture);


                                mDatabaseReference.child(emailKey)
                                        .setValue(newUser)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                    Toast.makeText(SignUp.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                                                final AlertDialog waitingDialog = new SpotsDialog(SignUp.this,R.style.SpotsDialogMessage);
                                                //waitingDialog.setMessage("Hang On...");
                                                waitingDialog.show();

                                                mDatabaseReference.child(emailKey)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                waitingDialog.dismiss();
                                                                User localUser = dataSnapshot.getValue(User.class);

                                                                Intent intent = new Intent(SignUp.this,Home.class);
                                                                Common.currentUser = localUser;
                                                                startActivity(intent);
                                                                finish();
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });

                                            }
                                        });


                            }
                            else {
                                final AlertDialog waitingDialog = new SpotsDialog(SignUp.this,R.style.SpotsDialogMessage);
                                //waitingDialog.setMessage("Hang On...");
                                waitingDialog.show();
                                mDatabaseReference.child(emailKey)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                waitingDialog.dismiss();
                                                User localUser = dataSnapshot.getValue(User.class);

                                                Intent intent = new Intent(SignUp.this,Home.class);
                                                Common.currentUser = localUser;
                                                startActivity(intent);
                                                finish();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });



                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

        }


    }





    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(SignUp.this);
        updateUI(account);
    }




}
