package com.example.change.foodorder.Activities;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PhoneVerification extends AppCompatActivity {
    private static final String TAG = "PhoneVerification";
    private FirebaseAuth mAuth;
    FirebaseDatabase database ;
    DatabaseReference userRef;
    // [END declare_auth]

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    MaterialEditText editPhoneTwoStep,editCodeTwoStep;

    Button btnSendCode,btnTwoStepContinue;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        editPhoneTwoStep = findViewById(R.id.editPhoneTwoStep);

        editCodeTwoStep = findViewById(R.id.editCodeTwoStep);

        editPhoneTwoStep.setText(Common.currentUser.getPhone());
        progressDialog = new ProgressDialog(PhoneVerification.this);



        btnSendCode = findViewById(R.id.btnSendCode);
        btnTwoStepContinue = findViewById(R.id.btnTwoStepContinue);
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("user");

        btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Sending Code..");
                progressDialog.show();
                startPhoneNumberVerification(editPhoneTwoStep.getText().toString());
            }
        });

        btnTwoStepContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Verifying..");
                progressDialog.show();
                verifyPhoneNumberWithCode(mVerificationId,editCodeTwoStep.getText().toString());
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
                Toast.makeText(PhoneVerification.this, "Verified", Toast.LENGTH_SHORT).show();
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

    }

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
        Map<String,Object> map = new HashMap<>();
        map.put("twoStep","on");
        userRef.child(Common.currentUser.getEmail().replace(".","_")).updateChildren(map).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PhoneVerification.this, "Your Two Step verification has been turned on", Toast.LENGTH_SHORT).show();
                    }
                }
        )
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PhoneVerification.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = mAuth.getCurrentUser();
       // updateUI(currentUser);

        // [START_EXCLUDE]
        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(editPhoneTwoStep.getText().toString());
        }
        // [END_EXCLUDE]
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = editPhoneTwoStep.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            editPhoneTwoStep.setError("Invalid phone number.");
            return false;
        }

        return true;
    }
}
