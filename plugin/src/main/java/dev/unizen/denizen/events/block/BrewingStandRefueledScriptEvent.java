package dev.unizen.denizen.events.block;

import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewingStandFuelEvent;

public class BrewingStandRefueledScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // brewing stand refueled
    //
    // @Regex ^on brewing stand refueled$
    //
    // @Group Block
    //
    // @Switch in <area>
    //
    // @Cancellable true
    //
    // @Triggers when a brewing stand block is about to refuel.
    //
    // @Context
    // <context.item> returns the ItemTag of the item about to be used as fuel.
    // <context.power> returns the ElementTag(Number) of the fuel power of the item.
    // <context.consuming> returns the ElementTag(Boolean) of whether the item will be consumed.
    // <context.location> returns the LocationTag of the brewing stand.
    //
    // @Determine
    // Element(Number) to set the new fuel power.
    // Element(Boolean) to set whether the item will be consumed.
    //
    // -->

    public static BrewingStandRefueledScriptEvent instance;

    public LocationTag location;
    public ItemTag fuelItem;
    public ElementTag power;
    public ElementTag shouldConsume;
    public BrewingStandFuelEvent event;

    public BrewingStandRefueledScriptEvent() {
        instance = this;
    }

    @Override
    public boolean couldMatch(ScriptPath path) {
        return path.eventLower.startsWith("brewing stand refueled");
    }

    @Override
    public boolean matches(ScriptPath path) {
        return runInCheck(path, location);
    }

    @Override
    public String getName() {
        return "BrewingStandRefueled";
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("location")) {
            return location;
        }
        else if (name.equals("item")) {
            return fuelItem;
        }
        else if (name.equals("power")) {
            return power;
        }
        else if (name.equals("consuming")) {
            return shouldConsume;
        }
        return super.getContext(name);
    }

    @Override
    public boolean applyDetermination(ScriptPath path, ObjectTag determination) {
        if (determination instanceof ElementTag) {
            ElementTag element = (ElementTag) determination;
            if (element.isInt()) {
                event.setFuelPower(element.asInt());
                return true;
            }
            else if (element.isBoolean()) {
                event.setConsuming(element.asBoolean());
                return true;
            }
        }
        return super.applyDetermination(path, determination);
    }

    @EventHandler
    public void onBrewingStandRefueled(BrewingStandFuelEvent event) {
        this.event = event;
        location = new LocationTag(event.getBlock().getLocation());
        fuelItem = new ItemTag(event.getFuel());
        power = new ElementTag(event.getFuelPower());
        shouldConsume = new ElementTag(event.isConsuming());
        fire(event);
    }
}
