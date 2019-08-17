package net.minecraft.server;

import gg.manny.spigot.util.WrappedOverflowArray;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;
import org.spigotmc.AsyncCatcher;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

// CraftBukkit start
// CraftBukkit end

public class EntityTrackerEntry {

    public Entity tracker;
    private int trackDistance = 0;
    public int c;
    public int xLoc;
    public int yLoc;
    public int zLoc;
    public int yRot;
    public int xRot;
    public int i;
    public double j;
    public double k;
    public double l;
    private int interval;
    private double q;
    private double r;
    private double s;
    private boolean firstUpdateDone;
    private boolean u;
    private int v;
    private Entity w;
    private boolean x;
    public boolean n;

    private Object2BooleanMap<EntityPlayer> trackedPlayersMap = new Object2BooleanOpenHashMap();
    private Set<EntityPlayer> trackedPlayers = this.trackedPlayersMap.keySet();
    private WrappedOverflowArray<Location> overflowArray = new WrappedOverflowArray<>(Location.class, 100);

    private int addRemoveRate = 20;
    private int addRemoveCooldown = 0;
    private static boolean SEND_ABSOLUTE_POSITION = true;

    public EntityTrackerEntry(Entity entity, int i, int j, boolean flag) {
        this.tracker = entity;
        this.trackDistance = i;
        this.c = j;
        this.u = flag;
        this.xLoc = entity.as.a(entity.locX);
        this.yLoc = MathHelper.floor(entity.locY * 32.0);
        this.zLoc = entity.as.a(entity.locZ);
        this.yRot = MathHelper.d(entity.yaw * 256.0f / 360.0f);
        this.xRot = MathHelper.d(entity.pitch * 256.0f / 360.0f);
        this.i = MathHelper.d(entity.getHeadRotation() * 256.0f / 360.0f);
    }

    public boolean isTracked(EntityPlayer entity) {
        return this.trackedPlayers.contains(entity);
    }

    public boolean equals(Object object) {
        if (!(this == object || object instanceof EntityTrackerEntry && ((EntityTrackerEntry)object).tracker.getId() == this.tracker.getId())) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.tracker.getId();
    }

    public Set<EntityPlayer> getTrackedPlayers() {
        return this.trackedPlayers;
    }

    public void update() {
        if (--this.addRemoveCooldown <= 0) {
            this.removeFarPlayers();
            this.addNearPlayers();
            this.addRemoveCooldown = this.addRemoveRate;
        }
        this.track(null);
    }

    private void removeFarPlayers() {
        ObjectIterator iterator = this.trackedPlayersMap.keySet().iterator();
        while (iterator.hasNext()) {
            EntityPlayer entityPlayer = (EntityPlayer)iterator.next();
            if (entityPlayer == null || this.tracker == null || this.tracker.worldOwner == null) continue;
            if (this.tracker.worldOwner == entityPlayer.worldOwner && (this.trackDistance * this.trackDistance) > entityPlayer.distanceSquared(this.tracker) && entityPlayer.getBukkitEntity().canSeeExtra(this.tracker.getBukkitEntity())) continue;
            entityPlayer.sendDestroyPacket(this.tracker);
            iterator.remove();
        }
    }

    private void addNearPlayers() {
        int chunkMiddleX = MathHelper.floor(this.tracker.locX) >> 4;
        int chunkMiddleZ = MathHelper.floor(this.tracker.locZ) >> 4;
        int chunkTrackRadius = (this.trackDistance >> 4) + 1;
        int x = chunkMiddleX - chunkTrackRadius;
        while (x <= chunkMiddleX + chunkTrackRadius) {
            int z = chunkMiddleZ - chunkTrackRadius;
            while (z <= chunkMiddleZ + chunkTrackRadius) {
                Chunk chunk = this.tracker.world.getChunkIfLoaded(x, z);
                if (chunk != null) {
                    for (EntityPlayer entityPlayer : chunk.getPlayers()) {
                        if (this.tracker == entityPlayer || this.tracker.worldOwner != entityPlayer.worldOwner || this.trackedPlayers.contains(entityPlayer) || entityPlayer.distanceSquared(this.tracker) >= (double)(this.trackDistance * this.trackDistance) || !entityPlayer.getBukkitEntity().canSeeExtra(this.tracker.getBukkitEntity())) continue;
                        this.trackPlayer(entityPlayer);
                    }
                }
                ++z;
            }
            ++x;
        }
    }

