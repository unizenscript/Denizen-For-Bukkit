package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Scaffolding;

public class MaterialScaffoldingDistanceToBottom implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Scaffolding;
    }

    public static MaterialScaffoldingDistanceToBottom getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialScaffoldingDistanceToBottom((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "distance_to_bottom", "max_distance_to_bottom"
    };

    public static final String[] handledMechs = new String[] {
            "distance_to_bottom"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialScaffoldingDistanceToBottom(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Scaffolding getScaffolding() {
        return (Scaffolding) material.getModernData().data;
    }

    private int getDistance() {
        return getScaffolding().getDistance();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return !getScaffolding().isBottom() ? String.valueOf(getDistance()) : null;
    }

    @Override
    public String getPropertyId() {
        return "distance_to_bottom";
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
        // @attribute <MaterialTag.max_distance_to_bottom>
        // @returns ElementTag(Boolean)
        // @group properties
        // @description
        // Returns the maximum distance the scaffolding material can have from a "bottom" scaffolding.
        // -->
        if (attribute.startsWith("distance_to_bottom")) {
            return new ElementTag(getDistance()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <MaterialTag.distance_to_bottom>
        // @returns ElementTag(Boolean)
        // @mechanism MaterialTag.distance_to_bottom
        // @group properties
        // @description
        // Returns how far the scaffolding material is from a "bottom" scaffolding.
        // -->
        if (attribute.startsWith("distance_to_bottom")) {
            return new ElementTag(getDistance()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name distance_to_bottom
        // @input ElementTag(Boolean)
        // @description
        // Sets how far the scaffolding material is from a "bottom" scaffolding.
        // @tags
        // <MaterialTag.distance_to_bottom>
        // -->
        if (mechanism.matches("distance_to_bottom") && mechanism.requireInteger()) {
            getScaffolding().setDistance(mechanism.getValue().asInt());
        }
    }
}
