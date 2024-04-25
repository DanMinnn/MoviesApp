package com.movieapi.movie.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.movieapi.movie.R;
import com.movieapi.movie.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {
    ActivityForgotPasswordBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        addEvents();
    }

    private void addEvents() {
        binding.btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Pending...");
                progressDialog.show();

                String email = binding.edEmailForgot.getText().toString();
                if(email.trim().length() == 0) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                }
                if (CheckEmail(email)){
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(ForgotPasswordActivity.this, "Please check your email !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(ForgotPasswordActivity.this, "Invalid email !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.txtSignUpForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iSignUp = new Intent(ForgotPasswordActivity.this, SignUpActivity.class);
                startActivity(iSignUp);
            }
        });
    }

    private boolean CheckEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}