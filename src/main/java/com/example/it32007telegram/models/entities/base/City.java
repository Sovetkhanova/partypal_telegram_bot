package com.example.it32007telegram.models.entities.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@Entity
@Getter
@Setter
@Builder
@ToString
@RequiredArgsConstructor
@Table(name = "city", schema = "partypal_location")
public class City extends BaseEntityWithCodeAndName {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    @JoinColumn(name = "country_id")
    @ToString.Exclude
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    @JoinColumn(name = "time_zone_id")
    @ToString.Exclude
    private TimeZone timeZone;

}
