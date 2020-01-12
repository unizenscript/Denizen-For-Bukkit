package dev.unizen.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.StructureBlock;

public class MaterialStructureBlockMode implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof StructureBlock;
    }

    public static MaterialStructureBlockMode getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialStructureBlockMode((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "mode"
    };

    public static final String[] handledMechs = new String[] {
            "mode"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialStructureBlockMode(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private StructureBlock getStructureBlock() {
        return (StructureBlock) material.getModernData().data;
    }

    private String getMode() {
        return getStructureBlock().getMode().name();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getMode();
    }

    @Override
    public String getPropertyId() {
        return "mode";
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
        // @attribute <MaterialTag.mode>
        // @returns ElementTag
        // @mechanism MaterialTag.mode
        // @group properties
        // @description
        // Returns the mode of the structure block material.
        // Can be any of: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/data/type/StructureBlock.Mode.html>
        // -->
        if (attribute.startsWith("mode")) {
            return new ElementTag(getMode()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name mode
        // @input ElementTag
        // @description
        // Sets the mode of the structure block material.
        // Can be any of: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/data/type/StructureBlock.Mode.html>
        // @tags
        // <MaterialTag.mode>
        // -->
        if (mechanism.matches("mode") && mechanism.requireEnum(false, StructureBlock.Mode.values())) {
            getStructureBlock().setMode(StructureBlock.Mode.valueOf(mechanism.getValue().asString().toUpperCase()));
        }
    }
}
