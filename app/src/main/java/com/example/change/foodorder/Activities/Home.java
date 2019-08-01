package com.example.change.foodorder.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.Databases.Database;
import com.example.change.foodorder.Interface.ItemClickListener;
import com.example.change.foodorder.Model.Category;
import com.example.change.foodorder.Model.Token;
import com.example.change.foodorder.R;
import com.example.change.foodorder.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import io.paperdb.Paper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference category,ratingTable;
    TextView textViewName,textEmail;
    ImageView imageProfilePic;

    RecyclerView recycle_menu;
    RecyclerView.LayoutManager layoutManager;

    SwipeRefreshLayout swipeRefreshLayout;
    CounterFab fab;
    Place homeAddress;
    public static final int RC_SIGN_IN_REQUEST = 101;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    FloatingActionButton fabComp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Home");
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnected(Home.this)) {
                    loadMenu();
                } else {
                    Toast.makeText(Home.this, "Couldn't Connect to Internet! " +
                            "Make Sure you have an active internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnected(getBaseContext())) {
                    loadMenu();
                } else {
                    Toast.makeText(getBaseContext(), "Couldn't Connect to Internet! " +
                            "Make Sure you have an active internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });


        database = FirebaseDatabase.getInstance();
        category = database.getReference("category");
        ratingTable = database.getReference("Ratings");


        Paper.init(this);


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, Cart.class));
            }
        });

        fab.setCount(new Database(Home.this).getCartCount(Common.currentUser.getEmail()));


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);


        View headerView = navigationView.getHeaderView(0);
        textViewName = headerView.findViewById(R.id.textViewfull);
        textEmail = headerView.findViewById(R.id.textEmail);
        imageProfilePic = headerView.findViewById(R.id.imageProfilePicture);
        textViewName.setText(Common.currentUser.getName());
        textEmail.setText(Common.currentUser.getEmail());

        recycle_menu = findViewById(R.id.recyclerView);
        //recycle_menu.setHasFixedSize(true);
       // layoutManager = new GridLayoutManager(this);
        recycle_menu.setLayoutManager(new GridLayoutManager(this,2));
        if (Common.currentUser.getPassword().equals("")){
            Snackbar.make(swipeRefreshLayout,"Update Your Profile",Snackbar.LENGTH_LONG).setActionTextColor(Color.WHITE)
                    .setAction("Update", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Home.this,Settings.class));
                        }
                    }).show();
        }


        updateToken(FirebaseInstanceId.getInstance().getToken());


    }

    private void updateToken(String fbToken) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tokens = database.getReference("Tokens");
        Token token = new Token(fbToken, false);
        tokens.child(Common.currentUser.getEmail().replace(".","_")).setValue(token);
    }

    private void loadMenu() {

        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category, Category.class).build();

        textViewName.setText(Common.currentUser.getName());
        Picasso.with(getBaseContext()).load(Common.currentUser.getImage()).into(imageProfilePic);
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MenuViewHolder viewHolder, int position, @NonNull Category model) {
                viewHolder.textMenu.setText(model.getName());
                viewHolder.textDesc.setText(model.getDesc());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);
                final Category clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Get the CategoryID/MenuId
                        Intent foodIntent = new Intent(Home.this, FoodList.class);
                        foodIntent.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(foodIntent);
                    }
                });



            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item, parent, false);
                return new MenuViewHolder(itemView);
            }
        };
        adapter.startListening();
        recycle_menu.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search_all) {
            loadMenu();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {

        } else if (id == R.id.nav_cart) {
            startActivity(new Intent(Home.this, Cart.class));

        } else if (id == R.id.nav_comp){
            startActivity(new Intent(Home.this,Complaints.class));

        }

        else if (id == R.id.nav_orders) {
            startActivity(new Intent(Home.this, OrderStatus.class));

        }  else if (id == R.id.nav_acc) {

            Paper.book().destroy();
            googleSignOut();

            Intent signOut = new Intent(Home.this, MainActivity.class);
            signOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(signOut);
            this.finish();
        }
        else if (id == R.id.nav_bal){
            startActivity(new Intent(Home.this,FingerprintAuth.class));
            overridePendingTransition(R.anim.slid_in_right, R.anim.slide_out_left);
        }


        else if (id == R.id.nav_settings){
            startActivity(new Intent(Home.this,Settings.class));
        }
        else if (id == R.id.nav_fav){
            startActivity(new Intent(Home.this, FavoritesActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void googleSignOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(Home.this, "Successfully Signed Out", Toast.LENGTH_SHORT).show();
                    }
                });

    }





    @Override
    protected void onPostResume() {
        super.onPostResume();
        fab.setCount(new Database(Home.this).getCartCount(Common.currentUser.getEmail()));
    }



}
