package com.denizenscript.denizen.tags.core;

import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.objects.TagRunnable;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.tags.ReplaceableTagEvent;
import com.denizenscript.denizencore.tags.TagManager;
import com.denizenscript.denizencore.utilities.CoreUtilities;

public class ParseTagBase {

    public ParseTagBase() {
        TagManager.registerTagHandler(new TagRunnable.RootForm() {
            @Override
            public void run(ReplaceableTagEvent event) {
                parseTags(event);
            }
        }, "parse");
    }

    public void parseTags(ReplaceableTagEvent event) {
        // TODO: Future: Deprecations.oldParseTag.warn(event.getScriptEntry());
        if (event.matches("parse")) {
            if (!event.hasValue()) {
                Debug.echoError("Escape tag '" + event.raw_tag + "' does not have a value!");
                return;
            }
            ObjectTag read = TagManager.tagObject(TagManager.cleanOutputFully(event.getValue()), event.getContext());
            event.setReplacedObject(CoreUtilities.autoAttrib(read, event.getAttributes().fulfill(1)));
        }
    }
}
