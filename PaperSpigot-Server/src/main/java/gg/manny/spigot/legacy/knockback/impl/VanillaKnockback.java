package gg.manny.spigot.legacy.knockback.impl;

import gg.manny.spigot.GenericSpigotConfig;
import gg.manny.spigot.legacy.knockback.Knockback;
import gg.manny.spigot.legacy.knockback.KnockbackType;
import lombok.Getter;

@Getter
public class VanillaKnockback extends Knockback {

	
	@Override
	public void loadConfig(GenericSpigotConfig config) { }
	
	@Override
	public KnockbackType getKnockback() {
		return KnockbackType.VANILLA;
	}

}
