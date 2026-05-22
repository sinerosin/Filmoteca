// LoginActivity.java
package com.example.filmoteca;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.filmoteca.ViewModel.AuthViewModel;
import com.example.filmoteca.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((binding = ActivityLoginBinding.inflate(getLayoutInflater())).getRoot());

        AuthViewModel viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        if (viewModel.getCurrentUser() != null) {
            goToMain();
        }
    }

    public void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}