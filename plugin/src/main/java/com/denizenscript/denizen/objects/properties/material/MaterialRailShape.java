package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.Rail;

import java.util.ArrayList;
import java.util.List;

public class MaterialRailShape implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Rail;
    }

    public static MaterialRailShape getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialRailShape((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "valid_rail_shapes", "rail_shape"
    };

    public static final String[] handledMechs = new String[] {
            "rail_shape"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialRailShape(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Rail getRail() {
        return (Rail) material.getModernData().data;
    }

    private List<String> getValidRails() {
        List<String> railShapes = new ArrayList<>();
        for (Rail.Shape shape : getRail().getShapes()) {
            railShapes.add(shape.name());
        }
        return railShapes;
    }

    private String getShape() {
        return getRail().getShape().name();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getShape();
    }

    @Override
    public String getPropertyId() {
        return "rail_shape";
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
        // @attribute <MaterialTag.valid_rail_shapes>
        // @returns ListTag
        // @group properties
        // @description
        // If the material is a rail, returns a list of all rail shapes that are applicable to this material.
        // A list of valid rail shapes can be found here: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/data/Rail.Shape.html>
        // -->
        if (attribute.startsWith("valid_rail_shapes")) {
            return new ListTag(getValidRails()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <MaterialTag.rail_shape>
        // @returns ElementTag
        // @mechanism MaterialTag.rail_shape
        // @group properties
        // @description
        // If the material is a rail, returns the material's rail shape.
        // A list of valid rail shapes can be found here: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/data/Rail.Shape.html>
        // -->
        if (attribute.startsWith("rail_shape")) {
            return new ElementTag(getRail().getShape().name()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name rail_shape
        // @input ElementTag
        // @description
        // If the material is a rail, sets the material's rail shape.
        // A list of valid rail shapes can be found here: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/data/Rail.Shape.html>
        // @tags
        // <MaterialTag.valid_rail_shapes>
        // <MaterialTag.rail_shape>
        // -->
        if (mechanism.matches("rail_shape") && mechanism.requireEnum(false, Rail.Shape.values())) {
            String value = mechanism.getValue().asString().toUpperCase();
            if (!getValidRails().contains(value)) {
                Debug.echoError("This shape is not applicable to this rail!");
                return;
            }
            getRail().setShape(Rail.Shape.valueOf(value));
        }
    }
}
