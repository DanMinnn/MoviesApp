package com.movieapi.movie.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.movieapi.movie.R;
import com.movieapi.movie.databinding.ActivitySignInBinding;
import com.movieapi.movie.model.member.Member;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, FirebaseAuth.AuthStateListener {
    ActivitySignInBinding binding;
    FirebaseAuth mAuth;
    GoogleApiClient apiClient;
    private static final int REQ_SIGN_IN = 20;
    public static int CHECK_PROVIDER = 0;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        //mAuth.signOut();

        progressDialog = new ProgressDialog(this);

        CreateClientSignInGoogle();
        addEvents();

        sharedPreferences= getSharedPreferences("sessionUser", MODE_PRIVATE);
    }

    private void addEvents() {
        binding.btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    SignInGoogle(apiClient);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        binding.txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iSignUp = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(iSignUp);
            }
        });

        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = binding.edEmailSignIn.getText().toString();
                String password = binding.edPasswordSignIn.getText().toString();

                if(email.trim().length() == 0){
                    binding.txtEnterEmailSignIn.setVisibility(View.VISIBLE);
                }else
                    binding.txtEnterEmailSignIn.setVisibility(View.INVISIBLE);

                if (password.trim().length() == 0){
                    binding.txtEnterPasswordSignIn.setVisibility(View.VISIBLE);
                } else {
                    binding.txtEnterPasswordSignIn.setVisibility(View.INVISIBLE);
                }
                if(email.trim().length() != 0 && password.trim().length() != 0){
                    progressDialog.setMessage("Pending...");
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(SignInActivity.this, "Sign in success !", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(SignInActivity.this, "Sign in failed ! Please check your email or password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        binding.txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iReset = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                startActivity(iReset);
            }
        });
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

    private void SignInGoogle(GoogleApiClient apiClient){
        CHECK_PROVIDER = 1;
        Intent iGoogle = Auth.GoogleSignInApi.getSignInIntent(apiClient);
        startActivityForResult(iGoogle, REQ_SIGN_IN);
    }

    private void SignInCredential(String tokenID){
        if (CHECK_PROVIDER == 1){
            AuthCredential credential = GoogleAuthProvider.getCredential(tokenID, null);
            mAuth.signInWithCredential(credential);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SIGN_IN){
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
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("idUser", user.getUid());
            editor.putString("emailUser", user.getEmail());
            editor.commit();

            Intent iMain = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(iMain);
            Toast.makeText(this, "Sign in success !", Toast.LENGTH_SHORT).show();
        }
    }
}