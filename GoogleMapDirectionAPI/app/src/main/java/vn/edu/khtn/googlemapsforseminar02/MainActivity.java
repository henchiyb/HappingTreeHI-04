package vn.edu.khtn.googlemapsforseminar02;


import android.content.DialogInterface;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Fragment curentFragment;
    private MapFragment mapFragment;
    private boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mapFragment = new MapFragment();
        openFragment(mapFragment, false);
    }

    private void openFragment(Fragment fragment, boolean addToBackStack){
        curentFragment = fragment;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_out_down, 0, 0, R.anim.slide_in_up);
        if (addToBackStack) {
            fragmentTransaction.replace(R.id.container, fragment)
                    .addToBackStack(fragment.getClass().getName())
                    .commit();
        }else {
            fragmentTransaction.replace(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Ấn back lần nữa để thoát", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
         if (id == R.id.nav_map) {
             if (!(curentFragment instanceof MapFragment)) {
                 openFragment(mapFragment, false);
             }
        } else if (id == R.id.nav_history) {
             if (!(curentFragment instanceof HistoryFragment)) {
                 Fragment fragmentHistory = new HistoryFragment();
                 openFragment(fragmentHistory, false);
             }

        } else if (id == R.id.nav_setting) {
             if (!(curentFragment instanceof SettingFragment)) {
                 Fragment fragmentSetting = new SettingFragment();
                 openFragment(fragmentSetting, false);
             }

         } else if (id == R.id.nav_log_out) {
             AlertDialog.Builder builder = new AlertDialog.Builder(this);
             builder.setTitle("Log out");
             builder.setIcon(R.drawable.ic_clear_black_24dp);
             builder.setMessage("Bạn có muốn đăng xuất ?");
             builder.setCancelable(false);
             builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialogInterface, int i) {
                     Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                     startActivity(intent);
                 }
             });
             builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialogInterface, int i) {
                     dialogInterface.dismiss();
                 }
             });
             AlertDialog alertDialog = builder.create();
             alertDialog.show();
         }
        item.setChecked(true);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
