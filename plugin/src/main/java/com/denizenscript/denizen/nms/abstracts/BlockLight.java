package com.denizenscript.denizen.nms.abstracts;

import com.denizenscript.denizen.nms.NMSHandler;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BlockLight {

    protected static final Map<Location, BlockLight> lightsByLocation = new HashMap<>();
    protected static final Map<Chunk, List<BlockLight>> lightsByChunk = new HashMap<>();
    protected static final BlockFace[] adjacentFaces = new BlockFace[] {
            BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN
    };

    public final Block block;
    public final Chunk chunk;
    public final int originalLight;
    public int currentLight;
    public int cachedLight;
    public int intendedLevel;
    public BukkitTask removeTask;
    public BukkitTask updateTask;

    protected BlockLight(Location location, long ticks) {
        this.block = location.getBlock();
        this.chunk = location.getChunk();
        this.originalLight = block.getLightFromBlocks();
        this.currentLight = originalLight;
        this.cachedLight = originalLight;
        this.intendedLevel = originalLight;
        this.removeLater(ticks);
    }

    public void removeLater(long ticks) {
        if (ticks > 0) {
            this.removeTask = new BukkitRunnable() {
                @Override
                public void run() {
                    removeTask = null;
                    removeLight(block.getLocation());
                }
            }.runTaskLater(NMSHandler.getJavaPlugin(), ticks);
        }
    }

    public static void removeLight(Location location) {
        location = location.getBlock().getLocation();
        BlockLight blockLight = lightsByLocation.get(location);
        if (blockLight != null) {
            if (blockLight.updateTask != null) {
                blockLight.updateTask.cancel();
                blockLight.updateTask = null;
            }
            blockLight.reset(true);
            if (blockLight.removeTask != null) {
                blockLight.removeTask.cancel();
                blockLight.removeTask = null;
            }
            lightsByLocation.remove(location);
            List<BlockLight> lights = lightsByChunk.get(blockLight.chunk);
            lights.remove(blockLight);
            if (lights.isEmpty()) {
                lightsByChunk.remove(blockLight.chunk);
            }
        }
    }

    public void reset(boolean updateChunk) {
        this.update(originalLight, updateChunk);
    }

    public abstract void update(int lightLevel, boolean updateChunk);
}
