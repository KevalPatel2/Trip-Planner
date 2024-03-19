package com.example.tripplanner;

import  android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;

import com.example.tripplanner.databinding.ActivitySignupBinding;

public class SignUpActivity extends ComponentActivity
{
    ActivitySignupBinding binding;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        binding.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.signupEmail.getText().toString();
                String username = binding.signupUsername.getText().toString();
                String password = binding.signupPassword.getText().toString();
                String confirmPassword = binding.confirmPassword.getText().toString();

                if(email.equals("") || username.equals("") || password.equals("") || confirmPassword.equals(""))
                {
                    Toast.makeText(SignUpActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
                } else {
                    if (password.equals(confirmPassword)){
                        Boolean checkUserEmail = databaseHelper.checkEmail(email);

                        if (!checkUserEmail)
                        {
                            Boolean insert = databaseHelper.insertData(email, username, password);

                            if(insert)
                            {
                                Toast.makeText(SignUpActivity.this, "Signup succesful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), SuccessActivity.class);
                                intent.putExtra("email", email);
                                intent.putExtra("username", username);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(SignUpActivity.this, "Signup Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(SignUpActivity.this, "User already exists, Please Login", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(SignUpActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