    private void track(List<EntityPlayer> list) {
        this.n = false;
        if (!this.firstUpdateDone || this.tracker.distanceSquared(this.q, this.r, this.s) > 16.0) {
            this.q = this.tracker.locX;
            this.r = this.tracker.locY;
            this.s = this.tracker.locZ;
            this.firstUpdateDone = true;
            this.n = true;
        }
        if (this.w != this.tracker.vehicle || this.tracker.vehicle != null && this.interval % 60 == 0) {
            this.w = this.tracker.vehicle;
            this.broadcast(new PacketPlayOutAttachEntity(0, this.tracker, this.tracker.vehicle));
        }
        if (this.tracker instanceof EntityItemFrame) {
            EntityItemFrame entityItemFrame = (EntityItemFrame)this.tracker;
            ItemStack framedItem = entityItemFrame.getItem();
            if (this.interval % 10 == 0 && framedItem != null && framedItem.getItem() instanceof ItemWorldMap) {
                WorldMap worldMap = Items.MAP.getSavedMap(framedItem, this.tracker.world);
                for (EntityPlayer entityPlayer : this.trackedPlayers) {
                    worldMap.a(entityPlayer, framedItem);
                    Packet packetToSend = Items.MAP.c(framedItem, this.tracker.world, entityPlayer);
                    if (packetToSend == null) continue;
                    entityPlayer.playerConnection.sendPacket(packetToSend);
                }
            }
            this.b();
        } else if (this.tracker.al || this.interval % this.c == 0 || this.tracker.getDataWatcher().a()) {
            int i;
            int j;
            if (this.tracker.vehicle == null) {
                double d1;
                double d4;
                double d0;
                double d2;
                double d3;
                ++this.v;
                i = this.tracker.as.a(this.tracker.locX);
                j = MathHelper.floor(this.tracker.locY * 32.0);
                int k = this.tracker.as.a(this.tracker.locZ);
                int l = MathHelper.d(this.tracker.yaw * 256.0f / 360.0f);
                int i1 = MathHelper.d(this.tracker.pitch * 256.0f / 360.0f);
                int j1 = i - this.xLoc;
                int k1 = j - this.yLoc;
                int l1 = k - this.zLoc;
                Packet object = null;
                if (this.interval > 0 || this.tracker instanceof EntityArrow || this.tracker instanceof EntityPotion) {
                    boolean flag1;
                    boolean flag = Math.abs(j1) >= 4 || Math.abs(k1) >= 4 || Math.abs(l1) >= 4;
                    boolean bl = flag1 = Math.abs(l - this.yRot) >= 4 || Math.abs(i1 - this.xRot) >= 4;
                    if (flag) {
                        this.xLoc = i;
                        this.yLoc = j;
                        this.zLoc = k;
                        this.updateLastSentPosition();
                    }
                    if (flag1) {
                        this.yRot = l;
                        this.xRot = i1;
                    }
                    if (j1 >= -128 && j1 < 128 && k1 >= -128 && k1 < 128 && l1 >= -128 && l1 < 128 && this.v <= 400 && !this.x) {
                        if (flag && flag1) {
                            object = new PacketPlayOutRelEntityMoveLook(this.tracker.getId(), (byte)j1, (byte)k1, (byte)l1, (byte)l, (byte)i1, this.tracker.onGround);
                        } else if (flag) {
                            object = this.v % 20 == 0 ? new PacketPlayOutRelEntityMove(this.tracker.getId(), (byte)j1, (byte)k1, (byte)l1, this.tracker.onGround) : new PacketPlayOutEntityTeleport(this.tracker.getId(), i, j, k, (byte)l, (byte)i1, this.tracker.onGround, this.tracker instanceof EntityFallingBlock || this.tracker instanceof EntityTNTPrimed);
                        } else if (flag1) {
                            object = new PacketPlayOutEntityLook(this.tracker.getId(), (byte)l, (byte)i1, this.tracker.onGround);
                        }
                    } else {
                        this.v = 0;
                        object = new PacketPlayOutEntityTeleport(this.tracker.getId(), i, j, k, (byte)l, (byte)i1, this.tracker.onGround, this.tracker instanceof EntityFallingBlock || this.tracker instanceof EntityTNTPrimed);
                    }
                }
                if (this.u && ((d4 = (d0 = this.tracker.motX - this.j) * d0 + (d1 = this.tracker.motY - this.k) * d1 + (d2 = this.tracker.motZ - this.l) * d2) > (d3 = 0.01) * d3 || d4 > 0.0 && this.tracker.motX == 0.0 && this.tracker.motY == 0.0 && this.tracker.motZ == 0.0)) {
                    this.j = this.tracker.motX;
                    this.k = this.tracker.motY;
                    this.l = this.tracker.motZ;
                    this.broadcast(new PacketPlayOutEntityVelocity(this.tracker.getId(), this.j, this.k, this.l));
                }
                if (object != null) {
                    if (object instanceof PacketPlayOutEntityTeleport) {
                        this.broadcast(object);
                    } else {
                        PacketPlayOutEntityTeleport teleportPacket = null;
                        for (Object2BooleanMap.Entry viewer : this.trackedPlayersMap.object2BooleanEntrySet()) {
                            if (viewer.getBooleanValue() == SEND_ABSOLUTE_POSITION) {
                                viewer.setValue(!SEND_ABSOLUTE_POSITION);
                                if (teleportPacket == null) {
                                    teleportPacket = new PacketPlayOutEntityTeleport(this.tracker.getId(), i, j, k, (byte)l, (byte)i1, this.tracker.onGround, this.tracker instanceof EntityFallingBlock || this.tracker instanceof EntityTNTPrimed);
                                }
                                ((EntityPlayer)viewer.getKey()).playerConnection.sendPacket(teleportPacket);
                                continue;
                            }
                            ((EntityPlayer)viewer.getKey()).playerConnection.sendPacket(object);
                        }
                    }
                }
                this.b();
                this.x = false;
            } else {
                boolean flag2;
                i = MathHelper.d(this.tracker.yaw * 256.0f / 360.0f);
                j = MathHelper.d(this.tracker.pitch * 256.0f / 360.0f);
                boolean bl = flag2 = Math.abs(i - this.yRot) >= 4 || Math.abs(j - this.xRot) >= 4;
                if (flag2) {
                    this.broadcast(new PacketPlayOutEntityLook(this.tracker.getId(), (byte)i, (byte)j, this.tracker.onGround));
                    this.yRot = i;
                    this.xRot = j;
                }
                this.xLoc = this.tracker.as.a(this.tracker.locX);
                this.yLoc = MathHelper.floor(this.tracker.locY * 32.0);
                this.zLoc = this.tracker.as.a(this.tracker.locZ);
                this.b();
                this.x = true;
            }
            i = MathHelper.d(this.tracker.getHeadRotation() * 256.0f / 360.0f);
            if (Math.abs(i - this.i) >= 4 && this.tracker instanceof EntityLiving) {
                this.broadcast(new PacketPlayOutEntityHeadRotation(this.tracker, (byte)i));
                this.i = i;
            }
            this.tracker.al = false;
        }
        ++this.interval;
        if (this.tracker.velocityChanged) {
            boolean cancelled = false;
            boolean isPlayer = false;
            if (this.tracker instanceof EntityPlayer) {
                isPlayer = true;
                Player player = (Player) (this.tracker.getBukkitEntity());
                Vector velocity = player.getVelocity();
                PlayerVelocityEvent event = new PlayerVelocityEvent(player, velocity);
                this.tracker.world.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    cancelled = true;
                } else if (!velocity.equals(event.getVelocity())) {
                    player.setVelocity(velocity);
                }
            }
            if (!cancelled) {
                if (isPlayer) {
                    this.sendToSelf(new PacketPlayOutEntityVelocity(this.tracker));
                } else {
                    this.broadcast(new PacketPlayOutEntityVelocity(this.tracker));
                }
            }
            this.tracker.velocityChanged = false;
        }
    }

    private void sendToSelf(Packet packet) {
        if (this.tracker instanceof EntityPlayer) {
            ((EntityPlayer)this.tracker).playerConnection.sendPacket(packet);
        }
    }

    private void b() {
        DataWatcher datawatcher = this.tracker.getDataWatcher();
        if (datawatcher.a()) {
            this.broadcastIncludingSelf(new PacketPlayOutEntityMetadata(this.tracker.getId(), datawatcher, false));
        }

        if (this.tracker instanceof EntityPlayer) {
            AttributeMapServer attributemapserver = (AttributeMapServer) ((EntityLiving) this.tracker).getAttributeMap();
            Set set = attributemapserver.getAttributes();
            if (!set.isEmpty()) {
                if (this.tracker instanceof EntityPlayer) {
                    ((EntityPlayer) this.tracker).getBukkitEntity().injectScaledMaxHealth(set, false);
                }
                ((EntityPlayer) this.tracker).playerConnection.sendPacket(new PacketPlayOutUpdateAttributes(this.tracker.getId(), set));
                set.clear();
            }
        }
    }

    public void broadcast(Packet packet) {
        Iterator<EntityPlayer> iterator = this.trackedPlayers.iterator();
        while (iterator.hasNext()) {
            iterator.next().playerConnection.sendPacket(packet);
        }
    }

    public void broadcastIncludingSelf(Packet packet) {
        this.broadcast(packet);
        if (this.tracker instanceof EntityPlayer) {
            ((EntityPlayer)this.tracker).playerConnection.sendPacket(packet);
        }
    }

    public void remove() {
        Iterator<EntityPlayer> iterator = this.trackedPlayers.iterator();
        while (iterator.hasNext()) {
            iterator.next().sendDestroyPacket(this.tracker);
        }
    }

    public void sendDestroyPacket(EntityPlayer entityplayer) {
        entityplayer.sendDestroyPacket(this.tracker);
    }

    public void a(EntityPlayer ep) {
        this.sendDestroyPacketIfTracked(ep);
    }

    public void sendDestroyPacketIfTracked(EntityPlayer entityplayer) {
        if (this.trackedPlayers.remove(entityplayer)) {
            entityplayer.sendDestroyPacket(this.tracker);
        }
    }

    public void scanPlayers(List players) {
        players.forEach(s -> this.trackPlayer((EntityPlayer)s));
    }

    private void trackPlayer(EntityPlayer entityplayer) {
        AsyncCatcher.catchOp("player tracker update");
        if (entityplayer != this.tracker) {
            double d0 = entityplayer.locX - this.tracker.locX;
            double d1 = entityplayer.locZ - this.tracker.locZ;
            if (d0 >= (double)(- this.trackDistance) && d0 <= (double)this.trackDistance && d1 >= (double)(- this.trackDistance) && d1 <= (double)this.trackDistance) {
                if (!this.trackedPlayers.contains(entityplayer) && (this.d(entityplayer) || this.tracker.attachedToPlayer)) {
                    EntityHuman entityhuman;
                    if (!entityplayer.getBukkitEntity().canSeeExtra(this.tracker.getBukkitEntity())) {
                        return;
                    }
                    this.trackedPlayersMap.put(entityplayer, SEND_ABSOLUTE_POSITION);
                    Packet packet = this.getSpawnPacket();
                    if (this.tracker instanceof EntityPlayer) {
                        entityplayer.playerConnection.sendPacket(PacketPlayOutPlayerInfo.addPlayer((EntityPlayer)this.tracker));
                        if (!entityplayer.getName().equals(entityplayer.listName) && entityplayer.playerConnection.networkManager.getVersion() > 28) {
                            entityplayer.playerConnection.sendPacket(PacketPlayOutPlayerInfo.updateDisplayName((EntityPlayer)this.tracker));
                        }
                    }
                    entityplayer.playerConnection.sendPacket(packet);
                    if (!this.tracker.getDataWatcher().d()) {
                        entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityMetadata(this.tracker.getId(), this.tracker.getDataWatcher(), true));
                    }
                    if (this.tracker instanceof EntityPlayer) {
                        AttributeMapServer attributemapserver = (AttributeMapServer)((EntityLiving)this.tracker).getAttributeMap();
                        Collection collection = attributemapserver.c();
                        if (this.tracker.getId() == entityplayer.getId()) {
                            ((EntityPlayer)this.tracker).getBukkitEntity().injectScaledMaxHealth(collection, false);
                        }
                        if (!collection.isEmpty()) {
                            entityplayer.playerConnection.sendPacket(new PacketPlayOutUpdateAttributes(this.tracker.getId(), collection));
                        }
                    }
                    this.j = this.tracker.motX;
                    this.k = this.tracker.motY;
                    this.l = this.tracker.motZ;
                    if (this.u && !(packet instanceof PacketPlayOutSpawnEntityLiving)) {
                        entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityVelocity(this.tracker.getId(), this.tracker.motX, this.tracker.motY, this.tracker.motZ));
                    }
                    if (this.tracker.vehicle != null) {
                        entityplayer.playerConnection.sendPacket(new PacketPlayOutAttachEntity(0, this.tracker, this.tracker.vehicle));
                    }
                    if (this.tracker.passenger != null) {
                        entityplayer.playerConnection.sendPacket(new PacketPlayOutAttachEntity(0, this.tracker.passenger, this.tracker));
                    }
                    if (this.tracker instanceof EntityInsentient && ((EntityInsentient)this.tracker).getLeashHolder() != null) {
                        entityplayer.playerConnection.sendPacket(new PacketPlayOutAttachEntity(1, this.tracker, ((EntityInsentient)this.tracker).getLeashHolder()));
                    }
                    if (this.tracker instanceof EntityLiving) {
                        int i = 0;
                        while (i < 5) {
                            ItemStack itemstack = ((EntityLiving)this.tracker).getEquipment(i);
                            if (itemstack != null) {
                                entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityEquipment(this.tracker.getId(), i, itemstack));
                            }
                            ++i;
                        }
                    }
                    if (this.tracker instanceof EntityHuman && (entityhuman = (EntityHuman)this.tracker).isSleeping()) {
                        entityplayer.playerConnection.sendPacket(new PacketPlayOutBed(entityhuman, MathHelper.floor(this.tracker.locX), MathHelper.floor(this.tracker.locY), MathHelper.floor(this.tracker.locZ)));
                    }
                    if (this.tracker instanceof EntityLiving) {
                        this.i = MathHelper.d(this.tracker.getHeadRotation() * 256.0f / 360.0f);
                        entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityHeadRotation(this.tracker, (byte)this.i));
                    }
                    if (this.tracker instanceof EntityLiving) {
                        Iterator<MobEffect> iterator = ((EntityLiving)this.tracker).getEffects().iterator();
                        while (iterator.hasNext()) {
                            entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityEffect(this.tracker.getId(), iterator.next()));
                        }
                    }
                }
            } else if (this.trackedPlayers.remove(entityplayer)) {
                entityplayer.sendDestroyPacket(this.tracker);
            }
        }
    }

    private boolean d(EntityPlayer entityplayer) {
        return entityplayer.r().getPlayerChunkMap().a(entityplayer, this.tracker.ah, this.tracker.aj);
    }

    private Packet getSpawnPacket() {
        if (this.tracker.dead) {
            return null;
        }
        if (this.tracker instanceof EntityItem) {
            return new PacketPlayOutSpawnEntity(this.tracker, 2, 1);
        }
        if (this.tracker instanceof EntityPlayer) {
            return new PacketPlayOutNamedEntitySpawn((EntityHuman)this.tracker);
        }
        if (this.tracker instanceof EntityMinecartAbstract) {
            EntityMinecartAbstract entityminecartabstract = (EntityMinecartAbstract)this.tracker;
            return new PacketPlayOutSpawnEntity(this.tracker, 10, entityminecartabstract.m());
        }
        if (this.tracker instanceof EntityBoat) {
            return new PacketPlayOutSpawnEntity(this.tracker, 1);
        }
        if (!(this.tracker instanceof IAnimal) && !(this.tracker instanceof EntityEnderDragon)) {
            if (this.tracker instanceof EntityFishingHook) {
                EntityHuman entityhuman = ((EntityFishingHook)this.tracker).owner;
                return new PacketPlayOutSpawnEntity(this.tracker, 90, entityhuman != null ? entityhuman.getId() : this.tracker.getId());
            }
            if (this.tracker instanceof EntityArrow) {
                Entity entity = ((EntityArrow)this.tracker).shooter;
                return new PacketPlayOutSpawnEntity(this.tracker, 60, entity != null ? entity.getId() : this.tracker.getId());
            }
            if (this.tracker instanceof EntitySnowball) {
                return new PacketPlayOutSpawnEntity(this.tracker, 61);
            }
            if (this.tracker instanceof EntityPotion) {
                return new PacketPlayOutSpawnEntity(this.tracker, 73, ((EntityPotion)this.tracker).getPotionValue());
            }
            if (this.tracker instanceof EntityThrownExpBottle) {
                return new PacketPlayOutSpawnEntity(this.tracker, 75);
            }
            if (this.tracker instanceof EntityEnderPearl) {
                return new PacketPlayOutSpawnEntity(this.tracker, 65);
            }
            if (this.tracker instanceof EntityEnderSignal) {
                return new PacketPlayOutSpawnEntity(this.tracker, 72);
            }
            if (this.tracker instanceof EntityFireworks) {
                return new PacketPlayOutSpawnEntity(this.tracker, 76);
            }
            if (this.tracker instanceof EntityFireball) {
                EntityFireball entityfireball = (EntityFireball)this.tracker;
                PacketPlayOutSpawnEntity packetplayoutspawnentity ;
                byte b0 = 63;
                if (this.tracker instanceof EntitySmallFireball) {
                    b0 = 64;
                }
                else if (this.tracker instanceof EntityWitherSkull) {
                    b0 = 66;
                }
                if (entityfireball.shooter != null) {
                    packetplayoutspawnentity = new PacketPlayOutSpawnEntity(this.tracker, b0, ((EntityFireball)this.tracker).shooter.getId());
                }
                else {
                    packetplayoutspawnentity = new PacketPlayOutSpawnEntity(this.tracker, b0, 0);
                }
                packetplayoutspawnentity.d((int)(entityfireball.dirX * 8000.0));
                packetplayoutspawnentity.e((int)(entityfireball.dirY * 8000.0));
                packetplayoutspawnentity.f((int)(entityfireball.dirZ * 8000.0));
                return packetplayoutspawnentity;
            }
            if (this.tracker instanceof EntityEgg) {
                return new PacketPlayOutSpawnEntity(this.tracker, 62);
            }
            if (this.tracker instanceof EntityTNTPrimed) {
                return new PacketPlayOutSpawnEntity(this.tracker, 50);
            }
            if (this.tracker instanceof EntityEnderCrystal) {
                return new PacketPlayOutSpawnEntity(this.tracker, 51);
            }
            if (this.tracker instanceof EntityFallingBlock) {
                EntityFallingBlock entityfallingblock = (EntityFallingBlock)this.tracker;
                return new PacketPlayOutSpawnEntity(this.tracker, 70, Block.getId(entityfallingblock.f()) | entityfallingblock.data << 16);
            }
            if (this.tracker instanceof EntityPainting) {
                return new PacketPlayOutSpawnEntityPainting((EntityPainting)this.tracker);
            }
            if (this.tracker instanceof EntityItemFrame) {
                EntityItemFrame entityitemframe = (EntityItemFrame)this.tracker;
                PacketPlayOutSpawnEntity packetplayoutspawnentity = new PacketPlayOutSpawnEntity(this.tracker, 71, entityitemframe.direction);
                packetplayoutspawnentity.a(MathHelper.d(entityitemframe.x * 32));
                packetplayoutspawnentity.b(MathHelper.d(entityitemframe.y * 32));
                packetplayoutspawnentity.c(MathHelper.d(entityitemframe.z * 32));
                return packetplayoutspawnentity;
            }
            if (this.tracker instanceof EntityLeash) {
                EntityLeash entityleash = (EntityLeash)this.tracker;
                PacketPlayOutSpawnEntity packetplayoutspawnentity = new PacketPlayOutSpawnEntity(this.tracker, 77);
                packetplayoutspawnentity.a(MathHelper.d(entityleash.x * 32));
                packetplayoutspawnentity.b(MathHelper.d(entityleash.y * 32));
                packetplayoutspawnentity.c(MathHelper.d(entityleash.z * 32));
                return packetplayoutspawnentity;
            }
            if (this.tracker instanceof EntityExperienceOrb) {
                return new PacketPlayOutSpawnEntityExperienceOrb((EntityExperienceOrb)this.tracker);
            }
            throw new IllegalArgumentException("Don't know how to add " + this.tracker.getClass() + "!");
        }
        this.i = MathHelper.d(this.tracker.getHeadRotation() * 256.0f / 360.0f);
        return new PacketPlayOutSpawnEntityLiving((EntityLiving)this.tracker);
    }

    public void clear(EntityPlayer entityplayer) {
        AsyncCatcher.catchOp("player tracker clear");
        if (this.trackedPlayers.remove(entityplayer)) {
            entityplayer.sendDestroyPacket(this.tracker);
        }
    }

    private void updateLastSentPosition() {
        Location lastSentLocation = new Location(this.tracker.getBukkitEntity().getWorld(), this.tracker.locX, this.tracker.lastY, this.tracker.locZ);
        this.overflowArray.add(lastSentLocation);
    }

}

