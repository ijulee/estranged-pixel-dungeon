package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

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
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SwordAura extends Buff implements ActionIndicator.Action {
    {
        type = buffType.NEUTRAL;
        announced = false;
    }

    private final static Image cross = Icons.TARGET.get();
    private static Char lastTarget = null;

    private int energy = 0;
    private int recovered = 0;

    public int getCost() {
        return Math.round(energy * (1 - 0.4f*hero.pointsInTalent(Talent.ENERGY_SAVING)/3));
    }

    private float chargeMulti() {
        return 1 + hero.pointsInTalent(Talent.MIND_FOCUSING)/3f;
    }

    private int maxEnergy() {
        return 60 + 30 * hero.pointsInTalent(Talent.STORED_POWER);
    }

    public void hit(int damage) {
        energy += Math.round(damage * chargeMulti());
        energy = Math.min(energy, maxEnergy());
        ActionIndicator.setAction(this);
        if (energy <= 0) {
            detach();
        }
    }

    public void useEnergy() {
        energy -= getCost() - recovered;
        if (energy <= 0) {
            detach();
        }

        recovered = 0;
        ActionIndicator.refresh();
    }

    @Override
    public void detach() {
        super.detach();
        ActionIndicator.clearAction();
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
        ActionIndicator.setAction(this);
        energy = bundle.getInt( DAMAGE );
    }

    @Override
    public int icon() {
        return BuffIndicator.AURA;
    }

    @Override
    public float iconFadePercent(){
        return Math.max(0, (maxEnergy() - energy)/(float) maxEnergy());
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
        int charge = (int) (100* energy /((float) maxEnergy()));
        txt.text(String.format("%d%%", charge));
        if (charge >= 100) {
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
            if (canAutoAim(lastTarget)){
                CharSprite sprite = lastTarget.sprite;
                if (sprite != null && sprite.parent != null) {
                    sprite.parent.addToFront(cross);
                    cross.point(sprite.center(cross));
                }
            }

            GameScene.selectCell(shooter);
        } else {
            if (canAutoAim(lastTarget)){
                int cell = QuickSlotButton.autoAim(lastTarget, knockAura());
                if (cell == -1) return;
                shooter.onSelect(cell);
            }
        }
    }

    private boolean canAutoAim(Char lastTarget) {
        return lastTarget != null &&
                lastTarget.isAlive() && lastTarget.isActive() &&
                lastTarget.alignment != Char.Alignment.ALLY &&
                Dungeon.hero.fieldOfView[lastTarget.pos];
    }

    public static void target( Char target ) {
        if (target != null && target.alignment != Char.Alignment.ALLY) {
            lastTarget = target;

            SwordAura aura = Dungeon.hero.buff(SwordAura.class);
            if (aura != null && GameScene.isCellSelecterActive(aura.shooter)) {
                CharSprite sprite = lastTarget.sprite;
                if (sprite.parent != null) {
                    sprite.parent.addToFront(cross);
                    cross.point(sprite.center(cross));
                }
            }
        }
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
            if (Random.Float() < hero.pointsInTalent(Talent.ARCANE_POWER)/3f && wep != null) {
                dmg = wep.proc(attacker, defender, dmg);
            }

            if (hero.hasTalent(Talent.ENERGY_COLLECT)) {
                recovered += Math.round(dmg / (float) (7 - hero.pointsInTalent(Talent.ENERGY_COLLECT)));
            }

            return dmg;
        }

        @Override
        protected void onThrow( int cell ) {
            if (cell != hero.pos) {
                // assume cell comes from throwPos(), which applies Ballistica.DASH and Projecting
                Ballistica aim = new Ballistica(hero.pos, cell, Ballistica.STOP_TARGET);

                ArrayList<Char> chars = new ArrayList<>();
                for (int c : aim.subPath(1, aim.dist)) {
                    Char ch = Actor.findChar( c );
                    if (ch != null) {
                        chars.add( ch );
                    }
                }

                for (Char ch : chars) {
                    if (curUser.shoot(ch, this)) {
                        // don't count ally/neutral NPC
                        if (ch.alignment == Char.Alignment.ENEMY ||
                                (ch instanceof Mimic && ch.alignment == Char.Alignment.NEUTRAL)) {
                            lastTarget = ch;
                        }

                        if (hero.hasTalent(Talent.WIND_BLAST)) {
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

        @Override
        public void cast(final Hero user, final int dst) {
            Char enemy = Actor.findChar( dst );
            SwordAura.target(enemy);

            super.cast(user, dst);
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
            }

            if (cross != null) {
                cross.remove();
            }
        }
        @Override
        public String prompt() {
            return Messages.get(SpiritBow.class, "prompt");
        }
    };

    public static class SwordAuraMagicDamage {}
}
