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

import java.util.Calendar;
import java.util.Date;

public class HomeActivity_Professor extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;
    private ScrollView verticalScrollView1;
    private ScrollView verticalScrollView;
    private ScrollView verticalScrollView2;
    private ScrollView verticalScrollView3;
    private TextView textView;
    private TextView textView2;
    private CalendarPickerView calendarPickerView;

    private JSONObject notas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_professor);

        String nome = getIntent().getExtras().getString("nome");
        setTitle("Bem vindo, " + nome + "!");


        /*************Calendario*************/

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.MONTH, 3);
        calendarPickerView = (CalendarPickerView) findViewById(R.id.calendarView_professor);
        Date today = new Date();
        calendarPickerView.init(today, nextYear.getTime())
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE);

        calendarPickerView.setVisibility(View.INVISIBLE);

        textView = (TextView) findViewById(R.id.textView_professor);
        textView2 = (TextView) findViewById(R.id.textView4_professor);
        verticalScrollView1 = (ScrollView) findViewById(R.id.vScrollView_professor);
        verticalScrollView = (ScrollView) findViewById(R.id.verticalScrollView_professor);
        verticalScrollView2 = (ScrollView) findViewById(R.id.verticalScrollView2_professor);
        verticalScrollView3 = (ScrollView) findViewById(R.id.verticalScrollView3_professor);

        /**************Barra****************/

        final BottomBar bottomBar = BottomBar.attach(this, savedInstanceState);
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
                        verticalScrollView1.setVisibility(View.VISIBLE);
                        calendarPickerView.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("Turmas");
                        textView2.setVisibility(View.VISIBLE);
                        verticalScrollView.setVisibility(View.VISIBLE);
                        verticalScrollView2.setVisibility(View.VISIBLE);
                        verticalScrollView3.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        verticalScrollView1.setVisibility(View.INVISIBLE);
                        verticalScrollView.setVisibility(View.INVISIBLE);
                        verticalScrollView2.setVisibility(View.INVISIBLE);
                        verticalScrollView3.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.INVISIBLE);
                        textView2.setVisibility(View.INVISIBLE);
                        calendarPickerView.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        verticalScrollView1.setVisibility(View.VISIBLE);
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


        // Make a Badge for the first tab, with red background color and a value of "4".
//        BottomBarBadge unreadMessages = bottomBar.makeBadgeForTabAt(1, "#E91E63", 4);

        // Control the badge's visibility
//        unreadMessages.show();
        //unreadMessages.hide();

        // Change the displayed count for this badge.
        //unreadMessages.setCount(4);

        // Change the show / hide animation duration.
//        unreadMessages.setAnimationDuration(200);

        bottomBar.useDarkTheme(true);

        /***************************************************Atividades*************************************************/

        String id = getIntent().getExtras().getString("id");
        String url = "http://192.168.0.24:8000/Matividades/?id=" + id;
        RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity_Professor.this);
        RequestQueue requestQueue1 = Volley.newRequestQueue(HomeActivity_Professor.this);

        /**************************Request Notas***************************/

        String urlNotas = "http://192.168.0.24:8000/Mnotas/?id=" + id;
        Log.e("testeNotas", id);

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

        ////////////////////////////////////////////////////

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject response_json = new JSONObject(response);

                            if (response_json.getString("valido").equals("true")) {

                                JSONArray turmas = response_json.getJSONArray("turmas");

                                ScrollView scrollView = (ScrollView) findViewById(R.id.verticalScrollView_professor);
                                final ScrollView scrollView2 = (ScrollView) findViewById(R.id.verticalScrollView2_professor);
                                final ScrollView scrollView3 = (ScrollView) findViewById(R.id.verticalScrollView3_professor);

                                LinearLayout topLinearLayout = new LinearLayout(HomeActivity_Professor.this);
                                final LinearLayout topLinearLayout2 = new LinearLayout(HomeActivity_Professor.this);
                                final LinearLayout topLinearLayout3 = new LinearLayout(HomeActivity_Professor.this);

                                topLinearLayout.setOrientation(LinearLayout.VERTICAL);
                                topLinearLayout2.setOrientation(LinearLayout.VERTICAL);
                                topLinearLayout3.setOrientation(LinearLayout.VERTICAL);

                                for(int i = 0; i < turmas.length(); i++) {
                                    final JSONObject turma = turmas.getJSONObject(i);

                                    final JSONArray atividades = turma.getJSONArray("atividades");

                                    final Button buttonTurmas = new Button(HomeActivity_Professor.this);
                                    buttonTurmas.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {
                                                topLinearLayout2.removeAllViews();
                                                scrollView2.removeAllViews();

                                                for(int i = 0; i < atividades.length(); i++) {
                                                    final JSONObject atividade = atividades.getJSONObject(i);

                                                    Button buttonAtividade = new Button(HomeActivity_Professor.this);
                                                    buttonAtividade.setClickable(true);
                                                    buttonAtividade.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            bottomBar.selectTabAtPosition(2, true);
                                                            try {

                                                                topLinearLayout3.removeAllViews();
                                                                scrollView3.removeAllViews();

                                                                JSONObject turma_selecionada = notas.getJSONObject(turma.getString("nome"));
                                                                JSONArray atividade_selecionada = turma_selecionada.getJSONArray(atividade.getString("nome"));

                                                                for (int j = 0; j < atividade_selecionada.length(); j++) {
                                                                    JSONObject nota_aluno = atividade_selecionada.getJSONObject(j);

                                                                    Button buttonNota = new Button(HomeActivity_Professor.this);
                                                                    String text_nota =  "Aluno: " + nota_aluno.getString("aluno") + "\n" +
                                                                                        "Nota: " + nota_aluno.getString("nota");


                                                                    buttonNota.setText(text_nota);
                                                                    buttonNota.setTextSize(25);

                                                                    topLinearLayout3.addView(buttonNota);
                                                                }

                                                                scrollView3.addView(topLinearLayout3);
                                                            }
                                                            catch (JSONException e) {

                                                            }
                                                        }
                                                    });

                                                    String text_atividade = atividade.getString("nome");
                                                    buttonAtividade.setText(text_atividade);
                                                    buttonAtividade.setTextSize(25);

                                                    topLinearLayout2.addView(buttonAtividade);
                                                }
                                                scrollView2.addView(topLinearLayout2);
                                            }
                                            catch (Exception e) {
                                                Toast.makeText(HomeActivity_Professor.this, "Erro na lista de atividades", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                    String text_turma = turma.getString("nome");
                                    buttonTurmas.setText(text_turma);
                                    buttonTurmas.setTextSize(25);

                                    topLinearLayout.addView(buttonTurmas);
                                }

                                scrollView.addView(topLinearLayout);

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
                }){
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

}
