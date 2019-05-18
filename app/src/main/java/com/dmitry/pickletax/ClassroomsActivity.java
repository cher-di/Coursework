package com.dmitry.pickletax;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;

import static com.dmitry.pickletax.Constants.CLASSROOM_BUSY;
import static com.dmitry.pickletax.Constants.CLASSROOM_FREE;

public class ClassroomsActivity extends AppCompatActivity {
    private Spinner campusSpinner;
    private Spinner lessonNumberSpinner;
    private RadioGroup statusRadioGroup;

    private DBHelper mDBHelper;

    ArrayList<Classroom> classroomsForRecyclerView;

    RecyclerAdapterClassrooms recyclerAdapterClassrooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classrooms);

        mDBHelper = new DBHelper(this);

        // campusSpinner
        campusSpinner = (Spinner) findViewById(R.id.activity_classrooms_spinner_campus);
        String[] campusForSpinner = mDBHelper.getCampusesForSpinner();
        ArrayAdapter<String> campusSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, campusForSpinner);
        campusSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campusSpinner.setAdapter(campusSpinnerAdapter);

        campusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                classroomsForRecyclerView.clear();
                recyclerAdapterClassrooms.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                campusSpinner.setSelection(0); // TODO может сломаться
            }
        });

        // campusSpinner.setSelection(0); // TODO может сломаться

        // lessonNumberSpinner
        lessonNumberSpinner = (Spinner) findViewById(R.id.activity_classrooms_spinner_lesson_number);
        int maxLessonNumber = mDBHelper.getMaxLessonNumber();
        String[] lessonNumberForSpinner = new String[maxLessonNumber];
        for (int i = 0; i < maxLessonNumber; i++)
            lessonNumberForSpinner[i] = "Занятие №" + Integer.toString(i + 1);
        ArrayAdapter<String> lessonNumberSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, lessonNumberForSpinner);
        lessonNumberSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lessonNumberSpinner.setAdapter(lessonNumberSpinnerAdapter);

        lessonNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                classroomsForRecyclerView.clear();
                recyclerAdapterClassrooms.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                lessonNumberSpinner.setSelection(0); // TODO может сломаться
            }
        });

        // lessonNumberSpinner.setSelection(0); // TODO может сломаться

        // classroomsRecyclerView
        RecyclerView classroomsRecyclerView = (RecyclerView) findViewById(R.id.activity_classrooms_recyclerview_classrooms);
        classroomsForRecyclerView = new ArrayList<Classroom>();
        recyclerAdapterClassrooms = new RecyclerAdapterClassrooms(this, classroomsForRecyclerView);
        classroomsRecyclerView.setAdapter(recyclerAdapterClassrooms);

        statusRadioGroup = (RadioGroup) findViewById(R.id.activity_classrooms_radiogroup_status);

    }

    public void onClickButtonSearch(View view) {
        String campus = campusSpinner.getSelectedItem().toString();
        int lesson_number = lessonNumberSpinner.getSelectedItemPosition() + 1;
        int status = statusRadioGroup.getCheckedRadioButtonId() == R.id.activity_classrooms_radiobutton_free ?
                CLASSROOM_FREE : CLASSROOM_BUSY;

        Classroom classrooms[] = mDBHelper.getClassroomsForShow(campus, status, lesson_number);

        classroomsForRecyclerView.clear();
        classroomsForRecyclerView.addAll(Arrays.asList(classrooms));
        recyclerAdapterClassrooms.notifyDataSetChanged();
    }
}
