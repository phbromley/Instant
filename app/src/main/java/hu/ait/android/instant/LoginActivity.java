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
    *  - have users enter more info in registration (Full name, maybe picture and bio ?)
    *       -- have registration button take you to new activity to enter in info and from there
    *           enter the feed and login
    *  - setup Profile fragment
    *       -- have picture ? full name and bio at the top, then recycler view underneath of
    *           all their posted photos
    *             --- this can probably be most easily achieved by altering the PostsAdapter to only
    *                  display posts with your UID - adding in this functionality is necessary bc
    *                  your feed is only supposed to be people you follow anyway
    *       -- build in followers and following ?
    *             --- find way to store lists of user UID's and then can just make a simple fragment
    *                  containing recyclerview of users where each row take you to their profile page
    *  - make feed only users you follow and yourself
    *  - implement search ? make the middle tab in the bottom navigation view search fragment
    *  - make it follow its original purpose of only allowing posting in a certain gap of time each day
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.btnRegister)
    void registerClick() {
        if (!isFormValid()) {
            return;
        }

        showProgressDialog();

        firebaseAuth.createUserWithEmailAndPassword(etEmail.getText().toString(),
                etPassword.getText().toString()).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            FirebaseUser fbUser = task.getResult().getUser();

                            fbUser.updateProfile(
                                    new UserProfileChangeRequest.Builder().
                                            setDisplayName(usernameFromEmail(fbUser.getEmail())).build()
                            );


                            Toast.makeText(LoginActivity.this,
                                    "Registration ok", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Error: "+
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        login();
    }

    @OnClick(R.id.btnLogin)
    void loginClick() {
        login();
    }

    private void login() {
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


    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

}
