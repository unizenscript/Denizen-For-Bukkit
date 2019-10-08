package dev.unizen.denizen.objects.properties.entity;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.DyeColor;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;

public class EntityCollarColor implements Property {

    public static boolean describes(ObjectTag entity) {
        return entity instanceof EntityTag && (
                ((EntityTag) entity).getBukkitEntityType() == EntityType.WOLF
                || ((EntityTag) entity).getBukkitEntityType() == EntityType.CAT);
    }

    public static EntityCollarColor getFrom(ObjectTag entity) {
        if (!describes(entity)) {
            return null;
        }
        return new EntityCollarColor((EntityTag) entity);
    }

    public static final String[] handledTags = new String[] {
            "collar_color"
    };

    public static final String[] handledMechs = new String[] {
            "collar_color"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private EntityCollarColor(EntityTag entity) {
        this.entity = entity;
    }

    private EntityTag entity;

    // If it's not a wolf, it's a cat!
    private boolean isWolf() {
        return entity.getBukkitEntityType() == EntityType.WOLF;
    }

    private String getCollarColor() {
        if (isWolf()) {
            return ((Wolf) entity.getBukkitEntity()).getCollarColor().name();
        }
        return ((Cat) entity.getBukkitEntity()).getCollarColor().name();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getCollarColor();
    }

    @Override
    public String getPropertyId() {
        return "collar_color";
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
        // @attribute <EntityTag.collar_color>
        // @returns ElementTag
        // @mechanism EntityTag.collar_color
        // @group properties
        // @description
        // If the entity is a wolf or cat, returns the color of its collar.
        // Can be one of: <>
        // -->
        if (attribute.startsWith("collar_color")) {
            return new ElementTag(getCollarColor()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object EntityTag
        // @name collar_color
        // @input ElementTag
        // @description
        // If the entity is a wolf or cat, sets the color of its collar.
        // Valid colors are: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/DyeColor.html>
        // @tags
        // <EntityTag.collar_color>
        // -->
        if (mechanism.matches("collar_color") && mechanism.requireEnum(false, DyeColor.values())) {
            DyeColor color = DyeColor.valueOf(mechanism.getValue().asString().toUpperCase());
            if (isWolf()) {
                ((Wolf) entity.getBukkitEntity()).setCollarColor(color);
            }
            else {
                ((Cat) entity.getBukkitEntity()).setCollarColor(color);
            }
        }
    }
}
