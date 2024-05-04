package com.movieapi.movie.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.movieapi.movie.activity.MovieDetailsActivity;
import com.movieapi.movie.activity.ViewAllCommentsActivity;
import com.movieapi.movie.adapter.CommentAdapter;
import com.movieapi.movie.controller.CommentController;
import com.movieapi.movie.controller.interfaces.GetDataCommentInterface;
import com.movieapi.movie.databinding.FragmentCommentsBinding;
import com.movieapi.movie.model.member.CommentModel;
import com.movieapi.movie.model.member.Member;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentsFragment extends Fragment implements GetDataCommentInterface{
    FragmentCommentsBinding binding;
    int movieId;
    SharedPreferences prefUser;
    String idUser;
    CommentController commentController;
    CommentAdapter commentAdapter;
    List<CommentModel> commentModels;
    String idMovie;
    DatabaseReference nodeRoot;
    FirebaseAuth mAuth;
    private GetDataCommentInterface getDataCommentInterface;

    public CommentsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCommentsBinding.inflate(inflater, container, false);

        MovieDetailsActivity activity = (MovieDetailsActivity) getActivity();
        movieId = activity.getMovieId();
        idMovie = String.valueOf(movieId);

        commentController = new CommentController();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDataCommentInterface = this;

        nodeRoot = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        prefUser = getActivity().getApplicationContext().getSharedPreferences("sessionUser", Context.MODE_PRIVATE);
        idUser = prefUser.getString("idUser", "");

        postComment();
        dataComment();

        binding.seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iViewAllComments = new Intent(getContext(), ViewAllCommentsActivity.class);
                iViewAllComments.putExtra("movieId", movieId);
                startActivity(iViewAllComments);
            }
        });
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
                    Toast.makeText(getContext(), "Please add your comments", Toast.LENGTH_SHORT).show();
                }

                CommentModel commentModel = new CommentModel();
                commentModel.setIdUser(idUser);
                commentModel.setContent(contentCmt);
                commentModel.setTimeComment(sdf.format(timeCmt));

                commentController.InsertComment(idMovie, commentModel);

                Toast.makeText(getContext(), "Comment success !", Toast.LENGTH_SHORT).show();

                binding.edAddComment.setText("");
            }
        });
    }


    private void dataComment(){
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
                    getDataCommentInterface.getDataComment(commentModels);
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
            /*if (commentAdapter == null){

            }else {
                commentAdapter.update(commentModelList);
            }*/

            commentAdapter = new CommentAdapter(getContext(), commentModelList);
            binding.recViewComments.setAdapter(commentAdapter);
            binding.recViewComments.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

            if(commentModelList.size() > 1){
                binding.totalComment.setText(commentModelList.size() + " Comments");
            }else
                binding.totalComment.setText(commentModelList.size() + " Comment");

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
