package com.denizenscript.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Bell;

public class MaterialBellAttachment implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Bell;
    }

    public static MaterialBellAttachment getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialBellAttachment((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "bell_attachment"
    };

    public static final String[] handledMechs = new String[] {
            "bell_attachment"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialBellAttachment(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Bell getBell() {
        return (Bell) material.getModernData().data;
    }

    private String getAttachment() {
        return getBell().getAttachment().name();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getBell().getAttachment() != Bell.Attachment.FLOOR ? getAttachment() : null;
    }

    @Override
    public String getPropertyId() {
        return "bell_attachment";
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
        // @attribute <MaterialTag.attachment>
        // @returns ElementTag
        // @mechanism MaterialTag.bell_attachment
        // @group properties
        // @description
        // If the material is a bell, returns the kind of attachment the material is using.
        // Can be one of: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/data/type/Bell.Attachment.html>
        // -->
        if (attribute.startsWith("bell_attachment")) {
            return new ElementTag(getAttachment()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name bell_attachment
        // @input ElementTag
        // @description
        // If the material is a bell, sets the kind of attachment the material will use.
        // Can be one of: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/data/type/Bell.Attachment.html>
        // @tags
        // <MaterialTag.bell_attachment>
        // -->
        if (mechanism.matches("bell_attachment") && mechanism.requireEnum(false, Bell.Attachment.values())) {
            getBell().setAttachment(Bell.Attachment.valueOf(mechanism.getValue().asString().toUpperCase()));
        }
    }
}
