package com.example.tripawy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.StandardCharsets;

public class SignUpPage extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
{
    EditText fullName, email, password, confirmPass;
    Button register;
    SignInButton google_btn;
    DatabaseReference dbRef;
    GoogleApiClient googleApiClient;
    public static final int RC_SIGN_IN = 1;

   /* @Override
    protected void onStart() {
        super.onStart();
        // check if user has already signed in ..
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(this);
        Intent intent = new Intent(this, HomeScreen.class);
        startActivity(intent);

    }**/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        // set view references ..
        initComponent();

        // create firebaseDatabase instance ..
        FirebaseDatabase fireDB = FirebaseDatabase.getInstance();
        // get reference for our DB node ..
        dbRef = fireDB.getReference().child("users");

        // configure Google sign-in ..
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // create google API Client ..
        googleApiClient = new GoogleApiClient.Builder(this)
               .enableAutoManage(this, this)
               .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
               .build();

        // handle buttons ..
        register.setOnClickListener(v -> registerUser());

        google_btn.setOnClickListener(v -> googleSignIn());


    }


    private void initComponent()
    {
        fullName    = findViewById(R.id.fullName);
        email       = findViewById(R.id.email);
        password    = findViewById(R.id.password);
        confirmPass = findViewById(R.id.confirmPassword);
        register    = findViewById(R.id.register);
        google_btn  = findViewById(R.id.google_btn);
        google_btn.setSize(SignInButton.SIZE_STANDARD);
    }


    private void registerUser()
    {
        String fullName_Data = fullName.getText().toString();
        String email_Data = email.getText().toString();
        String password_Data = password.getText().toString();

        // check empty fields ..
        if(TextUtils.isEmpty(fullName.getText()) || TextUtils.isEmpty(email.getText()) ||
                TextUtils.isEmpty(password.getText()) || TextUtils.isEmpty(confirmPass.getText()))
        {
            Toast.makeText(getApplicationContext(), "Please, complete the form", Toast.LENGTH_LONG).show();
        }
        else{
            // check password & confirm password equality ..
            if(password.getText().toString().equals(confirmPass.getText().toString()))
            {
                // Register User:
                // create user object ..
                User user = new User(fullName_Data, email_Data, password_Data);
                // get email name before '@' ..
                String key = email_Data.substring(0, email_Data.indexOf("@"));
                // store data in firebase DB ..
                dbRef.child(key).setValue(user);
                dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                        startActivity(intent);
                        // finish activity after sign in ..
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }else{
                Toast.makeText(getApplicationContext(), "Password Doesn't match", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void googleSignIn()
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    // for result after sign in ..
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // get result from intent ..
        if(requestCode == RC_SIGN_IN)
        {
            assert data != null;
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            assert result != null;
            handleSignInResult(result);
        }

    }

    private void handleSignInResult(GoogleSignInResult result)
    {
        // handle result after signIn ..
        if(result.isSuccess())
        {
            GoogleSignInAccount account = result.getSignInAccount();
            assert account != null;
            Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
            startActivity(intent);

        }else{
            Toast.makeText(getApplicationContext(), "SignIn Failed, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}