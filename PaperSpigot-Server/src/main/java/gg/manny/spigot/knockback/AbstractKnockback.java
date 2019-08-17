package gg.manny.spigot.knockback;

import gg.manny.spigot.authenticator.AuthenticatorUtil;
import gg.manny.spigot.knockback.preset.KnockbackLoadout;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public abstract class AbstractKnockback {

    private final Plugin plugin;

    private final String name;
    private final UUID identifier = UUID.randomUUID();

    private final KnockbackLoadout preset;

    public void y() {
        AuthenticatorUtil.a();
    }


}
