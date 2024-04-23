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

public class SignUpActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, FirebaseAuth.AuthStateListener{
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

        CreateClientSignInGoogle();

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

                if (nameUser.trim().length() == 0){
                    binding.txtEnterName.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
                else if (email.trim().length() == 0) {
                    binding.txtEnterEmail.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }else if (password.trim().length() == 0) {
                    binding.txtEnterPass.setVisibility(View.VISIBLE);
                    binding.txtErrorPassword.setVisibility(View.GONE);
                    progressDialog.dismiss();
                } else if (password.trim().length() < 6){
                    binding.txtEnterPass.setVisibility(View.GONE);
                    binding.txtErrorPassword.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                } else {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Member member = new Member();
                                member.setAvt("user.png");
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
                                Toast.makeText(SignUpActivity.this, "Sign up fail !", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        binding.btnGoogleSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInGoogle(apiClient);
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

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(this);
    }

    private void CreateClientSignInGoogle() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder()
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }

    private void SignInGoogle(GoogleApiClient apiClient){
        Intent iGoogle = Auth.GoogleSignInApi.getSignInIntent(apiClient);
        startActivityForResult(iGoogle, REQ_SIGN_UP);
    }

    private void SignInCredential(String tokenID){
            AuthCredential credential = GoogleAuthProvider.getCredential(tokenID, null);
            mAuth.signInWithCredential(credential);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SIGN_UP){
            if (resultCode == RESULT_OK){
                GoogleSignInResult signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                GoogleSignInAccount account = signInResult.getSignInAccount();
                String tokenID = account.getIdToken();
                SignInCredential(tokenID);
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            Intent iMain = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(iMain);
            Toast.makeText(this, "Sign in success !", Toast.LENGTH_SHORT).show();
        }
    }
}