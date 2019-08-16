package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Stairs;

public class MaterialStairsShape implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Stairs;
    }

    public static MaterialStairsShape getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialStairsShape((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "stair_shape"
    };

    public static final String[] handledMechs = new String[] {
            "stair_shape"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialStairsShape(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Stairs getStairs() {
        return (Stairs) material.getModernData().data;
    }

    private String getShape() {
        return getStairs().getShape().name();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getStairs().getShape() != Stairs.Shape.STRAIGHT ? getShape() : null;
    }

    @Override
    public String getPropertyId() {
        return "stair_shape";
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
        // @attribute <MaterialTag.stair_shape>
        // @returns ElementTag
        // @mechanism MaterialTag.stair_shape
        // @group properties
        // @description
        // Returns the shape of the stairs material.
        // Can be any of: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/data/type/Stairs.Shape.html>
        // -->
        if (attribute.startsWith("stair_shape")) {
            return new ElementTag(getShape()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name stair_shape
        // @input ElementTag
        // @description
        // Sets the shape of the stairs material.
        // Can be any of: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/data/type/Stairs.Shape.html>
        // @tags
        // <MaterialTag.stair_shape>
        // -->
        if (mechanism.matches("stair_shape") && mechanism.requireEnum(false, Stairs.Shape.values())) {
            getStairs().setShape(Stairs.Shape.valueOf(mechanism.getValue().asString().toUpperCase()));
        }
    }
}
