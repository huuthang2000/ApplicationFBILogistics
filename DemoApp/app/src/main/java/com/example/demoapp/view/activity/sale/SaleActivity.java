package com.example.demoapp.view.activity.sale;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.demoapp.R;
import com.example.demoapp.view.activity.chat.DashboardActivity;
import com.example.demoapp.view.activity.loginAndRegister.SignInActivity;
import com.example.demoapp.view.fragment.sales.AirlinesSaleFragment;
import com.example.demoapp.view.fragment.sales.HomeSaleFragment;
import com.example.demoapp.view.fragment.sales.InlandFragment;
import com.example.demoapp.view.fragment.sales.RoadFragment;
import com.example.demoapp.view.fragment.sales.SeawayFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SaleActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_SEAWAY = 1;
    private static final int FRAGMENT_AIRLINES = 2;
    private static final int FRAGMENT_ROAD = 3;
    private static final int FRAGMENT_INLAND = 4;
    private static final  int FRAGMENT_OVERSEA = 5;
    private static final int FRAGMENT_TYPE_CONTAINER = 6;
    private static final int FRAGMENT_LUUKHO = 7;
    private static final int FRAGMENT_HAIQUAN = 8;
    private static final int FRAGMENT_BAOHIEM = 9;
    private static final int FRAGMENT_NVOCC = 10;
    private static final  int FRAGMENT_DEPOT = 11;
    private int mCurrentFragmet = FRAGMENT_HOME;

    DrawerLayout mDrawerLayout;
    Toolbar toolbar;
    private FirebaseAuth mAuth;
    private String uid;
    TextView tvName, tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);
        mAuth = FirebaseAuth.getInstance();
        checkUserStatus();
        anhxa();
        Actionbar();




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


    private void anhxa() {
        toolbar = findViewById(R.id.tb_menu);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    private void Actionbar() {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar
                , R.string.nav_drawer_open, R.string.nav_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        setUserName();
        NavigationView navigationView = findViewById(R.id.navigation_view);
        View nav_header = LayoutInflater.from(SaleActivity.this).inflate(R.layout.layout_header_nav, null);
        tvName = nav_header.findViewById(R.id.tv_name_header);
        tvEmail = nav_header.findViewById(R.id.tv_email_header);
        navigationView.addHeaderView(nav_header);
        navigationView.setNavigationItemSelectedListener(this);
        // xử lí mặc định vào trang home
        replaceFragment(new HomeSaleFragment());
        navigationView.getMenu().findItem(R.id.nav_home).setCheckable(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            if (mCurrentFragmet != FRAGMENT_HOME) {
                replaceFragment(new HomeSaleFragment());
                mCurrentFragmet = FRAGMENT_HOME;
            }
        } else if (id == R.id.nav_duongbien) {
            if (mCurrentFragmet != FRAGMENT_SEAWAY) {
                replaceFragment(new SeawayFragment());
                mCurrentFragmet = FRAGMENT_SEAWAY;
            }
        } else if (id == R.id.nav_hangkhong) {
            if (mCurrentFragmet != FRAGMENT_AIRLINES) {
                replaceFragment(new AirlinesSaleFragment());
                mCurrentFragmet = FRAGMENT_AIRLINES;
            }
        } else if (id == R.id.nav_duongbo) {
            if (mCurrentFragmet != FRAGMENT_ROAD) {
                replaceFragment(new RoadFragment());
                mCurrentFragmet = FRAGMENT_ROAD;
            }
        } else if (id == R.id.nav_noidia) {
            if (mCurrentFragmet != FRAGMENT_INLAND) {
                replaceFragment(new InlandFragment());
                mCurrentFragmet = FRAGMENT_INLAND;
            }
        }else  if(id == R.id.nav_haiquan){
            if(mCurrentFragmet != FRAGMENT_HAIQUAN){
                Intent intent = new Intent(this, LogActivity.class);
                startActivity(intent);
            }
        }else if(id == R.id.nav_logout){
            mAuth.signOut();
            checkUserStatus();
        }else if(id == R.id.nav_message){
            startActivity(new Intent(this, DashboardActivity.class));
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkUserStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        } else {
            startActivity(new Intent(SaleActivity.this, SignInActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // sử lý fragement
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }


}