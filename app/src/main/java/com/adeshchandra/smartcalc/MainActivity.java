package com.adeshchandra.smartcalc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.cardSimple).setOnClickListener(v ->
            startActivity(new Intent(this, SimpleCalcActivity.class)));

        findViewById(R.id.cardScientific).setOnClickListener(v ->
            startActivity(new Intent(this, ScientificCalcActivity.class)));

        findViewById(R.id.cardConverter).setOnClickListener(v ->
            startActivity(new Intent(this, ConverterActivity.class)));
    }
}
