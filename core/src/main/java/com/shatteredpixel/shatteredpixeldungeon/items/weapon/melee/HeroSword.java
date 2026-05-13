package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.HolyWeapon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Projecting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.DualDagger;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

import java.util.ArrayList;

public class HeroSword extends MeleeWeapon {

    {
        image = ItemSpriteSheet.HERO_SWORD;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1f;

        unique = true;
        bones = false;
    }

    public enum Ability {
        SNEAK               (Dagger.class),
        HEAVY_BLOW          (HandAxe.class),
        COMBO_STRIKE        (Gloves.class),
        RETRIBUTION         (Greataxe.class),
        CLEAVE              (WornShortsword.class),
        DEFENSIVE_STANCE    (Quarterstaff.class),
        RUNIC_SLASH         (RunicBlade.class),
        SWORD_DANCE         (Scimitar.class),
        HARVEST             (Sickle.class),
        LUNGE               (Rapier.class),
        ANGELIZE            (Bible.class),
        REVERSE_GRIP        (DualDagger.class),
        PARRY               (Nunchaku.class),
        FLASH_SLASH         (WornKatana.class),
        LASH                (Whip.class),
        GUARD               (RoundShield.class);

        public final Class<? extends MeleeWeapon> wepClass;

        Ability(Class<? extends MeleeWeapon> wepClass) {
            this.wepClass = wepClass;
        }

        public String abilityName() {
            return Messages.get(wepClass, "ability_name");
        }
    }

    private MeleeWeapon baseWep;

    public Ability ability;

    public HeroSword() {
        this(null);
    }

    public HeroSword(MeleeWeapon wep) {
        baseWep = wep == null ? new WornShortsword() : wep;

        copyBaseWeapon();
    }

    private void copyBaseWeapon() {
        tier = baseWep.tier;
        DLY = baseWep.DLY;
        //RCH = baseWep.RCH;

        int level = baseWep.trueLevel();
        if (level > this.trueLevel()) {
            this.level(level);
        }

        //don't copy ench or CI bonus

        masteryPotionBonus = baseWep.masteryPotionBonus;
        levelKnown = baseWep.levelKnown;
        cursedKnown = baseWep.cursedKnown;
        cursed = baseWep.cursed;
        augment = baseWep.augment;
        enchantHardened = baseWep.enchantHardened;
    }

