package com.example.it32007telegram.models.entities.telegram;

import com.example.it32007telegram.models.entities.base.BaseEntityWithCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Cacheable
@Table(schema = "partypal_tg", name = "state")
public class State extends BaseEntityWithCode {
    public enum Code {
        EVENT_CREATE,
        EVENT_CREATE_CITY_SELECT,
        EVENT_CREATE_CATEGORY_SELECT,
        EVENT_CREATE_LOCATION_SELECT,
        EVENT_CREATE_NAME_SELECT,
        EVENT_CREATE_DESCRIPTION_SELECT,
        EVENT_CREATE_REQUIREMENTS_SELECT,
        EVENT_CREATE_DATE_SELECT,
        EVENT_CREATE_TIME_SELECT,
        EVENT_UPDATE,
        EVENT_UPDATE_CITY_SELECT,
        EVENT_UPDATE_CATEGORY_SELECT,
        EVENT_UPDATE_LOCATION_SELECT,
        EVENT_UPDATE_NAME_SELECT,
        EVENT_UPDATE_DESCRIPTION_SELECT,
        EVENT_UPDATE_REQUIREMENTS_SELECT,
        EVENT_UPDATE_DATE_SELECT,
        EVENT_UPDATE_TIME_SELECT,
        EVENT_SELECT,
        REMARK_CREATE,
        ENROLL_CREATE,
        REMARK_DELETE,
    }
}
