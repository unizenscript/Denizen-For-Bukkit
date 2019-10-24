package com.denizenscript.denizen.objects;

import com.denizenscript.denizencore.objects.*;
import com.denizenscript.denizen.tags.BukkitTagContext;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.TagContext;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.Arrays;
import java.util.regex.Matcher;

public class TradeTag implements ObjectTag, Adjustable {

    // <--[language]
    // @name Merchant Trades
    // @group Object System
    // @description
    // Merchant trades are the parts of a special merchant inventory that is typically viewed by right clicking
    // a villager entity. Any number of trades can fit in a single merchant inventory.
    //
    // Trades are represented by TradeTags.
    //
    // The properties that can be used to customize a merchant trade are:
    //
    // result=<item>
    // input=<item>(|<item>)
    // uses=<number of uses>
    // max_uses=<maximum number of uses>
    // has_xp=<true/false>
    //
    // For example, the following task script opens a virtual merchant inventory with two merchant trades. The
    // first trade offers a sponge for two emeralds for a sponge, can be used up to 10 times, and offers XP upon a
    // successful transaction. The second trade has zero maximum uses and displays a barrier.
    //
    // <code>
    // open two trades:
    //     type: task
    //     script:
    //     - opentrades trade@trade[max_uses=10;inputs=emerald[quantity=2];result=sponge|trade@trade[result=barrier]
    // </code>
    //
    // For format info, see <@link language trade@>
    // -->

    // <--[language]
    // @name trade@
    // @group Object Fetcher System
    // @description
    // trade@ refers to the 'object identifier' of a TradeTag. The 'trade@' is notation for Denizen's Object
    // Fetcher. The constructor for a TradeTag is just the text 'trade'. All other data is specified through properties.
    //
    // For general info, see <@link language Merchant Trades>
    //
    // -->

    //////////////////
    //    OBJECT FETCHER
    ////////////////

    public static TradeTag valueOf(String string) {
        return valueOf(string, null);
    }

    @Fetchable("trade")
    public static TradeTag valueOf(String string, TagContext context) {
        if (string == null) {
            return null;
        }

        ///////
        // Handle objects with properties through the object fetcher
        Matcher m = ObjectFetcher.DESCRIBED_PATTERN.matcher(string);
        if (m.matches()) {
            return ObjectFetcher.getObjectFrom(TradeTag.class, string, new BukkitTagContext(((BukkitTagContext) context).player,
                    ((BukkitTagContext) context).npc, false, null, !context.debug, null));
        }

        string = CoreUtilities.toLowerCase(string).replace("trade@", "");
        if (string.toLowerCase().matches("trade")) {
            MerchantRecipe recipe = new MerchantRecipe(new ItemStack(Material.AIR), 0);
            recipe.setIngredients(Arrays.asList(new ItemStack(Material.AIR)));
            return new TradeTag(recipe);
        }
        return null;
    }

    public static boolean matches(String str) {
        return valueOf(str, CoreUtilities.noDebugContext) != null;
    }

    ///////////////
    //   Constructors
    /////////////

    public TradeTag(MerchantRecipe recipe) {
        this.recipe = recipe;
    }

    /////////////////////
    //   INSTANCE FIELDS/METHODS
    /////////////////

    public String toString() {
        return identify();
    }

    private MerchantRecipe recipe;

    public MerchantRecipe getRecipe() {
        return recipe;
    }

    public void setRecipe(MerchantRecipe recipe) {
        this.recipe = recipe;
    }

    //////////////////////////////
    //  DSCRIPT ARGUMENT METHODS
    /////////////////////////

    public String getPrefix() {
        return "trade";
    }

    public TradeTag setPrefix(String prefix) {
        return this;
    }

    public boolean isUnique() {
        return false;
    }

    public String getObjectType() {
        return "Trade";
    }

    public String identify() {
        return getPrefix() + "@trade" + PropertyParser.getPropertiesString(this);
    }

    public String identifySimple() {
        return identify();
    }

    public ObjectTag getObjectAttribute(Attribute attribute) {
        if (attribute == null) {
            return null;
        }

        ObjectTag returned = CoreUtilities.autoPropertyTagObject(this, attribute);
        if (returned != null) {
            return returned;
        }

        return new ElementTag(identify()).getObjectAttribute(attribute);
    }

    public void applyProperty(Mechanism mechanism) {
        adjust(mechanism);
    }

    public void adjust(Mechanism mechanism) {
        CoreUtilities.autoPropertyMechanism(this, mechanism);
    }
}
