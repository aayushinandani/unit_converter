package com.example.unit_converter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class history extends AppCompatActivity {

    ListView listView;
    Button btnClear;
    TextView tvEmpty;
    List<String> historyList = new ArrayList<>();
    ArrayAdapter<String> adapter;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = findViewById(R.id.lvHistory);
        btnClear = findViewById(R.id.btnClearHistory);
        tvEmpty = findViewById(R.id.tvEmptyHistory);
        prefs = getSharedPreferences("CONVERSION_HISTORY", MODE_PRIVATE);

        loadHistory();
        checkEmpty();

        // Adapter with simple text
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, historyList) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setPadding(20, 20, 20, 20);
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(16f);

                GradientDrawable gd = new GradientDrawable();
                gd.setCornerRadius(20f);
                gd.setColor(Color.parseColor("#33FFFFFF")); // Transparent white
                gd.setStroke(2, Color.WHITE);
                tv.setBackground(gd);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 10, 0, 10);
                tv.setLayoutParams(params);

                return tv;
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String record = historyList.get(position);
            Intent i = new Intent();
            i.putExtra("HISTORY", record);
            setResult(Activity.RESULT_OK, i);
            finish();
        });

        btnClear.setOnClickListener(v -> {
            prefs.edit().remove("history").apply();
            historyList.clear();
            adapter.notifyDataSetChanged();
            checkEmpty();
            Toast.makeText(this, "History cleared", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadHistory() {
        String raw = prefs.getString("history", "");
        if (!raw.isEmpty()) {
            historyList.addAll(Arrays.asList(raw.split("\n")));
            java.util.Collections.reverse(historyList); // latest first
        }
    }

    private void checkEmpty() {
        if (historyList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }
}
