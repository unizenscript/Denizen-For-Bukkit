package dev.unizen.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Tripwire;

public class MaterialTripwireDisarmed implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Tripwire;
    }

    public static MaterialTripwireDisarmed getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialTripwireDisarmed((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "disarmed"
    };

    public static final String[] handledMechs = new String[] {
            "disarmed"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialTripwireDisarmed(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Tripwire getTripwire() {
        return (Tripwire) material.getModernData().data;
    }

    private boolean isDisarmed() {
        return getTripwire().isDisarmed();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        // The natural state of a tripwire string is disarmed.
        return !isDisarmed() ? "false" : null;
    }

    @Override
    public String getPropertyId() {
        return "disarmed";
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
        // @attribute <MaterialTag.disarmed>
        // @returns ElementTag(Boolean)
        // @mechanism MaterialTag.disarmed
        // @group properties
        // @description
        // Returns whether this tripwire material is disarmed.
        // If true, then breaking the material will not produce a current.
        // -->
        if (attribute.startsWith("disarmed")) {
            return new ElementTag(isDisarmed()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name disarmed
        // @input ElementTag(Boolean)
        // @description
        // Sets whether this tripwire material is disarmed.
        // If true, then breaking the material will not produce a current.
        // @tags
        // <MaterialTag.disarmed>
        // -->
        if (mechanism.matches("disarmed") && mechanism.requireBoolean()) {
            getTripwire().setDisarmed(mechanism.getValue().asBoolean());
        }
    }
}