    private static final String BASE_WEAPON = "usedWep";
    private static final String ABILITY = "ability";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( BASE_WEAPON, baseWep );
        bundle.put( ABILITY, ability );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        baseWep = (MeleeWeapon) bundle.get( BASE_WEAPON );
        copyBaseWeapon();
        ability = bundle.getEnum( ABILITY, Ability.class );
    }

    @Override
    public int min(int lvl) {
        return baseWep.min(lvl);
    }

    @Override
    public int max(int lvl) {
        return baseWep.max(lvl);
    }

    @Override
    public int STRReq(int lvl) {
        return baseWep.STRReq(lvl);
    }

    @Override
    public int defenseFactor( Char owner ) {
        return baseWep.defenseFactor(owner);
    }

    @Override
    public int reachFactor(Char owner) {
        int reach = baseWep.reachFactor(owner);

        if (hasEnchant(Projecting.class, owner) && !baseWep.hasEnchant(Projecting.class, owner)) {
            return reach + Math.round(Enchantment.genericProcChanceMultiplier(owner));
        } else {
            return reach;
        }
    }

    @Override
    public int value() {
        return -1;
    }

    @Override
    public String name() {
        if (isEquipped(Dungeon.hero) && !hasCurseEnchant() && Dungeon.hero.buff(HolyWeapon.HolyWepBuff.class) != null
                && (Dungeon.hero.subClass != HeroSubClass.PALADIN || enchantment == null)){
            return Messages.get(HolyWeapon.class, "ench_name", trueName());
        } else if (enchantment != null && (cursedKnown || !enchantment.curse())) {
            String name = trueName();
            if (baseWep.enchantment != null && enchantment.getClass() != baseWep.enchantment.getClass()) {
                name = baseWep.enchantment.name( name );
            }
            return enchantment.name( name );
        } else if (baseWep.enchantment != null) {
            return baseWep.enchantment.name( super.name() );
        } else {
            return super.name();
        }
    }

    @Override
    public String info() {
        String info = super.info();

        String baseWepName = baseWep.name() + ((baseWep.level() > 0) ? String.format(" +%d", baseWep.level()) : "");
        info += "\n\n" + Messages.get(this, "properties", baseWepName);
        if (baseWep.enchantment != null && baseWep.enchantment.getClass() != enchantment.getClass()) {
            info += " " + baseWep.enchantment.desc();
        }

        return info;
    }

    @Override
    public String statsInfo() {
        return baseWep.statsInfo();
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (enchantment != null && baseWep.enchantment != null &&
                enchantment.getClass() == baseWep.enchantment.getClass()) {
            Enchantment ench = baseWep.enchantment;
            baseWep.enchantment = null;
            damage = baseWep.proc( attacker, defender, damage );
            baseWep.enchantment = ench;
        } else {
            damage = baseWep.proc( attacker, defender, damage );
        }

        damage = super.proc( attacker, defender, damage );
        return damage;
    }

    @Override
    protected int baseChargeUse(Hero hero, Char target){
        if (ability == Ability.CLEAVE && hero.buff(Sword.CleaveTracker.class) != null) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public String targetingPrompt() {
        switch (ability) {
            case SNEAK: case HEAVY_BLOW: case COMBO_STRIKE: case RETRIBUTION: case CLEAVE: case RUNIC_SLASH: case HARVEST: case LUNGE: case FLASH_SLASH:
                return Messages.get(this, "prompt");
            default:
                return null;
        }
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        int dmgBoost;
        switch (ability) {
            case SNEAK:
                Dagger.sneakAbility(hero, target, 6, 2+buffedLvl(), this);
                break;
            case HEAVY_BLOW:
                //roughly +45% damage
                dmgBoost = augment.damageFactor(Math.round(0.45f*max(buffedLvl())));
                Mace.heavyBlowAbility(hero, target, 1, dmgBoost, this);
                break;
            case COMBO_STRIKE:
                //roughly +45% damage
                dmgBoost = augment.damageFactor(Math.round(0.45f*max(buffedLvl())));
                Sai.comboStrikeAbility(hero, target, 0, dmgBoost, this);
                break;
            case RETRIBUTION:
                //roughly +50% damage
                dmgBoost = augment.damageFactor( Math.round(0.5f * max(buffedLvl())) );
                retributionAbility(hero, target, dmgBoost, this);
                break;
            case CLEAVE:
                dmgBoost = augment.damageFactor(Math.round(0.67f*max(buffedLvl())));
                Sword.cleaveAbility(hero, target, 1, dmgBoost, this);
                break;
            case DEFENSIVE_STANCE:
                defensiveStanceAbility(hero, this);
                break;
            case RUNIC_SLASH:
                runicSlashAbility(hero, target,this);
                break;
            case SWORD_DANCE:
                swordDanceAbility(hero, this);
                break;
            case HARVEST:
                //replaces damage with 50% avg dmg of bleed
                int bleedAmt = augment.damageFactor(Math.round(0.5f*max(buffedLvl())));
                Sickle.harvestAbility(hero, target, 0f, bleedAmt, this);
                break;
            case LUNGE:
                //roughly +67% damage
                dmgBoost = augment.damageFactor(Math.round(0.67f*max(buffedLvl())));
                Rapier.lungeAbility(hero, target, 1, dmgBoost, this);
                break;
            case ANGELIZE:
                Bible.angelAbility(hero, 5+buffedLvl(), this);
                break;
            case REVERSE_GRIP:
                reverseGripAbility(hero, this);
                break;
            case PARRY:
                parryAbility(hero, this);
                break;
            case FLASH_SLASH:
                NormalKatana.flashSlashAbility(hero, target, 0.6f, this);
                break;
            case LASH:
                //roughly +20% damage
                dmgBoost = augment.damageFactor(Math.round(0.2f*max(buffedLvl())));
                lashAbility(hero, dmgBoost, this);
                break;
            case GUARD:
                RoundShield.guardAbility(hero, 5+buffedLvl(), this);
                break;
            default:
                break;
        }
    }

    @Override
    public String abilityName() {
        return Messages.upperCase(ability.abilityName());
    }

    @Override
    public String abilityInfo() {
        String info = Messages.get(this, "prefix");
        int dmgBoost;
        switch (ability) {
            case SNEAK:
                if (levelKnown) {
                    info += " " + Messages.get(ability.wepClass, "ability_desc", 2+buffedLvl());
                } else {
                    info += " " + Messages.get(ability.wepClass, "typical_ability_desc", 2);
                }
                break;
            case HEAVY_BLOW:
                dmgBoost = levelKnown ? 4 + Math.round(1.5f*buffedLvl()) : 4;
                if (levelKnown) {
                    info += " " + Messages.get(ability.wepClass, "ability_desc", augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost));
                } else {
                    info += " " + Messages.get(ability.wepClass, "typical_ability_desc", min(0)+dmgBoost, max(0)+dmgBoost);
                }
                break;
            case COMBO_STRIKE:
                dmgBoost = levelKnown ? 3 + buffedLvl() : 3;
                if (levelKnown) {
                    info += " " + Messages.get(ability.wepClass, "ability_desc", augment.damageFactor(dmgBoost));
                } else {
                    info += " " + Messages.get(ability.wepClass, "typical_ability_desc", augment.damageFactor(dmgBoost));
                }
                break;
            case RETRIBUTION:
                dmgBoost = levelKnown ? 15 + 2*buffedLvl() : 15;
                if (levelKnown) {
                    info += " " + Messages.get(ability.wepClass, "ability_desc", augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost));
                } else {
                    info += " " + Messages.get(ability.wepClass, "typical_ability_desc", min(0)+dmgBoost, max(0)+dmgBoost);
                }
                break;
            case CLEAVE:
                dmgBoost = levelKnown ? 3 + buffedLvl() : 3;
                if (levelKnown) {
                    info += " " + Messages.get(ability.wepClass, "ability_desc", augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost));
                } else {
                    info += " " + Messages.get(ability.wepClass, "typical_ability_desc", min(0)+dmgBoost, max(0)+dmgBoost);
                }
                break;
            case DEFENSIVE_STANCE:
            case SWORD_DANCE:
                if (levelKnown) {
                    info += " " + Messages.get(ability.wepClass, "ability_desc", 4+buffedLvl());
                } else {
                    info += " " + Messages.get(ability.wepClass, "typical_ability_desc", 4);
                }
                break;
            case RUNIC_SLASH:
                if (levelKnown) {
                    info += " " + Messages.get(ability.wepClass, "ability_desc", 300+50*buffedLvl());
                } else {
                    info += " " + Messages.get(ability.wepClass, "typical_ability_desc", 300);
                }
                break;
            case HARVEST:
                int bleedAmt = levelKnown ? Math.round(15f + 2.5f*buffedLvl()) : 15;
                if (levelKnown) {
                    info += " " + Messages.get(ability.wepClass, "ability_desc", augment.damageFactor(bleedAmt));
                } else {
                    info += " " + Messages.get(ability.wepClass, "typical_ability_desc", bleedAmt);
                }
                break;
            case LUNGE:
                dmgBoost = levelKnown ? 5 + Math.round(1.5f*buffedLvl()) : 5;
                if (levelKnown) {
                    info += " " + Messages.get(ability.wepClass, "ability_desc", augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost));
                } else {
                    info += " " + Messages.get(ability.wepClass, "typical_ability_desc", min(0)+dmgBoost, max(0)+dmgBoost);
                }
                break;
            case ANGELIZE:
            case REVERSE_GRIP:
                if (levelKnown) {
                    info += " " + Messages.get(ability.wepClass, "ability_desc", 6+buffedLvl());
                } else {
                    info += " " + Messages.get(ability.wepClass, "typical_ability_desc", 6);
                }
                break;
            case PARRY:
                if (levelKnown) {
                    info += " " + Messages.get(ability.wepClass, "ability_desc");
                } else {
                    info += " " + Messages.get(ability.wepClass, "typical_ability_desc");
                }
                break;
            case FLASH_SLASH:
                if (levelKnown) {
                    info += " " + Messages.get(ability.wepClass, "ability_desc", Messages.decimalFormat("#.##", 0.6f));
                } else {
                    info += " " + Messages.get(ability.wepClass, "typical_ability_desc", Messages.decimalFormat("#.##", 0.6f));
                }
                break;
            case LASH:
                if (levelKnown) {
                    info += " " + Messages.get(ability.wepClass, "ability_desc", augment.damageFactor(min()), augment.damageFactor(max()));
                } else {
                    info += " " + Messages.get(ability.wepClass, "typical_ability_desc", min(0), max(0));
                }
                break;
            case GUARD:
                if (levelKnown) {
                    info += " " + Messages.get(ability.wepClass, "ability_desc", 5+buffedLvl());
                } else {
                    info += " " + Messages.get(ability.wepClass, "typical_ability_desc", 5);
                }
                break;
            default:
                break;
        }
        return info;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return ItemSprite.DualGlowing.combineGlowing(super.glowing(), baseWep.glowing());
    }

    private static void retributionAbility(Hero hero, Integer target, int dmgBoost, MeleeWeapon wep) {
        if (hero.HP / (float)hero.HT >= 0.5f){
            GLog.w(Messages.get(wep, "ability_cant_use"));
            return;
        }

        if (target == null) {
            return;
        }

        Char enemy = Actor.findChar(target);

        if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(wep, "ability_no_target"));
            return;
        }

        hero.belongings.abilityWeapon = wep;
        if (!hero.canAttack(enemy)){
            GLog.w(Messages.get(wep, "ability_target_range"));
            hero.belongings.abilityWeapon = null;
            return;
        }
        hero.belongings.abilityWeapon = null;

        hero.sprite.attack(enemy.pos, new Callback() {
            @Override
            public void call() {
                wep.beforeAbilityUsed(hero, enemy);
                AttackIndicator.target(enemy);


                if (hero.attack(enemy, 1, dmgBoost, Char.INFINITE_ACCURACY)){
                    Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                }

                Invisibility.dispel();
                if (!enemy.isAlive()){
                    hero.next();
                    onAbilityKill(hero, enemy);
                } else {
                    hero.spendAndNext(hero.attackDelay());
                }
                wep.afterAbilityUsed(hero);
            }
        });
    }

    private static void defensiveStanceAbility(Hero hero, MeleeWeapon wep) {
        wep.beforeAbilityUsed(hero, null);
        //1 turn less as using the ability is instant
        Buff.prolong(hero, Quarterstaff.DefensiveStance.class, 3 + wep.buffedLvl());
        hero.sprite.operate(hero.pos);
        hero.next();
        wep.afterAbilityUsed(hero);
    }

    private static void runicSlashAbility(Hero hero, Integer target, MeleeWeapon wep) {
        if (target == null) {
            return;
        }

        Char enemy = Actor.findChar(target);
        if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(wep, "ability_no_target"));
            return;
        }

        //we apply here because of projecting
        RunicBlade.RunicSlashTracker tracker = Buff.affect(hero, RunicBlade.RunicSlashTracker.class);
        tracker.boost = 2f + 0.50f*wep.buffedLvl();
        hero.belongings.abilityWeapon = wep;
        if (!hero.canAttack(enemy)){
            GLog.w(Messages.get(wep, "ability_target_range"));
            tracker.detach();
            hero.belongings.abilityWeapon = null;
            return;
        }
        hero.belongings.abilityWeapon = null;

        hero.sprite.attack(enemy.pos, new Callback() {
            @Override
            public void call() {
                wep.beforeAbilityUsed(hero, enemy);
                AttackIndicator.target(enemy);
                if (hero.attack(enemy, 1f, 0, Char.INFINITE_ACCURACY)){
                    Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                    if (!enemy.isAlive()){
                        onAbilityKill(hero, enemy);
                    }
                }
                tracker.detach();
                Invisibility.dispel();
                hero.spendAndNext(hero.attackDelay());
                wep.afterAbilityUsed(hero);
            }
        });
    }

    private static void swordDanceAbility(Hero hero, MeleeWeapon wep) {
        wep.beforeAbilityUsed(hero, null);
        //1 turn less as using the ability is instant
        Buff.prolong(hero, Scimitar.SwordDance.class, 3+wep.buffedLvl());
        hero.sprite.operate(hero.pos);
        hero.next();
        wep.afterAbilityUsed(hero);
    }

    private static void reverseGripAbility(Hero hero, MeleeWeapon wep) {
        wep.beforeAbilityUsed(hero, null);
        //1 turn less as using the ability is instant
        Buff.prolong(hero, DualDagger.ReverseBlade.class, 5+wep.buffedLvl());
        Sample.INSTANCE.play(Assets.Sounds.MISS);
        hero.sprite.emitter().burst( Speck.factory( Speck.JET ), 20);
        hero.next();
        wep.afterAbilityUsed(hero);
    }

    private static void parryAbility(Hero hero, MeleeWeapon wep) {
        wep.beforeAbilityUsed(hero, null);
        Invisibility.dispel();
        Buff.affect(hero, Nunchaku.ParryTracker.class, Actor.TICK);
        hero.spendAndNext(Actor.TICK);
        hero.busy();
        wep.afterAbilityUsed(hero);
    }

    private static void lashAbility(Hero hero, int dmgBoost, MeleeWeapon wep) {
        ArrayList<Char> targets = new ArrayList<>();
        Char closest = null;

        hero.belongings.abilityWeapon = wep;
        for (Char ch : Actor.chars()){
            if (ch.alignment == Char.Alignment.ENEMY
                    && !hero.isCharmedBy(ch)
                    && Dungeon.level.heroFOV[ch.pos]
                    && hero.canAttack(ch)){
                targets.add(ch);
                if (closest == null || Dungeon.level.trueDistance(hero.pos, closest.pos) > Dungeon.level.trueDistance(hero.pos, ch.pos)){
                    closest = ch;
                }
            }
        }
        hero.belongings.abilityWeapon = null;

        if (targets.isEmpty()) {
            GLog.w(Messages.get(wep, "ability_no_target"));
            return;
        }

        wep.throwSound();
        Char finalClosest = closest;
        hero.sprite.attack(hero.pos, new Callback() {
            @Override
            public void call() {
                wep.beforeAbilityUsed(hero, finalClosest);
                for (Char ch : targets) {
                    hero.attack(ch, 1, dmgBoost, Char.INFINITE_ACCURACY);
                    if (!ch.isAlive()){
                        onAbilityKill(hero, ch);
                    }
                }
                Invisibility.dispel();
                hero.spendAndNext(hero.attackDelay());
                wep.afterAbilityUsed(hero);
            }
        });
    }
}
