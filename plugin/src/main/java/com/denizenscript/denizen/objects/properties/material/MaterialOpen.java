package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.Openable;

public class MaterialOpen implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Openable;
    }

    public static MaterialOpen getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialOpen((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "open"
    };

    public static final String[] handledMechs = new String[] {
            "open"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialOpen(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Openable getOpenable() {
        return (Openable) material.getModernData().data;
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getOpenable().isOpen() ? "true" : null;
    }

    @Override
    public String getPropertyId() {
        return "open";
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
        // @attribute <MaterialTag.open>
        // @returns ElementTag(Boolean)
        // @mechanism MaterialTag.open
        // @group properties
        // @description
        // If the material is a door, trapdoor, or fence gate, returns whether this material is opened.
        // -->
        if (attribute.startsWith("open")) {
            return new ElementTag(getOpenable().isOpen()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name open
        // @input ElementTag(Boolean)
        // @description
        // If the material is a door, trapdoor, or fence gate, sets whether this material is opened.
        // @tags
        // <MaterialTag.open>
        // -->
        if (mechanism.matches("open") && mechanism.requireBoolean()) {
            getOpenable().setOpen(mechanism.getValue().asBoolean());
        }
    }
}
