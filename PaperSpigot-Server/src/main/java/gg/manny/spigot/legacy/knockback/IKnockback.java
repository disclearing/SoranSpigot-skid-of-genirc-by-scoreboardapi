package gg.manny.spigot.legacy.knockback;

import gg.manny.spigot.GenericSpigotConfig;
import org.bukkit.command.CommandSender;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityFishingHook;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;

public interface IKnockback {

	KnockbackType getKnockback();

	void loadConfig(GenericSpigotConfig config);

	void a(EntityLiving entityLiving, Entity entity, double d0, double d1);

	void attack(EntityHuman entityHuman, Entity entity, float i);

	void attackVelocityChange(EntityHuman entityHuman, Entity entity, float i);

	void onHook(EntityFishingHook entityFishingHook, double d0, double d2);

	void execute(CommandSender sender, String[] args);
	
}
