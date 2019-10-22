package com.denizenscript.denizen.scripts.commands.core;

import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizen.objects.notable.NotableManager;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsException;
import com.denizenscript.denizencore.objects.*;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.notable.Notable;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.utilities.CoreUtilities;


public class NoteCommand extends AbstractCommand {

    // <--[command]
    // @Name Note
    // @Syntax note [<Notable ObjectTag>/remove] [as:<name>]
    // @Required 2
    // @Short Adds or removes a notable object.
    // @Group core
    //
    // @Description
    // Add or remove a notable object that can be referenced in events or scripts.
    // Notable objects are "permanent" versions of other ObjectTags. (See: <@link language ObjectTag>)
    // Notable objects keep their properties when added.
    //
    // @Tags
    // <server.list_notables[<type>]>
    // <CuboidTag.notable_name>
    // <InventoryTag.notable_name>
    // <ItemTag.notable_name>
    // <LocationTag.notable_name>
    //
    // @Usage
    // Use to add a notable cuboid.
    // - note cu@1,2,3,world|4,5,6,world as:mycuboid
    //
    // @Usage
    // Use to remove a notable cuboid.
    // - note remove as:mycuboid
    //
    // @Usage
    // Use to note a location.
    // - note l@10,5,10,world as:mylocation
    // -->

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {

        for (Argument arg : scriptEntry.getProcessedArgs()) {

            if (arg.matchesPrefix("as", "i", "id")) {
                scriptEntry.addObject("id", arg.asElement());
            }
            else if (ObjectFetcher.canFetch(arg.getValue().split("@")[0])) {
                scriptEntry.addObject("object", arg.getValue());
            }
            else if (arg.matches("remove")) {
                scriptEntry.addObject("remove", new ElementTag(true));
            }
            else {
                arg.reportUnhandled();
            }
        }

        if (!scriptEntry.hasObject("id")) {
            throw new InvalidArgumentsException("Must specify an id");
        }
        if (!scriptEntry.hasObject("object") && !scriptEntry.hasObject("remove")) {
            throw new InvalidArgumentsException("Must specify a fetchable-object to note.");
        }
        if (!scriptEntry.hasObject("remove")) {
            scriptEntry.addObject("remove", new ElementTag(false));
        }

    }

    @Override
    public void execute(ScriptEntry scriptEntry) {

        String object = (String) scriptEntry.getObject("object");
        ElementTag id = scriptEntry.getElement("id");
        ElementTag remove = scriptEntry.getElement("remove");

        if (scriptEntry.dbCallShouldDebug()) {

            Debug.report(scriptEntry, getName(), ArgumentHelper.debugObj("object", object) + id.debug() + remove.debug());

        }

        if (remove.asBoolean()) {
            if (NotableManager.isSaved(id.asString())) {
                NotableManager.remove(id.asString());
                Debug.echoDebug(scriptEntry, "notable '" + id.asString() + "' removed");
            }
            else {
                Debug.echoDebug(scriptEntry, id.asString() + " is not saved");
            }
            return;
        }

        String object_type = CoreUtilities.toLowerCase(object.split("@")[0]);
        Class object_class = ObjectFetcher.getObjectClass(object_type);

        if (object_class == null) {
            Debug.echoError(scriptEntry.getResidingQueue(), "Invalid object type! Could not fetch '" + object_type + "'!");
            return;
        }

        ObjectTag arg;
        try {

            if (!ObjectFetcher.checkMatch(object_class, object)) {
                Debug.echoError(scriptEntry.getResidingQueue(), "'" + object
                        + "' is an invalid " + object_class.getSimpleName() + ".");
                return;
            }

            arg = ObjectFetcher.getObjectFrom(object_class, object, scriptEntry.entryData.getTagContext());

            if (arg instanceof Notable) {
                ((Notable) arg).makeUnique(id.asString());
            }

        }
        catch (Exception e) {
            Debug.echoError(scriptEntry.getResidingQueue(), "Uh oh! Report this to the Denizen developers! Err: NoteCommandObjectReflection");
            Debug.echoError(scriptEntry.getResidingQueue(), e);
        }


    }
}
