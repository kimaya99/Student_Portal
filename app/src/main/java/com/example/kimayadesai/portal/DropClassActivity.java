package com.example.kimayadesai.portal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.List;

import static com.example.kimayadesai.portal.RegistrationLoginDeleteUser.PREFS_NAME;

public class DropClassActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    Button dropCourse;
    String redId, password,firstname,lastname,email,courseId;
    ListView registeredCoursesList;
    ArrayAdapter<String> dataAdapter;
    List<String> listForTitle = new ArrayList<String>();
    List<String> listForId = new ArrayList<String>();
    JSONObject jsonData;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drop_class);

        dropCourse = (Button) findViewById(R.id.droppingCourse);
        registeredCoursesList = (ListView) findViewById(R.id.WaitlistCoursesForDrop);
        back = (ImageButton)findViewById(R.id.imageButton2);

        SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);
        redId = sp.getString("redID",redId);
        password=sp.getString("password",password);
        firstname = sp.getString("firstname",firstname);
        lastname = sp.getString("lastname",lastname);
        email= sp.getString("email",email);


        getAllRegisteredSubjects();
        dropCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(courseId!=null) {
                    int courseidInt = Integer.parseInt(courseId);

                    jsonData = makeJSONObject(redId, password, courseidInt);
                    unRegisterStudent();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Select the course to drop",Toast.LENGTH_LONG).show();
                }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent dashboard = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(dashboard);
            }
        });
    }

    private void unRegisterStudent() {


        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        Response.Listener<JSONObject> success = new Response.Listener<JSONObject> () {
            @Override
            public void onResponse (JSONObject response){
                Log.i("kimi", response.toString());
                Toast.makeText(getApplicationContext(),"Course Dropped",Toast.LENGTH_LONG);


                try {
                    String aJsonString = response.getString("ok");
                    Toast.makeText(getApplicationContext(),"Course Dropped",Toast.LENGTH_LONG).show();

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

                    Toast.makeText(getApplicationContext(),""+aJsonString2,Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(),"Cant delete course",Toast.LENGTH_LONG).show();

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

        String url = "https://bismarck.sdsu.edu/registration/unregisterclass";
        JsonObjectRequest postRequest = new JsonObjectRequest(url, DropClassActivity.this.jsonData, success, failure) ;
        queue.add(postRequest);




    }

    private JSONObject makeJSONObject(String redId, String password, int courseidInt) {
        try {
            JSONObject json = new JSONObject();

            json.put("redid", redId);
            json.put("password",password);
            json.put("courseid",courseidInt);
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
            dataAdapter = new ArrayAdapter<>(DropClassActivity.this, android.R.layout.simple_spinner_item,list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
            registeredCoursesList.setAdapter(dataAdapter);
            registeredCoursesList.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String selectedOpt = (String) registeredCoursesList.getItemAtPosition(position);
        Log.e("position",""+selectedOpt);
        for (int i =0;i< listForTitle.size();i++){
            if (selectedOpt.equals(listForTitle.get(i))){
                courseId = listForId.get(i);
                registeredCoursesList.getChildAt(i).setBackgroundColor(Color.LTGRAY);
            }

        }
        Log.e("position",""+courseId);

    }

    @Override
    public void onBackPressed() {

    }
}