package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Comparator;

public class MaterialComparatorMode implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Comparator;
    }

    public static MaterialComparatorMode getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialComparatorMode((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "comparator_mode"
    };

    public static final String[] handledMechs = new String[] {
            "comparator_mode"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialComparatorMode(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Comparator getComparator() {
        return (Comparator) material.getModernData().data;
    }

    private String getMode() {
        return getComparator().getMode().name();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getComparator().getMode() != Comparator.Mode.COMPARE ? getMode() : null;
    }

    @Override
    public String getPropertyId() {
        return "comparator_mode";
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
        // @attribute <MaterialTag.comparator_mode>
        // @returns ElementTag
        // @mechanism MaterialTag.comparator_mode
        // @group properties
        // @description
        // If the material is a redstone comparator, returns the mode the material is in.
        // Can be either COMPARE or SUBTRACT.
        // -->
        if (attribute.startsWith("comparator_mode")) {
            return new ElementTag(getMode()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name comparator_mode
        // @input ElementTag
        // @description
        // If the material is a redstone comparator, sets the mode the material is in.
        // Can be either COMPARE or SUBTRACT.
        // @tags
        // <MaterialTag.comparator_mode>
        // -->
        if (mechanism.matches("comparator_mode") && mechanism.requireEnum(false, Comparator.Mode.values())) {
            getComparator().setMode(Comparator.Mode.valueOf(mechanism.getValue().asString().toUpperCase()));
        }
    }
}
