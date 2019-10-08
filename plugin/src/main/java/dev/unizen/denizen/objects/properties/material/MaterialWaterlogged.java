package dev.unizen.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.Waterlogged;

public class MaterialWaterlogged implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Waterlogged;
    }

    public static MaterialWaterlogged getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialWaterlogged((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "waterlogged"
    };

    public static final String[] handledMechs = new String[] {
            "waterlogged"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialWaterlogged(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Waterlogged getWaterlogged() {
        return (Waterlogged) material.getModernData().data;
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getWaterlogged().isWaterlogged() ? "true" : null;
    }

    @Override
    public String getPropertyId() {
        return "waterlogged";
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
        // @attribute <MaterialTag.waterlogged>
        // @returns ElementTag(Boolean)
        // @mechanism MaterialTag.waterlogged
        // @group properties
        // @description
        // Returns whether the material is waterlogged, if it can be waterlogged.
        // -->
        if (attribute.startsWith("waterlogged")) {
            return new ElementTag(getWaterlogged().isWaterlogged()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name waterlogged
        // @input ElementTag(Boolean)
        // @description
        // If the material is waterloggable, sets whether the material is waterlogged.
        // @tags
        // <MaterialTag.waterlogged>
        // -->
        if (mechanism.matches("waterlogged") && mechanism.requireBoolean()) {
            getWaterlogged().setWaterlogged(mechanism.getValue().asBoolean());
        }
    }
}
