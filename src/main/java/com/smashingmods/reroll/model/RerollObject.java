package com.smashingmods.reroll.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.ArrayList;
import java.util.List;

public class RerollObject {
    private List<DimensionObject> dimensionList;

    @JsonCreator
    public RerollObject() {
        dimensionList = new ArrayList<>();
    }

    public void addDimension(int id) {
        dimensionList.add(new DimensionObject(id));
    }

    public DimensionObject getDimensionObjectByID(int id) {
        for (DimensionObject dimension : dimensionList) {
            if (dimension.getId() == id) {
                return dimension;
            }
        }
        return null;
    }

    public boolean containsDimension(int id) {
        if (getDimensionObjectByID(id) != null) {
            return true;
        }
        return false;
    }

    public List<DimensionObject> getDimensionList() {
        return dimensionList;
    }
}



