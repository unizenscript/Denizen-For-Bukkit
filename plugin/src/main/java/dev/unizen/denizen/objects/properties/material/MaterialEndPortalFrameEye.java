package dev.unizen.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.EndPortalFrame;

public class MaterialEndPortalFrameEye implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof EndPortalFrame;
    }

    public static MaterialEndPortalFrameEye getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialEndPortalFrameEye((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "has_eye"
    };

    public static final String[] handledMechs = new String[] {
            "has_eye"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialEndPortalFrameEye(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private EndPortalFrame getFrame() {
        return (EndPortalFrame) material.getModernData().data;
    }

    private boolean hasEye() {
        return getFrame().hasEye();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return hasEye() ? "true" : null;
    }

    @Override
    public String getPropertyId() {
        return "has_eye";
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
        // @attribute <MaterialTag.has_eye>
        // @returns ElementTag(Boolean)
        // @mechanism MaterialTag.has_eye
        // @group properties
        // @description
        // Returns whether this end portal frame material has an Eye of Ender in it.
        // -->
        if (attribute.startsWith("has_eye")) {
            return new ElementTag(hasEye()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name has_eye
        // @input ElementTag(Boolean)
        // @description
        // Sets whether this end portal frame material has an Eye of Ender in it.
        // @tags
        // <MaterialTag.has_eye>
        // -->
        if (mechanism.matches("has_eye") && mechanism.requireBoolean()) {
            getFrame().setEye(mechanism.getValue().asBoolean());
        }
    }
}
