package cs407.onthedot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by connerhuff on 3/9/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "OnTheDot.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TRIP_TABLE_NAME = "TRIP";
    public static final String TRIP_COLUMN_TRIP_ID = "TRIP_ID";
    public static final String TRIP_COLUMN_DATE = "DATE";
    public static final String TRIP_COLUMN_LATITUDE = "LATITUDE";
    public static final String TRIP_COLUMN_LONGITUDE = "LONGITUDE";
    public static final String TRIP_COLUMN_COMPLETE = "TRIP_COMPLETE";

    public static final String PARTICIPANTS_TABLE_NAME = "PARTICIPANT";
    public static final String PARTICIPANTS_COLUMN_TRIP_ID = "TRIP_ID";
    public static final String PARTICIPANTS_COLUMN_PARTICIPANT_ID = "PARTICIPANT_ID";
    public static final String PARTICIPANTS_COLUMN_PARTICIPANT_NAME = "PARTICIPANT_NAME";

    /*
      Use this date format when inserting or retrieving a date from the database.
     */
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TRIP_TABLE_NAME + "(" +
                        TRIP_COLUMN_TRIP_ID + " INTEGER PRIMARY KEY NOT NULL," +
                        TRIP_COLUMN_DATE + " TEXT," +
                        TRIP_COLUMN_LATITUDE + " REAL," +
                        TRIP_COLUMN_LONGITUDE + " REAL," +
                        TRIP_COLUMN_COMPLETE + " INTEGER)"
        );

        db.execSQL(
                "CREATE TABLE " + PARTICIPANTS_TABLE_NAME + "(" +
                        PARTICIPANTS_COLUMN_TRIP_ID + " INTEGER," +
                        PARTICIPANTS_COLUMN_PARTICIPANT_ID + " TEXT," +
                        PARTICIPANTS_COLUMN_PARTICIPANT_NAME + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TRIP_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PARTICIPANTS_TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Adds the trip to the database.
     *
     * @param trip The Trip object that is to be added to the database
     * @return Returns the trip ID if the trip was successfully added; -1 otherwise
     */
    public long addTrip(Trip trip) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();

        long tripID;

        try {
            boolean success = true;

            ContentValues contentValues = new ContentValues();
            contentValues.put(TRIP_COLUMN_DATE, dateFormat.format(trip.getMeetupTime()));
            contentValues.put(TRIP_COLUMN_LATITUDE, trip.getDestinationLatitude());
            contentValues.put(TRIP_COLUMN_LONGITUDE, trip.getDestinationLongitude());
            contentValues.put(TRIP_COLUMN_COMPLETE, ((trip.isTripComplete())? 1 : 0));

            tripID = db.insert(TRIP_TABLE_NAME, null, contentValues);

            // Check to make sure the trip was successfully inserted into the database
            if (tripID < 0) {

                // The trip was not inserted successfully, so don't insert the participants
                success = false;
            }
            else {

                // Since the trip was inserted successfully, insert the participants
                success = insertParticipants(tripID, trip.getFacebookFriendsList());
            }

            if (success) {
                db.setTransactionSuccessful();
            }
        } catch (Exception e) { // Error in between database transaction
            Log.d("DBHelper",
                    "ERROR: Database Transaction was unsuccessful and threw an unexpected error",
                    e.getCause());

            tripID = -1;
        } finally { // Make sure to end the transaction
            db.endTransaction();
        }

        return tripID;
    }

    /**
     * Gets the number of trips (rows) stored in the database.
     *
     * @return The number of trips stored in the database
     */
    public int numberOfTrips(){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, TRIP_TABLE_NAME);
    }

    /**
     * Gets the trip in the database corresponding to the trip ID.
     *
     * @param tripID
     * @return Returns a Trip object corresponding to the trip ID; null otherwise if no trip
     * was found
     */
    public Trip getTripByTripID(long tripID) {

        Cursor res = getTripByIDWithParticipants(tripID);

        // Make sure we were able to find a trip with the corresponding ID
        if (res.getCount() <= 0) {
            return null;
        }

        res.moveToFirst();

        // Gather all the trip information and create a new Trip object
        LatLng destination = new LatLng(res.getDouble(res.getColumnIndex(TRIP_COLUMN_LATITUDE)),
                res.getDouble(res.getColumnIndex(TRIP_COLUMN_LONGITUDE)));

        boolean tripComplete = (res.getInt(res.getColumnIndex(TRIP_COLUMN_COMPLETE)) == 1);

        Date meetupTime;

        try {
            meetupTime = dateFormat.parse(res.getString(res.getColumnIndex(TRIP_COLUMN_DATE)));
        } catch (ParseException e) {
            Log.d("DBHelper", "ERROR: Corrupted trip (ID: " +
                    tripID + ") due to unparsable date stored in database");

            return null;
        }

        Trip trip = new Trip(tripID, destination, meetupTime, new ArrayList<Friend>(), tripComplete);

        // Get all the participant IDs and add them to the FacebookFriendsList in the trip object
        while(!res.isAfterLast()) {

            Friend friend = new Friend(
                    res.getString(res.getColumnIndex(PARTICIPANTS_COLUMN_PARTICIPANT_NAME)),
                    false,
                    res.getString(res.getColumnIndex(PARTICIPANTS_COLUMN_PARTICIPANT_ID)));

            trip.getFacebookFriendsList().add(friend);

            res.moveToNext();
        }

        res.close();

        return trip;
    }

    /**
     * Get all the Trips currently stored in the database.
     *
     * @return An ArrayList containing all the Trip objects stored in the database.  If the
     * database is empty, an empty ArrayList is returned.
     */
    public ArrayList<Trip> getAllTrips() {
        return getAllTripsByTripIDFromCursor(getAllTripIDs());
    }

    /**
     * Get all the active (i.e. not completed) Trips currently stored in the database.
     *
     * @return An ArrayList containing all the Trip objects stored in the database.  If the
     * database is empty, an empty ArrayList is returned.
     */
    public ArrayList<Trip> getAllActiveTrips() {
        return getAllTripsByTripIDFromCursor(getAllActiveTripIDs());
    }

    /**
     * Get all the past (i.e. completed) Trips currently stored in the database.
     *
     * @return An ArrayList containing all the Trip objects stored in the database.  If the
     * database is empty, an empty ArrayList is returned.
     */
    public ArrayList<Trip> getAllPastTrips() {
        return getAllTripsByTripIDFromCursor(getAllPastTripIDs());
    }

    /**
     * Delete a trip from the database corresponding to the trip ID
     *
     * @param tripID The trip ID corresponding to the trip to delete
     */
    public void deleteTripByTripID(long tripID) {
        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder sb = new StringBuilder();

        sb.append("DELETE FROM ");
        sb.append(TRIP_TABLE_NAME);
        sb.append(" WHERE ");
        sb.append(TRIP_COLUMN_TRIP_ID);
        sb.append(" = ");
        sb.append(tripID);

        // Delete the trip corresponding to the trip ID
        db.execSQL(sb.toString());

        sb = new StringBuilder();

        sb.append("DELETE FROM ");
        sb.append(PARTICIPANTS_TABLE_NAME);
        sb.append(" WHERE ");
        sb.append(PARTICIPANTS_COLUMN_TRIP_ID);
        sb.append(" = ");
        sb.append(tripID);

        // Delete the trip's participants corresponding trip ID
        db.execSQL(sb.toString());
    }

    private ArrayList<Trip> getAllTripsByTripIDFromCursor(Cursor resOfTripIDs) {
        ArrayList<Trip> trips = new ArrayList<>();

        // Make sure the database isn't empty
        if (resOfTripIDs.getCount() <= 0) {
            return trips;
        }

        resOfTripIDs.moveToFirst();

        while(!resOfTripIDs.isAfterLast()) {
            Trip trip = getTripByTripID(resOfTripIDs.getLong(resOfTripIDs.getColumnIndex(TRIP_COLUMN_TRIP_ID)));

            if (trip != null) {
                trips.add(trip);
            }

            resOfTripIDs.moveToNext();
        }

        resOfTripIDs.close();

        return trips;
    }

    private boolean insertParticipants(long tripID, List<Friend> facebookFriendsList) {

        SQLiteDatabase db = this.getWritableDatabase();

        for (Friend participant : facebookFriendsList) {
            ContentValues contentValues = new ContentValues();

            contentValues.put(PARTICIPANTS_COLUMN_TRIP_ID, tripID);
            contentValues.put(PARTICIPANTS_COLUMN_PARTICIPANT_ID, participant.getId());
            contentValues.put(PARTICIPANTS_COLUMN_PARTICIPANT_NAME, participant.getName());

            if (db.insert(PARTICIPANTS_TABLE_NAME, null, contentValues) < 0) {
              return false;
            }
        }

        return true;
    }

    private Cursor getTripByIDWithParticipants(long tripID){
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT t.");
        sb.append(TRIP_COLUMN_TRIP_ID);
        sb.append(", t.");
        sb.append(TRIP_COLUMN_DATE);
        sb.append(", t.");
        sb.append(TRIP_COLUMN_LATITUDE);
        sb.append(", t.");
        sb.append(TRIP_COLUMN_LONGITUDE);
        sb.append(", t.");
        sb.append(TRIP_COLUMN_COMPLETE);
        sb.append(", p.");
        sb.append(PARTICIPANTS_COLUMN_PARTICIPANT_ID);
        sb.append(", p.");
        sb.append(PARTICIPANTS_COLUMN_PARTICIPANT_NAME);
        sb.append(" FROM ");
        sb.append(TRIP_TABLE_NAME);
        sb.append(" t INNER JOIN ");
        sb.append(PARTICIPANTS_TABLE_NAME);
        sb.append(" p ON p.");
        sb.append(PARTICIPANTS_COLUMN_TRIP_ID);
        sb.append(" = t.");
        sb.append(TRIP_COLUMN_TRIP_ID);
        sb.append(" WHERE t.");
        sb.append(TRIP_COLUMN_TRIP_ID);
        sb.append(" = ");
        sb.append(tripID);

        return db.rawQuery(sb.toString(), null);
    }

    private Cursor getTripByIDWithoutParticipants(long tripID){
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT * FROM ");
        sb.append(TRIP_TABLE_NAME);
        sb.append(" WHERE ");
        sb.append(TRIP_COLUMN_TRIP_ID);
        sb.append(" = ");
        sb.append(tripID);

        return db.rawQuery(sb.toString(), null);
    }

    private Cursor getParticipantsByTripID(long tripID){
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");
        sb.append(PARTICIPANTS_COLUMN_PARTICIPANT_ID);
        sb.append(", ");
        sb.append(PARTICIPANTS_COLUMN_PARTICIPANT_NAME);
        sb.append(" FROM ");
        sb.append(PARTICIPANTS_TABLE_NAME);
        sb.append(" WHERE ");
        sb.append(PARTICIPANTS_COLUMN_TRIP_ID);
        sb.append(" = ");
        sb.append(tripID);

        return db.rawQuery(sb.toString(), null);
    }

    private Cursor getAllTripIDs(){
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");
        sb.append(TRIP_COLUMN_TRIP_ID);
        sb.append(" FROM ");
        sb.append(TRIP_TABLE_NAME);

        return db.rawQuery(sb.toString(), null);
    }

    private Cursor getAllActiveTripIDs(){
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");
        sb.append(TRIP_COLUMN_TRIP_ID);
        sb.append(" FROM ");
        sb.append(TRIP_TABLE_NAME);
        sb.append(" WHERE ");
        sb.append(TRIP_COLUMN_COMPLETE);
        sb.append(" = 0");

        return db.rawQuery(sb.toString(), null);
    }

    private Cursor getAllPastTripIDs(){
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");
        sb.append(TRIP_COLUMN_TRIP_ID);
        sb.append(" FROM ");
        sb.append(TRIP_TABLE_NAME);
        sb.append(" WHERE ");
        sb.append(TRIP_COLUMN_COMPLETE);
        sb.append(" = 1");

        return db.rawQuery(sb.toString(), null);
    }

//    //example of a function that updates the values of a given entry in the database
//    public boolean updateContact (Integer id, String name, String phone, String email, String street,String place)
//    {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("name", name);
//        contentValues.put("phone", phone);
//        contentValues.put("email", email);
//        contentValues.put("street", street);
//        contentValues.put("place", place);
//        db.update("events", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
//        return true;
//    }
//
//
//    //clear all tables. Use for debugging purposes.
//    public void restartDBDebug(){
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("DROP TABLE IF EXISTS " + TRIP_TABLE_NAME);
//        db.execSQL("DROP TABLE IF EXISTS " + PARTICIPANTS_TABLE_NAME);
//    }
}