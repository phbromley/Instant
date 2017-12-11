package hu.ait.android.instant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

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
                    showFragment(FragmentFeed.TAG);
                    return true;
                case R.id.navigation_profile:
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

        ButterKnife.bind(this);

        mBottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        showFragment(FragmentFeed.TAG);
    }

    public void showFragment(String fragmentTag) {
        // try to find the fragment
        Fragment newFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);

        // create if it was not found
        if (newFragment == null) {
            switch (fragmentTag) {
                case FragmentFeed.TAG:
                    newFragment = new FragmentFeed();
                    break;
                case FragmentProfile.TAG:
                    newFragment = new FragmentProfile();
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
        Intent intent = new Intent(BottomNavActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
