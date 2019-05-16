package com.dmitry.pickletax;

import android.content.Intent;
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
import static com.dmitry.pickletax.Constants.CITY_IDENTIFIER;
import static com.dmitry.pickletax.Constants.AUTH_CODE_ACK;
import static com.dmitry.pickletax.Constants.AUTH_CODE_FAIL;
import static com.dmitry.pickletax.Constants.EMAIL_IDENTIFIER;
import static com.dmitry.pickletax.Constants.JSON;
import static com.dmitry.pickletax.Constants.AUTH_VALIDATION_ACK;
import static com.dmitry.pickletax.Constants.AUTH_VALIDATION_FAIL;

public class AuthActivity extends AppCompatActivity {
    private EditText editCity;
    private EditText editEmail;
    private EditText editCode;
    private Button ackAuthButton;
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
    }

    public void onClickButtonGetCode(View view) {
        if (editCity.getText().toString().isEmpty()) {
            Toast toast = Toast.makeText(this, getString(R.string.activity_auth_edittext_city), Toast.LENGTH_SHORT);
            toast.show();
        } else if (editEmail.getText().toString().isEmpty()) {
            Toast toast = Toast.makeText(this, getString(R.string.activity_auth_edittext_email), Toast.LENGTH_SHORT);
            toast.show();
        } else if (!editEmail.getText().toString().matches(emailValidationRegex)) {
            Toast toast = Toast.makeText(this, getString(R.string.activity_auth_nonvalid_email), Toast.LENGTH_SHORT);
            toast.show();
        } else {
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
                            Toast toast = Toast.makeText(AuthActivity.this, "Проблемы с сетью", Toast.LENGTH_SHORT);
                            toast.show();
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
                                Toast toast = Toast.makeText(AuthActivity.this, "Невалидный город или Email", Toast.LENGTH_SHORT);
                                toast.show();
                            } else if (responseCode == AUTH_VALIDATION_ACK) {
                                editCode.setEnabled(true);
                                ackAuthButton.setEnabled(true);

                                ackAuthValues = authValues;

                                Toast toast = Toast.makeText(AuthActivity.this, "Сообщение с кодом отправлено на указанный адрес", Toast.LENGTH_SHORT);
                                toast.show();
                            } else {
                                Toast toast = Toast.makeText(AuthActivity.this, "Unexpected HTTP code: " + responseCode.toString(), Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    });
                }
            });
        }

    }

    public void onClickButtonACKAuth(View view) {
        String code = editCode.getText().toString();
        if (code.isEmpty()) {
            Toast toast = Toast.makeText(this, getString(R.string.activity_auth_edittext_entercode), Toast.LENGTH_SHORT);
            toast.show();
        } else {
            String json = "{'code' : '" + code + "'}";
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
                            Toast toast = Toast.makeText(AuthActivity.this, "Проблемы с сетью", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    AuthActivity.this.runOnUiThread(new Runnable() {
                        final Integer responseCode = response.code();

                        @Override
                        public void run() {
                            if (responseCode == AUTH_CODE_FAIL) {
                                Toast toast = Toast.makeText(AuthActivity.this, "Неверный код авторизации", Toast.LENGTH_SHORT);
                                toast.show();
                            } else if (responseCode == AUTH_CODE_ACK) {
                                Intent intent = new Intent();
                                intent.putExtra(EMAIL_IDENTIFIER, ackAuthValues.email);
                                intent.putExtra(CITY_IDENTIFIER, ackAuthValues.city);
                                setResult(AUTH_RESULT_ACK, intent);
                                AuthActivity.this.finish();
                            } else {
                                Toast toast = Toast.makeText(AuthActivity.this, "Unexpected HTTP code: " + responseCode.toString(), Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    });
                }
            });
        }
    }
}
