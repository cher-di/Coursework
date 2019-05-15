package com.dmitry.pickletax;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import static com.dmitry.pickletax.Constants.*;

public class MainActivity extends AppCompatActivity {
    private DBHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDBHelper = new DBHelper(this);
    }


    public void onClickButtonUpdate(View view) {
        if (mDBHelper.isAuthorized()) {
            Toast toast = Toast.makeText(this, R.string.activity_main_is_auth, Toast.LENGTH_SHORT);
            toast.show();
            // TODO замени на рабочий код
        } else {
            Toast toast = Toast.makeText(this, R.string.activity_main_is_not_auth, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onClickButtonClassrooms(View view) {
        Intent intent = new Intent(this, ClassroomsActivity.class);
        startActivity(intent);
        // TODO замени на рабочий код
    }

    public void onClickButtonChangeStatus(View view) {
        Intent intent = new Intent(this, ChangeStatusActivity.class);
        startActivity(intent);
        // TODO замени на рабочий код
    }

    public void onClickButtonAuth(View view) {
        if (mDBHelper.isAuthorized()) {
            Intent intent = new Intent(this, ReauthActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, AuthActivity.class);
            startActivityForResult(intent, AUTH_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTH_REQUEST) {
            if (resultCode == AUTH_RESULT_ACK) {
                AuthValues authValues = new AuthValues();
                authValues.email = data.getStringExtra(EMAIL_IDENTIFIER);
                authValues.city = data.getStringExtra(CITY_IDENTIFIER);
                mDBHelper.addServiceVars(authValues);
                // TODO добавь обновление базы сразу после авторизации
            }
        }
    }
}
