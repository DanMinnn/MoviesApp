
package com.movieapi.movie.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
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
import com.movieapi.movie.R;
import com.movieapi.movie.database.SessionManager;
import com.movieapi.movie.databinding.ActivitySignInBinding;

import java.util.HashMap;

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
        mAuth.signOut();

        progressDialog = new ProgressDialog(this);

        CreateClientSignInGoogle();
        addEvents();

        sharedPreferences= getSharedPreferences("sessionUser", MODE_PRIVATE);

        SessionManager sessionManager = new SessionManager(SignInActivity.this, SessionManager.SESSION_REMEMBERME);

        if (sessionManager.checkRememberMe()){
            HashMap<String, String> rememberDetails = sessionManager.getRememberMeFromSession();
            binding.edEmailSignIn.setText(rememberDetails.get(SessionManager.KEY_EMAIL));
            binding.edPasswordSignIn.setText(rememberDetails.get(SessionManager.KEY_PASSWORD));
        }
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

        binding.checkboxRememberme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (binding.checkboxRememberme.isChecked()){
                        String email = binding.edEmailSignIn.getText().toString();
                        String password = binding.edPasswordSignIn.getText().toString();

                        SessionManager sessionManager = new SessionManager(SignInActivity.this, SessionManager.SESSION_REMEMBERME);
                        sessionManager.createRememberMeSession(email, password);
                    }
                }
            }
        });
        ShowHidePass();
    }

    private void ShowHidePass() {
        binding.imvShowpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.edPasswordSignIn.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                    //show pass
                    binding.imvShowpass.setImageResource(R.drawable.visible_password);
                    binding.edPasswordSignIn.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    //hide pass
                    binding.imvShowpass.setImageResource(R.drawable.hide_password);
                    binding.edPasswordSignIn.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
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

/*
package com.movieapi.movie.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ApiException;
import com.movieapi.movie.R;
import com.movieapi.movie.database.SessionManager;
import com.movieapi.movie.databinding.ActivitySignInBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {

    private static final int REQ_SIGN_IN = 20;
    private ActivitySignInBinding binding;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        progressDialog = new ProgressDialog(this);
        sharedPreferences = getSharedPreferences("sessionUser", MODE_PRIVATE);

        setupGoogleSignIn();
        setupEventListeners();

        // Check if Remember Me was selected
        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_REMEMBERME);
        if (sessionManager.checkRememberMe()) {
            HashMap<String, String> rememberDetails = sessionManager.getRememberMeFromSession();
            binding.edEmailSignIn.setText(rememberDetails.get(SessionManager.KEY_EMAIL));
            binding.edPasswordSignIn.setText(rememberDetails.get(SessionManager.KEY_PASSWORD));
        }
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    private void setupEventListeners() {
        binding.btnGoogleSignIn.setOnClickListener(view -> signInWithGoogle());
        binding.txtSignUp.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
        binding.btnSignIn.setOnClickListener(v -> signInWithEmail());
        binding.txtForgotPassword.setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));

        binding.checkboxRememberme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                String email = binding.edEmailSignIn.getText().toString();
                String password = binding.edPasswordSignIn.getText().toString();
                new SessionManager(this, SessionManager.SESSION_REMEMBERME).createRememberMeSession(email, password);
            }
        });

        binding.imvShowpass.setOnClickListener(v -> togglePasswordVisibility());
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQ_SIGN_IN);
    }

    private void signInWithEmail() {
        String email = binding.edEmailSignIn.getText().toString().trim();
        String password = binding.edPasswordSignIn.getText().toString().trim();

        if (email.isEmpty()) {
            binding.txtEnterEmailSignIn.setVisibility(View.VISIBLE);
        } else {
            binding.txtEnterEmailSignIn.setVisibility(View.INVISIBLE);
        }

        if (password.isEmpty()) {
            binding.txtEnterPasswordSignIn.setVisibility(View.VISIBLE);
        } else {
            binding.txtEnterPasswordSignIn.setVisibility(View.INVISIBLE);
        }

        if (!email.isEmpty() && !password.isEmpty()) {
            progressDialog.setMessage("Signing in...");
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(SignInActivity.this, "Sign in success!", Toast.LENGTH_SHORT).show();
                    navigateToMainActivity();
                } else {
                    Toast.makeText(SignInActivity.this, "Sign in failed! Please check your email or password.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void togglePasswordVisibility() {
        if (binding.edPasswordSignIn.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
            binding.imvShowpass.setImageResource(R.drawable.visible_password);
            binding.edPasswordSignIn.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            binding.imvShowpass.setImageResource(R.drawable.hide_password);
            binding.edPasswordSignIn.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account.getIdToken());
            }
        } catch (ApiException e) {
            Log.w("SignInActivity", "Google sign-in failed", e);
            Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                navigateToMainActivity();
            } else {
                Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMainActivity() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("idUser", user.getUid());
            editor.putString("emailUser", user.getEmail());
            editor.apply();

            Intent mainIntent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
            Toast.makeText(this, "Sign in success!", Toast.LENGTH_SHORT).show();
        }
    }
}
*/
