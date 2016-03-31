package cs407.onthedot;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by connerhuff on 3/9/16.
 */
public class DBHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String EVENTS_TABLE_NAME = "events";
    public static final String EVENTS_COLUMN_ID = "id";
    public static final String EVENTS_COLUMN_NAME = "name";
    public static final String EVENTS_COLUMN_START = "start";
    public static final String EVENTS_COLUMN_END = "end";
    public static final String EVENTS_COLUMN_DATE = "date";
    //public static final String CONTACTS_COLUMN_NAME = "name";
    SQLiteDatabase db;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        //db.openOrCreateDatabase("MyDBName.db", null, ((MainActivity) getActivity()).MODE_PRIVATE);
        //db = this.getReadableDatabase();
        db.execSQL(
                "create table events " +
                        "(id integer primary key, name text,start text,end text, date text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS events");
        onCreate(db);
    }

    //insert a new tuple in the db
    /*
    public boolean insertEvent(String name, String start, String end, String date)
    {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("start", start);
        contentValues.put("end", end);
        contentValues.put("date", date);
        db.insert("events", null, contentValues);
        return true;
    }
    */

    //gets a single tuple as identified by its ID
    /*
    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from events where id="+id+"", null );
        return res;
    }
    */

    //gets the number of rows in the database
    /*
    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, EVENTS_TABLE_NAME);
        return numRows;
    }
    */

    //example of a function that updates the values of a given entry in the database
    /*
    public boolean updateContact (Integer id, String name, String phone, String email, String street,String place)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.update("events", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }
    */

    //delete from the database example
    /*
    public void deleteEvent (String name, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM events WHERE name = '" + name + "' AND date = '" + date + "';");
    }
    */

    //example of a function that GETS information from the db
    /*
    public ArrayList<ScheduleEvent> getAllEventsFromDate(String date)
    {
        ArrayList<ScheduleEvent> array_list = new ArrayList<ScheduleEvent>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor res =  db.rawQuery( "select * from events where 'date' = " + date, null );
        String query = "select * from events where date = '" + date + "'";
        //query = query.replace("/", "////");
        Cursor res =  db.rawQuery(query, null );

        res.moveToFirst();

        while(res.isAfterLast() == false){

            String name = res.getString(res.getColumnIndex(EVENTS_COLUMN_NAME));
            String start = res.getString(res.getColumnIndex(EVENTS_COLUMN_START));
            String end = res.getString(res.getColumnIndex(EVENTS_COLUMN_END));
            String currDate = res.getString(res.getColumnIndex(EVENTS_COLUMN_DATE));

            ScheduleEvent s = new ScheduleEvent(name, start, end, currDate);
            array_list.add(s);
            res.moveToNext();
        }
        return array_list;
    }
    */

    //clear all tables. Use for debugging purposes.
    /*
    public void restartDBDebug(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS events");
    }
    */
}