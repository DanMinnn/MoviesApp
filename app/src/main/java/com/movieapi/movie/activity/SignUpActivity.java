package com.movieapi.movie.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.movieapi.movie.R;
import com.movieapi.movie.controller.SignUpController;
import com.movieapi.movie.databinding.ActivitySignInBinding;
import com.movieapi.movie.databinding.ActivitySignUpBinding;
import com.movieapi.movie.model.member.Member;

public class SignUpActivity extends AppCompatActivity{
    ActivitySignUpBinding binding;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    GoogleApiClient apiClient;
    String nameUser;
    SharedPreferences sharedPreferences;
    SignUpController signUpController;

    private static final int REQ_SIGN_UP = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        addEvents();
        sharedPreferences = getSharedPreferences("saveUser", MODE_PRIVATE);
    }

    private void addEvents() {
        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Pending...");
                progressDialog.show();

                nameUser = binding.edNameSignUp.getText().toString();
                String email = binding.edEmailSignUp.getText().toString();
                String password = binding.edPasswordSignUp.getText().toString();

                boolean isValid = true;

                if (nameUser.trim().length() == 0){
                    binding.txtEnterName.setVisibility(View.VISIBLE);
                    isValid = false;
                    progressDialog.dismiss();
                }else
                    binding.txtEnterName.setVisibility(View.INVISIBLE);

                if (email.trim().length() == 0) {
                    binding.txtEnterEmail.setVisibility(View.VISIBLE);
                    isValid = false;
                    progressDialog.dismiss();
                }else
                    binding.txtEnterEmail.setVisibility(View.INVISIBLE);

                if (password.trim().length() < 6) {
                    binding.txtEnterPass.setVisibility(View.GONE);
                    binding.txtErrorPassword.setVisibility(View.VISIBLE);
                    if (password.trim().length() == 0){
                        binding.txtEnterPass.setVisibility(View.VISIBLE);
                        binding.txtErrorPassword.setVisibility(View.GONE);
                    }
                    isValid = false;
                    progressDialog.dismiss();
                }else {
                    binding.txtErrorPassword.setVisibility(View.GONE);
                }

                /*if (password.trim().length() < 6){

                    isValid = false;
                    progressDialog.dismiss();
                }*/

                if (isValid){
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Member member = new Member();
                                member.setAvt("user.jpg");
                                member.setEmail(email);
                                member.setName(nameUser);
                                String uid = task.getResult().getUser().getUid();

                                signUpController = new SignUpController();
                                signUpController.InsertMemberController(member, uid);

                                Toast.makeText(SignUpActivity.this, "Sign up success !", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                                Toast.makeText(SignUpActivity.this, "Sign up fail ! Please check your information", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        binding.txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iSignIn = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(iSignIn);
            }
        });
    }
}