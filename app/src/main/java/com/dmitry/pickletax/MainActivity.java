package com.dmitry.pickletax;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void noClickButtonUpdate(View view) {
        Toast toast = Toast.makeText(this, "Update button pressed", Toast.LENGTH_SHORT);
        toast.show();
        // TODO замени на рабочий код
    }


}
