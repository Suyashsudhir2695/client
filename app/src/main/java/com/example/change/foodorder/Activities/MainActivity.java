package com.example.change.foodorder.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.Model.User;
import com.example.change.foodorder.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thekhaeng.pushdownanim.PushDownAnim;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_SCALE;

public class MainActivity extends AppCompatActivity {
    Button btnSignIn;
    Button btnSignUp;

    Button txtSlogan,txtNeedAcc;
    DatabaseReference table_user;
    FirebaseDatabase database;

    RelativeLayout relativeLayout;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Simply Food");
        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("user");

       // txtSlogan = findViewById(R.id.txtSlogan);
        btnSignIn = findViewById(R.id.btnLogin);
        txtNeedAcc = findViewById(R.id.textViewNeedAnAcc);


        relativeLayout = findViewById(R.id.relativeLayout);


        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/googlesans.ttf");
//        txtSlogan.setTypeface(typeface);
        btnSignIn.setTypeface(typeface);
        PushDownAnim.setPushDownAnimTo(btnSignIn).
        setScale( MODE_SCALE ,
                PushDownAnim.DEFAULT_PUSH_SCALE)
                .setDurationPush( PushDownAnim.DEFAULT_PUSH_DURATION )
                .setDurationRelease( PushDownAnim.DEFAULT_RELEASE_DURATION )
                .setInterpolatorPush( PushDownAnim.DEFAULT_INTERPOLATOR )
                .setInterpolatorRelease( PushDownAnim.DEFAULT_INTERPOLATOR );


        Paper.init(this);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignIn.class));
            }
        });
        txtNeedAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SignUp.class));
            }
        });








        String user = Paper.book().read(Common.USER);
        String pass = Paper.book().read(Common.PWD);

        if (user != null && pass != null){
            if (!user.isEmpty() && !pass.isEmpty())
                loginAutomatically(user,pass);
        }
    }


    private void loginAutomatically(final String email, final String pass) {
        if (Common.isConnected(getBaseContext())) {
            //kuheagkjh


            final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
            final AlertDialog waitingDialog = new SpotsDialog(MainActivity.this,R.style.SpotsDialogMessage);




            waitingDialog.show();
            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    waitingDialog.dismiss();
                    User user = dataSnapshot.child(email.replace(".","_")).getValue(User.class);
                    user.setEmail(email);
                    if (dataSnapshot.child(email.replace(".","_")).exists()) {
                        if (user.getPassword().equals(pass)) {
                            Common.currentUser = user;
                            startActivity(new Intent(MainActivity.this, Dashboard.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Check Your Credentials", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "We Can\'t Find That User", Toast.LENGTH_SHORT).show();

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            Toast.makeText(MainActivity.this, "Couldn't Connect to Internet! " +
                    "Make Sure you have an active internet Connection", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
        if(account != null) {
            updateUI(account);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            String email = account.getEmail();
            String emailkey = email.replace(".", "_");
            final AlertDialog waitingDialog = new SpotsDialog(MainActivity.this, R.style.SpotsDialogMessage);
            waitingDialog.show();
            table_user.child(emailkey)
                    .addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            waitingDialog.dismiss();
                            User localUser = dataSnapshot.getValue(User.class);
                            Intent intent = new Intent(MainActivity.this, Home.class);
                            Common.currentUser = localUser;
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("MainActivity", "onCancelled: " + databaseError.getMessage());

                        }
                    });
        }
    }
}
