package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Jukebox;

public class MaterialJukeboxRecord implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Jukebox;
    }

    public static MaterialJukeboxRecord getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialJukeboxRecord((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "has_record"
    };

    public static final String[] handledMechs = new String[] {
            // None
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialJukeboxRecord(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private boolean hasRecord() {
        return ((Jukebox) material.getModernData().data).hasRecord();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return hasRecord() ? "true" : null;
    }

    @Override
    public String getPropertyId() {
        return "has_record";
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
        // @attribute <MaterialTag.has_record>
        // @returns ElementTag(Boolean)
        // @group properties
        // @description
        // Returns whether the jukebox material has a record inside of it.
        // -->
        if (attribute.startsWith("has_record")) {
            return new ElementTag(hasRecord()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {
        // None
    }
}
