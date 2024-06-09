package com.movieapi.movie.activity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.movieapi.movie.R;
import com.movieapi.movie.databinding.ActivityEditProfileBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 3;
    ActivityEditProfileBinding binding;
    private String name, email, avt, userId;

    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (o.getResultCode() == RESULT_OK && o.getData() != null){
                Uri imageUri = o.getData().getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    binding.userImageView.setImageBitmap(bitmap);
                    uploadImageToFirebase(imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    });;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarEdit);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.toolbarBackBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getData();
        loadData();
        addEvents();
    }

    private void addEvents() {
        binding.imvAvtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFromGallery();
            }
        });

        binding.btnUpdatePro5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = binding.edFullName.getText().toString();
                String phoneNumber = binding.edPhoneNumber.getText().toString();
                String alias = binding.edNickName.getText().toString();

                boolean isValid = true;

                if (fullName.trim().length() == 0) {
                    binding.txtEnterName.setVisibility(View.VISIBLE);
                    isValid = false;
                } else {
                    binding.txtEnterName.setVisibility(View.INVISIBLE);
                }

                if (phoneNumber.trim().length() == 0) {
                    binding.txtEnterPhone.setVisibility(View.VISIBLE);
                    isValid = false;
                } else {
                    binding.txtEnterPhone.setVisibility(View.INVISIBLE);
                }

                if (alias.trim().length() == 0){
                    binding.txtEnterAlias.setVisibility(View.VISIBLE);
                    isValid = false;
                }else {
                    binding.txtEnterAlias.setVisibility(View.INVISIBLE);
                }

                if (isValid) {
                    DatabaseReference nodeUpdate = FirebaseDatabase.getInstance().getReference();
                    HashMap<String, Object> updates = new HashMap<>();
                    //updates.put("avt", avt);
                    updates.put("name", alias);
                    updates.put("email", email);
                    updates.put("fullName", fullName);
                    updates.put("phone", phoneNumber);

                    nodeUpdate.child("members").child(userId).updateChildren(updates)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("UpdateMember", "Member updated successfully");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("UpdateMember", "Failed to update member");
                                }
                            });
                } else {
                    // Handling cho trường hợp ít nhất một trong hai thông tin là rỗng
                }

            }
        });
    }

    private void getData(){
        Intent iData = getIntent();
        name = iData.getStringExtra("name");
        email = iData.getStringExtra("email");
        avt = iData.getStringExtra("avt");
        userId = iData.getStringExtra("userId");
    }

    private void loadData(){
        binding.edNickName.setText(name);
        binding.edEmailEdit.setText(email);
        setImageUser(binding.userImageView, avt);
    }

    private void setImageUser(ImageView userImageView, String linkImv){
        StorageReference storageUserImv = FirebaseStorage
                .getInstance()
                .getReference()
                .child("members")
                .child(linkImv);
        long ONE_MEGABYE = 1024 * 1024;
        storageUserImv.getBytes(ONE_MEGABYE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            userImageView.setImageBitmap(bitmap);
        });
    }

    private void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        launcher.launch(intent);
    }

    //Update avt
    public void chooseImageFromGallery() {
        if (ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_CODE);
        } else {
            openGallery();
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference imagesRef = storageRef.child("members/" + imageUri.getLastPathSegment());

            // Lấy dữ liệu từ ImageView dưới dạng byte
            binding.userImageView.setDrawingCacheEnabled(true);
            binding.userImageView.buildDrawingCache();
            Bitmap bitmap = binding.userImageView.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = imagesRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(EditProfileActivity.this, "Upload failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Xử lý trường hợp tải lên thành công
                    Toast.makeText(EditProfileActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();

                    imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUrl) {
                            updateUserAvatarInDatabase(userId, Uri.parse(imageUri.getLastPathSegment()));
                        }
                    });
                }
            });
        }
    }

    private void updateUserAvatarInDatabase(String userId, Uri imageUrl) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference membersRef = database.getReference("members");

        membersRef.child(userId).child("avt").setValue(imageUrl.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Avatar updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to update avatar: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}