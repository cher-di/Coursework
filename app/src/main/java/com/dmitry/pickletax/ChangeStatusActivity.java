package com.dmitry.pickletax;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.dmitry.pickletax.Constants.CHANGE_STATUS_ACK;
import static com.dmitry.pickletax.Constants.CHANGE_STATUS_FAIL;
import static com.dmitry.pickletax.Constants.CLASSROOM_BUSY;
import static com.dmitry.pickletax.Constants.CLASSROOM_BUSY_STRING;
import static com.dmitry.pickletax.Constants.CLASSROOM_FREE;
import static com.dmitry.pickletax.Constants.CLASSROOM_FREE_STRING;
import static com.dmitry.pickletax.Constants.CONNECT_TIMEOUT;
import static com.dmitry.pickletax.Constants.JSON;
import static com.dmitry.pickletax.Constants.NULL_DESCRIPTION;
import static com.dmitry.pickletax.Constants.SERVER_ERROR;

public class ChangeStatusActivity extends AppCompatActivity {
    private Spinner campusSpinner;
    private Spinner classroomSpinner;
    private Spinner lessonNumberSpinner;

    private TextView statusTextView;
    private TextView descriptionTextView;
    private EditText addDesciptionEditText;
    private Button changeStatusButton;

    Classroom[] classroomsForSpinner;
    ArrayList<String> classroomsNames;

    private DBHelper mDBHelper;

    ArrayAdapter<String> classroomSpinnerAdapter;

    private class ChangeStatusValues {
        @SerializedName("email")
        @Expose
        private String email;

        @SerializedName("campus_name")
        @Expose
        private String campus_name;

        @SerializedName("classroom_name")
        @Expose
        private String classroom_name;

        @SerializedName("lesson_number")
        @Expose
        private int lesson_number;

        @SerializedName("new_classroom_status")
        @Expose
        private String new_classroom_status;

        @SerializedName("description")
        @Expose
        private String description = NULL_DESCRIPTION;

        public ChangeStatusValues(String email, String campus_name, String classroom_name, int lesson_number, int new_classroom_status, String description) {
            this.email = email;
            this.campus_name = campus_name;
            this.classroom_name = classroom_name;
            this.lesson_number = lesson_number;
            this.new_classroom_status = new_classroom_status == CLASSROOM_FREE ? CLASSROOM_FREE_STRING : CLASSROOM_BUSY_STRING;
            this.description = description;
        }

        public String getEmail() {
            return email;
        }

        public String getCampus_name() {
            return campus_name;
        }

        public String getClassroom_name() {
            return classroom_name;
        }

        public int getLesson_number() {
            return lesson_number;
        }

        public String getNew_classroom_status() {
            return new_classroom_status;
        }

        public String getDescription() {
            return description;
        }
    }

