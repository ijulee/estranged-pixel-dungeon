package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.bow;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BowMasterSkill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CountCooldownBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.ArrowBag;
import com.shatteredpixel.shatteredpixeldungeon.items.ArrowItem;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.bow.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.GunWeapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

// TODO allow ghost to shoot bows when equipped (this will be a nightmare)
public class BowWeapon extends GunWeapon {

    private static final int MAX_SHOTS = 3;

    @Override
    public boolean canShoot() {
        if (isEquipped(Dungeon.hero))
            return Dungeon.bullet >= 1;
        else
            return true;
    }

    @Override
    public void noAmmoAction(Hero hero) {
        GLog.w(Messages.get(this, "no_ammo"));
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target ){
        if (hero.buff(PenetrationShotBuff.class) != null) {
            GLog.w(Messages.get(this, "already_used"));
            return;
        }

        beforeAbilityUsed(hero, null);

        hero.sprite.operate(hero.pos);
        hero.spendAndNext(0);
        Buff.affect(hero, PenetrationShotBuff.class);
        Sample.INSTANCE.play(Assets.Sounds.MISS);

        afterAbilityUsed(hero);
    }

    @Override
    public String abilityInfo() {
        int lvl = levelKnown ? buffedLvl() : 0;
        int min = missileMin(lvl) + lvl + Math.round(missileMin(lvl)*(7-tier())*0.1f);
        int max = missileMax(lvl) + lvl + Math.round(missileMax(lvl)*(7-tier())*0.1f);
        if (levelKnown){
            return Messages.get(this, "ability_desc", augment.damageFactor(min), augment.damageFactor(max));
        } else {
            return Messages.get(this, "typical_ability_desc", augment.damageFactor(min), augment.damageFactor(max));
        }
    }

    @Override
    public int max(int lvl) {
        return 2 + tier() + lvl;
    }

    @Override
    public int missileMax(int lvl) {
        return (tier() + 1) * (lvl + 4) +
                (isEquipped(Dungeon.hero) ? RingOfSharpshooting.levelDamageBonus(Dungeon.hero) : 0);
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        // Adjacent melee attacks have 40% chance to knockback, or 100% chance if sneak attack.
        if (Dungeon.level.adjacent(attacker.pos, defender.pos) &&
                ((defender instanceof Mob && ((Mob) defender).surprisedBy(attacker)) ||
                        Random.Float() < 0.4f)) {
            pushEnemy(this, attacker, defender,
                    2 + (isEquipped(Dungeon.hero) ? Dungeon.hero.pointsInTalent(Talent.PUSHBACK) : 0));
        }

        return super.proc(attacker, defender, damage);
    }

    @Override
    public boolean collect(Bag container) {
        if (super.collect(container)) {
            Statistics.bowObtained = true;
            Badges.validateArcherUnlock();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String info() {
        String info = super.info();

        if (levelKnown) {
            info += "\n\n" + Messages.get(this, "bow_desc",
                    augment.damageFactor(missileMin()), augment.damageFactor(missileMax()));
        } else {
            info += "\n\n" + Messages.get(this, "bow_typical_desc",
                    augment.damageFactor(missileMin(0)), augment.damageFactor(missileMax(0)));
        }

        if (isEquipped(Dungeon.hero)) {
            float arrowCritChance = Dungeon.hero.critChance(getMissile());
            if (arrowCritChance > 0) {
                info += "\n\n" + Messages.get(this, "shooting_critchance", 100f * arrowCritChance);
            }
        }

        return info;
    }

    @Override
    public int targetingPos(Hero user, int dst) {
        return getMissile().targetingPos(user, dst);
    }

    @Override
    public Arrow getMissile(){
        return new Arrow();
    }

    public class Arrow extends GunMissile {
        {
            image = ItemSpriteSheet.NORMAL_ARROW;

            hitSound = Assets.Sounds.HIT_ARROW;

        }

        public boolean dropAmmo = false;

        private static final String DROP_AMMO = "dropAmmo";

        public Arrow() {
            super();

            dropAmmo = (curUser != null) && (Random.Float() <= arrowPinChance());
        }

        @Override
        public boolean doPickUp(Hero hero, int pos) {
            return new ArrowItem().doPickUp(hero, pos);
        }

        @Override
        public boolean collect(Bag container) {
            return new ArrowItem().collect(container);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);

            bundle.put(DROP_AMMO, dropAmmo);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);

            dropAmmo = bundle.getBoolean(DROP_AMMO);
        }

        @Override
        public int damageRoll(Char owner) {
            BowWeapon bow = BowWeapon.this;
            int damage = super.damageRoll(owner);
            if (owner == curUser) {
                Char enemy = curUser.attackTarget();
                if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(curUser)) {
                    // deals increased min dmg on surprise
                    // min dmg increased to 75%/60%/50%/43%/38% by tier
                    int diff = bow.missileMax() - bow.missileMin();
                    damage = augment.damageFactor(Hero.heroDamageIntRange(
                            bow.missileMin() + Math.round(diff*(3/(3f+tier))),
                            bow.missileMax()));
                }

                if (curUser.buff(PenetrationShotBuff.class) != null) {
                    damage = curUser.buff(PenetrationShotBuff.class).proc(damage, this.buffedLvl(), bow.tier);
                    Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                }

                if (curUser.buff(BowFatigue.class) != null) {
                    damage = curUser.buff(BowFatigue.class).damage(damage);
                }
            }
            return damage;
        }

