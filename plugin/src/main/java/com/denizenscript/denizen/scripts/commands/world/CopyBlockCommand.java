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
            List<BlockState> updateList = new ArrayList<>();

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
                            BlockState sourceState = LocationTag.getBlockStateFor(source);
                            Block updateDest = destination.clone().add(x, y, z).getBlock();

                            BlockData blockData = NMSHandler.getBlockHelper().getBlockData(source);
                            blockData.setBlock(updateDest, false);
                            BlockState updatedState = LocationTag.getBlockStateFor(updateDest);

                            if (sourceState instanceof InventoryHolder) {
                                ((InventoryHolder) updatedState).getInventory().setContents(((InventoryHolder) sourceState).getInventory().getContents());
                            }
                            else if (sourceState instanceof Sign) {
                                int n = 0;

                                for (String line : ((Sign) sourceState).getLines()) {
                                    ((Sign) updatedState).setLine(n, line);
                                    n++;
                                }
                            }
                            else if (sourceState instanceof NoteBlock) {
                                ((NoteBlock) updatedState).setNote(((NoteBlock) sourceState).getNote());
                            }
                            else if (sourceState instanceof Skull) {
                                ((Skull) updatedState).setSkullType(((Skull) sourceState).getSkullType());
                                ((Skull) updatedState).setOwner(((Skull) sourceState).getOwner());
                                ((Skull) updatedState).setRotation(((Skull) sourceState).getRotation());
                            }
                            else if (sourceState instanceof Banner) {
                                ((Banner) updatedState).setBaseColor(((Banner) sourceState).getBaseColor());
                                ((Banner) updatedState).setPatterns(((Banner) sourceState).getPatterns());
                            }
                            else if (sourceState instanceof CommandBlock) {
                                ((CommandBlock) updatedState).setName(((CommandBlock) sourceState).getName());
                                ((CommandBlock) updatedState).setCommand(((CommandBlock) sourceState).getCommand());
                            }
                            else if (sourceState instanceof CreatureSpawner) {
                                ((CreatureSpawner) updatedState).setSpawnedType(((CreatureSpawner) sourceState).getSpawnedType());
                                ((CreatureSpawner) updatedState).setDelay(((CreatureSpawner) sourceState).getDelay());
                                ((CreatureSpawner) updatedState).setMaxNearbyEntities(((CreatureSpawner) sourceState).getMaxNearbyEntities());
                                ((CreatureSpawner) updatedState).setMinSpawnDelay(((CreatureSpawner) sourceState).getMinSpawnDelay());
                                ((CreatureSpawner) updatedState).setMaxSpawnDelay(((CreatureSpawner) sourceState).getMaxSpawnDelay());
                                ((CreatureSpawner) updatedState).setRequiredPlayerRange(((CreatureSpawner) sourceState).getRequiredPlayerRange());
                                ((CreatureSpawner) updatedState).setSpawnCount(((CreatureSpawner) sourceState).getSpawnCount());
                                ((CreatureSpawner) updatedState).setSpawnRange(((CreatureSpawner) sourceState).getSpawnRange());
                            }

                            updateList.add(updatedState);

                            if (remove) {
                                source.setType(Material.AIR);
                            }
                        }
                    }
                }
            }

            if (delay) {
                new BukkitRunnable() {
                    int lastIndex = 0;
                    @Override
                    public void run() {
                        long startTime = System.currentTimeMillis();
                        int i;
                        for (i = lastIndex; i < updateList.size(); i++) {
                            if (System.currentTimeMillis() - startTime > 50) {
                                break;
                            }
                            updateList.get(i).update();
                        }
                        lastIndex = i;

                        if (lastIndex == updateList.size() - 1) {
                            cancel();
                        }
                    }
                }.runTaskTimer(DenizenAPI.getCurrentInstance(), 1, 1);
            }
            else {
                for (BlockState state : updateList) {
                    state.update();
                }
            }
        }
        else if (origin instanceof LocationTag) {
            LocationTag originLoc = (LocationTag) origin;

            Block source = originLoc.getBlock();
            BlockState sourceState = LocationTag.getBlockStateFor(source);
            Block update = destination.getBlock();

            // TODO: 1.13 - confirm this works
            BlockData blockData = NMSHandler.getBlockHelper().getBlockData(source);
            blockData.setBlock(update, false);

            BlockState updateState = LocationTag.getBlockStateFor(update);

            // Note: only a BlockState, not a Block, is actually an instance
            // of InventoryHolder
            if (sourceState instanceof InventoryHolder) {
                ((InventoryHolder) updateState).getInventory()
                        .setContents(((InventoryHolder) sourceState).getInventory().getContents());
            }
            else if (sourceState instanceof Sign) {
                int n = 0;

                for (String line : ((Sign) sourceState).getLines()) {
                    ((Sign) updateState).setLine(n, line);
                    n++;
                }
            }
            else if (sourceState instanceof NoteBlock) {
                ((NoteBlock) updateState).setNote(((NoteBlock) sourceState).getNote());
            }
            else if (sourceState instanceof Skull) {
                ((Skull) updateState).setSkullType(((Skull) sourceState).getSkullType());
                ((Skull) updateState).setOwner(((Skull) sourceState).getOwner());
                ((Skull) updateState).setRotation(((Skull) sourceState).getRotation());
            }
            else if (sourceState instanceof Jukebox) {
                ((Jukebox) updateState).setPlaying(((Jukebox) sourceState).getPlaying());
            }
            else if (sourceState instanceof Banner) {
                ((Banner) updateState).setBaseColor(((Banner) sourceState).getBaseColor());
                ((Banner) updateState).setPatterns(((Banner) sourceState).getPatterns());
            }
            else if (sourceState instanceof CommandBlock) {
                ((CommandBlock) updateState).setName(((CommandBlock) sourceState).getName());
                ((CommandBlock) updateState).setCommand(((CommandBlock) sourceState).getCommand());
            }
            else if (sourceState instanceof CreatureSpawner) {
                ((CreatureSpawner) updateState).setSpawnedType(((CreatureSpawner) sourceState).getSpawnedType());
                ((CreatureSpawner) updateState).setDelay(((CreatureSpawner) sourceState).getDelay());
                ((CreatureSpawner) updateState).setMaxNearbyEntities(((CreatureSpawner) sourceState).getMaxNearbyEntities());
                ((CreatureSpawner) updateState).setMinSpawnDelay(((CreatureSpawner) sourceState).getMinSpawnDelay());
                ((CreatureSpawner) updateState).setMaxSpawnDelay(((CreatureSpawner) sourceState).getMaxSpawnDelay());
                ((CreatureSpawner) updateState).setRequiredPlayerRange(((CreatureSpawner) sourceState).getRequiredPlayerRange());
                ((CreatureSpawner) updateState).setSpawnCount(((CreatureSpawner) sourceState).getSpawnCount());
                ((CreatureSpawner) updateState).setSpawnRange(((CreatureSpawner) sourceState).getSpawnRange());
            }

            updateState.update();

            if (remove) {
                originLoc.getBlock().setType(Material.AIR);
            }
        }
    }
}
