package com.example.luizangel.athena11;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

public class HomeActivity_Aluno extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;
    private ScrollView verticalScrollView, scrollView3;
    private HorizontalScrollView horizontalScrollView, horizontalScrollView2;
    private LinearLayout topLinearLayout, topLinearLayout2, topLinearLayout3;
    private TextView textView, textView4;
    private CalendarPickerView calendarPickerView;
    private BottomBar bottomBar;

    private JSONArray notas;

    private final int buttonSize = 30;

    private String nome;
    private String classe;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initialize(savedInstanceState);

        /* Perform requests */
        requestAtividades();
        requestNotas();
        requestCalendario();
    }

    public void initialize(Bundle savedInstanceState) {

        nome = getIntent().getExtras().getString("nome");
        classe = getIntent().getExtras().getString("class");
        id = getIntent().getExtras().getString("id");

        /**************Calendario**************/
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.MONTH, 3);
        calendarPickerView = (CalendarPickerView) findViewById(R.id.calendarView);
        calendarPickerView.init(new Date(), nextYear.getTime())
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE);

        calendarPickerView.setVisibility(View.INVISIBLE);

        /**************Componentes**************/
        textView = (TextView) findViewById(R.id.textView);
        textView4 = (TextView) findViewById(R.id.textView4);
        scrollView3 = (ScrollView) findViewById(R.id.scrollView3);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        horizontalScrollView2 = (HorizontalScrollView) findViewById(R.id.horizontalScrollView2);
        topLinearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        topLinearLayout2 = (LinearLayout) findViewById(R.id.linearLayout2);
        topLinearLayout3 = (LinearLayout) findViewById(R.id.linearLayout3);

        /**************Barra****************/
        bottomBar = BottomBar.attach(this, savedInstanceState);
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
                        setTitle("Bem vindo, " + nome);
                        calendarPickerView.setVisibility(View.INVISIBLE);
                        horizontalScrollView2.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.VISIBLE);
                        textView4.setVisibility(View.VISIBLE);
                        horizontalScrollView.setVisibility(View.VISIBLE);
                        scrollView3.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        setTitle("Calendario");
                        horizontalScrollView.setVisibility(View.INVISIBLE);
                        horizontalScrollView2.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.INVISIBLE);
                        textView4.setVisibility(View.INVISIBLE);
                        calendarPickerView.setVisibility(View.VISIBLE);
                        scrollView3.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        setTitle("Quadro de notas");
                        horizontalScrollView.setVisibility(View.INVISIBLE);
                        horizontalScrollView2.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.INVISIBLE);
                        textView4.setVisibility(View.INVISIBLE);
                        calendarPickerView.setVisibility(View.INVISIBLE);
                        scrollView3.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        Intent it = new Intent(HomeActivity_Aluno.this, LoginActivity.class);
                        startActivity(it);
                        finish();
                        break;
                }
            }
        });
        bottomBar.useDarkTheme(true);
    }

    /**
     * HTTP Request for Activities Screen
     */
    public void requestAtividades()
    {
        String url = APImanager.getInstance().APIatividades(id);
        RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity_Aluno.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject response_json = new JSONObject(response);
                    if (response_json.getString("valido").equals("true"))  {

                        JSONArray atividades = response_json.getJSONArray("atividades");
                        for (int i = 0; i < atividades.length(); i++) {
                            final JSONObject atividade = atividades.getJSONObject(i);
                            final String textAtividade =
                                            atividade.getString("turma") + "\n" +
                                            atividade.getString("nome") + "\nProf: " +
                                            atividade.getString("professor") + "\n" +
                                            atividade.getString("prazo");

                            final Button buttonAtividade = new Button(HomeActivity_Aluno.this);
                            buttonAtividade.setText(textAtividade);
                            buttonAtividade.setTextSize(buttonSize);

                            buttonAtividade.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    bottomBar.selectTabAtPosition(2, true);
                                    scrollView3.scrollTo(0, 0);
                                    try {
                                        topLinearLayout3.removeAllViews();
                                        String turma = atividade.getString("turma");
                                        setTitle("Turma: " + turma);

                                        for (int j = 0; j < notas.length(); j++) {
                                            final JSONObject nota = notas.getJSONObject(j);
                                            if (nota.getString("turma").equals(turma)) {

                                                Button buttonNota = new Button(HomeActivity_Aluno.this);
                                                String textNota =  "Atividade: "+ nota.getString("atividade") + "\n" +
                                                                    "Nota: "    + nota.getString("nota") + "\n" +
                                                                    "Prazo: "   + nota.getString("prazo") + "\n" +
                                                                    "Envio: "   + nota.getString("data_envio");

                                                buttonNota.setText(textNota);
                                                buttonNota.setTextSize(buttonSize);

                                                topLinearLayout3.addView(buttonNota);
                                            }
                                        }
                                    }
                                    catch (JSONException e) {
                                        Toast.makeText(HomeActivity_Aluno.this, "Erro no JSON", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            if (atividade.getString("entrega").equals("false")) {
                                if (isExpired(atividade.getString("prazo")))
                                    buttonAtividade.setTextColor(Color.parseColor("red"));
                                else buttonAtividade.setTextColor(Color.parseColor("blue"));
                                topLinearLayout.addView(buttonAtividade);
                            } else {
                                topLinearLayout2.addView(buttonAtividade);
                            }
                        }
                    } else {

                        Toast.makeText(HomeActivity_Aluno.this, "Lista de Atividades inválida!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {

                    Toast.makeText(HomeActivity_Aluno.this, "Erro Json", Toast.LENGTH_LONG).show();
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(HomeActivity_Aluno.this, error.toString(), Toast.LENGTH_LONG).show();

            }
        });
        requestQueue.add(stringRequest);
    }

    /**
     * HTTP Request for Calendar Screen
     */
    private void requestCalendario() {

        String url = APImanager.getInstance().APIcalendario(id);
        RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity_Aluno.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject response_json = new JSONObject(response);
                    if (response_json.getString("valido").equals("true"))  {


                    } else {

                        Toast.makeText(HomeActivity_Aluno.this, "Calendario inválido!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {

                    Toast.makeText(HomeActivity_Aluno.this, "Erro Json", Toast.LENGTH_LONG).show();
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity_Aluno.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    /**
     * HTTP Request for Notas Screen
     */
    public void requestNotas() {
        String urlNotas = APImanager.getInstance().APInotas(id);
        RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity_Aluno.this);
        StringRequest stringRequestNotas = new StringRequest(Request.Method.GET, urlNotas,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject resposta = new JSONObject(response);
                            if (resposta.getString("valido").equals("true")) {
                                notas = resposta.getJSONArray("notas");
                            }
                        }
                        catch (JSONException e) {
                            Toast.makeText(HomeActivity_Aluno.this, "Erro Json Notas",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomeActivity_Aluno.this,error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }){
        };
        requestQueue.add(stringRequestNotas);
    }

    public boolean isExpired (String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return (new Date().after(sdf.parse(date)));
        } catch (ParseException e) {
            System.out.println("error");
        }
        return true;
    }

}