        @Override
        public int proc(Char attacker, Char defender, int damage) {
            if (attacker == curUser) {
                if (curUser.belongings.getItem(ArrowBag.class) != null) {
                    ArrowBag arrowBag = curUser.belongings.getItem(ArrowBag.class);
                    damage = arrowBag.proc(curUser, defender, damage);
                }
                if (BowMasterSkill.isFastShot(curUser)) {
                    damage = Math.round(damage * BowMasterSkill.fastShotDamageMultiplier(curUser));
                }
                if (curUser.buff(BowMasterSkill.class) != null) {
                    damage = curUser.buff(BowMasterSkill.class).proc(damage);
                }
            }

            return super.proc(attacker, defender, damage);
        }

        @Override
        public float delayFactor(Char owner) {
            if (owner == curUser && BowMasterSkill.isFastShot(curUser)) {
                return 0;
            }
            return super.delayFactor(owner);
        }

        @Override
        public float accuracyFactor(Char owner, Char target) {
            float ACC = super.accuracyFactor(owner, target);

            if (owner == curUser &&
                    curUser.buff(BowMasterSkill.class) != null &&
                    curUser.buff(BowMasterSkill.class).isPowerShot()) {
                return Char.INFINITE_ACCURACY;
            }

            return ACC;
        }

        private float arrowPinChance() {
            // TODO nerf arrow drop chances
            if (curUser != null) {

                float chance = 0.25f;
                chance += 0.125f * curUser.pointsInTalent(Talent.DEXTERITY);
                if (isBurst && curUser.hasTalent(Talent.PERFECT_SHOT)) {
                    chance = Math.max(chance, curUser.pointsInTalent(Talent.PERFECT_SHOT) / 3f);
                } else if (curUser.hasTalent(Talent.SPECTRE_ARROW)) {
                    chance += curUser.pointsInTalent(Talent.SPECTRE_ARROW) / 6f;
                }
                return chance;
            } else {
                return 0;
            }
        }

        @Override
        protected void onThrow(int cell) {
            super.onThrow( cell );

            Char enemy = Actor.findChar( cell );

            if (enemy == null || enemy == curUser) {
                if (dropAmmo) {
                    dropArrow(cell);
                }
            }

            if (curUser != null) {
                Dungeon.bullet--;

                if (curUser.buff(PenetrationShotBuff.class) != null &&
                        enemy != null && enemy != curUser) {
                    curUser.buff(PenetrationShotBuff.class).detach();
                }

                if (curUser.subClass != HeroSubClass.BOWMASTER) {
                    Buff.affect(curUser, BowFatigue.class, BowFatigue.MAX_DURATION);
                }

                updateQuickslot();
            }
        }

        @Override
        protected void rangedHit(Char enemy, int cell) {
            super.rangedHit(enemy, cell);

            if (dropAmmo) {
                if (enemy.isAlive()) {
                    Buff.affect(enemy, ArrowAttached.class, ArrowAttached.DURATION);
                } else {
                    dropArrow(cell);
                }
            }

            if (curUser != null) {
                if (curUser.subClass == HeroSubClass.BOWMASTER) {
                    Buff.affect(curUser, BowMasterSkill.class).onShoot();
                }
            }
        }

