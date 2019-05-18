package com.dmitry.pickletax;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static com.dmitry.pickletax.Constants.CLASSROOM_FREE;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "main_database";
    public static final int DB_VER = 1;

    private static final String CREATE_TABLE_CAMPUSES = "CREATE TABLE campuses (name TEXT PRIMARY KEY NOT NULL);";
    private static final String CREATE_TABLE_CLASSROOMS_TYPES = "CREATE TABLE classrooms_types (type_name TEXT PRIMARY KEY NOT NULL);";
    private static final String CREATE_TABLE_CLASSROOMS = "CREATE TABLE classrooms " +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "name TEXT NOT NULL, " +
            "campus_name TEXT NOT NULL, " +
            "type TEXT NOT NULL, " +
            "FOREIGN KEY(campus_name) REFERENCES campuses(name), " +
            "FOREIGN KEY(type) REFERENCES classrooms_type(type_name));";
    private static final String CREATE_TABLE_SCHEDULE = "CREATE TABLE schedule " +
            "(classroom_id INTEGER NOT NULL, " +
            "lesson_number INTEGER NOT NULL CHECK(lesson_number >= 1), " +
            "status INTEGER NOT NULL DEFAULT 0 CHECK(status IN (0, 1)), " +
            "description TEXT, " +
            "PRIMARY KEY(classroom_id, lesson_number), " +
            "FOREIGN KEY(classroom_id) REFERENCES classrooms(id));";
    private static final String CREATE_TABLE_SERVICE_VARS = "CREATE TABLE service_vars" +
            "(email TEXT NOT NULL, " +
            "city TEXT NOT NULL, " +
            "max_lesson_number INTEGER NOT NULL, " +
            "youngest_update TEXT, " +
            "oldest_update TEXT, " +
            "PRIMARY KEY(email, city));";

    private static final String TABLES_NAMES[] = {"campuses", "classrooms", "schedule", "service_vars", "classrooms_types"};

    public DBHelper(Context context) {
        this(context, DB_NAME, new Factory(), DB_VER);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CLASSROOMS_TYPES);
        db.execSQL(CREATE_TABLE_CAMPUSES);
        db.execSQL(CREATE_TABLE_CLASSROOMS);
        db.execSQL(CREATE_TABLE_SCHEDULE);
        db.execSQL(CREATE_TABLE_SERVICE_VARS);
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

    public void addCampuses(String[] campusNames) {
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
            values.put("campus_name", classroom.getCampus_name());
            values.put("type", classroom.getType());
            db.insert("campuses", null, values);
            db.close();
        }
    }

    public void addClassrooms(Classroom[] classrooms) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            for (Classroom classroom : classrooms) {
                ContentValues values = new ContentValues();
                values.put("name", classroom.getName());
                values.put("campus_name", classroom.getCampus_name());
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

    public void addScheduleItems(ScheduleItem[] scheduleItems) {
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

    public void addServiceVars(AuthValues authValues, int max_lesson_number) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            ContentValues values = new ContentValues();
            values.put("email", authValues.email);
            values.put("city", authValues.city);
            values.put("max_lesson_number", max_lesson_number);
            db.insert("service_vars", null, values);
            db.close();
        }
    }

    public AuthValues getServiceVars() {
        SQLiteDatabase db = getReadableDatabase();
        AuthValues authValues = new AuthValues();
        if (db != null) {
            Cursor cursor = (Cursor) db.rawQuery("SELECT email, city FROM service_vars;", null);
            cursor.moveToFirst();
            authValues.email = cursor.getString(0);
            authValues.city = cursor.getString(1);
            db.close();
            return authValues;
        } else {
            authValues.email = "email";
            authValues.email = "city";
            return authValues;
        }
    }

    public void initTables(String jsonDB, AuthValues authValues) {
        Gson gson = new Gson();
        InitDBObject initDBObject = gson.fromJson(jsonDB, InitDBObject.class);
        addServiceVars(authValues, initDBObject.max_lesson_number);
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            ContentValues contentValues = new ContentValues();

            for (String classroom_type : initDBObject.classrooms_types) {
                contentValues.put("type_name", classroom_type);
                db.insert("classrooms_types", null, contentValues);
            }
            contentValues.clear();

            for (InitDBObject.Campus campus : initDBObject.campuses) {
                contentValues.put("name", campus.name);
                db.insert("campuses", null, contentValues);
                contentValues.clear();

                contentValues.put("campus_name", campus.name);
                for (Classroom classroom : campus.classrooms) {
                    contentValues.put("name", classroom.getName());
                    contentValues.put("type", classroom.getType());
                    db.insert("classrooms", null, contentValues);
                }
                contentValues.clear();
            }

            for (int lesson_number = 1; lesson_number <= initDBObject.max_lesson_number; lesson_number++) {
                db.execSQL("INSERT INTO schedule SELECT id, ?, ?, NULL FROM classrooms;",
                        new String[]{Integer.toString(lesson_number), Integer.toString(CLASSROOM_FREE)});
            }

            db.close();
        }
    }

    public boolean isAuthorized() {
        SQLiteDatabase db = getReadableDatabase();
        if (db != null) {
            Cursor cursor = (Cursor) db.rawQuery("SELECT COUNT(*) FROM service_vars;", null);
            cursor.moveToFirst();
            int num = cursor.getInt(0);
            cursor.close();
            db.close();
            if (num > 0) return true;
        }
        return false;
    }

    public void clearDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            for (String tableName : TABLES_NAMES)
                db.delete(tableName, null, null);
            db.execSQL("VACUUM");
            db.close();
        }
    }

    public Classroom[] getClassroomsForShow(String campus, int status, int lesson_number) {
        SQLiteDatabase db = getReadableDatabase();
        if (db != null) {
            Cursor cursor = db.rawQuery("SELECT c.name, c.type FROM schedule s, classrooms c " +
                            "WHERE s.classroom_id = c.id AND " +
                            "c.campus_name = ? AND " +
                            "s.status = ? AND " +
                            "s.lesson_number = ?;",
                    new String[]{campus, Integer.toString(status), Integer.toString(lesson_number)});

            int itemCount = cursor.getCount();
            Classroom[] classrooms = new Classroom[cursor.getCount()];
            int i = 0;
            while (cursor.moveToNext()) {
                classrooms[i] = new Classroom(cursor.getString(0), campus, cursor.getString(1));
                i++;
            }

            db.close();
            return classrooms;
        }
        return null;
    }

    public String[] getCampusesForSpinner() {
        SQLiteDatabase db = getReadableDatabase();
        if (db != null) {
            Cursor cursor = db.rawQuery("SELECT name FROM campuses;", null);

            String[] campuses = new String[cursor.getCount()];
            int i = 0;
            while (cursor.moveToNext()) {
                campuses[i] = cursor.getString(0);
                i++;
            }

            db.close();
            return campuses;
        }
        return null;
    }

    public int getMaxLessonNumber() {
        SQLiteDatabase db = getReadableDatabase();
        if (db != null) {
            Cursor cursor = db.rawQuery("SELECT max_lesson_number FROM service_vars;", null);
            cursor.moveToFirst();
            return cursor.getInt(0);
        }
        return 0;
    }

    public Classroom[] getClassroomsForSpinner(String campus) {
        SQLiteDatabase db = getReadableDatabase();
        if (db != null) {
            Cursor cursor = db.rawQuery("SELECT name, type FROM classrooms WHERE campus_name = " + campus + ";", null);

            Classroom[] classrooms = new Classroom[cursor.getCount()];
            int i = 0;
            while (cursor.moveToNext()) {
                classrooms[i].setName(cursor.getString(0));
                classrooms[i].setType(cursor.getString(1));
                classrooms[i].setCampus_name(campus);
                i++;
            }

            db.close();
            return classrooms;
        }
        return null;
    }

    public ScheduleItem getScheduleItemForChangeStatus(String campus, String name, int lesson_number) {
        SQLiteDatabase db = getReadableDatabase();
        if (db != null) {
            Cursor cursor = db.rawQuery("SELECT c.id, s.status, s.description FROM schedule s, classrooms c " +
                    "WHERE c.id = s.classroom_id AND " +
                    "c.campus_name = ? AND " +
                    "c.name = ? AND " +
                    "s.lesson_number = ?;",
                    new String[]{campus, name, Integer.toString(lesson_number)});

            cursor.moveToFirst();
            int id = cursor.getInt(0);
            int status = cursor.getInt(1);
            String description = cursor.getString(2);

            return new ScheduleItem(id, lesson_number, status, description);
        }
        return null;
    }

    private static final class Factory implements SQLiteDatabase.CursorFactory {

        @Override
        public Cursor newCursor(SQLiteDatabase sqLiteDatabase, SQLiteCursorDriver sqLiteCursorDriver, String s, SQLiteQuery sqLiteQuery) {
            return new SQLiteCursor(sqLiteCursorDriver, s, sqLiteQuery);
        }
    }

    private class InitDBObject {
        @SerializedName("campuses")
        @Expose
        public Campus campuses[];
        @SerializedName("classrooms_types")
        @Expose
        public String classrooms_types[];
        @SerializedName("max_lesson_number")
        @Expose
        public int max_lesson_number;

        public class Campus {
            @SerializedName("name")
            @Expose
            public String name;

            @SerializedName("classrooms")
            @Expose
              public Classroom classrooms[];
        }
    }

}
