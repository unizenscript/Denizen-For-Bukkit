package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.Axis;
import org.bukkit.block.data.Orientable;

public class MaterialOrientation implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Orientable;
    }

    public static MaterialOrientation getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialOrientation((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "valid_orientations", "orientation"
    };

    public static final String[] handledMechs = new String[] {
            "orientation"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialOrientation(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Orientable getOrientable() {
        return (Orientable) material.getModernData().data;
    }

    private String getAxis() {
        return getOrientable().getAxis().name();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getAxis();
    }

    @Override
    public String getPropertyId() {
        return "orientation";
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
        // @attribute <MaterialTag.valid_orientations>
        // @returns ListTag
        // @group properties
        // @description
        // If the material can be oriented along an axis, returns the axes that the material can be oriented on.
        // Can be X, Y, and/or Z.
        // -->
        if (attribute.startsWith("valid_orientations")) {
            ListTag axes = new ListTag();
            for (Axis axis : getOrientable().getAxes()) {
                axes.add(axis.name());
            }
            return axes.getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <MaterialTag.orientation>
        // @returns ElementTag
        // @mechanism MaterialTag.orientation
        // @group properties
        // @description
        // If the material can be oriented along an axis, returns the axis on which the material is oriented.
        // Can be X, Y, or Z.
        // -->
        if (attribute.startsWith("orientation")) {
            return new ElementTag(getAxis()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name orientation
        // @input ElementTag
        // @description
        // If the material can be oriented along an axis, sets the axis on which the material is oriented on.
        // Can be X, Y, or Z.
        // @tags
        // <MaterialTag.valid_orientations>
        // <MaterialTag.orientation>
        // -->
        if (mechanism.matches("orientation") && mechanism.requireEnum(false, Axis.values())) {
            getOrientable().setAxis(Axis.valueOf(mechanism.getValue().asString().toUpperCase()));
        }
    }
}
