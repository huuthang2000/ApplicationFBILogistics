package com.example.demoapp.view.activity.air;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.demoapp.R;
import com.example.demoapp.databinding.ActivityAirPageBinding;
import com.example.demoapp.view.activity.chat.DashboardActivity;
import com.example.demoapp.view.activity.loginAndRegister.SignInActivity;
import com.example.demoapp.view.fragment.air.AirExportFragment;
import com.example.demoapp.view.fragment.air.AirImportFragment;
import com.example.demoapp.view.fragment.air.RetailGoodsExportFragment;
import com.example.demoapp.view.fragment.home.HomeFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class AirPageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityAirPageBinding binding;

    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_AIR_IMPORT = 1;
    private static final int FRAGMENT_AIR_EXPORT = 2;
    private static final int FRAGMENT_AIR_RETAIL_GOODS = 3;
    private static final int MESSAGE = 4;
    private static final int LOGOUT = 5;

    private FirebaseAuth mAuth;
    private TextView tvName, tvEmail;
    String uid;

    private int mCurrentFragment = FRAGMENT_HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAirPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        binding.navigationViewAir.setNavigationItemSelectedListener(this);
        mAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        setUserName();
        NavigationView navigationView = findViewById(R.id.navigation_view_air);
        View nav_header = LayoutInflater.from(AirPageActivity.this).inflate(R.layout.layout_header_nav, null);
        tvName = nav_header.findViewById(R.id.tv_name_header);
        tvEmail = nav_header.findViewById(R.id.tv_email_header);
        navigationView.addHeaderView(nav_header);

        replaceFragment(new HomeFragment());
        binding.navigationViewAir.getMenu().findItem(R.id.tab_home_air).setChecked(true);
    }

    private void setUserName() {
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // check until required data get
                for (DataSnapshot ds : snapshot.getChildren()) {
                    // get data
                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();


                    // set data in profile
                    tvName.setText(name);
                    tvEmail.setText(email);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tab_home_air:
                if (mCurrentFragment != FRAGMENT_HOME) {
                    replaceFragment(new HomeFragment());
                    mCurrentFragment = FRAGMENT_HOME;
                    binding.toolbar.setTitle("Home");
                }
                break;
            case R.id.tab_import_air:
                if (mCurrentFragment != FRAGMENT_AIR_IMPORT) {
                    replaceFragment(new AirImportFragment());
                    mCurrentFragment = FRAGMENT_AIR_IMPORT;
                    binding.toolbar.setTitle("Air Import");
                }
                break;
            case R.id.tab_export_air:
                if (mCurrentFragment != FRAGMENT_AIR_EXPORT) {
                    replaceFragment(new AirExportFragment());
                    mCurrentFragment = FRAGMENT_AIR_EXPORT;
                    binding.toolbar.setTitle("Air Export");
                }
                break;
            case R.id.tab_retail_goods_air:
                if (mCurrentFragment != FRAGMENT_AIR_RETAIL_GOODS) {
                    replaceFragment(new RetailGoodsExportFragment());
                    mCurrentFragment = FRAGMENT_AIR_RETAIL_GOODS;
                    binding.toolbar.setTitle("Air Retail Goods");
                }
                break;
            case R.id.tab_chat:
                if(mCurrentFragment != MESSAGE){
                    startActivity(new Intent(AirPageActivity.this, DashboardActivity.class));
                    finish();
                }
                break;
            case R.id.nav_logout:
                if(mCurrentFragment != LOGOUT){
                    mAuth.signOut();
                    checkUserStatus();
                }
                break;
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkUserStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            uid =  user.getUid();
        } else {
            startActivity(new Intent(AirPageActivity.this, SignInActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }
}