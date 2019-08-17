package gg.manny.spigot.knockback.preset.type;

import gg.manny.spigot.authenticator.AuthenticatorUtil;
import gg.manny.spigot.knockback.AbstractKnockback;
import gg.manny.spigot.knockback.preset.KnockbackLoadout;
import org.bukkit.command.CommandSender;

public class DefaultLoadout extends KnockbackLoadout {

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

    public DefaultLoadout(AbstractKnockback instance) {
        super(instance, "Default");
        this.friction = this.getDouble("friction", 0.1D);
        this.horizontal = this.getDouble("horizontal", 0.35D);
        this.vertical = this.getDouble("vertical", 0.35D);
        this.verticalLimit = this.getDouble("verticalLimit", 0.4D);
        this.extraHorizontal = this.getDouble("extraHorizontal", 0.425D);
        this.extraVertical = this.getDouble("extraVertical", 0.085D);
        this.wtap = this.getBoolean("wtap", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        //Usage: /knockback <knockbackName> set <value>
        //Usage /knockback <knockbackName> reset
    }
}
