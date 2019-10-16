package dev.unizen.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.Instrument;
import org.bukkit.block.data.type.NoteBlock;

public class MaterialNoteblockInstrument implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof NoteBlock;
    }

    public static MaterialNoteblockInstrument getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialNoteblockInstrument((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "instrument"
    };

    public static final String[] handledMechs = new String[] {
            "instrument"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialNoteblockInstrument(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private NoteBlock getNoteBlock() {
        return (NoteBlock) material.getModernData().data;
    }

    private String getInstrument() {
        return getNoteBlock().getInstrument().name();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getNoteBlock().getInstrument() != Instrument.PIANO ? getInstrument() : null;
    }

    @Override
    public String getPropertyId() {
        return "instrument";
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
        // @attribute <MaterialTag.instrument>
        // @returns ElementTag
        // @mechanism MaterialTag.instrument
        // @group properties
        // @description
        // Returns the instrument the noteblock material is using.
        // The instrument can be any of: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Instrument.html>
        // -->
        if (attribute.startsWith("instrument")) {
            return new ElementTag(getInstrument()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name instrument
        // @input ElementTag
        // @description
        // Sets the instrument the noteblock material is using.
        // The instrument can be any of: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Instrument.html>
        // @tags
        // <MaterialTag.instrument>
        // -->
        if (mechanism.matches("instrument") && mechanism.requireEnum(false, Instrument.values())) {
            getNoteBlock().setInstrument(Instrument.valueOf(mechanism.getValue().asString().toUpperCase()));
        }
    }
}
