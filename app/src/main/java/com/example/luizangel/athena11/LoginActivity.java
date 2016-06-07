package com.example.luizangel.athena11;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;


public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.button) Button botao;
    @Bind(R.id.editText) EditText editText;
    @Bind(R.id.editText2) EditText editText2;
    @Bind(R.id.switch1) Switch switch1;

    public static final String PREFERENCES_NAME = "Lembrar_Senha";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initialize();
    }

    public void initialize() {
        try {
            SharedPreferences settings = getSharedPreferences(PREFERENCES_NAME, 0);
            editText.setText(settings.getString("email", ""));
            editText2.setText(settings.getString("senha", ""));
            switch1.setChecked(settings.getBoolean("lembrar_senha", false));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @OnEditorAction(R.id.editText) boolean onEditorAction(KeyEvent event) {

        if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            InputMethodManager in = (InputMethodManager) getSystemService(LoginActivity.INPUT_METHOD_SERVICE);

            in.hideSoftInputFromWindow(editText.getApplicationWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            return true;
        }
        return false;
    }

    @OnEditorAction(R.id.editText2) boolean onEditor2Action(KeyEvent event) {

        if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            InputMethodManager in = (InputMethodManager) getSystemService(LoginActivity.INPUT_METHOD_SERVICE);

            in.hideSoftInputFromWindow(editText2.getApplicationWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            return true;
        }
        return false;
    }

    @OnClick(R.id.button)
    public void onClick() {

        final Intent it_aluno = new Intent(this, HomeActivity_Aluno.class);
        final Intent it_professor = new Intent(this, HomeActivity_Professor.class);

        final String username = editText.getText().toString().trim();
        final String password = editText2.getText().toString().trim();

        String url = APImanager.getInstance().APIlogin(username, password);
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject response_json = new JSONObject(response);
                        if (response_json.getString("valido").equals("true")) {
                            Toast.makeText(LoginActivity.this, "Bem Vindo, " + response_json.getString("class") + "!", Toast.LENGTH_LONG).show();

                            if (switch1.isChecked()) {
                                SharedPreferences settings = getSharedPreferences(PREFERENCES_NAME, 0);
                                settings.edit().putString("email", editText.getText().toString()).
                                                putString("senha", editText2.getText().toString()).
                                                putBoolean("lembrar_senha", true).apply();
                            } else {
                                SharedPreferences settings = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
                                settings.edit().clear().apply();
                            }

                            if (response_json.getString("class").equals("Aluno")) {
                                it_aluno.putExtra("username",   response_json.getString("username"));
                                it_aluno.putExtra("email",      response_json.getString("email"));
                                it_aluno.putExtra("nome",       response_json.getString("nome"));
                                it_aluno.putExtra("id",         response_json.getString("id"));
                                it_aluno.putExtra("class",      response_json.getString("class"));
                                startActivity(it_aluno);
                            }
                            else {
                                it_professor.putExtra("username",   response_json.getString("username"));
                                it_professor.putExtra("email",      response_json.getString("email"));
                                it_professor.putExtra("nome",       response_json.getString("nome"));
                                it_professor.putExtra("id",         response_json.getString("id"));
                                it_professor.putExtra("class",      response_json.getString("class"));
                                startActivity(it_professor);
                            }
                        }
                        else Toast.makeText(LoginActivity.this, "Login inválido!",Toast.LENGTH_LONG).show();
                    }
                    catch (JSONException e) {
                        Toast.makeText(LoginActivity.this,"Erro Json",Toast.LENGTH_LONG).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                }
                });
        requestQueue.add(stringRequest);
    }

}
