package hu.ait.android.instant;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class BottomNavActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_feed:
                    mTextMessage.setText(R.string.feed);
                    return true;
                case R.id.navigation_post:
                    mTextMessage.setText(R.string.post);
                    return true;
                case R.id.navigation_profile:
                    mTextMessage.setText(R.string.profile);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
//                case FragmentGallery.TAG:
//                    newFragment = new FragmentGallery();
//                    break;
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

}
