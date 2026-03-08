package com.adeshchandra.smartcalc;

import android.os.*;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class ScientificCalcActivity extends AppCompatActivity {

    private TextView tvDisplay, tvExpression;
    private StringBuilder expr = new StringBuilder();
    private boolean justResult = false;
    private boolean isDeg = true; // deg vs rad mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scientific_calc);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        tvDisplay    = findViewById(R.id.tvDisplay);
        tvExpression = findViewById(R.id.tvExpression);

        setupButtons();
    }

    private void setupButtons() {
        // Number buttons
        int[] numIds = {
            R.id.btn0,R.id.btn1,R.id.btn2,R.id.btn3,R.id.btn4,
            R.id.btn5,R.id.btn6,R.id.btn7,R.id.btn8,R.id.btn9
        };
        for (int i = 0; i < numIds.length; i++) {
            final String n = String.valueOf(i);
            findViewById(numIds[i]).setOnClickListener(v -> append(n));
        }

        // Basic ops
        findViewById(R.id.btnDot).setOnClickListener(v -> append("."));
        findViewById(R.id.btnPlus).setOnClickListener(v -> appendOp("+"));
        findViewById(R.id.btnMinus).setOnClickListener(v -> appendOp("−"));
        findViewById(R.id.btnMul).setOnClickListener(v -> appendOp("×"));
        findViewById(R.id.btnDiv).setOnClickListener(v -> appendOp("÷"));
        findViewById(R.id.btnPow).setOnClickListener(v -> appendOp("^"));
        findViewById(R.id.btnPercent).setOnClickListener(v -> appendOp("%"));

        // Brackets
        findViewById(R.id.btnOpenBr).setOnClickListener(v -> append("("));
        findViewById(R.id.btnCloseBr).setOnClickListener(v -> append(")"));

        // Functions
        findViewById(R.id.btnSin).setOnClickListener(v -> appendFunc("sin("));
        findViewById(R.id.btnCos).setOnClickListener(v -> appendFunc("cos("));
        findViewById(R.id.btnTan).setOnClickListener(v -> appendFunc("tan("));
        findViewById(R.id.btnAsin).setOnClickListener(v -> appendFunc("asin("));
        findViewById(R.id.btnAcos).setOnClickListener(v -> appendFunc("acos("));
        findViewById(R.id.btnAtan).setOnClickListener(v -> appendFunc("atan("));
        findViewById(R.id.btnSinh).setOnClickListener(v -> appendFunc("sinh("));
        findViewById(R.id.btnCosh).setOnClickListener(v -> appendFunc("cosh("));
        findViewById(R.id.btnTanh).setOnClickListener(v -> appendFunc("tanh("));
        findViewById(R.id.btnSqrt).setOnClickListener(v -> appendFunc("sqrt("));
        findViewById(R.id.btnCbrt).setOnClickListener(v -> appendFunc("cbrt("));
        findViewById(R.id.btnLog).setOnClickListener(v -> appendFunc("log("));
        findViewById(R.id.btnLn).setOnClickListener(v -> appendFunc("ln("));
        findViewById(R.id.btnLog2).setOnClickListener(v -> appendFunc("log2("));
        findViewById(R.id.btnExp).setOnClickListener(v -> appendFunc("exp("));
        findViewById(R.id.btnAbs).setOnClickListener(v -> appendFunc("abs("));
        findViewById(R.id.btnFact).setOnClickListener(v -> appendFunc("fact("));
        findViewById(R.id.btnFloor).setOnClickListener(v -> appendFunc("floor("));
        findViewById(R.id.btnCeil).setOnClickListener(v -> appendFunc("ceil("));

        // Constants
        findViewById(R.id.btnPi).setOnClickListener(v -> append("π"));
        findViewById(R.id.btnE).setOnClickListener(v -> append("ℯ"));
        findViewById(R.id.btnPow2).setOnClickListener(v -> appendOp("^2"));
        findViewById(R.id.btnPow3).setOnClickListener(v -> appendOp("^3"));

        // Control
        findViewById(R.id.btnEqual).setOnClickListener(v -> onEqual());
        findViewById(R.id.btnClear).setOnClickListener(v -> onClear());
        findViewById(R.id.btnDel).setOnClickListener(v -> onDel());
        findViewById(R.id.btnPlusMinus).setOnClickListener(v -> onPlusMinus());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void append(String s) {
        if (justResult && Character.isDigit(s.charAt(0))) {
            expr.setLength(0);
            justResult = false;
        }
        justResult = false;
        expr.append(s);
        updateDisplay();
    }

    private void appendOp(String op) {
        justResult = false;
        if (expr.length() > 0) {
            expr.append(op);
            updateDisplay();
        }
    }

    private void appendFunc(String fn) {
        justResult = false;
        expr.append(fn);
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
            // smart delete: remove whole function name
            String s = expr.toString();
            String[] fns = {"sinh(","cosh(","tanh(","asin(","acos(","atan(","sin(","cos(","tan(",
                "sqrt(","cbrt(","log2(","log(","ln(","exp(","abs(","fact(","floor(","ceil("};
            for (String fn : fns) {
                if (s.endsWith(fn)) { expr.delete(expr.length()-fn.length(), expr.length()); updateDisplay(); return; }
            }
            expr.deleteCharAt(expr.length()-1);
            updateDisplay();
        }
    }

    private void onPlusMinus() {
        if (expr.length() > 0) {
            String s = expr.toString();
            if (s.startsWith("−")) expr.delete(0,1);
            else expr.insert(0,"−");
            updateDisplay();
        }
    }

    private void updateDisplay() {
        String s = expr.toString();
        tvDisplay.setText(s.isEmpty() ? "0" : s);
        if (s.length() > 1) {
            String preview = CalcEngine.evaluate(s);
            if (!preview.equals("Error")) tvExpression.setText("= " + preview);
            else tvExpression.setText("");
        } else {
            tvExpression.setText("");
        }
    }
}
