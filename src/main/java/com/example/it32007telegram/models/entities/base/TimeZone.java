package com.example.it32007telegram.models.entities.base;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@AllArgsConstructor
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "time_zone", schema = "partypal_location")
public class TimeZone extends BaseEntityWithCodeAndName {

    @JoinColumn(name = "utc_offset")
    private String UTC;
}
