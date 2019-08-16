package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.TurtleEgg;

public class MaterialTurtleEggsHatch implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof TurtleEgg;
    }

    public static MaterialTurtleEggsHatch getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialTurtleEggsHatch((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "hatch", "min_hatch", "max_hatch"
    };

    public static final String[] handledMechs = new String[] {
            "hatch"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialTurtleEggsHatch(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private TurtleEgg getTurtleEgg() {
        return (TurtleEgg) material.getModernData().data;
    }

    private int getHatch() {
        return getTurtleEgg().getHatch();
    }

    private int getMinHatch() {
        return getTurtleEgg().getMinimumEggs();
    }

    private int getMaxHatch() {
        return getTurtleEgg().getMaximumHatch();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getHatch() != getMinHatch() ? String.valueOf(getHatch()) : null;
    }

    @Override
    public String getPropertyId() {
        return "hatch";
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
        // @attribute <MaterialTag.min_hatch>
        // @returns ElementTag(Number)
        // @group properties
        // @description
        // Returns the minimum value that the hatch property of the turtle egg material may have.
        // -->
        if (attribute.startsWith("min_hatch")) {
            return new ElementTag(getMinHatch()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <MaterialTag.max_hatch>
        // @returns ElementTag(Number)
        // @group properties
        // @description
        // Returns the maximum value that the hatch property of the turtle egg material may have.
        // -->
        if (attribute.startsWith("max_hatch")) {
            return new ElementTag(getMaxHatch()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <MaterialTag.hatch>
        // @returns ElementTag(Number)
        // @mechanism MaterialTag.hatch
        // @group properties
        // @description
        // Returns the value of the hatch property of the turtle egg material.
        // -->
        if (attribute.startsWith("hatch")) {
            return new ElementTag(getHatch()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name hatch
        // @input ElementTag(Number)
        // @description
        // Sets the value of the hatch property of the turtle egg material.
        // When the hatch value is at the maximum allowed value, the turtle egg will be very close to hatching.
        // @tags
        // <MaterialTag.hatch>
        // <MaterialTag.min_hatch>
        // <MaterialTag.max_hatch>
        // -->
        if (mechanism.matches("hatch") && mechanism.requireInteger()) {
            int hatch = mechanism.getValue().asInt();
            if (hatch < getMinHatch() || hatch > getMinHatch()) {
                Debug.echoError("The value of the hatch property must be between '" + getMinHatch() + "' and '" + getMaxHatch() + "'!");
                return;
            }
            getTurtleEgg().setHatch(hatch);
        }
    }
}
