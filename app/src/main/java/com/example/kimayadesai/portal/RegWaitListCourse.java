package com.example.kimayadesai.portal;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.kimayadesai.portal.RegistrationLoginDeleteUser.PREFS_NAME;

public class RegWaitListCourse extends AppCompatActivity {
    int getInt;
    TextView myList;
    JSONObject jsonData;
    Button registerButton;
    ImageButton mImageButton;
    String courseID,enrolled,startTime,waitList,seats,firstname,lastname,email,title,building,days,room,course,section,units;
    int courseIDInt,enrolledInt,waitListInt,seatsInt;
    String redId, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        myList = (TextView) findViewById(R.id.textView);
        registerButton = (Button) findViewById(R.id.registerCourse);
        mImageButton = (ImageButton) findViewById(R.id.imageButton);

        SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);

        redId = sp.getString("redID",redId);
        password=sp.getString("password",password);
        firstname = sp.getString("firstname",firstname);
        lastname = sp.getString("lastname",lastname);
        email= sp.getString("email",email);


        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            getInt = extras.getInt("int_key");
        }
        getDetailOfSubject(getInt);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                courseIDInt = Integer.parseInt(courseID);
                jsonData = makeJSONObject(redId,password);
                registerStudent();
            }

            private void registerStudent() {


                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                    Response.Listener<JSONObject> success = new Response.Listener<JSONObject> () {
                        @Override
                        public void onResponse (JSONObject response){
                            Log.i("kimi", response.toString());
                            Toast.makeText(getApplicationContext(),"Course added",Toast.LENGTH_LONG);


                            try {
                                String aJsonString = response.getString("ok");
                                Toast.makeText(getApplicationContext(),"Registered student",Toast.LENGTH_LONG).show();

                                finish();
                                Intent menuActivity = new Intent(getApplicationContext(), DashboardActivity.class);
                                startActivity(menuActivity);



                            } catch (JSONException e) {
                                String aJsonString2 = null;
                                try {
                                    aJsonString2 = response.getString("error");
                                    final AlertDialog.Builder builder1 = new AlertDialog.Builder(RegWaitListCourse.this);
                                    builder1.setMessage(""+aJsonString2+"!\n\nDo you want to add this course in waitlist?");
                                    builder1.setCancelable(true);

                                    builder1.setPositiveButton(
                                            "Yes",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    seatsInt = Integer.parseInt(seats);
                                                    courseIDInt = Integer.parseInt(courseID);
                                                    enrolledInt = Integer.parseInt(enrolled);
                                                    waitListInt = Integer.parseInt(waitList);
                
                                                    Log.e("", "" + redId + password + courseIDInt);
                                                    jsonData = makeJSONObject2(redId, password, courseIDInt);
                                                    waitList();

                                                }
                                            });

                                    builder1.setNegativeButton(
                                            "No",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });

                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();

                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                                e.printStackTrace();
                            }
                        }

                    };

                    Response.ErrorListener failure = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.i("error", "post fail " + new String(error.networkResponse.data));

                        }
                    };

                    String url = "https://bismarck.sdsu.edu/registration/registerclass";
                    JsonObjectRequest postRequest = new JsonObjectRequest(url, RegWaitListCourse.this.jsonData, success, failure) ;
                    queue.add(postRequest);

                }

            private JSONObject makeJSONObject(String redId, String password) {
                try {
                    JSONObject json = new JSONObject();
                    json.put("redid", redId);
                    json.put("password",password);
                    json.put("courseid",courseIDInt);
                    return json;

                } catch (JSONException e) {
                    JSONObject json = new JSONObject();
                    e.printStackTrace();
                    Log.e("",""+e);

                    return json;
                }
            }

        });

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getApplicationContext(), SelectFilteredSubject.class);
                startActivity(intent);
            }
        });



    }

    private void waitList() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                Response.Listener<JSONObject> success = new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("kimi", response.toString());
                        Toast.makeText(getApplicationContext(), "Waitlisted student", Toast.LENGTH_LONG);


                        try {
                            String aJsonString = response.getString("ok");
                            Toast.makeText(getApplicationContext(), "Waitlisted student", Toast.LENGTH_LONG).show();

                            finish();
                            Intent menuActivity = new Intent(getApplicationContext(), DashboardActivity.class);
                            startActivity(menuActivity);




                        } catch (JSONException e) {
                            String aJsonString2 = null;
                            try {
                                aJsonString2 = response.getString("error");
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                            Toast.makeText(getApplicationContext(), "" + aJsonString2, Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

                    }

                };

                Response.ErrorListener failure = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.i("error", "post fail " + new String(error.networkResponse.data));


                    }
                };

                String url = "https://bismarck.sdsu.edu/registration/waitlistclass";
                JsonObjectRequest postRequest = new JsonObjectRequest(url, RegWaitListCourse.this.jsonData, success, failure);
                queue.add(postRequest);

    }

    private JSONObject makeJSONObject2(String redId, String password, int courseIDInt) {
                try {
                    JSONObject json = new JSONObject();

                    json.put("redid", redId);
                    json.put("password", password);
                    json.put("courseid", courseIDInt);


                    String objectString = json.toString();

                    Log.e("", "" + objectString);

                    return json;

                } catch (JSONException e) {
                    JSONObject json = new JSONObject();
                    e.printStackTrace();
                    Log.e("", "" + e);

                    return json;
                }

            }

    private void getDetailOfSubject(int id) {


        String url = "https://bismarck.sdsu.edu/registration/classdetails?classid="+ id;

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("kimaya", "" + response);

                        try {

                            JSONObject ja = new JSONObject(response);



                            waitList = ja.getString("waitlist");
                            seats = ja.getString("seats");
                            startTime = ja.getString("startTime");
                            enrolled = ja.getString("enrolled");
                            courseID = ja.getString("id");
                            units = ja.getString("units");
                            title = ja.getString("title");
                            days = ja.getString("days");
                            room = ja.getString("room");
                            section = ja.getString("section");
                            building = ja.getString("building");
                            course = ja.getString("course#");

                            if(course!=null)
                                myList.append("\n Course : "+course);
                            if(title!=null)
                                myList.append("\n Course Title : "+title);
                            if(section!=null)
                                myList.append("\n Section : "+section);
                            if(units!=null)
                                myList.append("\n Units : "+units);
                            if(days!=null)
                                myList.append("\n Days : "+days);
                            if(startTime!=null)
                                myList.append("\n StartTime : "+startTime);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("kimaya", "Log did not work");
            }
        });
        queue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {

    }
}
