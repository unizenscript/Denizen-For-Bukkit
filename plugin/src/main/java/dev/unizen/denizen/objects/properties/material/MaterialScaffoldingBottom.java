package dev.unizen.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Scaffolding;

public class MaterialScaffoldingBottom implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Scaffolding;
    }

    public static MaterialScaffoldingBottom getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialScaffoldingBottom((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "is_bottom"
    };

    public static final String[] handledMechs = new String[] {
            "is_bottom"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialScaffoldingBottom(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Scaffolding getScaffolding() {
        return (Scaffolding) material.getModernData().data;
    }

    private boolean isBottom() {
        return getScaffolding().isBottom();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return isBottom() ? "true" : null;
    }

    @Override
    public String getPropertyId() {
        return "is_bottom";
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
        // @attribute <MaterialTag.is_bottom>
        // @returns ElementTag(Boolean)
        // @mechanism MaterialTag.is_bottom
        // @group properties
        // @description
        // Returns whether this scaffolding material is a "bottom" scaffolding.
        // If false, then the scaffolding should be on top of another scaffolding.
        // -->
        if (attribute.startsWith("is_bottom")) {
            return new ElementTag(isBottom()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name is_bottom
        // @input ElementTag(Boolean)
        // @description
        // Sets whether this scaffolding material is a "bottom" scaffolding.
        // @tags
        // <MaterialTag.is_bottom>
        // -->
        if (mechanism.matches("is_bottom") && mechanism.requireBoolean()) {
            getScaffolding().setBottom(mechanism.getValue().asBoolean());
        }
    }
}
