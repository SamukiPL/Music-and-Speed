package me.samuki.musicandspeed;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class MusicDbAdapter {
    private static final String DEBUG_TAG_DB = "Music_Database";

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "database.db";

    public static final String KEY_ID = "ID";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final int ID_COLUMN = 0;
    public static final String KEY_NAME = "NAME";
    public static final String NAME_OPTIONS = "TEXT NOT NULL";
    public static final int NAME_COLUMN = 1;
    public static final String KEY_SLOW_DRIVING = "SLOW_DRIVING";
    public static final String SLOW_DRIVING_OPTIONS = "INTEGER DEFAULT 0";
    public static final int SLOW_DRIVING_COLUMN = 2;
    public static final String KEY_FAST_DRIVING = "FAST_DRIVING";
    public static final String FAST_DRIVING_OPTIONS = "INTEGER DEFAULT 0";
    public static final int FAST_DRIVING_COLUMN = 3;

    private SQLiteDatabase db;
    private Context context;
    private DatabaseHelper dbHelper;

    public MusicDbAdapter(Context context) {
        this.context = context;
    }

    public MusicDbAdapter open() {
        dbHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            db = dbHelper.getReadableDatabase();
        }
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void createTable(String tableName) {
        String createTableSQL = "CREATE TABLE " + tableName + "( " +
                KEY_ID + " " + ID_OPTIONS + ", " +
                KEY_NAME + " " + NAME_OPTIONS + ", " +
                KEY_SLOW_DRIVING + " " + SLOW_DRIVING_OPTIONS +
                KEY_FAST_DRIVING + " " + FAST_DRIVING_OPTIONS +
                ");";
        db.execSQL(createTableSQL);
    }

    public void dropTable(String tableName) {
        String dropTableSQL = "DROP TABLE IF EXISTS " + tableName;
        db.execSQL(dropTableSQL);
    }

    private ContentValues newValues(String name, int slowDriving, int fastDriving) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME, name);
        contentValues.put(KEY_SLOW_DRIVING, slowDriving);
        contentValues.put(KEY_FAST_DRIVING, fastDriving);
        return contentValues;
    }

    public long insertData(String tableName, String name, int slowDriving, int fastDriving) {
        ContentValues insertValues = newValues(name, slowDriving, fastDriving);
        return db.insert(tableName, null, insertValues);
    }

    public boolean updateData(String tableName, String name, int slowDriving, int fastDriving) {
        //Nie wiem czy ta metoda się przyda, ale niech będzie :D
        String where = KEY_NAME + " = " + name;
        ContentValues updateValues = newValues(name, slowDriving, fastDriving);
        return db.update(tableName, updateValues, where, null) > 0;
    }

    public Cursor getAllSongs(String tableName) {
        String columns[] = {KEY_ID, KEY_NAME, KEY_SLOW_DRIVING, KEY_FAST_DRIVING};
        return db.query(tableName, columns, null, null, null, null, null);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}
