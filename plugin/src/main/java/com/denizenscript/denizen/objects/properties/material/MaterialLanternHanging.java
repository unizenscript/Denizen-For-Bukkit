package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Lantern;

public class MaterialLanternHanging implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Lantern;
    }

    public static MaterialLanternHanging getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialLanternHanging((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "is_hanging"
    };

    public static final String[] handledMechs = new String[] {
            "is_hanging"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialLanternHanging(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Lantern getLantern() {
        return (Lantern) material.getModernData().data;
    }

    private boolean isHanging() {
        return getLantern().isHanging();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return isHanging() ? "true" : null;
    }

    @Override
    public String getPropertyId() {
        return "is_hanging";
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
        // @attribute <MaterialTag.is_hanging>
        // @returns ElementTag(Boolean)
        // @mechanism MaterialTag.is_hanging
        // @group properties
        // @description
        // Returns whether the lantern material is hanging from a block.
        // -->
        if (attribute.startsWith("is_hanging")) {
            return new ElementTag(isHanging()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name is_hanging
        // @input ElementTag(Boolean)
        // @description
        // Sets whether the lantern material is hanging from a block.
        // @tags
        // <MaterialTag.is_hanging>
        // -->
        if (mechanism.matches("is_hanging") && mechanism.requireBoolean()) {
            getLantern().setHanging(mechanism.getValue().asBoolean());
        }
    }
}
