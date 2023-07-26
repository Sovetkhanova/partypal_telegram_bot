package com.example.partypal.models.entities.users;

import com.example.partypal.models.entities.UserEventLink;
import com.example.partypal.models.entities.base.BaseEntity;
import com.example.partypal.models.entities.base.City;
import com.example.partypal.models.entities.telegram.State;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Builder
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "user_account", schema = "partypal_user")
@AllArgsConstructor
public class User extends BaseEntity {

    @JoinColumn(name = "first_name")
    private String firstName;

    @JoinColumn(name = "last_name")
    private String lastName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_status_id")
    @ToString.Exclude
    private UserStatus userStatus;

    @JoinColumn(name = "date_created")
    private LocalDate dateCreated;

    @JoinColumn(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_city_id")
    @ToString.Exclude
    private City city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_id")
    @ToString.Exclude
    private Gender gender;

    @Column(name = "last_login_date_time")
    private LocalDateTime lastLoginDateTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_state")
    @ToString.Exclude
    private State current_state;

    private String lang;

    @Column(name = "telegram_id")
    private Long telegramId;

    @Column(name = "telegram_username")
    private String telegramUsername;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserEventLink> userEventLinks = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
