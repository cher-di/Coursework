package com.dmitry.pickletax;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ReauthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reauth);

        TextView textView = (TextView)findViewById(R.id.activity_reauth_textview);
        textView.setText("Вы авторизованы как:\ncher-di@mail.ru");
        // TODO замени рабочим кодом
    }
}
