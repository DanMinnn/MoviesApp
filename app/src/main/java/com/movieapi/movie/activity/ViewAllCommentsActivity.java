package com.movieapi.movie.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.movieapi.movie.R;
import com.movieapi.movie.adapter.CommentAdapter;
import com.movieapi.movie.controller.CommentController;
import com.movieapi.movie.controller.interfaces.CommentItemListener;
import com.movieapi.movie.databinding.ActivityViewAllCommentsBinding;
import com.movieapi.movie.model.member.CommentModel;
import com.movieapi.movie.model.member.Member;
import com.movieapi.movie.model.member.ReportCommentModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewAllCommentsActivity extends AppCompatActivity implements CommentItemListener {
    ActivityViewAllCommentsBinding binding;
    CommentAdapter commentAdapter;
    int movieId;
    String idMovie;
    DatabaseReference nodeRoot;
    SharedPreferences prefUser;
    String idUser;
    CommentController commentController;
    ValueEventListener valueEventListener;
    CommentItemListener listener;
    private ReportCommentModel reportCommentModel;


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

        nodeRoot = FirebaseDatabase.getInstance().getReference();

        prefUser = getSharedPreferences("sessionUser", Context.MODE_PRIVATE);
        idUser = prefUser.getString("idUser", "");

        commentController = new CommentController();
        listener = this;

        loadComments();
        postComment();
    }

    private void loadComments() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot dataComments = snapshot.child("comments").child(idMovie);
                List<CommentModel> commentModels = new ArrayList<>();

                commentAdapter = new CommentAdapter(ViewAllCommentsActivity.this, commentModels, listener);

                for (DataSnapshot valueComment : dataComments.getChildren()){
                    CommentModel commentModel = valueComment.getValue(CommentModel.class);
                    commentModel.setIdComment(valueComment.getKey());

                    Member member = snapshot.child("members").child(commentModel.getIdUser()).getValue(Member.class);
                    commentModel.setMember(member);

                    commentModels.add(commentModel);
                }

                commentAdapter.setMovieId(idMovie);
                binding.commentsRecView.setLayoutManager(new LinearLayoutManager(ViewAllCommentsActivity.this, LinearLayoutManager.VERTICAL, false));
                binding.commentsRecView.setAdapter(commentAdapter);

                if(commentModels.size() > 1){
                    setTitle(commentModels.size() + " Comments");
                }else
                    setTitle(commentModels.size() + " Comment");

                commentAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        nodeRoot.addValueEventListener(valueEventListener);
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
    protected void onStop() {
        super.onStop();
        if (nodeRoot != null && valueEventListener != null)
            nodeRoot.removeEventListener(valueEventListener);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mnReportCmt){
            Toast.makeText(ViewAllCommentsActivity.this, "Reported ! ", Toast.LENGTH_SHORT).show();
            commentController.reportComments(reportCommentModel);
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCommentLongClick(ReportCommentModel reportCommentModel) {
        this.reportCommentModel = reportCommentModel;
    }
}