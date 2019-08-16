package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Leaves;

public class MaterialLeavesPersistence implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Leaves;
    }

    public static MaterialLeavesPersistence getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialLeavesPersistence((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "persistent"
    };

    public static final String[] handledMechs = new String[] {
            "persistent"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialLeavesPersistence(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Leaves getLeaves() {
        return (Leaves) material.getModernData().data;
    }

    private boolean isPersistent() {
        return getLeaves().isPersistent();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return isPersistent() ? "true" : null;
    }

    @Override
    public String getPropertyId() {
        return "persistent";
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
        // @attribute <MaterialTag.persistent>
        // @returns ElementTag(Boolean)
        // @mechanism MaterialTag.persistent
        // @group properties
        // @description
        // Returns whether the leaves material is persistent or not.
        // -->
        if (attribute.startsWith("persistent")) {
            return new ElementTag(isPersistent()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name persistent
        // @input ElementTag(Boolean)
        // @description
        // Sets whether the leaves material is persistent or not.
        // @tags
        // <MaterialTag.persistent>
        // -->
        if (mechanism.matches("persistent") && mechanism.requireBoolean()) {
            getLeaves().setPersistent(mechanism.getValue().asBoolean());
        }
    }
}
