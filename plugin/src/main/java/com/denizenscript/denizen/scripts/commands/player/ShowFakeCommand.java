package com.denizenscript.denizen.scripts.commands.player;

import com.denizenscript.denizen.utilities.Utilities;
import com.denizenscript.denizen.utilities.blocks.FakeBlock;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsException;
import com.denizenscript.denizencore.objects.*;
import com.denizenscript.denizencore.objects.core.DurationTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;

import java.util.List;

public class ShowFakeCommand extends AbstractCommand {

    // <--[command]
    // @Name ShowFake
    // @Syntax showfake [<material>|.../cancel] [<location>|...] (players:<player>|...) (d:<duration>{10s})
    // @Required 2
    // @Short Makes the player see a block change that didn't actually happen.
    // @Group player
    //
    // @Description
    // Makes the player see a block change that didn't actually happen.
    // This means that the server will still register the block being what it was before the command,
    // and players not included in the command will still see the original block.
    //
    // You must specify a location (or list of locations), and a material (or list of materials).
    // The material list does not have to be of the same size as the location list (materials will be repeated automatically).
    //
    // Optionally, specify a list of players to show the change to.
    // If unspecified, will default to the linked player.
    //
    // Optionally, specify how long the fake block should remain for.
    // If unspecified, will default to 10 seconds.
    // After the duration is up, the block will revert back to whatever it really is (on the server-side).
    // Be aware that this system is not perfect, and will not prevent blocks from reverting on their own.
    // This can happen if a player clicks on the block, or blocks change near it, or the player leaves the area and comes back.
    //
    // Note that while the player will see the block as though it were real, the server will have no knowledge of this.
    // This means that if the player, for example, stands atop a fake block that the server sees as air, that player will be seen as flying.
    // The reverse applies as well: if a player walks through fake air (that is actually solid), the server will see a player walking through walls.
    // This can easily lead to players getting kicked by anti-cheat systems or similar results.
    // Note as well that some clientside block effects may occur (eg fake fire may appear momentarily to actually ignite things, but won't actually damage them).
    //
    // @Tags
    // None
    //
    // @Usage
    // Use to place a fake gold block at where the player is looking
    // - showfake gold_block <player.location.cursor_on>
    //
    // @Usage
    // Use to place a stone block right on player's head, that only stays for a second.
    // - showfake stone <player.location.add[0,1,0]> duration:1s
    //
    // @Usage
    // Use to place fake lava that the player is standing in, for all the server to see
    // - showfake lava <player.location> players:<server.list_online_players>
    // -->

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {

        // Iterate through arguments
        for (Argument arg : scriptEntry.getProcessedArgs()) {

            if (!scriptEntry.hasObject("players")
                    && arg.matchesPrefix("to", "players")) {
                scriptEntry.addObject("players", arg.asType(ListTag.class).filter(PlayerTag.class, scriptEntry));
            }
            else if (arg.matchesPrefix("d", "duration")
                    && arg.matchesArgumentType(DurationTag.class)) {
                scriptEntry.addObject("duration", arg.asType(DurationTag.class));
            }
            else if (arg.matches("cancel")) {
                scriptEntry.addObject("cancel", new ElementTag(true));
            }
            else if (!scriptEntry.hasObject("materials")
                && arg.matchesArgumentList(MaterialTag.class)) {
                scriptEntry.addObject("materials", arg.asType(ListTag.class).filter(MaterialTag.class, scriptEntry));
            }
            else if (!scriptEntry.hasObject("locations")
                    && arg.matchesArgumentList(LocationTag.class)) {
                scriptEntry.addObject("locations", arg.asType(ListTag.class).filter(LocationTag.class, scriptEntry));
            }
            else {
                arg.reportUnhandled();
            }
        }

        if (!scriptEntry.hasObject("players") && Utilities.entryHasPlayer(scriptEntry)) {
            ListTag players = new ListTag();
            players.addObject(Utilities.getEntryPlayer(scriptEntry));
            scriptEntry.defaultObject("players", players);
        }

        if (!scriptEntry.hasObject("locations")) {
            throw new InvalidArgumentsException("Must specify at least one valid location!");
        }

        if (!scriptEntry.hasObject("players")) {
            throw new InvalidArgumentsException("Must have a valid, online player attached!");
        }

        if (!scriptEntry.hasObject("materials") && !scriptEntry.hasObject("cancel")) {
            throw new InvalidArgumentsException("Must specify valid material(s)!");
        }

        scriptEntry.defaultObject("duration", new DurationTag(10));
        scriptEntry.defaultObject("cancel", new ElementTag(false));
    }


    @Override
    public void execute(ScriptEntry scriptEntry) {

        DurationTag duration = scriptEntry.getObjectTag("duration");
        ElementTag cancel = scriptEntry.getElement("cancel");
        List<MaterialTag> materials = (List<MaterialTag>) scriptEntry.getObject("materials");
        List<LocationTag> locations = (List<LocationTag>) scriptEntry.getObject("locations");
        List<PlayerTag> players = (List<PlayerTag>) scriptEntry.getObject("players");

        if (scriptEntry.dbCallShouldDebug()) {
            Debug.report(scriptEntry, getName(), duration.debug() + cancel.debug()
                    + (materials != null ? ArgumentHelper.debugList("materials", materials) : "")
                    + ArgumentHelper.debugList("locations", locations)
                    + ArgumentHelper.debugList("players", players));
        }


        boolean shouldCancel = cancel.asBoolean();

        int i = 0;
        for (LocationTag loc : locations) {
            if (!shouldCancel) {
                FakeBlock.showFakeBlockTo(players, loc, materials.get(i % materials.size()), duration);
            }
            else {
                FakeBlock.stopShowingTo(players, loc);
            }
            i++;
        }
    }
}
