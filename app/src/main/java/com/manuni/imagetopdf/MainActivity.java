package com.manuni.imagetopdf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationBarView;
import com.manuni.imagetopdf.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadImagesFragment();

        binding.bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if(itemId == R.id.bottom_menu_images){
                    loadImagesFragment();
                }else if(itemId == R.id.bottom_menu_pdf){
                    loadPdfFragment();
                }
                return true;
            }
        });
    }
    private void loadImagesFragment(){
        ImageListFragment imageListFragment = new ImageListFragment();

        FragmentTransaction transactionLoadImage = getSupportFragmentManager().beginTransaction();
        transactionLoadImage.replace(R.id.frameLayout,imageListFragment,"ImageListFragment");
        transactionLoadImage.commit();
    }
    private void loadPdfFragment(){
        PdfListFragment pdfListFragment = new PdfListFragment();

        FragmentTransaction transactionLoadImage = getSupportFragmentManager().beginTransaction();
        transactionLoadImage.replace(R.id.frameLayout,pdfListFragment,"PdfListFragment");
        transactionLoadImage.commit();

    }
}