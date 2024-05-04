package com.movieapi.movie.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.movieapi.movie.R;
import com.movieapi.movie.adapter.CommentAdapter;
import com.movieapi.movie.controller.CommentController;
import com.movieapi.movie.controller.interfaces.GetDataCommentInterface;
import com.movieapi.movie.databinding.ActivityViewAllCommentsBinding;
import com.movieapi.movie.model.member.CommentModel;
import com.movieapi.movie.model.member.Member;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewAllCommentsActivity extends AppCompatActivity implements GetDataCommentInterface {
    ActivityViewAllCommentsBinding binding;
    List<CommentModel> commentModelList;
    CommentAdapter commentAdapter;
    GetDataCommentInterface commentInterface;
    int movieId;
    String idMovie;
    DatabaseReference nodeRoot;
    SharedPreferences prefUser;
    String idUser;
    CommentController commentController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewAllCommentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarComments);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbarComments.setTitleTextColor(Color.WHITE);

        Intent intent = getIntent();
        movieId = intent.getIntExtra("movieId", -1);
        idMovie = String.valueOf(movieId);

        if (movieId == -1) finish();

        commentInterface = this;
        nodeRoot = FirebaseDatabase.getInstance().getReference();

        prefUser = getSharedPreferences("sessionUser", Context.MODE_PRIVATE);
        idUser = prefUser.getString("idUser", "");

        commentController = new CommentController();

        loadComments();
        postComment();
    }

    private void loadComments() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot dataComments = snapshot.child("comments").child(idMovie);
                List<CommentModel> commentModels = new ArrayList<>();

                for (DataSnapshot valueComment : dataComments.getChildren()){
                    CommentModel commentModel = valueComment.getValue(CommentModel.class);
                    commentModel.setIdComment(valueComment.getKey());

                    Member member = snapshot.child("members").child(commentModel.getIdUser()).getValue(Member.class);
                    commentModel.setMember(member);

                    commentModels.add(commentModel);
                }

                try {
                    commentInterface.getDataComment(commentModels);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        nodeRoot.addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    public void getDataComment(List<CommentModel> commentModelList) {
        try {
            commentAdapter = new CommentAdapter(ViewAllCommentsActivity.this, commentModelList);
            binding.commentsRecView.setAdapter(commentAdapter);
            binding.commentsRecView.setLayoutManager(new LinearLayoutManager(ViewAllCommentsActivity.this, LinearLayoutManager.VERTICAL, false));

            if(commentModelList.size() > 1){
                setTitle(commentModelList.size() + " Comments");
            }else
                setTitle(commentModelList.size() + " Comment");


        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void postComment(){
        binding.sendCmtImv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contentCmt = binding.edAddComment.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date timeCmt = new Date();
                idMovie = String.valueOf(movieId);

                if (contentCmt.trim().length() == 0){
                    Toast.makeText(ViewAllCommentsActivity.this, "Please add your comments", Toast.LENGTH_SHORT).show();
                }

                CommentModel commentModel = new CommentModel();
                commentModel.setIdUser(idUser);
                commentModel.setContent(contentCmt);
                commentModel.setTimeComment(sdf.format(timeCmt));

                commentController.InsertComment(idMovie, commentModel);

                Toast.makeText(ViewAllCommentsActivity.this, "Comment success !", Toast.LENGTH_SHORT).show();

                binding.edAddComment.setText("");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}