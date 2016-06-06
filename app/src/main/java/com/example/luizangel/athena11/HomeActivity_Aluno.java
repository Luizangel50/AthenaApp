package com.example.luizangel.athena11;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarFragment;
import com.roughike.bottombar.OnTabSelectedListener;

import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarCellView;
import com.squareup.timessquare.CalendarPickerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class HomeActivity_Aluno extends AppCompatActivity implements HomeInterface{

    private ScrollView scrollView3;
    private HorizontalScrollView horizontalScrollView, horizontalScrollView2;
    private LinearLayout topLinearLayout, topLinearLayout2, topLinearLayout3;
    private TextView textView, textView4;
    private CalendarPickerView calendarPickerView;
    private BottomBar bottomBar;
    private final int buttonSize = 30;

    private JSONArray atividades, notas, datas;
    private String nome, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initialize(savedInstanceState);
        performRequests();
    }

    public void initialize(Bundle savedInstanceState) {

        nome = getIntent().getExtras().getString("nome");
        id = getIntent().getExtras().getString("id");

        /**************Calendario**************/
        Calendar nextMonth = Calendar.getInstance();
        nextMonth.add(Calendar.MONTH, 1);
        calendarPickerView = (CalendarPickerView) findViewById(R.id.calendarView);
        calendarPickerView.init(new Date(), nextMonth.getTime())
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
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("Notas");
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

    void performRequests() {
        manager.delegate = this;
        manager.delegateKind = 0;
        manager.HTTPrequest(HomeActivity_Aluno.this, APImanager.getInstance().APInotas(id));
        manager.HTTPrequest(HomeActivity_Aluno.this, APImanager.getInstance().APIcalendario(id));
        manager.HTTPrequest(HomeActivity_Aluno.this, APImanager.getInstance().APIatividades(id));
    }

    public void showAtividades(JSONArray listaAtividades) {
        this.atividades = listaAtividades;
        try {
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
                                    String textNota = "Atividade: " + nota.getString("atividade") + "\n" +
                                            "Nota: " + nota.getString("nota") + "\n" +
                                            "Prazo: " + nota.getString("prazo") + "\n" +
                                            "Envio: " + nota.getString("data_envio");

                                    buttonNota.setText(textNota);
                                    buttonNota.setTextSize(buttonSize);

                                    topLinearLayout3.addView(buttonNota);
                                }
                            }
                        } catch (JSONException e) {
                            Toast.makeText(HomeActivity_Aluno.this, "Erro no JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                if (atividade.getString("entrega").equals("false")) {
                    if (isExpired(atividade.getString("prazo")))
                        buttonAtividade.setTextColor(Color.parseColor("red"));
                    else buttonAtividade.setTextColor(Color.parseColor("blue"));
                    topLinearLayout.addView(buttonAtividade);
                } else topLinearLayout2.addView(buttonAtividade);
            }
        } catch (JSONException e) {
            Toast.makeText(HomeActivity_Aluno.this, "Erro Json", Toast.LENGTH_LONG).show();
        }
    }

    public void showNotasBisonhoQueORodrigoFez(JSONObject listaNotas) {

    }

    public void showNotas(JSONArray listaNotas) {
        this.notas = listaNotas;
    }

    public void showCalendario(JSONArray calendario) {
        this.datas = calendario;

        CalendarCellDecorator cellDecorator = new CalendarCellDecorator() {
            @Override
            public void decorate(CalendarCellView cellView, Date date) {
                try {
                    for (int i = 0; i < datas.length(); i++) {
                        final JSONObject data = datas.getJSONObject(i);

                        if (data.has("data_envio") && date.equals(sdf.parse(data.getString("data_envio")))) {
                            cellView.setDayOfMonthTextView(cellContent(daysdf.format(date),
                                    cellView.getDayOfMonthTextView(), Color.rgb(200, 225, 165),
                                    (data.getString("turma") + "\n" + data.getString("atividade"))
                                ));
                            return;
                        }
                        else if (date.equals(sdf.parse(data.getString("prazo")))) {
                            if (data.has("fechada") && data.getString("fechada").equals("true"))
                                cellView.setDayOfMonthTextView(cellContent(daysdf.format(date),
                                        cellView.getDayOfMonthTextView(), Color.rgb(255, 205, 210),
                                        (data.getString("turma") + "\n" + data.getString("atividade"))
                                ));
                            else
                                cellView.setDayOfMonthTextView(cellContent(daysdf.format(date),
                                    cellView.getDayOfMonthTextView(), Color.rgb(255, 245, 160),
                                    (data.getString("turma") + "\n" + data.getString("atividade"))
                                ));
                            return;
                        }
                    }
                } catch (JSONException|ParseException e) {
                    Toast.makeText(HomeActivity_Aluno.this, "Erro Json", Toast.LENGTH_LONG).show();
                }
            }
        };
        calendarPickerView.setDecorators(new ArrayList<>(Collections.singletonList(cellDecorator)));
    }

    TextView cellContent(String day, TextView cellTextView, int color, String text) {
        if (cellTextView.getText().equals(day)) {
            cellTextView.setLines(2);
            cellTextView.setText(text);
            cellTextView.setBackgroundColor(color);
            cellTextView.setTextSize(16);
        }
        return cellTextView;
    }

    public boolean isExpired (String date) {
        try {
            return (new Date().after(sdf.parse(date)));
        } catch (ParseException e) {
            System.out.println("error");
        }
        return true;
    }

}
