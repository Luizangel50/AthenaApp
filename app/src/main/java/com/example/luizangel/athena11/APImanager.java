package com.example.luizangel.athena11;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class APImanager {
    private static APImanager ourInstance = new APImanager();
    public static APImanager getInstance() {
        return ourInstance;
    }

    int delegateKind;
    HomeInterface delegate;

    /* String APIurl = "http://192.168.137.62:8000"; */
    String APIurl    = "http://10.0.2.2:8000";

    public String APIlogin (String username, String password) {
        return APIurl + "/Mlogin/?" + "username=" + username + "&password=" + password;
    }
    public String APIatividades (String id) {
        return APIurl + "/Matividades/?id=" + id;
    }
    public String APInotas      (String id) {
        return APIurl + "/Mnotas/?id="      + id;
    }
    public String APIcalendario (String id) {
        return APIurl + "/Mcalendario/?id=" + id;
    }

    /**
     * Simple HTTP GET request
     */
    public void HTTPrequest(final Context context, final String url) {

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            notifyDelegate(url, new JSONObject(response));
                        }
                        catch (JSONException e) {
                            Toast.makeText(context, "Erro Json Notas",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }){
        };
        requestQueue.add(request);
    }

    public void notifyDelegate (String url, JSONObject response) {
        if (response != null) {
            try {
                if (delegateKind == 0) {
                    if (url.contains("Matividades"))    delegate.showAtividades(response.getJSONArray("atividades"));
                    if (url.contains("Mnotas"))         delegate.showNotas(response.getJSONArray("notas"));
                    if (url.contains("Mcalendario"))    delegate.showCalendario(response.getJSONArray("datas"));
                }
                if (delegateKind == 1) {
                    if (url.contains("Matividades"))    delegate.showAtividades(response.getJSONArray("turmas"));
                    if (url.contains("Mnotas"))         delegate.showNotasBisonhoQueORodrigoFez(response);
                    if (url.contains("Mcalendario"))    delegate.showCalendario(response.getJSONArray("datas"));
                }
            } catch (JSONException e) {
                System.out.println("Json error");
            }

        }
    }
}
