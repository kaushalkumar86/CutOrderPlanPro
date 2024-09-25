package com.cut.order.plan.pro;
import com.cut.order.plan.pro.util.DBHelper;

import android.app.Application;
import android.content.Context;



public class MainApp extends Application {
	private static MainApp instance;
	private static DBHelper dbHelper;

    public MainApp() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

    public static DBHelper getDB() {
    	if (dbHelper == null) {
    		dbHelper = new DBHelper(instance);
    	}
    	return dbHelper;
    }
}
