package gg.manny.spigot.events;

import net.minecraft.server.EntityPlayer;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ReachEvent extends Event {
	
    private static final HandlerList handlers = new HandlerList();
    private EntityPlayer player;
    private double distanceSqrt;
    private double distance;

    public ReachEvent(EntityPlayer player, double distanceSqrt) {
        this.player = player;
        this.distanceSqrt = distanceSqrt;
        this.distance = EntityPlayer.invSqrt(distanceSqrt);
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }
    
    public double getDistance() {
    	return distance;
    }
    
    public double getDistanceSqrt() {
    	return distanceSqrt;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}