package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GreaterHaste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SharpShooterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.bow.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

// Melee weapons that can shoot missiles
public abstract class GunWeapon extends MeleeWeapon {
    public static final String AC_SHOOT		= "SHOOT";
    public boolean isBurst = false;

    {
        defaultAction = AC_SHOOT;
        usesTargeting = true;
    }

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
                if (canShoot()) {
                    usesTargeting = true;
                    curUser = hero;
                    curItem = this;
                    GameScene.selectCell(getShooter());
                } else {
                    usesTargeting = false;
                    noAmmoAction(hero);
                }
            }
        }
    }

    public abstract boolean canShoot();

    public abstract void noAmmoAction(Hero hero);
    public int missileMin(int lvl) {
        return tier() + lvl +
                (isEquipped(Dungeon.hero) ? RingOfSharpshooting.levelDamageBonus(Dungeon.hero) : 0);
    }

    public int missileMin(){
        return missileMin(buffedLvl());
    }

    public int missileMax(int lvl) {
        return tier() * (lvl + 5) +
                (isEquipped(Dungeon.hero) ? RingOfSharpshooting.levelDamageBonus(Dungeon.hero) : 0);
    }

    public int missileMax() {
        return missileMax(buffedLvl());
    }

    protected int missileDamageRoll(Char owner) {
        int dmg;
        if (owner instanceof Hero) {
            dmg = Hero.heroDamageIntRange(missileMin(), missileMax());
        } else {
            dmg = Random.NormalIntRange(missileMin(), missileMax());
        }
        return augment.damageFactor(dmg);
    }

    public GunMissile getMissile(){
        return new GunMissile();
    }

    @Override
    public int targetingPos(Hero user, int dst) {
        return getMissile().targetingPos(user, dst);
    }

    public class GunMissile extends MissileWeapon {
        {
            setID = 0;

            spawnedForEffect = true;
        }

        public GunMissile() {
            super();
            augment = GunWeapon.this.augment;
            enchantment = GunWeapon.this.enchantment;
            identify(false); // prevents "curse discovery" message
        }

        @Override
        public int defaultQuantity() {
            return 1;
        }

        @Override
        public Item split(int amount) {
            return this;
        }

        @Override
        public int min(int lvl) {
            return GunWeapon.this.missileMin(lvl);
        }

        @Override
        public int min() {
            return GunWeapon.this.missileMin();
        }

        @Override
        public int max(int lvl) {
            return GunWeapon.this.missileMax(lvl);
        }

        @Override
        public int max() {
            return GunWeapon.this.missileMax();
        }

        @Override
        public int damageRoll(Char owner) {
            return GunWeapon.this.missileDamageRoll(owner);
        }

        @Override
        public int proc(Char attacker, Char defender, int damage) {
            GunWeapon wep = GunWeapon.this;

            // track identify progress of source weapon
            if (curUser != null && !wep.levelKnown) {
                float uses = Math.min( wep.availableUsesToID,
                        Talent.itemIDSpeedFactor(curUser, wep) );
                wep.availableUsesToID -= uses;
                wep.usesLeftToID -= uses;
                if (wep.usesLeftToID <= 0) {
                    if (ShardOfOblivion.passiveIDDisabled()){
                        if (wep.usesLeftToID > -1){
                            GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready"), wep.name());
                        }
                        wep.setIDReady();
                    } else {
                        wep.identify();
                        GLog.p(Messages.get(Weapon.class, "identify"));
                        Badges.validateItemLevelAquired(wep);
                    }
                }
            }

            return super.proc(attacker, defender, damage);
        }

        @Override
        public int level() {
            return GunWeapon.this.level();
        }

        @Override
        public int buffedLvl() {
            return GunWeapon.this.buffedLvl();
        }

        @Override
        public boolean hasEnchant(Class<? extends Enchantment> type, Char owner) {
            return GunWeapon.this.hasEnchant(type, owner);
        }

        @Override
        public float delayFactor(Char owner) {
            return super.delayFactor(owner);
        }

        @Override
        protected float baseDelay(Char owner) {
            if (isBurst) {
                return 1f;
            } else {
                return super.baseDelay(owner);
            }
        }

        @Override
        public float accuracyFactor(Char owner, Char target) {
            float ACC = super.accuracyFactor(owner, target);

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

        /*@Override
        protected void onThrow(int cell) {
            super.onThrow(cell);
        }*/

        @Override
        protected void rangedHit(Char enemy, int cell) {
            if (curUser != null) {
                if (isBurst && !enemy.isAlive() && curUser.hasTalent(Talent.HURRICANE)) {
                    Buff.affect(curUser, GreaterHaste.class).set(1 + curUser.pointsInTalent(Talent.HURRICANE));
                }
            }
        }

        @Override
        public int STRReq() {
            return GunWeapon.this.STRReq();
        }

        int burstCount = -1;
        Actor burstActor = null;

        @Override
        public void cast(Hero user, int dst) {
            if (isBurst){
                if (burstCount == -1) burstCount = 3;

                final int cell;
                if (user.pos == dst){
                    cell = targetRandom();
                } else {
                    cell = throwPos( user, dst );
                }
                Char enemy = Actor.findChar(cell);

                if (enemy == null){
                    if (user.buff(Talent.LethalMomentumTracker.class) != null){
                        user.buff(Talent.LethalMomentumTracker.class).detach();
                        user.next();
                    } else {
                        user.spendAndNext(castDelay(user, cell));
                    }
                    isBurst = false;
                    burstCount = -1;

                    if (burstActor != null){
                        burstActor.next();
                        burstActor = null;
                    }
                    return;
                }

                QuickSlotButton.target(enemy);

                user.busy();

                throwSound();

                user.sprite.zap(cell);
                ((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
                        reset(user.sprite,
                                cell,
                                this,
                                new Callback() {
                                    @Override
                                    public void call() {
                                        if (enemy.isAlive()) {
                                            curUser = user;
                                            onThrow(cell);
                                        }

                                        burstCount--;
                                        if (burstCount > 0) {
                                            if (canShoot()) {
                                                Actor.add(new Actor() {
                                                    {
                                                        actPriority = VFX_PRIO - 1;
                                                    }

                                                    @Override
                                                    protected boolean act() {
                                                        burstActor = this;
                                                        int target;
                                                        if (user.pos == dst) {
                                                            target = dst;
                                                        } else {
                                                            target = QuickSlotButton.autoAim(enemy, GunMissile.this);
                                                            if (target == -1) target = cell;
                                                        }
                                                        cast(user, target);
                                                        Actor.remove(this);
                                                        return false;
                                                    }
                                                });
                                                curUser.next();
                                            } else {
                                                GLog.w(Messages.get(SharpShooterBuff.class, "no_ammo_during"));

                                                if (user.buff(Talent.LethalMomentumTracker.class) != null) {
                                                    user.buff(Talent.LethalMomentumTracker.class).detach();
                                                    user.next();
                                                } else {
                                                    user.spendAndNext(castDelay(user, cell));
                                                }
                                                isBurst = false;
                                                burstCount = -1;
                                            }
                                        } else {
                                            if (user.buff(Talent.LethalMomentumTracker.class) != null) {
                                                user.buff(Talent.LethalMomentumTracker.class).detach();
                                                user.next();
                                            } else {
                                                user.spendAndNext(castDelay(user, cell));
                                            }
                                            isBurst = false;
                                            burstCount = -1;
                                        }

                                        if (burstActor != null){
                                            burstActor.next();
                                            burstActor = null;
                                        }
                                    }
                                });

            } else {
                super.cast(user, dst);
            }
        }

        private int targetRandom() {
            ArrayList<Integer> candidates = new ArrayList<>();

            for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
                if (Dungeon.level.heroFOV[mob.pos]) {
                    int cell = QuickSlotButton.autoAim(mob, this);
                    if (cell != -1) {
                        candidates.add(cell);
                    }
                }
            }

            if (candidates.isEmpty()) {
                return -1;
            } else {
                return Random.element(candidates);
            }
        }

        public boolean isBurst() {
            return isBurst;
        }
    }

    protected abstract CellSelector.Listener getShooter();

    /*protected CellSelector.Listener shooter = new CellSelector.Listener() {
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
    };*/

}
