package com.adeshchandra.smartcalc;

import android.os.*;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class SimpleCalcActivity extends AppCompatActivity {

    private TextView tvDisplay, tvExpression;
    private StringBuilder expr = new StringBuilder();
    private boolean justResult = false;
    private List<String> history = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_calc);

        tvDisplay    = findViewById(R.id.tvDisplay);
        tvExpression = findViewById(R.id.tvExpression);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        setupButtons();
    }

    private void setupButtons() {
        int[] numIds = {
            R.id.btn0,R.id.btn1,R.id.btn2,R.id.btn3,R.id.btn4,
            R.id.btn5,R.id.btn6,R.id.btn7,R.id.btn8,R.id.btn9
        };
        String[] numLabels = {"0","1","2","3","4","5","6","7","8","9"};
        for (int i = 0; i < numIds.length; i++) {
            final String lbl = numLabels[i];
            findViewById(numIds[i]).setOnClickListener(v -> onInput(lbl));
        }

        findViewById(R.id.btnDot).setOnClickListener(v -> onInput("."));
        findViewById(R.id.btnPlus).setOnClickListener(v -> onOp("+"));
        findViewById(R.id.btnMinus).setOnClickListener(v -> onOp("−"));
        findViewById(R.id.btnMul).setOnClickListener(v -> onOp("×"));
        findViewById(R.id.btnDiv).setOnClickListener(v -> onOp("÷"));
        findViewById(R.id.btnPercent).setOnClickListener(v -> onOp("%"));
        findViewById(R.id.btnPow).setOnClickListener(v -> onOp("^"));

        findViewById(R.id.btnEqual).setOnClickListener(v -> onEqual());
        findViewById(R.id.btnClear).setOnClickListener(v -> onClear());
        findViewById(R.id.btnDel).setOnClickListener(v -> onDel());
        findViewById(R.id.btnPlusMinus).setOnClickListener(v -> onPlusMinus());
        findViewById(R.id.btnSqrt).setOnClickListener(v -> onSqrt());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void onInput(String s) {
        if (justResult && !s.equals(".")) {
            expr.setLength(0);
            justResult = false;
        }
        expr.append(s);
        updateDisplay();
    }

    private void onOp(String op) {
        justResult = false;
        if (expr.length() > 0) {
            char last = expr.charAt(expr.length()-1);
            if ("+-×÷%^".indexOf(last) >= 0) {
                expr.setCharAt(expr.length()-1, op.charAt(0));
            } else {
                expr.append(op);
            }
        } else if (op.equals("−")) {
            expr.append("−");
        }
        updateDisplay();
    }

    private void onEqual() {
        if (expr.length() == 0) return;
        String e = expr.toString();
        String result = CalcEngine.evaluate(e);
        tvExpression.setText(e + " =");
        expr.setLength(0);
        expr.append(result);
        tvDisplay.setText(result);
        history.add(0, e + " = " + result);
        if (history.size() > 50) history.remove(history.size()-1);
        justResult = true;
    }

    private void onClear() {
        expr.setLength(0);
        tvDisplay.setText("0");
        tvExpression.setText("");
        justResult = false;
    }

    private void onDel() {
        if (expr.length() > 0) {
            expr.deleteCharAt(expr.length()-1);
            updateDisplay();
        }
    }

    private void onPlusMinus() {
        if (expr.length() > 0) {
            String s = expr.toString();
            if (s.startsWith("−")) expr.delete(0,1);
            else { expr.insert(0,"−"); }
            updateDisplay();
        }
    }

    private void onSqrt() {
        if (expr.length() > 0) {
            String e = "sqrt(" + expr.toString() + ")";
            String result = CalcEngine.evaluate(e);
            tvExpression.setText("√(" + expr + ") =");
            expr.setLength(0);
            expr.append(result);
            tvDisplay.setText(result);
            justResult = true;
        }
    }

    private void updateDisplay() {
        String s = expr.toString();
        tvDisplay.setText(s.isEmpty() ? "0" : s);
        // Show live result preview
        if (s.length() > 1) {
            String preview = CalcEngine.evaluate(s);
            if (!preview.equals("Error")) tvExpression.setText("= " + preview);
            else tvExpression.setText("");
        } else {
            tvExpression.setText("");
        }
    }
}
