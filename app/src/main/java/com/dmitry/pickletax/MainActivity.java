package com.dmitry.pickletax;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import static com.dmitry.pickletax.Constants.AUTH_REQUEST;
import static com.dmitry.pickletax.Constants.AUTH_RESULT_ACK;
import static com.dmitry.pickletax.Constants.CITY_IDENTIFIER;
import static com.dmitry.pickletax.Constants.EMAIL_IDENTIFIER;
import static com.dmitry.pickletax.Constants.REAUTH_REQUEST;
import static com.dmitry.pickletax.Constants.REAUTH_REQUEST_ACK;

public class MainActivity extends AppCompatActivity {
    private DBHelper mDBHelper;
    private Button updateButton;
    private Button classroomsButton;
    private Button changeStatusButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDBHelper = new DBHelper(this);
        updateButton = (Button)findViewById(R.id.activity_main_button_update);
        classroomsButton = (Button)findViewById(R.id.activity_main_button_classrooms);
        changeStatusButton= (Button)findViewById(R.id.activity_main_button_change_status);
        if (!mDBHelper.isAuthorized()) {
            updateButton.setEnabled(false);
            classroomsButton.setEnabled(false);
            changeStatusButton.setEnabled(false);
        }
    }


    public void onClickButtonUpdate(View view) {

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
            AuthValues authValues = mDBHelper.getServiceVars();

            Intent intent = new Intent(this, ReauthActivity.class);
            intent.putExtra(EMAIL_IDENTIFIER, authValues.email);
            intent.putExtra(CITY_IDENTIFIER, authValues.city);

            startActivityForResult(intent, REAUTH_REQUEST);
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
//                AuthValues authValues = new AuthValues();
//                authValues.email = data.getStringExtra(EMAIL_IDENTIFIER);
//                authValues.city = data.getStringExtra(CITY_IDENTIFIER);
//                try {
//                    mDBHelper.addServiceVars(authValues);
//                }
//                catch (Exception e) {
//                    e.printStackTrace();
//                }

                // TODO добавь обновление базы сразу после авторизации

                updateButton.setEnabled(true);
                classroomsButton.setEnabled(true);
                changeStatusButton.setEnabled(true);
            }
        }
        else if (requestCode == REAUTH_REQUEST) {
            if (resultCode == REAUTH_REQUEST_ACK) {
                updateButton.setEnabled(false);
                classroomsButton.setEnabled(false);
                changeStatusButton.setEnabled(false);

                mDBHelper.clearDatabase(); // TODO сделай в отедльном потоке
            }
        }
    }
}
