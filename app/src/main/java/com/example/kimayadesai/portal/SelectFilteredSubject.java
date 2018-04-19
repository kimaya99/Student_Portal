package com.example.kimayadesai.portal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SelectFilteredSubject extends AppCompatActivity implements AdapterView.OnItemClickListener {
    Spinner majorSpinner, timeSpinner, levelSpinner;
    String major, time, level;
    int positionForMajor;
    Button applyFilter;
    ImageButton homeButton;
    String url;
    ListView myList;
    List<String> listForTitle = new ArrayList<String>();
    List<String> listForTitle5 = new ArrayList<String>();
    List<String> listForID5 = new ArrayList<String>();
    List<String> listForID = new ArrayList<String>();

    private ArrayAdapter<String> dataAdapter;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        majorSpinner = (Spinner) findViewById(R.id.majorSpinner);
        timeSpinner = (Spinner) findViewById(R.id.timeSpinner);
        levelSpinner = (Spinner) findViewById(R.id.levelSpinner);
        applyFilter = (Button) findViewById(R.id.applyButton);
        myList = (ListView) findViewById(R.id.filterMyList);
        homeButton = (ImageButton) findViewById(R.id.imageButton4);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);
        getMajorRequest();
        majorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                major = parent.getItemAtPosition(position).toString();
                positionForMajor = position+2;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        setLevelFilter();
        levelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                level = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        setTimeFilter();
        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                time = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        applyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                filterMe();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(intent);
            }
        });
    }

    private void filterMe() {
        myList.setAdapter(null);
        Log.e("",""+major+level+time);
        if (major.equals("Astronomy") && level.equals("All Levels") && time.equals("Anytime")) {
            url ="https://bismarck.sdsu.edu/registration/classidslist?subjectid="+positionForMajor;
            findMajor(url);
        }
        if (major.equals("Astronomy") && !level.equals("All Levels") && time.equals("Anytime")) {
            url ="https://bismarck.sdsu.edu/registration/classidslist?subjectid="+positionForMajor+"&level="+level;
            findMajor(url);
        }
        if (major.equals("Astronomy") && level.equals("All Levels") && !time.equals("Anytime")) {
            url ="https://bismarck.sdsu.edu/registration/classidslist?subjectid="+positionForMajor+"&start-time="+time;
            findMajor(url);
        }
        if (major.equals("Astronomy") && !level.equals("All Levels") && !time.equals("Anytime")) {
            url ="https://bismarck.sdsu.edu/registration/classidslist?subjectid="+positionForMajor+"&start-time="+time+"&level="+level;
            findMajor(url);
        }
        if (!major.equals("Astronomy") && level.equals("All Levels") && time.equals("Anytime")) {
            url ="https://bismarck.sdsu.edu/registration/classidslist?subjectid="+positionForMajor;
            findMajor(url);
        }
        if (!major.equals("Astronomy") && !level.equals("All Levels") && time.equals("Anytime")) {
            url ="https://bismarck.sdsu.edu/registration/classidslist?subjectid="+positionForMajor+"&level="+level;
            findMajor(url);
        }
        if (!major.equals("Astronomy") && level.equals("All Levels") && !time.equals("Anytime")) {
            url ="https://bismarck.sdsu.edu/registration/classidslist?subjectid="+positionForMajor+"&start-time="+time;
            findMajor(url);
        }

        if (!major.equals("Astronomy") && !level.equals("All Levels") && !time.equals("Anytime")) {

            url ="https://bismarck.sdsu.edu/registration/classidslist?subjectid="+positionForMajor+"&level="+level+"&start-time="+time;
            findMajor(url);
        }
    }

    private void findMajor(String url) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("kimaya", "" + response);
                        response = response.replace("[", "");
                        response = response.replace("]", "");
                        response = response.replace("\"", "");

                        String[] ary = response.split(",");

                        for (int k = 0; k < ary.length; k++) {

                            String url = "https://bismarck.sdsu.edu/registration/classdetails?classid="+ary[k];
                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response2) {
                                            try {
                                                JSONObject ja = new JSONObject(response2);
                                                String title = ja.getString("title");
                                                String id = ja.getString("id");
                                                Log.e("", "" + title+id);
                                                listForTitle5.add(title);
                                                listForID5.add(id);

                                                addToDisplayView5(listForTitle5);
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

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e( "kimaya","Log did not work");
            }
        });
        queue.add(stringRequest);
    }

    private void addToDisplayView5(List<String> list) {

        for(int k=0; k<list.size(); k++){
            dataAdapter = new ArrayAdapter<>(SelectFilteredSubject.this, android.R.layout.simple_spinner_item,list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
            myList.setAdapter(dataAdapter);
            myList.setOnItemClickListener(this);
        }

        progressBar.setVisibility(View.INVISIBLE);

    }

    private void setTimeFilter() {
        String time[] = new String[62];
        time[0] = "Anytime";
        time[1] = "700";
        int startTime = 700;
        int endTime = 2200;
        int i = 1;
        while(startTime < endTime) {
            String time_val = "";
            if (startTime % 100 < 45) {
                startTime = (startTime / 100) * 100 + (i % 4) * 15;
            }
            else if(startTime % 100 == 45) {
                startTime = ((startTime / 100) + 1) * 100;
            }
                    time_val += startTime;

            time[i + 1] = time_val;
            i++;
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SelectFilteredSubject.this, android.R.layout.simple_spinner_item, time);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(dataAdapter);
    }

    private void setLevelFilter() {
        String level[] = new String[4];
        level[0] = "All Levels";
        level[1] = "lower";
        level[2] = "upper";
        level[3] = "graduate";

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SelectFilteredSubject.this, android.R.layout.simple_spinner_item, level);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelSpinner.setAdapter(dataAdapter);
    }

    private void getMajorRequest() {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://bismarck.sdsu.edu/registration/subjectlist";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("kimaya", "" + response.toString());
                        System.out.println("Fetching JSON");

                        try {
                            JSONArray ja = new JSONArray(response);
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject JO = (JSONObject) ja.get(i);
                                String title = JO.getString("title");
                                String id = JO.getString("id");
                                listForTitle.add(i, title);
                                listForID.add(i, id);

                                addToDisplayView(listForTitle);
                            }
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

    private void addToDisplayView(List<String> list) {
        for (int k = 0; k < list.size(); k++) {
            dataAdapter = new ArrayAdapter<>(SelectFilteredSubject.this, android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            majorSpinner.setAdapter(dataAdapter);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String selectedOpt = (String) myList.getItemAtPosition(position);
        Log.e("position",""+selectedOpt);

        id = Long.parseLong(listForID5.get(position));
        Integer i = (int) (long) id;

        Intent intent = new Intent(this, RegWaitListCourse.class);
        intent.putExtra("int_key", i);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

    }
}
