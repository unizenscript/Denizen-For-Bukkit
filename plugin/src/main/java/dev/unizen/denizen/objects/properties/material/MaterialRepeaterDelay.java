package dev.unizen.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Repeater;

public class MaterialRepeaterDelay implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Repeater;
    }

    public static MaterialRepeaterDelay getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialRepeaterDelay((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "delay", "max_delay", "min_delay"
    };

    public static final String[] handledMechs = new String[] {
            "delay"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialRepeaterDelay(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Repeater getRepeater() {
        return (Repeater) material.getModernData().data;
    }

    private int getDelay() {
        return getRepeater().getDelay();
    }

    private int getMaxDelay() {
        return getRepeater().getMaximumDelay();
    }

    private int getMinDelay() {
        return getRepeater().getMinimumDelay();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getDelay() != getMinDelay() ? String.valueOf(getDelay()) : null;
    }

    @Override
    public String getPropertyId() {
        return "delay";
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
        // @attribute <MaterialTag.max_delay>
        // @returns ElementTag(Number)
        // @group properties
        // @description
        // Returns the maximum delay the redstone repeater material can have.
        // -->
        if (attribute.startsWith("max_delay")) {
            return new ElementTag(getMaxDelay()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <MaterialTag.min_delay>
        // @returns ElementTag(Number)
        // @group properties
        // @description
        // Returns the minimum delay the redstone repeater material can have.
        // -->
        if (attribute.startsWith("min_delay")) {
            return new ElementTag(getMinDelay()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <MaterialTag.delay>
        // @returns ElementTag(Number)
        // @mechanism MaterialTag.delay
        // @group properties
        // @description
        // Returns the delay the redstone repeater material currently has.
        // -->
        if (attribute.startsWith("delay")) {
            return new ElementTag(getDelay()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name delay
        // @input ElementTag(Number)
        // @description
        // Sets the delay of the redstone repeater material.
        // @tags
        // <MaterialTag.delay>
        // <MaterialTag.min_delay>
        // <MaterialTag.max_delay>
        // -->
        if (mechanism.matches("delay") && mechanism.requireInteger()) {
            int delay = mechanism.getValue().asInt();
            if (delay < getMinDelay() || delay > getMaxDelay()) {
                Debug.echoError("Invalid delay specified! Must be between '" + getMinDelay() + "' and '" + getMaxDelay() + "'!");
                return;
            }
            getRepeater().setDelay(delay);
        }
    }
}
