package com.example.luizangel.athena11;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by ftuyama on 05/06/16.
 */
public interface HomeInterface {

    APImanager manager = APImanager.getInstance();

    void showAtividades(JSONArray atividades);
    void showNotas(JSONArray notas);
    void showNotasBisonhoQueORodrigoFez(JSONObject notas);
    void showCalendario(JSONArray datas);
}
