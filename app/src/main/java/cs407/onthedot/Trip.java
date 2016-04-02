package cs407.onthedot;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Alex Swenson on 4/2/16.
 */
public class Trip {

    /*
      Stores the latitude/longitude of the meet-up destination.  We can use this to
      pinpoint the location on a Google maps widget, and use it to determine a person's
      distance from the destination.  When we store the destination in the database, we
      will need to store the latitude/longitude separately as two Strings by getting their
      double representations and converting to a String.
     */
    private LatLng destination;

    /*
      Stored in a Date object, this stores the date and time of the meet-up.  When we store
      it in the database, however, we will need to convert the date into a String with the
      format "yyyy-MM-dd HH:mm:ss."  Use the getMeetupTimeAsString() method to accomplish this.
     */
    private Date meetupTime;

    /*
      TODO: Figure out how we want to store the friends that are part of the trip

      This list contains the friends that are part of the trip.  To store the list in the
      database, we use another database table to represent the one-to-many relationship of
      friends to a specific trip.  That is, use another database table where each row
      represents a single friend corresponding to a trip ID.
     */
    private List<String> facebookFriendsIdList;

    public Trip(LatLng destination, Date meetupTime, List<String> facebookFriendsIdList) {
        this.destination = destination;
        this.meetupTime = meetupTime;
        this.facebookFriendsIdList = facebookFriendsIdList;
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

    public String getMeetupTimeAsString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        return dateFormat.format(meetupTime);
    }

    public void setMeetupTime(Date meetupTime) {
        this.meetupTime = meetupTime;
    }

    public List<String> getFacebookFriendsIdList() {
        return facebookFriendsIdList;
    }

    public void setFacebookFriendsIdList(List<String> facebookFriendsIdList) {
        this.facebookFriendsIdList = facebookFriendsIdList;
    }
}
