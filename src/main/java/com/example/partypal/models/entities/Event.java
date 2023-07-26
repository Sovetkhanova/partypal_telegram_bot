package com.example.partypal.models.entities;

import com.example.partypal.models.entities.base.BaseEntity;
import com.example.partypal.models.entities.base.Category;
import com.example.partypal.models.entities.base.City;
import com.example.partypal.models.entities.base.Country;
import com.example.partypal.models.entities.users.User;
import lombok.*;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Builder
@Table(name = "event", schema = "partypal_event")
@AllArgsConstructor
public class Event extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country")
    @ToString.Exclude
    private Country country;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city")
    private City city;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_user")
    private User createdUser;

    @Column(name = "place")
    private String place;

    @Column(name = "date")
    private Date date;

    @Column(name = "time")
    private Time time;

    @Column(name = "requirements")
    private String requirement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category")
    @ToString.Exclude
    private Category category;

    @Column(name = "tg_id")
    private Long tgId;

}
