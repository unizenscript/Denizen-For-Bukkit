package com.denizenscript.denizen.objects.properties.bukkit;

import com.denizenscript.denizen.nms.NMSHandler;
import com.denizenscript.denizen.nms.NMSVersion;
import com.denizenscript.denizen.objects.*;
import com.denizenscript.denizen.scripts.containers.core.FormatScriptContainer;
import com.denizenscript.denizen.scripts.containers.core.ItemScriptHelper;
import com.denizenscript.denizen.utilities.FormattedTextHelper;
import com.denizenscript.denizencore.objects.Argument;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizen.BukkitScriptEntryData;
import com.denizenscript.denizen.tags.BukkitTagContext;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.scripts.ScriptRegistry;
import com.denizenscript.denizencore.tags.Attribute;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;

public class BukkitElementProperties implements Property {

    public static boolean describes(ObjectTag element) {
        return element instanceof ElementTag;
    }

    public static BukkitElementProperties getFrom(ObjectTag element) {
        if (!describes(element)) {
            return null;
        }
        else {
            return new BukkitElementProperties((ElementTag) element);
        }
    }


    private BukkitElementProperties(ElementTag element) {
        this.element = element;
    }

    public static final String[] handledTags = new String[] {
            "aschunk", "as_chunk", "ascolor", "as_color", "ascuboid", "as_cuboid", "asentity", "as_entity",
            "asinventory", "as_inventory", "asitem", "as_item", "aslocation", "as_location", "asmaterial",
            "as_material", "asnpc", "as_npc", "asplayer", "as_player", "asworld", "as_world", "asplugin",
            "as_plugin", "last_color", "format", "strip_color", "parse_color", "to_itemscript_hash",
            "to_secret_colors", "from_secret_colors", "to_raw_json", "from_raw_json", "on_hover", "on_click",
            "with_insertion", "text_hover", "item_hover", "entity_hover", "run_on_click", "suggest_on_click",
            "page_on_click", "url_on_click", "file_on_click", "insert_on_shift_click", "to_json", "from_json"
    };

    public static final String[] handledMechs = new String[] {
    }; // None

    static char textComponentSecret = net.md_5.bungee.api.ChatColor.COLOR_CHAR;

    ElementTag element;

