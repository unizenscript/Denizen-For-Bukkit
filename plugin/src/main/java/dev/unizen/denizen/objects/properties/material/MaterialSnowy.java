package dev.unizen.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.Snowable;

public class MaterialSnowy implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Snowable;
    }

    public static MaterialSnowy getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialSnowy((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "snowy"
    };

    public static final String[] handledMechs = new String[] {
            "snowy"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialSnowy(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Snowable getSnowable() {
        return (Snowable) material.getModernData().data;
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getSnowable().isSnowy() ? "true" : null;
    }

    @Override
    public String getPropertyId() {
        return "snowy";
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
        // @attribute <MaterialTag.snowy>
        // @returns ElementTag(Boolean)
        // @mechanism MaterialTag.snowy
        // @group properties
        // @description
        // Returns whether this material is covered with snow, if it has a special texture when snow is on top of it.
        // -->
        if (attribute.startsWith("snowy")) {
            return new ElementTag(getSnowable().isSnowy()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name snowy
        // @input ElementTag(Boolean)
        // @description
        // If the material has a special texture when snow is on top of it, sets whether this material will have that special texture.
        // @tags
        // <MaterialTag.snowy>
        // -->
        if (mechanism.matches("snowy") && mechanism.requireBoolean()) {
            getSnowable().setSnowy(mechanism.getValue().asBoolean());
        }
    }
}
