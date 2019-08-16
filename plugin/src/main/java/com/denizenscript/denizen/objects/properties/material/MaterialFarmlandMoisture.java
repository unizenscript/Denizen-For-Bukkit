package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Farmland;

public class MaterialFarmlandMoisture implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Farmland;
    }

    public static MaterialFarmlandMoisture getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialFarmlandMoisture((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "moisture", "max_moisture"
    };

    public static final String[] handledMechs = new String[] {
            "moisture"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialFarmlandMoisture(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Farmland getFarmland() {
        return (Farmland) material.getModernData().data;
    }

    private int getMoisture() {
        return getFarmland().getMoisture();
    }

    private int getMaxMoisture() {
        return getFarmland().getMoisture();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getMoisture() != 0 ? String.valueOf(getMoisture()) : null;
    }

    @Override
    public String getPropertyId() {
        return "moisture";
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
        // @attribute <MaterialTag.max_moisture>
        // @returns ElementTag(Number)
        // @group properties
        // @description
        // Returns the maximum moisture level of the farmland material.
        // -->
        if (attribute.startsWith("max_moisture")) {
            return new ElementTag(getMaxMoisture()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <MaterialTag.moisture>
        // @returns ElementTag(Number)
        // @mechanism MaterialTag.moisture
        // @group properties
        // @description
        // Returns the moisture level of the farmland material.
        // -->
        if (attribute.startsWith("moisture")) {
            return new ElementTag(getMoisture()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name moisture
        // @input ElementTag(Number)
        // @description
        // Sets the moisture level of the farmland material.
        // Cannot be higher than the max moisture level.
        // @tags
        // <MaterialTag.moisture>
        // <MaterialTag.max_moisture>
        // -->
        if (mechanism.matches("moisture") && mechanism.requireInteger()) {
            int moisture = mechanism.getValue().asInt();
            if (moisture < 0 || moisture > getMaxMoisture()) {
                Debug.echoError("The moisture level must be between '0' and '" + getMaxMoisture() + "'!");
                return;
            }
            getFarmland().setMoisture(moisture);
        }
    }
}