        @Override
        protected void rangedMiss(int cell) {
            if (dropAmmo) {
                dropArrow(cell);
            }
        }

        @Override
        public int image() {
            if (isEquipped(Dungeon.hero)) {
                BowMasterSkill b = Dungeon.hero.buff(BowMasterSkill.class);
                if (b != null && b.isPowerShot()) {
                    return ItemSpriteSheet.GOLDEN_ARROW;
                }
            }
            return super.image();
        }

        @Override
        public void throwSound() {
            Sample.INSTANCE.play( Assets.Sounds.ATK_SPIRITBOW, 1, Random.Float(0.87f, 1.15f) );
        }
    }

    public static void dropArrow(int cell) {
        Dungeon.level.drop(new ArrowItem(), cell).sprite.drop(cell);
    }

    public static void dropArrow(int qty, int cell) {
        Dungeon.level.drop((new ArrowItem()).quantity(qty), cell).sprite.drop(cell);
    }

    public static void pushEnemy(KindOfWeapon weapon, Char attacker, Char defender, int dist) {
        //trace a ballistica to our target (which will also extend past them
        Ballistica trajectory = new Ballistica(attacker.pos, defender.pos, Ballistica.STOP_TARGET);
        //trim it to just be the part that goes past them
        trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size()-1), Ballistica.PROJECTILE);
        //knock them back along that ballistica, ensuring they don't fall into a pit
        if (!defender.flying) {
            while (dist > trajectory.dist ||
                    (dist > 0 && Dungeon.level.pit[trajectory.path.get(dist)])) {
                dist--;
            }
        }
        WandOfBlastWave.throwChar(defender, trajectory, dist, false, false, attacker);
    }

    @Override
    protected CellSelector.Listener getShooter() {
        return shooter;
    }

    protected CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer cell) {
            if (cell != null) {
                if (cell == curUser.pos && curUser.heroClass == HeroClass.DUELIST) {
                    execute(curUser, AC_ABILITY);
                } else {
                    getMissile().cast(curUser, cell);
                }
            }
        }

        @Override
        public String prompt() {
            return Messages.get(SpiritBow.class, "prompt");
        }
    };

    public static class ArrowAttached extends CountCooldownBuff {
        {
            type = buffType.NEUTRAL;
        }

        public static final float DURATION = 50f;

        @Override
        public int icon() {
            return BuffIndicator.ARROW_ATTACHED;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (DURATION - visualcooldown()) / DURATION);
        }

        @Override
        public boolean act() {
            int oldCount = count();
            super.act();
            dropArrow(oldCount-count(), target.pos);
            return true;
        }

        @Override
        protected void spend(float time) {
            super.spend(time);
            if (count() >= 10) {
                Badges.validateHedgehog();
            }
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", count(), cooldown());
        }
    }

    public static class PenetrationShotBuff extends Buff {
        {
            type = buffType.NEUTRAL;
        }

        @Override
        public int icon() {
            return BuffIndicator.DUEL_BOW;
        }

        public int proc(int damage, int lvl, int tier) {
            return damage + lvl +
                    Math.round(damage * Math.max(0, 7-tier) * 0.1f);
        }
    }

    public static class BowFatigue extends CountCooldownBuff {
        {
            type = buffType.NEGATIVE;
        }

        public static final float MAX_DURATION = 5f;

        @Override
        public int icon() {
            return BuffIndicator.WEAKNESS;
        }

        @Override
        public void tintIcon(Image icon) {
            int xs = (count() > MAX_SHOTS) ? count() - MAX_SHOTS : 0;
            float intensity = (float) Math.pow(0.95f, xs);
            icon.hardlight(.6f/intensity, .6f*intensity, .6f*intensity);
        }

        @Override
        public String desc() {
            int xs = (count() > MAX_SHOTS) ? count() - MAX_SHOTS : 0;
            return Messages.get(this, "desc", MAX_SHOTS, (int) MAX_DURATION, count(), (float)100*Math.pow(0.9f, xs));
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (MAX_SHOTS - (float) count()) / MAX_SHOTS);
        }

        public int damage(int damage) {
            if (count() > MAX_SHOTS) {
                int xs = count() - MAX_SHOTS;
                damage = Math.round(damage * (float)Math.pow(0.9f, xs));
            }
            return damage;
        }
    }
}
