package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.Attachable;

public class MaterialAttached implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Attachable;
    }

    public static MaterialAttached getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialAttached((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "attached"
    };

    public static final String[] handledMechs = new String[] {
            "attached"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialAttached(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Attachable getAttachable() {
        return (Attachable) material.getModernData().data;
    }

    private boolean isAttached() {
        return getAttachable().isAttached();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return isAttached() ? "true" : null;
    }

    @Override
    public String getPropertyId() {
        return "attached";
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
        // @attribute <MaterialTag.attached>
        // @returns ElementTag(Boolean)
        // @mechanism MaterialTag.attached
        // @group properties
        // @description
        // If the material is a tripwire hook or string, returns if the material is part of a complete tripwire circuit.
        // -->
        if (attribute.startsWith("attached")) {
            return new ElementTag(isAttached()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name attached
        // @input ElementTag(Boolean)
        // @description
        // Sets the attached state of the material, if the material is a tripwire hook or a string.
        // NOTE: Updating the tripwire hook will visibly change the material's texture, while a string will not have any notable difference.
        // @tags
        // <MaterialTag.attached>
        // -->
        if (mechanism.matches("attached") && mechanism.requireBoolean()) {
            getAttachable().setAttached(mechanism.getValue().asBoolean());
        }
    }
}
