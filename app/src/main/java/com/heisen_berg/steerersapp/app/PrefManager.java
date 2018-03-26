package com.heisen_berg.steerersapp.app;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by heisen-berg on 23/02/18.
 */

public class PrefManager {

    SharedPreferences pref;
    private static PrefManager instance  = null;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "SteerersApp";

    private static final String KEY_MODULE = "keyModule";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static synchronized PrefManager getInstance(Context context){
        if (null == instance) instance = new PrefManager(context);
        return instance;
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }

    /** true == user // false == admin **/
    public void setKeyModule(boolean keyModule){
        editor.putBoolean(KEY_MODULE, keyModule);
        editor.commit();
    }

    public boolean getKeyModule(){
        return pref.getBoolean(KEY_MODULE, true);
    }
}
