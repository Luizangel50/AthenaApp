package com.example.luizangel.athena11;

/**
 * Created by ftuyama on 04/06/16.
 */
public class APImanager {
    private static APImanager ourInstance = new APImanager();
    public static APImanager getInstance() {
        return ourInstance;
    }

    /* String APIurl = "http://192.168.137.62:8000"; */
    String APIurl              = "http://10.0.2.2:8000";
    String APIurllogin         = APIurl + "/Mlogin/?";
    String APIurlatividades    = APIurl + "/Matividades/?id=";
    String APIurlnotas         = APIurl + "/Mnotas/?id=";
    String APIurlcalendario    = APIurl + "/Mcalendario/?id=";

    public String APIlogin (String username, String password) {
        return APIurllogin + "username=" + username + "&password=" + password;
    }
    public String APIatividades (String id) {
        return APIurlatividades + id;
    }
    public String APInotas (String id)      { return APIurlnotas + id;      }
    public String APIcalendario (String id) {
        return APIurlcalendario + id;
    }




}
