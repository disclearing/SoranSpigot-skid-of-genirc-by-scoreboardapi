package net.minecraft.server;

// CraftBukkit start
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftEnderPearl;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EnderpearlLandEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.material.Gate;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.github.paperspigot.PaperSpigotConfig;
// CraftBukkit end

public class EntityEnderPearl extends EntityProjectile {

    public EntityEnderPearl(World world) {
        super(world);
        this.loadChunks = world.paperSpigotConfig.loadUnloadedEnderPearls; // PaperSpigot
    }

    public EntityEnderPearl(World world, EntityLiving entityliving) {
        super(world, entityliving);
        this.loadChunks = world.paperSpigotConfig.loadUnloadedEnderPearls; // PaperSpigot
    }

    protected void a(MovingObjectPosition movingobjectposition) {
        if (!PaperSpigotConfig.enderpearlCollidesStringFenceGate) {
            Block block = this.world.getType(movingobjectposition.b, movingobjectposition.c, movingobjectposition.d);

            if (block == Blocks.TRIPWIRE) {
                return;
            } else if (block == Blocks.FENCE_GATE) {
                BlockIterator bi = null;

                try {
                    Vector l = new Vector(this.locX, this.locY, this.locZ);
                    Vector l2 = new Vector(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
                    Vector dir = new Vector(l2.getX() - l.getX(), l2.getY() - l.getY(), l2.getZ() - l.getZ()).normalize();
                    bi = new BlockIterator(this.world.getWorld(), l, dir, 0, 1);
                } catch (IllegalStateException e) {
                    // Do nothing
                }

                if (bi != null) {
                    boolean open = true;
                    while (bi.hasNext()) {
                        org.bukkit.block.Block b = bi.next();
                        if (b.getState().getData() instanceof Gate && !((Gate) b.getState().getData()).isOpen()) {
                            open = false;
                            break;
                        }
                    }
                    if (open) {
                        return;
                    }
                }
            }
        }

        if (movingobjectposition.entity != null) {
            movingobjectposition.entity.damageEntity(DamageSource.projectile(this, this.getShooter()), 0.0F);
        }

        // PaperSpigot start - Remove entities in unloaded chunks
        if (inUnloadedChunk && world.paperSpigotConfig.removeUnloadedEnderPearls) {
            die();
        }
        // PaperSpigot end

        for (int i = 0; i < 32; ++i) {
            this.world.addParticle("portal", this.locX, this.locY + this.random.nextDouble() * 2.0D, this.locZ, this.random.nextGaussian(), 0.0D, this.random.nextGaussian());
        }

        if (!this.world.isStatic) {
            if (this.getShooter() != null && this.getShooter() instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) this.getShooter();

                if (entityplayer.playerConnection.b().isConnected() && entityplayer.world == this.world) {
                    // CraftBukkit start - Fire PlayerTeleportEvent
                    org.bukkit.craftbukkit.entity.CraftPlayer player = entityplayer.getBukkitEntity();

                    EnderpearlLandEvent.Reason reason = movingobjectposition.entity != null ? EnderpearlLandEvent.Reason.ENTITY : EnderpearlLandEvent.Reason.BLOCK;
                    CraftEntity bukkitHitEntity = movingobjectposition.entity != null ? movingobjectposition.entity.getBukkitEntity() : null;
                    EnderpearlLandEvent landEvent = new EnderpearlLandEvent((CraftEnderPearl) getBukkitEntity(), reason, bukkitHitEntity);
                    Bukkit.getPluginManager().callEvent(landEvent);

                    if (landEvent.isCancelled()) {
                        this.die();
                        return;
                    }

                    org.bukkit.Location location = getBukkitEntity().getLocation();
                    location.setPitch(player.getLocation().getPitch());
                    location.setYaw(player.getLocation().getYaw());

                    PlayerTeleportEvent teleEvent = new PlayerTeleportEvent(player, player.getLocation(), location, PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
                    Bukkit.getPluginManager().callEvent(teleEvent);

                    if (!teleEvent.isCancelled() && !entityplayer.playerConnection.isDisconnected()) {
                        if (this.getShooter().am()) {
                            this.getShooter().mount((Entity) null);
                        }

                        entityplayer.playerConnection.teleport(teleEvent.getTo());
                        this.getShooter().fallDistance = 0.0F;
                        CraftEventFactory.entityDamage = this;
                        this.getShooter().damageEntity(DamageSource.FALL, 5.0F);
                        CraftEventFactory.entityDamage = null;
                    }
                    // CraftBukkit end
                }
            }

            this.die();
        }
    }
}
