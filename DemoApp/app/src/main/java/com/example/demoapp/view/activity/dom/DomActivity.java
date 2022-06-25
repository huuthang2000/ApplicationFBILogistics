//package com.example.demoapp.view.activity.dom;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.ActionBarDrawerToggle;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.view.GravityCompat;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentTransaction;
//
//import com.example.demoapp.R;
//import com.example.demoapp.databinding.ActivityDomBinding;
//import com.example.demoapp.view.activity.LoginActivity;
//import com.example.demoapp.view.activity.chat.DashboardActivity;
//import com.example.demoapp.view.driver.UISplashScreen;
//import com.example.demoapp.view.fragment.dom.DomColdFragment;
//import com.example.demoapp.view.fragment.dom.DomCyFragment;
//import com.example.demoapp.view.fragment.dom.DomCySeaFragment;
//import com.example.demoapp.view.fragment.dom.DomDoorFragment;
//import com.example.demoapp.view.fragment.dom.DomDoorSeaFragment;
//import com.example.demoapp.view.fragment.dom.DomDryFragment;
//import com.example.demoapp.view.fragment.dom.DomExportFragment;
//import com.example.demoapp.view.fragment.dom.DomImportFragment;
//import com.example.demoapp.view.fragment.home.HomeFragment;
//import com.google.android.material.navigation.NavigationView;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//
//public class DomActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
//    private ActivityDomBinding binding;
//
//    private static final int FRAGMENT_HOME = 0;
//    private static final int FRAGMENT_DOM_EXPORT = 1;
//    private static final int FRAGMENT_DOM_IMPORT = 2;
//    private static final int FRAGMENT_DOM_DRY = 3;
//    private static final int FRAGMENT_DOM_COLD = 4;
//    private static final int FRAGMENT_DOM_CY= 5;
//    private static final int FRAGMENT_DOM_CY_SEA = 6;
//    private static final int FRAGMENT_DOM_DOOR= 7;
//    private static final int FRAGMENT_DOM_DOOR_SEA = 8;
//    private static final int ACTIVITY_MESSAGE = 9;
//    private static final int ACTIVITY_DRIVER = 10;
//    private static final int LOG_OUT = 11;
//    private PreferenceManager preferenceManager;
//    private FirebaseAuth mAuth;
//    private int mCurrentFragment = FRAGMENT_HOME;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityDomBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        setSupportActionBar(binding.toolbar);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar,
//                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        binding.drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//        binding.navigationView.setNavigationItemSelectedListener(this);
//        mAuth = FirebaseAuth.getInstance();
//        replaceFragment(new HomeFragment());
//        binding.navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
//       // preferenceManager = new PreferenceManager(this);
//        initHearderView();
//    }
//
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.nav_home:
//                if (mCurrentFragment != FRAGMENT_HOME) {
//                    replaceFragment(new HomeFragment());
//                    mCurrentFragment = FRAGMENT_HOME;
//                    binding.toolbar.setTitle("Home");
//                }
//                break;
//            case R.id.nav_dom_export:
//                if (mCurrentFragment != FRAGMENT_DOM_EXPORT ){
//                    replaceFragment(new DomExportFragment());
//                    mCurrentFragment = FRAGMENT_DOM_EXPORT;
//                    binding.toolbar.setTitle("Dom Export");
//                }
//                break;
//            case R.id.nav_dom_import:
//                if (mCurrentFragment != FRAGMENT_DOM_IMPORT ){
//                    replaceFragment(new DomImportFragment());
//                    mCurrentFragment = FRAGMENT_DOM_IMPORT;
//                    binding.toolbar.setTitle("Dom Import");
//                }
//                break;
//            case R.id.nav_dom_dry:
//                if (mCurrentFragment != FRAGMENT_DOM_DRY ){
//                    replaceFragment(new DomDryFragment());
//                    mCurrentFragment = FRAGMENT_DOM_DRY;
//                    binding.toolbar.setTitle("Dom Dry");
//                }
//                break;
//            case R.id.nav_dom_cold:
//                if (mCurrentFragment != FRAGMENT_DOM_COLD ){
//                    replaceFragment(new DomColdFragment());
//                    mCurrentFragment = FRAGMENT_DOM_COLD;
//                    binding.toolbar.setTitle("Dom Cold");
//                }
//                break;
//            case R.id.nav_dom_cy:
//                if (mCurrentFragment != FRAGMENT_DOM_CY ){
//                    replaceFragment(new DomCyFragment());
//                    mCurrentFragment = FRAGMENT_DOM_CY;
//                    binding.toolbar.setTitle("Dom Cy");
//                }
//                break;
//            case R.id.nav_dom_door:
//                if (mCurrentFragment != FRAGMENT_DOM_DOOR ){
//                    replaceFragment(new DomDoorFragment());
//                    mCurrentFragment = FRAGMENT_DOM_DOOR;
//                    binding.toolbar.setTitle("Dom Door");
//                }
//                break;
//            case R.id.nav_dom_cy_sea:
//                if (mCurrentFragment != FRAGMENT_DOM_CY_SEA ){
//                    replaceFragment(new DomCySeaFragment());
//                    mCurrentFragment = FRAGMENT_DOM_CY_SEA;
//                    binding.toolbar.setTitle("Dom Cy Sea");
//                }
//                break;
//            case R.id.nav_dom_door_sea:
//                if (mCurrentFragment != FRAGMENT_DOM_DOOR_SEA){
//                    replaceFragment(new DomDoorSeaFragment());
//                    mCurrentFragment = FRAGMENT_DOM_DOOR_SEA;
//                    binding.toolbar.setTitle("Dom Door Sea");
//                }
//                break;
//            case R.id.nav_message:
////                if (mCurrentFragment != ACTIVITY_MESSAGE) {
////                    Intent intent = new Intent(this, MainMessageActivity.class);
////                    startActivity(intent);
//                startActivity(new Intent(this, DashboardActivity.class));
////                }
//                break;
//            case R.id.nav_Trucktracking:
//                if (mCurrentFragment != ACTIVITY_DRIVER) {
//                    Intent intent = new Intent(this, UISplashScreen.class);
//                    startActivity(intent);
//                }
//                break;
//            case R.id.nav_logout:
//                if (mCurrentFragment != LOG_OUT) {
//                    mAuth.signOut();
//                    checkUserStatus();
//                }
//                finish();
//        }
//        binding.drawerLayout.closeDrawer(GravityCompat.START);
//        return true;
//    }
//
//    private void replaceFragment(Fragment fragment) {
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.content_frame, fragment);
//        transaction.commit();
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            binding.drawerLayout.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//
//    }
//    private void checkUserStatus() {
//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user != null) {
//
//        } else {
//            startActivity(new Intent(DomActivity.this, LoginActivity.class));
//            finish();
//        }
//    }
//   private void signOut() {
////       mAuth.signOut();
////       checkUserStatus();
////        showToast("Sign out...");
////        FirebaseFirestore database = FirebaseFirestore.getInstance();
////        DocumentReference documentReference =
////                database.collection(Constants.KEY_COLLECTION_USERS).document(
////                        preferenceManager.getString(Constants.KEY_USER_ID)
////                );
////        HashMap<String, Object> update = new HashMap<>();
////        update.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
////        documentReference.update(update)
////                .addOnSuccessListener(undates -> {
////                    preferenceManager.clear();
////                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
////                    finish();
////                })
////                .addOnFailureListener(e -> showToast("Unable to sign out"));
//   }
//    private void showToast(String message) {
//        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//    }
//
//    private void initHearderView(){
//        View header = binding.navigationView.getHeaderView(0);
//        TextView tvName = header.findViewById(R.id.tv_name_header);
//        ImageView imageView = header.findViewById(R.id.image_header);
////        tvName.setText(preferenceManager.getClass(Constants.KEY_NAME));
////        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
//    //    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//     //   imageView.setImageBitmap(bitmap);
//    }
//
//}
package com.example.demoapp.view.activity.dom;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
<<<<<<< HEAD
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
=======
import androidx.core.view.GravityCompat;
>>>>>>> fix_message
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.demoapp.R;
<<<<<<< HEAD
import com.example.demoapp.view.activity.LoginActivity;
import com.example.demoapp.view.activity.chat.DashboardActivity;
import com.example.demoapp.view.driver.UISplashScreen;
=======
import com.example.demoapp.databinding.ActivityDomBinding;
import com.example.demoapp.view.activity.LoginActivity;
import com.example.demoapp.view.activity.chat.DashboardActivity;
>>>>>>> fix_message
import com.example.demoapp.view.fragment.dom.DomColdFragment;
import com.example.demoapp.view.fragment.dom.DomCyFragment;
import com.example.demoapp.view.fragment.dom.DomCySeaFragment;
import com.example.demoapp.view.fragment.dom.DomDoorFragment;
import com.example.demoapp.view.fragment.dom.DomDoorSeaFragment;
import com.example.demoapp.view.fragment.dom.DomDryFragment;
import com.example.demoapp.view.fragment.dom.DomExportFragment;
import com.example.demoapp.view.fragment.dom.DomImportFragment;
import com.example.demoapp.view.fragment.home.HomeFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


