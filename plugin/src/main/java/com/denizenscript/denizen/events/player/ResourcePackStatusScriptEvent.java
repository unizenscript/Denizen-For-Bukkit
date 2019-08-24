package com.denizenscript.denizen.events.player;

import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.BukkitScriptEntryData;
import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;

public class ResourcePackStatusScriptEvent extends BukkitScriptEvent {

    // <--[event]
    // @Events
    // resource pack status
    //
    // @Regex ^on resource pack status$
    //
    // @Triggers when a player accepts, denies, successfully loads, or fails to download a resource pack.
    //
    // @Context
    // <context.hash> returns an ElementTag of the resource pack's hash, or null if one was not specified.
    // <context.status> returns an ElementTag of the status. Can be: SUCCESSFULLY_LOADED, DECLINED, FAILED_DOWNLOAD, ACCEPTED.
    //
    // -->

    public ResourcePackStatusScriptEvent() {
        instance = this;
    }

    public static ResourcePackStatusScriptEvent instance;

    public ElementTag hash;
    public ElementTag status;
    public PlayerTag player;

    @Override
    public boolean couldMatch(ScriptPath path) {
        return path.eventLower.startsWith("resource pack status");
    }

    @Override
    public boolean matches(ScriptPath path) {
        return true;
    }

    @Override
    public String getName() {
        return "ResourcePackStatus";
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("hash")) {
            return hash;
        }
        else if (name.equals("status")) {
            return status;
        }
        return super.getContext(name);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(player, null);
    }
}
