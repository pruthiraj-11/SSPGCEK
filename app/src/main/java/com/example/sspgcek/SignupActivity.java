package com.example.sspgcek;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sspgcek.Models.Users;
import com.example.sspgcek.databinding.ActivitySignupBinding;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;
    private FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog dialog;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        AnimationDrawable animationDrawable = (AnimationDrawable)binding.linearLayoutup.getBackground();
        animationDrawable.setEnterFadeDuration(1500);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();
        //cc
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        dialog=new ProgressDialog(SignupActivity.this);
        dialog.setTitle("Creating Account");
        dialog.setMessage("We are creating your account");
        binding.sup.setOnClickListener(v -> {
            if(Objects.requireNonNull(binding.mailfield.getText()).toString().isEmpty()&& Objects.requireNonNull(binding.passfield.getText()).toString().isEmpty()&& Objects.requireNonNull(binding.uname.getText()).toString().isEmpty()){
                binding.uname.setError("Please fill out this field.");
                binding.mailfield.setError("Please fill out this field.");
                binding.passfield.setError("Please fill out this field.");
            } else {
                dialog.show();
                String email = String.valueOf(binding.mailfield.getText());
                String pass = String.valueOf(binding.passfield.getText());
                auth.createUserWithEmailAndPassword(email, pass).
                        addOnCompleteListener(task -> {
                            dialog.dismiss();
                            if (task.isSuccessful()) {
                                Users user=new Users(Objects.requireNonNull(binding.uname.getText()).toString(),email,pass);
                                String id= Objects.requireNonNull(task.getResult().getUser()).getUid();
                                database.getReference().child("Users").child(id).setValue(user);
                                final Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content),"Account created",Snackbar.LENGTH_LONG);
                                snackBar.setAction("OK", v1 -> snackBar.dismiss());
                                snackBar.show();
                                binding.uname.setText("");
                                binding.mailfield.setText("");
                                binding.passfield.setText("");
                                Toast.makeText(getApplicationContext(),"Account created successfully.",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                finish();
                            } else {
                                final Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content), Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()),Snackbar.LENGTH_SHORT);
                                snackBar.setAction("OK", v12 -> snackBar.dismiss());
                                snackBar.show();
                            }
                        });
            }
        });

        binding.ap.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, SigninActivity.class));
            finish();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)ev.getRawX(), (int)ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}