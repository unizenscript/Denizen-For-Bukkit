package com.denizenscript.denizen;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.objects.NPCTag;
import com.denizenscript.denizen.objects.PlayerTag;
import org.bukkit.entity.Entity;

// Upstream (DenizenScript) moved this class. Added this placeholder for basic compatibility.
@Deprecated
public class BukkitScriptEntryData extends com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData {
    public BukkitScriptEntryData(PlayerTag player, NPCTag npc) {
        super(player, npc);
    }

    public BukkitScriptEntryData(EntityTag entity) {
        super(entity);
    }

    public BukkitScriptEntryData(Entity entity) {
        super(entity);
    }
}
