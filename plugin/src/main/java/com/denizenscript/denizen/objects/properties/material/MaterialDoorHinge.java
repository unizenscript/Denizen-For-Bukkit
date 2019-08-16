package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Door;

public class MaterialDoorHinge implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Door;
    }

    public static MaterialDoorHinge getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialDoorHinge((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "hinge"
    };

    public static final String[] handledMechs = new String[] {
            "hinge"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialDoorHinge(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Door getDoor() {
        return (Door) material.getModernData().data;
    }

    private String getHinge() {
        return getDoor().getHinge().name();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getDoor().getHinge() != Door.Hinge.LEFT ? getHinge() : null;
    }

    @Override
    public String getPropertyId() {
        return "hinge";
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
        // @attribute <MaterialTag.hinge>
        // @returns ElementTag
        // @mechanism MaterialTag.hinge
        // @group properties
        // @description
        // Returns which hinge the door material is using.
        // Can be either LEFT or RIGHT.
        // -->
        if (attribute.startsWith("hinge")) {
            return new ElementTag(getHinge()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name hinge
        // @input ElementTag
        // @description
        // Sets the hinge the door material is using.
        // Can be either LEFT or RIGHT.
        // @tags
        // <MaterialTag.hinge>
        // -->
        if (mechanism.matches("hinge") && mechanism.requireEnum(false, Door.Hinge.values())) {
            getDoor().setHinge(Door.Hinge.valueOf(mechanism.getValue().asString().toUpperCase()));
        }
    }
}
