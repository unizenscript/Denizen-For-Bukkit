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

import java.util.ArrayList;
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
    // Makes the player see fake block changes.
    // Only the players targeted by this command will see the fake block changes.
    //
    // You must specify one or more locations, and one or more materials.
    // If the material list is not the same size as the location list, then the materials used will cycle or be cut short.
    //
    // Optionally, specify a list of players to show the fake change to.
    // If unspecified, the targeted player will default to the linked player.
    //
    // Optionally, specify how long the fake blocks should remain for.
    // If unspecified, the fake blocks will disappear after the default duration of 10 seconds.
    // After the duration is up, the blocks will revert back to whatever they really were.
    // Be aware that this system is not perfect, and will not prevent faked blocks from reverting on their own.
    // This can happen if a player clicks on the block, or blocks near the faked ones cahnge, or the player leaves the area and returns.
    //
    // Note that while the player will see the faked blocks as though they were real, the server will have no knowledge of the fake blocks.
    // If a player stands on top a fake block that the server sees as air, the server will think that the player is flying.
    // Similarly, if a player walks through fake air that the server sees as a solid block, the server will think that the player is walking through walls.
    // This can lead to players getting kicked by anti-cheat systems.
    // Note as well that some clientside block effects may occur. For example, fake fire may appear to momentarily ignite things, but the fire isn't real.
    //
    // @Tags
    // <PlayerTag.fake_block_locations>
    // <PlayerTag.fake_block[<location>]>
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
            else if (!scriptEntry.hasObject("duration")
                    && arg.matchesPrefix("d", "duration")
                    && arg.matchesArgumentType(DurationTag.class)) {
                scriptEntry.addObject("duration", arg.asType(DurationTag.class));
            }
            else if (!scriptEntry.hasObject("cancel")
                    && arg.matches("cancel")) {
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
            List<PlayerTag> players = new ArrayList<>();
            players.add(Utilities.getEntryPlayer(scriptEntry));
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
