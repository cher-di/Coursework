package com.dmitry.pickletax;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.dmitry.pickletax.Constants.AUTH_REQUEST;
import static com.dmitry.pickletax.Constants.AUTH_RESULT_ACK;
import static com.dmitry.pickletax.Constants.CITY_IDENTIFIER;
import static com.dmitry.pickletax.Constants.CONNECT_TIMEOUT;
import static com.dmitry.pickletax.Constants.EMAIL_IDENTIFIER;
import static com.dmitry.pickletax.Constants.JSON;
import static com.dmitry.pickletax.Constants.REAUTH_REQUEST;
import static com.dmitry.pickletax.Constants.REAUTH_REQUEST_ACK;
import static com.dmitry.pickletax.Constants.SERVER_ERROR;
import static com.dmitry.pickletax.Constants.UPDATE_ACK;
import static com.dmitry.pickletax.Constants.UPDATE_FAIL;

public class MainActivity extends AppCompatActivity {
    private DBHelper mDBHelper;
    private Button updateButton;
    private Button classroomsButton;
    private Button changeStatusButton;
    private Button authButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDBHelper = new DBHelper(this);
        updateButton = (Button) findViewById(R.id.activity_main_button_update);
        classroomsButton = (Button) findViewById(R.id.activity_main_button_classrooms);
        changeStatusButton = (Button) findViewById(R.id.activity_main_button_change_status);
        authButton = (Button) findViewById(R.id.activity_main_button_auth);
        if (!mDBHelper.isAuthorized()) {
            updateButton.setEnabled(false);
            classroomsButton.setEnabled(false);
            changeStatusButton.setEnabled(false);
        }
    }


    public void onClickButtonUpdate(View view) {
        updateDatabase(true);
    }

    public void onClickButtonClassrooms(View view) {
        Intent intent = new Intent(this, ClassroomsActivity.class);
        startActivity(intent);
    }

    public void onClickButtonChangeStatus(View view) {
        Intent intent = new Intent(this, ChangeStatusActivity.class);
        startActivity(intent);
    }

    public void onClickButtonAuth(View view) {
        if (mDBHelper.isAuthorized()) {
            ServiceValues serviceValues = mDBHelper.getServiceVars();

            Intent intent = new Intent(this, ReauthActivity.class);
            intent.putExtra(EMAIL_IDENTIFIER, serviceValues.getEmail());
            intent.putExtra(CITY_IDENTIFIER, serviceValues.getCity());

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
            if (resultCode == AUTH_RESULT_ACK)
                updateDatabase(true);
        } else if (requestCode == REAUTH_REQUEST) {
            if (resultCode == REAUTH_REQUEST_ACK) {
                updateButton.setEnabled(false);
                classroomsButton.setEnabled(false);
                changeStatusButton.setEnabled(false);

                class ClearDatabase extends AsyncTask<Void, Void, Void> {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        mDBHelper.clearDatabase();
                        return null;
                    }
                }
                ClearDatabase clearDatabase = new ClearDatabase();
                clearDatabase.execute();
            }
        }
    }

    private void updateDatabase(final boolean show_message_for_user) {

        if (show_message_for_user)
            updateButton.setEnabled(false);
            classroomsButton.setEnabled(false);
            changeStatusButton.setEnabled(false);
            authButton.setEnabled(false);

            Toast.makeText(this, "Запущено обновление базы данных", Toast.LENGTH_SHORT).show();

        String email = mDBHelper.getServiceVars().getEmail();

        String json = "{\"email\" : \"" + email + "\"}";

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(getString(R.string.url_update))
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (show_message_for_user) {
                            updateButton.setEnabled(true);
                            classroomsButton.setEnabled(true);
                            changeStatusButton.setEnabled(true);
                            authButton.setEnabled(true);

                            Toast.makeText(MainActivity.this, "Проблемы с сетью", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                MainActivity.this.runOnUiThread(new Runnable() {
                    final int responseCode = response.code();
                    final String responseBody = response.body().string();

                    @Override
                    public void run() {
                        if (responseCode == UPDATE_FAIL) {
                            if (show_message_for_user) {
                                updateButton.setEnabled(true);
                                classroomsButton.setEnabled(true);
                                changeStatusButton.setEnabled(true);
                                authButton.setEnabled(true);

                                Toast.makeText(MainActivity.this, "Ошибка обновления", Toast.LENGTH_SHORT).show();
                            }
                        } else if (responseCode == UPDATE_ACK) {
                            if (show_message_for_user)
                                Toast.makeText(MainActivity.this, "Идет обновление базы данных...", Toast.LENGTH_SHORT).show();

                            class UpdateDatabase extends AsyncTask<Void, Void, Void> {
                                private String jsonUpdate;

                                UpdateDatabase(String jsonUpdate) {
                                    this.jsonUpdate = jsonUpdate;
                                }

                                @Override
                                protected Void doInBackground(Void... voids) {
                                    mDBHelper.updateDatabase(jsonUpdate);
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void aVoid) {
                                    super.onPostExecute(aVoid);
                                    if (show_message_for_user) {
                                        updateButton.setEnabled(true);
                                        classroomsButton.setEnabled(true);
                                        changeStatusButton.setEnabled(true);
                                        authButton.setEnabled(true);

                                        Toast.makeText(MainActivity.this, "База данных обновлена", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            UpdateDatabase updateDatabase = new UpdateDatabase(responseBody);
                            updateDatabase.execute();
                        } else if (responseCode == SERVER_ERROR) {
                            if (show_message_for_user)
                                Toast.makeText(MainActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                        } else if (show_message_for_user)
                            Toast.makeText(MainActivity.this, "Unexpected HTTP code: " + Integer.toString(responseCode), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
