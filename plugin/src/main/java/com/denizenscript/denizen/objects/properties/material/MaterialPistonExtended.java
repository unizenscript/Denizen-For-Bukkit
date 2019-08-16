package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Piston;

public class MaterialPistonExtended implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Piston;
    }

    public static MaterialPistonExtended getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialPistonExtended((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "extended"
    };

    public static final String[] handledMechs = new String[] {
            "extended"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialPistonExtended(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Piston getPiston() {
        return (Piston) material.getModernData().data;
    }

    private boolean isExtended() {
        return getPiston().isExtended();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return isExtended() ? "true" : null;
    }

    @Override
    public String getPropertyId() {
        return "extended";
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
        // @attribute <MaterialTag.extended>
        // @returns ElementTag(Boolean)
        // @mechanism MaterialTag.extended
        // @group properties
        // @description
        // Returns whether the piston material's head is extended.
        // -->
        if (attribute.startsWith("extended")) {
            return new ElementTag(isExtended()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name extended
        // @input ElementTag(Boolean)
        // @description
        // Sets whether the piston material's head is extended.
        // @tags
        // <MaterialTag.extended>
        // -->
        if (mechanism.matches("extended") && mechanism.requireBoolean()) {
            getPiston().setExtended(mechanism.getValue().asBoolean());
        }
    }
}
