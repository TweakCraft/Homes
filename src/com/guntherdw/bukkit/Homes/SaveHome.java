package com.guntherdw.bukkit.Homes;

import com.avaje.ebean.validation.Length;
import com.sun.istack.internal.NotNull;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author GuntherDW
 */
@Entity
@Table(name="savehomes")
public class SaveHome {
    @Id
    private int id;

    @NotNull
    private double x;
    @NotNull
    private double y;

    @NotNull
    private double z;

    @NotNull
    private float pitch;

    @NotNull
    private float yaw;

    @Length(max=20)
    private String world;

    @Length(max=30)
    private String name;

    @Length(max=30)
    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public String toString() {
        return "SaveHome{owner:"+this.name+",description:"+this.description+"x:"+this.x+",y:"+this.y+",z:"+this.z+",Yaw:"+this.yaw+",Pitch:"+this.pitch+",World:"+this.world+"}";
    }
}
