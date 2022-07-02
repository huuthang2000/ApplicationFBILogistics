package com.example.demoapp.view.activity.log;

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
import com.example.demoapp.databinding.ActivityLogProBinding;
import com.example.demoapp.view.activity.chat.DashboardActivity;
import com.example.demoapp.view.activity.loginAndRegister.SignInActivity;
import com.example.demoapp.view.fragment.home.HomeFragment;
import com.example.demoapp.view.fragment.log.LogFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LogProActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityLogProBinding binding;

    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_LOG = 1;
    private static final int ACTIVITY_MESSAGE = 2;
    private static final int LOG_OUT = 3;

    private FirebaseAuth mAuth;
    private String uid;
    TextView tvName, tvEmail;

    private int mCurrentFragment = FRAGMENT_HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLogProBinding.inflate(getLayoutInflater());
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
        View nav_header = LayoutInflater.from(LogProActivity.this).inflate(R.layout.layout_header_nav, null);
        tvName = nav_header.findViewById(R.id.tv_name_header);
        tvEmail = nav_header.findViewById(R.id.tv_email_header);
        navigationView.addHeaderView(nav_header);

        replaceFragment(new HomeFragment());
        binding.navigationView.getMenu().findItem(R.id.navigation_home_log).setChecked(true);

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
            case R.id.navigation_home_log:
                if (mCurrentFragment != FRAGMENT_HOME) {
                    replaceFragment(new HomeFragment());
                    mCurrentFragment = FRAGMENT_HOME;
                    binding.toolbar.setTitle("Home");
                }
                break;
            case R.id.navigation_log:
                if (mCurrentFragment != FRAGMENT_LOG) {
                    replaceFragment(new LogFragment());
                    mCurrentFragment = FRAGMENT_LOG;
                    binding.toolbar.setTitle("LOG");
                }
                break;
            case R.id.navigation_message_log:
                if (mCurrentFragment != ACTIVITY_MESSAGE) {
                    Intent intent = new Intent(this, DashboardActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.navigation_logout_log:
                if (mCurrentFragment != LOG_OUT) {
                    mAuth.signOut();
                    checkUserStatus();
                }
                finish();
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkUserStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            uid= user.getUid();
        } else {
            startActivity(new Intent(LogProActivity.this, SignInActivity.class));
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