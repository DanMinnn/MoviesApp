package com.movieapi.movie.database;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    public static final String SESSION_REMEMBERME = "rememberMe";

    //Remember me variables
    public static final String IS_REMEMBER = "IsRememberMe";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";

    SharedPreferences userSession;
    SharedPreferences.Editor editor;
    Context context;

    public SessionManager(Context _context, String sessionName) {
        context = _context;
        userSession = context.getSharedPreferences(sessionName, Context.MODE_PRIVATE);
        editor = userSession.edit();
    }

    /*
       Users
       Login session
     */

    /*
        Remember me
        Session functions
     */

    public void createRememberMeSession(String email, String password){
        editor.putBoolean(IS_REMEMBER, true);

        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);

        editor.commit();
    }

    public HashMap<String, String> getRememberMeFromSession(){
        HashMap<String, String> userData = new HashMap<>();

        userData.put(KEY_EMAIL, userSession.getString(KEY_EMAIL, null));
        userData.put(KEY_PASSWORD, userSession.getString(KEY_PASSWORD, null));

        return userData;
    }

    public boolean checkRememberMe(){
        if (userSession.getBoolean(IS_REMEMBER, false))
            return true;
        else
            return false;
    }
}

