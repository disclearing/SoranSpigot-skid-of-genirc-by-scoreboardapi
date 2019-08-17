package gg.manny.spigot.legacy.knockback;

import gg.manny.spigot.authenticator.AuthenticatorUtil;
import gg.manny.spigot.util.chatcolor.CC;
import org.bukkit.command.CommandSender;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityFishingHook;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.MathHelper;

@Setter
@Getter
@Deprecated
public abstract class Knockback implements IKnockback {

	public String getConfigPrefix() {
		return "knockback." + this.getKnockback().toString() + ".";
	}

	public void y() {
		AuthenticatorUtil.a();
	}

	@Override
	public void a(EntityLiving entityLiving, Entity entity, double d0, double d1) {
        float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1);
        float f3 = 0.4f;
        entityLiving.motX /= 2.0;
        entityLiving.motY /= 2.0;
        entityLiving.motZ /= 2.0;
        entityLiving.motX -= d0 / f2 * f3;
        entityLiving.motY += f3;
        entityLiving.motZ -= d1 / f2 * f3;
        if (entityLiving.motY > 0.4000000059604645) {
            entityLiving.motY = 0.4000000059604645;
        }
	}
	
	@Override
	public void attack(EntityHuman entityHuman, Entity entity, float i) {
		//CraftBukkit start
		entity.g(
				-MathHelper.sin(entityHuman.yaw * 3.1415927F / 180.0F) * (float) i * 0.5F, 
				0.1D, 
				MathHelper.cos(entityHuman.yaw * 3.1415927F / 180.0F) * (float) i * 0.5F);
    	//CraftBukkit end
	}
	
	@Override
	public void attackVelocityChange(EntityHuman entityHuman, Entity entity, float i) {
		
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		sender.sendMessage(CC.RED + "Knockback " + this.getKnockback().name() + " isn't configurable.");
	}
	
	@Override
	public void onHook(EntityFishingHook entityFishingHook, double d0, double d2) {
        double magnitude = Entity.invSqrt(d0 * d0 + d2 * d2);
        entityFishingHook.hooked.motX /= 2;
        entityFishingHook.hooked.motY /= 2;
        entityFishingHook.hooked.motZ /= 2;
        entityFishingHook.hooked.motX = - d0 * magnitude * .35;
        entityFishingHook.hooked.motY += .35;
        entityFishingHook.hooked.motZ = - d2 * magnitude * .35;
        entityFishingHook.hooked = null;
	}
	
}
