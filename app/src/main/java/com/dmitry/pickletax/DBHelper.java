package com.dmitry.pickletax;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "main_database";
    public static final int DB_VER = 1;

    private static final String CREATE_TABLE_CAMPUSES = "CREATE TABLE campuses (name TEXT PRIMARY KEY NOT NULL);";
    private static final String CREATE_TABLE_CLASSROOMS = "CREATE TABLE classrooms " +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "name TEXT NOT NULL, campus_name TEXT NOT NULL, " +
            "type TEXT NOT NULL, " +
            "FOREIGN KEY(campus_name) REFERENCES campuses(name));";
    private static final String CREATE_TABLE_SCHEDULE = "CREATE TABLE schedule " +
            "(classroom_id INTEGER NOT NULL, " +
            "lesson_number INTEGER NOT NULL, " +
            "status INTEGER NOT NULL DEFAULT 0, " +
            "description TEXT, " +
            "PRIMARY KEY(classroom_id, lesson_number), " +
            "FOREIGN KEY(classroom_id) REFERENCES classrooms(id));";
    private static final String CREATE_TABLE_SERVICE_VAR = "CREATE TABLE service_var" +
            "(email TEXT NOT NULL, " +
            "city TEXT NOT NULL, " +
            "youngest_update TEXT, " +
            "oldest_update TEXT, " +
            "PRIMARY KEY(email, city));";

    private static final class Factory implements SQLiteDatabase.CursorFactory {

        @Override
        public Cursor newCursor(SQLiteDatabase sqLiteDatabase, SQLiteCursorDriver sqLiteCursorDriver, String s, SQLiteQuery sqLiteQuery) {
            return new SQLiteCursor(sqLiteCursorDriver, s, sqLiteQuery);
        }
    }

    public DBHelper(Context context) {
        this(context, DB_NAME, new Factory(), DB_VER);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CAMPUSES);
        db.execSQL(CREATE_TABLE_CLASSROOMS);
        db.execSQL(CREATE_TABLE_SCHEDULE);
        db.execSQL(CREATE_TABLE_SERVICE_VAR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addCampus(String campusName) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            ContentValues values = new ContentValues();
            values.put("name", campusName);
            db.insert("campuses", null, values);
            db.close();
        }
    }

    public void addCampuses(String campusNames[]) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            for (String campusName : campusNames) {
                ContentValues values = new ContentValues();
                values.put("name", campusName);
                db.insert("campuses", null, values);
            }
            db.close();
        }
    }

    public void addClassroom(Classroom classroom) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            ContentValues values = new ContentValues();
            values.put("name", classroom.getName());
            values.put("campus_name", classroom.getCampus());
            values.put("type", classroom.getType());
            db.insert("campuses", null, values);
            db.close();
        }
    }

    public void addClassrooms(Classroom classrooms[]) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            for (Classroom classroom : classrooms) {
                ContentValues values = new ContentValues();
                values.put("name", classroom.getName());
                values.put("campus_name", classroom.getCampus());
                values.put("type", classroom.getType());
                db.insert("campuses", null, values);
            }
            db.close();
        }
    }

    public void addScheduleItem(ScheduleItem scheduleItem) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            ContentValues values = new ContentValues();
            values.put("classroom_id", scheduleItem.getClassroom_id());
            values.put("lesson_number", scheduleItem.getLesson_number());
            values.put("status", scheduleItem.getStatus());
            if (scheduleItem.getDescription() != null) {
                values.put("description", scheduleItem.getDescription());
            }
            db.insert("schedule", null, values);
            db.close();
        }
    }

    public void addScheduleItems(ScheduleItem scheduleItems[]) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            for (ScheduleItem scheduleItem : scheduleItems) {
                ContentValues values = new ContentValues();
                values.put("classroom_id", scheduleItem.getClassroom_id());
                values.put("lesson_number", scheduleItem.getLesson_number());
                values.put("status", scheduleItem.getStatus());
                if (scheduleItem.getDescription() != null) {
                    values.put("description", scheduleItem.getDescription());
                }
                db.insert("schedule", null, values);
            }
            db.close();
        }
    }

    public void addServiceVars(AuthValues authValues) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            ContentValues values = new ContentValues();
            values.put("email", authValues.email);
            values.put("city", authValues.city);
            db.insert("service_var", null, values);
        }
        db.close();
    }

    public boolean isAuthorized() {
        SQLiteDatabase db = getReadableDatabase();
        if (db != null) {
            Cursor cursor = (Cursor) db.rawQuery("SELECT COUNT(*) FROM service_var", null);
            cursor.moveToFirst();
            int num = cursor.getInt(0);
            cursor.close();
            if (num > 0) return true;
        }
        return false;
    }

}
