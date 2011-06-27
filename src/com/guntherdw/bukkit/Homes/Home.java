package com.guntherdw.bukkit.Homes;

import com.avaje.ebean.validation.Length;
import com.sun.istack.internal.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author GuntherDW
 */
@Entity
@Table(name="homes")
public class Home {

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

    @Length(max=35)
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /* public Home(double x, double y, double z, float yaw, float pitch, String world) {

       X = x;
       Y = y;
       Z = z;
       Yaw = yaw;     // rotx
       Pitch = pitch; // roty
       World = world;
   } */
    /*
     00204         double rotX = this.getYaw();
     00205         double rotY = this.getPitch();
      */


    public String toString() {
        return "Home{x:"+this.x+",y:"+this.y+",z:"+this.z+",Yaw:"+this.yaw+",Pitch:"+this.pitch+",World:"+this.world+"}";
    }

    public void parseLocation(Location loc) {
        if(loc==null) return;
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
        this.world = loc.getWorld().getName();
    }

    public Location toLocation() {
        return new Location(Bukkit.getServer().getWorld(this.world), this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public void parseHome(SaveHome h) {
        if(h==null) return;
        this.name = h.getName();
        this.x = h.getX();
        this.y = h.getY();
        this.z = h.getZ();
        this.yaw = h.getYaw();
        this.pitch = h.getPitch();
        this.world = h.getWorld();
    }
}
