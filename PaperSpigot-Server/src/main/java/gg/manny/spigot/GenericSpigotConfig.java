package gg.manny.spigot;

import com.google.common.base.Throwables;
import gg.manny.spigot.authenticator.AuthenticatorUtil;
import gg.manny.spigot.legacy.knockback.Knockback;
import gg.manny.spigot.legacy.knockback.KnockbackType;
import gg.manny.spigot.legacy.knockback.impl.*;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

@Data
public class GenericSpigotConfig {

    private static final String HEADER = "This is the main configuration file for the Spigot.\n"
            + "Modify with extreme caution, and make sure you know what you are doing.\n";

    private File configFile;
    private YamlConfiguration config;

    private String privacyKey;

    private boolean hidePlayersFromTab;

	private boolean invalidArmAnimationKick;
	private boolean anticheatEnabled;

	private boolean mobStackingEnabled;
	private int mobStackingMultiplier;

    private boolean disableBlockTick;
    private boolean disableEntityAI;
    private boolean disableEntityCollisions;

    private boolean disableTileEntityTick;
    private boolean disableRecheckGaps;
    private boolean disableTickingWeather;
    private boolean disableBiomeCacheCleanup;
    private boolean disableTickingSleepCheck;
    private boolean disableTickingVillages;
    private boolean disableTickingChunks;
    private boolean disableTickingMaps;

    private boolean disableLoadingNearbyChunks;

    private boolean disableUnloadingChunks;

    private boolean enderpearlTaliban;
    private boolean enderpearlGates;
    private boolean enderpearlTripwire;

    private boolean disableEventPlayerMove;
    private boolean disableEventLeftClickAir;
    private boolean disableEventLeftClickBlock;

    private boolean disableWeather;
	
	public static Knockback activeKnockback = null;
	public static List<Knockback> knockbacks = new ArrayList<>();
	
	public static float potionI = 0.05F;
	public static float potionE = 0.5F;
	public static float potionF = -20.0F;
	
	private String versionMessage;

    public void y() {
        AuthenticatorUtil.a();
    }

    public GenericSpigotConfig() {
        this.configFile = new File("settings.yml");
        this.config = new YamlConfiguration();

        try {
            config.load(this.configFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load settings.yml, please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }

        this.config.options().header(GenericSpigotConfig.HEADER);
        this.config.options().copyDefaults(true);
        this.loadConfig();
    }

    private void loadConfig() {
        this.privacyKey = getString("key", "Secret Key");

        this.disableBlockTick = getBoolean("disable.ticking.blocks", false);

        this.disableEntityAI = getBoolean("disable.entity.ai", true);
        this.disableEntityCollisions = getBoolean("disable.entity.collisions", true);

        this.disableTileEntityTick = getBoolean("disable.ticking.tile-entities", false);
        this.disableRecheckGaps = getBoolean("disable.ticking.recheck-gaps", false);
        this.disableTickingWeather = getBoolean("disable.ticking.weather", true);
        this.disableBiomeCacheCleanup = getBoolean("disable.ticking.biome-cache-cleanup", true);
        this.disableTickingSleepCheck = getBoolean("disable.ticking.sleep-check", true);
        this.disableTickingVillages = getBoolean("disable.ticking.villages", true);
        this.disableTickingChunks = getBoolean("disable.ticking.chunks", false);
        this.disableTickingMaps = getBoolean("disable.ticking.maps", true);

        this.disableWeather = getBoolean("disable.weather", true);

        this.disableUnloadingChunks = getBoolean("disable.unloading-chunks", true);
        this.disableLoadingNearbyChunks = getBoolean("disable.loading-nearby-chunks", true);

        this.disableEventLeftClickAir = getBoolean("disable.event.left-click-air", false);
        this.disableEventLeftClickBlock = getBoolean("disable.event.left-click-block", false);
        this.disableEventPlayerMove = getBoolean("disable.event.player-move", false);

    	this.anticheatEnabled = this.getBoolean("anticheat", true);
	    this.hidePlayersFromTab = this.getBoolean("hide-players-from-tab", true);

	    this.invalidArmAnimationKick = this.getBoolean("invalid-arm-animation-kick", false);

        this.mobStackingEnabled = this.getBoolean("mobStackingEnabled", false);
        this.mobStackingMultiplier = this.getInt("mobStackingMultiplier", 5);

	    this.versionMessage = this.getString("version-message", "This server is running a cool spigot named %serverName% version %serverVersion% using api %apiVersion%.");

        this.enderpearlTaliban = this.getBoolean("enderpearl.taliban", true);
        this.enderpearlGates = this.getBoolean("enderpearl.gates", true);
        this.enderpearlTripwire = this.getBoolean("enderpearl.tripwire", true);

        knockbacks.addAll(Arrays.asList(
                new CustomKnockback(),
                new KitPvPKnockback(),
                new KohiKnockback(),
                new LoungeKnockback(),
                new MinemenClubKnockback(),
                new TheCraftKnockback(),
                new VanillaKnockback(),
                new SimpleKnockback(),
                new RengoKnockback(),
                new ZonixKnockback()
        ));
	    
	    KnockbackType knockbackType = KnockbackType.valueOf(this.getString("knockbackType", KnockbackType.CUSTOM.toString()));
	    activeKnockback = getKnockbackByType(knockbackType);
	    
	    knockbacks.forEach(knockback -> knockback.loadConfig(this));

        potionI = this.getFloat("potionI", 0.05F);
	    potionE = this.getFloat("potionE", 0.5F);
	    potionF = this.getFloat("potionF", -20.0F);
	    
	    try {
		    this.config.save(this.configFile);
	    } catch (IOException ex) {
		    Bukkit.getLogger().log(Level.SEVERE, "Could not save " + this.configFile, ex);
	    }
    }

    public Knockback getKnockbackByType(KnockbackType knockbackType) {
        return knockbacks.stream().filter(knockback -> knockback.getKnockback().equals(knockbackType)).findAny().orElse(null);
    }

    public Knockback getKnockbackByName(String source) {
        return knockbacks.stream().filter(knockback -> knockback.getKnockback().name().equals(source.toUpperCase())).findAny().orElse(null);
    }
    
    public void saveConfig() {
	    try {
		    this.config.save(this.configFile);
	    } catch (IOException ex) {
		    Bukkit.getLogger().log(Level.SEVERE, "Could not save " + this.configFile, ex);
	    }
    }

    public void set(String path, Object val) {
        this.config.set(path, val);

        try {
            this.config.save(this.configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public Set<String> getKeys(String path) {
        if (!this.config.isConfigurationSection(path)) {
            this.config.createSection(path);
            return new HashSet<>();
        }

        return this.config.getConfigurationSection(path).getKeys(false);
    }

    public boolean getBoolean(String path, boolean def) {
        this.config.addDefault(path, def);
        return this.config.getBoolean(path, this.config.getBoolean(path));
    }

    public double getDouble(String path, double def) {
        this.config.addDefault(path, def);
        return this.config.getDouble(path, this.config.getDouble(path));
    }

    public float getFloat(String path, float def) {
        return (float) this.getDouble(path, (double) def);
    }

    public int getInt(String path, int def) {
        this.config.addDefault(path, def);
        return config.getInt(path, this.config.getInt(path));
    }

    public <T> List getList(String path, T def) {
        this.config.addDefault(path, def);
        return this.config.getList(path, this.config.getList(path));
    }

    public String getString(String path, String def) {
        this.config.addDefault(path, def);
        return this.config.getString(path, this.config.getString(path));
    }

}
