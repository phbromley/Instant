package hu.ait.android.instant;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import hu.ait.android.instant.data.DataManager;

public class BottomNavActivity extends AppCompatActivity {

    @BindView(R.id.navigation)
    BottomNavigationView mBottomNav;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_feed:
                    showFragment(FragmentFeed.TAG);
                    return true;
                case R.id.navigation_post:
                    startActivity(new Intent(BottomNavActivity.this,
                            CreatePostActivity.class));
                    return true;
                case R.id.navigation_profile:
                    DataManager.getInstance().setData(
                            FirebaseAuth.getInstance().getCurrentUser().getUid()
                    );
                    showFragment(FragmentProfile.TAG);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        ButterKnife.bind(this);

        mBottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        showFragment(FragmentFeed.TAG);

        //DataManager.getInstance();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        requestNeededPermission();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 45);
        calendar.set(Calendar.SECOND, 0);
        Intent intent1 = new Intent(BottomNavActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) BottomNavActivity.this.getSystemService(BottomNavActivity.this.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public void showFragment(String fragmentTag) {
        // try to find the fragment
        Fragment newFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);

        // could check if currentUser is up and then not make it null i guess
        if(fragmentTag.equals(FragmentProfile.TAG))
            newFragment = null;

        // create if it was not found
        if (newFragment == null) {
            switch (fragmentTag) {
                case FragmentFeed.TAG:
                    newFragment = new FragmentFeed();
                    break;
                case FragmentProfile.TAG:
                    newFragment = new FragmentProfile();
                    break;
                case FragmentSettings.TAG:
                    newFragment = new FragmentSettings();
                    break;
                case FragmentFollow.TAG:
                    newFragment = new FragmentFollow();
                    break;
                default:
                    newFragment = new FragmentFeed();
                    break;
            }
        }

        // display the fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.layoutContainer, newFragment, fragmentTag);
        ft.addToBackStack("stack");
        ft.commit();
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        DataManager.signOut();
        Intent intent = new Intent(BottomNavActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                            android.Manifest.permission.CAMERA},
                    101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "Permission must be granted",
                        Toast.LENGTH_SHORT).show();
                requestNeededPermission();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(BottomNavActivity.this, CreatePostActivity.class);
        startActivity(intent);
        return true;
    }
}
