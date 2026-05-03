/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class SpearNShield extends MeleeWeapon implements AlchemyWeapon {

    public boolean stance;
    public static final String AC_CHANGE		= "CHANGE";

    {
        image = ItemSpriteSheet.SPEAR_N_SHIELD;
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch = 1.1f;

        tier = 3;
        RCH = 2;
    }

    @Override
    public int max(int lvl) {
        //halved in proc() if no reach
        return  5 * (tier+1) + //20 base
                lvl * (tier+1);  //+4 per level
    }

    @Override
    public int defenseFactor( Char owner ) {
        return DRMax();
    }

    public int DRMax(){
        return DRMax(buffedLvl());
    }

    //4 extra defence, plus 1 per level
    public int DRMax(int lvl){
        return 4 + lvl;
    }

    public String statsInfo(){
        if (isIdentified()) {
            return Messages.get(this, "stats_desc", DRMax());
        } else {
            return Messages.get(this, "typical_stats_desc", DRMax(0));
        }
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        damage = super.proc(attacker, defender, damage);

        if (Dungeon.level.distance(attacker.pos, defender.pos) <= 1) {
            damage = Math.round(damage/2f);
        }

        return damage;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        if (target == null) {
            return;
        }

        //can parry charmed enemies but not attack them
        Char enemy = Actor.findChar(target);
        if (enemy == null || enemy == hero || !Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(this, "ability_no_target"));
            return;
        }

        hero.belongings.abilityWeapon = this;
        if (!hero.canAttack(enemy)){
            GLog.w(Messages.get(this, "ability_target_range"));
            hero.belongings.abilityWeapon = null;
            return;
        }
        hero.belongings.abilityWeapon = null;

        beforeAbilityUsed(hero, null);
        Buff.affect(hero, ParryTracker.class, Actor.TICK).enemy = enemy;
        hero.spendAndNext(Actor.TICK);
        hero.busy();

    }

    public static class ParryTracker extends FlavourBuff {
        { actPriority = HERO_PRIO+1;}

        public Char enemy;
        public boolean parried;

        @Override
        public boolean act() {
            Hero hero = (Hero) target;
            MeleeWeapon wep = (MeleeWeapon) hero.belongings.attackingWeapon();

            //+(9+2*lvl) damage, roughly +83% base damage, +80% scaling
            int dmgBoost = (parried) ? wep.augment.damageFactor(9 + Math.round(2f*wep.buffedLvl())) : 0;

            if (!hero.canAttack(enemy)) {
                GLog.w(Messages.get(MeleeWeapon.class, "ability_target_range"));
                hero.belongings.abilityWeapon = null;
            } else if (hero.isCharmedBy(enemy)) {
                GLog.w(Messages.get(Charm.class, "cant_attack"));
                hero.belongings.abilityWeapon = null;
            } else {
                hero.sprite.attack(enemy.pos, () -> {
                    AttackIndicator.target(enemy);
                    int oldPos = enemy.pos;
                    int power = Math.min(3-Dungeon.level.distance(hero.pos, enemy.pos), 1);
                    //do not push if enemy has moved, or another push is active (e.g. elastic)
                    if (hero.attack(enemy, 1, dmgBoost, Char.INFINITE_ACCURACY)) {
                        if (enemy.isAlive() && enemy.pos == oldPos && !Pushing.pushingExistsForChar(enemy)) {
                            //trace a ballistica to our target (which will also extend past them
                            Ballistica trajectory = new Ballistica(hero.pos, enemy.pos, Ballistica.STOP_TARGET);
                            //trim it to just be the part that goes past them
                            trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
                            //knock them back along that ballistica
                            WandOfBlastWave.throwChar(enemy, trajectory, power, true, false, hero);
                        } else if (!enemy.isAlive()) {
                            onAbilityKill(hero, enemy);
                        }
                        Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                    }
                    Invisibility.dispel();
                    float delay = Math.max(0, hero.attackDelay() - TICK);
                    hero.spendAndNext(delay);
                    wep.afterAbilityUsed(hero);
                });
            }
            return super.act();
        }
    }

    @Override
    public String abilityInfo() {
        int dmgBoost = levelKnown ? 7 + Math.round(1.5f*buffedLvl()) : 7;
        if (levelKnown) {
            return Messages.get(this, "ability_desc", augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost));
        } else {
            return Messages.get(this, "typical_ability_desc", min(0)+dmgBoost, max(0)+dmgBoost);
        }
    }

    @Override
    public String discoverHint() {
        return AlchemyWeapon.hintString(this.getClass());
    }

    @Override
    public String desc() {
        return super.desc() + "\n\n" + discoverHint();
    }

}
