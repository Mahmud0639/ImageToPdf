package com.manuni.imagetopdf;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.manuni.imagetopdf.databinding.ActivityImageViewBinding;

public class ImageViewActivity extends AppCompatActivity {
    ActivityImageViewBinding binding;
    private String image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setTitle("All Images");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        image = getIntent().getStringExtra("imageUri");
        try {
            Glide.with(this).load(image).placeholder(R.drawable.ic_image_black).into(binding.showImageList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();


    }
}