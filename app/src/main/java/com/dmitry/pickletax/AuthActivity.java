package com.dmitry.pickletax;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.dmitry.pickletax.Constants.AUTH_RESULT_ACK;
import static com.dmitry.pickletax.Constants.AUTH_CODE_ACK;
import static com.dmitry.pickletax.Constants.AUTH_CODE_FAIL;
import static com.dmitry.pickletax.Constants.JSON;
import static com.dmitry.pickletax.Constants.AUTH_VALIDATION_ACK;
import static com.dmitry.pickletax.Constants.AUTH_VALIDATION_FAIL;

public class AuthActivity extends AppCompatActivity {
    private EditText editCity;
    private EditText editEmail;
    private EditText editCode;
    private Button ackAuthButton;
    private DBHelper mDBHelper;
    private String emailValidationRegex = ".+@.+\\..+";

    private AuthValues ackAuthValues;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        editCity = (EditText) findViewById(R.id.activity_auth_edittext_city);
        editEmail = (EditText) findViewById(R.id.activity_auth_edittext_email);
        editCode = (EditText) findViewById(R.id.activity_auth_edittext_code);
        editCode.setEnabled(false);
        ackAuthButton = (Button) findViewById(R.id.activity_auth_button_ackauth);
        ackAuthButton.setEnabled(false);

        mDBHelper = new DBHelper(this);
    }

    public void onClickButtonGetCode(View view) {
        if (editCity.getText().toString().isEmpty())
            Toast.makeText(this, getString(R.string.activity_auth_edittext_city), Toast.LENGTH_SHORT).show();
        else if (editEmail.getText().toString().isEmpty())
            Toast.makeText(this, getString(R.string.activity_auth_edittext_email), Toast.LENGTH_SHORT).show();
        else if (!editEmail.getText().toString().matches(emailValidationRegex))
            Toast.makeText(this, getString(R.string.activity_auth_nonvalid_email), Toast.LENGTH_SHORT).show();
        else {
            final AuthValues authValues = new AuthValues();
            authValues.city = editCity.getText().toString();
            authValues.email = editEmail.getText().toString();

            Gson gson = new Gson();
            String json = gson.toJson(authValues);

            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(getString(R.string.url_auth))
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    AuthActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AuthActivity.this, "Проблемы с сетью", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    AuthActivity.this.runOnUiThread(new Runnable() {
                        final Integer responseCode = response.code();

                        @Override
                        public void run() {
                            if (responseCode == AUTH_VALIDATION_FAIL) {
                                Toast.makeText(AuthActivity.this, "Невалидный город или Email", Toast.LENGTH_SHORT).show();
                            } else if (responseCode == AUTH_VALIDATION_ACK) {
                                editCode.setEnabled(true);
                                ackAuthButton.setEnabled(true);

                                ackAuthValues = authValues;

                                Toast.makeText(AuthActivity.this, "Сообщение с кодом отправлено на указанный адрес", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(AuthActivity.this, "Unexpected HTTP code: " + responseCode.toString(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });
        }

    }

    public void onClickButtonACKAuth(View view) {
        String code = editCode.getText().toString();
        if (code.isEmpty())
            Toast.makeText(this, getString(R.string.activity_auth_edittext_entercode), Toast.LENGTH_SHORT).show();
        else {
            final String json = "{'code' : '" + code + "'}";
            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(getString(R.string.url_auth))
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    AuthActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AuthActivity.this, "Проблемы с сетью", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    AuthActivity.this.runOnUiThread(new Runnable() {
                        final Integer responseCode = response.code();

                        @Override
                        public void run() {
                            if (responseCode == AUTH_CODE_FAIL)
                                Toast.makeText(AuthActivity.this, "Неверный код авторизации", Toast.LENGTH_SHORT).show();
                            else if (responseCode == AUTH_CODE_ACK) {
                                String jsonDB = null;
                                try {
                                    jsonDB = response.body().string();
                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                                }

                                class InitDB extends AsyncTask<String, Void, Void> {
                                    private String jsonDB;
                                    private AuthValues authValues;

                                    InitDB(String jsonDB, AuthValues authValues) {
                                        this.jsonDB = jsonDB;
                                        this.authValues = authValues;
                                    }

                                    @Override
                                    protected Void doInBackground(String... strings) {
                                        mDBHelper.initTables(jsonDB, authValues);
                                        return null;
                                    }
                                }
                                InitDB initDB = new InitDB(jsonDB, ackAuthValues);
                                initDB.execute();

                                Intent intent = new Intent();
                                setResult(AUTH_RESULT_ACK, intent);
                                AuthActivity.this.finish();
                            } else
                                Toast.makeText(AuthActivity.this, "Unexpected HTTP code: " + responseCode.toString(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });
        }
    }
}
