package com.emiliomanco.vestigia.entity;

import com.geckolib.animatable.GeoAnimatable;

public interface PosedHumanoid extends GeoAnimatable {

    default boolean armsDriven() {
        return false;
    }

    default boolean bodyDriven() {
        return false;
    }
}
