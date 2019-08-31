package com.denizenscript.denizen.objects.properties.entity;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MushroomCow;

public class EntityMooshroomVariant implements Property {

    public static boolean describes(ObjectTag entity) {
        return entity instanceof EntityTag && ((EntityTag) entity).getBukkitEntityType() == EntityType.MUSHROOM_COW;
    }

    public static EntityMooshroomVariant getFrom(ObjectTag entity) {
        if (!describes(entity)) {
            return null;
        }
        return new EntityMooshroomVariant((EntityTag) entity);
    }

    public static final String[] handledTags = new String[] {
            "mooshroom_variant"
    };

    public static final String[] handledMechs = new String[] {
            "mooshroom_variant"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private EntityMooshroomVariant(EntityTag entity) {
        this.entity = entity;
    }

    private EntityTag entity;

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return ((MushroomCow) entity.getBukkitEntity()).getVariant().name();
    }

    @Override
    public String getPropertyId() {
        return "mooshroom_variant";
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
        // @attribute <EntityTag.mooshroom_variant>
        // @returns ElementTag
        // @mechanism EntityTag.mooshroom_variant
        // @group properties
        // @description
        // If the entity is a mooshroom cow, returns the variant of the entity.
        // Can be either RED or BROWN.
        // -->
        if (attribute.startsWith("mooshroom_variant")) {
            return new ElementTag(((MushroomCow) entity.getBukkitEntity()).getVariant().name()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object EntityTag
        // @name mooshroom_variant
        // @input ElementTag
        // @description
        // Sets the mooshroom cow entity's color variation. Can be either RED or BROWN.
        // @tags
        // <EntityTag.mooshroom_variant>
        // -->
        if (mechanism.matches("mooshroom_variant") && mechanism.requireEnum(false, MushroomCow.Variant.values())) {
            ((MushroomCow) entity.getBukkitEntity()).setVariant(MushroomCow.Variant.valueOf(mechanism.getValue().asString().toUpperCase()));
        }
    }
}
