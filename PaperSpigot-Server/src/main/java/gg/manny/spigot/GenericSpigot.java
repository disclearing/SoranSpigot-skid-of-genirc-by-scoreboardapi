package gg.manny.spigot;

import gg.manny.spigot.authenticator.AuthenticatorUtil;
import gg.manny.spigot.commands.KnockbackCommand;
import gg.manny.spigot.handler.MovementHandler;
import gg.manny.spigot.handler.PacketHandler;
import gg.manny.spigot.handler.SimpleMovementHandler;
import gg.manny.spigot.knockback.AbstractKnockback;
import lombok.Getter;
import net.minecraft.server.MinecraftServer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;

public enum GenericSpigot {

    INSTANCE;

    private GenericSpigotConfig config;

    @Getter
    private Set<PacketHandler> packetHandlers = new HashSet<>();

    @Getter
    private Set<MovementHandler> movementHandlers = new HashSet<>();

    @Getter
    private Set<SimpleMovementHandler> simpleMovementHandlers = new HashSet<>();

    @Getter
    private Set<AbstractKnockback> knockbacks = new HashSet<>();

    public GenericSpigotConfig getConfig() {
        return this.config;
    }

    public void setConfig(GenericSpigotConfig config) {
        this.config = config;
    }

    public void registerKnockback(AbstractKnockback knockback) {
        this.knockbacks.add(knockback);

        MinecraftServer.getServer().server.getCommandMap().register(knockback.getName(), "knockback", new Command(knockback.getName(), "", "", new ArrayList<>()) {
            @Override
            public boolean execute(CommandSender sender, String label, String[] args) {

                return true;
            }
        });
    }

    public void y() {
        AuthenticatorUtil.a();
    }


    public void addPacketHandler(PacketHandler handler) {
		this.packetHandlers.add(handler);
	}

    public void addMovementHandler(MovementHandler handler) {
        this.movementHandlers.add(handler);
    }

    public void addMovementHandler(SimpleMovementHandler handler) {
        this.simpleMovementHandlers.add(handler);
    }

	public void registerCommands() {
		Map<String, Command> commands = new HashMap<>();
        commands.put("knockback", new KnockbackCommand());
        //commands.put("worldstats", new WorldStatsCommand());
        //commands.put("tps2", new TPS2Command());

		for (Map.Entry<String, Command> entry : commands.entrySet()) {
			MinecraftServer.getServer().server.getCommandMap().register(entry.getKey(), "Spigot", entry.getValue());
		}
	}
    
}
