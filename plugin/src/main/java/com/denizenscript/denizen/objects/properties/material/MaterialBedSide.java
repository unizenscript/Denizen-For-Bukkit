package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Bed;

public class MaterialBedSide implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData() instanceof Bed;
    }

    public static MaterialBedSide getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialBedSide((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "bed_side", "is_occupied"
    };

    public static final String[] handledMechs = new String[] {
            "bed_side"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialBedSide(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Bed getBed() {
        return (Bed) material.getModernData().data;
    }

    private String getSide() {
        return getBed().getPart().name();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getSide();
    }

    @Override
    public String getPropertyId() {
        return "bed_side";
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
        // @attribute <MaterialTag.bed_side>
        // @returns ElementTag
        // @mechanism MaterialTag.bed_side
        // @group properties
        // @description
        // If the material is a bed, returns which side of the bed it is.
        // Can be either HEAD or FOOT.
        // -->
        if (attribute.startsWith("bed_side")) {
            return new ElementTag(getSide()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <MaterialTag.is_occupied>
        // @returns ElementTag(Boolean)
        // @group properties
        // @description
        // If the material is a bed, returns whether it is occupied.
        // -->
        if (attribute.startsWith("is_occupied")) {
            return new ElementTag(getBed().isOccupied()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name bed_side
        // @input ElementTag
        // @description
        // If the material is a bed, sets which side of the bed the material is.
        // @tags
        // <MaterialTag.bed_side>
        // -->
        if (mechanism.matches("bed_side") && mechanism.requireEnum(false, Bed.Part.values())) {
            getBed().setPart(Bed.Part.valueOf(mechanism.getValue().asString().toUpperCase()));
        }
    }
}
