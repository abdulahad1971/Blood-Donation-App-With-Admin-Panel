package com.abdulahad.blooddonationapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "user_session_pref";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_NAME = "name";
    private static final String KEY_IS_ADMIN = "is_admin"; // ✅ Admin check এর জন্য
//    private static final String KEY_USER_ID = "user_id";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // ✅ Session তৈরি (admin কিনা সেটা দিয়ে)
// ✅ Session তৈরি করার সময় userId ও পাঠাতে হবে
    public void createLoginSession( String phone, String name) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_NAME, name);
//        editor.putInt(KEY_USER_ID, userId); // ✅ এখন ঠিক আছে

        if (phone.equals("01111111111")) {
            editor.putBoolean(KEY_IS_ADMIN, true);
        } else {
            editor.putBoolean(KEY_IS_ADMIN, false);
        }

        editor.apply();
    }

    // ✅ Admin কিনা চেক করো
    public boolean isAdmin() {
        return pref.getBoolean(KEY_IS_ADMIN, false);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserPhone() {
        return pref.getString(KEY_PHONE, null);
    }

    public String getUserName() {
        return pref.getString(KEY_NAME, null);
    }

//    public int getUserId() {
//        return pref.getInt(KEY_USER_ID, -1); // -1 = invalid
//    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}

