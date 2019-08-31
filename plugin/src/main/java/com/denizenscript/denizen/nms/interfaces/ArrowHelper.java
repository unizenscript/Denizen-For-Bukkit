package com.denizenscript.denizen.nms.interfaces;

import org.bukkit.Color;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.List;

public interface ArrowHelper {

    boolean isArrow(Entity entity);

    boolean isTippedArrow(Entity entity);

    boolean isInBlock(Entity entity);

    Block getAttachedBlock(Entity entity);

    boolean isCritical(Entity entity);

    void setCritical(Entity entity, boolean isCrit);

    double getDamage(Entity entity);

    void setDamage(Entity entity, double damage);

    int getKnockbackStrength(Entity entity);

    void setKnockbackStrength(Entity entity, int strength);

    String getPickupStatus(Entity entity);

    void setPickupStatus(Entity entity, String status);

    PotionData getBasePotionData(Entity entity);

    void setBasePotionData(Entity entity, PotionType type, boolean upgrade, boolean extend);

    Color getColor(Entity entity);

    void setColor(Entity entity, Color color);

    boolean hasCustomEffects(Entity entity);

    boolean haaCustomEffect(Entity entity, PotionEffectType effect);

    List<PotionEffect> getCustomEffects(Entity entity);

    boolean addCustomEffect(Entity entity, PotionEffectType effect, int duration, int amplifier, boolean ambient, boolean particles, boolean icon, Color color, boolean overwrite);

    boolean removeCustomEffect(Entity entity, PotionEffectType effect);

    void clearCustomEffects(Entity entity);
}
