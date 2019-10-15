package dev.unizen.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.Note;
import org.bukkit.block.data.type.NoteBlock;

public class MaterialNoteblockNote implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof NoteBlock;
    }

    public static MaterialNoteblockNote getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialNoteblockNote((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "note"
    };

    public static final String[] handledMechs = new String[] {
            "note"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialNoteblockNote(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private NoteBlock getNoteBlock() {
        return (NoteBlock) material.getModernData().data;
    }

    private int getNoteOctave() {
        return getNoteBlock().getNote().getOctave();
    }

    private String getNoteTone() {
        return getNoteBlock().getNote().getTone().name();
    }

    private boolean isNoteSharped() {
        return getNoteBlock().getNote().isSharped();
    }

    private String getNoteInfo() {
        return getNoteOctave() + "|" + getNoteTone() + "|" + isNoteSharped();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getNoteInfo();
    }

    @Override
    public String getPropertyId() {
        return "note";
    }

    ///////////
    // ObjectTag Attributes
    ////////

    @Override
    public String getAttribute(Attribute attribute) {
        if (attribute == null) {
            return null;
        }

        // <--[tag]
        // @attribute <MaterialTag.note>
        // @returns ElementTag(Number)|ElementTag|ElementTag(Boolean)
        // @mechanism MaterialTag.note
        // @group properties
        // @description
        // Returns the note information of the noteblock material.
        // Formatted as: OCTAVE|TONE|SHARP
        // -->
        if (attribute.startsWith("note")) {
            attribute.fulfill(1);

            // <--[tag]
            // @attribute <MaterialTag.note.octave>
            // @returns ElementTag(Number)
            // @mechanism MaterialTag.note
            // @group properties
            // @description
            // Returns the note octave of the noteblock material.
            // -->
            if (attribute.startsWith("octave")) {
                return new ElementTag(getNoteOctave()).getAttribute(attribute.fulfill(1));
            }

            // <--[tag]
            // @attribute <MaterialTag.note.tone>
            // @returns ElementTag
            // @mechanism MaterialTag.note
            // @group properties
            // @description
            // Returns the note tone of the noteblock material.
            // -->
            if (attribute.startsWith("tone")) {
                return new ElementTag(getNoteTone()).getAttribute(attribute.fulfill(1));
            }

            // <--[tag]
            // @attribute <MaterialTag.note.is_sharp>
            // @returns ElementTag(Boolean)
            // @mechanism MaterialTag.note
            // @group properties
            // @description
            // Returns whether the note tone of the noteblock material is sharp.
            // -->
            if (attribute.startsWith("is_sharp")) {
                return new ElementTag(isNoteSharped()).getAttribute(attribute.fulfill(1));
            }

            return new ElementTag(getNoteInfo()).getAttribute(attribute);
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name note
        // @input ElementTag(Number)|ElementTag(|ElementTag(Boolean))
        // @description
        // Sets the note information of the noteblock material.
        // The first ElementTag is a number noting the octave.
        // The second ElementTag is the note tone, which can be any of: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Note.Tone.html>
        // Optionally, use the third ElementTag to specify if the note should be sharp.
        // Omitting the third ElementTag or setting it to "false" makes the note's tone natural.
        // @tags
        // <MaterialTag.note>
        // <MaterialTag.note.octave>
        // <MaterialTag.note.tone>
        // <MaterialTag.note.is_sharp>
        // -->
        if (mechanism.matches("note") && mechanism.requireObject(ListTag.class)) {
            ListTag noteInfo = mechanism.valueAsType(ListTag.class);
            if (noteInfo.size() < 2 || noteInfo.size() > 3) {
                Debug.echoError("Invalid input! Valid input must be at least OCTAVE|NOTE, and at most OCTAVE|NOTE|SHARP!");
                return;
            }

            ElementTag elOctave = new ElementTag(noteInfo.get(0));
            int octave = 0;
            if (elOctave.isInt()) {
                octave = elOctave.asInt();
            }

            String elTone = noteInfo.get(1);
            Note.Tone tone;
            try {
                tone = Note.Tone.valueOf(elTone.toUpperCase());
            }
            catch (IllegalArgumentException e) {
                Debug.echoError(e);
                return;
            }

            boolean sharp = false;
            if (noteInfo.size() == 3) {
                ElementTag elSharp = new ElementTag(noteInfo.get(2));
                if (elSharp.isBoolean()) {
                    sharp = elSharp.asBoolean();
                }
            }

            getNoteBlock().setNote(new Note(octave, tone, sharp));
        }
    }
}
