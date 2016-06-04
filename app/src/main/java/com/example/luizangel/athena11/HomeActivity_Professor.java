package com.example.luizangel.athena11;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Visibility;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.roughike.bottombar.BottomBarBadge;
import com.roughike.bottombar.BottomBarFragment;
import com.roughike.bottombar.OnMenuTabSelectedListener;
import com.roughike.bottombar.OnTabSelectedListener;

import com.squareup.timessquare.CalendarPickerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeActivity_Professor extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;
    private ScrollView verticalScrollViewZ, verticalScrollView, verticalScrollView2, verticalScrollView3;
    private LinearLayout topLinearLayout, topLinearLayout2, topLinearLayout3;
    private TextView textView, textView2;
    private CalendarPickerView calendarPickerView;
    private BottomBar bottomBar;

    private JSONObject notas;

    private final int buttonSize = 30;

    private String nome, classe, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_professor);

        initialize(savedInstanceState);

        /* Perform requests */
        requestNotas();
        requestAtividades();
        requestCalendario();
    }

    void initialize(Bundle savedInstanceState) {

        nome = getIntent().getExtras().getString("nome");
        classe = getIntent().getExtras().getString("class");
        id = getIntent().getExtras().getString("id");

        /**************Calendario**************/
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.MONTH, 3);
        calendarPickerView = (CalendarPickerView) findViewById(R.id.calendarView_professor);
        calendarPickerView.init(new Date(), nextYear.getTime())
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE);
        calendarPickerView.setVisibility(View.INVISIBLE);

        /**************Componentes**************/
        textView = (TextView) findViewById(R.id.textView_professor);
        textView2 = (TextView) findViewById(R.id.textView4_professor);
        topLinearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        topLinearLayout2 = (LinearLayout) findViewById(R.id.linearLayout2);
        topLinearLayout3 = (LinearLayout) findViewById(R.id.linearLayout3);
        verticalScrollViewZ = (ScrollView) findViewById(R.id.vScrollView_professor);
        verticalScrollView = (ScrollView) findViewById(R.id.verticalScrollView_professor);
        verticalScrollView2 = (ScrollView) findViewById(R.id.verticalScrollView2_professor);
        verticalScrollView3 = (ScrollView) findViewById(R.id.verticalScrollView3_professor);

        /**************Barra****************/
        bottomBar = BottomBar.attach(this, savedInstanceState);
        bottomBar.setFragmentItems(getSupportFragmentManager(), R.id.fragmentContainer_professor,
                new BottomBarFragment(new SampleFragment(), R.drawable.ic_home, "Home"),
                new BottomBarFragment(new SampleFragment(), R.drawable.ic_prazos, "Prazos"),
                new BottomBarFragment(new SampleFragment(), R.drawable.ic_notas, "Notas"),
                new BottomBarFragment(new SampleFragment(), R.drawable.ic_logout, "Logout")
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
                        verticalScrollViewZ.setVisibility(View.VISIBLE);
                        calendarPickerView.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("Turmas");
                        textView2.setVisibility(View.VISIBLE);
                        verticalScrollView.setVisibility(View.VISIBLE);
                        verticalScrollView2.setVisibility(View.VISIBLE);
                        verticalScrollView3.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        setTitle("Calendario");
                        verticalScrollViewZ.setVisibility(View.INVISIBLE);
                        verticalScrollView.setVisibility(View.INVISIBLE);
                        verticalScrollView2.setVisibility(View.INVISIBLE);
                        verticalScrollView3.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.INVISIBLE);
                        textView2.setVisibility(View.INVISIBLE);
                        calendarPickerView.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        setTitle("Quadro de Notas");
                        verticalScrollViewZ.setVisibility(View.VISIBLE);
                        verticalScrollView.setVisibility(View.INVISIBLE);
                        verticalScrollView2.setVisibility(View.INVISIBLE);
                        verticalScrollView3.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("Notas");
                        textView2.setVisibility(View.INVISIBLE);
                        calendarPickerView.setVisibility(View.INVISIBLE);
                        break;

                    case 3:
                        Intent it = new Intent(HomeActivity_Professor.this, LoginActivity.class);
                        startActivity(it);
                        finish();
                        break;
                }
            }
        });
        bottomBar.useDarkTheme(true);
    }

    /**
     * HTTP Request for Atividades Screen
     */
    private void requestAtividades() {

        String url = APImanager.getInstance().APIatividades(id);
        RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity_Professor.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject response_json = new JSONObject(response);

                    if (response_json.getString("valido").equals("true")) {

                        JSONArray turmas = response_json.getJSONArray("turmas");

                        for(int i = 0; i < turmas.length(); i++) {
                            final JSONObject turma = turmas.getJSONObject(i);

                            final JSONArray atividades = turma.getJSONArray("atividades");

                            final Button buttonTurmas = new Button(HomeActivity_Professor.this);
                            buttonTurmas.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        topLinearLayout2.removeAllViews();

                                        for(int i = 0; i < atividades.length(); i++) {
                                            final JSONObject atividade = atividades.getJSONObject(i);

                                            Button buttonAtividade = new Button(HomeActivity_Professor.this);
                                            buttonAtividade.setClickable(true);
                                            buttonAtividade.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    bottomBar.selectTabAtPosition(2, true);
                                                    verticalScrollViewZ.scrollTo(0, 0);
                                                    try {
                                                        topLinearLayout3.removeAllViews();

                                                        JSONObject turma_selecionada = notas.getJSONObject(turma.getString("nome"));
                                                        JSONArray atividade_selecionada = turma_selecionada.getJSONArray(atividade.getString("nome"));
                                                        setTitle("Turma: " + turma.getString("nome"));

                                                        for (int j = 0; j < atividade_selecionada.length(); j++) {
                                                            JSONObject nota_aluno = atividade_selecionada.getJSONObject(j);

                                                            Button buttonNota = new Button(HomeActivity_Professor.this);
                                                            String text_nota =  "Aluno: "   + nota_aluno.getString("aluno") + "\n" +
                                                                                "Nota: "    + nota_aluno.getString("nota")   + "\n" +
                                                                                "Resultado: " + nota_aluno.getString("resultado");

                                                            buttonNota.setText(text_nota);
                                                            buttonNota.setTextSize(buttonSize);

                                                            topLinearLayout3.addView(buttonNota);
                                                        }
                                                    }
                                                    catch (JSONException e) {
                                                        Toast.makeText(HomeActivity_Professor.this, "Erro no JSON", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                            final String textAtividade =
                                                            atividade.getString("nome") + "\nSubmissoes: " +
                                                            atividade.getString("submissoes") + "\n" +
                                                            atividade.getString("prazo");
                                            buttonAtividade.setText(textAtividade);
                                            buttonAtividade.setTextSize(buttonSize);

                                            if (isExpired(atividade.getString("prazo"))) {
                                                buttonAtividade.setTextColor(Color.parseColor("red"));
                                            } else {
                                                buttonAtividade.setTextColor(Color.parseColor("blue"));
                                            }
                                            topLinearLayout2.addView(buttonAtividade);
                                        }
                                    }
                                    catch (Exception e) {
                                        Toast.makeText(HomeActivity_Professor.this, "Erro na lista de atividades", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            String textTurma = turma.getString("nome") + "\n" +
                                    atividades.length() + " Atividades";
                            buttonTurmas.setText(textTurma);
                            buttonTurmas.setTextSize(buttonSize);

                            topLinearLayout.addView(buttonTurmas);
                        }
                    }
                    else {
                        Toast.makeText(HomeActivity_Professor.this, "Lista de Atividades inválida!",Toast.LENGTH_LONG).show();
                    }
                }
                catch (JSONException e) {

                    Toast.makeText(HomeActivity_Professor.this,"Erro Json",Toast.LENGTH_LONG).show();
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(HomeActivity_Professor.this,error.toString(),Toast.LENGTH_LONG).show();

            }
        });
        requestQueue.add(stringRequest);
    }

    /**
     * HTTP Request for Notas Screen
     */
    private void requestNotas() {

        String urlNotas = APImanager.getInstance().APInotas(id);
        RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity_Professor.this);
        StringRequest stringRequestNotas = new StringRequest(Request.Method.GET, urlNotas,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            notas = new JSONObject(response);
                        }

                        catch (JSONException e) {
                            Toast.makeText(HomeActivity_Professor.this, "Erro Json Notas",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomeActivity_Professor.this,error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }){
        };
        requestQueue.add(stringRequestNotas);
    }

    /**
     * HTTP Request for Calendar Screen
     */
    private void requestCalendario() {


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
