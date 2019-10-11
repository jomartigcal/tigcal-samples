package com.tigcal.emailvalidator;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tigcal.emailvalidator.util.NetworkUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final String AT = "@";
    public static final String EMPTY_STRING = "";

    private String[] commonEmailDomains;
    private boolean isBackPress;

    private AutoCompleteTextView emailText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        commonEmailDomains = getResources().getStringArray(R.array.domains);

        progressBar = findViewById(R.id.progress_bar);

        emailText = findViewById(R.id.email_text);
        emailText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    validateEmail(emailText.getText().toString());
                }
                return false;
            }
        });
        emailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isBackPress = count > 0 && after == 0;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String emailInput = editable.toString();
                String domainMatch = EMPTY_STRING;
                String emailDomain = EMPTY_STRING;
                String emailLocalPart;
                List<String> emailSuggestions = new ArrayList<>();
                List<String> allEmailSuggestions = new ArrayList<>();

                if (!doesEmailHaveAt(emailInput)) {
                    for (String domain : commonEmailDomains) {
                        allEmailSuggestions.add(emailInput + AT + domain);
                    }
                } else {
                    allEmailSuggestions.clear();
                }

                int atIndex = emailInput.indexOf(AT);

                if (atIndex >= 1 && atIndex < (emailInput.length() + 1)) {
                    emailLocalPart = emailInput.substring(0, atIndex + 1);
                    emailDomain = emailInput.substring(atIndex + 1, emailInput.length());

                    for (String domain : commonEmailDomains) {
                        if (!TextUtils.isEmpty(emailDomain) && domain.startsWith(emailDomain)) {
                            domainMatch = domain;
                            emailSuggestions.add(emailLocalPart + domain);
                        }
                        allEmailSuggestions.add(emailLocalPart + domain);
                    }
                }

                emailText.setAdapter(new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        emailSuggestions.isEmpty() ? allEmailSuggestions : emailSuggestions));

                if (!isBackPress && !domainMatch.isEmpty()) {
                    emailText.removeTextChangedListener(this);

                    String filled = domainMatch.substring(emailDomain.length(), domainMatch.length());
                    String text = emailInput + filled;
                    int highlight = text.lastIndexOf(filled);
                    emailText.setText(text);
                    emailText.setSelection(highlight, text.length());

                    emailText.addTextChangedListener(this);
                }
            }
        });
    }

    private void validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            displayEmailErrorMessage(getString(R.string.email_address_no_input));
        } else if (!doesEmailHaveAt(email)) {
            displayEmailErrorMessage(getString(R.string.email_address_no_at));
        } else if (doesEmailHaveManyAts(email)) {
            displayEmailErrorMessage(getString(R.string.email_address_many_ats));
        } else if (!isEmailValid(email)) {
            displayEmailErrorMessage(getString(R.string.email_address_invalid));
        } else {
            validateEmailDeliverability(email);
        }
    }

    private boolean doesEmailHaveAt(String email) {
        return email.contains(AT);
    }

    private void displayEmailErrorMessage(String errorMessage) {
        emailText.setError(errorMessage);
    }

    private boolean doesEmailHaveManyAts(String email) {
        int atIndex = email.indexOf(AT);
        return email.indexOf(AT, atIndex + 1) >= 0;
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void displayValidEmailMessage() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.email_address_valid))
                .setPositiveButton(getString(R.string.ok), null)
                .show();
    }

    private void validateEmailDeliverability(String email) {
        progressBar.setVisibility(View.VISIBLE);

        Uri.Builder uriBuilder = Uri.parse(Kickbox.BASE_URL).buildUpon();
        uriBuilder.appendQueryParameter(Kickbox.PARAM_EMAIL, email);
        uriBuilder.appendQueryParameter(Kickbox.PARAM_API_KEY, Kickbox.API_KEY);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, uriBuilder.toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.INVISIBLE);
                if (response == null || response.optString(Kickbox.OUTPUT_RESULT) == null) {
                    displayEmailErrorMessage(getString(R.string.email_address_invalid));
                } else if (Kickbox.RESPONSE_DELIVERABLE.equals(response.optString(Kickbox.OUTPUT_RESULT))) {
                    displayValidEmailMessage();
                } else {
                    displayEmailDeliverabilityErrorMessage(response.optString(Kickbox.OUTPUT_REASON,
                            getString(R.string.email_address_invalid)));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                displayEmailErrorMessage(getString(R.string.email_address_invalid));
                Log.e(TAG, "Error encountered: " + error.getMessage());
            }
        });

        NetworkUtil.addToRequestQueue(this, request);
    }

    private void displayEmailDeliverabilityErrorMessage(String reason) {
        if (Kickbox.REASON_INVALID_EMAIL.equals(reason)) {
            displayEmailErrorMessage(getString(R.string.email_address_invalid_email));
        } else if (Kickbox.REASON_INVALID_DOMAIN.equals(reason)) {
            displayEmailErrorMessage(getString(R.string.email_address_invalid_domain));
        } else if (Kickbox.REASON_REJECTED_EMAIL.equals(reason)) {
            displayEmailErrorMessage(getString(R.string.email_addressrejected_email));
        } else if (Kickbox.REASON_LOW_QUALITY.equals(reason)) {
            displayEmailErrorMessage(getString(R.string.email_address_low_quality));
        } else if (Kickbox.REASON_LOW_DELIVERABILITY.equals(reason)) {
            displayEmailErrorMessage(getString(R.string.email_address_low_deliverability));
        } else if (Kickbox.REASON_NO_CONNECT.equals(reason)) {
            displayEmailErrorMessage(getString(R.string.email_address_no_connect));
        } else if (Kickbox.REASON_TIMEOUT.equals(reason)) {
            displayEmailErrorMessage(getString(R.string.email_address_timeout));
        } else if (Kickbox.REASON_INVALID_SMTP.equals(reason)) {
            displayEmailErrorMessage(getString(R.string.email_address_invalid_smtp));
        } else if (Kickbox.REASON_UNAVAILABLE_SMTP.equals(reason)) {
            displayEmailErrorMessage(getString(R.string.email_address_unavailable_smtp));
        } else if (Kickbox.REASON_UNEXPECTED_ERROR.equals(reason)) {
            displayEmailErrorMessage(getString(R.string.email_address_unexpected_error));
        }
    }

}
