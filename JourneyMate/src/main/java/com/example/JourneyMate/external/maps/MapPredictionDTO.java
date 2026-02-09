package com.example.JourneyMate.external.maps;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MapPredictionDTO {
    private String description;
    private String placeId;
    private String mainText;
}
