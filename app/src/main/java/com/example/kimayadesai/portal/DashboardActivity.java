package com.example.kimayadesai.portal;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.kimayadesai.portal.RegistrationLoginDeleteUser.PREFS_NAME;

public class DashboardActivity extends AppCompatActivity {

    TextView redIdUser;
    Button register,dropRegister, dropWaitList,resetButton,signOut;
    ListView registeredCourses, waitListedCourses;
    String redId, password,firstName,lastName,email;
    ArrayAdapter<String> dataAdapter;
    List<String> listForTitle = new ArrayList<String>();
    JSONObject jsonData;
    List<String> listForTitle2 = new ArrayList<String>();
    List<String> listForId = new ArrayList<String>();
    List<String> listForId2 = new ArrayList<String>();
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        register = (Button)findViewById(R.id.registerButton);

        dropRegister = (Button)findViewById(R.id.dropRegister);
        resetButton = (Button)findViewById(R.id.resetButton);
        signOut = (Button)findViewById(R.id.signOut);
        dropWaitList = (Button)findViewById(R.id.dropWaitlist);
        registeredCourses = (ListView)findViewById(R.id.regCourseViewTxt);
        waitListedCourses = (ListView)findViewById(R.id.waitCourseViewTxt);
        redIdUser = (TextView)findViewById(R.id.redId);
        mProgressBar = (ProgressBar)findViewById(R.id.progress);
        mProgressBar.setVisibility(View.VISIBLE);


        SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);
        redId = sp.getString("redID",redId);
        password=sp.getString("password",password);
        firstName = sp.getString("firstname",firstName);
        lastName = sp.getString("lastname",lastName);
        email= sp.getString("email",email);

        redIdUser.setText(redId);

        getAllRegisteredSubjects();
        getAllWaitListedSubjects();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent menuActivity = new Intent(getApplicationContext(), SelectFilteredSubject.class);
                startActivity(menuActivity);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent menuActivity = new Intent(getApplicationContext(), RegistrationLoginDeleteUser.class);
                startActivity(menuActivity);
            }
        });


        dropRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent menuActivity = new Intent(getApplicationContext(), DropClassActivity.class);
                startActivity(menuActivity);
            }
        });

        dropWaitList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent menuActivity = new Intent(getApplicationContext(), DropWaitListActivity.class);
                startActivity(menuActivity);
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final AlertDialog.Builder builder1 = new AlertDialog.Builder(DashboardActivity.this);
                builder1.setMessage(" Do you want to reset all courses?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                                jsonData =  makeJSONObject();
                                resetSubject(jsonData);
                                Intent menuActivity = new Intent(getApplicationContext(),DashboardActivity.class);
                                startActivity(menuActivity);

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

            }
        });
    }

    private JSONObject makeJSONObject() {
        try {
            JSONObject json = new JSONObject();
            json.put("redid", redId);
            json.put("password", password);

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

    private void resetSubject(JSONObject jsonData) {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://bismarck.sdsu.edu/registration/resetstudent?redid="+redId+"&password="+password;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("kimaya", "" + response.toString());
                        System.out.println("Success");
                        finish();
                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        startActivity(intent);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("kimaya", "Log did not work");
            }
        });
        queue.add(stringRequest);

    }

    private void getAllRegisteredSubjects() {

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url ="https://bismarck.sdsu.edu/registration/studentclasses?redid="+redId+"&password="+password;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject ja = new JSONObject(response);
                                String courseid = ja.getString("classes");
                                courseid = courseid.replace("[","");
                                courseid = courseid.replace("]","");
                                courseid = courseid.replace("\"","");
                                String[] ary = courseid.split(",");

                                Log.e( "kimaya",""+ary[0]);


                                for (int k = 0; k < ary.length; k++) {


                                    String url = "https://bismarck.sdsu.edu/registration/classdetails?classid=" + ary[k];

                                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());


                                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response2) {

                                                    Log.e("", "" + response2);

                                                    try {
                                                        JSONObject ja = new JSONObject(response2);
                                                        String title = ja.getString("title");
                                                        String id = ja.getString("id");
                                                        Log.e("", "" + title);
                                                        listForTitle.add(title);
                                                        listForId.add(id);


                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                    addToDisplayView(listForTitle);

                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.e("kimaya", "Log did not work");
                                        }
                                    });


                                    queue.add(stringRequest);

                                }
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e( "kimaya","Log did not work");
                }
            });
            queue.add(stringRequest);

    }

    private void addToDisplayView(List<String> list) {
        for(int k=0; k<list.size(); k++){
            dataAdapter = new ArrayAdapter<>(DashboardActivity.this, android.R.layout.simple_spinner_item,list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
            registeredCourses.setAdapter(dataAdapter);

        }
    }

    private void getAllWaitListedSubjects() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="https://bismarck.sdsu.edu/registration/studentclasses?redid="+redId+"&password="+password;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject ja = new JSONObject(response);
                            String courseid = ja.getString("waitlist");
                            courseid = courseid.replace("[","");
                            courseid = courseid.replace("]","");
                            courseid = courseid.replace("\"","");
                            String[] ary = courseid.split(",");

                            Log.e( "kimaya",""+ary[0]);


                            for (int k = 0; k < ary.length; k++) {


                                String url = "https://bismarck.sdsu.edu/registration/classdetails?classid=" + ary[k];

                                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());


                                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response2) {

                                                Log.e("", "" + response2);

                                                try {
                                                    JSONObject ja = new JSONObject(response2);
                                                    String title = ja.getString("title");
                                                    String id = ja.getString("id");
                                                    Log.e("", "" + title);
                                                    listForTitle2.add(title);
                                                    listForId2.add(id);
                                                    mProgressBar.setVisibility(View.INVISIBLE);


                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                addToDisplayView2(listForTitle2);

                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("kimaya", "Log did not work");
                                    }
                                });


                                queue.add(stringRequest);
                                mProgressBar.setVisibility(View.INVISIBLE);


                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e( "kimaya","Log did not work");
            }
        });
        queue.add(stringRequest);
    }

    private void addToDisplayView2(List<String> list) {

        for(int k=0; k<list.size(); k++){
            dataAdapter = new ArrayAdapter<>(DashboardActivity.this, android.R.layout.simple_spinner_item,list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
            waitListedCourses.setAdapter(dataAdapter);


        }

    }

    @Override
    public void onBackPressed() {

    }
}
