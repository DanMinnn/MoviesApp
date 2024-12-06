package com.movieapi.movie.activity.series;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.movieapi.movie.R;
import com.movieapi.movie.activity.movies.ViewAllCommentsMovieActivity;
import com.movieapi.movie.adapter.movies.CommentMovieAdapter;
import com.movieapi.movie.adapter.series.CommentSeriesAdapter;
import com.movieapi.movie.controller.CommentController;
import com.movieapi.movie.controller.interfaces.CommentItemListener;
import com.movieapi.movie.databinding.ActivityViewAllCommentSeriesBinding;
import com.movieapi.movie.model.member.CommentModel;
import com.movieapi.movie.model.member.Member;
import com.movieapi.movie.model.member.ReportCommentModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewAllCommentsSeriesActivity extends AppCompatActivity implements CommentItemListener {

    ActivityViewAllCommentSeriesBinding binding;
    CommentSeriesAdapter commentSeriesAdapter;
    int seriesId;
    String idSeries;
    DatabaseReference nodeRoot;
    SharedPreferences prefUser;
    String idUser;
    CommentController commentController;
    ValueEventListener valueEventListener;
    CommentItemListener listener;
    ReportCommentModel reportCommentModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewAllCommentSeriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarComments);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbarComments.setTitleTextColor(Color.WHITE);

        Intent intent = getIntent();
        seriesId = intent.getIntExtra("seriesId", -1);
        idSeries = String.valueOf(seriesId);

        if (seriesId == -1) finish();

        nodeRoot = FirebaseDatabase.getInstance().getReference();

        prefUser = getSharedPreferences("sessionUser", Context.MODE_PRIVATE);
        idUser = prefUser.getString("idUser", "");

        commentController = new CommentController();
        listener = this;

        loadComments();
        postComment();
    }

    private void postComment() {
        binding.sendCmtImv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String contentCmt = binding.edAddComment.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date timeCmt = new Date();
                idSeries = String.valueOf(seriesId);

                if (contentCmt.trim().length() == 0){
                    Toast.makeText(ViewAllCommentsSeriesActivity.this, "Please add your comments", Toast.LENGTH_SHORT).show();
                }

                CommentModel commentModel = new CommentModel();
                commentModel.setIdUser(idUser);
                commentModel.setContent(contentCmt);
                commentModel.setTimeComment(sdf.format(timeCmt));

                commentController.InsertComment(idSeries, commentModel);

                Toast.makeText(ViewAllCommentsSeriesActivity.this, "Comment success !", Toast.LENGTH_SHORT).show();

                binding.edAddComment.setText("");
            }
        });
    }

    private void loadComments() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot dataComments = snapshot.child("comments").child(idSeries);
                List<CommentModel> commentModels = new ArrayList<>();

                commentSeriesAdapter = new CommentSeriesAdapter(ViewAllCommentsSeriesActivity.this, commentModels, listener);

                for (DataSnapshot valueComment : dataComments.getChildren()){
                    CommentModel commentModel = valueComment.getValue(CommentModel.class);
                    commentModel.setIdComment(valueComment.getKey());

                    Member member = snapshot.child("members").child(commentModel.getIdUser()).getValue(Member.class);
                    commentModel.setMember(member);

                    commentModels.add(commentModel);
                }

                commentSeriesAdapter.setSeriesId(idSeries);
                binding.commentsRecView.setLayoutManager(new LinearLayoutManager(ViewAllCommentsSeriesActivity.this, LinearLayoutManager.VERTICAL, false));
                binding.commentsRecView.setAdapter(commentSeriesAdapter);

                if(commentModels.size() > 1){
                    setTitle(commentModels.size() + " Comments");
                }else
                    setTitle(commentModels.size() + " Comment");

                commentSeriesAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        nodeRoot.addValueEventListener(valueEventListener);
    }

    @Override
    public void onCommentLongClick(ReportCommentModel reportCommentModel) {
        this.reportCommentModel = reportCommentModel;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (nodeRoot != null && valueEventListener != null)
            nodeRoot.removeEventListener(valueEventListener);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mnReportCmt){
            Toast.makeText(ViewAllCommentsSeriesActivity.this, "Reported ! ", Toast.LENGTH_SHORT).show();
            commentController.reportComments(reportCommentModel);
        }
        return super.onContextItemSelected(item);
    }
}
