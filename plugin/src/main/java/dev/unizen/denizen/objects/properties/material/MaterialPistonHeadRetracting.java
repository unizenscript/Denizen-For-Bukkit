package dev.unizen.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.PistonHead;

public class MaterialPistonHeadRetracting implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof PistonHead;
    }

    public static MaterialPistonHeadRetracting getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialPistonHeadRetracting((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "retracting"
    };

    public static final String[] handledMechs = new String[] {
            "retracting"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialPistonHeadRetracting(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private PistonHead getPistonHead() {
        return (PistonHead) material.getModernData().data;
    }

    private boolean isShort() {
        return getPistonHead().isShort();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return isShort() ? "true" : null;
    }

    @Override
    public String getPropertyId() {
        return "retracting";
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
        // @attribute <MaterialTag.retracting>
        // @returns ElementTag(Boolean)
        // @mechanism MaterialTag.retracting
        // @group properties
        // @description
        // Returns whether this piston head is currently retracting.
        // -->
        if (attribute.startsWith("retracting")) {
            return new ElementTag(isShort()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name retracting
        // @input ElementTag(Boolean)
        // @description
        // Sets whether this piston head is currently retracting.
        // @tags
        // <MaterialTag.retracting>
        // -->
        if (mechanism.matches("retracting") && mechanism.requireBoolean()) {
            getPistonHead().setShort(mechanism.getValue().asBoolean());
        }
    }
}
