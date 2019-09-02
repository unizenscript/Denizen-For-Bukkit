package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Chest;

public class MaterialChestType implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Chest;
    }

    public static MaterialChestType getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialChestType((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "chest_type"
    };

    public static final String[] handledMechs = new String[] {
            "chest_type"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialChestType(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Chest getChest() {
        return (Chest) material.getModernData().data;
    }

    private String getType() {
        return getChest().getType().name();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getChest().getType() != Chest.Type.SINGLE ? getType() : null;
    }

    @Override
    public String getPropertyId() {
        return "chest_type";
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
        // @attribute <MaterialTag.chest_type>
        // @returns ElementTag
        // @mechanism MaterialTag.chest_type
        // @group properties
        // @description
        // If the material is a chest, returns what kind of chest it is.
        // Can be SINGLE, LEFT, or RIGHT. If the type is LEFT or RIGHT, then the chest material is a half of a double chest.
        // -->
        if (attribute.startsWith("chest_type")) {
            return new ElementTag(getType()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name chest_type
        // @input ElementTag
        // @description
        // If the material is a chest, sets what kind of chest it is.
        // Can be SINGLE, LEFT, or RIGHT. If the type is LEFT or RIGHT, then the chest material is a half of a double chest.
        // @tags
        // <MaterialTag.chest_type>
        // -->
        if (mechanism.matches("chest_type") && mechanism.requireEnum(false, Chest.Type.values())) {
            getChest().setType(Chest.Type.valueOf(mechanism.getValue().asString().toUpperCase()));
        }
    }
}
