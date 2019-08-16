package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.AnaloguePowerable;

public class MaterialRedstonePower implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof AnaloguePowerable;
    }

    public static MaterialRedstonePower getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialRedstonePower((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "redstone_power", "max_redstone_power"
    };

    public static final String[] handledMechs = new String[] {
            "redstone_power"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialRedstonePower(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private AnaloguePowerable getPowerable() {
        return (AnaloguePowerable) material.getModernData().data;
    }

    private int getPower() {
        return getPowerable().getPower();
    }

    private int getMaxPower() {
        return getPowerable().getMaximumPower();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getPower() != 0 ? String.valueOf(getPower()) : null;
    }

    @Override
    public String getPropertyId() {
        return "redstone_power";
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
        // @attribute <MaterialTag.redstone_power>
        // @returns ElementTag(Number)
        // @mechanism MaterialTag.redstone_power
        // @group properties
        // @description
        // If the material can have redstone power sent through it, returns the current power sent by this material.
        // -->
        if (attribute.startsWith("redstone_power")) {
            return new ElementTag(getPower()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <MaterialTag.max_redstone_power>
        // @returns ElementTag(Number)
        // @mechanism MaterialTag.power
        // @group properties
        // @description
        // If the material can have redstone power sent through it, returns the maximum power that the material can send.
        // -->
        if (attribute.startsWith("max_redstone_power")) {
            return new ElementTag(getMaxPower()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name redstone_power
        // @input ElementTag(Number)
        // @description
        // Sets the redstone power the material sends, if it can send redstone power.
        // @tags
        // <MaterialTag.redstone_power>
        // <MaterialTag.max_redstone_power>
        // -->
        if (mechanism.matches("redstone_power") && mechanism.requireInteger()) {
            int power = mechanism.getValue().asInt();
            if (power < 0 || power > getMaxPower()) {
                Debug.echoError("Redstone power for material '" + material.getMaterial().name() + "' must be between 0 and " + getMaxPower() + "!");
                return;
            }
            getPowerable().setPower(power);
        }
    }
}
