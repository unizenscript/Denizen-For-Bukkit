package com.denizenscript.denizen.objects.properties.entity;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Panda;

import java.util.ArrayList;
import java.util.List;

public class EntityPandaGene implements Property {

    public static boolean describes(ObjectTag entity) {
        return entity instanceof EntityTag && ((EntityTag) entity).getBukkitEntityType() == EntityType.PANDA;
    }

    public static EntityPandaGene getFrom(ObjectTag entity) {
        if (!describes(entity)) {
            return null;
        }
        return new EntityPandaGene((EntityTag) entity);
    }

    public static final String[] handledTags = new String[] {
            "genes", "main_gene", "hidden_gene"
    };

    public static final String[] handledMechs = new String[] {
            "genes", "main_gene", "hidden_gene"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private EntityPandaGene(EntityTag entity) {
        this.entity = entity;
    }

    private EntityTag entity;

    private List<String> getGenes() {
        List<String> list = new ArrayList<>();
        Panda panda = (Panda) entity.getBukkitEntity();
        list.add(panda.getMainGene().name());
        list.add(panda.getHiddenGene().name());
        return list;
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return new ListTag(getGenes()).identify();
    }

    @Override
    public String getPropertyId() {
        return "genes";
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
        // @attribute <EntityTag.main_gene>
        // @returns ElementTag
        // @mechanism EntityTag.main_gene
        // @group properties
        // @description
        // If the entity is a panda, returns the main gene.
        // The gene can be any of: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/Panda.Gene.html>
        // -->
        if (attribute.startsWith("main_gene")) {
            return new ElementTag(((Panda) entity.getBukkitEntity()).getMainGene().name()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <EntityTag.hidden_gene>
        // @returns ElementTag
        // @mechanism EntityTag.hidden_gene
        // @group properties
        // @description
        // If the entity is a panda, returns the hidden gene.
        // The gene can be any of: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/Panda.Gene.html>
        // -->
        if (attribute.startsWith("hidden_gene")) {
            return new ElementTag(((Panda) entity.getBukkitEntity()).getHiddenGene().name()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <EntityTag.genes>
        // @returns ListTag
        // @group properties
        // @description
        // If the entity is a panda, returns both the main and hidden genes in this order: MAIN|HIDDEN.
        // The genes can be any of: <@link url https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/Panda.Gene.html>
        // -->
        if (attribute.startsWith("genes")) {
            ListTag list = new ListTag();
            return new ListTag(getGenes()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object EntityTag
        // @name main_gene
        // @input ElementTag
        // @description
        // If the entity is a panda, sets the entity's main gene.
        // @tags
        // <EntityTag.genes>
        // <EntityTag.main_gene>
        // <EntityTag.hidden_gene>
        // -->
        if (mechanism.matches("main_gene") && mechanism.requireEnum(false, Panda.Gene.values())) {
            ((Panda) entity.getBukkitEntity()).setMainGene(Panda.Gene.valueOf(mechanism.getValue().asString().toUpperCase()));
        }

        // <--[mechanism]
        // @object EntityTag
        // @name hidden_gene
        // @input ElementTag
        // @description
        // If the entity is a panda, sets the entity's hidden gene.
        // @tags
        // <EntityTag.genes>
        // <EntityTag.main_gene>
        // <EntityTag.hidden_gene>
        // -->
        if (mechanism.matches("hidden_gene") && mechanism.requireEnum(false, Panda.Gene.values())) {
            ((Panda) entity.getBukkitEntity()).setHiddenGene(Panda.Gene.valueOf(mechanism.getValue().asString().toUpperCase()));
        }

        // <--[mechanism]
        // @object EntityTag
        // @name genes
        // @input ListTag
        // @description
        // If the entity is a panda, sets the entity's main and hidden gene.
        // Input should be formatted as MAIN_GENE|HIDDEN_GENE.
        // @tags
        // <EntityTag.genes>
        // <EntityTag.main_gene>
        // <EntityTag.hidden_gene>
        // -->
        if (mechanism.matches("genes") && mechanism.requireObject(ListTag.class)) {
            ListTag list = mechanism.valueAsType(ListTag.class);
            List<String> filteredList = list.filter(Panda.Gene.values());
            if (filteredList == null || filteredList.size() != 2) {
                Debug.echoError("Gene list must contain only valid genes and be exactly two items long!");
                return;
            }

            Panda.Gene mainGene;
            Panda.Gene hiddenGene;
            try {
                mainGene = Panda.Gene.valueOf(filteredList.get(0).toUpperCase());
                hiddenGene = Panda.Gene.valueOf(filteredList.get(1).toUpperCase());
            }
            catch (IllegalArgumentException e) {
                Debug.echoError("Invalid gene(s) were specified!");
                return;
            }

            Panda panda = (Panda) entity.getBukkitEntity();
            panda.setMainGene(mainGene);
            panda.setHiddenGene(hiddenGene);
        }
    }
}
