package com.movieapi.movie.adapter.movies;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.movieapi.movie.R;
import com.movieapi.movie.controller.CommentController;
import com.movieapi.movie.controller.interfaces.CommentItemListener;
import com.movieapi.movie.model.member.CommentModel;
import com.movieapi.movie.model.member.ReportCommentModel;

import java.util.List;

public class CommentMovieAdapter extends RecyclerView.Adapter<CommentMovieAdapter.CommentHolder> {
    Context context;
    List<CommentModel> commentModelList;
    private String movieId;
    private CommentController commentController;
    private String idUser;
    private SharedPreferences prefUser;
    public CommentItemListener listener;

    public void setMovieId(String movieId) {
        this.movieId = movieId;
        notifyDataSetChanged();
    }

    public CommentMovieAdapter(Context context, List<CommentModel> commentModelList, CommentItemListener listener) {
        this.context = context;
        this.commentModelList = commentModelList;
        this.listener =  listener;
    }

    @NonNull
    @Override
    public CommentMovieAdapter.CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentHolder(LayoutInflater.from(context).inflate(R.layout.item_comments, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentMovieAdapter.CommentHolder holder, int position) {
        commentController = new CommentController();

        CommentModel commentModel = commentModelList.get(position);

        final String idCmt = commentModel.getIdComment();

        setImageUser(holder.avtComment_imv, commentModel.getMember().getAvt());
        holder.txtNamePersonComment.setText(commentModel.getMember().getName());
        holder.txtContentComment.setText(commentModel.getContent());
        holder.total_likes_comments.setText(String.valueOf(commentModel.getTotalLikeComment()));
        holder.time_comment.setText(commentModel.getTimeComment());

        prefUser = context.getSharedPreferences("sessionUser", Context.MODE_PRIVATE);
        idUser = prefUser.getString("idUser", "");

        DatabaseReference userLike = FirebaseDatabase.getInstance().getReference().child("likecomments").child(idUser).child(idCmt);
        userLike.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isLiked = snapshot.exists();
                holder.likeCommentImv.setImageResource(isLiked ? R.drawable.heart_fill_ic : R.drawable.heart_outline_ic);
                holder.likeCommentImv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isLiked){
                            if (commentModel.getTotalLikeComment() == 0){
                                commentController.insertTotalLikeCmt(movieId, idCmt, commentModel.getTotalLikeComment());
                            }else {
                                commentController.insertTotalLikeCmt(movieId, idCmt, commentModel.getTotalLikeComment() - 1);
                            }
                        }else
                            commentController.insertTotalLikeCmt(movieId, idCmt, commentModel.getTotalLikeComment() + 1);

                        commentController.stateLikeComments(idUser, idCmt, !isLiked);

                        userLike.setValue(!isLiked ? true : null);
                        holder.likeCommentImv.setImageResource(!isLiked ? R.drawable.heart_fill_ic : R.drawable.heart_outline_ic);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.moreComment_imv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String idComment = commentModel.getIdComment();
                String content = commentModel.getContent();

                ReportCommentModel reportCommentModel = new ReportCommentModel();
                reportCommentModel.setIdCmt(idComment);
                reportCommentModel.setContent(content);
                reportCommentModel.setIdUser(idUser);
                reportCommentModel.setNameUser(commentModel.getMember().getName());

                try{
                     listener.onCommentLongClick(reportCommentModel);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                return false;
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
