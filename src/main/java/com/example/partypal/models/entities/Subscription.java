package com.example.partypal.models.entities;

import com.example.partypal.models.entities.base.BaseEntityWithCode;
import lombok.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Min;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
@Entity
@Table(schema = "partypal_event", name = "subscription")
public class Subscription extends BaseEntityWithCode {

    @Column(name = "price")
    @Min(value = 500)
    private Integer price;

    @Column(name = "days")
    private Integer daysCount;

    public enum Code {
        WEEK_5,
        TWO_WEEK_5,
        MONTH_5
    }
}