    @Override
    public ObjectTag getObjectAttribute(Attribute attribute) {

        // <--[tag]
        // @attribute <ElementTag.as_chunk>
        // @returns ChunkTag
        // @group conversion
        // @description
        // Returns the element as a chunk. Note: the value must be a valid chunk.
        // -->
        if (attribute.startsWith("aschunk")
                || attribute.startsWith("as_chunk")) {
            ObjectTag object = ElementTag.handleNull(element.asString(), ChunkTag.valueOf(element.asString(),
                    new BukkitTagContext(attribute.getScriptEntry(), false)), "dChunk", attribute.hasAlternative());
            if (object != null) {
                return object.getObjectAttribute(attribute.fulfill(1));
            }
        }

        // <--[tag]
        // @attribute <ElementTag.as_color>
        // @returns ColorTag
        // @group conversion
        // @description
        // Returns the element as a ColorTag. Note: the value must be a valid color.
        // -->
        if (attribute.startsWith("ascolor")
                || attribute.startsWith("as_color")) {
            ObjectTag object = ElementTag.handleNull(element.asString(), ColorTag.valueOf(element.asString(),
                    new BukkitTagContext(attribute.getScriptEntry(), false)), "dColor", attribute.hasAlternative());
            if (object != null) {
                return object.getObjectAttribute(attribute.fulfill(1));
            }
        }

        // <--[tag]
        // @attribute <ElementTag.as_cuboid>
        // @returns CuboidTag
        // @group conversion
        // @description
        // Returns the element as a cuboid. Note: the value must be a valid cuboid.
        // -->
        if (attribute.startsWith("ascuboid")
                || attribute.startsWith("as_cuboid")) {
            ObjectTag object = ElementTag.handleNull(element.asString(), CuboidTag.valueOf(element.asString(),
                    new BukkitTagContext(attribute.getScriptEntry(), false)), "dCuboid", attribute.hasAlternative());
            if (object != null) {
                return object.getObjectAttribute(attribute.fulfill(1));
            }
        }

        // <--[tag]
        // @attribute <ElementTag.as_entity>
        // @returns EntityTag
        // @group conversion
        // @description
        // Returns the element as an entity. Note: the value must be a valid entity.
        // -->
        if (attribute.startsWith("asentity")
                || attribute.startsWith("as_entity")) {
            ObjectTag object = ElementTag.handleNull(element.asString(), EntityTag.valueOf(element.asString(),
                    new BukkitTagContext(attribute.getScriptEntry(), false)), "dEntity", attribute.hasAlternative());
            if (object != null) {
                return object.getObjectAttribute(attribute.fulfill(1));
            }
        }

        // <--[tag]
        // @attribute <ElementTag.as_inventory>
        // @returns InventoryTag
        // @group conversion
        // @description
        // Returns the element as an inventory. Note: the value must be a valid inventory.
        // -->
        if (attribute.startsWith("asinventory")
                || attribute.startsWith("as_inventory")) {
            ObjectTag object = ElementTag.handleNull(element.asString(), InventoryTag.valueOf(element.asString(),
                    new BukkitTagContext(attribute.getScriptEntry(), false)), "dInventory", attribute.hasAlternative());
            if (object != null) {
                return object.getObjectAttribute(attribute.fulfill(1));
            }
        }

        // <--[tag]
        // @attribute <ElementTag.as_item>
        // @returns ItemTag
        // @group conversion
        // @description
        // Returns the element as an item. Additional attributes can be accessed by ItemTag.
        // Note: the value must be a valid item.
        // -->
        if (attribute.startsWith("asitem")
                || attribute.startsWith("as_item")) {
            ObjectTag object = ElementTag.handleNull(element.asString(), ItemTag.valueOf(element.asString(),
                    new BukkitTagContext(attribute.getScriptEntry(), false)), "dItem", attribute.hasAlternative());
            if (object != null) {
                return object.getObjectAttribute(attribute.fulfill(1));
            }
        }

        // <--[tag]
        // @attribute <ElementTag.as_location>
        // @returns LocationTag
        // @group conversion
        // @description
        // Returns the element as a location. Note: the value must be a valid location.
        // -->
        if (attribute.startsWith("aslocation")
                || attribute.startsWith("as_location")) {
            ObjectTag object = ElementTag.handleNull(element.asString(), LocationTag.valueOf(element.asString(),
                    new BukkitTagContext(attribute.getScriptEntry(), false)), "dLocation", attribute.hasAlternative());
            if (object != null) {
                return object.getObjectAttribute(attribute.fulfill(1));
            }
        }

        // <--[tag]
        // @attribute <ElementTag.as_material>
        // @returns MaterialTag
        // @group conversion
        // @description
        // Returns the element as a material. Note: the value must be a valid material.
        // -->
        if (attribute.startsWith("asmaterial")
                || attribute.startsWith("as_material")) {
            ObjectTag object = ElementTag.handleNull(element.asString(), MaterialTag.valueOf(element.asString(),
                    new BukkitTagContext(attribute.getScriptEntry(), false)), "dMaterial", attribute.hasAlternative());
            if (object != null) {
                return object.getObjectAttribute(attribute.fulfill(1));
            }
        }

        // <--[tag]
        // @attribute <ElementTag.as_npc>
        // @returns NPCTag
        // @group conversion
        // @description
        // Returns the element as an NPC. Note: the value must be a valid NPC.
        // -->
        if (attribute.startsWith("asnpc")
                || attribute.startsWith("as_npc")) {
            ObjectTag object = ElementTag.handleNull(element.asString(), NPCTag.valueOf(element.asString(),
                    new BukkitTagContext(attribute.getScriptEntry(), false)), "dNPC", attribute.hasAlternative());
            if (object != null) {
                return object.getObjectAttribute(attribute.fulfill(1));
            }
        }

        // <--[tag]
        // @attribute <ElementTag.as_player>
        // @returns PlayerTag
        // @group conversion
        // @description
        // Returns the element as a player. Note: the value must be a valid player. Can be online or offline.
        // -->
        if (attribute.startsWith("asplayer")
                || attribute.startsWith("as_player")) {
            ObjectTag object = ElementTag.handleNull(element.asString(), PlayerTag.valueOf(element.asString(),
                    new BukkitTagContext(attribute.getScriptEntry(), false)), "dPlayer", attribute.hasAlternative());
            if (object != null) {
                return object.getObjectAttribute(attribute.fulfill(1));
            }
        }

        // <--[tag]
        // @attribute <ElementTag.as_world>
        // @returns WorldTag
        // @group conversion
        // @description
        // Returns the element as a world.
        // -->
        if (attribute.startsWith("asworld")
                || attribute.startsWith("as_world")) {
            ObjectTag object = ElementTag.handleNull(element.asString(), WorldTag.valueOf(element.asString(),
                    new BukkitTagContext(attribute.getScriptEntry(), false)), "dWorld", attribute.hasAlternative());
            if (object != null) {
                return object.getObjectAttribute(attribute.fulfill(1));
            }
        }

        // <--[tag]
        // @attribute <ElementTag.as_plugin>
        // @returns PluginTag
        // @group conversion
        // @description
        // Returns the element as a plugin. Note: the value must be a valid plugin.
        // -->
        if (attribute.startsWith("asplugin")
                || attribute.startsWith("as_plugin")) {
            ObjectTag object = ElementTag.handleNull(element.asString(), PluginTag.valueOf(element.asString(),
                    new BukkitTagContext(attribute.getScriptEntry(), false)), "dPlugin", attribute.hasAlternative());
            if (object != null) {
                return object.getObjectAttribute(attribute.fulfill(1));
            }
        }

        // <--[tag]
        // @attribute <ElementTag.last_color>
        // @returns ElementTag
        // @group text checking
        // @description
        // Returns the ChatColors used last in an element.
        // -->
        if (attribute.startsWith("last_color")) {
            return new ElementTag(ChatColor.getLastColors(element.asString())).getObjectAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <ElementTag.format[<script>]>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Returns the text re-formatted according to a format script.
        // -->
        if (attribute.startsWith("format")
                && attribute.hasContext(1)) {
            FormatScriptContainer format = ScriptRegistry.getScriptContainer(attribute.getContext(1));
            if (format == null) {
                Debug.echoError("Could not find format script matching '" + attribute.getContext(1) + "'");
                return null;
            }
            else {
                return new ElementTag(format.getFormattedText(element.asString(),
                        attribute.getScriptEntry() != null ? ((BukkitScriptEntryData) attribute.getScriptEntry().entryData).getNPC() : null,
                        attribute.getScriptEntry() != null ? ((BukkitScriptEntryData) attribute.getScriptEntry().entryData).getPlayer() : null))
                        .getObjectAttribute(attribute.fulfill(1));
            }
        }

        // <--[tag]
        // @attribute <ElementTag.strip_color>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Returns the element with all color encoding stripped.
        // -->
        if (attribute.startsWith("strip_color")) {
            return new ElementTag(ChatColor.stripColor(element.asString())).getObjectAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <ElementTag.parse_color[<prefix>]>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Returns the element with all color codes parsed.
        // Optionally, specify a character to prefix the color ids. Defaults to '&' if not specified.
        // -->
        if (attribute.startsWith("parse_color")) {
            char prefix = '&';
            if (attribute.hasContext(1)) {
                prefix = attribute.getContext(1).charAt(0);
            }
            return new ElementTag(ChatColor.translateAlternateColorCodes(prefix, element.asString()))
                    .getObjectAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <ElementTag.to_itemscript_hash>
        // @returns ElementTag
        // @group conversion
        // @description
        // Shortens the element down to an itemscript hash ID, made of invisible color codes.
        // -->
        if (attribute.startsWith("to_itemscript_hash")) {
            return new ElementTag(ItemScriptHelper.createItemScriptID(element.asString()))
                    .getObjectAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <ElementTag.to_secret_colors>
        // @returns ElementTag
        // @group conversion
        // @description
        // Hides the element's text in invisible color codes.
        // Inverts <@link tag ElementTag.from_secret_colors>.
        // -->
        if (attribute.startsWith("to_secret_colors")) {
            String text = element.asString();
            byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
            String hex = DatatypeConverter.printHexBinary(bytes);
            StringBuilder colors = new StringBuilder(text.length() * 2);
            for (int i = 0; i < hex.length(); i++) {
                colors.append(ChatColor.COLOR_CHAR).append(hex.charAt(i));
            }
            return new ElementTag(colors.toString())
                    .getObjectAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <ElementTag.from_secret_colors>
        // @returns ElementTag
        // @group conversion
        // @description
        // Un-hides the element's text from invisible color codes back to normal text.
        // Inverts <@link tag ElementTag.to_secret_colors>.
        // -->
        if (attribute.startsWith("from_secret_colors")) {
            String text = element.asString().replace(String.valueOf(ChatColor.COLOR_CHAR), "");
            byte[] bytes = DatatypeConverter.parseHexBinary(text);
            return new ElementTag(new String(bytes, StandardCharsets.UTF_8))
                    .getObjectAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <ElementTag.to_raw_json>
        // @returns ElementTag
        // @group conversion
        // @description
        // Converts normal colored text to Minecraft-style "raw JSON" format.
        // Inverts <@link tag ElementTag.from_raw_json>.
        // -->
        if (attribute.startsWith("to_raw_json")) {
            return new ElementTag(ComponentSerializer.toString(FormattedTextHelper.parse(element.asString())));
        }

        // <--[tag]
        // @attribute <ElementTag.from_raw_json>
        // @returns ElementTag
        // @group conversion
        // @description
        // Un-hides the element's text from invisible color codes back to normal text.
        // Inverts <@link tag ElementTag.to_raw_json>.
        // -->
        if (attribute.startsWith("from_raw_json")) {
            return new ElementTag(FormattedTextHelper.stringify(ComponentSerializer.parse(element.asString())));
        }

        // @attribute <ElementTag.text_hover[<text>]>
        // @returns ElementTag
        // @group conversion
        // @description
        // Returns a copy of the element that displays the specified hover text when the mouse is left over the element.
        // Equivalent to <&hover[<text>]><ElementTag><&end_hover>
        // -->
        if (attribute.startsWith("text_hover")) {
            if (!attribute.hasContext(1)) {
                Debug.echoError("An input is required for attribute 'text_hover'!");
                return null;
            }
            return new ElementTag(textComponentSecret + "[hover=SHOW_TEXT;" +
                    FormattedTextHelper.escape(attribute.getContext(1)) + "]" +
                    element.asString() + textComponentSecret + "[/hover]")
                    .getObjectAttribute(attribute.fulfill(1));
        }

        // @attribute <ElementTag.item_hover[<item>/<text>]>
        // @returns ElementTag
        // @group conversion
        // @description
        // Returns a copy of the element that displays the specified item when the mouse is left over the element.
        // If text is used, then it must specify a valid item in JSON format.
        // Equivalent to <&hover[<text>].type[SHOW_ITEM]><ElementTag><&end_hover>
        // -->
        if (attribute.startsWith("item_hover")) {
            if (!attribute.hasContext(1)) {
                Debug.echoError("An item or text input is required for attribute 'item_hover'!");
                return null;
            }

            String output = attribute.getContext(1);
            if (Argument.valueOf(attribute.getContext(1)).matchesArgumentType(ItemTag.class)) {
                output = NMSHandler.getItemHelper().getUnmodifiedJsonString(ItemTag.valueOf(attribute.getContext(1)).getItemStack());
            }
            return new ElementTag(textComponentSecret + "[hover=SHOW_ITEM;" +
                    FormattedTextHelper.escape(output) + "]" +
                    element.asString() + textComponentSecret + "[/hover]")
                    .getObjectAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <ElementTag.on_hover[<message>]>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Adds a hover message to the element, which makes the element display the input hover text when the mouse is left over it.
        // -->
        if (attribute.startsWith("on_hover") && attribute.hasContext(1)) {
            String hoverText = attribute.getContext(1);
            String type = "SHOW_TEXT";

            // <--[tag]
            // @attribute <ElementTag.on_hover[<message>].type[<type>]>
            // @returns ElementTag
            // @group text manipulation
            // @description
            // Adds a hover message to the element, which makes the element display the input hover text when the mouse is left over it.
            // Optionally specify the hover type as one of: SHOW_TEXT, SHOW_ACHIEVEMENT, SHOW_ITEM, or SHOW_ENTITY.
            // Note: for "SHOW_ITEM", replace the text with a valid ItemTag. For "SHOW_ENTITY", replace the text with a valid spawned EntityTag (requires F3+H to see entities).
            // -->
            if (attribute.startsWith("type", 2)) {
                type = attribute.getContext(2);
                attribute.fulfill(1);
            }
            return new ElementTag(ChatColor.COLOR_CHAR + "[hover=" + type + ";" + FormattedTextHelper.escape(hoverText) + "]"
                    + element.asString() + ChatColor.COLOR_CHAR + "[/hover]").getObjectAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <ElementTag.on_click[<click command>]>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Adds a click command to the element, which makes the element execute the input command when clicked.
        // -->
        if (attribute.startsWith("on_click") && attribute.hasContext(1)) {
            String clickText = attribute.getContext(1);
            String type = "RUN_COMMAND";

            // <--[tag]
            // @attribute <ElementTag.on_click[<message>].type[<type>]>
            // @returns ElementTag
            // @group text manipulation
            // @description
            // Adds a click command to the element, which makes the element execute the input command when clicked.
            // Optionally specify the hover type as one of: OPEN_URL, OPEN_FILE, RUN_COMMAND, SUGGEST_COMMAND, or CHANGE_PAGE.
            // -->
            if (attribute.startsWith("type", 2)) {
                type = attribute.getContext(2);
                attribute.fulfill(1);
            }
            return new ElementTag(ChatColor.COLOR_CHAR + "[click=" + type + ";" + FormattedTextHelper.escape(clickText) + "]"
                    + element.asString() + ChatColor.COLOR_CHAR + "[/click]").getObjectAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <ElementTag.with_insertion[<message>]>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Adds an insertion message to the element, which makes the element insert the input message to chat when shift-clicked.
        // -->
        if (attribute.startsWith("with_insertion") && attribute.hasContext(1)) {
            String insertionText = attribute.getContext(1);
            return new ElementTag(ChatColor.COLOR_CHAR + "[insertion="  + FormattedTextHelper.escape(insertionText) + "]"
                    + element.asString() + ChatColor.COLOR_CHAR + "[/insertion]").getObjectAttribute(attribute.fulfill(1));
        }

        // @attribute <ElementTag.entity_hover[<entity>/<text>]>
        // @returns ElementTag
        // @group conversion
        // @description
        // Returns a copy of the element that displays the specified hover text when the mouse is left over the element.
        // If text is used, then it must follow the JSON format {"id":"UUID","type":"ENTITY_TYPE","name":"CUSTOM NAME"} (but none of the three keys are required).
        // Equivalent to <&hover[<text>].type[SHOW_ENTITY]><ElementTag><&end_hover>
        // -->
        if (attribute.startsWith("entity_hover")) {
            if (!attribute.hasContext(1)) {
                Debug.echoError("An entity or text input is required for attribute 'entity_hover'!");
                return null;
            }

            Entity entity = null;
            String output = attribute.getContext(1);
            if (Argument.valueOf(attribute.getContext(1)).matchesArgumentType(EntityTag.class)) {
                entity = EntityTag.valueOf(attribute.getContext(1)).getBukkitEntity();
            }

            if (entity != null) {
                String customName = entity.getCustomName();
                String entityType = entity.getType().getName();
                if (entity instanceof Player) {
                    customName = entity.getName();
                    entityType = "Player";
                }
                else if (entity instanceof FallingBlock) {
                    customName = "Falling block: \"" + ((FallingBlock) entity).getBlockData().getMaterial().name().toLowerCase().replace('_', ' ') + "\"";
                }
                else if (entity instanceof Item) {
                    customName = "Item: \"" + ((Item) entity).getItemStack().getType().name().toLowerCase().replace('_', ' ') + "\"";
                }

                if (NMSHandler.getVersion().isAtLeast(NMSVersion.v1_14)) {
                    if (entity instanceof Player) {
                        customName = "{\\\"text\\\":\\\"" + entity.getName() + "\\\"}";
                    }
                    else {
                        entityType = entity.getType().getKey().getKey();
                        if (customName != null) {
                            customName = ComponentSerializer.toString(FormattedTextHelper.parse(customName))
                                    .replace("\\", "\\\\").replace("\"", "\\\"");
                        }
                    }
                }

                if (entityType == null) {
                    entityType = "Unknown";
                }

                String[] typeSplit = entityType.split("_");
                StringBuilder constructedType = new StringBuilder().append(typeSplit[0].toUpperCase().charAt(0));
                if (typeSplit[0].length() > 1) {
                    constructedType.append(typeSplit[1].toLowerCase().substring(1));
                }
                for (int i = 1; i < entityType.split("_").length; i++) {
                    if (typeSplit[i].length() == 0) {
                        continue;
                    }
                    constructedType.append(" ").append(typeSplit[i].toUpperCase().charAt(0));
                    if (typeSplit[i].length() > 1) {
                        constructedType.append(typeSplit[i].toLowerCase().substring(1));
                    }
                }

                output = "{\"id\":\"" + entity.getUniqueId().toString() + "\",\"type\":\"" + constructedType.toString().trim() + (customName == null ? "" : "\",\"name\":\"" + customName) + "\"}";
            }

            return new ElementTag(textComponentSecret + "[hover=SHOW_ENTITY;" +
                    FormattedTextHelper.escape(output) + "]" +
                    element.asString() + textComponentSecret + "[/hover]")
                    .getObjectAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <ElementTag.run_on_click[<command>]>
        // @returns ElementTag
        // @group conversion
        // @description
        // Returns a copy of the element that runs the specified command as the player who clicked the element.
        // Equivalent to <&click[<command>]><ElementTag><&end_click>
        // -->
        if (attribute.startsWith("run_on_click")) {
            if (!attribute.hasContext(1)) {
                Debug.echoError("An input is required for attribute 'run_on_click'!");
                return null;
            }
            return new ElementTag(textComponentSecret + "[click=RUN_COMMAND;" +
                    FormattedTextHelper.escape(attribute.getContext(1)) + "]" +
                    element.asString() + textComponentSecret + "[/click]")
                    .getObjectAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <ElementTag.suggest_on_click[<command>]>
        // @returns ElementTag
        // @group conversion
        // @description
        // Returns a copy of the element that suggests the specified command to the player who clicked the element.
        // Equivalent to <&click[<command>].type[SUGGEST_COMMAND]><ElementTag><&end_click>
        // -->
        if (attribute.startsWith("suggest_on_click")) {
            if (!attribute.hasContext(1)) {
                Debug.echoError("An input is required for attribute 'suggest_on_click'!");
                return null;
            }
            return new ElementTag(textComponentSecret + "[click=SUGGEST_COMMAND;" +
                    FormattedTextHelper.escape(attribute.getContext(1)) + "]" +
                    element.asString() + textComponentSecret + "[/click]")
                    .getObjectAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <ElementTag.page_on_click[<#>]>
        // @returns ElementTag
        // @group conversion
        // @description
        // Returns a copy of the element that makes the player who clicked the element turn to a specific page number in the book they are viewing.
        // Equivalent to <&click[<#>].type[CHANGE_PAGE]><ElementTag><&end_click>
        // -->
        if (attribute.startsWith("page_on_click")) {
            if (!attribute.hasContext(1)) {
                Debug.echoError("An input is required for attribute 'page_on_click'!");
                return null;
            }

            ElementTag page = new ElementTag(attribute.getContext(1));
            if (!page.isInt() || page.asInt() < 1) {
                Debug.echoError("A positive integer input is required for attribute 'page_on_click'!");
                return null;
            }

            return new ElementTag(textComponentSecret + "[click=CHANGE_PAGE;" +
                    page.asInt() + "]" + element.asString() + textComponentSecret + "[/click]")
                    .getObjectAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <ElementTag.url_on_click[<url>]>
        // @returns ElementTag
        // @group conversion
        // @description
        // Returns a copy of the element that makes the player who clicked the element open the specified URL.
        // Equivalent to <&click[<url>].type[OPEN_URL]><ElementTag><&end_click>
        // -->
        if (attribute.startsWith("url_on_click")) {
            if (!attribute.hasContext(1)) {
                Debug.echoError("An input is required for attribute 'url_on_click'!");
                return null;
            }
            return new ElementTag(textComponentSecret + "[click=OPEN_URL;" +
                    FormattedTextHelper.escape(attribute.getContext(1)) + "]" +
                    element.asString() + textComponentSecret + "[/click]")
                    .getObjectAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <ElementTag.file_on_click[<filepath>]>
        // @returns ElementTag
        // @group conversion
        // @description
        // Returns a copy of the element that makes the player who clicked the element open a file on their computer.
        // Equivalent to <&click[<filepath>].type[OPEN_FILE]><ElementTag><&end_click>
        // -->
        if (attribute.startsWith("file_on_click")) {
            if (!attribute.hasContext(1)) {
                Debug.echoError("An input is required for attribute 'file_on_click'!");
                return null;
            }
            return new ElementTag(textComponentSecret + "[click=OPEN_FILE;" +
                    FormattedTextHelper.escape(attribute.getContext(1)) + "]" +
                    element.asString() + textComponentSecret + "[/click]")
                    .getObjectAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <ElementTag.insert_on_shift_click[<text>]>
        // @returns ElementTag
        // @group conversion
        // @description
        // Returns a copy of the element that sends the specified text to the chat when a player shift + left clicks the element.
        // Equivalent to <&insertion[<text>]><ElementTag><&end_insertion>
        // -->
        if (attribute.startsWith("insert_on_shift_click")) {
            if (!attribute.hasContext(1)) {
                Debug.echoError("An input is required for attribute 'insert_on_shift_click'!");
                return null;
            }
            return new ElementTag(textComponentSecret + "[insertion=" +
                    FormattedTextHelper.escape(attribute.getContext(1)) + "]" +
                    element.asString() + textComponentSecret + "[/insertion]")
                    .getObjectAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public String getPropertyString() {
        return null;
    }

    @Override
    public String getPropertyId() {
        return "BukkitElementProperties";
    }

    @Override
    public void adjust(Mechanism mechanism) {
        // None
    }
}
