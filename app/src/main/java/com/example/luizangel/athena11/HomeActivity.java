package com.example.luizangel.athena11;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarFragment;
import com.roughike.bottombar.OnTabSelectedListener;

import com.squareup.timessquare.CalendarPickerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;
    private HorizontalScrollView horizontalScrollView;
    private HorizontalScrollView horizontalScrollView2;
    private TextView textView;
    private TextView textView4;
    //    private CalendarView calendarView;
    private CalendarPickerView calendarPickerView;
    private ImageView imageView2;

    private final int buttonSize = 30;

    private String nome;
    private String classe;
    private String id;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        nome = getIntent().getExtras().getString("nome");
        classe = getIntent().getExtras().getString("classe");
        id = getIntent().getExtras().getString("id");

        setTitle("Bem vindo, " + nome);

        this.requestAtividades();
        this.requestNotas();
        this.requestCalendario();

        BottomBar bottomBar = BottomBar.attach(this, savedInstanceState);
        bottomBar.setFragmentItems(getSupportFragmentManager(), R.id.fragmentContainer,
                new BottomBarFragment(new SampleFragment(), R.drawable.ic_home,     "Home"),
                new BottomBarFragment(new SampleFragment(), R.drawable.ic_prazos,   "Prazos"),
                new BottomBarFragment(new SampleFragment(), R.drawable.ic_notas,    "Notas"),
                new BottomBarFragment(new SampleFragment(), R.drawable.ic_logout,   "Logout")
        );
        // Setting colors for different tabs when there's more than three of them.
        bottomBar.mapColorForTab(0, "#3B494C");
        bottomBar.mapColorForTab(1, "#00796B");
        bottomBar.mapColorForTab(2, "#7B1FA2");
        bottomBar.mapColorForTab(3, "#FF5252");

        bottomBar.setOnItemSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                switch (position) {
                    case 0:
                        calendarPickerView.setVisibility(View.INVISIBLE);
                        imageView2.setVisibility(View.INVISIBLE);
                        horizontalScrollView2.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.VISIBLE);
                        textView4.setVisibility(View.VISIBLE);
                        horizontalScrollView.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        horizontalScrollView.setVisibility(View.INVISIBLE);
                        horizontalScrollView2.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.INVISIBLE);
                        textView4.setVisibility(View.INVISIBLE);
                        calendarPickerView.setVisibility(View.VISIBLE);
                        imageView2.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        horizontalScrollView.setVisibility(View.INVISIBLE);
                        horizontalScrollView2.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.INVISIBLE);
                        textView4.setVisibility(View.INVISIBLE);
                        calendarPickerView.setVisibility(View.INVISIBLE);
                        imageView2.setVisibility(View.VISIBLE);
                        break;

                    case 3:
                        Intent it = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(it);
                        finish();
                        break;
                }
            }
        });

        bottomBar.useDarkTheme(true);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    /**
     * HTTP Request for Activities Screen
     */
    public void requestAtividades()
    {
        String url = APImanager.getInstance().APIatividades(id);
        RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject response_json = new JSONObject(response);
                            if (response_json.getString("valido").equals("true"))  {
                                HorizontalScrollView scrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
                                HorizontalScrollView scrollView2 = (HorizontalScrollView) findViewById(R.id.horizontalScrollView2);

                                LinearLayout topLinearLayout = new LinearLayout(HomeActivity.this);
                                LinearLayout topLinearLayout2 = new LinearLayout(HomeActivity.this);

                                topLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                                topLinearLayout2.setOrientation(LinearLayout.HORIZONTAL);

                                if (classe.equals("Aluno")) {
                                    JSONArray atividades = response_json.getJSONArray("atividades");
                                    for (int i = 0; i < atividades.length(); i++) {
                                        JSONObject atividade = atividades.getJSONObject(i);
                                        final String textAtividade =
                                                        atividade.getString("nome") + "\n" +
                                                        atividade.getString("turma") + "\n" +
                                                        atividade.getString("professor") + "\n" +
                                                        atividade.getString("prazo");

                                        final Button textViewAtividade = new Button(HomeActivity.this);
                                        textViewAtividade.setText(textAtividade);
                                        textViewAtividade.setTextSize(buttonSize);

                                        if (atividade.getString("entrega").equals("false")) {
                                            if (isExpired(atividade.getString("prazo"))) {
                                                textViewAtividade.setTextColor(Color.parseColor("red"));
                                            } else {
                                                textViewAtividade.setTextColor(Color.parseColor("blue"));
                                            }
                                            topLinearLayout.addView(textViewAtividade);
                                        } else {
                                            topLinearLayout2.addView(textViewAtividade);
                                        }
                                    }
                                } else {
                                    JSONArray turmas = response_json.getJSONArray("turmas");
                                    for (int i = 0; i < turmas.length(); i++) {
                                        JSONObject turma = turmas.getJSONObject(i);

                                        final Button textViewTurma = new Button(HomeActivity.this);
                                        final String textTurma = "Turma:\n" + turma.getString("nome");
                                        textViewTurma.setText(textTurma);
                                        textViewTurma.setTextSize(buttonSize);
                                        topLinearLayout.addView(textViewTurma);

                                        final Button textViewTurma2 = new Button(HomeActivity.this);
                                        textViewTurma2.setText(textTurma);
                                        textViewTurma2.setTextSize(buttonSize);
                                        topLinearLayout2.addView(textViewTurma2);

                                        JSONArray atividades = turma.getJSONArray("atividades");
                                        for (int j = 0; j < atividades.length(); j++) {
                                            JSONObject atividade = atividades.getJSONObject(j);
                                            final String textAtividade =
                                                            atividade.getString("nome") + "\nSubmissoes: " +
                                                            atividade.getString("submissoes") + "\n" +
                                                            atividade.getString("prazo");

                                            final Button textViewAtividade = new Button(HomeActivity.this);
                                            textViewAtividade.setText(textAtividade);
                                            textViewAtividade.setTextSize(buttonSize);

                                            if (isExpired(atividade.getString("prazo"))) {
                                                textViewAtividade.setTextColor(Color.parseColor("red"));
                                                topLinearLayout2.addView(textViewAtividade);
                                            } else {
                                                textViewAtividade.setTextColor(Color.parseColor("blue"));
                                                topLinearLayout.addView(textViewAtividade);
                                            }
                                        }
                                    }
                                }

                                scrollView.addView(topLinearLayout);
                                scrollView2.addView(topLinearLayout2);

                            } else {

                                Toast.makeText(HomeActivity.this, "Lista de Atividades inválida!", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {

                            Toast.makeText(HomeActivity.this, "Erro Json", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(HomeActivity.this, error.toString(), Toast.LENGTH_LONG).show();

                    }
                }) {
//                    @Override
//                    protected Map<String,String> getParams(){
//                        Map<String,String> params = new HashMap<String, String>();
//                        params.put(KEY_USERNAME,username);
//                        params.put(KEY_PASSWORD,password);
////                        params.put(KEY_EMAIL, email);
//                        return params;
//                    }

        };
        requestQueue.add(stringRequest);
    }

    /**
     * HTTP Request for Calendar Screen
     */
    private void requestCalendario() {
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        //coordinatorLayout = (CoordinatorLayout) findViewById(R.id.home_activity);

//        calendarView = (CalendarView) findViewById(R.id.calendarView);
//        calendarView.setVisibility(View.INVISIBLE);

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.MONTH, 3);
        calendarPickerView = (CalendarPickerView) findViewById(R.id.calendarView);
        Date today = new Date();
        calendarPickerView.init(today, nextYear.getTime())
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE);

        calendarPickerView.setVisibility(View.INVISIBLE);

        textView = (TextView) findViewById(R.id.textView);
        textView4 = (TextView) findViewById(R.id.textView4);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        horizontalScrollView2 = (HorizontalScrollView) findViewById(R.id.horizontalScrollView2);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView2.setVisibility(View.INVISIBLE);

        String url = APImanager.getInstance().APIcalendario(id);
        RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject response_json = new JSONObject(response);
                            if (response_json.getString("valido").equals("true"))  {
                                HorizontalScrollView scrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
                                HorizontalScrollView scrollView2 = (HorizontalScrollView) findViewById(R.id.horizontalScrollView2);

                                LinearLayout topLinearLayout = new LinearLayout(HomeActivity.this);
                                LinearLayout topLinearLayout2 = new LinearLayout(HomeActivity.this);

                                topLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                                topLinearLayout2.setOrientation(LinearLayout.HORIZONTAL);

                                if (classe.equals("Aluno")) {
                                    JSONArray atividades = response_json.getJSONArray("atividades");
                                    for (int i = 0; i < atividades.length(); i++) {
                                        JSONObject atividade = atividades.getJSONObject(i);
                                        final String textAtividade =
                                                atividade.getString("nome") + "\n" +
                                                        atividade.getString("turma") + "\n" +
                                                        atividade.getString("professor") + "\n" +
                                                        atividade.getString("prazo");

                                        final Button textViewAtividade = new Button(HomeActivity.this);
                                        textViewAtividade.setText(textAtividade);
                                        textViewAtividade.setTextSize(buttonSize);

                                        if (atividade.getString("entrega").equals("false")) {
                                            if (isExpired(atividade.getString("prazo"))) {
                                                textViewAtividade.setTextColor(Color.parseColor("red"));
                                            } else {
                                                textViewAtividade.setTextColor(Color.parseColor("blue"));
                                            }
                                            topLinearLayout.addView(textViewAtividade);
                                        } else {
                                            topLinearLayout2.addView(textViewAtividade);
                                        }
                                    }
                                }
                                scrollView.addView(topLinearLayout);
                                scrollView2.addView(topLinearLayout2);

                            } else {

                                Toast.makeText(HomeActivity.this, "Lista de Atividades inválida!", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {

                            Toast.makeText(HomeActivity.this, "Erro Json", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(HomeActivity.this, error.toString(), Toast.LENGTH_LONG).show();

                    }
                }) {
//                    @Override
//                    protected Map<String,String> getParams(){
//                        Map<String,String> params = new HashMap<String, String>();
//                        params.put(KEY_USERNAME,username);
//                        params.put(KEY_PASSWORD,password);
////                        params.put(KEY_EMAIL, email);
//                        return params;
//                    }

        };
        requestQueue.add(stringRequest);
    }

    /**
     * HTTP Request for Notas Screen
     */
    public void requestNotas() {

    }

    public boolean isExpired (String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return new Date().after(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Home Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.luizangel.athena11/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Home Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.luizangel.athena11/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
