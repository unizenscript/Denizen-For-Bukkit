package dev.unizen.denizen.objects.properties.material;

import com.denizenscript.denizen.objects.MaterialTag;
import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.block.data.type.Snow;

public class MaterialSnowLayers implements Property {

    public static boolean describes(ObjectTag material) {
        return material instanceof MaterialTag
                && ((MaterialTag) material).hasModernData()
                && ((MaterialTag) material).getModernData().data instanceof Snow;
    }

    public static MaterialSnowLayers getFrom(ObjectTag material) {
        if (!describes(material)) {
            return null;
        }
        return new MaterialSnowLayers((MaterialTag) material);
    }

    public static final String[] handledTags = new String[] {
            "snow_layers", "min_snow_layers", "max_snow_layers"
    };

    public static final String[] handledMechs = new String[] {
            "snow_layers"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private MaterialSnowLayers(MaterialTag material) {
        this.material = material;
    }

    private MaterialTag material;

    private Snow getSnow() {
        return (Snow) material.getModernData().data;
    }

    private int getLayers() {
        return getSnow().getLayers();
    }

    private int getMinLayers() {
        return getSnow().getMinimumLayers();
    }

    private int getMaxLayers() {
        return getSnow().getMaximumLayers();
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return getLayers() != getMinLayers() ? String.valueOf(getLayers()) : null;
    }

    @Override
    public String getPropertyId() {
        return "snow_layers";
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
        // @attribute <MaterialTag.min_snow_layers>
        // @returns ElementTag(Number)
        // @group properties
        // @description
        // Returns the minimum amount of layers this snow layer material can have.
        // -->
        if (attribute.startsWith("min_snow_layers")) {
            return new ElementTag(getMinLayers()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <MaterialTag.max_snow_layers>
        // @returns ElementTag(Number)
        // @group properties
        // @description
        // Returns the maximum amount of layers this snow layer material can have.
        // -->
        if (attribute.startsWith("max_snow_layers")) {
            return new ElementTag(getMaxLayers()).getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <MaterialTag.snow_layers>
        // @returns ElementTag(Number)
        // @mechanism MaterialTag.snow_layers
        // @group properties
        // @description
        // Returns the amount of layers this snow layer material can have.
        // -->
        if (attribute.startsWith("snow_layers")) {
            return new ElementTag(getLayers()).getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object MaterialTag
        // @name snow_layers
        // @input ElementTag(Number)
        // @description
        // Sets the amount of layers this snow layer can have.
        // @tags
        // <MaterialTag.snow_layers>
        // <MaterialTag.min_snow_layers>
        // <MaterialTag.max_snow_layers>
        // -->
        if (mechanism.matches("snow_layers") && mechanism.requireInteger()) {
            int layers = mechanism.getValue().asInt();
            if (layers < getMinLayers() || layers > getMaxLayers()) {
                Debug.echoError("The number of snow layers must be between '" + getMinLayers() + "' and '" + getMaxLayers() + "'!");
                return;
            }
            getSnow().setLayers(layers);
        }
    }
}
