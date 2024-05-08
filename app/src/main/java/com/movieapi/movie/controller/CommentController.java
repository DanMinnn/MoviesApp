package com.movieapi.movie.controller;

import com.movieapi.movie.model.member.CommentModel;
import com.movieapi.movie.model.member.ReportCommentModel;

public class CommentController {
    CommentModel commentModel;
    ReportCommentModel reportCommentModel;
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

    public void reportComments(ReportCommentModel reportCommentModel){
        reportCommentModel.reportComments(reportCommentModel);
    }
}
