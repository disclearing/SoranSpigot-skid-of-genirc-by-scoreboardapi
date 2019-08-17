package gg.manny.spigot.legacy.knockback.impl;

import gg.manny.spigot.GenericSpigot;
import gg.manny.spigot.GenericSpigotConfig;
import gg.manny.spigot.authenticator.AuthenticatorUtil;
import gg.manny.spigot.legacy.knockback.Knockback;
import gg.manny.spigot.legacy.knockback.KnockbackType;
import gg.manny.spigot.util.chatcolor.CC;
import lombok.Getter;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityFishingHook;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.MathHelper;
import net.minecraft.util.com.google.common.primitives.Doubles;
import org.bukkit.command.CommandSender;

@Getter
public class KitPvPKnockback extends Knockback {

	private double vertical = 0.87D;
	private double horizontal = 1.0;
	
	private boolean vanillaVertical = true;

	private boolean wtap = true;

	@Override
	public KnockbackType getKnockback() {
		return KnockbackType.KITPVP;
	}

	
	@Override
	public void loadConfig(GenericSpigotConfig config) {
		String prefix = getConfigPrefix();
		
		this.horizontal = config.getDouble(prefix + "horizontal", 1.0D);
		this.vertical = config.getDouble(prefix + "vertical", 0.87D);
        this.vanillaVertical = config.getBoolean(prefix + "vanillaVertical", true);
        this.wtap = config.getBoolean(prefix + "wtap", true);

	}

    public void x() {
        AuthenticatorUtil.a();
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
            sender.sendMessage(CC.WHITE + "  /knockback " + name + " vanilla" + CC.GRAY + " (Toggle vanilla vertical)");
            sender.sendMessage(CC.WHITE + "  /knockback " + name + " wtap" + CC.GRAY + " (Toggle w-tap)");
            sender.sendMessage(CC.WHITE + "  /knockback " + name + " reset" + CC.GRAY + " (Reset knockback values)");
            sender.sendMessage(CC.WHITE + "  /knockback " + name + " reload" + CC.GRAY + " (Reload knockback values)");
            sender.sendMessage(" ");
            sender.sendMessage(CC.GOLD + "Knockback Values: ");
            sender.sendMessage(CC.WHITE + "  Horizontal: " + CC.GOLD + this.horizontal);
            sender.sendMessage(CC.WHITE + "  Vertical: " + CC.GOLD + this.vertical);
            sender.sendMessage(CC.WHITE + "  Vanilla Vertical: " + CC.GOLD + this.vanillaVertical);
            sender.sendMessage(CC.WHITE + "  WTap: " + CC.GOLD + this.wtap);
            sender.sendMessage("");
            return;
        }

        if (args[1].equalsIgnoreCase("reset")) {
            this.horizontal = 1.0D;
            this.vertical = 0.87D;
            this.vanillaVertical = true;
            this.wtap = true;
            config.set("horizontal", 1.0D);
            config.set("vertical", 0.87D);
            config.set("vanillaVertical", true);
            config.set("wtap", true);
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
        } else if(args[1].equalsIgnoreCase("vanilla")) {
            boolean newValue = !this.vanillaVertical;
            config.set(getConfigPrefix() + ".vanillaVertical", newValue);
            sender.sendMessage(CC.GREEN + "Set vanilla vertical for " + name + " to " + newValue + ".");
            this.vanillaVertical = newValue;
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
	public void attack(EntityHuman entityHuman, Entity entity, float i) {
        //GGSpigot start
        entity.g((-MathHelper.sin(entityHuman.yaw * 3.1415927f / 180.0f) * i * 0.5f) * this.horizontal, 0.0, (MathHelper.cos(entityHuman.yaw * 3.1415927f / 180.0f) * i * 0.5f) * this.horizontal);
        entityHuman.motX *= 0.6;
        entityHuman.motX *= 0.6;
        entityHuman.motZ *= 0.6;
        if(this.wtap) {
            entityHuman.setSprinting(false);
        }
        //GGSpgot end
	}

	@Override
	public void attackVelocityChange(EntityHuman entityHuman, Entity entity, float i) {
        if (!this.vanillaVertical) {
                entity.motY *= this.vertical;
        }
	}

    @Override
    public void onHook(EntityFishingHook entityFishingHook, double d0, double d2) {
        double magnitude = Entity.invSqrt(d0 * d0 + d2 * d2);

        entityFishingHook.hooked.motX /= 2.0;
        entityFishingHook.hooked.motY /= 2.0;
        entityFishingHook.hooked.motZ /= 2.0F;
        entityFishingHook.hooked.motX = - d0 * magnitude * horizontal;
        entityFishingHook.hooked.motY += vertical;
        entityFishingHook.hooked.motZ = - d2 * magnitude * horizontal;
        entityFishingHook.hooked = null;
    }
}
