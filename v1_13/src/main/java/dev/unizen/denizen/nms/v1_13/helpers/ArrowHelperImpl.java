package dev.unizen.denizen.nms.v1_13.helpers;

import com.denizenscript.denizen.nms.interfaces.ArrowHelper;
import com.denizenscript.denizen.utilities.debugging.Debug;
import org.bukkit.Color;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TippedArrow;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.List;

public class ArrowHelperImpl implements ArrowHelper {

    @Override
    public boolean isArrow(Entity entity) {
        if (entity == null) {
            return false;
        }
        return entity instanceof Arrow;
    }

    @Override
    public boolean isTippedArrow(Entity entity) {
        if (entity == null) {
            return false;
        }
        return entity instanceof TippedArrow;
    }

    @Override
    public boolean isInBlock(Entity entity) {
        if (!isArrow(entity)) {
            return false;
        }
        return ((Arrow) entity).isInBlock();
    }

    @Override
    public Block getAttachedBlock(Entity entity) {
        if (!isArrow(entity)) {
            return null;
        }
        return ((Arrow) entity).getAttachedBlock();
    }

    @Override
    public boolean isCritical(Entity entity) {
        if (!isArrow(entity)) {
            return false;
        }
        return ((Arrow) entity).isCritical();
    }

    @Override
    public void setCritical(Entity entity, boolean isCrit) {
        if (!isArrow(entity)) {
            return;
        }
        ((Arrow) entity).setCritical(isCrit);
    }

    @Override
    public double getDamage(Entity entity) {
        if (!isArrow(entity)) {
            return -1;
        }
        return ((Arrow) entity).getDamage();
    }

    @Override
    public void setDamage(Entity entity, double damage) {
        if (!isArrow(entity)) {
            return;
        }
        ((Arrow) entity).setDamage(damage);
    }

    @Override
    public int getKnockbackStrength(Entity entity) {
        if (!isArrow(entity)) {
            return -1;
        }
        return ((Arrow) entity).getKnockbackStrength();
    }

    @Override
    public void setKnockbackStrength(Entity entity, int strength) {
        if (!isArrow(entity)) {
            return;
        }
        ((Arrow) entity).setKnockbackStrength(strength);
    }

    @Override
    public String getPickupStatus(Entity entity) {
        if (!isArrow(entity)) {
            return null;
        }
        return ((Arrow) entity).getPickupStatus().toString();
    }

    @Override
    public void setPickupStatus(Entity entity, String status) {
        if (!isArrow(entity)) {
            return;
        }
        Arrow.PickupStatus pickupStatus;
        try {
            pickupStatus = Arrow.PickupStatus.valueOf(status.toUpperCase());
        }
        catch (Exception e) {
            Debug.echoError("Invalid arrow pickup status! Input was: '" + status + "'.");
            return;
        }
        ((Arrow) entity).setPickupStatus(pickupStatus);
    }

    @Override
    public PotionData getBasePotionData(Entity entity) {
        if (!isTippedArrow(entity)) {
            return null;
        }
        return ((TippedArrow) entity).getBasePotionData();
    }

    @Override
    public void setBasePotionData(Entity entity, PotionType type, boolean upgrade, boolean extend) {
        if (!isTippedArrow(entity) || type == null) {
            return;
        }
        if (type == PotionType.UNCRAFTABLE) {
            Debug.echoError("A base potion effect cannot be type 'UNCRAFTABLE'! Not applying this change.");
            return;
        }
        if (!type.isUpgradeable() && upgrade) {
            Debug.echoError("A base potion effect of type '" + type.name() + "' is not upgradeable! Reverting this to 'false'...");
            upgrade = false;
        }
        if (!type.isExtendable() && extend) {
            Debug.echoError("A base potion effect of type '" + type.name() + "' is not extendable! Reverting this to 'false'...");
            extend = false;
        }
        ((TippedArrow) entity).setBasePotionData(new PotionData(type, extend, upgrade));
    }

    @Override
    public Color getColor(Entity entity) {
        if (!isTippedArrow(entity)) {
            return null;
        }
        return ((TippedArrow) entity).getColor();
    }

    @Override
    public void setColor(Entity entity, Color color) {
        if (!isTippedArrow(entity) || color == null) {
            return;
        }
        ((TippedArrow) entity).setColor(color);
    }

    @Override
    public boolean hasCustomEffects(Entity entity) {
        if (!isTippedArrow(entity)) {
            return false;
        }
        return ((TippedArrow) entity).hasCustomEffects();
    }

    @Override
    public boolean haaCustomEffect(Entity entity, PotionEffectType effect) {
        if (!isTippedArrow(entity) || effect == null) {
            return false;
        }
        return ((TippedArrow) entity).hasCustomEffect(effect);
    }

    @Override
    public List<PotionEffect> getCustomEffects(Entity entity) {
        if (!isTippedArrow(entity)) {
            return null;
        }
        return ((TippedArrow) entity).getCustomEffects();
    }

    @Override
    public boolean addCustomEffect(Entity entity, PotionEffectType effect, int duration, int amplifier, boolean ambient, boolean particles, boolean icon, Color color, boolean overwrite) {
        if (!isTippedArrow(entity) || effect == null) {
            return false;
        }
        if (color != null) {
            Debug.echoError("Colors for custom effects are deprecated! Please use the dEntity.color property instead.");
        }
        return ((TippedArrow) entity).addCustomEffect(
                new PotionEffect(effect, duration, amplifier, ambient, particles, icon),
                overwrite
        );
    }

    @Override
    public boolean removeCustomEffect(Entity entity, PotionEffectType effect) {
        if (!isTippedArrow(entity) || effect == null) {
            return false;
        }
        return ((TippedArrow) entity).removeCustomEffect(effect);
    }

    @Override
    public void clearCustomEffects(Entity entity) {
        if (!isTippedArrow(entity)) {
            return;
        }
        ((TippedArrow) entity).clearCustomEffects();
    }
}
