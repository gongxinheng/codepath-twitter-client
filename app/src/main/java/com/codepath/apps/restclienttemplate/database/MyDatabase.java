package com.codepath.apps.restclienttemplate.database;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = MyDatabase.NAME, version = MyDatabase.VERSION)
public class MyDatabase {

    public static final String NAME = "SimpleTwitterClientDataBase";

    public static final int VERSION = 3;
}
