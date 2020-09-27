package com.denizenscript.denizen.scripts.commands.world;

import com.denizenscript.denizen.objects.CuboidTag;
import com.denizenscript.denizen.utilities.DenizenAPI;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsException;
import com.denizenscript.denizencore.objects.Argument;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CopyBlockCommand extends AbstractCommand {

    public CopyBlockCommand() {
        setName("copyblock");
        setSyntax("copyblock [<location>] [to:<location>] (remove_original)");
        setRequiredArguments(2, 3);
        isProcedural = false;
    }

    // <--[command]
    // @Name CopyBlock
    // @Syntax copyblock [<location>/<cuboid>] (origin:<location>) [to:<location>] (remove_original) (delayed)
    // @Required 2
    // @Short Copies a block to another location, keeping metadata when possible.
    // @Group world
    //
    // @Description
    // Copies a block or cuboid to another location.
    // If you use the "delayed" argument, the cuboid will be copied at a pace roughly matched to the server's limits.
    // If you specify an "origin" location, that location will be treated as the reference block to use while copying the cuboid.
    // The origin block does not need to be a block that's being copied!
    // If the "origin" argument is excluded, then the minimum location of the first member of the cuboid will be used as the origin.
    // You may also use the 'remove_original' argument to delete the original block.
    // This effectively moves the block to the target location.
    //
    // @Tags
    // <LocationTag.material>
    //
    // @Usage
    // Use to copy the block the player is looking at to their current location
    // - copyblock <player.cursor_on> to:<player.location>
    //
    // @Usage
    // Use to move the block the player is looking at to their current location (removing it from its original location)
    // - copyblock <player.cursor_on> to:<player.location> remove_original
    // -->

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {
        for (Argument arg : scriptEntry.getProcessedArgs()) {

            if (!scriptEntry.hasObject("source")
                    && arg.matchesArgumentType(CuboidTag.class)) {
                scriptEntry.addObject("source", arg.asType(CuboidTag.class));
            }
            else if (!scriptEntry.hasObject("source")
                    && arg.matchesArgumentType(LocationTag.class)
                    && !arg.matchesPrefix("t", "to") && !arg.matchesPrefix("origin")) {
                scriptEntry.addObject("source", arg.asType(LocationTag.class));
            }
            else if (!scriptEntry.hasObject("origin")
                    && arg.matchesArgumentType(LocationTag.class)
                    && arg.matchesPrefix("origin")) {
                scriptEntry.addObject("origin", arg.asType(LocationTag.class));
            }
            else if (!scriptEntry.hasObject("destination")
                    && arg.matchesArgumentType(LocationTag.class)
                    && arg.matchesPrefix("t", "to")) {
                scriptEntry.addObject("destination", arg.asType(LocationTag.class));
            }
            else if (!scriptEntry.hasObject("remove")
                    && arg.matches("remove_original")) {
                scriptEntry.addObject("remove", new ElementTag(true));
            }
            else if (!scriptEntry.hasObject("delayed")
                    && arg.matches("delayed")) {
                scriptEntry.addObject("delayed", new ElementTag(true));
            }
            else {
                arg.reportUnhandled();
            }
        }

        // Check required arguments
        if (!scriptEntry.hasObject("source")) {
            throw new InvalidArgumentsException("Must specify a source location or cuboid.");
        }
        if (!scriptEntry.hasObject("destination")) {
            throw new InvalidArgumentsException("Must specify a destination location.");
        }
        // Set defaults
        if (scriptEntry.getObject("source") instanceof CuboidTag) {
            scriptEntry.defaultObject("origin", new LocationTag(((CuboidTag) scriptEntry.getObject("source")).pairs.get(0).low));
        }
        else {
            scriptEntry.defaultObject("origin", ((LocationTag) scriptEntry.getObject("source")).clone());
        }
        scriptEntry.defaultObject("remove", new ElementTag(false));
        scriptEntry.defaultObject("delayed", new ElementTag(false));
    }

    @Override
    public void execute(ScriptEntry scriptEntry) {

        ObjectTag sourceObject = (ObjectTag) scriptEntry.getObject("source");
        LocationTag destination = (LocationTag) scriptEntry.getObject("destination");
        LocationTag originEntry = (LocationTag) scriptEntry.getObject("origin");
        ElementTag removeOriginal = (ElementTag) scriptEntry.getObject("remove");
        ElementTag delayElement = (ElementTag) scriptEntry.getObject("delayed");

        if (scriptEntry.dbCallShouldDebug()) {
            Debug.report(scriptEntry, getName(), sourceObject.debug() + originEntry.debug()
                    + destination.debug() + removeOriginal.debug() + delayElement.debug());
        }

        boolean remove = removeOriginal.asBoolean();
        boolean delay = delayElement.asBoolean();

        if (sourceObject instanceof CuboidTag) {
            CuboidTag sourceCuboid = (CuboidTag) sourceObject;
            Set<Block> locationCache = new HashSet<>();

            if (delay) {
                new BukkitRunnable() {
                    List<CuboidTag.LocationPair> pairList = new ArrayList<>(sourceCuboid.pairs);
                    int lastX = 0;
                    int lastY = 0;
                    int lastZ = 0;

                    @Override
                    public void run() {
                        long startTime = System.currentTimeMillis();
                        List<CuboidTag.LocationPair> doneCuboids = new ArrayList<>();

                        full_loop:
                        for (CuboidTag.LocationPair pair : pairList) {
                            int xOffset = pair.low.getBlockX() - originEntry.getBlockX();
                            int yOffset = pair.low.getBlockY() - originEntry.getBlockY();
                            int zOffset = pair.low.getBlockZ() - originEntry.getBlockZ();
                            int xDist = pair.high.getBlockX() - pair.low.getBlockX();
                            int yDist = pair.high.getBlockY() - pair.low.getBlockY();
                            int zDist = pair.high.getBlockZ() - pair.low.getBlockZ();

                            for (int x = lastX; x <= xDist; x++) {
                                for (int y = lastY; y <= yDist; y++) {
                                    if (pair.low.getBlockY() + y + yOffset < 0 || pair.low.getBlockY() + y + yOffset > 255) {
                                        continue;
                                    }

                                    for (int z = lastZ; z <= zDist; z++) {
                                        if (System.currentTimeMillis() - startTime > 50) {
                                            lastX = x;
                                            lastY = y;
                                            lastZ = z;
                                            break full_loop;
                                        }

                                        Block source = pair.low.clone().add(x, y, z).getBlock();
                                        Block updateDest = destination.clone().add(xOffset + x, yOffset + y, zOffset + z).getBlock();

                                        if (locationCache.contains(updateDest)) {
                                            continue;
                                        }

                                        replaceBlock(source, updateDest, remove);
                                        locationCache.add(updateDest);
                                    }
                                }
                            }

                            lastX = 0;
                            lastY = 0;
                            lastZ = 0;
                            doneCuboids.add(pair);
                        }

                        pairList.removeAll(doneCuboids);
                        if (pairList.isEmpty()) {
                            cancel();
                        }
                    }
                }.runTaskTimer(DenizenAPI.getCurrentInstance(), 1, 1);
            }
            else {
                for (CuboidTag.LocationPair pair : sourceCuboid.pairs) {
                    int xOffset = pair.low.getBlockX() - originEntry.getBlockX();
                    int yOffset = pair.low.getBlockY() - originEntry.getBlockY();
                    int zOffset = pair.low.getBlockZ() - originEntry.getBlockZ();
                    int xDist = pair.high.getBlockX() - pair.low.getBlockX();
                    int yDist = pair.high.getBlockY() - pair.low.getBlockY();
                    int zDist = pair.high.getBlockZ() - pair.low.getBlockZ();

                    for (int x = 0; x <= xDist; x++) {
                        for (int y = 0; y <= yDist; y++) {
                            if (pair.low.getBlockY() + y < 0 || pair.low.getBlockY() + y > 255) {
                                continue;
                            }

                            for (int z = 0; z <= zDist; z++) {
                                Block source = pair.low.clone().add(x, y, z).getBlock();
                                Block updateDest = destination.clone().add(xOffset + x, yOffset + y, zOffset + z).getBlock();

                                if (updateDest.getY() < 0 || updateDest.getY() > 255) {
                                    break;
                                }
                                if (locationCache.contains(updateDest)) {
                                    continue;
                                }

                                replaceBlock(source, updateDest, remove);
                                locationCache.add(updateDest);
                            }
                        }
                    }
                }
            }
        }
        else if (sourceObject instanceof LocationTag) {
            Block source = ((LocationTag) sourceObject).getBlock();
            Block update = destination.getBlock();

            replaceBlock(source, update.getLocation().clone().getBlock(), remove);
        }
    }

    private void replaceBlock(Block origin, Block destination, boolean removeOrigin) {
        BlockState originState = origin.getState();
        BlockData originData = NMSHandler.getBlockHelper().getBlockData(origin);
        originData.setBlock(destination, false);
        BlockState destState = destination.getState();

        if (originState instanceof InventoryHolder) {
            ((InventoryHolder) destState).getInventory()
                    .setContents(((InventoryHolder) originState).getInventory().getContents());
        }
        else if (originState instanceof Sign) {
            Sign destSign = (Sign) destState;
            String[] origSignLines = ((Sign) originState).getLines();
            for (int i = 0; i < origSignLines.length; i++) {
                destSign.setLine(i, origSignLines[i]);
            }
        }
        else if (originState instanceof NoteBlock) {
            ((NoteBlock) destState).setNote(((NoteBlock) originState).getNote());
        }
        else if (originState instanceof Skull) {
            ((Skull) destState).setSkullType(((Skull) originState).getSkullType());
            ((Skull) destState).setOwner(((Skull) originState).getOwner());
            ((Skull) destState).setRotation(((Skull) originState).getRotation());
        }
        else if (originState instanceof Jukebox) {
            ((Jukebox) destState).setPlaying(((Jukebox) originState).getPlaying());
        }
        else if (originState instanceof Banner) {
            ((Banner) destState).setBaseColor(((Banner) originState).getBaseColor());
            ((Banner) destState).setPatterns(((Banner) originState).getPatterns());
        }
        else if (originState instanceof CommandBlock) {
            ((CommandBlock) destState).setName(((CommandBlock) originState).getName());
            ((CommandBlock) destState).setCommand(((CommandBlock) originState).getCommand());
        }
        else if (originState instanceof CreatureSpawner) {
            ((CreatureSpawner) destState).setSpawnedType(((CreatureSpawner) originState).getSpawnedType());
            ((CreatureSpawner) destState).setDelay(((CreatureSpawner) originState).getDelay());
            ((CreatureSpawner) destState).setMaxNearbyEntities(((CreatureSpawner) originState).getMaxNearbyEntities());
            ((CreatureSpawner) destState).setMinSpawnDelay(((CreatureSpawner) originState).getMinSpawnDelay());
            ((CreatureSpawner) destState).setMaxSpawnDelay(((CreatureSpawner) originState).getMaxSpawnDelay());
            ((CreatureSpawner) destState).setRequiredPlayerRange(((CreatureSpawner) originState).getRequiredPlayerRange());
            ((CreatureSpawner) destState).setSpawnCount(((CreatureSpawner) originState).getSpawnCount());
            ((CreatureSpawner) destState).setSpawnRange(((CreatureSpawner) originState).getSpawnRange());
        }

        destState.update();

        if (removeOrigin) {
            origin.setType(Material.AIR);
        }
    }
}
