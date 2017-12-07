package hu.ait.android.instant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    /*
    * this is going to be a splash screen where it checks using SharedPreferences
    *   if the person is logged in or not
    *
    *   if logged in, transitions after 3 seconds to feed
    *
    *   if not logged in, transitions after 3 seconds to login page
    *
    *   @Peter is going to do this and then setup the login activity
    *   - also throw in an animation on splash screen that has the Instant logo pls
    *
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnTest = (Button) findViewById(R.id.btnTest);
    }
}
