package com.movieapi.movie.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.movieapi.movie.R;
import com.movieapi.movie.activity.MainActivity;
import com.movieapi.movie.activity.SignInActivity;
import com.movieapi.movie.controller.interfaces.InformationInterface;
import com.movieapi.movie.databinding.FragmentProfileBinding;
import com.movieapi.movie.model.member.Member;

public class ProfileFragment extends Fragment implements InformationInterface{
    FragmentProfileBinding binding;
    SharedPreferences prefSignIn;
    Context context;
    DatabaseReference nodeRoot;
    FirebaseAuth mAuth;
    String email, name;
    private InformationInterface anInterface;
    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //this.context = container.getContext();
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

        //binding.txtNameUser.setText("");
        binding.txtNameUser.setVisibility(View.GONE);
        binding.txtEmailUser.setText("");

        prefSignIn = getActivity().getApplicationContext().getSharedPreferences("sessionUser", Context.MODE_PRIVATE);
        getNameUser();

        email = prefSignIn.getString("emailUser", "");
        binding.txtEmailUser.setText(email);

        setImageUser(binding.userImageView, "user.jpg");
        Logout();
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
                                if (isAdded()) {
                                    if (binding != null) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (name != null){
                                                    binding.txtNameUser.setVisibility(View.VISIBLE);
                                                    binding.txtNameUser.setText(name);
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
}
