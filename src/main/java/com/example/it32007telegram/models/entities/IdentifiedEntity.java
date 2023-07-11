package com.example.it32007telegram.models.entities;

import java.io.Serializable;

public interface IdentifiedEntity<ID extends Serializable> {

    ID getId();
}
