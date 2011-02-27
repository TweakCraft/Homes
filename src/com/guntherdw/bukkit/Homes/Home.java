package com.guntherdw.bukkit.Homes;

/**
 * @author GuntherDW
 */
public class Home {
    private double X,Y,Z;
    private float Pitch,Yaw;
    private String World;

    public String getWorld() {
        return World;
    }

    public void setWorld(String world) {
        World = world;
    }

    public double getX() {
        return X;
    }

    public void setX(double x) {
        X = x;
    }

    public double getY() {
        return Y;
    }

    public void setY(double y) {
        Y = y;
    }

    public double getZ() {
        return Z;
    }

    public void setZ(double z) {
        Z = z;
    }

    public float getPitch() {
        return Pitch;
    }

    public void setPitch(float pitch) {
        Pitch = pitch;
    }

    public float getYaw() {
        return Yaw;
    }

    public void setYaw(float yaw) {
        Yaw = yaw;
    }

    public Home(double x, double y, double z, float pitch, float yaw, String world) {

        X = x;
        Y = y;
        Z = z;
        Pitch = pitch;
        Yaw = yaw;
        World = world;
    }
}
