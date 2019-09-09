package com.denizenscript.denizen.scripts.commands.entity;

import com.denizenscript.denizen.utilities.DenizenAPI;
import com.denizenscript.denizen.utilities.Utilities;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizen.utilities.depends.Depends;
import com.denizenscript.denizencore.objects.Argument;
import com.google.common.base.Function;
import com.denizenscript.denizen.nms.NMSHandler;
import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.objects.NPCTag;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsException;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.ArgumentHelper;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.scripts.commands.Holdable;
import net.citizensnpcs.api.ai.Navigator;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WalkCommand extends AbstractCommand implements Holdable {

    // <--[command]
    // @Name Walk
    // @Syntax walk (<entity>|...) [<location>/stop] (speed:<#.#>) (auto_range) (radius:<#.#>) (lookat:<location>)
    // @Required 1
    // @Short Causes an entity or list of entities to walk to another location.
    // @Group entity
    //
    // @Description
    // TODO: Document Command Details
    //
    // @Tags
    // <NPCTag.navigator.is_navigating>
    // <NPCTag.navigator.speed>
    // <NPCTag.navigator.range>
    // <NPCTag.navigator.target_location>
    //
    // @Usage
    // TODO: Document Command Details
    // -->

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {

        // Interpret arguments

        for (Argument arg : scriptEntry.getProcessedArgs()) {

            if (!scriptEntry.hasObject("lookat")
                    && arg.matchesPrefix("lookat")
                    && arg.matchesArgumentType(LocationTag.class)) {
                scriptEntry.addObject("lookat", arg.asType(LocationTag.class));
            }
            else if (!scriptEntry.hasObject("speed")
                    && arg.matchesPrimitive(ArgumentHelper.PrimitiveType.Percentage)
                    && arg.matchesPrefix("s, speed")) {
                scriptEntry.addObject("speed", arg.asElement());
            }
            else if (!scriptEntry.hasObject("auto_range")
                    && arg.matches("auto_range")) {
                scriptEntry.addObject("auto_range", new ElementTag(true));
            }
            else if (!scriptEntry.hasObject("radius")
                    && arg.matchesPrimitive(ArgumentHelper.PrimitiveType.Double)
                    && arg.matchesPrefix("radius")) {
                scriptEntry.addObject("radius", arg.asElement());
            }
            else if (!scriptEntry.hasObject("stop")
                    && arg.matches("stop")) {
                scriptEntry.addObject("stop", new ElementTag(true));
            }
            else if (!scriptEntry.hasObject("location")
                    && arg.matchesArgumentType(LocationTag.class)) {
                scriptEntry.addObject("location", arg.asType(LocationTag.class));
            }
            else if (!scriptEntry.hasObject("entities")
                    && arg.matchesArgumentList(EntityTag.class)) {
                scriptEntry.addObject("entities", arg.asType(ListTag.class).filter(EntityTag.class, scriptEntry));
            }
            else {
                arg.reportUnhandled();
            }
        }


        // Check for required information

        if (!scriptEntry.hasObject("location") && !scriptEntry.hasObject("stop")) {
            throw new InvalidArgumentsException("Must specify a location!");
        }

        if (!scriptEntry.hasObject("entities")) {
            if (Utilities.getEntryNPC(scriptEntry) == null
                    || !Utilities.getEntryNPC(scriptEntry).isValid()
                    || !Utilities.getEntryNPC(scriptEntry).isSpawned()) {
                throw new InvalidArgumentsException("Must have a valid spawned NPC attached.");
            }
            else {
                scriptEntry.addObject("entities",
                        Arrays.asList(Utilities.getEntryNPC(scriptEntry).getDenizenEntity()));
            }
        }

        scriptEntry.defaultObject("stop", new ElementTag(false));
    }


    @Override
    public void execute(ScriptEntry scriptEntry) {

        // Fetch required objects

        LocationTag loc = (LocationTag) scriptEntry.getObject("location");
        ElementTag speed = scriptEntry.getElement("speed");
        ElementTag auto_range = scriptEntry.getElement("auto_range");
        ElementTag radius = scriptEntry.getElement("radius");
        ElementTag stop = scriptEntry.getElement("stop");
        List<EntityTag> entities = (List<EntityTag>) scriptEntry.getObject("entities");
        final LocationTag lookat = scriptEntry.getObjectTag("lookat");


        // Debug the execution

        if (scriptEntry.dbCallShouldDebug()) {

            Debug.report(scriptEntry, getName(), (loc != null ? loc.debug() : "")
                    + (speed != null ? speed.debug() : "")
                    + (auto_range != null ? auto_range.debug() : "")
                    + (radius != null ? radius.debug() : "")
                    + (lookat != null ? lookat.debug() : "")
                    + stop.debug()
                    + (ArgumentHelper.debugObj("entities", entities)));

        }

        // Do the execution

        boolean shouldStop = stop.asBoolean();

        List<NPCTag> npcs = new ArrayList<>();
        final List<EntityTag> waitForEntities = new ArrayList<>();
        for (final EntityTag entity : entities) {
            if (entity.isCitizensNPC()) {
                NPCTag npc = entity.getDenizenNPC();
                npcs.add(npc);
                if (!npc.isSpawned()) {
                    Debug.echoError(scriptEntry.getResidingQueue(), "NPC " + npc.identify() + " is not spawned!");
                    continue;
                }

                if (shouldStop) {
                    npc.getNavigator().cancelNavigation();
                    continue;
                }

                if (auto_range != null
                        && auto_range.asBoolean()) {
                    double distance = npc.getLocation().distance(loc);
                    if (npc.getNavigator().getLocalParameters().range() < distance + 10) {
                        npc.getNavigator().getLocalParameters().range((float) distance + 10);
                    }
                }

                npc.getNavigator().setTarget(loc);

                if (lookat != null) {
                    npc.getNavigator().getLocalParameters().lookAtFunction(new Function<Navigator, Location>() {
                        @Override
                        public Location apply(Navigator nav) {
                            return lookat;
                        }
                    });
                }

                if (speed != null) {
                    npc.getNavigator().getLocalParameters().speedModifier(speed.asFloat());
                }

                if (radius != null) {
                    npc.getNavigator().getLocalParameters().distanceMargin(radius.asDouble());
                    npc.getNavigator().getLocalParameters().pathDistanceMargin(radius.asDouble());
                    npc.getNavigator().getLocalParameters().addRunCallback(WalkCommandCitizensEvents
                            .generateNewFlocker(npc.getCitizen(), radius.asDouble()));
                }
            }
            else if (shouldStop) {
                NMSHandler.getEntityHelper().stopWalking(entity.getBukkitEntity());
            }
            else {
                waitForEntities.add(entity);
                NMSHandler.getEntityHelper().walkTo(entity.getBukkitEntity(), loc, speed != null ? speed.asDouble() : 0.2,
                        new Runnable() {
                            @Override
                            public void run() {
                                checkHeld(entity);
                            }
                        });
            }
        }

        if (scriptEntry.shouldWaitFor()) {
            held.add(scriptEntry);
            if (!npcs.isEmpty()) {
                scriptEntry.addObject("tally", npcs);
            }
            if (!waitForEntities.isEmpty()) {
                scriptEntry.addObject("entities", waitForEntities);
            }
        }

    }


    // Held script entries
    public static List<ScriptEntry> held = new ArrayList<>();

    public void checkHeld(EntityTag entity) {
        for (int i = 0; i < held.size(); i++) {
            ScriptEntry entry = held.get(i);
            List<EntityTag> waitForEntities = (List<EntityTag>) entry.getObject("entities");
            if (waitForEntities == null) {
                continue;
            }
            waitForEntities.remove(entity);
            if (waitForEntities.isEmpty()) {
                if (!entry.hasObject("tally") || ((List<NPCTag>) entry.getObject("tally")).isEmpty()) {
                    entry.setFinished(true);
                    held.remove(i);
                    i--;
                }
            }
        }
    }

    @Override
    public void onEnable() {
        if (Depends.citizens != null) {
            DenizenAPI.getCurrentInstance().getServer().getPluginManager()
                    .registerEvents(new WalkCommandCitizensEvents(), DenizenAPI.getCurrentInstance());
        }
    }
}
