package com.example.unit_converter;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class converter extends AppCompatActivity {

    // UI
    private EditText etInput;
    private TextView tvResult;
    private Spinner spinnerFrom, spinnerTo;
    private LinearLayout categoryContainer;

    // SharedPreferences
    private SharedPreferences prefs;

    // Conversion Data
    private final Map<String, Double> lengthFactors = new HashMap<>();
    private final Map<String, Double> weightFactors = new HashMap<>();
    private final Map<String, Double> areaFactors = new HashMap<>();

    private final Map<String, String[]> unitCategories = new HashMap<>();
    private final Map<String, Map<String, Double>> categoryFactorMap = new HashMap<>();

    private String currentCategory = "Length";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("CONVERSION_HISTORY", MODE_PRIVATE);

        initializeData();
        initializeViews();
        setupCategoryTabs();
    }

    // ---------------- DATA SETUP ----------------

    private void initializeData() {

        // LENGTH (Base: Meter)
        lengthFactors.put("Meter", 1.0);
        lengthFactors.put("Kilometer", 1000.0);
        lengthFactors.put("Centimeter", 0.01);
        lengthFactors.put("Millimeter", 0.001);
        lengthFactors.put("Inch", 0.0254);
        lengthFactors.put("Foot", 0.3048);
        lengthFactors.put("Mile", 1609.34);

        // WEIGHT (Base: Kilogram)
        weightFactors.put("Kilogram", 1.0);
        weightFactors.put("Gram", 0.001);
        weightFactors.put("Pound", 0.453592);
        weightFactors.put("Ounce", 0.0283495);
        weightFactors.put("Ton", 1000.0);

        // AREA (Base: Square Meter)
        areaFactors.put("Square Meter", 1.0);
        areaFactors.put("Square Centimeter", 0.0001);
        areaFactors.put("Square Kilometer", 1_000_000.0);
        areaFactors.put("Square Foot", 0.092903);
        areaFactors.put("Acre", 4046.86);
        areaFactors.put("Hectare", 10000.0);
        areaFactors.put("Are", 100.0);

        // TEMPERATURE
        String[] tempUnits = {"Celsius", "Fahrenheit", "Kelvin"};

        unitCategories.put("Length", lengthFactors.keySet().toArray(new String[0]));
        unitCategories.put("Weight", weightFactors.keySet().toArray(new String[0]));
        unitCategories.put("Area", areaFactors.keySet().toArray(new String[0]));
        unitCategories.put("Temperature", tempUnits);

        categoryFactorMap.put("Length", lengthFactors);
        categoryFactorMap.put("Weight", weightFactors);
        categoryFactorMap.put("Area", areaFactors);
    }

    // ---------------- VIEW SETUP ----------------

    private void initializeViews() {

        etInput = findViewById(R.id.etInput);
        tvResult = findViewById(R.id.tvResult);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        categoryContainer = findViewById(R.id.categoryContainer);

        etInput.addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) { calculateResult(); }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getChildAt(0) instanceof TextView) {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                }
                calculateResult();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerFrom.setOnItemSelectedListener(listener);
        spinnerTo.setOnItemSelectedListener(listener);
    }

    // ---------------- CATEGORY TABS ----------------

    private void setupCategoryTabs() {
        String[] categories = {"Length", "Weight", "Area", "Temperature"};
        categoryContainer.removeAllViews();

        for (String cat : categories) {
            Button btn = new Button(this);
            btn.setText(cat);
            btn.setTextSize(14);
            btn.setTextColor(Color.GRAY);
            btn.setBackgroundColor(Color.TRANSPARENT);
            btn.setPadding(30, 0, 30, 0);

            btn.setOnClickListener(v -> loadCategory(cat, btn));
            categoryContainer.addView(btn);

            if (cat.equals(currentCategory)) {
                loadCategory(cat, btn);
            }
        }
    }

    private void loadCategory(String category, Button selectedBtn) {
        currentCategory = category;

        for (int i = 0; i < categoryContainer.getChildCount(); i++) {
            Button b = (Button) categoryContainer.getChildAt(i);
            b.setTextColor(Color.GRAY);
            b.setTypeface(null, Typeface.NORMAL);
        }

        selectedBtn.setTextColor(Color.WHITE);
        selectedBtn.setTypeface(null, Typeface.BOLD);

        String[] units = unitCategories.get(category);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, units);

        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        spinnerFrom.setSelection(0);
        if (units.length > 1) spinnerTo.setSelection(1);
    }

    // ---------------- CALCULATION ----------------

    private void calculateResult() {
        String inputStr = etInput.getText().toString();
        if (inputStr.isEmpty() || inputStr.equals(".")) {
            tvResult.setText("0");
            return;
        }

        try {
            double input = Double.parseDouble(inputStr);
            String fromUnit = spinnerFrom.getSelectedItem().toString();
            String toUnit = spinnerTo.getSelectedItem().toString();
            double result;

            if (currentCategory.equals("Temperature")) {
                double celsius;
                if (fromUnit.equals("Celsius")) celsius = input;
                else if (fromUnit.equals("Fahrenheit")) celsius = (input - 32) * 5 / 9;
                else celsius = input - 273.15;

                if (toUnit.equals("Celsius")) result = celsius;
                else if (toUnit.equals("Fahrenheit")) result = (celsius * 9 / 5) + 32;
                else result = celsius + 273.15;
            } else {
                Map<String, Double> factors = categoryFactorMap.get(currentCategory);
                result = input * (factors.get(fromUnit) / factors.get(toUnit));
            }

            String formatted = (result % 1 == 0)
                    ? String.format("%.0f", result)
                    : String.format("%.4f", result);

            tvResult.setText(formatted);
            saveHistory(input, fromUnit, toUnit, formatted);

        } catch (Exception e) {
            tvResult.setText("0");
        }
    }

    // ---------------- HISTORY ----------------

    private void saveHistory(double input, String from, String to, String result) {
        String old = prefs.getString("history", "");
        String entry = currentCategory + ": " + input + " " + from + " → " + to + " = " + result;
        prefs.edit().putString("history", entry + "\n" + old).apply();
    }
}
