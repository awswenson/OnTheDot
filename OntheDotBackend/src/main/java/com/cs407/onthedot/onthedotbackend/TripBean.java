package com.cs407.onthedot.onthedotbackend;

/** The object model for the data we are sending through endpoints */
public class TripBean {
    private Long id;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    private String date;
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }


    public String getDestLat() {
        return destLat;
    }

    private String destLat;
    public void setDestLat(String destLat) {
        this.destLat = destLat;
    }

    private String destLong;
    public String getDestLong() {
        return destLong;
    }

    public void setDestLong(String destLong) {
        this.destLong = destLong;
    }

    private String startLat;
    public String getStartLat() {
        return startLat;
    }

    public void setStartLat(String startLat) {
        this.startLat = startLat;
    }

    private String startLong;
    public String getStartLong() {
        return startLong;
    }

    public void setStartLong(String startLong) {
        this.startLong = startLong;
    }

    private String tripComplete;
    public String getTripComplete() {
        return tripComplete;
    }

    public void setTripComplete(String tripComplete) {
        this.tripComplete = tripComplete;
    }

    private String friendsList;
    public String getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(String friendsList) {
        this.friendsList = friendsList;
    }
}