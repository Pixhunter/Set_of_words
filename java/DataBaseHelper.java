package com;

import static com.VariablesConfig.DB_NAME;
import static com.BuildConfig.VERSION_CODE;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

// Override class for SQLite using
public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION_CODE);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) { }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) { }

}
