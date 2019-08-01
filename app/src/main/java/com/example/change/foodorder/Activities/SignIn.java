package com.example.change.foodorder.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.Model.User;
import com.example.change.foodorder.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_SCALE;

public class SignIn extends AppCompatActivity {
    EditText editPhone;
    EditText editPass;
    Button btnSignIn;
    DatabaseReference table_user;
    FirebaseDatabase database;
    CheckBox checkBox;
    TextView forgotPass;
    EditText forgotPhoneEdit;
    EditText secureCodeEdit;
    RelativeLayout relativeLayout;
    ActionBar actionBar;
    User user;
    private static final String TAG = "PhoneVerification";
    private FirebaseAuth mAuth;


    // [END declare_auth]

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    ProgressDialog progressDialog;
    MaterialEditText editSigninCode;
    AlertDialog.Builder codeBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
       // actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setDisplayUseLogoEnabled(true);
        //actionBar.setLogo(R.mipmap.logo);
        //actionBar.setTitle("Sign In");
        //setTitle("Sign In");
        editPhone = findViewById(R.id.editPhone);
        editPass = findViewById(R.id.editPass);
        btnSignIn = findViewById(R.id.btnLoginS);
        checkBox = findViewById(R.id.remember);
        forgotPass = findViewById(R.id.txtPassForgot);
        relativeLayout = findViewById(R.id.relativeLayout);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/googlesans.ttf");
        btnSignIn.setTypeface(typeface);
        checkBox.setTypeface(typeface);
        forgotPass.setTypeface(typeface);
        editPhone.setTypeface(typeface);
        editPass.setTypeface(typeface);

        Paper.init(this);
        progressDialog = new ProgressDialog(SignIn.this);

        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("user");

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPass();
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                progressDialog.dismiss();
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                //updateUI(STATE_VERIFY_SUCCESS, credential);
                // [END_EXCLUDE]
                //signInWithPhoneAuthCredential(credential);
                Toast.makeText(SignIn.this, "Verified", Toast.LENGTH_SHORT).show();
                editSigninCode.setText(mVerificationId);

                Common.currentUser = user;
                startActivity(new Intent(SignIn.this, Dashboard.class));
                finish();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    // mPhoneNumberField.setError("Invalid phone number.");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    progressDialog.dismiss();
                    Snackbar.make(findViewById(android.R.id.content), "You have exceeded your code limits! Try again in sometime",
                            Snackbar.LENGTH_SHORT).show();

                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                //updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);
                progressDialog.dismiss();

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // [START_EXCLUDE]
                // Update UI
                // updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        };

