package com.example.hlc_03;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.hlc_03.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private ActivityMainBinding binding;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.button.setOnClickListener(this);
        binding.button2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        if(view == binding.button)
        {
            intent = new Intent(this, Main1.class);
            startActivity(intent);
        }
        if(view == binding.button2)
        {
            intent = new Intent(this, Main2.class);
            startActivity(intent);
        }

    }
}