package com.dmitry.pickletax;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import static com.dmitry.pickletax.Constants.CITY_IDENTIFIER;
import static com.dmitry.pickletax.Constants.EMAIL_IDENTIFIER;
import static com.dmitry.pickletax.Constants.REAUTH_REQUEST_ACK;

public class ReauthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reauth);

        Intent intent = getIntent();
        AuthValues authValues = new AuthValues();
        authValues.email = intent.getStringExtra(EMAIL_IDENTIFIER);
        authValues.city = intent.getStringExtra(CITY_IDENTIFIER);

        TextView textView = (TextView) findViewById(R.id.activity_reauth_textview);
        textView.setText("Вы авторизованы как:\n" + authValues.email + "\nГород:\n" + authValues.city);
    }

    public void onClickButtonLogOut(View view) {
        setResult(REAUTH_REQUEST_ACK, new Intent());
    }
}
