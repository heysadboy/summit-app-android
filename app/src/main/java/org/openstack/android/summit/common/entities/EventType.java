package org.openstack.android.summit.common.entities;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Claudio Redi on 11/4/2015.
 */
public class EventType extends RealmObject implements INamedEntity, ISummitOwned {
    @PrimaryKey
    private int id;
    private String name;
    private String color;
    private Summit summit;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Summit getSummit() {
        return summit;
    }

    @Override
    public void setSummit(Summit summit) {
        this.summit = summit;
    }
}
