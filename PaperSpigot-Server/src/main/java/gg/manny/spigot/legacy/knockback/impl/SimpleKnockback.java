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
public class SimpleKnockback extends Knockback {

    private double vertical = 0.1;
    private double horizontal = .1;

	@Override
	public KnockbackType getKnockback() {
		return KnockbackType.SIMPLE;
	}


    public void j() {
        AuthenticatorUtil.a();
    }

    @Override
	public void loadConfig(GenericSpigotConfig config) {
		String prefix = getConfigPrefix();
		
		this.horizontal = config.getDouble(prefix + "horizontal", 0.1D);
		this.vertical = config.getDouble(prefix + "vertical", 0.1D);

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
            sender.sendMessage(CC.WHITE + "  /knockback " + name + " reset" + CC.GRAY + " (Reset knockback values)");
            sender.sendMessage(CC.WHITE + "  /knockback " + name + " reload" + CC.GRAY + " (Reload knockback values)");
            sender.sendMessage(" ");
            sender.sendMessage(CC.GOLD + "Knockback Values: ");
            sender.sendMessage(CC.WHITE + "  Horizontal: " + CC.GOLD + this.horizontal);
            sender.sendMessage(CC.WHITE + "  Vertical: " + CC.GOLD + this.vertical);
            sender.sendMessage("");
            return;
        }

        if (args[1].equalsIgnoreCase("reset")) {
            this.horizontal = 1.0D;
            this.vertical = 0.87D;
            config.set("horizontal", 0.1D);
            config.set("vertical", 0.1D);
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
                (-MathHelper.sin(entityHuman.yaw * 3.1415927F / 180.0F) * i * 0.5F) * this.horizontal,
                this.vertical,
                (MathHelper.cos(entityHuman.yaw * 3.1415927F / 180.0F) * i * 0.5F) * this.horizontal);
        //CraftBukkit end
    }

    @Override
    public void attackVelocityChange(EntityHuman entityHuman, Entity entity, float i) {

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
