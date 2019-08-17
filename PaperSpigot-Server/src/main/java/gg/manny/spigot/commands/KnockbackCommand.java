package gg.manny.spigot.commands;

import gg.manny.spigot.GenericSpigot;
import gg.manny.spigot.GenericSpigotConfig;
import gg.manny.spigot.authenticator.AuthenticatorUtil;
import gg.manny.spigot.legacy.knockback.Knockback;
import gg.manny.spigot.legacy.knockback.KnockbackType;
import gg.manny.spigot.util.chatcolor.CC;
import net.minecraft.util.com.google.common.primitives.Floats;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public class KnockbackCommand extends Command {

	public KnockbackCommand() {
		super("knockback");
		
		this.setDescription("Knockback alternations for the server.");
		this.setPermission("spigot.knockback");

		this.setAliases(Collections.singletonList("kb"));
	}

    public void y() {
        AuthenticatorUtil.a();
    }


    @Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!this.testPermission(sender)) return true;

		if (args.length == 0) {
		    return this.printHelpMessage(sender);
        }

        GenericSpigotConfig config = GenericSpigot.INSTANCE.getConfig();
        if (args[0].equalsIgnoreCase("set")) {
            if (args.length == 1) {
                return this.printHelpMessage(sender);
            }
            Knockback knockback = config.getKnockbackByName(args[1]);
            if(knockback == null) {
                sender.sendMessage(CC.RED + "Knockback " + args[1] + " not found.");
                return true;
            }
            sender.sendMessage(CC.GREEN + "Set knockback from " + GenericSpigotConfig.activeKnockback.getKnockback().name() + " to " + knockback.getKnockback().name() + " preset.");
            GenericSpigotConfig.activeKnockback = knockback;
            config.saveConfig();

        } else if (args[0].equalsIgnoreCase("potion")) {
            if(args.length < 3) {
                sender.sendMessage(CC.RED + "Usage: /knockback potion <E> <F> <I>");
                return true;
            }

            float e, f, i;
            try {
                e = Floats.tryParse(args[1]);
                f = Floats.tryParse(args[2]);
                i = Floats.tryParse(args[3]);
                GenericSpigotConfig.potionE = e;
                GenericSpigotConfig.potionF = f;
                GenericSpigotConfig.potionI = i;
                sender.sendMessage(CC.GREEN + "Set potion values from " + GenericSpigotConfig.potionE + ", " + GenericSpigotConfig.potionF + ", " + GenericSpigotConfig.potionI + " to " + e + ", " + f + ", " + e + ".");

            } catch (Exception exception) {
                sender.sendMessage(CC.RED + "Usage: /knockback potion <E> <F> <I>");
            }
        } else {
            Knockback knockback = null;
            try {
                knockback = config.getKnockbackByType(KnockbackType.valueOf(args[0].toUpperCase()));
            } catch (Exception exception) {
                sender.sendMessage(CC.RED + "Knockback " + args[0] + " not found.");
            }

            if (knockback != null) {
                knockback.execute(sender, args);
            }
        }

		return true;
	}
	
	public boolean printHelpMessage(CommandSender sender) {
		sender.sendMessage(" ");
		sender.sendMessage(CC.GOLD + "Knockback Guide");
		sender.sendMessage(" ");
		sender.sendMessage(CC.GOLD + "Knockback Commands");
		sender.sendMessage(CC.WHITE + "  /knockback set [knockbackPreset]" + CC.GRAY + " (Set knockback preset)");
		sender.sendMessage(CC.WHITE + "  /knockback potion <e> <f> <i>" + CC.GRAY + " (Set potion values)");
		sender.sendMessage(CC.WHITE + "  /knockback <knockbackPreset>" + CC.GRAY + " (Dependent commands of knockback)");
		sender.sendMessage(" ");
		sender.sendMessage(CC.GOLD + "Knockback Loadout");
		sender.sendMessage(CC.WHITE + "  " + GenericSpigotConfig.activeKnockback.getKnockback().name());
		sender.sendMessage(" ");
        sender.sendMessage(CC.GOLD + "Potion Values" + CC.WHITE + " [E, F, I]");
        sender.sendMessage(CC.WHITE + "  " + GenericSpigotConfig.potionE + ", " + GenericSpigotConfig.potionF + ", " + GenericSpigotConfig.potionI);
		sender.sendMessage(CC.GRAY + "Available Loadouts: " + StringUtils.join(GenericSpigotConfig.knockbacks.stream().map(knockback -> knockback.getKnockback().name()).collect(Collectors.toList()), ", "));
		return true;
	}
	
}