    ChangeStatusValues changeStatusValues;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_status);

        mDBHelper = new DBHelper(this);

        statusTextView = (TextView) findViewById(R.id.activity_change_status_textview_status);
        descriptionTextView = (TextView) findViewById(R.id.activity_change_status_textview_description);
        addDesciptionEditText = (EditText) findViewById(R.id.activity_change_status_edittext_add_description);
        addDesciptionEditText.setEnabled(false);
        changeStatusButton = (Button) findViewById(R.id.activity_change_status_button_change_status);

        // campusSpinner
        campusSpinner = (Spinner) findViewById(R.id.activity_change_status_spinner_campus);
        String[] campusForSpinner = mDBHelper.getCampusesForSpinner();
        ArrayAdapter<String> campusSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, campusForSpinner);
        campusSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campusSpinner.setAdapter(campusSpinnerAdapter);
        campusSpinner.setSelection(0); // TODO может сломаться

        String campus = campusSpinnerAdapter.getItem(0).toString();
        classroomsForSpinner = mDBHelper.getClassroomsForSpinner(campus);
        classroomsNames = new ArrayList<String>();
        for (Classroom classroom : classroomsForSpinner)
            classroomsNames.add(classroom.getName());

        campusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String campus = (String) campusSpinner.getSelectedItem();
                classroomsForSpinner = mDBHelper.getClassroomsForSpinner(campus);
                classroomsNames.clear();
                for (Classroom classroom : classroomsForSpinner)
                    classroomsNames.add(classroom.getName());

                classroomSpinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                campusSpinner.setSelection(0);
            }
        });

        // classroomSpinner
        classroomSpinner = (Spinner) findViewById(R.id.activity_change_status_spinner_classroom);
        classroomSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, classroomsNames);
        classroomSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classroomSpinner.setAdapter(classroomSpinnerAdapter);
        classroomSpinner.setSelection(0); // TODO может сломаться

        classroomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String campus = campusSpinner.getSelectedItem().toString();
                String classroom_name = classroomSpinner.getSelectedItem().toString();
                int lesson_number = lessonNumberSpinner.getSelectedItemPosition() + 1;
                ScheduleItem currScheduleItem = mDBHelper.getScheduleItemForChangeStatus(campus, classroom_name, lesson_number);

                statusTextView.setText(currScheduleItem.getStatus() == CLASSROOM_FREE ? "Свободна" : "Занята");
                if (currScheduleItem.getStatus() == CLASSROOM_FREE) {
                    statusTextView.setText("Свободна");
                    descriptionTextView.setText("");
                    addDesciptionEditText.setEnabled(true);
                    changeStatusButton.setText("Занять");
                } else {
                    statusTextView.setText("Занята");
                    String description = currScheduleItem.getDescription() != null ? currScheduleItem.getDescription() : "Описание отсутствует";
                    descriptionTextView.setText(description);
                    addDesciptionEditText.setEnabled(false);
                    changeStatusButton.setText("Освободить");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                classroomSpinner.setSelection(0);
            }
        });

        // lessonNumberSpinner
        lessonNumberSpinner = (Spinner) findViewById(R.id.activity_change_status_spinner_lesson_number);
        int maxLessonNumber = mDBHelper.getMaxLessonNumber();
        String[] lessonNumberForSpinner = new String[maxLessonNumber];
        for (int i = 0; i < maxLessonNumber; i++)
            lessonNumberForSpinner[i] = "Занятие №" + Integer.toString(i + 1);
        ArrayAdapter<String> lessonNumberSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, lessonNumberForSpinner);
        lessonNumberSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lessonNumberSpinner.setAdapter(lessonNumberSpinnerAdapter);
        lessonNumberSpinner.setSelection(0); // TODO может сломаться

        lessonNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String campus = campusSpinner.getSelectedItem().toString();
                String classroom_name = classroomSpinner.getSelectedItem().toString();
                int lesson_number = lessonNumberSpinner.getSelectedItemPosition() + 1;
                ScheduleItem currScheduleItem = mDBHelper.getScheduleItemForChangeStatus(campus, classroom_name, lesson_number);

                statusTextView.setText(currScheduleItem.getStatus() == CLASSROOM_FREE ? "Свободна" : "Занята");
                if (currScheduleItem.getStatus() == CLASSROOM_FREE) {
                    statusTextView.setText("Свободна");
                    descriptionTextView.setText("");
                    addDesciptionEditText.setEnabled(true);
                    changeStatusButton.setText("Занять");
                } else {
                    statusTextView.setText("Занята");
                    String description = currScheduleItem.getDescription() != null ? currScheduleItem.getDescription() : "Описание отсутствует";
                    descriptionTextView.setText(description);
                    addDesciptionEditText.setEnabled(false);
                    changeStatusButton.setText("Освободить");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                lessonNumberSpinner.setSelection(0);
            }
        });
    }

    public void OnClickButtonChangeStatus(View view) {
        String email = mDBHelper.getServiceVars().email;
        String campus_name = campusSpinner.getSelectedItem().toString();
        String classroom_name = classroomSpinner.getSelectedItem().toString();
        int lesson_number = lessonNumberSpinner.getSelectedItemPosition() + 1;

        ScheduleItem currScheduleItem = mDBHelper.getScheduleItemForChangeStatus(campus_name, classroom_name, lesson_number);
        int new_classroom_status = currScheduleItem.getStatus() == CLASSROOM_FREE ? CLASSROOM_BUSY : CLASSROOM_FREE;
        String description = NULL_DESCRIPTION;
        if (new_classroom_status == CLASSROOM_BUSY && !addDesciptionEditText.getText().toString().isEmpty())
            description = addDesciptionEditText.getText().toString();

        changeStatusValues = new ChangeStatusValues(email, campus_name, classroom_name, lesson_number,
                new_classroom_status, description);
        Gson gson = new Gson();
        String json = gson.toJson(changeStatusValues);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(getString(R.string.url_change_status))
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                ChangeStatusActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChangeStatusActivity.this, "Проблемы с сетью", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                ChangeStatusActivity.this.runOnUiThread(new Runnable() {
                    final int responseCode = response.code();

                    @Override
                    public void run() {
                        if (responseCode == CHANGE_STATUS_FAIL) {
                            Toast.makeText(ChangeStatusActivity.this, "Отказано в изменении статуса", Toast.LENGTH_SHORT).show();
                        } else if (responseCode == CHANGE_STATUS_ACK) {
                            int new_classroom_status = changeStatusValues.getNew_classroom_status() == CLASSROOM_FREE_STRING ? CLASSROOM_FREE : CLASSROOM_BUSY;
                            mDBHelper.updateClassroomStatus(changeStatusValues.getCampus_name(), changeStatusValues.getClassroom_name(),
                                    changeStatusValues.getLesson_number(), new_classroom_status, changeStatusValues.getDescription());

                            classroomSpinner.setSelection(classroomSpinner.getSelectedItemPosition()); // это вызывает обновление activity
                            Toast.makeText(ChangeStatusActivity.this, "Статус аудитории изменен", Toast.LENGTH_SHORT).show();
                        } else if (responseCode == SERVER_ERROR) {
                            Toast.makeText(ChangeStatusActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(ChangeStatusActivity.this, "Unexpected HTTP code: " + Integer.toString(responseCode), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

    }
}
