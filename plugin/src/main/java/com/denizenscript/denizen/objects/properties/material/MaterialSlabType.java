package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Slab;

public class MaterialSlabType implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Slab;
    }

    public static MaterialSlabType getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialSlabType((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "slab_type"
    };

    public static final String[] handledMechs = new String[] {
            "slab_type"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialSlabType(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Slab getSlab() {
        return (Slab) material.getModernData().data;
    }

    private String getType() {
        return getSlab().getType().name();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getSlab().getType() != Slab.Type.BOTTOM ? getType() : null;
    }

    @Override
    public String getPropertyId() {
        return "slab_type";
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
        // @attribute <MaterialTag.slab_type>
        // @returns ElementTag
        // @mechanism MaterialTag.slab_type
        // @group properties
        // @description
        // Returns the type of the slab material.
        // Can be BOTTOM, TOP, or DOUBLE.
        // -->
        if (attribute.startsWith("slab_type")) {
            return new ElementTag(getType()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name slab_type
        // @input ElementTag
        // @description
        // Sets the type of the slab material.
        // Can be BOTTOM, TOP, or DOUBLE.
        // @tags
        // <MaterialTag.slab_type>
        // -->
        if (mechanism.matches("slab_type") && mechanism.requireEnum(false, Slab.Type.values())) {
            getSlab().setType(Slab.Type.valueOf(mechanism.getValue().asString().toUpperCase()));
        }
    }
}
