package com.dongkyeom.gps.emitter.emitter.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EmitGpsRequestDto(
        @JsonProperty("destination")
        String destination
) {
}
