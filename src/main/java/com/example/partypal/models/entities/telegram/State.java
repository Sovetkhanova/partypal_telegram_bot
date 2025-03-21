package com.example.partypal.models.entities.telegram;

import com.example.partypal.models.entities.base.BaseEntityWithCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Cacheable
@AllArgsConstructor
@Table(schema = "partypal_tg", name = "state")
public class State extends BaseEntityWithCode {
    public enum StateCode {
        USER_CREATED,
        EVENT_CREATED,
        EVENT_CREATED_NAME_SELECTED,
        EVENT_CREATED_DESCRIPTION_SELECTED,
        EVENT_CREATED_REQUIREMENTS_SELECTED,
        EVENT_CREATED_CATEGORY_SELECTED,
        EVENT_CREATED_CITY_SELECTED,
        EVENT_CREATED_LOCATION_SELECTED,
        EVENT_DATE_SELECT,
        EVENT_TIME_SELECT,
        EVENT_PHOTO_SELECT,
        EVENT_UPDATE,
        EVENT_UPDATE_CITY_SELECT,
        EVENT_UPDATE_CATEGORY_SELECT,
        EVENT_UPDATE_LOCATION_SELECT,
        EVENT_UPDATE_NAME_SELECT,
        EVENT_UPDATE_DESCRIPTION_SELECT,
        EVENT_UPDATE_REQUIREMENTS_SELECT,
        DEFAULT,
    }
}
