package com.example.android.utabazzar.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.android.utabazzar.BottomNavigation;
import com.example.android.utabazzar.CustomToast;
import com.example.android.utabazzar.R;
import com.example.android.utabazzar.SingletonRequestQueue;
import com.example.android.utabazzar.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity {
    FloatingActionButton fab;
    CardView cvAdd;
    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private EditText editTextUsername, editTextPassword, editTextRepeatPassword, getEditTextUtaID;
    public static String STR_EXTRA_ACTION_REGISTER = "register";


    private static View view;
    private static EditText fullName, userName, emailId, mobileNumber, location,
            password, confirmPassword;
    private static TextView login;
    private static Button signUpButton;
    private static CheckBox terms_conditions;

    String domain = "http://52.90.174.26:8000";
    String loginUrl = domain + "/user/login/";
    String registerUrl = domain + "/user/register/";
    String testUrl = domain + "/user";
    String googleEmailUrl = domain + "/user/email_id/";
    String myPreferences = "MY_PREFERENCES";
    String passWord = "password";
    String phoneNumber = "phone_number";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = RegisterActivity.this.getSharedPreferences(myPreferences, Context.MODE_PRIVATE);
        setContentView(R.layout.activity_register);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        cvAdd = (CardView) findViewById(R.id.cv_add);
        editTextUsername = (EditText) findViewById(R.id.et_username);
        editTextPassword = (EditText) findViewById(R.id.et_password);
        getEditTextUtaID = (EditText) findViewById(R.id.et_utaid);
        fullName = (EditText) findViewById(R.id.namep);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateRevealClose();
            }
        });
    }
    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth()/2,0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd,cvAdd.getWidth()/2,0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.ic_signup);
                RegisterActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }
    @Override
    public void onBackPressed() {
        animateRevealClose();
    }

    public void clickRegister(View view) {
        final String getEmailId = editTextUsername.getText().toString();
        String getFullName = fullName.getText().toString();
        final String getPassword = editTextPassword.getText().toString();
        final String getUserName = getEditTextUtaID.getText().toString();
        Random rand = new Random();
        String phone = String.valueOf(rand.nextInt(1000000000)+1);
        // Get all edit text texts
        final String getMobileNumber = phone;
        String getLocation = "0";
        String getConfirmPassword = "0";

        // Pattern match for email id
        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(getEmailId);

        // Check if all strings are null or not
        if (getFullName.equals("") || getFullName.length() == 0
                || getEmailId.equals("") || getEmailId.length() == 0
                || getMobileNumber.equals("") || getMobileNumber.length() == 0
                || getLocation.equals("") || getLocation.length() == 0
                || getPassword.equals("") || getPassword.length() == 0
                || getConfirmPassword.equals("")
                || getConfirmPassword.length() == 0
                || getUserName.length() == 0)

            new CustomToast().Show_Toast(this, view,
                    "All fields are required.");

            // Check if email id valid or not
        else if (!m.find())
            new CustomToast().Show_Toast(this, view,
                    "Your Email Id is Invalid.");

            // Check if both password should be equal



            // Else do signup or do your stuff
        else {
            Toast.makeText(this, "Do SignUp.", Toast.LENGTH_SHORT)
                    .show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, registerUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_SHORT).show();
                            Log.d("Response", response);
                            if (response.charAt(2) == 'i' || response.charAt(2) == 'S') {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("username", getUserName);
                                editor.putString("password", getPassword);
                                editor.putString("email_id", getEmailId);
                                editor.putString("phone_number", getMobileNumber);
                                editor.commit();
                                Intent intent = new Intent(RegisterActivity.this, BottomNavigation.class);
                                startActivity(intent);
                                RegisterActivity.this.finish();
                                Toast.makeText(RegisterActivity.this, "inside", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(RegisterActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                            Log.d("Error.Response", error.toString());
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username", getUserName);
                    params.put("password", getPassword);
                    params.put("email_id", getEmailId);
                    params.put("phone_number", getMobileNumber);
                    return params;
                }
            };
            SingletonRequestQueue.getInstance(this).addToRequestQueue(stringRequest);

        }
    }

    /**
     * Validate email, pass == re_pass
     * @param emailStr
     * @param password
     * @return
     */
    private boolean validate(String emailStr, String password) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return password.length() > 0 && matcher.find();
    }
}