<<<<<<< HEAD
public class DomActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


=======
public class DomActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    private ActivityDomBinding binding;
>>>>>>> fix_message
    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_DOM_EXPORT = 1;
    private static final int FRAGMENT_DOM_IMPORT = 2;
    private static final int FRAGMENT_DOM_DRY = 3;
    private static final int FRAGMENT_DOM_COLD = 4;
<<<<<<< HEAD
    private static final int FRAGMENT_DOM_CY= 5;
    private static final int FRAGMENT_DOM_CY_SEA = 6;
    private static final int FRAGMENT_DOM_DOOR= 7;
    private static final int FRAGMENT_DOM_DOOR_SEA = 8;
    private static final int ACTIVITY_MESSAGE = 9;
    private static final int ACTIVITY_DRIVER = 10;
    private static final int LOG_OUT = 11;

    private int mCurrentFragment = FRAGMENT_HOME;
    NavigationView navigationView;
    DrawerLayout mDrawerLayout;
    Toolbar toolbar;
    private FirebaseAuth mAuth;
=======
    private static final int FRAGMENT_DOM_CY = 5;
    private static final int FRAGMENT_DOM_DOOR= 6;
    private static final int FRAGMENT_DOM_CY_SEA = 7;
    private static final int FRAGMENT_DOM_DOOR_SEA = 8;
    private static final int ACTIVITY_MESSAGE = 9;
    private static final int LOG_OUT = 10;

    private FirebaseAuth mAuth;

    private int mCurrentFragment = FRAGMENT_HOME;
