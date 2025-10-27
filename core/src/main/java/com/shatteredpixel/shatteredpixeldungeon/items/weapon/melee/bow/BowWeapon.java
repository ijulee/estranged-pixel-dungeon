package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.bow;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BowMasterSkill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CountCooldownBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GreaterHaste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SharpShooterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.ArrowBag;
import com.shatteredpixel.shatteredpixeldungeon.items.ArrowItem;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.bow.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.DisposableMissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

// TODO allow ghost to shoot bows when equipped (this will be a nightmare)
public class BowWeapon extends MeleeWeapon {

    public static final String AC_SHOOT		= "SHOOT";

    {
        defaultAction = AC_SHOOT;
        usesTargeting = true;
    }

    private static final int MAX_SHOTS = 3;

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (isEquipped( hero )) {
            actions.add(AC_SHOOT);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_SHOOT)) {
            if (!isEquipped( hero )) {
                usesTargeting = false;
                if (hero.hasTalent(Talent.SWIFT_EQUIP)){
                    if (hero.buff(Talent.SwiftEquipCooldown.class) == null
                            || hero.buff(Talent.SwiftEquipCooldown.class).hasSecondUse()){
                        execute(hero, AC_EQUIP);
                    } else if (hero.heroClass == HeroClass.DUELIST) {
                        GLog.w(Messages.get(this, "ability_need_equip"));
                    }
                } else if (hero.heroClass == HeroClass.DUELIST) {
                    GLog.w(Messages.get(this, "ability_need_equip"));
                }  else {
                    GLog.w(Messages.get(this, "not_equipped"));
                }
            } else {
                if (Dungeon.bullet <= 0) {
                    usesTargeting = false;
                    GLog.w(Messages.get(this, "no_arrow"));
                } else {
                    usesTargeting = true;
                    curUser = hero;
                    curItem = this;
                    GameScene.selectCell(shooter);
                }
            }
        }
    }

    protected void duelistAbility( Hero hero, Integer target ){
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
        int min = arrowMin(lvl) + lvl + Math.round(arrowMin(lvl)*(7-tier())*0.1f);
        int max = arrowMax(lvl) + lvl + Math.round(arrowMax(lvl)*(7-tier())*0.1f);
        if (levelKnown){
            return Messages.get(this, "ability_desc", augment.damageFactor(min), augment.damageFactor(max));
        } else {
            return Messages.get(this, "typical_ability_desc", augment.damageFactor(min), augment.damageFactor(max));
        }
    }

    @Override
    public int max(int lvl) {
        return 2+tier()
                +lvl;
    }

    public int arrowMin(int lvl) {
        if (hero != null) {
            return tier() +
                    lvl +
                    RingOfSharpshooting.levelDamageBonus(hero);
        } else {
            return tier() +
                    lvl;
        }

    }

    public int arrowMin() {
        return arrowMin(this.buffedLvl());
    }

    public int arrowMax(int lvl) {
        if (hero != null) {
            return 4*(tier()+1) +
                    lvl*(tier()+1) +
                    RingOfSharpshooting.levelDamageBonus(hero);
        } else {
            return 4*(tier()+1) +
                    lvl*(tier()+1);
        }
    }

    public int arrowMax() {
        return arrowMax(this.buffedLvl());
    }

    public int arrowDamage() {
        int damage = Random.NormalIntRange(arrowMin(), arrowMax());

        damage = augment.damageFactor(damage);  //증강에 따라 변화하는 효과
        return damage;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        // Adjacent melee attacks have 40% chance to knockback, or 100% chance if sneak attack.
        if (Dungeon.level.adjacent(attacker.pos, defender.pos) &&
                ((defender instanceof Mob && ((Mob) defender).surprisedBy(attacker)) ||
                        Random.Float() < 0.4f)) {
            pushEnemy(this, attacker, defender, 2 + hero.pointsInTalent(Talent.PUSHBACK));
        }

        return super.proc(attacker, defender, damage);
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {
        Statistics.bowObtained = true;
        Badges.validateArcherUnlock();
        return super.doPickUp(hero, pos);
    }

    @Override
    public String info() {
        String info = super.info();

        if (levelKnown) {
            info += "\n\n" + Messages.get(this, "bow_desc",
                    augment.damageFactor(arrowMin()), augment.damageFactor(arrowMax()));
        } else {
            info += "\n\n" + Messages.get(this, "bow_typical_desc",
                    augment.damageFactor(arrowMin(0)), augment.damageFactor(arrowMax(0)));
        }

        // TODO add crit rate to arrow desc
        if (hero != null && isEquipped(hero)) {
            float arrowCritChance = hero.critChance(new Arrow());
            if (arrowCritChance > 0) {
                info += "\n\n" + Messages.get(Weapon.class, "shooting_critchance", 100f * arrowCritChance);
            }
        }

        return info;
    }

    @Override
    public int targetingPos(Hero user, int dst) {
        return knockArrow().targetingPos(user, dst);
    }

    public Arrow knockArrow(){
        return new Arrow();
    }

    public class Arrow extends DisposableMissileWeapon {
        {
            image = ItemSpriteSheet.NORMAL_ARROW;

            hitSound = Assets.Sounds.HIT_ARROW;

            setID = 0;
        }

        public boolean dropAmmo = false;
        public boolean isBurst = false;

        private static final String DROP_AMMO = "dropAmmo";
        private static final String IS_BURST = "isBurst";

        public Arrow() {
            super();

            augment = BowWeapon.this.augment;
            enchantment = BowWeapon.this.enchantment;
            spawnedForEffect = true;

            dropAmmo = (curUser != null) && (Random.Float() <= arrowPinChance());

            identify(false); // identify to prevent cursed arrow message
        }

        @Override
        public int defaultQuantity() {
            return 1;
        }

        @Override
        public int min() {
            return BowWeapon.this.arrowMin();
        }

        @Override
        public int min(int lvl) {
            return BowWeapon.this.arrowMin(lvl);
        }

        @Override
        public int max() {
            return BowWeapon.this.arrowMax();
        }

        @Override
        public int max(int lvl) {
            return BowWeapon.this.arrowMax(lvl);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);

            bundle.put(DROP_AMMO, dropAmmo);
            bundle.put(IS_BURST, isBurst);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);

            dropAmmo = bundle.getBoolean(DROP_AMMO);
            isBurst = bundle.getBoolean(IS_BURST);
        }

        @Override
        public int proc(Char attacker, Char defender, int damage) {
            BowWeapon bow = BowWeapon.this;
            if (attacker == curUser) {
                if (curUser.belongings.getItem(ArrowBag.class) != null) {
                    ArrowBag arrowBag = curUser.belongings.getItem(ArrowBag.class);
                    damage = arrowBag.proc((Hero) attacker, defender, damage);
                }
                if (BowMasterSkill.isFastShot((Hero) attacker)) {
                    damage = Math.round(damage * BowMasterSkill.fastShotDamageMultiplier((Hero) attacker));
                }
                if (attacker.buff(BowMasterSkill.class) != null) {
                    damage = attacker.buff(BowMasterSkill.class).proc(damage);
                }

                // track identify progress of source bow
                if (!bow.levelKnown) {
                    float uses = Math.min( bow.availableUsesToID,
                            Talent.itemIDSpeedFactor(curUser, bow) );
                    bow.availableUsesToID -= uses;
                    bow.usesLeftToID -= uses;
                    if (bow.usesLeftToID <= 0) {
                        if (ShardOfOblivion.passiveIDDisabled()){
                            if (bow.usesLeftToID > -1){
                                GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready"), bow.name());
                            }
                            bow.setIDReady();
                        } else {
                            bow.identify();
                            GLog.p(Messages.get(Weapon.class, "identify"));
                            Badges.validateItemLevelAquired(bow);
                        }
                    }
                }
            }

            return super.proc(attacker, defender, damage);
        }

        @Override
        public int level() {
            return BowWeapon.this.level();
        }

        @Override
        public int buffedLvl() {
            return BowWeapon.this.buffedLvl();
        }

        @Override
        public int damageRoll(Char owner) {
            BowWeapon bow = BowWeapon.this;
            int damage = bow.arrowDamage();
            if (owner == curUser) {
                Char enemy = curUser.attackTarget();
                if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(curUser)) {
                    //deals 50% toward max to max on surprise, instead of min to max.
                    int diff = bow.arrowMax() - bow.arrowMin();
                    damage = augment.damageFactor(Hero.heroDamageIntRange(
                            bow.arrowMin() + Math.round(diff*(3/(3f+tier))), //75%, 60%, 50%, 43%, 38% toward max
                            bow.arrowMax()));
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
        public float delayFactor(Char owner) {
            if (owner == curUser && BowMasterSkill.isFastShot(curUser)) {
                return 0;
            }
            return BowWeapon.this.delayFactor(owner);
        }

        @Override
        public float accuracyFactor(Char owner, Char target) {
            float ACC = super.accuracyFactor(owner, target);

            if (owner == curUser &&
                    curUser.buff(BowMasterSkill.class) != null &&
                    curUser.buff(BowMasterSkill.class).isPowerShot()) {
                return Char.INFINITE_ACCURACY;
            }

            if (isBurst && owner == curUser && curUser.hasTalent(Talent.BULLSEYE)) {
                switch (curUser.pointsInTalent(Talent.BULLSEYE)) {
                    case 3:
                        return Char.INFINITE_ACCURACY;
                    case 2:
                        ACC *= 5;
                        break;
                    case 1: default:
                        ACC *= 2;
                        break;
                }
            }

            return ACC;
        }

        @Override
        public int STRReq() {
            return BowWeapon.this.STRReq();
        }

        private float arrowPinChance() {
            // TODO nerf arrow drop chances
            if (curUser != null) {

                float chance = 0.25f;
                chance += 0.125f * curUser.pointsInTalent(Talent.DEXTERITY);
                if (isBurst && curUser.hasTalent(Talent.PERFECT_SHOT)) {
                    chance = Math.max(chance, curUser.pointsInTalent(Talent.PERFECT_SHOT) / 3f);
                } else if (curUser.hasTalent(Talent.SPECTRE_ARROW)) {
                    chance += hero.pointsInTalent(Talent.SPECTRE_ARROW) / 6f;
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


            SharpShooterBuff.rangedLethal(enemy, isBurst, this);

            onShoot();
        }

        @Override
        protected void rangedHit(Char enemy, int cell) {
            if (dropAmmo) {
                if (enemy.isAlive()) {
                    Buff.affect(enemy, ArrowAttached.class, ArrowAttached.DURATION);
                } else {
                    dropArrow(cell);
                }
            }

            if (isBurst && !enemy.isAlive() && curUser != null &&
                    curUser.hasTalent(Talent.HURRICANE)) {
                Buff.affect(curUser, GreaterHaste.class).set(1+curUser.pointsInTalent(Talent.HURRICANE));
            }
        }

        @Override
        protected void rangedMiss(int cell) {
            if (dropAmmo) {
                dropArrow(cell);
            }
        }

        public void onShoot() {
            if (curUser != null) {
                Dungeon.bullet--;

                if (curUser.buff(PenetrationShotBuff.class) != null) {
                    curUser.buff(PenetrationShotBuff.class).detach();
                }
                if (curUser.subClass != HeroSubClass.BOWMASTER) {
                    Buff.affect(hero, BowFatigue.class, BowFatigue.MAX_DURATION);
                }
                if (curUser.subClass == HeroSubClass.BOWMASTER) {
                    Buff.affect(curUser, BowMasterSkill.class).shoot();
                }

                updateQuickslot();
            }
        }

        @Override
        public void throwSound() {
            Sample.INSTANCE.play( Assets.Sounds.ATK_SPIRITBOW, 1, Random.Float(0.87f, 1.15f) );
        }
    }

    private CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {
            if (target != null) {
                if (target == curUser.pos &&
                        curUser.heroClass == HeroClass.DUELIST &&
                        curUser.buff(Charger.class) != null) {

                    Charger charger = curUser.buff(Charger.class);
                    if (charger.charges >= 1) {
                        duelistAbility(curUser, target);
                    } else {
                        GLog.w(Messages.get(MeleeWeapon.class, "ability_no_charge"));
                    }

                } else {
                    knockArrow().cast(curUser, target);
                }
            }
        }
        @Override
        public String prompt() {
            return Messages.get(SpiritBow.class, "prompt");
        }
    };

    public static void dropArrow(int cell) {
        Dungeon.level.drop(new ArrowItem(), cell).sprite.drop(cell);
    }

    public static void dropArrow(int qty, int cell) {
        Dungeon.level.drop((new ArrowItem()).quantity(qty), cell).sprite.drop(cell);
    }

    public static void pushEnemy(Weapon weapon, Char attacker, Char defender, int power) {
         /* TODO nerf this: prevent knocking into pits. */

        //trace a ballistica to our target (which will also extend past them
        Ballistica trajectory = new Ballistica(attacker.pos, defender.pos, Ballistica.STOP_TARGET);
        //trim it to just be the part that goes past them
        trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size()-1), Ballistica.PROJECTILE);
        //knock them back along that ballistica
        WandOfBlastWave.throwChar(defender,
                trajectory,
                power,
                false,
                false,
                BowWeapon.class);
    }

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
