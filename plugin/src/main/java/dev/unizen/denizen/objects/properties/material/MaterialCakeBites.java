package dev.unizen.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Cake;

public class MaterialCakeBites implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Cake;
    }

    public static MaterialCakeBites getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialCakeBites((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "bites", "max_bites"
    };

    public static final String[] handledMechs = new String[] {
            "bites"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialCakeBites(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Cake getCake() {
        return (Cake) material.getModernData().data;
    }

    private int getBites() {
        return getCake().getBites();
    }

    private int getMaxBites() {
        return getCake().getMaximumBites();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getBites() != 0 ? String.valueOf(getBites()) : null;
    }

    @Override
    public String getPropertyId() {
        return "bites";
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
        // @attribute <MaterialTag.max_bites>
        // @returns ElementTag(Number)
        // @group properties
        // @description
        // If the material is a cake, returns the maximum number of bites the material can have.
        // -->
        if (attribute.startsWith("max_bites") || attribute.startsWith("maximum_bites")) {
            return new ElementTag(getMaxBites()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <MaterialTag.bites>
        // @returns ElementTag(Number)
        // @mechanism MaterialTag.bites
        // @group properties
        // @description
        // If the material is a cake, returns the number of bites the material has.
        // 0 bites means the cake is untouched.
        // -->
        if (attribute.startsWith("bites")) {
            return new ElementTag(getBites()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name bites
        // @input ElementTag(Number)
        // @description
        // If the material is a cake, sets the number of bites the material has.
        // 0 means the material is untouched.
        // @tags
        // <MaterialTag.bites>
        // <MaterialTag.max_bites>
        // -->
        if (mechanism.matches("bites") && mechanism.requireInteger()) {
            int value = mechanism.getValue().asInt();
            if (value < 0 || value > getMaxBites()) {
                Debug.echoError("The number of cake bites must be between 0 and " + getMaxBites() + "!");
                return;
            }
            getCake().setBites(value);
        }
    }
}
