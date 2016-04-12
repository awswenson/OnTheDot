package cs407.onthedot;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Alex Swenson on 4/2/16.
 */
public class Trip implements Parcelable {

    /*
      This is the ID that is assigned by the database to refer to this trip object.  DO NOT
      explicitly set the tripID expect for with the tripID that is returned by the database.
     */
    private long tripID;

    /*
      Stores the latitude/longitude of the meet-up destination.  We can use this to
      pinpoint the location on a Google maps widget, and use it to determine a person's
      distance from the destination.
     */
    private LatLng destination;

    /*
      Stored in a Date object, this stores the date and time of the meet-up.
     */
    private Date meetupTime;

    /*
      This list contains the friends that are part of the trip.  To store the list in the
      database, we use another database table to represent the one-to-many relationship of
      friends to a specific trip.  That is, use another database table where each row
      represents a single friend corresponding to a trip ID.
     */
    private ArrayList<Friend> attendingFBFriendsList;

    /*
      A boolean variable to identify if the trip is complete (true) or is in
      progress/upcoming (false).
     */
    private boolean tripComplete;

    public Trip(LatLng destination, Date meetupTime, ArrayList<Friend> attendingFBFriendsList,
                boolean tripComplete) {
        this.destination = destination;
        this.meetupTime = meetupTime;
        this.attendingFBFriendsList = attendingFBFriendsList;
        this.tripComplete = tripComplete;
    }

    /*
      Only use this when the tripID is known (i.e. been assigned by the database)
     */
    public Trip(long tripID, LatLng destination, Date meetupTime, ArrayList<Friend> attendingFBFriendsList,
                boolean tripComplete) {
        this.tripID = tripID;
        this.destination = destination;
        this.meetupTime = meetupTime;
        this.attendingFBFriendsList = attendingFBFriendsList;
        this.tripComplete = tripComplete;
    }

    public Trip(Parcel in) {
        this.tripID = in.readLong();
        this.destination = in.readParcelable(LatLng.class.getClassLoader());
        this.meetupTime = ((Date) in.readSerializable());
        this.attendingFBFriendsList = new ArrayList<Friend>();
        in.readTypedList(this.attendingFBFriendsList, Friend.CREATOR);
        this.tripComplete = (in.readInt() == 1);
    }

    public long getTripID() {
        return tripID;
    }

    public void setTripID(long tripID) {
        this.tripID = tripID;
    }

    public LatLng getDestination() {
        return destination;
    }

    public double getDestinationLatitude() {
        return destination.latitude;
    }

    public double getDestinationLongitude() {
        return destination.longitude;
    }

    public void setDestination(LatLng destination) {
        this.destination = destination;
    }

    public Date getMeetupTime() {
        return meetupTime;
    }

    public void setMeetupTime(Date meetupTime) {
        this.meetupTime = meetupTime;
    }

    public ArrayList<Friend> getAttendingFBFriendsList() {
        return attendingFBFriendsList;
    }

    public void setAttendingFBFriendsList(ArrayList<Friend> attendingFBFriendsList) {
        this.attendingFBFriendsList = attendingFBFriendsList;
    }

    public boolean isTripComplete() {
        return tripComplete;
    }

    public void setTripComplete(boolean tripComplete) {
        this.tripComplete = tripComplete;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(tripID);
        dest.writeParcelable(destination, flags);
        dest.writeSerializable(meetupTime);
        dest.writeTypedList(attendingFBFriendsList);
        dest.writeInt(tripComplete ? 1 : 0);
    }

    public static final Parcelable.Creator<Trip> CREATOR = new Parcelable.Creator<Trip>() {

        public Trip createFromParcel(Parcel in){
            return new Trip(in);
        }
        public Trip[] newArray(int size){
            return new Trip[size];
        }
    };
}
