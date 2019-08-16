package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Sapling;

public class MaterialSaplingStage implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Sapling;
    }

    public static MaterialSaplingStage getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialSaplingStage((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "growth_stage", "max_growth_stage"
    };

    public static final String[] handledMechs = new String[] {
            "growth_stage"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialSaplingStage(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Sapling getSapling() {
        return (Sapling) material.getModernData().data;
    }

    private int getStage() {
        return getSapling().getStage();
    }

    private int getMaxStage() {
        return getSapling().getMaximumStage();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getStage() != 0 ? String.valueOf(getStage()) : null;
    }

    @Override
    public String getPropertyId() {
        return "growth_stage";
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
        // @attribute <MaterialTag.max_growth_stage>
        // @returns ElementTag(Number)
        // @group properties
        // @description
        // Returns the maximum growth stage of the sapling or bamboo material.
        // Once the material's growth stage reaches this number, the material will attempt to grow into a tree as its next stage.
        // -->
        if (attribute.startsWith("max_growth_stage")) {
            return new ElementTag(getMaxStage()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <MaterialTag.growth_stage>
        // @returns ElementTag(Number)
        // @mechanism MaterialTag.growth_stage
        // @group properties
        // @description
        // Returns the growth stage of the sapling or bamboo material.
        // -->
        if (attribute.startsWith("growth_stage")) {
            return new ElementTag(getStage()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name growth_stage
        // @input ElementTag(Number)
        // @description
        // Sets the growth stage of the sapling or bamboo material.
        // @tags
        // <MaterialTag.growth_stage>
        // <MaterialTag.max_growth_stage>
        // -->
        if (mechanism.matches("growth_stage") && mechanism.requireInteger()) {
            int stage = mechanism.getValue().asInt();
            if (stage < 0 || stage > getMaxStage()) {
                Debug.echoError("The growth stage must be between '0' and '" + getMaxStage() + "'!");
                return;
            }
            getSapling().setStage(stage);
        }
    }
}
