package com.movieapi.movie.controller.interfaces;

import com.movieapi.movie.model.member.CommentModel;

import java.util.List;

public interface GetDataCommentInterface {
    void getDataComment(List<CommentModel> commentModelList);
}