        PushDownAnim.setPushDownAnimTo(btnSignIn,forgotPass,checkBox).
                setScale( MODE_SCALE ,
                        PushDownAnim.DEFAULT_PUSH_SCALE)
                .setDurationPush( PushDownAnim.DEFAULT_PUSH_DURATION )
                .setDurationRelease( PushDownAnim.DEFAULT_RELEASE_DURATION )
                .setInterpolatorPush( PushDownAnim.DEFAULT_INTERPOLATOR )
                .setInterpolatorRelease( PushDownAnim.DEFAULT_INTERPOLATOR );


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnected(getBaseContext())) {


                    if (checkBox.isChecked()) {
                        Paper.book().write(Common.USER, editPhone.getText().toString());
                        Paper.book().write(Common.PWD, editPass.getText().toString());
                    }


                    final String phone = editPhone.getText().toString();
                    final String pass = editPass.getText().toString();
                    if (phone.isEmpty()) {
                        Toast.makeText(SignIn.this, "Fill in the email", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (pass.isEmpty()) {
                        Toast.makeText(SignIn.this, "Fill in the password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else{


                    final ProgressDialog dialog = new ProgressDialog(SignIn.this);
                    dialog.setMessage("Hang On...");
                    final AlertDialog waitingDialog = new SpotsDialog(SignIn.this, R.style.SpotsDialogMessage);
                    //waitingDialog.setMessage("Hang On...");
                    waitingDialog.show();


                    table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            waitingDialog.dismiss();
                             user = dataSnapshot.child(phone.replace(".", "_")).getValue(User.class);

                            if (dataSnapshot.child(phone.replace(".", "_")).exists()) {
                                if (user.getPassword().equals(pass)) {

                                    if (user.getTwoStep().equals("on")){
                                        startPhoneNumberVerification(user.getPhone());
                                        ShowCodeAlert();

                                    }
                                    else {
                                        Common.currentUser = user;
                                        startActivity(new Intent(SignIn.this, Dashboard.class));
                                        finish();
                                    }


                                    table_user.removeEventListener(this);

                                } else {
                                    Toast.makeText(SignIn.this, "Check Your Credentials", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(SignIn.this, "We Can\'t Find That User", Toast.LENGTH_SHORT).show();

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                }
                else {
                    Snackbar.make(relativeLayout,"Check Your Internet Connection",Snackbar.LENGTH_LONG)
                            .setActionTextColor(Color.WHITE)
                            .setAction("Settings", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivityForResult(new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS),0);

                                }
                            }).show();

                }
            }
        });


    }

    private void ShowCodeAlert() {
        codeBuilder = new AlertDialog.Builder(this)
                .setIcon(R.drawable.two_step_verification)
                .setTitle("Two Step Verification")
                .setMessage("We sent you a message containing a code!");
        codeBuilder.setCancelable(false);

        View view = this.getLayoutInflater().inflate(R.layout.two_step_signin_code,null);

         editSigninCode = view.findViewById(R.id.editSigninCode);
        codeBuilder.setView(view);
        codeBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                verifyPhoneNumberWithCode(mVerificationId,editSigninCode.getText().toString());
            }
        });
        codeBuilder.setNegativeButton("Send Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    resendVerificationCode(user.getPhone(),mResendToken);
                    ShowCodeAlert();
            }
        });

        codeBuilder.show();
    }

    private void showForgotPass() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Did You Forget Your Password?");
        builder.setMessage("Fill In Your Information and Leave the rest to Us");
        builder.setIcon(R.drawable.forgot_password);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.forgot_password_layout,null);
        forgotPhoneEdit = view.findViewById(R.id.editPhoneForgot);
        secureCodeEdit = view.findViewById(R.id.editCodeForgot);
        builder.setView(view);


        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (forgotPhoneEdit.getText().toString().isEmpty()) {
                    Toast.makeText(SignIn.this, "Fill in the Phone", Toast.LENGTH_SHORT).show();
                    return;

                } else if (secureCodeEdit.getText().toString().isEmpty()) {
                    Toast.makeText(SignIn.this, "Fill In The Secure Code", Toast.LENGTH_SHORT).show();
                    return;

                } else{


                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.child(forgotPhoneEdit.getText().toString().replace(".","_")).getValue(User.class);

                            if (user.getSecureCode().equals(secureCodeEdit.getText().toString()))
                                Toast.makeText(SignIn.this, "Your Password is " + user.getPassword(), Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(SignIn.this, "Invalid Secure Code", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            }

            }
        });

        builder.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();



            }
        });
        builder.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        // updateUI(currentUser);

        // [START_EXCLUDE]
        if (mVerificationInProgress ) {
            startPhoneNumberVerification(user.getPhone());
        }
        // [END_EXCLUDE]
    }

//    private boolean validatePhoneNumber() {
//        String phoneNumber = user.getPhone();
//        if (TextUtils.isEmpty(phoneNumber)) {
//            editPhoneTwoStep.setError("Invalid phone number.");
//            return false;
//        }
//
//        return true;
//    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        //
        Log.i(TAG, "verifyPhoneNumberWithCode: " + credential);
        Toast.makeText(this, "Verified gggg", Toast.LENGTH_SHORT).show();
        Common.currentUser = user;
        startActivity(new Intent(SignIn.this, Home.class));
        finish();



    }
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
}
