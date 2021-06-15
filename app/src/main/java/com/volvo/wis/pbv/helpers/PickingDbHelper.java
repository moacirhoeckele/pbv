package com.volvo.wis.pbv.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.volvo.wis.pbv.contracts.PickingViewContract.PickingViewEntry;

public class PickingDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "PBV.db";

    //https://www.androidpro.com.br/blog/armazenamento-de-dados/sqlite/
    //https://developer.android.com/training/data-storage/sqlite#java

    private static final String SQL_CREATE_PICKING_VIEW =
            "CREATE TABLE " + PickingViewEntry.TABLE_NAME + " (" +
                    PickingViewEntry._ID  + " INTEGER PRIMARY KEY," +
                    PickingViewEntry.COLUMN_NAME_ESTACAO + " INTEGER," +
                    PickingViewEntry.COLUMN_NAME_MODULO + " INTEGER," +
                    PickingViewEntry.COLUMN_NAME_BOX  + " INTEGER," +
                    PickingViewEntry.COLUMN_NAME_PRODUTO  + " INTEGER," +
                    PickingViewEntry.COLUMN_NAME_DATAPRODUCAO  + " TEXT," +
                    PickingViewEntry.COLUMN_NAME_CHASSI01  + " INTEGER," +
                    PickingViewEntry.COLUMN_NAME_QUANTIDADE01  + " INTEGER," +
                    PickingViewEntry.COLUMN_NAME_SEQUENCE01  + " INTEGER," +
                    PickingViewEntry.COLUMN_NAME_CHASSI02 + " INTEGER," +
                    PickingViewEntry.COLUMN_NAME_QUANTIDADE02+ " INTEGER," +
                    PickingViewEntry.COLUMN_NAME_SEQUENCE02 + " INTEGER," +
                    PickingViewEntry.COLUMN_NAME_CHASSI03 + " INTEGER," +
                    PickingViewEntry.COLUMN_NAME_QUANTIDADE03 + " INTEGER," +
                    PickingViewEntry.COLUMN_NAME_SEQUENCE03 + " INTEGER," +
                    PickingViewEntry.COLUMN_NAME_STATUS  + " TEXT)";

    private static final String SQL_DELETE_PICKING_VIEW =
            "DROP TABLE IF EXISTS " + PickingViewEntry.TABLE_NAME;

    public PickingDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PICKING_VIEW);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_PICKING_VIEW);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
