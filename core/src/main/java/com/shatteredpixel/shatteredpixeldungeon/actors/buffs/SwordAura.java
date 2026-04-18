package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;
import static com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton.lastTarget;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.Sheath;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.bow.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Projecting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collections;

public class SwordAura extends TargetingAction {
    {
        type = buffType.NEUTRAL;
        announced = false;
    }

    private int energy = 0;
    private int recovered = 0;

    public int getCost() {
        return energy;
    }

    private float chargeMulti() {
        return 0.4f + 0.2f * Dungeon.hero.pointsInTalent(Talent.MIND_FOCUSING);
    }

    private int maxEnergy() {
        return 60 + 30 * Dungeon.hero.pointsInTalent(Talent.STORED_POWER);
    }

    public void hit(int damage) {
        energy += Math.round(damage * chargeMulti());
        refresh();
    }

    public void useEnergy() {
        energy -= getCost() - recovered;
        refresh();
        recovered = 0;
    }

    public void refresh() {
        energy = Math.min(energy, maxEnergy());
        if (energy <= 0) {
            ActionIndicator.clearAction(this);
        } else {
            ActionIndicator.setAction(this);
        }
        ActionIndicator.refresh();
    }

    @Override
    public void detach() {
        super.detach();
        ActionIndicator.clearAction(this);
    }

    private static final String DAMAGE = "damage";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put( DAMAGE, energy );
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        energy = bundle.getInt( DAMAGE );
        refresh();
    }

    @Override
    public int icon() {
        return BuffIndicator.AURA;
    }

    @Override
    public float iconFadePercent(){
        return Math.max(0, 1 - (energy/(float) maxEnergy()));
    }

    @Override
    public String iconTextDisplay(){
        return Integer.toString(energy);
    }

    @Override
    public String desc(){
        return Messages.get(this, "desc", energy, maxEnergy(), getCost());
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }

    @Override
    public int actionIcon() {
        return HeroIcon.SWORD_AURA;
    }

    @Override
    public Visual secondaryVisual() {
        BitmapText txt = new BitmapText(PixelScene.pixelFont);
        txt.text(Messages.format("%d", energy));
        if (energy >= maxEnergy()) {
            txt.hardlight(CharSprite.POSITIVE);
        }
        txt.measure();
        return txt;
    }

    @Override
    public int indicatorColor() {
        return 0xFF2A00;
    }

    @Override
    public void doAction() {
        if (!GameScene.isCellSelecterActive(shooter)) {
            showCross();

            GameScene.selectCell(shooter);
        } else {
            if (canAutoAim(lastTarget)) {
                int cell = QuickSlotButton.autoAim(lastTarget);
                if (cell != -1) {
                    GameScene.handleCell(cell);
                } else {
                    //couldn't auto-aim, just target the position and hope for the best.
                    GameScene.handleCell(lastTarget.pos);
                }
            }
        }
    }

    private boolean canAutoAim(Char lastTarget) {
        return  lastTarget != null &&
                lastTarget.isAlive() && lastTarget.isActive() &&
                lastTarget.alignment != Char.Alignment.ALLY &&
                Dungeon.hero.fieldOfView[lastTarget.pos];
    }

    public Aura knockAura(){
        return new Aura();
    }

    public class Aura extends MissileWeapon {

        {
            image = ItemSpriteSheet.SWORD_AURA;
            hitSound = Assets.Sounds.HIT_SLASH;
            spawnedForEffect = true;
        }

        @Override
        public int defaultQuantity() {
            return 1;
        }

        @Override
        public float accuracyFactor(Char owner, Char target) {
            return Char.INFINITE_ACCURACY;
        }

        @Override
        public int max() {
            return SwordAura.this.energy;
        }

        @Override
        public int damageRoll(Char owner) {
            return SwordAura.this.energy;
        }

        @Override
        public int STRReq(int lvl) {
            KindOfWeapon wep = hero.belongings.weapon();
            if (wep instanceof Weapon) {
                return ((Weapon) wep).STRReq();
            } else {
                return hero.STR();
            }
        }

        @Override
        public int proc(Char attacker, Char defender, int damage) {
            int dmg = super.proc(attacker, defender, damage);

            KindOfWeapon wep = hero.belongings.weapon();
            if (Random.Int(3) < hero.pointsInTalent(Talent.ARCANE_POWER) && wep != null) {
                dmg = wep.proc(attacker, defender, dmg);
            }

            if (hero.hasTalent(Talent.ENERGY_COLLECT)) {
                recovered += Math.round(dmg / (float) (7 - hero.pointsInTalent(Talent.ENERGY_COLLECT)));
            }

            return dmg;
        }

        @Override
        public int throwPos(Hero user, int dst) {
            int projecting = 0;

            if (Random.Int(3) < user.pointsInTalent(Talent.ARCANE_POWER)) {
                MeleeWeapon wep = (MeleeWeapon) user.belongings.weapon();
                if (wep != null && wep.hasEnchant(Projecting.class, user)) {
                    projecting += 4;
                }
            }

            projecting = Math.round(projecting * Enchantment.genericProcChanceMultiplier(user));

            if (Dungeon.level.distance(user.pos, dst) <= projecting) {
                return dst;
            }

            return new Ballistica(hero.pos, dst, Ballistica.DASH).collisionPos;
        }

        @Override
        protected void onThrow( int cell ) {
            if (cell != hero.pos) {
                //throwPos() has already applied Ballistica.STOP_SOLID and Projecting
                Ballistica aim = new Ballistica(hero.pos, cell, Ballistica.STOP_TARGET);

                ArrayList<Char> chars = new ArrayList<>();
                for (int c : aim.subPath(1, aim.dist)) {
                    Char ch = Actor.findChar( c );
                    if (ch != null) {
                        chars.add( ch );
                    }
                }

                //process in reverse, mostly for elastic
                Collections.reverse(chars);

                for (Char ch : chars) {
                    if (curUser.shoot(ch, this)) {
                        //don't count ally/neutral NPC
                        if (ch.alignment == Char.Alignment.ENEMY ||
                                (ch instanceof Mimic && ch.alignment == Char.Alignment.NEUTRAL)) {
                            lastTarget = ch;
                        }

                        if (hero.buff(Sheath.Sheathing.class) != null &&
                            hero.hasTalent(Talent.WIND_BLAST)) {
                            ch.damage(5*hero.pointsInTalent(Talent.WIND_BLAST), new SwordAuraMagicDamage());
                        }
                    }
                }
            }

            useEnergy();

            Invisibility.dispel();
            if (hero.buff(Sheath.Sheathing.class) != null) {
                hero.buff(Sheath.Sheathing.class).detach();
            }
        }

        @Override
        public void throwSound() {
            Sample.INSTANCE.play( Assets.Sounds.MISS );
        }
    }

    private final CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {
            if (target != null) {
                if (target != hero.pos) {
                    knockAura().cast(hero, target);
                } else {
                    GLog.w(Messages.get(this, "cannot_hero"));
                }

                removeCross();
            }
        }
        @Override
        public String prompt() {
            return Messages.get(SpiritBow.class, "prompt");
        }
    };

    public static class SwordAuraMagicDamage {}
}
