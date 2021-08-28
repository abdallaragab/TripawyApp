package com.example.tripawy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.StandardCharsets;

public class SignUpPage extends AppCompatActivity
{
    EditText fullName, email, password, confirmPass;
    Button register;
    private DatabaseReference dbRef;

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

        register.setOnClickListener(v ->
        {
            registerUser();
        });


    }


    private void initComponent()
    {
        fullName    = findViewById(R.id.fullName);
        email       = findViewById(R.id.email);
        password    = findViewById(R.id.password);
        confirmPass = findViewById(R.id.confirmPassword);
        register    = findViewById(R.id.register);
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

            }else{
                Toast.makeText(getApplicationContext(), "Password Doesn't match", Toast.LENGTH_SHORT).show();
            }
        }
    }


    









}