package com.denizenscript.denizen.scripts.commands.npc;

import com.denizenscript.denizen.utilities.Utilities;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizen.npc.traits.TriggerTrait;
import com.denizenscript.denizen.objects.NPCTag;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsException;
import com.denizenscript.denizencore.objects.Argument;
import com.denizenscript.denizencore.objects.core.DurationTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.ArgumentHelper;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;

public class TriggerCommand extends AbstractCommand {

    // <--[command]
    // @Name Trigger
    // @Syntax trigger [name:chat/click/damage/proximity] (state:{toggle}/true/false) (cooldown:<duration>) (radius:<#>)
    // @Required 1
    // @Short Enables or disables a trigger.
    // @Group npc
    //
    // @Description
    // Sets, enables, or disables the state of an NPC trigger.
    // The Denizen config enables the click and chat triggers automatically.
    // If the "state" argument is not specified, then the trigger's state will toggle between true and false.
    // If a cooldown is specified, then there will be a delay between two consecutive trigger pulses. Otherwise, the default cooldown in the Denizen config is used.
    // If a radius is specified, then the trigger will only activate for that specified radius. Otherwise, the default radius in the Denizen config is used.
    //
    // @Tags
    // <NPCTag.has_trigger[<trigger>]>
    //
    // @Usage
    // Use to enable the click trigger.
    // - trigger name:click state:true
    //
    // @Usage
    // Use to enable the chat trigger with a 10-second cooldown and a radius of 5 blocks.
    // - trigger name:chat state:true cooldown:10s radius:5
    //
    // @Usage
    // Use to disable the click trigger.
    // - trigger name:click state:false
    // -->

    private enum Toggle {TOGGLE, TRUE, FALSE}

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {

        for (Argument arg : scriptEntry.getProcessedArgs()) {

            if (!scriptEntry.hasObject("cooldown")
                    && arg.matchesPrefix("cooldown", "c")
                    && arg.matchesArgumentType(DurationTag.class)) {
                scriptEntry.addObject("cooldown", arg.asType(DurationTag.class));
            }
            else if (!scriptEntry.hasObject("radius")
                    && arg.matchesPrefix("radius", "r")
                    && arg.matchesPrimitive(ArgumentHelper.PrimitiveType.Integer)) {
                scriptEntry.addObject("radius", arg.asElement());
            }
            else if (!scriptEntry.hasObject("toggle")
                    && arg.matchesEnum(Toggle.values())) {
                scriptEntry.addObject("toggle", arg.asElement());
            }
            else if (!scriptEntry.hasObject("npc")
                    && arg.matchesArgumentType(NPCTag.class)) {
                scriptEntry.addObject("npc", arg.asType(NPCTag.class));
            }
            else if (!scriptEntry.hasObject("trigger")) {
                scriptEntry.addObject("trigger", arg.asElement());
            }
            else {
                arg.reportUnhandled();
            }
        }

        if (!scriptEntry.hasObject("trigger")) {
            throw new InvalidArgumentsException("Missing name argument!");
        }

        if (!scriptEntry.hasObject("toggle")) {
            scriptEntry.addObject("toggle", new ElementTag("TOGGLE"));
        }

        if (!Utilities.entryHasNPC(scriptEntry) && !scriptEntry.hasObject("npc")) {
            throw new InvalidArgumentsException("This command requires a linked NPC!");
        }

    }

    @Override
    public void execute(ScriptEntry scriptEntry) {

        ElementTag toggle = scriptEntry.getElement("toggle");
        ElementTag trigger = scriptEntry.getElement("trigger");
        ElementTag radius = scriptEntry.getElement("radius");
        DurationTag cooldown = (DurationTag) scriptEntry.getObject("cooldown");
        NPCTag npc = scriptEntry.hasObject("npc") ? (NPCTag) scriptEntry.getObject("npc") : Utilities.getEntryNPC(scriptEntry);

        if (scriptEntry.dbCallShouldDebug()) {

            Debug.report(scriptEntry, getName(),
                    trigger.debug() + toggle.debug() +
                            (radius != null ? radius.debug() : "") +
                            (cooldown != null ? cooldown.debug() : "") +
                            npc.debug());

        }

        // Add trigger trait
        if (!npc.getCitizen().hasTrait(TriggerTrait.class)) {
            npc.getCitizen().addTrait(TriggerTrait.class);
        }

        TriggerTrait trait = npc.getCitizen().getTrait(TriggerTrait.class);

        switch (Toggle.valueOf(toggle.asString().toUpperCase())) {

            case TOGGLE:
                trait.toggleTrigger(trigger.asString());
                break;

            case TRUE:
                trait.toggleTrigger(trigger.asString(), true);
                break;

            case FALSE:
                trait.toggleTrigger(trigger.asString(), false);
                break;
        }

        if (radius != null) {
            trait.setLocalRadius(trigger.asString(), radius.asInt());
        }

        if (cooldown != null) {
            trait.setLocalCooldown(trigger.asString(), cooldown.getSeconds());
        }
    }
}
