package com.example.puniaraharja.balicilichat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageActivity extends AppCompatActivity {

    String imageUpload;
    ImageView back;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ImageView imageView=(ImageView) findViewById(R.id.imageView);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                onBackPressed();
            }
        });


        imageUpload = getIntent().getStringExtra("imageUpload");
        StorageReference gsReference2 = storage.getReference().child("image").child(imageUpload);
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(gsReference2)
                .into(imageView);
    }
}
