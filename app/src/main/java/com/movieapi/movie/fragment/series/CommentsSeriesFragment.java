package com.movieapi.movie.fragment.series;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.movieapi.movie.R;
import com.movieapi.movie.activity.movies.ViewAllCommentsMovieActivity;
import com.movieapi.movie.activity.series.SeriesDetailsActivity;
import com.movieapi.movie.activity.series.ViewAllCommentsSeriesActivity;
import com.movieapi.movie.adapter.movies.CommentMovieAdapter;
import com.movieapi.movie.controller.CommentController;
import com.movieapi.movie.controller.interfaces.CommentItemListener;
import com.movieapi.movie.databinding.FragmentCommentsBinding;
import com.movieapi.movie.model.member.CommentModel;
import com.movieapi.movie.model.member.Member;
import com.movieapi.movie.model.member.ReportCommentModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentsSeriesFragment extends Fragment implements CommentItemListener {
    FragmentCommentsBinding binding;
    int seriesId;
    SharedPreferences prefUser;
    String idUser;
    CommentController commentController;
    CommentMovieAdapter commentAdapter;
    String idSeries;
    DatabaseReference nodeRoot;
    ValueEventListener valueEventListener;
    FirebaseAuth mAuth;
    CommentItemListener commentItemListener;
    private ReportCommentModel reportCommentModel;

    public CommentsSeriesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCommentsBinding.inflate(inflater, container, false);

        SeriesDetailsActivity activity = (SeriesDetailsActivity) getActivity();
        seriesId = activity.getSeriesId();
        idSeries = String.valueOf(seriesId);

        commentController = new CommentController();

        commentItemListener = this;

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nodeRoot = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        prefUser = getActivity().getApplicationContext().getSharedPreferences("sessionUser", Context.MODE_PRIVATE);
        idUser = prefUser.getString("idUser", "");

        postComment();
        dataComment();

        binding.seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iViewAllComments = new Intent(getContext(), ViewAllCommentsSeriesActivity.class);
                iViewAllComments.putExtra("seriesId", seriesId);
                startActivity(iViewAllComments);
            }
        });
    }

    private void postComment(){
        binding.sendCmtImbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contentCmt = binding.edAddComment.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date timeCmt = new Date();
                idSeries = String.valueOf(seriesId);

                if (contentCmt.trim().length() == 0){
                    Toast.makeText(getContext(), "Please add your comments", Toast.LENGTH_SHORT).show();
                }else {
                    CommentModel commentModel = new CommentModel();
                    commentModel.setIdUser(idUser);
                    commentModel.setContent(contentCmt);
                    commentModel.setTimeComment(sdf.format(timeCmt));
                    commentModel.setTotalLikeComment(0);

                    commentController.InsertComment(idSeries, commentModel);

                    Toast.makeText(getContext(), "Comment success !", Toast.LENGTH_SHORT).show();

                    binding.edAddComment.setText("");
                }
            }
        });
    }


    private void dataComment(){
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot dataComments = snapshot.child("comments").child(idSeries);
                List<CommentModel> commentModels = new ArrayList<>();
                commentAdapter = new CommentMovieAdapter(getContext(), commentModels, commentItemListener);

                commentModels.clear();

                for (DataSnapshot valueComment : dataComments.getChildren()){
                    CommentModel commentModel = valueComment.getValue(CommentModel.class);
                    commentModel.setIdComment(valueComment.getKey());

                    Member member = snapshot.child("members").child(commentModel.getIdUser()).getValue(Member.class);
                    commentModel.setMember(member);

                    commentModels.add(commentModel);
                }

                if (getActivity() != null){
                    commentAdapter.setMovieId(idSeries);
                    binding.recViewComments.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    binding.recViewComments.setAdapter(commentAdapter);
                    if(commentModels.size() > 1){
                        binding.totalComment.setText(commentModels.size() + " Comments");
                    }else
                        binding.totalComment.setText(commentModels.size() + " Comment");

                    commentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        nodeRoot.addValueEventListener(valueEventListener);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (nodeRoot != null && valueEventListener != null){
            nodeRoot.removeEventListener(valueEventListener);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mnReportCmt){
            Toast.makeText(getContext(), "Reported ! ", Toast.LENGTH_SHORT).show();
            commentController.reportComments(reportCommentModel);
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCommentLongClick(ReportCommentModel reportCommentModel) {
        this.reportCommentModel = reportCommentModel;
    }
}
