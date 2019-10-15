package dev.unizen.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Gate;

public class MaterialGateInStoneWall implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Gate;
    }

    public static MaterialGateInStoneWall getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialGateInStoneWall((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "in_stone_wall"
    };

    public static final String[] handledMechs = new String[] {
            "in_stone_wall"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialGateInStoneWall(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Gate getGate() {
        return (Gate) material.getModernData().data;
    }

    private boolean isInWall() {
        return getGate().isInWall();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return isInWall() ? "true" : null;
    }

    @Override
    public String getPropertyId() {
        return "in_stone_wall";
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
        // @attribute <MaterialTag.in_stone_wall>
        // @returns ElementTag(Boolean)
        // @mechanism MaterialTag.in_stone_wall
        // @group properties
        // @description
        // Returns whether this fence gate material is attached to a stone wall.
        // -->
        if (attribute.startsWith("in_stone_wall")) {
            return new ElementTag(isInWall()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name in_stone_wall
        // @input ElementTag(Boolean)
        // @description
        // Sets whether this fence gate material is attached to a stone wall.
        // If true, then the material's texture is lowered.
        // @tags
        // <MaterialTag.in_stone_wall>
        // -->
        if (mechanism.matches("in_stone_wall") && mechanism.requireBoolean()) {
            getGate().setInWall(mechanism.getValue().asBoolean());
        }
    }
}
