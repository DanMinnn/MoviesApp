package com.movieapi.movie.controller;

import com.movieapi.movie.model.member.CommentModel;

public class CommentController {
    CommentModel commentModel;
    public CommentController(){
        commentModel = new CommentModel();
    }

    public void InsertComment(String movieId, CommentModel commentModel){
        commentModel.InsertComment(movieId, commentModel);
    }

    public void insertTotalLikeCmt(String movieId, String idComment, int totalLike){
        commentModel.insertTotalLikesCmt(movieId, idComment, totalLike);
    }

    public void stateLikeComments(String idUser, String idComment, boolean liked){
        commentModel.stateLikeComments(idUser, idComment, liked);
    }
}
