package com.movieapi.movie.controller.interfaces;

import com.movieapi.movie.model.member.CommentModel;
import com.movieapi.movie.model.member.ReportCommentModel;

import java.util.List;

public interface CommentItemListener {
    void onCommentLongClick(ReportCommentModel reportCommentModel);
}
