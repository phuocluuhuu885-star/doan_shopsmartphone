package com.example.doan_shopsmartphone.view.danh_gia;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.example.doan_shopsmartphone.R;

public class DialogRate extends Dialog {

    private float userRate = 0;
    public DialogRate(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_us_dialog_layout);

        final AppCompatButton rateNowBtn = findViewById(R.id.rateNowBtn);
        final AppCompatButton lateBtn = findViewById(R.id.laterBtn);
        final RatingBar ratingBar = findViewById(R.id.ratingBar);
        final ImageView ratingImage = findViewById(R.id.ratingImage);

        ratingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        lateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(rating <=1) {
                    ratingImage.setImageResource(R.drawable.one);
                } else if(rating <=2) {
                    ratingImage.setImageResource(R.drawable.two);
                }else if(rating <=3) {
                    ratingImage.setImageResource(R.drawable.three);
                }else if(rating <=4) {
                    ratingImage.setImageResource(R.drawable.four);
                } else if(rating<= 5) {
                    ratingImage.setImageResource(R.drawable.five);
                }
                animateImage(ratingImage);

                userRate = rating;
            }
        });
    }
    private void animateImage(ImageView ratingImage)  {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0,1f,0,1f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(200);
        ratingImage.startAnimation(scaleAnimation);
    }

}