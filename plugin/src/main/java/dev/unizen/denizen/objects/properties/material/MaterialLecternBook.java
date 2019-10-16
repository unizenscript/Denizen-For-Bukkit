package dev.unizen.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Lectern;

public class MaterialLecternBook implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Lectern;
    }

    public static MaterialLecternBook getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialLecternBook((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "has_book"
    };

    public static final String[] handledMechs = new String[] {
            // None
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialLecternBook(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private boolean hasBook() {
        return ((Lectern) material.getModernData().data).hasBook();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return hasBook() ? "true" : null;
    }

    @Override
    public String getPropertyId() {
        return "has_book";
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
        // @attribute <MaterialTag.has_book>
        // @returns ElementTag(Boolean)
        // @group properties
        // @description
        // Returns whether the lectern material has a book.
        // -->
        if (attribute.startsWith("has_book")) {
            return new ElementTag(hasBook()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {
        // None
    }
}
