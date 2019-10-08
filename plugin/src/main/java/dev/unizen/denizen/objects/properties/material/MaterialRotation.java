package dev.unizen.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Rotatable;

public class MaterialRotation implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Rotatable;
    }

    public static MaterialRotation getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialRotation((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "rotation"
    };

    public static final String[] handledMechs = new String[] {
            "rotation"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialRotation(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Rotatable getRotatable() {
        return (Rotatable) material.getModernData().data;
    }

    private String getRotation() {
        return getRotatable().getRotation().name();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getRotatable().getRotation() != BlockFace.SELF ? getRotation() : null;
    }

    @Override
    public String getPropertyId() {
        return "rotation";
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
        // @attribute <MaterialTag.rotation>
        // @returns Element
        // @mechanism MaterialTag.rotation
        // @group properties
        // @description
        // If the material is rotatable, returns the material's rotation.
        // The rotation can be one of: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/BlockFace.html>
        // -->
        if (attribute.startsWith("rotation")) {
            return new ElementTag(getRotation()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name rotation
        // @input ElementTag
        // @description
        // If the material is rotatable, set the material's rotation.
        // The rotation can be one of: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/BlockFace.html>
        // NOTE: The rotation should consist of only compass directions. Values like "DOWN" are not valid!
        // @tags
        // <MaterialTag.rotation>
        // -->
        if (mechanism.matches("rotation") && mechanism.requireEnum(false, BlockFace.values())) {
            BlockFace face = BlockFace.valueOf(mechanism.getValue().asString().toUpperCase());
            getRotatable().setRotation(face);
        }
    }
}
