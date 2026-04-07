package com.example.currencyconverter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText amountEditText;
    private AutoCompleteTextView autoCompleteFrom, autoCompleteTo;
    private TextView resultTextView;
    private final String[] currencies = {"USD", "EUR", "INR", "JPY"};
    private final Map<String, Double> rates = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initViews();
    }

    private void checkTheme() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(isDark ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    private void initData() {
        rates.put("USD", 1.0);
        rates.put("EUR", 0.92);
        rates.put("INR", 83.30);
        rates.put("JPY", 151.60);
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        amountEditText = findViewById(R.id.amountEditText);
        autoCompleteFrom = findViewById(R.id.autoCompleteFrom);
        autoCompleteTo = findViewById(R.id.autoCompleteTo);
        resultTextView = findViewById(R.id.resultTextView);
        MaterialButton convertBtn = findViewById(R.id.convertButton);
        MaterialButton swapBtn = findViewById(R.id.swapButton);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, currencies);
        
        autoCompleteFrom.setAdapter(adapter);
        autoCompleteTo.setAdapter(adapter);

        // Set default values
        autoCompleteFrom.setText(currencies[0], false);
        autoCompleteTo.setText(currencies[2], false);

        convertBtn.setOnClickListener(v -> convert());
        swapBtn.setOnClickListener(v -> swapCurrencies());
    }

    private void swapCurrencies() {
        String fromText = autoCompleteFrom.getText().toString();
        String toText = autoCompleteTo.getText().toString();
        
        autoCompleteFrom.setText(toText, false);
        autoCompleteTo.setText(fromText, false);
    }

    private void convert() {
        String input = amountEditText.getText().toString();
        if (input.isEmpty()) {
            amountEditText.setError("Please enter an amount");
            return;
        }

        try {
            double amount = Double.parseDouble(input);
            String fromCurr = autoCompleteFrom.getText().toString();
            String toCurr = autoCompleteTo.getText().toString();

            if (!rates.containsKey(fromCurr) || !rates.containsKey(toCurr)) return;

            double fromRate = rates.get(fromCurr);
            double toRate = rates.get(toCurr);

            double result = (amount / fromRate) * toRate;
            resultTextView.setText(String.format("%.2f %s", result, toCurr));
        } catch (NumberFormatException e) {
            amountEditText.setError("Invalid number");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 100, 0, "Settings").setIcon(android.R.drawable.ic_menu_preferences)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 100) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}