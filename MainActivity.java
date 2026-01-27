package com.example.unit_converter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText etInput;
    Spinner spinnerFrom, spinnerTo;
    TextView tvResult;
    Button btnConvert,btnHistory;

    LinearLayout categoryContainer;

    String currentCategory = "Length";

    Map<String, String[]> unitCategories = new HashMap<>();
    Map<String, Map<String, Double>> categoryFactorMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get category from Home Screen
        String selectedCategory = getIntent().getStringExtra("CATEGORY");
        if (selectedCategory != null) {
            currentCategory = selectedCategory;
        }

        initializeViews();
        initializeData();
        setupCategoryTabs();
        updateSpinners(currentCategory);
    }

    private void initializeViews() {
        etInput = findViewById(R.id.etInput);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        tvResult = findViewById(R.id.tvResult);
        btnConvert = findViewById(R.id.btnConvert);
        categoryContainer = findViewById(R.id.categoryContainer);
        btnHistory = findViewById(R.id.btnHistory);

        btnHistory.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, history.class))
        );

        tvResult.setText(""); // No default 0

        btnConvert.setOnClickListener(v -> calculateResult());
    }

    private void initializeData() {

        // LENGTH (Base: Meter)
        Map<String, Double> length = new HashMap<>();
        length.put("Meter", 1.0);
        length.put("Kilometer", 1000.0);
        length.put("Centimeter", 0.01);
        length.put("Millimeter", 0.001);

        // WEIGHT (Base: Kilogram)
        Map<String, Double> weight = new HashMap<>();
        weight.put("Kilogram", 1.0);
        weight.put("Gram", 0.001);
        weight.put("Pound", 0.453592);

        // AREA (Base: Square Meter)
        Map<String, Double> area = new HashMap<>();
        area.put("Square Meter", 1.0);
        area.put("Square Kilometer", 1_000_000.0);
        area.put("Square Centimeter", 0.0001);

        // TIME (Base: Second)
        Map<String, Double> time = new HashMap<>();
        time.put("Second", 1.0);
        time.put("Minute", 60.0);
        time.put("Hour", 3600.0);
        time.put("Day", 86400.0);

        // VOLUME (Base: Liter)
        Map<String, Double> volume = new HashMap<>();
        volume.put("Liter", 1.0);
        volume.put("Milliliter", 0.001);
        volume.put("Cubic Meter", 1000.0);
        volume.put("Gallon", 3.78541);

        unitCategories.put("Length", length.keySet().toArray(new String[0]));
        unitCategories.put("Weight", weight.keySet().toArray(new String[0]));
        unitCategories.put("Area", area.keySet().toArray(new String[0]));
        unitCategories.put("Time", time.keySet().toArray(new String[0]));
        unitCategories.put("Volume", volume.keySet().toArray(new String[0]));
        unitCategories.put("Temperature", new String[]{"Celsius", "Fahrenheit", "Kelvin"});

        categoryFactorMap.put("Length", length);
        categoryFactorMap.put("Weight", weight);
        categoryFactorMap.put("Area", area);
        categoryFactorMap.put("Time", time);
        categoryFactorMap.put("Volume", volume);
    }

    private void setupCategoryTabs() {
        categoryContainer.removeAllViews();

        String[] categories = {
                "Length",
                "Temperature",
                "Weight",
                "Time",
                "Area",
                "Volume"
        };

        for (String category : categories) {
            Button btn = new Button(this);
            btn.setText(category);
            btn.setAllCaps(false);

            btn.setOnClickListener(v -> {
                currentCategory = category;
                updateSpinners(category);
                tvResult.setText("");
            });

            categoryContainer.addView(btn);
        }
    }

    private void updateSpinners(String category) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                unitCategories.get(category)
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
    }

    private void calculateResult() {

        String inputStr = etInput.getText().toString().trim();

        if (inputStr.isEmpty() || inputStr.equals(".")) {
            tvResult.setText("");
            Toast.makeText(this, "Enter a value", Toast.LENGTH_SHORT).show();
            return;
        }

        double inputValue = Double.parseDouble(inputStr);
        String fromUnit = spinnerFrom.getSelectedItem().toString();
        String toUnit = spinnerTo.getSelectedItem().toString();

        double result;

        try {
            if (currentCategory.equals("Temperature")) {
                result = convertTemperature(inputValue, fromUnit, toUnit);
            } else {
                Map<String, Double> factors = categoryFactorMap.get(currentCategory);
                double baseValue = inputValue * factors.get(fromUnit);
                result = baseValue / factors.get(toUnit);
            }

            tvResult.setText(String.format("%.4f", result));
            // Save to SharedPreferences
            saveHistory(inputValue, fromUnit, toUnit, result);

        } catch (Exception e) {
            tvResult.setText("");
        }
    }
    private void saveHistory(double input, String from, String to, double result) {
        // Format: category|input|from|to|result
        String entry = currentCategory + "|" + input + "|" + from + "|" + to + "|" + result;

        // Save in SharedPreferences
        getSharedPreferences("CONVERSION_HISTORY", MODE_PRIVATE)
                .edit()
                .putString("history", entry + "\n" + getSharedPreferences("CONVERSION_HISTORY", MODE_PRIVATE).getString("history", ""))
                .apply();
    }

    private double convertTemperature(double value, String from, String to) {

        double celsius;

        if (from.equals("Celsius")) {
            celsius = value;
        } else if (from.equals("Fahrenheit")) {
            celsius = (value - 32) * 5 / 9;
        } else {
            celsius = value - 273.15;
        }

        if (to.equals("Celsius")) return celsius;
        if (to.equals("Fahrenheit")) return (celsius * 9 / 5) + 32;
        return celsius + 273.15;
    }
}
