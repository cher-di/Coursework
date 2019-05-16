package com.dmitry.pickletax;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;

public class ClassroomsActivity extends AppCompatActivity {
    private Spinner campusSpinner;
    private Spinner lessonNumberSpinner;
    private DBHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classrooms);

        mDBHelper = new DBHelper(this);

        campusSpinner = (Spinner)findViewById(R.id.activity_classrooms_spinner_campus);
        String campusForSpinner[] = mDBHelper.getCampusesForSpinner();
        ArrayAdapter<String> campusSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, campusForSpinner);
        campusSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campusSpinner.setAdapter(campusSpinnerAdapter);

        lessonNumberSpinner = (Spinner)findViewById(R.id.activity_classrooms_spinner_lesson_number);
        int maxLessonNumber = mDBHelper.getMaxLessonNumber();
        String lessonNumberForSpinner[] = new String[maxLessonNumber];
        for (Integer i = 1; i <= maxLessonNumber; i++)
            lessonNumberForSpinner[i] = "Занятие №" + i.toString();
        ArrayAdapter<String> lessonNumberSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, lessonNumberForSpinner);
        lessonNumberSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lessonNumberSpinner.setAdapter(lessonNumberSpinnerAdapter);
    }
}
