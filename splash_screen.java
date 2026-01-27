package com.example.unit_converter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);



        // Splash delay
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(splash_screen.this, home_screen.class);
            startActivity(intent);
            finish();
        }, 3000); // 2 seconds
    }
}
