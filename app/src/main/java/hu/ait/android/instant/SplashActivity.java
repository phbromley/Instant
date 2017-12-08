package hu.ait.android.instant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private class SplashTimer extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    if (currentUser != null) {
                        startActivity(new Intent(SplashActivity.this, PostsActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }

                    finish();
                }
            });

        }
    }

    private Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final LinearLayout layoutContent = (LinearLayout) findViewById(R.id.layoutContent);
        final Animation anim = AnimationUtils.loadAnimation(
                SplashActivity.this, R.anim.show_anim);
        layoutContent.startAnimation(anim);
        firebaseAuth = FirebaseAuth.getInstance();

        timer = new Timer();
        timer.schedule(new SplashTimer(), 3000);


    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }
}