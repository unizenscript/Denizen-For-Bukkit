package com.denizenscript.denizen.utilities.nbt;

import com.denizenscript.denizen.utilities.debugging.Debug;
import com.denizenscript.denizen.objects.ColorTag;
import com.denizenscript.denizen.objects.ItemTag;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class LeatherColorer {

    public static void colorArmor(ItemTag item, String colorArg) {

        if (item == null) {
            return;
        }

        if (ColorTag.matches(colorArg)) {

            try {
                LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemStack().getItemMeta();
                meta.setColor(ColorTag.valueOf(colorArg).getColor());
                item.getItemStack().setItemMeta(meta);
            }
            catch (Exception e) {
                Debug.echoError("Unable to color '" + item.identify() + "'.");
            }
        }

    }
}
