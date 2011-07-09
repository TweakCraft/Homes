package com.guntherdw.bukkit.Homes;

import com.avaje.ebean.validation.Length;
import com.sun.istack.internal.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import javax.persistence.*;

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

    @NotNull
    @Length(max=20)
    private String world;

    @NotNull
    @Length(max=35)
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Home() {
        
    }

    public Home(String playername, Location location) {
        if(location==null) return;
        if(playername==null) return;
        this.name = playername;
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        this.world = location.getWorld().getName();
    }

    public String toString() {
        return "Home{name:"+name+",x:"+this.x+",y:"+this.y+",z:"+this.z+",Yaw:"+this.yaw+",Pitch:"+this.pitch+",World:"+this.world+"}";
    }

    public void setLocation(Location location) {
        if(location==null) return;
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        this.world = location.getWorld().getName();
    }

    public Location getLocation() {
        return new Location(Bukkit.getServer().getWorld(this.world), this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public void setSaveHome(SaveHome saveHome) {
        if(saveHome==null) return;
        this.name = saveHome.getName();
        this.x = saveHome.getX();
        this.y = saveHome.getY();
        this.z = saveHome.getZ();
        this.yaw = saveHome.getYaw();
        this.pitch = saveHome.getPitch();
        this.world = saveHome.getWorld();
    }

    public Home(String name, double x, double y, double z, float yaw, float pitch, String world) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;     // rotx
        this.pitch = pitch; // roty
        this.world = world;
    }

    public Home(int id, String name, double x, double y, double z, float yaw, float pitch, String world) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;     // rotx
        this.pitch = pitch; // roty
        this.world = world;
    }
}
