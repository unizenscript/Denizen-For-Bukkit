package dev.unizen.denizen.events.entity;

import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.objects.NPCTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityMountEvent;

public class EntityMountsEntityScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // entity mounts entity
    // entity mounts <entity>
    // <entity> mounts entity
    // <entity> mounts <entity>
    //
    // @Regex ^on [^\s]+ mounts [^\s]+$
    // @Switch in <area>
    //
    // @Cancellable true
    //
    // @Triggers when an entity mounts another entity.
    //
    // @Context
    // <context.entity> returns the EntityTag that is trying to mount.
    // <context.mount> returns the EntityTag that is being mounted.
    //
    // @Player when the mounting entity or the entity being mounted is a player. Cannot be both.
    //
    // @NPC when the mounting entity or the entity being mounted is an NPC. Cannot be both.
    //
    // -->

    public EntityMountsEntityScriptEvent() {
        instance = this;
    }

    public static EntityMountsEntityScriptEvent instance;
    public EntityTag entity;
    public EntityTag mount;
    public EntityMountEvent event;

    @Override
    public boolean couldMatch(ScriptPath path) {
        return path.eventArgLowerAt(1).equals("mounts");
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
        return "EntityMountsEntity";
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        PlayerTag player = entity.isPlayer() ? EntityTag.getPlayerFrom(entity.getBukkitEntity()) : null;
        if (mount != null && player == null && mount.isPlayer()) {
            player = EntityTag.getPlayerFrom(mount.getBukkitEntity());
        }
        NPCTag npc = entity.isCitizensNPC() ? EntityTag.getNPCFrom(entity.getBukkitEntity()) : null;
        if (mount != null && npc == null && mount.isCitizensNPC()) {
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
    public void onEntityMountsEntity(EntityMountEvent event) {
        entity = new EntityTag(event.getEntity());
        mount = new EntityTag(event.getMount());
        this.event = event;
        fire(event);
    }
}
