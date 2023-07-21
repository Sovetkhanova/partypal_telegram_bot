package com.example.it32007telegram.models.entities.telegram;

import com.example.it32007telegram.models.entities.base.BaseEntityWithCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Cacheable
@AllArgsConstructor
@Table(schema = "partypal_tg", name = "state")
public class State extends BaseEntityWithCode {
    public enum StateCode {
        EVENT_CREATED,
        EVENT_CREATED_NAME_SELECTED,
        EVENT_CREATED_DESCRIPTION_SELECTED,
        EVENT_CREATED_REQUIREMENTS_SELECTED,
        EVENT_CREATED_CATEGORY_SELECTED,
        EVENT_CREATED_CITY_SELECTED,
        EVENT_CREATED_LOCATION_SELECTED,
        EVENT_CREATED_DATE_SELECTED,
        EVENT_CREATED_TIME_SELECTED,
        EVENT_UPDATE,
        EVENT_UPDATE_CITY_SELECT,
        EVENT_UPDATE_CATEGORY_SELECT,
        EVENT_UPDATE_LOCATION_SELECT,
        EVENT_UPDATE_NAME_SELECT,
        EVENT_UPDATE_DESCRIPTION_SELECT,
        EVENT_UPDATE_REQUIREMENTS_SELECT,
        EVENT_UPDATE_DATE_SELECT,
        EVENT_UPDATE_TIME_SELECT,
        EVENT_DELETE,
        MINE_EVENT_SELECTED,
        ENROLLED_EVENT_SELECTED,
        SOME_EVENT_SELECTED,
        REMARK_CREATE,
        ENROLL_CREATE,
        REMARK_DELETE,
    }
}
