package com.example.doan_shopsmartphone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.model.Comment;
import com.example.doan_shopsmartphone.model.ReviewShowModel;
public class ReviewShowAdapter extends RecyclerView.Adapter<ReviewShowAdapter.ReviewViewHolder> {
    private List<Comment> reviewList;
    private Context context;

    public void setReviewList(List<Comment> reviewList) {
        this.reviewList = reviewList;
    }

    public ReviewShowAdapter(List<Comment> reviewList, Context context) {
        this.reviewList = reviewList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Comment review = reviewList.get(position);
        holder.nameTextView.setText(review.getName());
        holder.commentTextView.setText(review.getContent());
        holder.ratingBar.setRating(review.getRate());
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

        // 2. Định nghĩa định dạng đầu ra bạn muốn (Ngày/Tháng/Năm)
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // 3. Chuyển đổi
        try {
            Date date = inputFormat.parse(review.getCreatedAt());
            String formattedDate = outputFormat.format(date);
            holder.date.setText(formattedDate);
        }catch (Exception e){}


    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatarImageView;
        public TextView nameTextView,date;
        public TextView commentTextView;
        public RatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.img_avt_review);
            nameTextView = itemView.findViewById(R.id.txt_name_review);
            commentTextView = itemView.findViewById(R.id.txt_comment_review);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            date = itemView.findViewById(R.id.date);
        }
    }
}
