package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Campfire;

public class MaterialCampfireSignal implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Campfire;
    }

    public static MaterialCampfireSignal getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialCampfireSignal((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "is_signal"
    };

    public static final String[] handledMechs = new String[] {
            "is_signal"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialCampfireSignal(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Campfire getCampfire() {
        return (Campfire) material.getModernData().data;
    }

    private boolean isSignal() {
        return getCampfire().isSignalFire();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return isSignal() ? "true" : null;
    }

    @Override
    public String getPropertyId() {
        return "is_signal";
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
        // @attribute <MaterialTag.is_signal>
        // @returns ElementTag(Boolean)
        // @mechanism MaterialTag.is_signal
        // @group properties
        // @description
        // If the material is a campfire, returns if the material is a signal fire.
        // -->
        if (attribute.startsWith("is_signal")) {
            return new ElementTag(isSignal()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name is_signal
        // @input ElementTag(Boolean)
        // @description
        // If the material is a campfire, sets if the material is a signal fire.
        // @tags
        // <MaterialTag.is_signal>
        // -->
        if (mechanism.matches("is_signal") && mechanism.requireBoolean()) {
            getCampfire().setSignalFire(mechanism.getValue().asBoolean());
        }
    }
}
