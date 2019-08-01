package com.example.change.foodorder.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.Interface.ItemClickListener;
import com.example.change.foodorder.Model.User;
import com.example.change.foodorder.R;
import com.example.change.foodorder.ViewHolder.SettingsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class Settings extends AppCompatActivity {
    RecyclerView listView;
    String[] title = {"Change Name","Change Password","Change Phone","Change Home Address","Two Step verification"};
    FirebaseDatabase database;
    DatabaseReference users;
    Place homeAddress;
    ActionBar actionBar;
    FirebaseRecyclerAdapter<User, SettingsViewHolder> adapter;
    RecyclerView.LayoutManager layoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setTitle("Settings");


        listView = findViewById(R.id.listViewSettings);
        layoutManager = new LinearLayoutManager(this);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(layoutManager);
        database = FirebaseDatabase.getInstance();
        users = database.getReference("user");

        loadSettings(Common.currentUser.getEmail());






    }

    private void loadSettings(String email) {
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(users.orderByChild("email").equalTo(email),User.class).build();
        adapter = new FirebaseRecyclerAdapter<User, SettingsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final SettingsViewHolder holder, int position, @NonNull User model) {
                holder.textHomeSettings.setText(model.getAddress());
                holder.textNameSettings.setText(model.getName());
                holder.textPasswordSettings.setText(model.getPassword().replace(model.getPassword(),"******"));
                holder.textPhoneSettings.setText(model.getPhone());
                holder.textTwoStepSettings.setText(model.getTwoStep());
                if (Common.currentUser.getTwoStep().equalsIgnoreCase("on")){
                    holder.switchTwoStep.setChecked(true);
                }

                holder.textHomeSettings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUpdateHomeAddressDialog();
                    }
                });
                holder.textNameSettings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showChangeName();
                    }
                });
                holder.textPasswordSettings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changePassword();
                    }
                });

                holder.textPhoneSettings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showChangePhone();
                    }
                });

                holder.switchTwoStep.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this)
                                    .setIcon(R.drawable.two_step_verification)
                                    .setTitle("Turn On Two Step Verification?")
                                    .setMessage("You Don't have  two step verification turned on! Turn it on now?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            holder.switchTwoStep.setChecked(true);
                                            startActivity(new Intent(Settings.this,PhoneVerification.class));
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            holder.switchTwoStep.setChecked(false);
                                            dialog.dismiss();
                                        }
                                    });
                            builder.show();
                        }
                    }
                });




            }

            @NonNull
            @Override
            public SettingsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.settings_profile_layout,viewGroup,false);
                return new SettingsViewHolder(view);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }

    private void changePassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
        builder.setTitle("Change Your Password");
        builder.setMessage("Type in the old password and the the new one!");
        builder.setIcon(R.drawable.ic_security_black_24dp);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.update_password_layout, null);
        final MaterialEditText editOldPass = view.findViewById(R.id.editOldPass);
        final MaterialEditText editNewPass = view.findViewById(R.id.editNewPass);
        final MaterialEditText editPassAgain = view.findViewById(R.id.editPassAgain);
        builder.setView(view);

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                final android.app.AlertDialog waitingDialog = new SpotsDialog(Settings.this,R.style.spotsDialogPass);
                waitingDialog.show();

                if (editOldPass.getText().toString().equals(Common.currentUser.getPassword())) {
                    if (editNewPass.getText().toString().equals(editPassAgain.getText().toString())) {
                        Map<String, Object> updatedPass = new HashMap<>();
                        updatedPass.put("password", editNewPass.getText().toString());

                        DatabaseReference user = FirebaseDatabase.getInstance().getReference("user");
                        user.child(Common.currentUser.getEmail().replace(".","_")).updateChildren(updatedPass)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        waitingDialog.dismiss();
                                        Toast.makeText(Settings.this, "Your Password's Been Updated", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Settings.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                    } else {
                        waitingDialog.dismiss();
                        Toast.makeText(Settings.this, "Passwords Don't Match", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    waitingDialog.dismiss();
                    Toast.makeText(Settings.this, "Check Your Old Password Again", Toast.LENGTH_SHORT).show();
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
    private void showChangePhone() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
        builder.setTitle("Update Your Phone");
        builder.setMessage("Enter Your New Phone to be Updated");

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.update_phone_layout,null);
        final MaterialEditText editUpdatePhoneSettings = view.findViewById(R.id.editUpdatePhoneSettings);

        builder.setView(view);

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String phone = editUpdatePhoneSettings.getText().toString();
                Common.currentUser.setPhone(phone);
                users.child(Common.currentUser.getEmail().replace(".","_"))
                        .setValue(Common.currentUser)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Settings.this, "Your Phone's Been Updated", Toast.LENGTH_SHORT).show();

                            }
                        });
                dialogInterface.dismiss();

            }
        });
        builder.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        builder.show();


    }

    private void showChangeName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
        builder.setTitle("Update Your Name");
        builder.setMessage("Enter Your New Name to be Updated");

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.update_name_layout,null);
        final MaterialEditText editUpdateNameSettings = view.findViewById(R.id.editUpdateNameSettings);

        builder.setView(view);

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = editUpdateNameSettings.getText().toString();
                Common.currentUser.setName(name);
                users.child(Common.currentUser.getEmail().replace(".","_"))
                        .setValue(Common.currentUser)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Settings.this, "Your Name's Been Updated", Toast.LENGTH_SHORT).show();

                            }
                        });
                dialogInterface.dismiss();

            }
        });
        builder.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        builder.show();

    }


    private void showUpdateHomeAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
        builder.setTitle("Update Your Home Address");
        builder.setMessage("Set Your Home Address So that We Can Easily Deliver to Your Home! ");
        builder.setIcon(R.drawable.ic_add_location_black_24dp);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.update_home_address, null);
        final PlaceAutocompleteFragment fragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        fragment.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
        ((EditText) fragment.getView().findViewById(R.id.place_autocomplete_search_input)).setHint("Enter Home Address");
        ((EditText) fragment.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(20);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("IN")
                .build();
        fragment.setFilter(typeFilter);


        fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                homeAddress = place;
            }

            @Override
            public void onError(Status status) {
                Log.e("Error Places", status.getStatusMessage());

            }
        });
        builder.setView(view);
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String addHome = homeAddress.getAddress().toString();
                Common.currentUser.setAddress(addHome);

                FirebaseDatabase.getInstance().getReference("user")
                        .child(Common.currentUser.getEmail().replace(".","_"))
                        .setValue(Common.currentUser)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Settings.this, "Home Address Has Been Updated", Toast.LENGTH_SHORT).show();

                            }
                        });


                dialogInterface.dismiss();

            }
        });
        builder.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                getFragmentManager().beginTransaction()
                        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
                return;

            }
        });
        builder.show();
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
