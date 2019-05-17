package com.dmitry.pickletax;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;

public class ChangeStatusActivity extends AppCompatActivity {
    private Spinner campusSpinner;
    private Spinner classroomSpinner;
    private Spinner lessonNumberSpinner;

    ArrayList<Classroom> classroomsForSpinner;

    private DBHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_status);

        mDBHelper = new DBHelper(this);

        // campusSpinner
        campusSpinner = (Spinner) findViewById(R.id.activity_change_status_spinner_campus);
        String[] campusForSpinner = mDBHelper.getCampusesForSpinner();
        ArrayAdapter<String> campusSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, campusForSpinner);
        campusSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campusSpinner.setAdapter(campusSpinnerAdapter);
        campusSpinner.setSelection(0); // TODO может сломаться

        String campus = campusSpinnerAdapter.getItem(0).toString();
        Classroom[] classrooms = mDBHelper.getClassroomsForSpinner(campus);
        String[] classroomsNames = new String[classrooms.length];
        for (int i = 0; i < classrooms.length; i++)
            classroomsNames[i] = classrooms[i].getName();

        // classroomSpinner
        classroomSpinner = (Spinner) findViewById(R.id.activity_change_status_spinner_classroom);
        final ArrayAdapter<String> classroomSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, classroomsNames);
        classroomSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classroomSpinner.setAdapter(classroomSpinnerAdapter);
        classroomSpinner.setSelection(0); // TODO может сломаться

        campusSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String campus = (String) campusSpinner.getSelectedItem();
                Classroom[] classrooms = mDBHelper.getClassroomsForSpinner(campus);
                String[] classroomsNames = new String[classrooms.length];
                for (int i = 0; i < classrooms.length; i++)
                    classroomsNames[i] = classrooms[i].getName();

                classroomSpinnerAdapter.clear();
                classroomSpinnerAdapter.addAll(Arrays.asList(classroomsNames));
                classroomSpinnerAdapter.notifyDataSetChanged();
            }
        });

        // lessonNumberSpinner
        lessonNumberSpinner = (Spinner) findViewById(R.id.activity_change_status_spinner_lesson_number);
        int maxLessonNumber = mDBHelper.getMaxLessonNumber();
        String[] lessonNumberForSpinner = new String[maxLessonNumber];
        for (Integer i = 1; i <= maxLessonNumber; i++)
            lessonNumberForSpinner[i] = "Занятие №" + i.toString();
        ArrayAdapter<String> lessonNumberSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, lessonNumberForSpinner);
        lessonNumberSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lessonNumberSpinner.setAdapter(lessonNumberSpinnerAdapter);
        lessonNumberSpinner.setSelection(0); // TODO может сломаться
    }

    // TODO доделай инициализацию activity
}
