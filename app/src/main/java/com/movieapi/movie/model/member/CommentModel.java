package com.movieapi.movie.model.member;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CommentModel {
    Member member;
    String content, idUser, idComment, timeComment;

    public CommentModel() {
    }

    public CommentModel(Member member, String content, String idUser, String idComment, String timeComment) {
        this.member = member;
        this.content = content;
        this.idUser = idUser;
        this.idComment = idComment;
        this.timeComment = timeComment;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdComment() {
        return idComment;
    }

    public void setIdComment(String idComment) {
        this.idComment = idComment;
    }

    public String getTimeComment() {
        return timeComment;
    }

    public void setTimeComment(String timeComment) {
        this.timeComment = timeComment;
    }

    public void InsertComment(String movieId, CommentModel commentModel){
        DatabaseReference nodeComment = FirebaseDatabase.getInstance().getReference().child("comments");
        String idComment = nodeComment.child(movieId).push().getKey();

        nodeComment.child(movieId).child(idComment).setValue(commentModel);
    }
}
