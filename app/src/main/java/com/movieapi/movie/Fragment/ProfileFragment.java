package com.movieapi.movie.Fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.movieapi.movie.R;
import com.movieapi.movie.activity.MainActivity;
import com.movieapi.movie.activity.NotificationActivity;
import com.movieapi.movie.activity.SignInActivity;
import com.movieapi.movie.controller.interfaces.InformationInterface;
import com.movieapi.movie.databinding.FragmentProfileBinding;
import com.movieapi.movie.model.member.Member;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileFragment extends Fragment implements InformationInterface{
    FragmentProfileBinding binding;
    SharedPreferences prefSignIn;
    Context context;
    DatabaseReference nodeRoot;
    FirebaseAuth mAuth;
    String email, userId, avt;
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1;
    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (o.getResultCode() == RESULT_OK && o.getData() != null){
                Uri imageUri = o.getData().getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                    binding.userImageView.setImageBitmap(bitmap);
                    uploadImageToFirebase(imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    });;

    private InformationInterface anInterface;


    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nodeRoot = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        anInterface = this;//notice

        setEmailUser();

        uploadAvt();
        Logout();
        setNotification();
    }

    private void uploadAvt() {
        binding.imvEditAvt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFromGallery();
            }
        });
    }

    private void setEmailUser() {
        binding.txtNameUser.setVisibility(View.GONE);
        binding.txtEmailUser.setText("");

        prefSignIn = getActivity().getApplicationContext().getSharedPreferences("sessionUser", Context.MODE_PRIVATE);
        getNameUser();

        email = prefSignIn.getString("emailUser", "");
        userId = prefSignIn.getString("idUser", "");
        binding.txtEmailUser.setText(email);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void getNameUser(){
        ValueEventListener dataMember = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot dataMember = snapshot.child("members");

                for (DataSnapshot valueMember : dataMember.getChildren()){
                    Member member = valueMember.getValue(Member.class);
                    if (member != null){
                        String emailData = member.getEmail();
                        //c1
                        if (email.equals(emailData)){
                            final String name = member.getName();
                            final String linkAvt = member.getAvt();
                                if (isAdded()) {
                                    if (binding != null) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (name != null){
                                                    binding.txtNameUser.setVisibility(View.VISIBLE);
                                                    binding.txtNameUser.setText(name);
                                                    avt = linkAvt;
                                                    setImageUser(binding.userImageView, avt);
                                                }
                                                else
                                                    binding.txtNameUser.setVisibility(View.GONE);
                                            }
                                        });
                                    }
                                }
                                break;
                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("error", error.getMessage().toString());
            }
        };
        nodeRoot.addListenerForSingleValueEvent(dataMember);
    }

    @Override
    public void getNameUser(String username) {
        //c2
        /*try {
            name = username;

            if (isAdded() && getView() != null){
                Log.d("name", name + "");
//                binding.txtNameUser.setText(username);
            }
        }catch (Exception e){
            e.printStackTrace();
        }*/
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

    //Logout
    private void Logout(){
        binding.lnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Log out");
                builder.setIcon(R.drawable.logout_ic);
                builder.setMessage("Do you want log out?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        Intent iSignIn = new Intent(getContext(), SignInActivity.class);
                        startActivity(iSignIn);
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }
    private void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        launcher.launch(intent);
    }

    //Update avt
    public void chooseImageFromGallery() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_CODE);
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
                    Toast.makeText(getActivity(), "Upload failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Xử lý trường hợp tải lên thành công
                    Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(getActivity(), "Avatar updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Failed to update avatar: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Notification
    private void setNotification(){
        binding.lnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iNoti = new Intent(getContext(), NotificationActivity.class);
                startActivity(iNoti);
            }
        });
    }
}
