package com.adeshchandra.smartcalc;

import android.os.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import java.util.*;

public class ConverterActivity extends AppCompatActivity {

    private LinearLayout categoryContainer;
    private TextView tvCategoryTitle;
    private Spinner spinnerFrom, spinnerTo;
    private EditText etInput;
    private TextView tvResult, tvFormula;
    private int selectedCat = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        categoryContainer = findViewById(R.id.categoryContainer);
        tvCategoryTitle   = findViewById(R.id.tvCategoryTitle);
        spinnerFrom       = findViewById(R.id.spinnerFrom);
        spinnerTo         = findViewById(R.id.spinnerTo);
        etInput           = findViewById(R.id.etInput);
        tvResult          = findViewById(R.id.tvResult);
        tvFormula         = findViewById(R.id.tvFormula);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSwap).setOnClickListener(v -> swapUnits());

        buildCategoryBar();
        selectCategory(0);
        setupInputListener();
    }

    private void buildCategoryBar() {
        for (int i = 0; i < UnitConverter.CATEGORIES.length; i++) {
            final int idx = i;
            UnitConverter.Category cat = UnitConverter.CATEGORIES[i];

            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(android.view.Gravity.CENTER);
            item.setPadding(20, 16, 20, 16);

            TextView emoji = new TextView(this);
            emoji.setText(cat.emoji);
            emoji.setTextSize(22);
            emoji.setGravity(android.view.Gravity.CENTER);

            TextView label = new TextView(this);
            label.setText(cat.name.split(" ")[0]);
            label.setTextSize(10);
            label.setTextColor(0xFFB0B0CC);
            label.setGravity(android.view.Gravity.CENTER);
            label.setMaxLines(1);

            item.addView(emoji);
            item.addView(label);
            item.setOnClickListener(v -> selectCategory(idx));
            categoryContainer.addView(item);
        }
    }

    private void selectCategory(int idx) {
        selectedCat = idx;
        UnitConverter.Category cat = UnitConverter.CATEGORIES[idx];
        tvCategoryTitle.setText(cat.emoji + "  " + cat.name);

        // Highlight selected
        for (int i = 0; i < categoryContainer.getChildCount(); i++) {
            View child = categoryContainer.getChildAt(i);
            child.setBackgroundColor(i == idx ? 0xFF2A2A50 : 0x00000000);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, cat.units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
        if (cat.units.length > 1) spinnerTo.setSelection(1);

        etInput.setText("");
        tvResult.setText("—");
        tvFormula.setText("");
    }

    private void swapUnits() {
        int fromPos = spinnerFrom.getSelectedItemPosition();
        int toPos   = spinnerTo.getSelectedItemPosition();
        spinnerFrom.setSelection(toPos);
        spinnerTo.setSelection(fromPos);
        doConvert();
    }

    private void setupInputListener() {
        etInput.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            public void onTextChanged(CharSequence s, int st, int b, int c) { doConvert(); }
            public void afterTextChanged(Editable s) {}
        });

        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) { doConvert(); }
            public void onNothingSelected(AdapterView<?> p) {}
        });

        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) { doConvert(); }
            public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    private void doConvert() {
        String input = etInput.getText().toString().trim();
        if (input.isEmpty()) { tvResult.setText("—"); tvFormula.setText(""); return; }
        try {
            double value  = Double.parseDouble(input);
            int fromIdx   = spinnerFrom.getSelectedItemPosition();
            int toIdx     = spinnerTo.getSelectedItemPosition();
            String result = UnitConverter.convert(selectedCat, value, fromIdx, toIdx);
            UnitConverter.Category cat = UnitConverter.CATEGORIES[selectedCat];

            tvResult.setText(result + " " + cat.units[toIdx]);
            tvFormula.setText(input + " " + cat.units[fromIdx] + " = " +
                    result + " " + cat.units[toIdx]);
        } catch (NumberFormatException e) {
            tvResult.setText("Invalid");
            tvFormula.setText("");
        }
    }
}
