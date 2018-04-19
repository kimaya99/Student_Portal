package com.example.kimayadesai.portal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class RegistrationLoginDeleteUser extends AppCompatActivity {

    EditText firstName,lastName,redID,email,password;
    Button signUp,logIn;
    int fNameNEmp, lNameNEmp,redIDNEmp,passwordNEmp,emailNEmp,redIDValid,emailValid,passwordValid, noDuplicates;
    JSONObject jsonData;
    String firstNameStr,lastNameStr,redIDStr,emailStr,passwordStr;
    public static final String PREFS_NAME = "MyPrefsFile";
    private String redIDStore;
    private String passwordStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_login_delete);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        firstName = (EditText) findViewById(R.id.firstNameTxt);
        lastName = (EditText)findViewById(R.id.lastNameTxt);
        redID = (EditText)findViewById(R.id.redIDTxt);
        email = (EditText)findViewById(R.id.emailTxt);
        password = (EditText)findViewById(R.id.passwordTxt);
        signUp = (Button) findViewById(R.id.SignUpButton);
        logIn = (Button) findViewById(R.id.loginButton);



        firstNameStr = firstName.getText().toString();
        lastNameStr = lastName.getText().toString();
        redIDStr = redID.getText().toString();
        emailStr = email.getText().toString();
        passwordStr = password.getText().toString();


        SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);
        firstName.setText(sp.getString("firstName", firstNameStr));
        lastName.setText(sp.getString("lastName",lastNameStr));
       redID.setText(sp.getString("redID",redIDStr));
        email.setText(sp.getString("email",emailStr));
       password.setText(sp.getString("password",passwordStr));


        redIDStore = sp.getString("redID",redIDStr);
        passwordStore = sp.getString("password",passwordStr);

                email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (checkValidity() == 1){
                       jsonData =  makeJSONObject();
                       postData(jsonData);
               }
               else{

                   Log.e("error in SignUp","Validation required");
               }
            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (redIDStore.equals(redID.getText().toString())&& passwordStore.equals(password.getText().toString())) {

                    finish();
                    Intent menuActivity = new Intent(getApplicationContext(), DashboardActivity.class);
                    startActivity(menuActivity);
                }
                else{

                    Toast.makeText(getApplicationContext(),"Incorrect Credentials",Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private JSONObject makeJSONObject() {
        try {
            JSONObject json = new JSONObject();

            json.put("firstname", firstName.getText().toString() );
            json.put("lastname", lastName.getText().toString());
            json.put("redid", redID.getText().toString());
            json.put("password", password.getText().toString());
            json.put("email", email.getText().toString());

            String objectString = json.toString();

            Log.e("",""+ objectString);

            return json;

        } catch (JSONException e) {
            JSONObject json = new JSONObject();
            e.printStackTrace();
            Log.e("",""+e);

            return json;
        }
    }

    private int checkValidity() {

      Drawable valid = getResources().getDrawable(R.drawable.valid);
      valid.setBounds(0, 0, valid.getIntrinsicWidth(), valid.getIntrinsicHeight());

        Drawable invalid = getResources().getDrawable(R.drawable.invalid);
        invalid.setBounds(0, 0, invalid.getIntrinsicWidth(), invalid.getIntrinsicHeight());

        if (firstName.getText().toString().isEmpty()){

            fNameNEmp = 0;
            firstName.setError("Empty First Name Field",invalid);
            firstName.setCompoundDrawables(null,null,invalid,null);
            firstName.requestFocus();
        }
        else{

            firstName.setCompoundDrawables(null,null,valid,null);
            fNameNEmp = 1; //

            if (lastName.getText().toString().isEmpty()){

                lNameNEmp = 0;
                lastName.requestFocus();
                lastName.setCompoundDrawables(null,null,invalid,null);
                lastName.setError("Empty Last Name Field",invalid);
            }
            else{
                lastName.setCompoundDrawables(null,null,valid,null);
                lNameNEmp = 1; //

                if (redID.getText().toString().isEmpty()){

                    redIDNEmp = 0;
                    redID.requestFocus();
                    redID.setCompoundDrawables(null,null,invalid,null);
                    redID.setError("Empty RedID Field",invalid);

                }
                else{

                    redIDNEmp = 1;
                    String abc = redID.getText().toString().substring(redID.getText().toString().length()-3,redID.getText().toString().length());

                    if (redID.getText().toString().length() == 9 && redID.getText().toString().substring(redID.getText().toString().length()-3,redID.getText().toString().length()).equalsIgnoreCase("799")){

                        redIDValid = 1; //
                        redID.setCompoundDrawables(null,null,valid,null);

                        if (password.getText().toString().isEmpty()){

                            passwordNEmp = 0;
                            password.requestFocus();
                            password.setCompoundDrawables(null,null,invalid,null);
                            password.setError("Empty Password Field",invalid);
                        }

                        else {

                            passwordNEmp = 1;

                            if (password.getText().toString().length() < 8){

                                passwordValid = 0;
                                password.requestFocus();
                                password.setCompoundDrawables(null,null,invalid,null);
                                password.setError("Password too short",invalid);
                            }

                            else {

                                passwordValid = 1; //
                                password.setCompoundDrawables(null,null,valid,null);

                                if (email.getText().toString().isEmpty()){

                                    emailNEmp = 0;
                                    email.requestFocus();
                                    email.setCompoundDrawables(null,null,invalid,null);
                                    email.setError("Empty Email Field",invalid);
                                }
                                else{

                                    emailNEmp = 1;

                                    if (!TextUtils.isEmpty(email.getText().toString()) && Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
                                        emailValid = 1; //
                                        email.setCompoundDrawables(null,null,valid,null);

                                    }

                                    else{
                                        emailValid = 0;
                                        email.requestFocus();
                                        email.setCompoundDrawables(null,null,invalid,null);
                                        email.setError("Invalid Email Field",invalid);
                                    }


                                }
                            }

                        }


                    }

                    else {
                        redIDValid = 0;
                        redID.requestFocus();
                        redID.setCompoundDrawables(null,null,invalid,null);
                        redID.setError("Invalid RedID Field",invalid);


                    }


                }

            }
        }

        if (emailNEmp ==  1 && passwordNEmp == 1 && redIDNEmp == 1 && lNameNEmp == 1 && fNameNEmp == 1 && emailValid == 1 && redIDValid ==1 && passwordValid == 1){
            return 1;
        }
        else{
            return 0;
        }
    }

    private int postData(JSONObject jsonData) {

        RequestQueue queue = Volley.newRequestQueue(this);

        Response.Listener<JSONObject> success = new Response.Listener<JSONObject> () {
            @Override
            public void onResponse (JSONObject response){
                Log.i("kimi", response.toString());
                Drawable valid = getResources().getDrawable(R.drawable.valid);
                valid.setBounds(0, 0, valid.getIntrinsicWidth(), valid.getIntrinsicHeight());

                Drawable invalid = getResources().getDrawable(R.drawable.invalid);
                invalid.setBounds(0, 0, invalid.getIntrinsicWidth(), invalid.getIntrinsicHeight());


                try {

                    redID.setCompoundDrawables(null,null,valid,null);
                    password.setCompoundDrawables(null,null,valid,null);
                    String aJsonString = response.getString("ok");
                    noDuplicates = 1;
                    saveData();

                    Toast.makeText(getApplicationContext(),"Student added",Toast.LENGTH_LONG).show();
                    finish();
                    Intent menuActivity = new Intent(getApplicationContext(), DashboardActivity.class);
                    startActivity(menuActivity);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {


                    String aJsonString2 = response.getString("error");
                    noDuplicates = 0;

                    Toast.makeText(getApplicationContext(),""+aJsonString2,Toast.LENGTH_LONG).show();
                    if (aJsonString2.equals("Red Id already in use")){
                        redID.requestFocus();
                        redID.setCompoundDrawables(null,null,invalid,null);
                        redID.setError("RedID Already In Use",invalid);

                    }
                    else if (aJsonString2.equals("Invalid Red Id")){
                        redID.requestFocus();
                        redID.setCompoundDrawables(null,null,invalid,null);
                        redID.setError("Invalid Red Id",invalid);

                    }




                   // Toast.makeText(getApplicationContext(),"Cant add student",Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            private void saveData() {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("firstName", firstName.getText().toString());
                editor.putString("lastName", lastName.getText().toString());
                editor.putString("redID", redID.getText().toString());
                editor.putString("email", email.getText().toString());
                editor.putString("password", password.getText().toString());

                editor.commit();
            }
        };

        Response.ErrorListener failure = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("error", "post fail " + new String(error.networkResponse.data));




            }
        };

        String url = "https://bismarck.sdsu.edu/registration/addstudent";
        JsonObjectRequest postRequest = new JsonObjectRequest(url, jsonData, success, failure) ;
        queue.add(postRequest);


        return noDuplicates;

    }

    private void hideKeyboard(View v) {
        InputMethodManager manager;
        manager =(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(email.getWindowToken(), 0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom()) ) {
                hideKeyboard(view);
            }
        }
        return ret;
    }

    @Override
    public void onBackPressed() {

    }
}
