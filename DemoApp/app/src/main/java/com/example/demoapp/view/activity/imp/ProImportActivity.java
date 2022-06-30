package com.example.demoapp.view.activity.imp;

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
import com.example.demoapp.databinding.ActivityProImportBinding;
import com.example.demoapp.view.activity.chat.DashboardActivity;
import com.example.demoapp.view.activity.loginAndRegister.SignInActivity;
import com.example.demoapp.view.fragment.home.HomeFragment;
import com.example.demoapp.view.fragment.imp.ImportFragment;
import com.example.demoapp.view.fragment.imp.ImportLclFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProImportActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ActivityProImportBinding binding;

    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_IMPORT = 1;
    private static final int FRAGMENT_IMPORT_LCL = 2;
    private static final int ACTIVITY_MESSAGE = 3;
    private static final int LOG_OUT = 4;

    private FirebaseAuth mAuth;
    String uid;
    TextView tvName, tvEmail;

    private int mCurrentFragment = FRAGMENT_HOME;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProImportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        binding.navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        setUserName();
        NavigationView navigationView = findViewById(R.id.navigation_view);
        View nav_header = LayoutInflater.from(ProImportActivity.this).inflate(R.layout.layout_header_nav, null);
        tvName = nav_header.findViewById(R.id.tv_name_header);
        tvEmail = nav_header.findViewById(R.id.tv_email_header);
        navigationView.addHeaderView(nav_header);

        replaceFragment(new HomeFragment());
        binding.navigationView.getMenu().findItem(R.id.navigation_home).setChecked(true);

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
            case R.id.navigation_home:
                if (mCurrentFragment != FRAGMENT_HOME) {
                    replaceFragment(new HomeFragment());
                    mCurrentFragment = FRAGMENT_HOME;
                    binding.toolbar.setTitle("Home");
                }
                break;
            case R.id.navigation_import:
                if (mCurrentFragment != FRAGMENT_IMPORT) {
                    replaceFragment(new ImportFragment());
                    mCurrentFragment = FRAGMENT_IMPORT;
                    binding.toolbar.setTitle("Import");
                }
                break;
            case R.id.navigation_import_lcl:
                if (mCurrentFragment != FRAGMENT_IMPORT_LCL) {
                    replaceFragment(new ImportLclFragment());
                    mCurrentFragment = FRAGMENT_IMPORT_LCL;
                    binding.toolbar.setTitle("Import LCL");
                }
                break;
            case R.id.nav_logout:
                if (mCurrentFragment != LOG_OUT) {
                    mAuth.signOut();
                    checkUserStatus();
                }
                break;
            case R.id.nav_message_import:
                if (mCurrentFragment != ACTIVITY_MESSAGE) {
                    Intent intent = new Intent(this, DashboardActivity.class);
                    startActivity(intent);
                }
                break;
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkUserStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        } else {
            startActivity(new Intent(ProImportActivity.this, SignInActivity.class));
            finish();
        }
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