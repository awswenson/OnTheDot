package com.cs407.onthedot.onthedotbackend;

/** The object model for the data we are sending through endpoints */
public class TripBean {

    private Long id;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    private String date;
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    private Double destLat;
    public Double getDestLat() {
        return destLat;
    }
    public void setDestLat(Double destLat) {
        this.destLat = destLat;
    }

    private Double destLong;
    public Double getDestLong() {
        return destLong;
    }
    public void setDestLong(Double destLong) {
        this.destLong = destLong;
    }

    private Double startLat;
    public Double getStartLat() {
        return startLat;
    }
    public void setStartLat(Double startLat) {
        this.startLat = startLat;
    }

    private Double startLong;
    public Double getStartLong() {
        return startLong;
    }
    public void setStartLong(Double startLong) {
        this.startLong = startLong;
    }

    private Boolean tripComplete;
    public Boolean getTripComplete() {
        return tripComplete;
    }
    public void setTripComplete(Boolean tripComplete) {
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