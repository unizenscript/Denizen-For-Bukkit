package dev.unizen.denizen.objects.properties.entity;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.entity.Raider;

public class EntityPatrolLeader implements Property {

    public static boolean describes(ObjectTag entity) {
        return entity instanceof EntityTag && ((EntityTag) entity).getBukkitEntity() instanceof Raider;
    }

    public static EntityPatrolLeader getFrom(ObjectTag entity) {
        if (!describes(entity)) {
            return null;
        }
        return new EntityPatrolLeader((EntityTag) entity);
    }

    public static final String[] handledTags = new String[] {
            "is_patrol_leader"
    };

    public static final String[] handledMechs = new String[] {
            "is_patrol_leader"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private EntityPatrolLeader(EntityTag entity) {
        this.entity = entity;
    }

    private EntityTag entity;

    /////////
    // Property Methods
    ///////


    @Override
    public String getPropertyString() {
        return String.valueOf(((Raider) entity.getBukkitEntity()).isPatrolLeader());
    }

    @Override
    public String getPropertyId() {
        return "is_patrol_leader";
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
        // @attribute <EntityTag.is_patrol_leader>
        // @returns ElementTag(Boolean)
        // @mechanism EntityTag.is_patrol_leader
        // @group properties
        // @description
        // If the entity is a raider, returns whether this entity is a patrol leader.
        // -->
        if (attribute.startsWith("is_patrol_leader")) {
            return new ElementTag(((Raider) entity.getBukkitEntity()).isPatrolLeader()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object EntityTag
        // @name is_patrol_leader
        // @input ElementTag(Boolean)
        // @description
        // If the entity is a raider, set whether this entity is a patrol leader.
        // @tags
        // <EntityTag.is_patrol_leader>
        // -->
        if (mechanism.matches("is_patrol_leader") && mechanism.requireBoolean()) {
            ((Raider) entity.getBukkitEntity()).setPatrolLeader(mechanism.getValue().asBoolean());
        }
    }
}
