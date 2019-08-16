package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Bamboo;

public class MaterialBambooLeaves implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Bamboo;
    }

    public static MaterialBambooLeaves getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialBambooLeaves((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "bamboo_leaves"
    };

    public static final String[] handledMechs = new String[] {
            "bamboo_leaves"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialBambooLeaves(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Bamboo getBamboo() {
        return (Bamboo) material.getModernData().data;
    }

    private String getLeaves() {
        return getBamboo().getLeaves().name();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getBamboo().getLeaves() != Bamboo.Leaves.NONE ? getLeaves() : null;
    }

    @Override
    public String getPropertyId() {
        return "bamboo_leaves";
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
        // @attribute <MaterialTag.bamboo_leaves>
        // @returns ElementTag
        // @mechanism MaterialTag.bamboo_leaves
        // @group properties
        // @description
        // Returns the size of the leaves on this material, if the material is a bamboo block.
        // -->
        if (attribute.startsWith("bamboo_leaves")) {
            return new ElementTag(getLeaves()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name bamboo_leaves
        // @input ElementTag
        // @description
        // Sets the size of the leaves on this bamboo material.
        // Can be any of: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/data/type/Bamboo.Leaves.html>
        // @tags
        // <MaterialTag.bamboo_leaves>
        // -->
        if (mechanism.matches("bamboo_leaves") && mechanism.requireEnum(false, Bamboo.Leaves.values())) {
            getBamboo().setLeaves(Bamboo.Leaves.valueOf(mechanism.getValue().asString().toUpperCase()));
        }
    }
}
