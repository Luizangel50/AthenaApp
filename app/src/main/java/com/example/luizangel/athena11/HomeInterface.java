package com.example.luizangel.athena11;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * Created by ftuyama on 05/06/16.
 */
public interface HomeInterface {

    APImanager manager = APImanager.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat daysdf = new SimpleDateFormat("d");
    int buttonSize = 30;

    void showAtividades(JSONArray atividades);
    void showNotas(JSONArray notas);
    void showNotasBisonhoQueORodrigoFez(JSONObject notas);
    void showCalendario(JSONArray datas);
}
