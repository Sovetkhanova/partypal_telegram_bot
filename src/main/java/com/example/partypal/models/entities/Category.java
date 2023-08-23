package com.example.partypal.models.entities;


import com.example.partypal.models.entities.base.BaseEntityWithCodeAndName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "category", schema = "partypal_event")
@AllArgsConstructor
@Cacheable
@EqualsAndHashCode(callSuper = true)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Category extends BaseEntityWithCodeAndName {

}
