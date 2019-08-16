package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.BrewingStand;

public class MaterialBrewingStandBottles implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof BrewingStand;
    }

    public static MaterialBrewingStandBottles getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialBrewingStandBottles((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "bottles", "has_bottles", "has_bottle", "max_bottles"
    };

    public static final String[] handledMechs = new String[] {
            "bottles"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialBrewingStandBottles(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private BrewingStand getStand() {
        return (BrewingStand) material.getModernData().data;
    }

    private boolean hasBottles() {
        return !getStand().getBottles().isEmpty();
    }

    private ListTag getBottles() {
        return new ListTag(getStand().getBottles());
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return hasBottles() ? getBottles().identify() : null;
    }

    @Override
    public String getPropertyId() {
        return "bottles";
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
        // @attribute <MaterialTag.max_bottles>
        // @returns ElementTag(Number)
        // @group properties
        // @description
        // Returns the maximum number of bottles that can be on this brewing stand material.
        // Currently, the maximum number of bottles a brewing stand can hold is 3.
        // -->
        if (attribute.startsWith("max_bottles")) {
            return new ElementTag(getStand().getMaximumBottles()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <MaterialTag.has_bottles>
        // @returns ElementTag(Boolean)
        // @mechanism MaterialTag.bottles
        // @group properties
        // @description
        // Returns whether this brewing stand material has any bottles in it.
        // -->
        if (attribute.startsWith("has_bottles")) {
            return new ElementTag(hasBottles()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <MaterialTag.has_bottle[<#>]>
        // @returns ElementTag(Boolean)
        // @mechanism MaterialTag.bottles
        // @group properties
        // @description
        // Returns whether this brewing stand material has the specified bottle.
        // The specified bottle can be either 1, 2, or 3.
        // -->
        if (attribute.startsWith("has_bottle") && attribute.hasContext(1)) {
            ElementTag context = new ElementTag(attribute.getContext(1));
            if (!context.isInt()) {
                Debug.echoError("The specified bottle must be 1, 2, or 3!");
                return null;
            }
            int bottle = context.asInt();
            if (bottle < 1 || bottle > 3) {
                Debug.echoError("The specified bottle must be 1, 2, or 3!");
                return null;
            }
            return new ElementTag(getStand().hasBottle(bottle - 1)).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <MaterialTag.bottles>
        // @returns ListTag(Number)
        // @mechanism MaterialTag.bottles
        // @group properties
        // @description
        // Returns the indexes of all bottles present on this brewing stand material.
        // -->
        if (attribute.startsWith("bottles")) {
            return new ListTag(getBottles()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name bottles
        // @input ListTag(Number)
        // @description
        // If the material is a brewing stand, sets which bottles are present on it.
        // The list must only contain the numbers 1, 2, and/or 3!
        // @tags
        // <MaterialTag.bottles>
        // <MaterialTag.has_bottle[<#>]>
        // <MaterialTag.has_bottles>
        // <MaterialTag.max_bottles>
        // -->
        if (mechanism.matches("bottles") && mechanism.requireObject(ListTag.class)) {
            boolean bottle1 = false;
            boolean bottle2 = false;
            boolean bottle3 = false;
            for (String input : mechanism.valueAsType(ListTag.class)) {
                ElementTag parsedInput = new ElementTag(input);
                if (parsedInput.isInt()) {
                    switch (parsedInput.asInt()) {
                        case 1:
                            bottle1 = true;
                            break;
                        case 2:
                            bottle2 = true;
                            break;
                        case 3:
                            bottle3 = true;
                            break;
                    }
                }
            }
            getStand().setBottle(0, bottle1);
            getStand().setBottle(1, bottle2);
            getStand().setBottle(2, bottle3);
        }
    }
}
