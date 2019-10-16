package dev.unizen.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.DaylightDetector;

public class MaterialDaylightDetectorInverted implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof DaylightDetector;
    }

    public static MaterialDaylightDetectorInverted getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialDaylightDetectorInverted((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "inverted"
    };

    public static final String[] handledMechs = new String[] {
            "inverted"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialDaylightDetectorInverted(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private DaylightDetector getDetector() {
        return (DaylightDetector) material.getModernData().data;
    }

    private boolean isInverted() {
        return getDetector().isInverted();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return isInverted() ? "true" : null;
    }

    @Override
    public String getPropertyId() {
        return "inverted";
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
        // @attribute <MaterialTag.inverted>
        // @returns ElementTag(Boolean)
        // @mechanism MaterialTag.inverted
        // @group properties
        // @description
        // Returns whether the daylight detector material is inverted.
        // If the material is inverted, it will activate in the absence of light.
        // Otherwise, it activates in the presence of light.
        // -->
        if (attribute.startsWith("inverted")) {
            return new ElementTag(isInverted()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name inverted
        // @input ElementTag(Boolean)
        // @description
        // Sets whether the daylight detector material is inverted.
        // If the material is inverted, it will activate in the absence of light.
        // Otherwise, it activates in the presence of light.
        // @tags
        // <MaterialTag.inverted>
        // -->
        if (mechanism.matches("inverted") && mechanism.requireBoolean()) {
            getDetector().setInverted(mechanism.getValue().asBoolean());
        }
    }
}
