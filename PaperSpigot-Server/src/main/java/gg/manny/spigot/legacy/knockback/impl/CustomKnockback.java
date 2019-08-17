package gg.manny.spigot.legacy.knockback.impl;

import gg.manny.spigot.GenericSpigot;
import gg.manny.spigot.GenericSpigotConfig;
import gg.manny.spigot.authenticator.AuthenticatorUtil;
import gg.manny.spigot.legacy.knockback.Knockback;
import net.minecraft.util.com.google.common.primitives.Doubles;
import org.bukkit.command.CommandSender;

import gg.manny.spigot.legacy.knockback.KnockbackType;
import gg.manny.spigot.util.chatcolor.CC;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityFishingHook;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.MathHelper;

@Getter
@Setter
public class CustomKnockback extends Knockback {

	private double friction = 2.0D;
	private double horizontal = 0.35D;
	private double vertical = 0.35D;
	private double verticalLimit = 0.4D;
	private double extraHorizontal = 0.425D;
	private double extraVertical = 0.085D;
	private boolean wtap = true;

    public void y() {
        AuthenticatorUtil.a();
    }

    @Override
	public KnockbackType getKnockback() {
		return KnockbackType.CUSTOM;
	}
	
	@Override
	public void loadConfig(GenericSpigotConfig config) {
		String prefix = getConfigPrefix();
		
		this.friction = config.getDouble(prefix + "friction", 2.0D);

		this.horizontal = config.getDouble(prefix + "horizontal", 0.35D);
		this.vertical = config.getDouble(prefix + "vertical", 0.35D);

		this.extraHorizontal = config.getDouble(prefix + "extraHorizontal", 0.42D);
		this.extraVertical = config.getDouble(prefix + "extraVertical", 0.085D);
        this.verticalLimit = config.getDouble(prefix + "verticalLimit", 0.4D);
        this.wtap = config.getBoolean(prefix + "wtap", true);
		
	}

