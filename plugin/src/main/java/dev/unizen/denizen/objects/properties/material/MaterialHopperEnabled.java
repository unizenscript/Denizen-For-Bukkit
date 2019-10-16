package dev.unizen.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Hopper;

public class MaterialHopperEnabled implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Hopper;
    }

    public static MaterialHopperEnabled getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialHopperEnabled((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "enabled"
    };

    public static final String[] handledMechs = new String[] {
            "enabled"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialHopperEnabled(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Hopper getHopper() {
        return (Hopper) material.getModernData().data;
    }

    private boolean isEnabled() {
        return getHopper().isEnabled();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        // The default state of a hopper is being enabled.
        return !isEnabled() ? "false" : null;
    }

    @Override
    public String getPropertyId() {
        return "enabled";
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
        // @attribute <MaterialTag.enabled>
        // @returns ElementTag(Boolean)
        // @mechanism MaterialTag.enabled
        // @group properties
        // @description
        // Returns whether this hopper material is enabled.
        // NOTE: A hopper is enabled when it is not receiving power!
        // -->
        if (attribute.startsWith("enabled")) {
            return new ElementTag(isEnabled()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name enabled
        // @input ElementTag(Boolean)
        // @description
        // Sets whether this hopper material is enabled.
        // NOTE: A hopper is enabled when it is not receiving power!
        // @tags
        // <MaterialTag.enabled>
        // -->
        if (mechanism.matches("enabled") && mechanism.requireBoolean()) {
            getHopper().setEnabled(mechanism.getValue().asBoolean());
        }
    }
}
