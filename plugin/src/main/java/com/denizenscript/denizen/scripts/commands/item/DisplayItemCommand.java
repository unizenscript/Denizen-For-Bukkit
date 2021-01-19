package com.denizenscript.denizen.scripts.commands.item;
import com.denizenscript.denizen.Denizen;
import com.denizenscript.denizen.nms.NMSHandler;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsException;
import com.denizenscript.denizencore.objects.Argument;
import com.denizenscript.denizencore.objects.core.DurationTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

import java.util.HashSet;
import java.util.UUID;

public class DisplayItemCommand extends AbstractCommand implements Listener {

    public DisplayItemCommand() {
        setName("displayitem");
        setSyntax("displayitem [<item>] [<location>] (duration:<value>)");
        setRequiredArguments(2, 3);
        Bukkit.getPluginManager().registerEvents(this, Denizen.getInstance());
        isProcedural = false;
    }

    // <--[command]
    // @Name DisplayItem
    // @Syntax displayitem [<item>] [<location>] (no_gravity) (permanent) (duration:<value>)
    // @Required 2
    // @Maximum 3
    // @Short Makes a non-touchable item spawn for players to view.
    // @Group item
    //
    // @Description
    // This command drops an item at the specified location which cannot be picked up by players.
    // If the "no_gravity" option is specified, then the item will have no gravity.
    // If the "permanent" option is specified, then the item will not disappear naturally. If this option is used, then the duration argument is ignored.
    // It accepts a duration which determines how long the item will stay for until disappearing.
    // If no duration is specified the item will stay for 1 minute, after which the item will disappear.
    //
    // When the server restarts, all permanent display items will persist, but all temporary display items will be automatically removed.
    //
    // @Tags
    // <server.list_display_items>
    // <server.list_temporary_display_items>
    // <server.list_permanent_display_items>
    // <EntityTag.item>
    // <entry[saveName].dropped> returns a EntityTag of the spawned item.
    //
    // @Usage
    // Use to display a stone block dropped at a players location.
    // - displayitem stone <player.location>
    //
    // @Usage
    // Use to display a diamond sword dropped at a relevant location.
    // - displayitem diamond_sword <context.location>
    //
    // @Usage
    // Use to display redstone dust dropped at a related location disappear after 10 seconds.
    // - displayitem redstone <context.location> duration:10s
    //
    // @Usage
    // Use to save the dropped item to save entry 'item_dropped'.
    // - displayitem redstone <context.location> duration:10s save:item_dropped
    // -->

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {

        for (Argument arg : scriptEntry.getProcessedArgs()) {

            if (arg.matchesArgumentType(DurationTag.class)
                    && !scriptEntry.hasObject("duration")) {
                scriptEntry.addObject("duration", arg.asType(DurationTag.class));
            }
            else if (arg.matchesArgumentType(LocationTag.class)
                    && !scriptEntry.hasObject("location")) {
                scriptEntry.addObject("location", arg.asType(LocationTag.class));
            }
            else if (arg.matchesArgumentType(ItemTag.class)
                    && !scriptEntry.hasObject("item")) {
                scriptEntry.addObject("item", arg.asType(ItemTag.class));
            }
            else if (!scriptEntry.hasObject("no_gravity")
                    && arg.matches("no_gravity")) {
                scriptEntry.addObject("no_gravity", new ElementTag(true));
            }
            else if (!scriptEntry.hasObject("permanent")
                    && arg.matches("permanent")) {
                scriptEntry.addObject("permanent", new ElementTag(true));
            }
            else {
                arg.reportUnhandled();
            }
        }

        if (!scriptEntry.hasObject("item")) {
            throw new InvalidArgumentsException("Must specify an item to display.");
        }

        if (!scriptEntry.hasObject("location")) {
            throw new InvalidArgumentsException("Must specify a location!");
        }

        scriptEntry.defaultObject("duration", new DurationTag(60));
        scriptEntry.defaultObject("no_gravity", new ElementTag(false));
        scriptEntry.defaultObject("permanent", new ElementTag(false));
    }

    private final HashSet<UUID> protectedEntities = new HashSet<>();
    private final HashSet<UUID> protectedPermanentEntities = new HashSet<>();

    public HashSet<UUID> getProtectedEntities() {
        cleanProtectedItems();
        return protectedEntities;
    }

    public HashSet<UUID> getProtectedPermanentEntities() {
        cleanProtectedItems();
        return protectedPermanentEntities;
    }

    @EventHandler
    public void onItemMerge(ItemMergeEvent event) {
        if (protectedEntities.contains(event.getEntity().getUniqueId()) ||
                protectedEntities.contains(event.getTarget().getUniqueId()) ||
                protectedPermanentEntities.contains(event.getEntity().getUniqueId()) ||
                protectedPermanentEntities.contains(event.getTarget().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemInventoryPickup(InventoryPickupItemEvent event) {
        if (protectedEntities.contains(event.getItem().getUniqueId()) ||
                protectedPermanentEntities.contains(event.getItem().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemEntityPickup(EntityPickupItemEvent event) {
        if (protectedEntities.contains(event.getItem().getUniqueId()) ||
                protectedPermanentEntities.contains(event.getItem().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void execute(ScriptEntry scriptEntry) {
        ItemTag item = scriptEntry.getObjectTag("item");
        DurationTag duration = scriptEntry.getObjectTag("duration");
        LocationTag location = scriptEntry.getObjectTag("location");
        ElementTag noGravity = scriptEntry.getObjectTag("no_gravity");
        ElementTag permanent = scriptEntry.getObjectTag("permanent");

        if (scriptEntry.dbCallShouldDebug()) {
            Debug.report(scriptEntry, getName(),
                    item.debug()
                            + duration.debug()
                            + location.debug()
                            + noGravity.debug()
                            + permanent.debug());

        }

        if (location.getWorld() == null) {
            Debug.echoError("The location must have a valid world!");
            return;
        }

        // Drop the item
        final Item dropped = location.getWorld()
                .dropItem(location.getBlockLocation().clone().add(0.5, 1.5, 0.5), item.getItemStack());
        NMSHandler.getEntityHelper().makeItemDisplayOnly(dropped);
        dropped.setGravity(!noGravity.asBoolean());
        dropped.teleport(location); // Force teleport to correct drop
        if (!dropped.isValid()) {
            Debug.echoDebug(scriptEntry, "Item failed to spawned (likely blocked by some plugin).");
            return;
        }
        final UUID itemUUID = dropped.getUniqueId();

        // If the displayed item isn't permanent, remove it after the specified/default duration.
        if (!permanent.asBoolean()) {
            protectedEntities.add(itemUUID);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Denizen.getInstance(),
                    () -> {
                        if (dropped.isValid() && !dropped.isDead()) {
                            dropped.remove();
                            protectedEntities.remove(itemUUID);
                        }
                    }, duration.getTicks());
        }
        else {
            protectedPermanentEntities.add(itemUUID);
        }

        // Remember the item entity
        scriptEntry.addObject("dropped", new EntityTag(dropped));
    }

    public void cleanProtectedItems() {
        cleanIndividualSet(protectedEntities);
        cleanIndividualSet(protectedPermanentEntities);
    }

    private void cleanIndividualSet(HashSet<UUID> set) {
        HashSet<UUID> invalidEntities = new HashSet<>();
        for (UUID uuid : set) {
            if (EntityTag.getEntityForID(uuid) == null || EntityTag.getEntityForID(uuid).getType() != EntityType.DROPPED_ITEM) {
                invalidEntities.add(uuid);
            }
        }

        set.removeAll(invalidEntities);
    }
}
