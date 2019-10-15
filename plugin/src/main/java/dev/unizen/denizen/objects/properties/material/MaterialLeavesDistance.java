package dev.unizen.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Leaves;

public class MaterialLeavesDistance implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Leaves;
    }

    public static MaterialLeavesDistance getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialLeavesDistance((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "distance"
    };

    public static final String[] handledMechs = new String[] {
            "distance"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialLeavesDistance(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Leaves getLeaves() {
        return (Leaves) material.getModernData().data;
    }

    private int getDistance() {
        return getLeaves().getDistance();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getDistance() != 0 ? String.valueOf(getDistance()) : null;
    }

    @Override
    public String getPropertyId() {
        return "distance";
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
        // @attribute <MaterialTag.distance>
        // @returns ElementTag(Number)
        // @mechanism MaterialTag.distance
        // @group properties
        // @description
        // Returns how far the leaves material is from a tree.
        // Used together with <@link tag MaterialTag.persistent> to determine if the leaves will decay.
        // -->
        if (attribute.startsWith("distance")) {
            return new ElementTag(getDistance()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name distance
        // @input ElementTag(Number)
        // @description
        // Sets how far the leaves material is from a tree.
        // Used together with <@link mechanism MaterialTag.persistent> to determine if the leaves will decay.
        // @tags
        // <MaterialTag.distance>
        // -->
        if (mechanism.matches("distance") && mechanism.requireInteger()) {
            getLeaves().setDistance(mechanism.getValue().asInt());
        }
    }
}
