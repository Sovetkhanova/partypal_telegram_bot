package com.example.partypal.projectors;

import java.sql.Date;

public interface SubscriptionEventLinkProjector {
    String getCode();

    Date getPromoteUntil();

    String getName();
}
