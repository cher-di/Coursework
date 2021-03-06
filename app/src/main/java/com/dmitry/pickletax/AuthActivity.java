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
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.dmitry.pickletax.Constants.AUTH_RESULT_ACK;
import static com.dmitry.pickletax.Constants.AUTH_CODE_ACK;
import static com.dmitry.pickletax.Constants.AUTH_CODE_FAIL;
import static com.dmitry.pickletax.Constants.CONNECT_TIMEOUT;
import static com.dmitry.pickletax.Constants.JSON;
import static com.dmitry.pickletax.Constants.AUTH_VALIDATION_ACK;
import static com.dmitry.pickletax.Constants.AUTH_VALIDATION_FAIL;
import static com.dmitry.pickletax.Constants.SERVER_ERROR;

public class AuthActivity extends AppCompatActivity {
    private EditText editCity;
    private EditText editEmail;
    private EditText editCode;
    private Button ackAuthButton;
    private Button getCodeButton;
    private DBHelper mDBHelper;
    private String emailValidationRegex = ".+@.+\\..+";

    private ServiceValues ackServiceValues;

    private class VerificationValues {
        @SerializedName("email")
        @Expose
        private String email;

        @SerializedName("verification_code")
        @Expose
        private String verification_code;


        public VerificationValues(String email, String verification_code) {
            this.email = email;
            this.verification_code = verification_code;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        editCity = (EditText) findViewById(R.id.activity_auth_edittext_city);
        editEmail = (EditText) findViewById(R.id.activity_auth_edittext_email);
        editCode = (EditText) findViewById(R.id.activity_auth_edittext_code);
        editCode.setEnabled(false);
        getCodeButton = (Button) findViewById(R.id.activity_auth_button_getcode);
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
            editEmail.setEnabled(false);
            editCity.setEnabled(false);
            getCodeButton.setEnabled(false);


            final ServiceValues serviceValues = new ServiceValues();
            serviceValues.setCity(editCity.getText().toString());
            serviceValues.setEmail(editEmail.getText().toString());

            Gson gson = new Gson();
            String json = gson.toJson(serviceValues);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .build();

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
                            editEmail.setEnabled(true);
                            editCity.setEnabled(true);
                            getCodeButton.setEnabled(true);

                            Toast.makeText(AuthActivity.this, "Проблемы с сетью", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    AuthActivity.this.runOnUiThread(new Runnable() {
                        final int responseCode = response.code();

                        @Override
                        public void run() {
                            if (responseCode == AUTH_VALIDATION_FAIL) {
                                editEmail.setEnabled(true);
                                editCity.setEnabled(true);
                                getCodeButton.setEnabled(true);

                                Toast.makeText(AuthActivity.this, "Невалидный город или Email", Toast.LENGTH_SHORT).show();

                            } else if (responseCode == AUTH_VALIDATION_ACK) {
                                editCode.setEnabled(true);
                                ackAuthButton.setEnabled(true);

                                ackServiceValues = serviceValues;

                                Toast.makeText(AuthActivity.this, "Сообщение с кодом отправлено на указанный адрес", Toast.LENGTH_SHORT).show();
                            } else if (responseCode == SERVER_ERROR) {
                                Toast.makeText(AuthActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                                editEmail.setEnabled(true);
                                editCity.setEnabled(true);
                                getCodeButton.setEnabled(true);
                            } else {
                                editEmail.setEnabled(true);
                                editCity.setEnabled(true);
                                getCodeButton.setEnabled(true);

                                Toast.makeText(AuthActivity.this, "Unexpected HTTP code: " + Integer.toString(responseCode), Toast.LENGTH_SHORT).show();
                            }
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
            editCode.setEnabled(false);
            ackAuthButton.setEnabled(false);

            VerificationValues verificationValues = new VerificationValues(ackServiceValues.getEmail(), code);
            Gson gson = new Gson();
            final String json = gson.toJson(verificationValues);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .build();

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
                            editEmail.setEnabled(true);
                            editCity.setEnabled(true);
                            getCodeButton.setEnabled(true);

                            Toast.makeText(AuthActivity.this, "Проблемы с сетью", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    AuthActivity.this.runOnUiThread(new Runnable() {
                        final int responseCode = response.code();
                        final String responseBody = response.body().string();

                        @Override
                        public void run() {
                            if (responseCode == AUTH_CODE_FAIL) {
                                editCode.setEnabled(true);
                                ackAuthButton.setEnabled(true);

                                Toast.makeText(AuthActivity.this, "Неверный код авторизации", Toast.LENGTH_SHORT).show();
                            }
                            else if (responseCode == AUTH_CODE_ACK) {
                                class InitDB extends AsyncTask<String, Void, Void> {
                                    private String jsonDB;
                                    private ServiceValues authValues;

                                    InitDB(String jsonDB, ServiceValues authValues) {
                                        this.jsonDB = jsonDB;
                                        this.authValues = authValues;
                                    }

                                    @Override
                                    protected Void doInBackground(String... strings) {
                                        mDBHelper.initTables(jsonDB, authValues);
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void aVoid) {
                                        super.onPostExecute(aVoid);
                                        Intent intent = new Intent();
                                        setResult(AUTH_RESULT_ACK, intent);
                                        AuthActivity.this.finish();
                                    }
                                }
                                Toast.makeText(AuthActivity.this, "Загрузка базы данных...", Toast.LENGTH_SHORT).show();
                                InitDB initDB = new InitDB(responseBody, ackServiceValues);
                                initDB.execute();
                                // mDBHelper.initTables(responseBody, ackServiceValues);

//                                Intent intent = new Intent();
//                                setResult(AUTH_RESULT_ACK, intent);
//                                AuthActivity.this.finish();
                            } else if (responseCode == SERVER_ERROR) {
                                Toast.makeText(AuthActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                            } else {
                                editCode.setEnabled(true);
                                ackAuthButton.setEnabled(true);

                                Toast.makeText(AuthActivity.this, "Unexpected HTTP code: " + Integer.toString(responseCode), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
    }
}
