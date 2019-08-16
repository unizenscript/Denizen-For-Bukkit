package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.SeaPickle;

public class MaterialSeaPickles implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof SeaPickle;
    }

    public static MaterialSeaPickles getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialSeaPickles((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "pickles", "min_pickles", "max_pickles"
    };

    public static final String[] handledMechs = new String[] {
            "pickles"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialSeaPickles(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private SeaPickle getSeaPickle() {
        return (SeaPickle) material.getModernData().data;
    }

    private int getPickles() {
        return getSeaPickle().getPickles();
    }

    private int getMinPickles() {
        return getSeaPickle().getMinimumPickles();
    }

    private int getMaxPickles() {
        return getSeaPickle().getMaximumPickles();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getPickles() != getMinPickles() ? String.valueOf(getPickles()) : null;
    }

    @Override
    public String getPropertyId() {
        return "pickles";
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
        // @attribute <MaterialTag.min_pickles>
        // @returns ElementTag(Number)
        // @group properties
        // @description
        // Returns the maximum number of pickles the sea pickle material can have.
        // -->
        if (attribute.startsWith("min_pickles")) {
            return new ElementTag(getMinPickles()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <MaterialTag.max_pickles>
        // @returns ElementTag(Number)
        // @group properties
        // @description
        // Returns the minimum number of pickles the sea pickle material can have.
        // -->
        if (attribute.startsWith("max_pickles")) {
            return new ElementTag(getMaxPickles()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <MaterialTag.pickles>
        // @returns ElementTag(Number)
        // @mechanism MaterialTag.pickles
        // @group properties
        // @description
        // Returns how many pickles the sea pickle material has.
        // -->
        if (attribute.startsWith("pickles")) {
            return new ElementTag(getPickles()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name pickles
        // @input ElementTag(Number)
        // @description
        // Sets how many pickles the sea pickle material has.
        // @tags
        // <MaterialTag.pickles>
        // <MaterialTag.min_pickles>
        // <MaterialTag.max_pickles>
        // -->
        if (mechanism.matches("pickles") && mechanism.requireInteger()) {
            int pickles = mechanism.getValue().asInt();
            if (pickles < getMinPickles() || pickles > getMaxPickles()) {
                Debug.echoError("The number of pickles must be between '" + getMinPickles() + "' and '" + getMaxPickles() + "'!");
                return;
            }
            getSeaPickle().setPickles(pickles);
        }
    }
}
