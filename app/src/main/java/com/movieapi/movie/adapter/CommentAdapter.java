package com.movieapi.movie.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.movieapi.movie.R;
import com.movieapi.movie.model.member.CommentModel;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {
    Context context;
    List<CommentModel> commentModelList;

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
        CommentModel commentModel = commentModelList.get(position);
        setImageUser(holder.avtComment_imv, commentModel.getMember().getAvt());
        holder.txtNamePersonComment.setText(commentModel.getMember().getName());
        holder.txtContentComment.setText(commentModel.getContent());
        //holder.total_likes_comments.setText(commentModel.);
        holder.time_comment.setText(commentModel.getTimeComment());
    }

    @Override
    public int getItemCount() {
        return commentModelList.size();
    }

    public class CommentHolder extends RecyclerView.ViewHolder {
        ImageView avtComment_imv;
        TextView txtNamePersonComment, txtContentComment, total_likes_comments, time_comment;
        public CommentHolder(@NonNull View itemView) {
            super(itemView);

            avtComment_imv = itemView.findViewById(R.id.avtComment_imv);
            txtNamePersonComment = itemView.findViewById(R.id.txtNamePersonComment);
            txtContentComment = itemView.findViewById(R.id.txtContentComment);
            total_likes_comments = itemView.findViewById(R.id.total_likes_comments);
            time_comment = itemView.findViewById(R.id.time_comment);
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