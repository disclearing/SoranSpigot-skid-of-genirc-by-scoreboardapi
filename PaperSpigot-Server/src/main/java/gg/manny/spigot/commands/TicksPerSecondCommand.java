package gg.manny.spigot.commands;

import gg.manny.spigot.authenticator.AuthenticatorUtil;
import gg.manny.spigot.util.chatcolor.CC;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.spigotmc.ActivationRange;

import java.text.DecimalFormat;

public class TicksPerSecondCommand extends Command  {

	private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public TicksPerSecondCommand(){
        super("tps");

        this.description = "Gets the current ticks per second for the server";
        this.usageMessage = "/tps";
        this.setPermission("spigot.tps");
    }

    public void y() {
        AuthenticatorUtil.a();
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        double[] tps = Bukkit.spigot().getTPS();
        String[] tpsAvg = new String[tps.length];

        for (int i = 0; i < tps.length; i++) {
            tpsAvg[i] = format(tps[i]);
        }

        if (!this.testPermissionSilent(sender)) {
            sender.sendMessage(CC.GOLD + "TPS from last 1m, 5m, 15m: " + CC.GREEN + StringUtils.join(tpsAvg, ", "));
            return true;
        }

        float totalEntities = 0;
        float activeEntities = 0;
        for(World world : Bukkit.getWorlds()) {
            totalEntities += world.getEntities().size();

            for(Entity entity : world.getEntities()) {
                if(ActivationRange.checkIfActive(((CraftEntity)entity).getHandle())) {
                    activeEntities++;
                }
            }
        }

        sender.sendMessage(CC.GOLD + "TPS from last 1m, 5m, 15m: " + CC.GREEN + StringUtils.join(tpsAvg, ", "));
        sender.sendMessage(CC.GOLD + "Full tick: " + CC.GREEN + this.decimalFormat.format((float)MinecraftServer.lastTickTime / 1000000.0D) + "ms");
        sender.sendMessage(CC.GOLD + "Active entities: " + CC.GREEN + (int) activeEntities + "/" + (int) totalEntities + " (" + this.round(100 * (activeEntities / totalEntities)) + "%)");
        sender.sendMessage(CC.GOLD + "Online players: " + CC.GREEN + Bukkit.getOnlinePlayers().size() + '/' + Bukkit.getMaxPlayers());
        return true;
    }

    private static String format(double tps) {
        return (tps >= 18.0 ? ChatColor.GREEN : tps >= 15.0 ? ChatColor.YELLOW : ChatColor.RED).toString() + Math.min(Math.round(tps * 100.0) / 100.0, 20);
    }

    private float round(float value) {
        return (float) (Math.round(value * 100.0) / 100.0);
    }

}
