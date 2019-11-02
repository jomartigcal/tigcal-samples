package com.tigcal.samples.billcalc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class BillCalcActivity extends AppCompatActivity {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");
    public static final String COMMA = ",";
    public static final String EMPTY_STRING = "";
    public static final String SPACE_STRING = " ";

    private TextView totalKwhText;
    private TextView totalAmountText;
    private TextView tigcalKwhText;
    private TextView neighborKwhText;

    private int totalKwh = 0;
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private TextView tigcalAmountText;
    private TextView neighborAmountText;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_BillCalc);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        totalKwhText = findViewById(R.id.total_kwh_text);
        totalAmountText = findViewById(R.id.total_amount_text);
        tigcalKwhText = findViewById(R.id.tigcal_kwh_text);
        neighborKwhText = findViewById(R.id.neighbor_kwh_text);
        tigcalAmountText = findViewById(R.id.tigcal_amount_text);
        neighborAmountText = findViewById(R.id.neighbor_amount_text);

        preferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        totalKwhText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                clearKwhAndAmount();
            }
        });

        totalAmountText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!(view instanceof EditText)) {
                    return;
                }

                EditText editText = (EditText) view;
                if (!hasFocus && !EMPTY_STRING.equals(editText.getText().toString())) {
                    String input = editText.getText().toString();
                    BigDecimal decimal = new BigDecimal(input.replaceAll("\\,", EMPTY_STRING));
                    editText.setText(formatDecimal(decimal));
                    clearKwhAndAmount();
                }
            }
        });

        tigcalKwhText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (EMPTY_STRING.equals(s.toString())) {
                    tigcalAmountText.setText(formatDecimal(BigDecimal.ZERO));
                    return;
                }

                totalKwh = getInteger(totalKwhText.getText().toString());

                int difference = totalKwh - getInteger(s.toString());
                if (difference >= 0) {
                    neighborKwhText.setText(String.valueOf(difference));
                } else {
                    neighborKwhText.setText(formatDecimal(BigDecimal.ZERO));
                }

                tigcalAmountText.setText(formatDecimal(computeAmountDue(new BigDecimal(s.toString()))));

            }
        });
        neighborKwhText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!EMPTY_STRING.equals(s.toString())) {
                    neighborAmountText.setText(formatDecimal(computeAmountDue(new BigDecimal(s.toString()))));
                } else {
                    neighborAmountText.setText(formatDecimal(BigDecimal.ZERO));
                }
            }
        });

        displayCachedBill();
    }

    @Override
    protected void onDestroy() {
        preferences.edit()
                .putString(getString(R.string.bc_kwh_total), totalKwhText.getText().toString())
                .putString(getString(R.string.bc_amount_total), totalAmountText.getText().toString())
                .putString(getString(R.string.bc_kwh_tigcal), tigcalKwhText.getText().toString())
                .putString(getString(R.string.bc_kwh_neighbor), neighborKwhText.getText().toString())
                .putString(getString(R.string.bc_amount_tigcal), tigcalAmountText.getText().toString())
                .putString(getString(R.string.bc_amount_neighbor), neighborAmountText.getText().toString())
                .apply();

        super.onDestroy();
    }

    private void clearKwhAndAmount() {
        tigcalKwhText.setText(EMPTY_STRING);
        tigcalAmountText.setText(EMPTY_STRING);
        neighborKwhText.setText(EMPTY_STRING);
        neighborAmountText.setText(EMPTY_STRING);
    }

    private void displayCachedBill() {
        totalKwhText.setText(preferences.getString(getString(R.string.bc_kwh_total), EMPTY_STRING));
        totalAmountText.setText(preferences.getString(getString(R.string.bc_amount_total), EMPTY_STRING));
        tigcalKwhText.setText(preferences.getString(getString(R.string.bc_kwh_tigcal), EMPTY_STRING));
        neighborKwhText.setText(preferences.getString(getString(R.string.bc_kwh_neighbor), EMPTY_STRING));
        tigcalAmountText.setText(preferences.getString(getString(R.string.bc_amount_tigcal), EMPTY_STRING));
        neighborAmountText.setText(preferences.getString(getString(R.string.bc_amount_neighbor), EMPTY_STRING));
    }

    private BigDecimal computeAmountDue(BigDecimal kwh) {
        totalKwh = getInteger(totalKwhText.getText().toString());
        totalAmount = getDecimalValue(totalAmountText.getText().toString());

        if (totalKwh <= 0 || totalAmount.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }

        if (kwh.compareTo(new BigDecimal(totalKwh)) > 0) {
            return totalAmount;
        }

        MathContext mathContext = new MathContext(5, RoundingMode.HALF_UP);
        BigDecimal tigcalAmount = kwh.divide(BigDecimal.valueOf(totalKwh), mathContext);
        return tigcalAmount.multiply(totalAmount);
    }

    private int getInteger(String integerString) {
        if (integerString == null || EMPTY_STRING.equals(integerString)) {
            return 0;
        } else {
            return Integer.parseInt(integerString);
        }
    }

    private String formatDecimal(BigDecimal decimal) {
        return DECIMAL_FORMAT.format(decimal);
    }

    private BigDecimal getDecimalValue(String decimalString) {
        BigDecimal decimalValue = BigDecimal.ZERO;

        if (decimalString != null && !decimalString.equals(EMPTY_STRING)) {
            if (decimalString.contains(COMMA)) {
                decimalString = decimalString.replace(COMMA, EMPTY_STRING);
            }

            if (decimalString.contains(SPACE_STRING)) {
                decimalString = decimalString.replace(SPACE_STRING, EMPTY_STRING);
            }

            try {
                decimalValue = BigDecimal.valueOf(Math.max(0, Double.parseDouble(decimalString)));
            } catch (NumberFormatException exception) {
                //Do nothing, decimal value will still be BigDecimal.ZERO
            }
        }

        return decimalValue;
    }
}
