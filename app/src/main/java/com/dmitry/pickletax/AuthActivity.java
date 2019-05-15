package com.dmitry.pickletax;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthActivity extends AppCompatActivity {
    private EditText editCode;
    private Button ackAuthButton;
    private String emailValidationRegex = ".+@.+\\..+";

    private Integer VALIDATION_ACK = 250;
    private Integer VALIDATION_FAIL = 450;
    private Integer CODE_ACK = 251;
    private Integer CODE_FAIL = 451;

    public class AuthValues {
        @SerializedName("email")
        @Expose
        public String email;

        @SerializedName("city")
        @Expose
        public String city;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        editCode = (EditText) findViewById(R.id.activity_auth_edittext_code);
        editCode.setEnabled(false);
        ackAuthButton = (Button) findViewById(R.id.activity_auth_button_ackauth);
        ackAuthButton.setEnabled(false);
    }

    public void onClickButtonGetCode(View view) {
        EditText editCity = (EditText) findViewById(R.id.activity_auth_edittext_city);
        EditText editEmail = (EditText) findViewById(R.id.activity_auth_edittext_email);

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
//            Toast toast = Toast.makeText(this, "Send query to the server", Toast.LENGTH_SHORT);
//            toast.show();
            AuthValues authValues = new AuthValues();
            authValues.city = editCity.getText().toString();
            authValues.email = editEmail.getText().toString();

            Gson gson = new Gson();
            String json = gson.toJson(authValues);

            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("message", json)
                    .build();
            Request request = new Request.Builder()
                    .url(getString(R.string.url_auth))
                    .post(formBody)
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
                            if (responseCode == VALIDATION_FAIL) {
                                Toast toast = Toast.makeText(AuthActivity.this, "Невалидный город или Email", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            else if (responseCode == VALIDATION_ACK) {
                                editCode.setEnabled(true);
                                ackAuthButton.setEnabled(true);

                                Toast toast = Toast.makeText(AuthActivity.this, "Сообщение с кодом отправлено на указанный адрес", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            else {
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
        }
        else {
            String json = "{\"code\" : \"" + code + "\"}";
            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("message", json)
                    .build();
            Request request = new Request.Builder()
                    .url(getString(R.string.url_auth))
                    .post(formBody)
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
                            if (responseCode == CODE_FAIL) {
                                Toast toast = Toast.makeText(AuthActivity.this, "Неверный код авторизации", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            else if (responseCode == CODE_ACK) {
                                AuthActivity.this.finish();
                                // TODO добавь возврат результата в MainActivity
                            }
                            else {
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
