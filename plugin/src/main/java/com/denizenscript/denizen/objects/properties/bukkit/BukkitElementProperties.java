package com.denizenscript.denizen.objects.properties.bukkit;

import com.denizenscript.denizen.nms.NMSHandler;
import com.denizenscript.denizen.objects.*;
import com.denizenscript.denizen.scripts.containers.core.FormatScriptContainer;
import com.denizenscript.denizen.scripts.containers.core.ItemScriptHelper;
import com.denizenscript.denizen.utilities.FormattedTextHelper;
import com.denizenscript.denizencore.objects.Argument;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizen.tags.BukkitTagContext;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.Mechanism;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.objects.properties.Property;
import com.denizenscript.denizencore.scripts.ScriptRegistry;
import com.denizenscript.denizencore.tags.Attribute;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.ChatColor;

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

    public static final String[] handledMechs = new String[] {
    }; // None

    static char textComponentSecret = net.md_5.bungee.api.ChatColor.COLOR_CHAR;

    ElementTag element;

    public static void registerTags() {

        // <--[tag]
        // @attribute <ElementTag.as_chunk>
        // @returns ChunkTag
        // @group conversion
        // @description
        // Returns the element as a chunk. Note: the value must be a valid chunk.
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("as_chunk", (attribute, object) -> {
            return ElementTag.handleNull(object.asString(), ChunkTag.valueOf(object.asString(),
                    new BukkitTagContext(attribute.getScriptEntry(), false)), "ChunkTag", attribute.hasAlternative());
        }, "aschunk");

        // <--[tag]
        // @attribute <ElementTag.as_color>
        // @returns ColorTag
        // @group conversion
        // @description
        // Returns the element as a ColorTag. Note: the value must be a valid color.
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("as_color", (attribute, object) -> {
            return ElementTag.handleNull(object.asString(), ColorTag.valueOf(object.asString(),
                    new BukkitTagContext(attribute.getScriptEntry(), false)), "ColorTag", attribute.hasAlternative());
        }, "ascolor");

        // <--[tag]
        // @attribute <ElementTag.as_cuboid>
        // @returns CuboidTag
        // @group conversion
        // @description
        // Returns the element as a cuboid. Note: the value must be a valid cuboid.
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("as_cuboid", (attribute, object) -> {
            return ElementTag.handleNull(object.asString(), CuboidTag.valueOf(object.asString(),
                    new BukkitTagContext(attribute.getScriptEntry(), false)), "CuboidTag", attribute.hasAlternative());
        }, "ascuboid");

        // <--[tag]
        // @attribute <ElementTag.as_entity>
        // @returns EntityTag
        // @group conversion
        // @description
        // Returns the element as an entity. Note: the value must be a valid entity.
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("as_entity", (attribute, object) -> {
            return ElementTag.handleNull(object.asString(), EntityTag.valueOf(object.asString(),
                    new BukkitTagContext(attribute.getScriptEntry(), false)), "EntityTag", attribute.hasAlternative());
        }, "asentity");

        // <--[tag]
        // @attribute <ElementTag.as_inventory>
        // @returns InventoryTag
        // @group conversion
        // @description
        // Returns the element as an inventory. Note: the value must be a valid inventory.
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("as_inventory", (attribute, object) -> {
            return ElementTag.handleNull(object.asString(), InventoryTag.valueOf(object.asString(),
                    new BukkitTagContext(attribute.getScriptEntry(), false)), "InventoryTag", attribute.hasAlternative());
        }, "asinventory");

        // <--[tag]
        // @attribute <ElementTag.as_item>
        // @returns ItemTag
        // @group conversion
        // @description
        // Returns the element as an item. Note: the value must be a valid item.
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("as_item", (attribute, object) -> {
            return ElementTag.handleNull(object.asString(), ItemTag.valueOf(object.asString(),
                    new BukkitTagContext(attribute.getScriptEntry(), false)), "ItemTag", attribute.hasAlternative());
        }, "asitem");

        // <--[tag]
        // @attribute <ElementTag.as_location>
        // @returns LocationTag
        // @group conversion
        // @description
        // Returns the element as a location. Note: the value must be a valid location.
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("as_location", (attribute, object) -> {
            return ElementTag.handleNull(object.asString(), LocationTag.valueOf(object.asString(),
                    new BukkitTagContext(attribute.getScriptEntry(), false)), "LocationTag", attribute.hasAlternative());
        }, "aslocation");

        // <--[tag]
        // @attribute <ElementTag.as_material>
        // @returns MaterialTag
        // @group conversion
        // @description
        // Returns the element as a material. Note: the value must be a valid material.
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("as_material", (attribute, object) -> {
            return ElementTag.handleNull(object.asString(), MaterialTag.valueOf(object.asString(),
                    new BukkitTagContext(attribute.getScriptEntry(), false)), "MaterialTag", attribute.hasAlternative());
        }, "asmaterial");

        // <--[tag]
        // @attribute <ElementTag.as_npc>
        // @returns NPCTag
        // @group conversion
        // @description
        // Returns the element as an NPC. Note: the value must be a valid NPC.
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("as_npc", (attribute, object) -> {
            return ElementTag.handleNull(object.asString(), NPCTag.valueOf(object.asString(),
                    new BukkitTagContext(attribute.getScriptEntry(), false)), "NPCTag", attribute.hasAlternative());
        }, "asnpc");

        // <--[tag]
        // @attribute <ElementTag.as_player>
        // @returns PlayerTag
        // @group conversion
        // @description
        // Returns the element as a player. Note: the value must be a valid player. Can be online or offline.
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("as_player", (attribute, object) -> {
            return ElementTag.handleNull(object.asString(), PlayerTag.valueOf(object.asString(),
                    new BukkitTagContext(attribute.getScriptEntry(), false)), "PlayerTag", attribute.hasAlternative());
        }, "asplayer");

        // <--[tag]
        // @attribute <ElementTag.as_world>
        // @returns WorldTag
        // @group conversion
        // @description
        // Returns the element as a world.
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("as_world", (attribute, object) -> {
            return ElementTag.handleNull(object.asString(), WorldTag.valueOf(object.asString(),
                    new BukkitTagContext(attribute.getScriptEntry(), false)), "WorldTag", attribute.hasAlternative());
        }, "asworld");

        // <--[tag]
        // @attribute <ElementTag.as_plugin>
        // @returns PluginTag
        // @group conversion
        // @description
        // Returns the element as a plugin. Note: the value must be a valid plugin.
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("as_plugin", (attribute, object) -> {
            return ElementTag.handleNull(object.asString(), PluginTag.valueOf(object.asString(),
                    new BukkitTagContext(attribute.getScriptEntry(), false)), "PluginTag", attribute.hasAlternative());
        }, "asplugin");

        // <--[tag]
        // @attribute <ElementTag.last_color>
        // @returns ElementTag
        // @group text checking
        // @description
        // Returns the ChatColors used last in an element.
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("last_color", (attribute, object) -> {
            return new ElementTag(ChatColor.getLastColors(object.asString()));
        });

        // <--[tag]
        // @attribute <ElementTag.format[<script>]>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Returns the text re-formatted according to a format script.
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("format", (attribute, object) -> {
            if (!attribute.hasContext(1)) {
                return null;
            }
            FormatScriptContainer format = ScriptRegistry.getScriptContainer(attribute.getContext(1));
            if (format == null) {
                Debug.echoError("Could not find format script matching '" + attribute.getContext(1) + "'");
                return null;
            }
            else {
                return new ElementTag(format.getFormattedText(object.asString(),
                        attribute.getScriptEntry() != null ? ((BukkitScriptEntryData) attribute.getScriptEntry().entryData).getNPC() : null,
                        attribute.getScriptEntry() != null ? ((BukkitScriptEntryData) attribute.getScriptEntry().entryData).getPlayer() : null));
            }
        });

        // <--[tag]
        // @attribute <ElementTag.strip_color>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Returns the element with all color encoding stripped.
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("strip_color", (attribute, object) -> {
            return new ElementTag(ChatColor.stripColor(object.asString()));
        });

        // <--[tag]
        // @attribute <ElementTag.parse_color[(<prefix>)]>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Returns the element with all color codes parsed.
        // Optionally, specify a character to prefix the color ids. Defaults to '&' if not specified.
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("parse_color", (attribute, object) -> {
            char prefix = '&';
            if (attribute.hasContext(1)) {
                prefix = attribute.getContext(1).charAt(0);
            }
            return new ElementTag(ChatColor.translateAlternateColorCodes(prefix, object.asString()));
        });

        // <--[tag]
        // @attribute <ElementTag.to_itemscript_hash>
        // @returns ElementTag
        // @group conversion
        // @description
        // Shortens the element down to an itemscript hash ID, made of invisible color codes.
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("to_itemscript_hash", (attribute, object) -> {
            return new ElementTag(ItemScriptHelper.createItemScriptID(object.asString()));
        });

        // <--[tag]
        // @attribute <ElementTag.to_secret_colors>
        // @returns ElementTag
        // @group conversion
        // @description
        // Hides the element's text in invisible color codes.
        // Inverts <@link tag ElementTag.from_secret_colors>.
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("to_secret_colors", (attribute, object) -> {
            String text = object.asString();
            byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
            String hex = DatatypeConverter.printHexBinary(bytes);
            StringBuilder colors = new StringBuilder(text.length() * 2);
            for (int i = 0; i < hex.length(); i++) {
                colors.append(ChatColor.COLOR_CHAR).append(hex.charAt(i));
            }
            return new ElementTag(colors.toString());
        });

        // <--[tag]
        // @attribute <ElementTag.from_secret_colors>
        // @returns ElementTag
        // @group conversion
        // @description
        // Un-hides the element's text from invisible color codes back to normal text.
        // Inverts <@link tag ElementTag.to_secret_colors>.
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("from_secret_colors", (attribute, object) -> {
            String text = object.asString().replace(String.valueOf(ChatColor.COLOR_CHAR), "");
            byte[] bytes = DatatypeConverter.parseHexBinary(text);
            return new ElementTag(new String(bytes, StandardCharsets.UTF_8));
        });

        // <--[tag]
        // @attribute <ElementTag.to_raw_json>
        // @returns ElementTag
        // @group conversion
        // @description
        // Converts normal colored text to Minecraft-style "raw JSON" format.
        // Inverts <@link tag ElementTag.from_raw_json>.
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("to_raw_json", (attribute, object) -> {
            return new ElementTag(ComponentSerializer.toString(FormattedTextHelper.parse(object.asString())));
        });

        // <--[tag]
        // @attribute <ElementTag.from_raw_json>
        // @returns ElementTag
        // @group conversion
        // @description
        // Un-hides the element's text from invisible color codes back to normal text.
        // Inverts <@link tag ElementTag.to_raw_json>.
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("from_raw_json", (attribute, object) -> {
            return new ElementTag(FormattedTextHelper.stringify(ComponentSerializer.parse(object.asString())));
        });

        // <--[tag]
        // @attribute <ElementTag.on_hover[<message>]>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Adds a hover message to the element, which makes the element display the input hover text when the mouse is left over it.
        //
        // This tag works for chat outputs and books. It does not work in other places (inside an item, title command, etc. this is not valid).
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("on_hover", (attribute, object) -> {
            if (!attribute.hasContext(1)) {
                return null;
            }
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
            //
            // This tag works for chat outputs and books. It does not work in other places (inside an item, title command, etc. this is not valid).
            // -->
            if (attribute.startsWith("type", 2)) {
                type = attribute.getContext(2);
                attribute.fulfill(1);

                if (type.equalsIgnoreCase("SHOW_ITEM")) {
                    if (Argument.valueOf(attribute.getContext(1)).matchesArgumentType(ItemTag.class)) {
                        hoverText = NMSHandler.getItemHelper().getRawHoverText(ItemTag.valueOf(attribute.getContext(1)).getItemStack());
                    }
                }
                else if (type.equalsIgnoreCase("SHOW_ENTITY")) {
                    if (Argument.valueOf(attribute.getContext(1)).matchesArgumentType(EntityTag.class)) {
                        hoverText = NMSHandler.getEntityHelper().getRawHoverText(EntityTag.valueOf(attribute.getContext(1)).getBukkitEntity());
                    }
                }
            }
            return new ElementTag(ChatColor.COLOR_CHAR + "[hover=" + type + ";" + FormattedTextHelper.escape(hoverText) + "]"
                    + object.asString() + ChatColor.COLOR_CHAR + "[/hover]");
        });

        // <--[tag]
        // @attribute <ElementTag.on_click[<click command>]>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Adds a click command to the element, which makes the element execute the input command when clicked.
        //
        // This tag works for chat outputs and books. It does not work in other places (inside an item, title command, etc. this is not valid).
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("on_click", (attribute, object) -> {
            if (!attribute.hasContext(1)) {
                return null;
            }
            String clickText = attribute.getContext(1);
            String type = "RUN_COMMAND";

            // <--[tag]
            // @attribute <ElementTag.on_click[<message>].type[<type>]>
            // @returns ElementTag
            // @group text manipulation
            // @description
            // Adds a click command to the element, which makes the element execute the input command when clicked.
            // Optionally specify the hover type as one of: OPEN_URL, OPEN_FILE, RUN_COMMAND, SUGGEST_COMMAND, or CHANGE_PAGE.
            //
            // This tag works for chat outputs and books. It does not work in other places (inside an item, title command, etc. this is not valid).
            // -->
            if (attribute.startsWith("type", 2)) {
                type = attribute.getContext(2);
                attribute.fulfill(1);
            }
            return new ElementTag(ChatColor.COLOR_CHAR + "[click=" + type + ";" + FormattedTextHelper.escape(clickText) + "]"
                    + object.asString() + ChatColor.COLOR_CHAR + "[/click]");
        });

        // <--[tag]
        // @attribute <ElementTag.with_insertion[<message>]>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Adds an insertion message to the element, which makes the element insert the input message to chat when shift-clicked.
        //
        // This tag works for chat outputs and books. It does not work in other places (inside an item, title command, etc. this is not valid).
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("with_insertion", (attribute, object) -> {
            if (!attribute.hasContext(1)) {
                return null;
            }
            String insertionText = attribute.getContext(1);
            return new ElementTag(ChatColor.COLOR_CHAR + "[insertion="  + FormattedTextHelper.escape(insertionText) + "]"
                    + object.asString() + ChatColor.COLOR_CHAR + "[/insertion]");
        });

        // <--[tag]
        // @attribute <ElementTag.text_hover[<text>]>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Returns a copy of the element that displays the specified hover text when the mouse is left over the element.
        // Equivalent to <&hover[<text>]><ElementTag><&end_hover>
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("text_hover", (attribute, object) -> {
            String hoverText = attribute.getContext(1);
            return new ElementTag(textComponentSecret + "[hover=SHOW_TEXT;" +
                    FormattedTextHelper.escape(hoverText) + "]" +
                    object.asString() + textComponentSecret + "[/hover]")
                    .getObjectAttribute(attribute.fulfill(1));
        });

        // <--[tag]
        // @attribute <ElementTag.item_hover[<item>/<text>]>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Returns a copy of the element that displays the specified item when the mouse is left over the element.
        // If text is used, then it must specify a valid item in JSON format.
        // Equivalent to <&hover[<text>].type[SHOW_ITEM]><ElementTag><&end_hover>
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("item_hover", (attribute, object) -> {
            String hoverText = attribute.getContext(1);
            return new ElementTag(textComponentSecret + "[hover=SHOW_ITEM;" +
                    FormattedTextHelper.escape(hoverText) + "]" +
                    object.asString() + textComponentSecret + "[/hover]")
                    .getObjectAttribute(attribute.fulfill(1));
        });

        // <--[tag]
        // @attribute <ElementTag.entity_hover[<entity>/<text>]>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Returns a copy of the element that displays the specified hover text when the mouse is left over the element.
        // If text is used, then it must follow the JSON format {"id":"UUID","type":"ENTITY_TYPE","name":"CUSTOM NAME"} (but none of the three keys are required).
        // Equivalent to <&hover[<text>].type[SHOW_ENTITY]><ElementTag><&end_hover>
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("entity_hover", (attribute, object) -> {
            String hoverText = attribute.getContext(1);
            return new ElementTag(textComponentSecret + "[hover=SHOW_ENTITY;" +
                    FormattedTextHelper.escape(hoverText) + "]" +
                    object.asString() + textComponentSecret + "[/hover]")
                    .getObjectAttribute(attribute.fulfill(1));
        });

        // <--[tag]
        // @attribute <ElementTag.run_on_click[<command>]>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Returns a copy of the element that runs the specified command as the player who clicked the element.
        // Equivalent to <&click[<command>]><ElementTag><&end_click>
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("run_on_click", (attribute, object) -> {
            String clickText = attribute.getContext(1);
            return new ElementTag(textComponentSecret + "[click=RUN_COMMAND;" +
                    FormattedTextHelper.escape(clickText) + "]" +
                    object.asString() + textComponentSecret + "[/click]")
                    .getObjectAttribute(attribute.fulfill(1));
        });

        // <--[tag]
        // @attribute <ElementTag.suggest_on_click[<command>]>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Returns a copy of the element that suggests the specified command to the player who clicked the element.
        // Equivalent to <&click[<command>].type[SUGGEST_COMMAND]><ElementTag><&end_click>
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("suggest_on_click", (attribute, object) -> {
            String clickText = attribute.getContext(1);
            return new ElementTag(textComponentSecret + "[click=SUGGEST_COMMAND;" +
                    FormattedTextHelper.escape(clickText) + "]" +
                    object.asString() + textComponentSecret + "[/click]")
                    .getObjectAttribute(attribute.fulfill(1));
        });

        // <--[tag]
        // @attribute <ElementTag.page_on_click[<#>]>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Returns a copy of the element that makes the player who clicked the element turn to a specific page number in the book they are viewing.
        // A number input is required for this tag!
        // Equivalent to <&click[<#>].type[CHANGE_PAGE]><ElementTag><&end_click>
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("page_on_click", (attribute, object) -> {
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
                    page.asInt() + "]" + object.asString() + textComponentSecret + "[/click]")
                    .getObjectAttribute(attribute.fulfill(1));
        });

        // <--[tag]
        // @attribute <ElementTag.url_on_click[<url>]>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Returns a copy of the element that makes the player who clicked the element open the specified URL.
        // Equivalent to <&click[<url>].type[OPEN_URL]><ElementTag><&end_click>
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("url_on_click", (attribute, object) ->  {
            String clickText = attribute.getContext(1);
            return new ElementTag(textComponentSecret + "[click=OPEN_URL;" +
                    FormattedTextHelper.escape(clickText) + "]" +
                    object.asString() + textComponentSecret + "[/click]")
                    .getObjectAttribute(attribute.fulfill(1));
        });

        // <--[tag]
        // @attribute <ElementTag.file_on_click[<filepath>]>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Returns a copy of the element that makes the player who clicked the element open a file on their computer.
        // Equivalent to <&click[<filepath>].type[OPEN_FILE]><ElementTag><&end_click>
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("file_on_click", (attribute, object) -> {
            String clickText = attribute.getContext(1);
            return new ElementTag(textComponentSecret + "[click=OPEN_FILE;" +
                    FormattedTextHelper.escape(clickText) + "]" +
                    object.asString() + textComponentSecret + "[/click]")
                    .getObjectAttribute(attribute.fulfill(1));
        });

        // @attribute <ElementTag.no_reset>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Makes a color code (&0123456789abcdef) not reset other formatting details.
        // Use like '<&c.no_reset>' or '<red.no_reset>'.
        //
        // This tag works for chat outputs and books. It does not work in other places (inside an item, title command, etc. this is not valid).
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("no_reset", (attribute, object) -> {
            if (object.asString().length() == 2 && object.asString().charAt(0) == ChatColor.COLOR_CHAR) {
                return new ElementTag(ChatColor.COLOR_CHAR + "[color=" + object.asString().charAt(1) + "]");
            }
            return null;
        });

        // <--[tag]
        // @attribute <ElementTag.end_format>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Makes a chat format code (&klmno) be the end of a format, as opposed to the start.
        // Use like '<&o.end_format>' or '<italic.end_format>'.
        //
        // This tag works for chat outputs and books. It does not work in other places (inside an item, title command, etc. this is not valid).
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("end_format", (attribute, object) -> {
            if (object.asString().length() == 2 && object.asString().charAt(0) == ChatColor.COLOR_CHAR) {
                return new ElementTag(ChatColor.COLOR_CHAR + "[reset=" + object.asString().charAt(1) + "]");
            }
            return null;
        });

        // <--[tag]
        // @attribute <ElementTag.italicize>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Makes the input text italic. Equivalent to "<&o><ELEMENT_HERE><&o.end_format>"
        //
        // This tag works for chat outputs and books. It does not work in other places (inside an item, title command, etc. this is not valid).
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("italicize", (attribute, object) -> {
            return new ElementTag(ChatColor.ITALIC + object.asString() + ChatColor.COLOR_CHAR + "[reset=o]");
        });

        // <--[tag]
        // @attribute <ElementTag.bold>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Makes the input text bold. Equivalent to "<&l><ELEMENT_HERE><&l.end_format>"
        //
        // This tag works for chat outputs and books. It does not work in other places (inside an item, title command, etc. this is not valid).
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("bold", (attribute, object) -> {
            return new ElementTag(ChatColor.BOLD + object.asString() + ChatColor.COLOR_CHAR + "[reset=l]");
        });

        // <--[tag]
        // @attribute <ElementTag.underline>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Makes the input text underlined. Equivalent to "<&n><ELEMENT_HERE><&n.end_format>"
        //
        // This tag works for chat outputs and books. It does not work in other places (inside an item, title command, etc. this is not valid).
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("underline", (attribute, object) -> {
            return new ElementTag(ChatColor.UNDERLINE + object.asString() + ChatColor.COLOR_CHAR + "[reset=n]");
        });

        // <--[tag]
        // @attribute <ElementTag.strikethrough>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Makes the input text struck-through. Equivalent to "<&m><ELEMENT_HERE><&m.end_format>"
        //
        // This tag works for chat outputs and books. It does not work in other places (inside an item, title command, etc. this is not valid).
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("strikethrough", (attribute, object) -> {
            return new ElementTag(ChatColor.STRIKETHROUGH + object.asString() + ChatColor.COLOR_CHAR + "[reset=m]");
        });

        // <--[tag]
        // @attribute <ElementTag.obfuscate>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Makes the input text obfuscated. Equivalent to "<&k><ELEMENT_HERE><&k.end_format>"
        //
        // This tag works for chat outputs and books. It does not work in other places (inside an item, title command, etc. this is not valid).
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("obfuscate", (attribute, object) -> {
            return new ElementTag(ChatColor.MAGIC + object.asString() + ChatColor.COLOR_CHAR + "[reset=k]");
        });

        // <--[tag]
        // @attribute <ElementTag.color[<color>]>
        // @returns ElementTag
        // @group text manipulation
        // @description
        // Makes the input text colored by the input color. Equivalent to "<COLOR><ELEMENT_HERE><COLOR.end_format>"
        // Color can be either a color name, or code.
        // That is: ".color[gold]" and ".color[6]" are both valid.
        //
        // This tag works for chat outputs and books. It does not work in other places (inside an item, title command, etc. this is not valid).
        // -->
        PropertyParser.<BukkitElementProperties>registerTag("color", (attribute, object) -> {
            if (!attribute.hasContext(1)) {
                return null;
            }
            String colorName = attribute.getContext(1);
            ChatColor color = null;
            if (colorName.length() == 1) {
                color = ChatColor.getByChar(colorName.charAt(0));
            }
            if (color == null) {
                try {
                    color = ChatColor.valueOf(colorName.toUpperCase());
                }
                catch (IllegalArgumentException ex) {
                    if (!attribute.hasAlternative()) {
                        Debug.echoError("Color '" + colorName + "' doesn't exist (for ElementTag.color[...]).");
                    }
                    return null;
                }
            }
            return new ElementTag(color + object.asString() + ChatColor.COLOR_CHAR + "[reset=" + color.getChar() + "]");
        });
    }

    public String asString() {
        return element.asString();
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
