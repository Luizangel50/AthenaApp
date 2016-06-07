package com.example.luizangel.athena11;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity_Professor extends AppCompatActivity implements HomeInterface {

    /**************Componentes**************/

    @Bind(R.id.textView_professor) TextView textView;
    @Bind(R.id.textView4_professor) TextView textView2;
    @Bind(R.id.linearLayout) LinearLayout topLinearLayout;
    @Bind(R.id.linearLayout2) LinearLayout topLinearLayout2;
    @Bind(R.id.linearLayout3) LinearLayout topLinearLayout3;
    @Bind(R.id.verticalScrollView_professor) ScrollView verticalScrollView;
    @Bind(R.id.verticalScrollView2_professor) ScrollView verticalScrollView2;
    @Bind(R.id.verticalScrollView3_professor) ScrollView verticalScrollView3;
    @Bind(R.id.vScrollView_professor) ScrollView verticalScrollViewZ;

    private CalendarPickerView calendarPickerView;
    private BottomBar bottomBar;

    private JSONObject notas;
    private JSONArray turmas, datas;
    private String nome, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_professor);
        initialize(savedInstanceState);
        ButterKnife.bind(this);
        performRequests();
    }

    void performRequests() {
        manager.delegate = this;
        manager.delegateKind = 1;
        manager.HTTPrequest(HomeActivity_Professor.this, APImanager.getInstance().APInotas(id));
        manager.HTTPrequest(HomeActivity_Professor.this, APImanager.getInstance().APIcalendario(id));
        manager.HTTPrequest(HomeActivity_Professor.this, APImanager.getInstance().APIatividades(id));
    }

    void initialize(Bundle savedInstanceState) {

        nome = getIntent().getExtras().getString("nome");
        id = getIntent().getExtras().getString("id");

        /**************Calendario**************/
        Calendar nextMonth = Calendar.getInstance();
        nextMonth.add(Calendar.MONTH, 1);
        calendarPickerView = (CalendarPickerView)findViewById(R.id.calendarView_professor);
        calendarPickerView.init(new Date(), nextMonth.getTime())
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE);
        calendarPickerView.setVisibility(View.INVISIBLE);

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

    public void showAtividades (JSONArray listaTurmas) {
        this.turmas = listaTurmas;
        try {
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

                                if (manager.isExpired(atividade.getString("prazo")))
                                    buttonAtividade.setTextColor(Color.parseColor("red"));
                                else buttonAtividade.setTextColor(Color.parseColor("blue"));

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
        } catch (JSONException e) {
            Toast.makeText(HomeActivity_Professor.this,"Erro Json",Toast.LENGTH_LONG).show();
        }
    }

    public void showNotasBisonhoQueORodrigoFez(JSONObject listaNotas) {
        this.notas = listaNotas;
    }

    public void showNotas (JSONArray listaNotas) {
    }

    public void showCalendario (JSONArray calendario) {
        this.datas = calendario;
        CalendarCellDecorator cellDecorator = new CalendarCellDecorator() {
            @Override
            public void decorate(CalendarCellView cellView, Date date) {
                try {
                    for (int i = 0; i < datas.length(); i++) {
                        final JSONObject data = datas.getJSONObject(i);

                        if (date.equals(sdf.parse(data.getString("prazo")))) {
                            if (data.has("fechada") && data.getString("fechada").equals("true"))
                                cellView.setDayOfMonthTextView(cellContent(daysdf.format(date),
                                        cellView.getDayOfMonthTextView(), Color.rgb(255, 205, 210),
                                        (data.getString("turma")
                                                + "\n" + "Sub:" + data.getString("submissoes")
                                                + "\n" + data.getString("atividade"))
                                ));
                            else
                                cellView.setDayOfMonthTextView(cellContent(daysdf.format(date),
                                        cellView.getDayOfMonthTextView(), Color.rgb(190, 220, 250),
                                        (data.getString("turma") +  "\n"
                                                + "Sub:" + data.getString("submissoes")
                                                + "\n" + data.getString("atividade"))
                                ));
                            return;
                        }
                    }
                } catch (JSONException|ParseException e) {
                    Toast.makeText(HomeActivity_Professor.this, "Erro Json", Toast.LENGTH_LONG).show();
                }
            }
        };
        calendarPickerView.setDecorators(new ArrayList<>(Collections.singletonList(cellDecorator)));
    }

    TextView cellContent(String day, TextView cellTextView, int color, String text) {
        if (cellTextView.getText().equals(day)) {
            cellTextView.setLines(3);
            cellTextView.setText(text);
            cellTextView.setBackgroundColor(color);
            cellTextView.setTextSize(16);
        }
        return cellTextView;
    }
}
