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

public class HomeActivity_Aluno extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;
    private ScrollView verticalScrollView;
    private HorizontalScrollView horizontalScrollView;
    private HorizontalScrollView horizontalScrollView2;
    private TextView textView;
    private TextView textView4;
    private CalendarPickerView calendarPickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        String nome = getIntent().getExtras().getString("nome");
        setTitle("Bem vindo, " + nome + "!");


        /************Atividades************/

        String id = getIntent().getExtras().getString("id");
        String url = "http://192.168.0.24:8000/Matividades/?id=" + id;
        RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity_Aluno.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject response_json = new JSONObject(response);
                            if (response_json.getString("valido").equals("true")) {

                                JSONArray atividades = response_json.getJSONArray("atividades");

                                HorizontalScrollView scrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
                                HorizontalScrollView scrollView2 = (HorizontalScrollView) findViewById(R.id.horizontalScrollView2);

                                LinearLayout topLinearLayout = new LinearLayout(HomeActivity_Aluno.this);
                                LinearLayout topLinearLayout2 = new LinearLayout(HomeActivity_Aluno.this);

                                topLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                                topLinearLayout2.setOrientation(LinearLayout.HORIZONTAL);

                                for(int i = 0; i < atividades.length(); i++) {
                                    JSONObject atividade = atividades.getJSONObject(i);
//                                    Toast.makeText(HomeActivity_Aluno.this, atividade.toString(), Toast.LENGTH_LONG).show();

                                    final Button textViewAtividadesPendentes = new Button(HomeActivity_Aluno.this);
                                    String text_atividade = atividade.getString("turma") + "\n" +
                                                            atividade.getString("nome") + "\n" +
                                                            "Nota: " + atividade.getString("nota") + "\n" +
                                                            "Envio: " + atividade.getString("envio") + "\n" +
                                                            "Prazo: " + atividade.getString("prazo");
                                    textViewAtividadesPendentes.setText(text_atividade);
                                    textViewAtividadesPendentes.setTextSize(25);

                                    if(atividade.getString("entrega").equals("false")) {
                                        topLinearLayout.addView(textViewAtividadesPendentes);
                                    }

                                    else {
                                        topLinearLayout2.addView(textViewAtividadesPendentes);
                                    }
                                }

                                scrollView.addView(topLinearLayout);
                                scrollView2.addView(topLinearLayout2);

                            }
                            else {

                                Toast.makeText(HomeActivity_Aluno.this, "Lista de Atividades inválida!",Toast.LENGTH_LONG).show();
                            }
                        }

                        catch (JSONException e) {

                            Toast.makeText(HomeActivity_Aluno.this,"Erro Json",Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(HomeActivity_Aluno.this,error.toString(),Toast.LENGTH_LONG).show();

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

        /*************Calendario*************/
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
        verticalScrollView = (ScrollView) findViewById(R.id.verticalScrollView_aluno);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        horizontalScrollView2 = (HorizontalScrollView) findViewById(R.id.horizontalScrollView2);

        BottomBar bottomBar = BottomBar.attach(this, savedInstanceState);
        bottomBar.setFragmentItems(getSupportFragmentManager(), R.id.fragmentContainer,
                new BottomBarFragment(new SampleFragment(), R.drawable.ic_home, "Home"),
                new BottomBarFragment(new SampleFragment(), R.drawable.ic_prazos, "Prazos"),
                new BottomBarFragment(new SampleFragment(), R.drawable.ic_logout, "Logout")
        );

        // Setting colors for different tabs when there's more than three of them.
        bottomBar.mapColorForTab(0, "#3B494C");
        bottomBar.mapColorForTab(1, "#00796B");
        bottomBar.mapColorForTab(2, "#FF5252");

        bottomBar.setOnItemSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                switch (position) {
                    case 0:
                        verticalScrollView.setVisibility(View.VISIBLE);
                        calendarPickerView.setVisibility(View.INVISIBLE);
                        horizontalScrollView2.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.VISIBLE);
                        textView4.setVisibility(View.VISIBLE);
                        horizontalScrollView.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        verticalScrollView.setVisibility(View.INVISIBLE);
                        horizontalScrollView.setVisibility(View.INVISIBLE);
                        horizontalScrollView2.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.INVISIBLE);
                        textView4.setVisibility(View.INVISIBLE);
                        calendarPickerView.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        Intent it = new Intent(HomeActivity_Aluno.this, LoginActivity.class);
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
    }

}