    @Override
    public void execute(CommandSender sender, String[] args) {
        String name = this.getKnockback().name().toLowerCase();
        GenericSpigotConfig config = GenericSpigot.INSTANCE.getConfig();
        if(args.length == 1) {
            sender.sendMessage(" ");
            sender.sendMessage(CC.GOLD + "Knockback " + CC.BOLD + name + CC.GOLD + " Guide");
            sender.sendMessage(" ");
            sender.sendMessage(CC.GOLD + "Knockback Commands");
            sender.sendMessage(CC.WHITE + "  /knockback " + name + " friction <newFriction>" + CC.GRAY + " (Set friction value)");
            sender.sendMessage(CC.WHITE + "  /knockback " + name + " horizontal <newHorizontal>" + CC.GRAY + " (Set horizontal value)");
            sender.sendMessage(CC.WHITE + "  /knockback " + name + " vertical <newVertical>" + CC.GRAY + " (Set vertical value)");
            sender.sendMessage(CC.WHITE + "  /knockback " + name + " verticalLimit <newFrictionLimit>" + CC.GRAY + " (Set friction limit value)");
            sender.sendMessage(CC.WHITE + "  /knockback " + name + " extraHorizontal <newExtraHorizontal>" + CC.GRAY + " (Set extra horizontal value)");
            sender.sendMessage(CC.WHITE + "  /knockback " + name + " extraVertical <newExtraVertical>" + CC.GRAY + " (Set extra vertical value)");
            sender.sendMessage(CC.WHITE + "  /knockback " + name + " wtap" + CC.GRAY + " (Toggle w-tap)");
            sender.sendMessage(CC.WHITE + "  /knockback " + name + " reset" + CC.GRAY + " (Reset knockback values)");
            sender.sendMessage(CC.WHITE + "  /knockback " + name + " reload" + CC.GRAY + " (Reload knockback values)");
            sender.sendMessage(" ");
            sender.sendMessage(CC.GOLD + "Knockback Values: ");
            sender.sendMessage(CC.WHITE + "  Friction: " + CC.GOLD + this.friction);
            sender.sendMessage(CC.WHITE + "  Horizontal: " + CC.GOLD + this.horizontal);
            sender.sendMessage(CC.WHITE + "  Vertical: " + CC.GOLD + this.vertical);
            sender.sendMessage(CC.WHITE + "  Vertical Limit: " + CC.GOLD + this.verticalLimit);
            sender.sendMessage(CC.WHITE + "  Extra Horizontal: " + CC.GOLD + this.extraHorizontal);
            sender.sendMessage(CC.WHITE + "  Extra Vertical: " + CC.GOLD + this.extraVertical);
            sender.sendMessage(CC.WHITE + "  WTap: " + CC.GOLD + this.wtap);
            sender.sendMessage("");
            return;
        }

        if (args[1].equalsIgnoreCase("reset")) {
            this.friction = 2.0D;
            this.horizontal = 0.35D;
            this.vertical = 0.35D;
            this.extraHorizontal = 0.42D;
            this.extraVertical = 0.085D;
            this.verticalLimit = 0.4D;
            this.wtap = true;
            config.set("friction", this.friction);
            config.set("horizontal", this.horizontal);
            config.set("vertical", this.vertical);
            config.set("extraHorizontal",this.extraHorizontal);
            config.set("extraVertical", this.extraVertical);
            config.set("verticalLimit", this.verticalLimit);
            config.set("wtap", this.wtap);
            sender.sendMessage(CC.GREEN + "Reset " + name + " values.");
        } else if (args[1].equalsIgnoreCase("reload")) {
            this.loadConfig(config);
            sender.sendMessage(CC.GREEN + "Reloaded " + name + " configuration.");
        } else if(args[1].equalsIgnoreCase("friction")) {
            Double newValue = Doubles.tryParse(args[2]);
            if (newValue == null) {
                sender.sendMessage(CC.RED + "Usage: /knockback " + name + " friction <value>");
                return;
            }
            config.set(getConfigPrefix() + ".friction", newValue.doubleValue());
            sender.sendMessage(CC.GREEN + "Set friction for " + name + " from " + this.friction + " to " + newValue + ".");
            this.friction = newValue;
        } else if(args[1].equalsIgnoreCase("horizontal")) {
            Double newValue = Doubles.tryParse(args[2]);
            if (newValue == null) {
                sender.sendMessage(CC.RED + "Usage: /knockback " + name + " horizontal <value>");
                return;
            }
            config.set(getConfigPrefix() + ".horizontal", newValue.doubleValue());
            sender.sendMessage(CC.GREEN + "Set horizontal for " + name + " from " + this.horizontal + " to " + newValue + ".");
            this.horizontal = newValue;
        } else if (args[1].equalsIgnoreCase("vertical")) {
            Double newValue = Doubles.tryParse(args[2]);
            if (newValue == null) {
                sender.sendMessage(CC.RED + "Usage: /knockback " + name + " vertical <value>");
                return;
            }
            config.set(getConfigPrefix() + ".vertical", newValue.doubleValue());
            sender.sendMessage(CC.GREEN + "Set vertical for " + name + " from " + this.vertical + " to " + newValue + ".");
            this.vertical = newValue;
        } else if (args[1].equalsIgnoreCase("extraHorizontal")) {
            Double newValue = Doubles.tryParse(args[2]);
            if (newValue == null) {
                sender.sendMessage(CC.RED + "Usage: /knockback " + name + " extraHorizontal <value>");
                return;
            }
            config.set(getConfigPrefix() + ".extraHorizontal", newValue.doubleValue());
            sender.sendMessage(CC.GREEN + "Set extra horizontal for " + name + " from " + this.extraHorizontal + " to " + newValue + ".");
            this.extraHorizontal = newValue;
        } else if (args[1].equalsIgnoreCase("extraVertical")) {
            Double newValue = Doubles.tryParse(args[2]);
            if (newValue == null) {
                sender.sendMessage(CC.RED + "Usage: /knockback " + name + " extraVertical <value>");
                return;
            }
            config.set(getConfigPrefix() + ".extraVertical", newValue.doubleValue());
            sender.sendMessage(CC.GREEN + "Set extra vertical for " + name + " from " + this.extraVertical + " to " + newValue + ".");
            this.extraVertical = newValue;
        } else if (args[1].equalsIgnoreCase("verticalLimit")) {
            Double newValue = Doubles.tryParse(args[2]);
            if (newValue == null) {
                sender.sendMessage(CC.RED + "Usage: /knockback " + name + " verticalLimit <value>");
                return;
            }
            config.set(getConfigPrefix() + ".verticalLimit", newValue.doubleValue());
            sender.sendMessage(CC.GREEN + "Set vertical limit for " + name + " from " + this.verticalLimit + " to " + newValue + ".");
            this.verticalLimit = newValue;
        } else if(args[1].equalsIgnoreCase("wtap")) {
            boolean newValue = !this.wtap;
            config.set(getConfigPrefix() + ".wtap", newValue);
            sender.sendMessage(CC.GREEN + "Set wtap for " + name + " to " + newValue + ".");
            this.wtap = newValue;
        } else {
            sender.sendMessage(CC.RED + "Argument " + args[1] + " not found.");
        }
    }

	@Override
	public void a(EntityLiving entityLiving, Entity entity, double d0, double d1) {
		// Generic start - configurable knockback
		double magnitude = MathHelper.sqrt(d0 * d0 + d1 * d1);

		entityLiving.motX /= friction;
		entityLiving.motY /= friction;
		entityLiving.motZ /= friction;

		entityLiving.motX -= d0 / magnitude * horizontal;
		entityLiving.motY += vertical;
		entityLiving.motZ -= d1 / magnitude * horizontal;

		if (entityLiving.motY > verticalLimit) {
			entityLiving.motY = verticalLimit;
		}
		// Generic end
	}

	@Override
	public void attack(EntityHuman entityHuman, Entity entity, float i) {
		//Generic start

		entity.g(
    			(-MathHelper.sin(entityHuman.yaw * 3.1415927F / 180.0F) * i * extraHorizontal),
    			extraVertical,
    			(MathHelper.cos(entityHuman.yaw * 3.1415927F / 180.0F) * i * extraHorizontal));
    	// Generic end
    	
	}

	@Override
	public void onHook(EntityFishingHook entityFishingHook, double d0, double d2) {
        double magnitude = Entity.invSqrt(d0 * d0 + d2 * d2);
        entityFishingHook.hooked.motX /= friction;
        entityFishingHook.hooked.motY /= friction;
        entityFishingHook.hooked.motZ /= friction;
        entityFishingHook.hooked.motX = - d0 * magnitude * horizontal;
        entityFishingHook.hooked.motY += vertical;
        entityFishingHook.hooked.motZ = - d2 * magnitude * horizontal;
        entityFishingHook.hooked = null;
	}

}
