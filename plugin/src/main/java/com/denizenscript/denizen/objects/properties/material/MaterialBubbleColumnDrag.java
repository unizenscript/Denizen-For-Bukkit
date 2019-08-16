package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.BubbleColumn;

public class MaterialBubbleColumnDrag implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof BubbleColumn;
    }

    public static MaterialBubbleColumnDrag getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialBubbleColumnDrag((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "drags_down"
    };

    public static final String[] handledMechs = new String[] {
            "drags_down"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialBubbleColumnDrag(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private BubbleColumn getBubbleColumn() {
        return (BubbleColumn) material.getModernData().data;
    }

    private boolean isDragging() {
        return getBubbleColumn().isDrag();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return !isDragging() ? "false" : null;
    }

    @Override
    public String getPropertyId() {
        return "drags_down";
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
        // @attribute <MaterialTag.drags_down>
        // @returns ElementTag(Boolean)
        // @mechanism MaterialTag.drags_down
        // @group properties
        // @description
        // Returns whether this bubble column material is dragging the player down.
        // If false, then the material is pushing the player up.
        // -->
        if (attribute.startsWith("drags_down")) {
            return new ElementTag(isDragging()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name drags_down
        // @input ElementTag(Boolean)
        // @description
        // If the material is a bubble column, sets whether the material is dragging the player down.
        // If false, then the material is pushing the player up.
        // @tags
        // <MaterialTag.drags_down>
        // -->
        if (mechanism.matches("drags_down") && mechanism.requireBoolean()) {
            getBubbleColumn().setDrag(mechanism.getValue().asBoolean());
        }
    }
}
