package dev.unizen.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.TechnicalPiston;

public class MaterialPistonType implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof TechnicalPiston;
    }

    public static MaterialPistonType getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialPistonType((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "piston_type"
    };

    public static final String[] handledMechs = new String[] {
            "piston_type"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialPistonType(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private TechnicalPiston getPiston() {
        return (TechnicalPiston) material.getModernData().data;
    }

    private String getType() {
        return getPiston().getType().name();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getPiston().getType() != TechnicalPiston.Type.NORMAL ? getType() : null;
    }

    @Override
    public String getPropertyId() {
        return "piston_type";
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
        // @attribute <MaterialTag.piston_type>
        // @returns ElementTag
        // @mechanism MaterialTag.piston_type
        // @group properties
        // @description
        // Returns the kind of piston this piston block is.
        // Can be either NORMAL or STICKY.
        // -->
        if (attribute.startsWith("piston_type")) {
            return new ElementTag(getType()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name piston_type
        // @input ElementTag
        // @description
        // Sets the kind of piston this piston block is.
        // Can be either NORMAL or STICKY.
        // @tags
        // <MaterialTag.piston_type>
        // -->
        if (mechanism.matches("piston_type") && mechanism.requireEnum(false, TechnicalPiston.Type.values())) {
            getPiston().setType(TechnicalPiston.Type.valueOf(mechanism.getValue().asString().toUpperCase()));
        }
    }
}
