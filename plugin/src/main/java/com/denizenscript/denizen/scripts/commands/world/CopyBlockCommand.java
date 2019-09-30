package com.denizenscript.denizen.scripts.commands.world;

import com.denizenscript.denizen.objects.CuboidTag;
import com.denizenscript.denizen.utilities.DenizenAPI;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizen.nms.NMSHandler;
import com.denizenscript.denizen.nms.interfaces.BlockData;
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
import java.util.List;

public class CopyBlockCommand extends AbstractCommand {

    // <--[command]
    // @Name CopyBlock
    // @Syntax copyblock [<location>/<cuboid>] [to:<location>] (remove_original) (delayed)
    // @Required 1
    // @Short Copies a block to another location, keeping metadata when possible.
    // @Group world
    //
    // @Description
    // Copies a block to another location.
    // You may also use the 'remove_original' argument to delete the original block.
    // This effectively moves the block to the target location.
    // If you use the "delayed" argument, the copied cuboid will slowly copy roughly matched to the server's limits.
    //
    // @Tags
    // <LocationTag.material>
    //
    // @Usage
    // Use to copy the block the player is looking at to their current location
    // - copyblock <player.location.cursor_on> to:<player.location>
    //
    // @Usage
    // Use to move the block the player is looking at to their current location (removing it from its original location)
    // - copyblock <player.location.cursor_on> to:<player.location> remove_original
    // -->

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {

        for (Argument arg : scriptEntry.getProcessedArgs()) {

            if (arg.matchesArgumentType(CuboidTag.class)
                    && !scriptEntry.hasObject("origin")) {
                scriptEntry.addObject("origin", arg.asType(CuboidTag.class));
            }
            else if (arg.matchesArgumentType(LocationTag.class)
                    && !scriptEntry.hasObject("origin")
                    && !arg.matchesPrefix("t", "to")) {
                scriptEntry.addObject("origin", arg.asType(LocationTag.class));
            }
            else if (arg.matchesArgumentType(LocationTag.class)
                    && arg.matchesPrefix("t", "to")) {
                scriptEntry.addObject("destination", arg.asType(LocationTag.class));
            }
            else if (arg.matches("remove_original")) {
                scriptEntry.addObject("remove", new ElementTag(true));
            }
            else if (arg.matches("delayed")) {
                scriptEntry.addObject("delayed", new ElementTag(true));
            }
            else {
                arg.reportUnhandled();
            }
        }

        // Check required arguments
        if (!scriptEntry.hasObject("origin")) {
            throw new InvalidArgumentsException("Must specify a source location or cuboid.");
        }

        if (!scriptEntry.hasObject("destination")) {
            throw new InvalidArgumentsException("Must specify a destination location.");
        }

        // Set defaults
        scriptEntry.defaultObject("remove", new ElementTag(false));
        scriptEntry.defaultObject("delayed", new ElementTag(false));
    }

    @Override
    public void execute(ScriptEntry scriptEntry) {

        ObjectTag origin = (ObjectTag) scriptEntry.getObject("origin");
        LocationTag destination = (LocationTag) scriptEntry.getObject("destination");
        ElementTag removeOriginal = (ElementTag) scriptEntry.getObject("remove");
        ElementTag delayElement = (ElementTag) scriptEntry.getObject("delayed");

        if (scriptEntry.dbCallShouldDebug()) {
            Debug.report(scriptEntry, getName(), (origin != null ? origin.debug() : "")
                    + destination.debug() + removeOriginal.debug() + delayElement.debug());
        }

        boolean remove = removeOriginal.asBoolean();
        boolean delay = delayElement.asBoolean();

        if (origin instanceof CuboidTag) {
            CuboidTag originCuboid = (CuboidTag) origin;

            if (delay) {
                new BukkitRunnable() {
                    List<CuboidTag.LocationPair> pairList = new ArrayList<>(originCuboid.pairs);
                    int lastX = 0;
                    int lastY = 0;
                    int lastZ = 0;

                    @Override
                    public void run() {
                        long startTime = System.currentTimeMillis();
                        List<CuboidTag.LocationPair> doneCuboids = new ArrayList<>();

                        for (CuboidTag.LocationPair pair : pairList) {
                            boolean continueLoops = true;
                            int xDist = pair.high.getBlockX() - pair.low.getBlockX();
                            int yDist = pair.high.getBlockY() - pair.low.getBlockY();
                            int zDist = pair.high.getBlockZ() - pair.low.getBlockZ();

                            for (int x = lastX; x <= xDist; x++) {
                                lastX = x;
                                for (int y = 0; y <= yDist; y++) {
                                    if (pair.low.getBlockY() + y < 0 || pair.low.getBlockY() + y > 255) {
                                        continue;
                                    }

                                    lastY = y;
                                    for (int z = 0; z <= zDist; z++) {
                                        if (System.currentTimeMillis() - startTime > 50) {
                                            continueLoops = false;
                                            break;
                                        }

                                        lastZ = z;

                                        Block source = pair.low.clone().add(x, y, z).getBlock();
                                        Block updateDest = destination.clone().add(x, y, z).getBlock();

                                        replaceBlock(source, updateDest, remove);
                                    }

                                    if (!continueLoops) {
                                        break;
                                    }
                                }

                                if (!continueLoops) {
                                    break;
                                }
                            }

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
                for (CuboidTag.LocationPair pair : originCuboid.pairs) {
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
                                Block updateDest = destination.clone().add(x, y, z).getBlock();

                                replaceBlock(source, updateDest, remove);
                            }
                        }
                    }
                }
            }
        }
        else if (origin instanceof LocationTag) {
            Block source = ((LocationTag) origin).getBlock();
            Block update = destination.getBlock();

            replaceBlock(source, update, remove);
        }
    }

    private void replaceBlock(Block origin, Block destination, boolean removeOrigin) {
        BlockState originState = LocationTag.getBlockStateFor(origin);
        BlockData originData = NMSHandler.getBlockHelper().getBlockData(origin);
        originData.setBlock(destination, false);
        BlockState destState = LocationTag.getBlockStateFor(destination);

        if (originState instanceof InventoryHolder) {
            ((InventoryHolder) destState).getInventory()
                    .setContents(((InventoryHolder) originState).getInventory().getContents());
        }
        else if (originState instanceof Sign) {
            int n = 0;

            for (String line : ((Sign) originState).getLines()) {
                ((Sign) destState).setLine(n, line);
                n++;
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
