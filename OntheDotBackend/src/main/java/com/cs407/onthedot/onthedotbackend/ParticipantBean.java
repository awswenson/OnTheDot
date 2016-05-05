package com.cs407.onthedot.onthedotbackend;

/**
 * Created by connerhuff on 5/4/16.
 */
public class ParticipantBean {

    private Long tripId;
    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    private String participantId;
    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    private String participantName;
    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }


}
