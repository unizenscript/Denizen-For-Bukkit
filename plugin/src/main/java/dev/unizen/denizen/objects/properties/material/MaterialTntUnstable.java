package dev.unizen.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.TNT;

public class MaterialTntUnstable implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof TNT;
    }

    public static MaterialTntUnstable getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialTntUnstable((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "unstable"
    };

    public static final String[] handledMechs = new String[] {
            "unstable"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialTntUnstable(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private TNT getTnt() {
        return (TNT) material.getModernData().data;
    }

    private boolean isUnstable() {
        return getTnt().isUnstable();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return isUnstable() ? "true" : null;
    }

    @Override
    public String getPropertyId() {
        return "unstable";
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
        // @attribute <MaterialTag.unstable>
        // @returns ElementTag(Boolean)
        // @mechanism MaterialTag.unstable
        // @group properties
        // @description
        // Returns whether the TNT material is unstable.
        // If true, then punching the material will cause it to explode.
        // -->
        if (attribute.startsWith("unstable")) {
            return new ElementTag(isUnstable()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name unstable
        // @input ElementTag(Boolean)
        // @description
        // Sets whether the TNT material is unstable.
        // If true, then punching the material will cause it to explode.
        // @tags
        // <MaterialTag.unstable>
        // -->
        if (mechanism.matches("unstable") && mechanism.requireBoolean()) {
            getTnt().setUnstable(mechanism.getValue().asBoolean());
        }
    }
}
