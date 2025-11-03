package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Brute;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.bow.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.GunWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class SharpShooterBuff extends Buff implements ActionIndicator.Action {

    {
        type = buffType.NEUTRAL;
        revivePersists = true;
    }

    @Override
    public boolean attachTo(Char target) {
        if (!(target instanceof Hero)) return false;

        ActionIndicator.setAction(this);
        return super.attachTo(target);
    }

    @Override
    public void detach() {
        ActionIndicator.clearAction();
        super.detach();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);

        ActionIndicator.setAction(this);
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }

    @Override
    public int actionIcon() {
        return HeroIcon.SHARPSHOOTING;
    }

    @Override
    public int indicatorColor() {
        return 0x1F1F1F;
    }

    @Override
    public void doAction() {
        Hero hero = Dungeon.hero;
        if (hero == null) return;

        KindOfWeapon wep = hero.belongings.weapon();
        if (!(wep instanceof GunWeapon)) {
            GLog.w(Messages.get(SharpShooterBuff.class, "no_weapon"));
            return;
        }

        if (!((GunWeapon) wep).canShoot()) {
            GLog.w(Messages.get(SharpShooterBuff.class, "no_ammo"));
            return;
        }

        GameScene.selectCell(burstShooter);
    }

    /*private static int randomDirection() {
        ArrayList<Integer> candidates = new ArrayList<>();
        for (Char ch : Actor.chars()) {
            if (ch.alignment != Char.Alignment.ENEMY) continue;
            if (!Dungeon.level.heroFOV[ch.pos]) continue;

            int cell = ch.pos;
            Ballistica aim = new Ballistica(Dungeon.hero.pos, cell, Ballistica.MAGIC_BOLT);
            int destination = aim.collisionPos;
            if (cell == destination) {
                candidates.add(cell);
            }
        }
        Dungeon.hero.next();
        try {
            return Random.element(candidates);
        } catch (NullPointerException e) {
            return -1;
        }
    }*/

    /*private void randomlyShootBullet(Gun gun, float delay, boolean isLast) {
        int direction = randomDirection();
        if (direction == -1 || gun.round() <= 0) {
            Dungeon.hero.next();
            return;
        }

        Dungeon.hero.sprite.parent.add(new Tweener(Dungeon.hero.sprite.parent, delay) {
            @Override
            protected void updateValues(float progress) {
                Dungeon.hero.busy();
            }

            @Override
            protected void onComplete() {
                super.onComplete();
                Gun.Bullet bullet = gun.knockBullet();
                bullet.isBurst = true;
                bullet.cast(Dungeon.hero, direction, true, 0, new Callback() {
                    @Override
                    public void call() {
                        if (isLast) {
                            Dungeon.hero.spendAndNext(bullet.castDelay(Dungeon.hero, direction));
                            if (Dungeon.hero.hasTalent(Talent.PERFECT_SHOT)) {
                                gun.manualReload(Dungeon.hero.pointsInTalent(Talent.PERFECT_SHOT), false);
                            }
                        }
                    }
                });
                CellEmitter.heroCenter(Dungeon.hero.pos).burst(BulletParticle.factory(DungeonTilemap.tileCenterToWorld(direction)), 10);
                bullet.throwSound();
                Dungeon.hero.sprite.zap(direction);
            }
        });
    }*/

    /*private void randomlyShootArrow(BowWeapon bow, float delay, boolean isLast) {
        int direction = randomDirection();
        if (direction == -1 || Dungeon.bullet <= 0) {
            Dungeon.hero.next();
            return;
        }

        Dungeon.hero.sprite.parent.add(new Tweener(Dungeon.hero.sprite.parent, delay) {
            @Override
            protected void updateValues(float progress) {
                Dungeon.hero.busy();
            }

            @Override
            protected void onComplete() {
                super.onComplete();
                BowWeapon.Arrow arrow = bow.getMissile();
                bow.isBurst = true;
                arrow.cast(Dungeon.hero, direction, true, 0, new Callback() {
                    @Override
                    public void call() {
                        if (isLast) Dungeon.hero.spendAndNext(arrow.castDelay(Dungeon.hero, direction));
                    }
                });
                arrow.throwSound();
                Dungeon.hero.sprite.zap(direction);
            }
        });
    }*/

    /*private void bulletBurst(Gun gun, float delay, final int direction, boolean isLast) {
        if (direction == -1 || gun.round() <= 0) {
            Dungeon.hero.next();
            return;
        }

        Dungeon.hero.sprite.parent.add(new Tweener(Dungeon.hero.sprite.parent, delay) {
            @Override
            protected void updateValues(float progress) {
                Dungeon.hero.busy();
            }

            @Override
            protected void onComplete() {
                super.onComplete();
                Gun.Bullet bullet = gun.knockBullet();
                bullet.isBurst = true;
                bullet.cast(Dungeon.hero, direction, true, 0, new Callback() {
                    @Override
                    public void call() {
                        if (isLast) {
                            Dungeon.hero.spendAndNext(bullet.castDelay(Dungeon.hero, direction));
                            if (Dungeon.hero.hasTalent(Talent.PERFECT_SHOT)) {
                                gun.manualReload(Dungeon.hero.pointsInTalent(Talent.PERFECT_SHOT), false);
                            }
                        }
                    }
                });
                bullet.throwSound();
                Dungeon.hero.sprite.zap(direction);
            }
        });
    }*/

    /*private void arrowBurst(BowWeapon bow, float delay, final int direction, boolean isLast) {
        if (direction == -1 || Dungeon.bullet <= 0) {
            Dungeon.hero.next();
            return;
        }

        Dungeon.hero.sprite.parent.add(new Tweener(Dungeon.hero.sprite.parent, delay) {
            @Override
            protected void updateValues(float progress) {
                Dungeon.hero.busy();
            }

            @Override
            protected void onComplete() {
                super.onComplete();
                BowWeapon.Arrow arrow = bow.getMissile();
                bow.isBurst = true;
                arrow.cast(Dungeon.hero, direction, true, 0, new Callback() {
                    @Override
                    public void call() {
                        if (isLast) Dungeon.hero.spendAndNext(arrow.castDelay(Dungeon.hero, direction));
                    }
                });
                arrow.throwSound();
                Dungeon.hero.sprite.zap(direction);
            }
        });
    }*/

    CellSelector.Listener burstShooter = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer cell) {
            if (cell == null) return;

            Hero hero = (Hero) target;
            GunWeapon wep = (GunWeapon) hero.belongings.weapon();
            if (!wep.canShoot()) return;

            GunWeapon.GunMissile missile = wep.getMissile();
            if (missile == null) return;

            wep.isBurst = true;

            missile.cast(hero, cell);

            /* if (cell == null) return;

            Hero hero = (Hero) target;
            KindOfWeapon wep = hero.belongings.weapon();
            if (wep == null || (!(wep instanceof Gun) && !(wep instanceof BowWeapon))) return;

            int shots = 3;
            float delay = 0.2f;

            if (wep instanceof Gun) {
                if (((Gun) wep).round() < shots) shots = ((Gun) wep).round();
                if (shots <= 0) {
                    GLog.w(Messages.get(SharpShooterBuff.class, "no_bullet"));
                    return;
                }

                if (cell == Dungeon.hero.pos) {
                    for (int i = 0; i < shots; i++) {
                        randomlyShootBullet((Gun) wep, i*delay, i == shots-1);
                    }
                } else {
                    for (int i = 0; i < shots; i++) {
                        bulletBurst((Gun) wep, i*delay, cell, i == shots-1);
                    }
                }
            } else if (wep instanceof BowWeapon) {
                if (Dungeon.bullet < shots) shots = Dungeon.bullet;
                if (shots <= 0) {
                    GLog.w(Messages.get(SharpShooterBuff.class, "no_arrow"));
                    return;
                }

                if (cell == Dungeon.hero.pos) {
                    for (int i = 0; i < shots; i++) {
                        randomlyShootArrow((BowWeapon) wep, i*delay, i == shots-1);
                    }
                } else {
                    for (int i = 0; i < shots; i++) {
                        arrowBurst((BowWeapon) wep, i*delay, cell, i == shots-1);
                    }
                }
            }*/

            Buff.affect(target, BurstShotCooldown.class);
            detach();
        }

        @Override
        public String prompt() {
            return Messages.get(SpiritBow.class, "prompt");
        }
    };

    public static void onKill() {
        if (Dungeon.hero.buff(BurstShotCooldown.class) != null) {
            Dungeon.hero.buff(BurstShotCooldown.class).onKill();
        }
    }

    public static void procRangedLethal(Char enemy, GunWeapon.GunMissile wep) {
        if (enemy.isAlive() && enemy.alignment == Char.Alignment.ENEMY &&
                !Char.hasProp(enemy, Char.Property.BOSS) && !Char.hasProp(enemy, Char.Property.MINIBOSS) &&
                (enemy.HP/(float)enemy.HT) <= 0.1f*Dungeon.hero.pointsInTalent(Talent.RANGED_LETHALITY)) {
            enemy.HP = 0;
            if (enemy.buff(Brute.BruteRage.class) != null){
                enemy.buff(Brute.BruteRage.class).detach();
            }
            if (!enemy.isAlive()) {
                enemy.die(wep);
            } else {
                //helps with triggering any on-damage effects that need to activate
                enemy.damage(-1, wep);
                //DeathMark.processFearTheReaper(enemy);
            }
            if (enemy.sprite != null) {
                enemy.sprite.showStatus(CharSprite.NEGATIVE, Messages.get(SharpShooterBuff.class, "executed"));
            }
        }
    }

    public static void procChanneling(Char attacker, Char defender, int damage) {
        if (attacker instanceof Hero
                && ((Hero)attacker).hasTalent(Talent.CHANNELING)
                && Random.Float() < 0.1f*((Hero)attacker).pointsInTalent(Talent.CHANNELING)
                && !defender.isImmune(Electricity.class)) {
            defender.damage(Hero.heroDamageIntRange(Math.round(damage*0.2f), Math.round(damage*0.6f)), new Electricity());
            if (!defender.isAlive()) {
                onKill();
            }
            ThunderImbue.thunderEffect(defender.sprite);
        }
    }

    public static class BurstShotCooldown extends Buff {
        {
            type = buffType.NEUTRAL;
        }

        int shots = 0;

        private static final String SHOOT = "shoot";

        private int maxShots() {
            return 9 - 2 * Dungeon.hero.pointsInTalent(Talent.FOCUS_SHOT);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);

            bundle.put(SHOOT, shots);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);

            shots = bundle.getInt(SHOOT);
        }

        @Override
        public int icon() {
            return BuffIndicator.TIME;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0x1F1F1F);
        }

        @Override
        public float iconFadePercent() {
            if (!Dungeon.hero.hasTalent(Talent.FOCUS_SHOT)) return 0;

            int max = maxShots();
            return Math.max(0, (max - shots)/((float) max));
        }

        @Override
        public String iconTextDisplay() {
            if (!Dungeon.hero.hasTalent(Talent.FOCUS_SHOT)) return super.iconTextDisplay();

            return Integer.toString(maxShots() - shots);
        }

        @Override
        public String desc() {
            if (!Dungeon.hero.hasTalent(Talent.FOCUS_SHOT))
                return super.desc();
            else
                return Messages.get(this, "alt_desc", maxShots() - shots);
        }

        public void onKill() {
            Buff.affect(target, SharpShooterBuff.class);
            detach();
        }

        public void onHit() {
            if (!Dungeon.hero.hasTalent(Talent.FOCUS_SHOT)) return;

            shots++;
            if (shots >= maxShots()) {
                Buff.affect(target, SharpShooterBuff.class);
                detach();
            }
        }

        public static void onHit(Char attacker, Char defender) {
            if (attacker == Dungeon.hero && defender.alignment != Char.Alignment.ALLY &&
                    attacker.buff(BurstShotCooldown.class) != null) {
                attacker.buff(BurstShotCooldown.class).onHit();
            }
        }
    }
}
