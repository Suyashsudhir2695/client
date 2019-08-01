package com.example.change.foodorder.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.change.foodorder.R;

public class CommentsViewHolder extends RecyclerView.ViewHolder {

    public TextView txtUserPhoneComment,txtComment;
    public RatingBar ratingBar;
    public CommentsViewHolder(View itemView) {
        super(itemView);

        txtUserPhoneComment = itemView.findViewById(R.id.textUserPhoneComment);
        txtComment = itemView.findViewById(R.id.textComment);
        ratingBar = itemView.findViewById(R.id.commentRatingBar);
    }
}
