package net.minecraft.server;

public class WorldManager implements IWorldAccess {

    private MinecraftServer server;
    public WorldServer world;

    public WorldManager(MinecraftServer minecraftserver, WorldServer worldserver) {
        this.server = minecraftserver;
        this.world = worldserver;
    }

    @Override
    public void a(String s, double d0, double d1, double d2, double d3, double d4, double d5) {
    }

    @Override
    public void a(Entity entity) {
        this.world.getTracker().track(entity);
    }

    @Override
    public void b(Entity entity) {
        this.world.getTracker().untrackEntity(entity);
    }

    public void a(Entity entity, String s, double d0, double d1, double d2, float f, float f1) {
        this.server.getPlayerList().sendPacketNearbyIncludingSelf(entity, d0, d1, d2, f > 1.0f ? (double)(16.0f * f) : 16.0, this.world.dimension, new PacketPlayOutNamedSoundEffect(s, d0, d1, d2, f, f1));
    }

    @Override
    public void a(String s, double d0, double d1, double d2, float f, float f1) {
        this.server.getPlayerList().sendPacketNearby(d0, d1, d2, f > 1.0f ? (double)(16.0f * f) : 16.0, this.world.dimension, new PacketPlayOutNamedSoundEffect(s, d0, d1, d2, f, f1));
    }

    @Override
    public void a(EntityHuman entityhuman, String s, double d0, double d1, double d2, float f, float f1) {
        this.server.getPlayerList().sendPacketNearby(entityhuman, d0, d1, d2, f > 1.0f ? (double)(16.0f * f) : 16.0, this.world.dimension, new PacketPlayOutNamedSoundEffect(s, d0, d1, d2, f, f1));
    }

    @Override
    public void a(int i, int j, int k, int l, int i1, int j1) {
    }

    @Override
    public void a(int i, int j, int k) {
        this.world.getPlayerChunkMap().flagDirty(i, j, k);
    }

    @Override
    public void b(int i, int j, int k) {
    }

    @Override
    public void a(String s, int i, int j, int k) {
    }

    @Override
    public void a(EntityHuman entityhuman, int i, int j, int k, int l, int i1) {
        this.server.getPlayerList().sendPacketNearby(entityhuman, (double)j, (double)k, (double)l, 64.0, this.world.dimension, (Packet)new PacketPlayOutWorldEvent(i, j, k, l, i1, false));
    }

    @Override
    public void a(int i, int j, int k, int l, int i1) {
        this.server.getPlayerList().sendAll(new PacketPlayOutWorldEvent(i, j, k, l, i1, true));
    }

    @Override
    public void b(int i, int j, int k, int l, int i1) {
        for (EntityPlayer entityplayer : this.world.findNearbyPlayers(j, k, l, 32.0)) {
            if (entityplayer == null || entityplayer.world != this.world || entityplayer.getId() == i) continue;
            entityplayer.playerConnection.sendPacket(new PacketPlayOutBlockBreakAnimation(i, j, k, l, i1));
        }
    }

    public void a(Entity entity, int i, int j, int k, int l, int i1) {
        this.server.getPlayerList().sendPacketNearbyIncludingSelf(entity, j, k, l, 64.0, this.world.dimension, new PacketPlayOutWorldEvent(i, j, k, l, i1, false));
    }

    @Override
    public void b() {
    }
}
