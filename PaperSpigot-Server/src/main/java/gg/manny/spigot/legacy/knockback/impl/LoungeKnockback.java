package gg.manny.spigot.legacy.knockback.impl;

import gg.manny.spigot.GenericSpigot;
import gg.manny.spigot.GenericSpigotConfig;
import gg.manny.spigot.authenticator.AuthenticatorUtil;
import gg.manny.spigot.legacy.knockback.Knockback;
import gg.manny.spigot.legacy.knockback.KnockbackType;
import gg.manny.spigot.util.chatcolor.CC;
import lombok.Getter;
import net.minecraft.server.*;
import net.minecraft.util.com.google.common.primitives.Doubles;
import org.bukkit.command.CommandSender;

@Getter
public class LoungeKnockback extends Knockback {

	private double vertical = 0.89D;
	private double horizontal = 0.92D;

	@Override
	public KnockbackType getKnockback() {
		return KnockbackType.LOUNGE;
	}


	public void l() {
		AuthenticatorUtil.a();
	}

	@Override
	public void loadConfig(GenericSpigotConfig config) {
		String prefix = getConfigPrefix();
		
		this.horizontal = config.getDouble(prefix + "horizontal", 0.9D);
		this.vertical = config.getDouble(prefix + "vertical", 0.86D);
		
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
			sender.sendMessage(CC.WHITE + "  /knockback " + name + " horizontal <newHorizontal>" + CC.GRAY + " (Set horizontal value)");
            sender.sendMessage(CC.WHITE + "  /knockback " + name + " vertical <newVertical>" + CC.GRAY + " (Set vertical value)");
            sender.sendMessage(CC.WHITE + "  /knockback " + name + " applyingSprint <newApplyingSprinty>" + CC.GRAY + " (Set applying sprint value)");

			sender.sendMessage(" ");
			sender.sendMessage(CC.GOLD + "Knockback Values: ");
			sender.sendMessage(CC.WHITE + "  Horizontal: " + CC.GOLD + this.horizontal);
            sender.sendMessage(CC.WHITE + "  Vertical: " + CC.GOLD + this.vertical);

			sender.sendMessage("");
			return;
		}

		if (args[1].equalsIgnoreCase("reset")) {
            this.vertical = 0.89D;
            this.horizontal = 0.92D;
			config.set("horizontal", this.horizontal);
            config.set("vertical", this.vertical);

			sender.sendMessage(CC.GREEN + "Reset " + name + " values.");
		} else if (args[1].equalsIgnoreCase("reload")) {
			this.loadConfig(config);
			sender.sendMessage(CC.GREEN + "Reloaded " + name + " configuration.");
		} else if(args[1].equalsIgnoreCase("horizontal")) {
			Double newValue = Doubles.tryParse(args[2]);
			if (args.length == 2 || newValue == null) {
				sender.sendMessage(CC.RED + "Usage: /knockback " + name + " horizontal <value>");
				return;
			}
			config.set(getConfigPrefix() + ".horizontal", newValue.doubleValue());
			sender.sendMessage(CC.GREEN + "Set horizontal for " + name + " from " + this.horizontal + " to " + newValue + ".");
			this.horizontal = newValue;
		} else if (args[1].equalsIgnoreCase("vertical")) {
			Double newValue = Doubles.tryParse(args[2]);
			if (args.length == 2 || newValue == null) {
				sender.sendMessage(CC.RED + "Usage: /knockback " + name + " vertical <value>");
				return;
			}
			config.set(getConfigPrefix() + ".vertical", newValue.doubleValue());
			sender.sendMessage(CC.GREEN + "Set vertical for " + name + " from " + this.vertical + " to " + newValue + ".");
			this.vertical = newValue;
        } else {
            sender.sendMessage(CC.RED + "Argument " + args[1] + " not found.");
        }
	}
	@Override
	public void attack(EntityHuman entityHuman, Entity entity, float i) {
		//Zonix start
        if (entityHuman.isApplyingSprintKnockback()) {
            entity.g((double) (
                    -MathHelper.sin(entityHuman.yaw * 3.1415927F / 180.0F) * (float) i * 0.5F),
                    0.1D,
                    (double) (MathHelper.cos(entityHuman.yaw * 3.1415927F / 180.0F) * (float) i * 0.5F));

            entityHuman.setApplyingSprintKnockback(false);
        }
    	//Zonix end
	}

    @Override
    public void a(EntityLiving entityLiving, Entity entity, double d0, double d1) {
        // Kohi start - configurable knockback
        float f1 = MathHelper.sqrt(d0 * d0 + d1 * d1);
        float f2 = 0.4F;
        float f3 = 0.4F;

            /*
            this.motX /= 2.0D;
            this.motY /= 1.5D;
            this.motZ /= 2.0D;
            */
        double knockbackReductionHorizontal = 1.0D - this.horizontal;
        double knockbackReductionVertical = 1.0D - this.vertical;

        double frictionHorizontal = 2.0D - knockbackReductionHorizontal;
        double frictionVertical = (2.0D - knockbackReductionVertical) - 0.25D;

        f2 *= (1.0D - knockbackReductionHorizontal);
        f3 *= (1.0D - knockbackReductionVertical);

        entityLiving.motX /= frictionHorizontal;
        entityLiving.motY /= frictionVertical;
        entityLiving.motZ /= frictionHorizontal;
        entityLiving.motX -= d0 / (double) f1 * (double) f2;
        entityLiving.motY += (double) f3;
        entityLiving.motZ -= d1 / (double) f1 * (double) f2;

        if (entityLiving.motY > 0.4000000059604645D) {
            entityLiving.motY = 0.4000000059604645D;
        }
    }

	@Override
	public void onHook(EntityFishingHook entityFishingHook, double d0, double d2) {
		double magnitude = Entity.invSqrt(d0 * d0 + d2 * d2);
		double knockbackReductionHorizontal = 1.0D - this.horizontal;
		double knockbackReductionVertical = 1.0D - this.vertical;

		double frictionHorizontal = 2.0D - knockbackReductionHorizontal;
		double frictionVertical = (2.0D - knockbackReductionVertical) - 0.25D;

		entityFishingHook.hooked.motX /= frictionHorizontal;
		entityFishingHook.hooked.motY /= frictionVertical;
		entityFishingHook.hooked.motZ /= frictionHorizontal;
		entityFishingHook.hooked.motX = - d0 * magnitude * horizontal;
		entityFishingHook.hooked.motY += vertical;
		entityFishingHook.hooked.motZ = - d2 * magnitude * horizontal;
		entityFishingHook.hooked = null;
	}

}
