package com.example.it32007telegram.models.entities;

import com.example.it32007telegram.models.entities.base.BaseEntity;
import com.example.it32007telegram.models.entities.users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Builder
@Table(name = "event_user_reference", schema = "partypal_event")
@AllArgsConstructor
public class UserEventLink extends BaseEntity {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;
}
