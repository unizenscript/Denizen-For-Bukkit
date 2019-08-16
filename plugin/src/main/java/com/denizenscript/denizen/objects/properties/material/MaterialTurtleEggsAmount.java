package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.TurtleEgg;

public class MaterialTurtleEggsAmount implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof TurtleEgg;
    }

    public static MaterialTurtleEggsAmount getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialTurtleEggsAmount((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "eggs"
    };

    public static final String[] handledMechs = new String[] {
            "eggs"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialTurtleEggsAmount(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private TurtleEgg getTurtleEgg() {
        return (TurtleEgg) material.getModernData().data;
    }

    private int getAmount() {
        return getTurtleEgg().getEggs();
    }

    private int getMinAmount() {
        return getTurtleEgg().getMinimumEggs();
    }

    private int getMaxAmount() {
        return getTurtleEgg().getMaximumEggs();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getAmount() != getMinAmount() ? String.valueOf(getAmount()) : null;
    }

    @Override
    public String getPropertyId() {
        return "eggs";
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
        // @attribute <MaterialTag.min_eggs>
        // @returns ElementTag(Number)
        // @group properties
        // @description
        // Returns the minimum number of eggs that the turtle egg material can have.
        // -->
        if (attribute.startsWith("min_eggs")) {
            return new ElementTag(getMinAmount()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <MaterialTag.max_eggs>
        // @returns ElementTag(Number)
        // @group properties
        // @description
        // Returns the maximum number of eggs that the turtle egg material can have.
        // -->
        if (attribute.startsWith("max_eggs")) {
            return new ElementTag(getMaxAmount()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <MaterialTag.eggs>
        // @returns ElementTag(Number)
        // @mechanism MaterialTag.eggs
        // @group properties
        // @description
        // Returns the number of eggs that the turtle egg material has.
        // -->
        if (attribute.startsWith("eggs")) {
            return new ElementTag(getAmount()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name eggs
        // @input ElementTag(Number)
        // @description
        // Sets how many eggs this turtle egg material has.
        // @tags
        // <MaterialTag.eggs>
        // <MaterialTag.min_eggs>
        // <MaterialTag.max_eggs>
        // -->
        if (mechanism.matches("eggs") && mechanism.requireInteger()) {
            int eggs = mechanism.getValue().asInt();
            if (eggs < getMinAmount() || eggs > getMaxAmount()) {
                Debug.echoError("The egg count must be between '" + getMinAmount() + "' and '" + getMaxAmount() + "'!");
                return;
            }
            getTurtleEgg().setEggs(eggs);
        }
    }
}
