package com.movieapi.movie.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Layout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.movieapi.movie.R;
import com.movieapi.movie.activity.MovieDetailsActivity;
import com.movieapi.movie.controller.CommentController;
import com.movieapi.movie.model.member.CommentModel;
import com.movieapi.movie.utils.Constants;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {
    Context context;
    List<CommentModel> commentModelList;
    private String movieId;
    private String idCmt;
    private CommentController commentController;

    public void setMovieId(String movieId) {
        this.movieId = movieId;
        notifyDataSetChanged();
    }

    public CommentAdapter(Context context, List<CommentModel> commentModelList) {
        this.context = context;
        this.commentModelList = commentModelList;
    }

    @NonNull
    @Override
    public CommentAdapter.CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentHolder(LayoutInflater.from(context).inflate(R.layout.item_comments, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CommentHolder holder, int position) {
        commentController = new CommentController();
        CommentModel commentModel = commentModelList.get(position);
        idCmt = commentModel.getIdComment();

        setImageUser(holder.avtComment_imv, commentModel.getMember().getAvt());
        holder.txtNamePersonComment.setText(commentModel.getMember().getName());
        holder.txtContentComment.setText(commentModel.getContent());
        holder.total_likes_comments.setText(String.valueOf(commentModel.getTotalLikeComment()));
        holder.time_comment.setText(commentModel.getTimeComment());


        holder.likeCommentImv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = 0;
                //holder.likeCommentImv.setTag(Constants.TAG_NOT_FAV);
                holder.likeCommentImv.setImageResource(R.drawable.heart_fill_ic);
                count++;
                //holder.likeCommentImv.setTag(Constants.TAG_FAV);
                commentController.insertTotalLikeCmt(movieId, idCmt, commentModel.getTotalLikeComment() + 1);

                /*if ((int)holder.likeCommentImv.getTag() == Constants.TAG_NOT_FAV) {

                }else {
                    holder.likeCommentImv.setTag(Constants.TAG_NOT_FAV);
                    holder.likeCommentImv.setImageResource(R.drawable.heart_outline_ic );
                    count--;
                    commentController.insertTotalLikeCmt(movieId, idCmt, count);
                }

                Log.d("movieId - idCmt: ", movieId + "-" + idCmt);*/
            }
        });

    }

    @Override
    public int getItemCount() {
        return commentModelList.size();
    }

    public class CommentHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        ImageView avtComment_imv, moreComment_imv, likeCommentImv;
        TextView txtNamePersonComment, txtContentComment, total_likes_comments, time_comment;
        public CommentHolder(@NonNull View itemView) {
            super(itemView);

            avtComment_imv = itemView.findViewById(R.id.avtComment_imv);
            txtNamePersonComment = itemView.findViewById(R.id.txtNamePersonComment);
            txtContentComment = itemView.findViewById(R.id.txtContentComment);
            total_likes_comments = itemView.findViewById(R.id.total_likes_comments);
            time_comment = itemView.findViewById(R.id.time_comment);
            likeCommentImv = itemView.findViewById(R.id.like_comment_imv);

            moreComment_imv = itemView.findViewById(R.id.more_comment_imv);

            moreComment_imv.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuInflater inflater = new MenuInflater(v.getContext());
            inflater.inflate(R.menu.more_comment_menu, menu);
        }
    }

    private void setImageUser(ImageView userImageView, String linkImv){
        StorageReference storageUserImv = FirebaseStorage
                .getInstance()
                .getReference()
                .child("members")
                .child(linkImv);
        long ONE_MEGABYE = 1024 * 1024;
        storageUserImv.getBytes(ONE_MEGABYE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            userImageView.setImageBitmap(bitmap);
        });
    }

    public void update(List<CommentModel> newComments){
        commentModelList.clear();
        commentModelList.addAll(newComments);
        notifyDataSetChanged();
    }
}
