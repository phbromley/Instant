package hu.ait.android.instant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    /*  THINGS TO ACCOMPLISH:
    *  - have users enter more info in registration
    *       -- have registration button take you to new activity to enter in info and from there
    *           enter the feed and login
    *
    *           DONE -> picture and bio can be edited from profile settings
    *
    *  - setup Profile fragment
    *       -- have picture ? full name and bio at the top, then recycler view underneath of
    *           all their posted photos
    *             --- this can probably be most easily achieved by using special querying as can
    *                   be explained in https://firebase.google.com/docs/database/admin/retrieve-data#section-complex-queries
    *
    *           DONE -> querying is all that is necessary
    *
    *       -- build in followers and following
    *             --- find way to store lists of user UID's and then can just make a simple fragment
    *                  containing recyclerview of users where each row take you to their profile page
    *                   ---- things currently needing to be done:
    *                       > all the things I've aggressively commented in FragmentProfile,
    *                           FragmentFollow and FollowAdapter
    *
    *  - make feed only users you follow and yourself
    *       -- can be attained by selective querying loop in similar fashion to profile
    *
    *  - implement search ? make the middle tab in the bottom navigation view search fragment
    *       -- not really important for demo but if we have time
    *
    *  - make it follow its original purpose of only allowing posting in a certain gap of time each day
    *
    *  - look into every activity transition and make sure the back stack is maintained properly
    *       -- example: cannot hit back button to go back to login, must logout and settings saving
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.btnRegister)
    public void registerClick() {
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
    }

    @OnClick(R.id.btnLogin)
    public void loginClick() {
        if (!isFormValid()) {
            return;
        }

        showProgressDialog();

        firebaseAuth.signInWithEmailAndPassword(
                etEmail.getText().toString(),
                etPassword.getText().toString()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();

                if (task.isSuccessful()) {
                    startActivity(new Intent(LoginActivity.this,
                            BottomNavActivity.class));

                } else {
                    Toast.makeText(LoginActivity.this,
                            "Error: "+task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this,
                        "Error: "+e.getMessage(),
                        Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }
        });
    }


    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Wait for it...");
        }

        progressDialog.show();
    }


    private boolean isFormValid() {
        if (TextUtils.isEmpty(etEmail.getText())) {
            etEmail.setError("The email can not be empty");
            return false;
        }

        if (TextUtils.isEmpty(etPassword.getText())) {
            etPassword.setError("The password can not be empty");
            return false;
        }

        return true;
    }

}
