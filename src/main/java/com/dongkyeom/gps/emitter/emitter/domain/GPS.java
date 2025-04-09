package com.dongkyeom.gps.emitter.emitter.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GPS {

    /* -------------------------------------------- */
    /* Default Column ----------------------------- */
    /* -------------------------------------------- */
    private final String tripId;


    /* -------------------------------------------- */
    /* Information Column ------------------------- */
    /* -------------------------------------------- */
    private final String agentId;
    private final String latitude;
    private final String longitude;
    private final String timestamp;

    /* -------------------------------------------- */
    /* Methods ------------------------------------ */
    /* -------------------------------------------- */
    @Builder
    public GPS(String tripId, String agentId, String latitude, String longitude, String timestamp) {
        this.tripId = tripId;
        this.agentId = agentId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }
}
