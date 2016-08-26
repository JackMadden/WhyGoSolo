package com.reraisedesign.whygosolo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void createAccountIntent(View v) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void signIn(View v){
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }
}
