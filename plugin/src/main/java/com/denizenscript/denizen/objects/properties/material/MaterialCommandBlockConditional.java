package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.CommandBlock;

public class MaterialCommandBlockConditional implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof CommandBlock;
    }

    public static MaterialCommandBlockConditional getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialCommandBlockConditional((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "conditional"
    };

    public static final String[] handledMechs = new String[] {
            "conditional"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialCommandBlockConditional(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private CommandBlock getCmdBlock() {
        return (CommandBlock) material.getModernData().data;
    }

    private boolean isConditional() {
        return getCmdBlock().isConditional();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return isConditional() ? "true" : null;
    }

    @Override
    public String getPropertyId() {
        return "conditional";
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
        // @attribute <MaterialTag.conditional>
        // @returns ElementTag(Boolean)
        // @mechanism MaterialTag.conditional
        // @group properties
        // @description
        // If the material is a command block, returns whether this command block is conditional.
        // -->
        if (attribute.startsWith("conditional")) {
            return new ElementTag(isConditional()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name conditional
        // @input ElementTag(Boolean)
        // @description
        // If the material is a command block, sets whether this material is conditional.
        // @tags
        // <MaterialTag.conditional>
        // -->
        if (mechanism.matches("conditional") && mechanism.requireBoolean()) {
            getCmdBlock().setConditional(mechanism.getValue().asBoolean());
        }
    }
}
