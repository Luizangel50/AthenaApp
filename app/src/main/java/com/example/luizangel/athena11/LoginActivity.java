package com.example.luizangel.athena11;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.luizangel.athena11.HomeActivity_Aluno;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity  implements
        View.OnClickListener{

    private Button botao;
    private EditText editText;
    private EditText editText2;
    private Switch switch1;
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";
    public static final String PREFRENCES_NAME = "Lembrar_Senha";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        botao = (Button) findViewById(R.id.button);
        botao.setOnClickListener(this);
        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        switch1 = (Switch) findViewById(R.id.switch1);

        try {
            SharedPreferences settings = getSharedPreferences(PREFRENCES_NAME, 0);
            String email = settings.getString("email", "");
            String senha = settings.getString("senha", "");
            Boolean lembrar_senha = settings.getBoolean("lembrar_senha", false);

            editText.setText(email);
            editText2.setText(senha);
            switch1.setChecked(lembrar_senha);
        }
        catch (Exception e) {

        }

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(LoginActivity.INPUT_METHOD_SERVICE);

                    in.hideSoftInputFromWindow(editText
                                    .getApplicationWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    return true;

                }
                return false;
            }
        });

        editText2.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(LoginActivity.INPUT_METHOD_SERVICE);

                    in.hideSoftInputFromWindow(editText2
                                    .getApplicationWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    return true;

                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:

                final Intent it_aluno = new Intent(this, HomeActivity_Aluno.class);
                final Intent it_professor = new Intent(this, HomeActivity_Professor.class);

                final String username = editText.getText().toString().trim();
                final String password = editText2.getText().toString().trim();

                String url = "http://192.168.0.24:8000/Mlogin/?username=" + username + "&password="+ password;
                RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject response_json = new JSONObject(response);
                                    if (response_json.getString("valido").equals("true")) {

                                        Toast.makeText(LoginActivity.this, "Bem Vindo, " + response_json.getString("class") + "!", Toast.LENGTH_LONG).show();

                                        it_aluno.putExtra("username", response_json.getString("username"));
                                        it_aluno.putExtra("email", response_json.getString("email"));
                                        it_aluno.putExtra("nome", response_json.getString("nome"));
                                        it_aluno.putExtra("id", response_json.getString("id"));
                                        it_aluno.putExtra("class", response_json.getString("class"));

                                        if (switch1.isChecked()) {
                                            SharedPreferences settings = getSharedPreferences(PREFRENCES_NAME, 0);
                                            settings.edit().putString("email", editText.getText().toString()).
                                                    putString("senha", editText2.getText().toString()).
                                                    putBoolean("lembrar_senha", true).commit();
                                        } else {
                                            SharedPreferences settings = getSharedPreferences(PREFRENCES_NAME, MODE_PRIVATE);
                                            settings.edit().clear().commit();
                                        }

                                        startActivity(it_aluno);
                                        //finish();

                                    }

                                    else if (response_json.getString("valido").equals("true") && response_json.getString("class").equals("Professor")) {
                                        Toast.makeText(LoginActivity.this, "Bem Vindo, " + response_json.getString("class") + "!", Toast.LENGTH_LONG).show();

                                        it_professor.putExtra("username", response_json.getString("username"));
                                        it_professor.putExtra("email", response_json.getString("email"));
                                        it_professor.putExtra("nome", response_json.getString("nome"));
                                        it_professor.putExtra("id", response_json.getString("id"));
                                        it_professor.putExtra("class", response_json.getString("class"));

                                        if (switch1.isChecked()) {
                                            SharedPreferences settings = getSharedPreferences(PREFRENCES_NAME, 0);
                                            settings.edit().putString("email", editText.getText().toString()).
                                                    putString("senha", editText2.getText().toString()).
                                                    putBoolean("lembrar_senha", true).commit();
                                        } else {
                                            SharedPreferences settings = getSharedPreferences(PREFRENCES_NAME, MODE_PRIVATE);
                                            settings.edit().clear().commit();
                                        }

                                        startActivity(it_professor);
                                    }

                                    else {

                                        Toast.makeText(LoginActivity.this, "Login inválido!",Toast.LENGTH_LONG).show();
                                    }
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

                /***********Timer para funcoes*********/
//                new Timer().schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        if (autenticado) {
//
//
//                        }
//                    }
//                }, 2000);

            break;


        }
    }

}