>>>>>>> fix_message

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dom);

        initView();
        Actionbar();
        mAuth = FirebaseAuth.getInstance();
    }
    private void Actionbar() {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar
                , R.string.nav_drawer_open, R.string.nav_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

         navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // xử lí mặc định vào trang home
        replaceFragment(new HomeFragment());
        navigationView.getMenu().findItem(R.id.nav_homee).setCheckable(true);
    }

    public void initView() {
<<<<<<< HEAD


        toolbar = findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.


=======
        setSupportActionBar(binding.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        binding.navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();

        replaceFragment(new HomeFragment());
        binding.navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
>>>>>>> fix_message
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.dom, menu);
//        return true;
//    }
    private void checkUserStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {

        } else {
            startActivity(new Intent(DomActivity.this, LoginActivity.class));
            finish();
        }
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.dom, menu);
//        return true;
//    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
<<<<<<< HEAD
            case R.id.nav_homee:
                if (mCurrentFragment != FRAGMENT_HOME) {
                    replaceFragment(new HomeFragment());
                    mCurrentFragment = FRAGMENT_HOME;
                    toolbar.setTitle("Home");
                }
                break;
            case R.id.nav_dom_export:
                if (mCurrentFragment != FRAGMENT_DOM_EXPORT ){
                    replaceFragment(new DomExportFragment());
                    mCurrentFragment = FRAGMENT_DOM_EXPORT;
                    toolbar.setTitle("Dom Export");
                }
                break;
            case R.id.nav_dom_import:
                if (mCurrentFragment != FRAGMENT_DOM_IMPORT ){
                    replaceFragment(new DomImportFragment());
                    mCurrentFragment = FRAGMENT_DOM_IMPORT;
                    toolbar.setTitle("Dom Import");
                }
                break;
            case R.id.nav_dom_dry:
                if (mCurrentFragment != FRAGMENT_DOM_DRY ){
                    replaceFragment(new DomDryFragment());
                    mCurrentFragment = FRAGMENT_DOM_DRY;
                   toolbar.setTitle("Dom Dry");
                }
                break;
            case R.id.nav_dom_cold:
                if (mCurrentFragment != FRAGMENT_DOM_COLD ){
                    replaceFragment(new DomColdFragment());
                    mCurrentFragment = FRAGMENT_DOM_COLD;
                   toolbar.setTitle("Dom Cold");
                }
                break;
            case R.id.nav_dom_cy:
                if (mCurrentFragment != FRAGMENT_DOM_CY ){
                    replaceFragment(new DomCyFragment());
                    mCurrentFragment = FRAGMENT_DOM_CY;
                    toolbar.setTitle("Dom Cy");
                }
                break;
            case R.id.nav_dom_door:
                if (mCurrentFragment != FRAGMENT_DOM_DOOR ){
                    replaceFragment(new DomDoorFragment());
                    mCurrentFragment = FRAGMENT_DOM_DOOR;
                   toolbar.setTitle("Dom Door");
                }
                break;
            case R.id.nav_dom_cy_sea:
                if (mCurrentFragment != FRAGMENT_DOM_CY_SEA ){
                    replaceFragment(new DomCySeaFragment());
                    mCurrentFragment = FRAGMENT_DOM_CY_SEA;
                    toolbar.setTitle("Dom Cy Sea");
                }
                break;
            case R.id.nav_dom_door_sea:
                if (mCurrentFragment != FRAGMENT_DOM_DOOR_SEA){
                    replaceFragment(new DomDoorSeaFragment());
                    mCurrentFragment = FRAGMENT_DOM_DOOR_SEA;
                    toolbar.setTitle("Dom Door Sea");
                }
                break;
            case R.id.nav_message:
