package com.denizenscript.denizen.events.entity;

import com.denizenscript.denizen.BukkitScriptEntryData;
import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.objects.NPCTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.denizencore.scripts.containers.ScriptContainer;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityDismountEvent;

public class EntityDismountsEntityScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // entity dismounts entity
    // entity dismounts <entity>
    // <entity> dismounts entity
    // <entity> dismounts <entity>
    //
    // @Regex ^on [^\s]+ dismounts [^\s]+$
    // @Switch in <area>
    //
    // @Cancellable true
    //
    // @Triggers when an entity dismounts another entity
    //
    // @Context
    // <context.entity> returns the EntityTag that is trying to dismount.
    // <context.mount> returns the EntityTag that is being dismounted.
    //
    // @Player when the dismounting entity or the entity being dismounted is a player. Cannot be both.
    //
    // @NPC when the dismounting entity or the entity being dismounted is an NPC. Cannot be both.
    //
    // -->

    public EntityDismountsEntityScriptEvent() {
        instance = this;
    }

    public static EntityDismountsEntityScriptEvent instance;
    public EntityTag entity;
    public EntityTag mount;
    public EntityDismountEvent event;

    @Override
    public boolean couldMatch(ScriptPath path) {
        return path.eventArgLowerAt(1).equals("dismounts");
    }

    @Override
    public boolean matches(ScriptPath path) {
        if (!tryEntity(entity, path.eventArgLowerAt(0))
                || !tryEntity(mount, path.eventArgLowerAt(2))
                || !runInCheck(path, mount.getLocation())) {
            return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return "EntityDismountsEntity";
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        PlayerTag player = entity.isPlayer() ? EntityTag.getPlayerFrom(entity.getBukkitEntity()) : null;
        if (mount != null && player == null && mount.isPlayer()) {
            player = EntityTag.getPlayerFrom(mount.getBukkitEntity());
        }
        NPCTag npc = entity.isCitizensNPC() ? EntityTag.getNPCFrom(entity.getBukkitEntity()) : null;
        if (mount != null && npc == null && mount.isPlayer()) {
            npc = EntityTag.getNPCFrom(mount.getBukkitEntity());
        }
        return new BukkitScriptEntryData(player, npc);
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("entity")) {
            return entity.getDenizenObject();
        }
        else if (name.equals("mount")) {
            return mount.getDenizenObject();
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onEntityDismountsEntity(EntityDismountEvent event) {
        entity = new EntityTag(event.getEntity());
        mount = new EntityTag(event.getDismounted());
        this.event = event;
        fire(event);
    }
}
