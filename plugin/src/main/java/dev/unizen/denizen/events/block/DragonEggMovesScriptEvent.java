package dev.unizen.denizen.events.block;

import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

public class DragonEggMovesScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // dragon egg moves
    //
    // @Regex ^on dragon egg moves$
    //
    // @Group Block
    //
    // @Switch in <area>
    //
    // @Cancellable true
    //
    // @Triggers when a dragon egg moves.
    //
    // @Context
    // <context.destination> returns the LocationTag the dragon egg moved to.
    // <context.location> returns the LocationTag the block the egg was on.
    //
    // -->

    public DragonEggMovesScriptEvent() {
        instance = this;
    }

    public static DragonEggMovesScriptEvent instance;
    public LocationTag location;
    public LocationTag destination;
    public BlockFromToEvent event;

    @Override
    public boolean couldMatch(ScriptPath path) {
        return path.eventLower.startsWith("dragon egg moves");
    }

    @Override
    public boolean matches(ScriptPath path) {
        if (!runInCheck(path, location) && !runInCheck(path, destination)
                || event.getBlock().getType() != Material.DRAGON_EGG) {
            return false;
        }
        return true;
    }

    @Override
    public String getName() {
        return "DragonEggMoves";
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("location")) {
            return location;
        }
        else if (name.equals("destination")) {
            return destination;
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onDragonEggMoves(BlockFromToEvent event) {
        location = new LocationTag(event.getBlock().getLocation());
        destination = new LocationTag(event.getToBlock().getLocation());
        this.event = event;
        fire(event);
    }
}
