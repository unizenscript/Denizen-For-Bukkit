package com.denizenscript.denizen.events.entity;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizen.BukkitScriptEntryData;
import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;

public class EntityInteractScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // <entity> interacts with <material>
    // entity interacts with <material>
    // <entity> interacts with block
    // entity interacts with block
    //
    // @Regex ^on [^\s]+ interacts with [^\s]+$
    // @Switch in <area>
    //
    // @Cancellable true
    //
    // @Triggers when an entity interacts with a block (EG an arrow hits a button)
    //
    // @Context
    // <context.location> returns a LocationTag of the block being interacted with.
    // <context.entity> returns a EntityTag of the entity doing the interaction.
    //
    // -->

    public EntityInteractScriptEvent() {
        instance = this;
    }

    public static EntityInteractScriptEvent instance;
    public EntityTag entity;
    public LocationTag location;
    private MaterialTag material;
    public EntityInteractEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        return CoreUtilities.toLowerCase(s).contains("interacts with");
    }

    @Override
    public boolean matches(ScriptPath path) {

        if (!tryEntity(entity, path.eventArgLowerAt(0))) {
            return false;
        }

        if (!tryMaterial(material, path.eventArgLowerAt(3))) {
            return false;
        }

        if (!runInCheck(path, location)) {
            return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return "EntityInteracts";
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(entity.isPlayer() ? EntityTag.getPlayerFrom(event.getEntity()) : null,
                entity.isCitizensNPC() ? EntityTag.getNPCFrom(event.getEntity()) : null);
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("entity")) {
            return entity;
        }
        else if (name.equals("location")) {
            return location;
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onEntityInteract(EntityInteractEvent event) {
        entity = new EntityTag(event.getEntity());
        location = new LocationTag(event.getBlock().getLocation());
        material = new MaterialTag(event.getBlock());
        this.event = event;
        fire(event);
    }

}
