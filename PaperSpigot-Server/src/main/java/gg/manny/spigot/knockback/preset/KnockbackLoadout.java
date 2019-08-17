package gg.manny.spigot.knockback.preset;

import gg.manny.spigot.GenericSpigot;
import gg.manny.spigot.GenericSpigotConfig;
import gg.manny.spigot.authenticator.AuthenticatorUtil;
import gg.manny.spigot.knockback.AbstractKnockback;
import gg.manny.spigot.util.chatcolor.CC;
import lombok.Data;
import net.minecraft.server.*;
import org.bukkit.command.CommandSender;

@Data
public abstract class KnockbackLoadout {

	private final AbstractKnockback instance;
	private final GenericSpigotConfig config;

	protected final String name;

	private String key;

	public KnockbackLoadout(AbstractKnockback instance, String name) {
	    this.instance = instance;
	    this.name = name;
	    this.config = GenericSpigot.INSTANCE.getConfig();
	    this.key = "knockback." + this.instance.getName() + "." + this.name;
	}

    public void y() {
        AuthenticatorUtil.a();
    }


    public double getDouble(String name, double defaultValue) {
        return config.getDouble(key + "." + name, defaultValue);
    }

    public boolean getBoolean(String name, boolean defaultValue) {
        return config.getBoolean(key + "." + name, defaultValue);
    }

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

	public void attack(EntityHuman entityHuman, Entity entity, float i) {
		entity.g(-MathHelper.sin(entityHuman.yaw * 3.1415927F / 180.0F) * i * 0.5F, 0.1D, MathHelper.cos(entityHuman.yaw * 3.1415927F / 180.0F) * i * 0.5F);
	}

	public void attackVelocityChange(EntityHuman entityHuman, Entity entity, float i) {

	}

	public void execute(CommandSender sender, String[] args) {
		sender.sendMessage(CC.RED + "Knockback " + this.name + " isn't configurable.");
	}

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