=======
            case R.id.nav_home:
                if (mCurrentFragment != FRAGMENT_HOME) {
                    replaceFragment(new HomeFragment());
                    mCurrentFragment = FRAGMENT_HOME;
                    binding.toolbar.setTitle("Home");
                }
                break;
            case R.id.nav_dom_export:
                if (mCurrentFragment != FRAGMENT_DOM_EXPORT) {
                    replaceFragment(new DomExportFragment());
                    mCurrentFragment = FRAGMENT_DOM_EXPORT;
                    binding.toolbar.setTitle("DOM EXPORT");
                }
                break;
            case R.id.nav_dom_import:
                if (mCurrentFragment != FRAGMENT_DOM_IMPORT) {
                    replaceFragment(new DomImportFragment());
                    mCurrentFragment = FRAGMENT_DOM_IMPORT;
                    binding.toolbar.setTitle("DOM IMPORT");
                }
                break;
            case R.id.nav_dom_dry:
                if (mCurrentFragment != FRAGMENT_DOM_DRY) {
                    replaceFragment(new DomDryFragment());
                    mCurrentFragment = FRAGMENT_DOM_DRY;
                    binding.toolbar.setTitle("DOM DRY");
                }
                break;
            case R.id.nav_dom_cold:
                if (mCurrentFragment != FRAGMENT_DOM_COLD) {
                    replaceFragment(new DomColdFragment());
                    mCurrentFragment = FRAGMENT_DOM_COLD;
                    binding.toolbar.setTitle("DOM COLD");
                }
                break;
            case R.id.nav_dom_cy:
                if (mCurrentFragment != FRAGMENT_DOM_CY) {
                    replaceFragment(new DomCyFragment());
                    mCurrentFragment = FRAGMENT_DOM_CY;
                    binding.toolbar.setTitle("DOM CY");
                }
                break;
            case R.id.nav_dom_door:
                if (mCurrentFragment != FRAGMENT_DOM_DOOR) {
                    replaceFragment(new DomDoorFragment());
                    mCurrentFragment = FRAGMENT_DOM_DOOR;
                    binding.toolbar.setTitle("DOM DOOR");
                }
                break;
            case R.id.nav_dom_cy_sea:
                if (mCurrentFragment != FRAGMENT_DOM_CY_SEA) {
                    replaceFragment(new DomCySeaFragment());
                    mCurrentFragment = FRAGMENT_DOM_CY_SEA;
                    binding.toolbar.setTitle("DOM CY SEA");
                }
                break;
            case R.id.nav_dom_door_sea:
                if (mCurrentFragment != FRAGMENT_DOM_DOOR_SEA) {
                    replaceFragment(new DomDoorSeaFragment());
                    mCurrentFragment = FRAGMENT_DOM_DOOR_SEA;
                    binding.toolbar.setTitle("DOM DOOR SEA");
                }
                break;
            case R.id.nav_dom_message:
>>>>>>> fix_message
                if (mCurrentFragment != ACTIVITY_MESSAGE) {
                    Intent intent = new Intent(this, DashboardActivity.class);
                    startActivity(intent);
                }
                break;
<<<<<<< HEAD
            case R.id.nav_Trucktracking:
                if (mCurrentFragment != ACTIVITY_DRIVER) {
                    Intent intent = new Intent(this, UISplashScreen.class);
                    startActivity(intent);
                }
                break;
            case R.id.nav_logout:
=======
            case R.id.nav_dom_logout:
>>>>>>> fix_message
                if (mCurrentFragment != LOG_OUT) {
                    mAuth.signOut();
                    checkUserStatus();
                }
                finish();
        }
<<<<<<< HEAD
       mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_framedom, fragment);
        transaction.commit();
=======
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkUserStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {

        } else {
            startActivity(new Intent(DomActivity.this, LoginActivity.class));
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

>>>>>>> fix_message
    }
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }


}  private void initHearderView(){
//        View header = navigationView.getHeaderView(0);
//        TextView tvName = header.findViewById(R.id.tv_name_header);
//        ImageView imageView = header.findViewById(R.id.image_header);
//        tvName.setText(preferenceManager.getString(Constants.KEY_NAME));
//        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
//        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//        imageView.setImageBitmap(bitmap);
    }
}
