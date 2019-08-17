package net.minecraft.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spigotmc.AsyncCatcher;
import org.spigotmc.TrackingRange;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EntityTracker {

    private static final Logger a = LogManager.getLogger();
    private Int2ObjectOpenHashMap<EntityTrackerEntry> trackedEntities = new Int2ObjectOpenHashMap<>();
    private List<EntityTrackerEntry> trackedEntitiesList = new ArrayList<EntityTrackerEntry>(){

        @Override
        public boolean add(EntityTrackerEntry entityTrackerEntry) {
            if (super.contains(entityTrackerEntry)) {
                return true;
            }
            return super.add(entityTrackerEntry);
        }
    };

    private int e;
    private static int trackerThreads = 4;
    private static ExecutorService pool = Executors.newFixedThreadPool(trackerThreads - 1, new ThreadFactoryBuilder().setNameFormat("entity-tracker-%d").build());
    private static boolean trackingDisabled = Boolean.getBoolean("disabletracking");

    public EntityTracker(WorldServer worldserver) {
        this.e = worldserver.getMinecraftServer().getPlayerList().d();
    }

    public EntityTrackerEntry getTrackedEntity(int i) {
        return this.trackedEntities.get(i);
    }

    public void track(Entity entity) {
        if (entity instanceof EntityPlayer) {
            this.addEntity(entity, 512, 2);
        } else if (entity instanceof EntityFishingHook) {
            this.addEntity(entity, 64, 5, true);
        } else if (entity instanceof EntityArrow) {
            this.addEntity(entity, 64, 20, false);
        } else if (entity instanceof EntitySmallFireball) {
            this.addEntity(entity, 64, 10, false);
        } else if (entity instanceof EntityFireball) {
            this.addEntity(entity, 64, 10, false);
        } else if (entity instanceof EntitySnowball) {
            this.addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityEnderPearl) {
            this.addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityEnderSignal) {
            this.addEntity(entity, 64, 4, true);
        } else if (entity instanceof EntityEgg) {
            this.addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityPotion) {
            this.addEntity(entity, 64, 2, true);
        } else if (entity instanceof EntityThrownExpBottle) {
            this.addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityFireworks) {
            this.addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityItem) {
            this.addEntity(entity, 64, 20, true);
        } else if (entity instanceof EntityMinecartAbstract) {
            this.addEntity(entity, 80, 3, true);
        } else if (entity instanceof EntityBoat) {
            this.addEntity(entity, 80, 3, true);
        } else if (entity instanceof EntitySquid) {
            this.addEntity(entity, 64, 3, true);
        } else if (entity instanceof EntityWither) {
            this.addEntity(entity, 80, 3, false);
        } else if (entity instanceof EntityBat) {
            this.addEntity(entity, 80, 3, false);
        } else if (entity instanceof IAnimal) {
            this.addEntity(entity, 80, 3, true);
        } else if (entity instanceof EntityEnderDragon) {
            this.addEntity(entity, 160, 3, true);
        } else if (entity instanceof EntityTNTPrimed) {
            this.addEntity(entity, 160, 10, true);
        } else if (entity instanceof EntityFallingBlock) {
            this.addEntity(entity, 160, 20, true);
        } else if (entity instanceof EntityHanging) {
            this.addEntity(entity, 160, Integer.MAX_VALUE, false);
        } else if (entity instanceof EntityExperienceOrb) {
            this.addEntity(entity, 160, 20, true);
        } else if (entity instanceof EntityEnderCrystal) {
            this.addEntity(entity, 256, Integer.MAX_VALUE, false);
        }
    }

    public void addEntity(Entity entity, int i, int j) {
        this.addEntity(entity, i, j, false);
    }

    public void addEntity(Entity entity, int i, int j, boolean alwaysSendVelocity) {
        AsyncCatcher.catchOp("entity track");
        i = TrackingRange.getEntityTrackingRange(entity, i);
        if (i > this.e) {
            i = this.e;
        }
        try {
            if (this.trackedEntities.containsKey(entity.getId())) {
                throw new IllegalStateException("Entity is already tracked!");
            }
            EntityTrackerEntry entitytrackerentry = new EntityTrackerEntry(entity, i, j, alwaysSendVelocity);
            this.trackedEntitiesList.add(entitytrackerentry);
            this.trackedEntities.put(entity.getId(), entitytrackerentry);
        }
        catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Adding entity to track");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity To Track");
            crashreportsystemdetails.a("Tracking range", i + " blocks");
            crashreportsystemdetails.a("Update interval", new CrashReportEntityTrackerUpdateInterval(this, j));
            entity.a(crashreportsystemdetails);
            CrashReportSystemDetails crashreportsystemdetails1 = crashreport.a("Entity That Is Already Tracked");
            this.trackedEntities.get(entity.getId()).tracker.a(crashreportsystemdetails1);
            try {
                throw new ReportedException(crashreport);
            }
            catch (ReportedException reportedexception) {
                a.error("\"Silently\" catching entity tracking error.", reportedexception);
            }
        }
    }

    public void untrackEntity(Entity entity) {
        EntityTrackerEntry entitytrackerentry1;
        AsyncCatcher.catchOp("entity untrack");
        if (entity instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)entity;
            for (EntityTrackerEntry entitytrackerentry : this.trackedEntitiesList) {
                entitytrackerentry.sendDestroyPacketIfTracked(entityplayer);
            }
        }
        if ((entitytrackerentry1 = this.trackedEntities.remove(entity.getId())) != null) {
            this.trackedEntitiesList.remove(entitytrackerentry1);
            entitytrackerentry1.remove();
        }
    }

    public void updatePlayers() { // FIXME: 6/8/2019 This may cause concurrency issues among other things
        if (trackingDisabled) {
            return;
        }
        int offset = 0;
        CountDownLatch latch = new CountDownLatch(trackerThreads);
        int i = 1;
        while (i <= trackerThreads) {
            int localOffset = offset++;
            Runnable runnable = () -> {
                int i1 = localOffset;
                while (i1 < this.trackedEntitiesList.size()) {
                    this.trackedEntitiesList.get(i1).update();
                    i1 += trackerThreads;
                }
                latch.countDown();
            };
            if (i < trackerThreads) {
                pool.execute(runnable);
            } else {
                runnable.run();
            }
            ++i;
        }
        try {
            latch.await();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void a(Entity entity, Packet packet) {
        EntityTrackerEntry entitytrackerentry = this.trackedEntities.get(entity.getId());
        if (entitytrackerentry != null) {
            entitytrackerentry.broadcast(packet);
        }
    }

    public void sendPacketToEntity(Entity entity, Packet packet) {
        EntityTrackerEntry entitytrackerentry = this.trackedEntities.get(entity.getId());
        if (entitytrackerentry != null) {
            entitytrackerentry.broadcastIncludingSelf(packet);
        }
    }

    public void untrackPlayer(EntityPlayer entityplayer) {
        for (EntityTrackerEntry entitytrackerentry : this.trackedEntitiesList) {
            entitytrackerentry.clear(entityplayer);
        }
    }

}
