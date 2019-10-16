package dev.unizen.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Dispenser;

public class MaterialDispenserTriggered implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Dispenser;
    }

    public static MaterialDispenserTriggered getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialDispenserTriggered((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "triggered"
    };

    public static final String[] handledMechs = new String[] {
            "triggered"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialDispenserTriggered(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Dispenser getDispenser() {
        return (Dispenser) material.getModernData().data;
    }

    private boolean triggered() {
        return getDispenser().isTriggered();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return triggered() ? "true" : null;
    }

    @Override
    public String getPropertyId() {
        return "triggered";
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
        // @attribute <MaterialTag.triggered>
        // @returns ElementTag(Boolean)
        // @mechanism MaterialTag.triggered
        // @group properties
        // @description
        // Returns whether the dispenser material is triggered.
        // -->
        if (attribute.startsWith("triggered")) {
            return new ElementTag(triggered()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name triggered
        // @input ElementTag(Boolean)
        // @description
        // Sets whether the dispenser material is triggered.
        // @tags
        // <MaterialTag.triggered>
        // -->
        if (mechanism.matches("triggered") && mechanism.requireBoolean()) {
            getDispenser().setTriggered(mechanism.getValue().asBoolean());
        }
    }
}
