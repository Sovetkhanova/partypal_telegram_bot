package com.example.partypal.models.entities;

import java.io.Serializable;

public interface IdentifiedEntity<ID extends Serializable> {

    ID getId();
}
