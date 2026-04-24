package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.LG;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LaserParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collections;

public class LG extends Gun {

    {
        maxRounds = 2;
        rounds = maxRounds;
        ammoPerRound = 3;
        shootingAcc = 1.5f;
    }

    @Override
    public int baseMissileMax(int lvl) {
        return 3 * (tier() + 1) +
                lvl * (tier() + 1);
    }

    @Override
    public Bullet getMissile(){
        return new LGBullet();
    }

    public class LGBullet extends Bullet {
        {
            hitSound = Assets.Sounds.BURNING;
            image = ItemSpriteSheet.NO_BULLET;
        }

        public int maxDist() {
            return 2 * (tier() + 1);
        }

        @Override
        public int throwPos(Hero user, int dst) {
            Ballistica beam = new Ballistica(user.pos, dst, Ballistica.WONT_STOP);
            return beam.path.get(Math.min(beam.dist, this.maxDist()));
        }

        @Override
        protected void onThrow(int cell) {
            if (cell != curUser.pos) {
                Ballistica aim = new Ballistica(curUser.pos, cell, Ballistica.WONT_STOP);
                ArrayList<Char> targets = new ArrayList<>();
                int maxDist = maxDist();
                int dist = Math.min(aim.dist, maxDist);
                int endCell = aim.path.get(Math.min(aim.dist, dist));
                boolean terrainAffected = false;
                for (int c : aim.subPath(1, maxDist)) {

                    Char ch;
                    if ((ch = Actor.findChar( c )) != null) {
                        targets.add( ch );
                    }

                    if (Dungeon.level.flamable[c]) {
                        Dungeon.level.destroy( c );
                        GameScene.updateMap( c );
                        terrainAffected = true;

                    }

                    CellEmitter.center( c ).burst( LaserParticle.BURST, 3 );
                }

                if (terrainAffected) {
                    Dungeon.observe();
                }

                float multi;
                WeightMod weightMod = getGunMod(WeightMod.class);
                switch (weightMod) {
                    case NORMAL_WEIGHT: default:
                        multi = 2f;
                        break;
                    case LIGHT_WEIGHT:
                        multi = 1f;
                        break;
                    case HEAVY_WEIGHT:
                        multi = 3f;
                        break;
                }
                curUser.sprite.parent.add(new Beam.SuperNovaRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld( endCell ), multi));

                if (targets.isEmpty()) {
                    //mainly to proc seer shot when no chars in range
                    super.onThrow(cell);
                    return;
                }

                //furthest to closest, mainly for elastic
                Collections.sort(targets, (a, b) -> Float.compare(
                        Dungeon.level.trueDistance(b.pos, curUser.pos),
                        Dungeon.level.trueDistance(a.pos, curUser.pos)));

                for (Char target : targets) {
                    shootTarget(target);
                }
            }
        }

        @Override
        public void ghostThrow(DriedRose.GhostHero ghost, int cell) {
            if (cell != ghost.pos) {
                Ballistica aim = new Ballistica(ghost.pos, cell, Ballistica.WONT_STOP);
                ArrayList<Char> targets = new ArrayList<>();
                int maxDist = maxDist();
                int dist = Math.min(aim.dist, maxDist);
                int endCell = aim.path.get(Math.min(aim.dist, dist));
                boolean terrainAffected = false;
                boolean visibleBeam = false;
                for (int c : aim.subPath(1, maxDist)) {

                    Char ch;
                    if ((ch = Actor.findChar( c )) != null) {
                        targets.add( ch );
                    }

                    if (Dungeon.level.flamable[c]) {
                        Dungeon.level.destroy( c );
                        GameScene.updateMap( c );
                        terrainAffected = true;

                    }

                    if (Dungeon.level.heroFOV[c]) {
                        CellEmitter.center( c ).burst( LaserParticle.BURST, 3 );
                        visibleBeam = true;
                    }
                }

                if (terrainAffected) {
                    Dungeon.observe();
                }

                float multi;
                WeightMod weightMod = getGunMod(WeightMod.class);
                switch (weightMod) {
                    case NORMAL_WEIGHT: default:
                        multi = 2f;
                        break;
                    case LIGHT_WEIGHT:
                        multi = 1f;
                        break;
                    case HEAVY_WEIGHT:
                        multi = 3f;
                        break;
                }

                if (visibleBeam) {
                    ghost.sprite.parent.add(
                            new Beam.SuperNovaRay(
                                    ghost.sprite.center(),
                                    DungeonTilemap.raisedTileCenterToWorld( endCell ),
                                    multi) );
                }

                if (targets.isEmpty()) {
                    return;
                }

                //furthest to closest, mainly for elastic
                Collections.sort(targets, (a, b) -> Float.compare(
                        Dungeon.level.trueDistance(b.pos, ghost.pos),
                        Dungeon.level.trueDistance(a.pos, ghost.pos)));

                for (Char target : targets) {
                    ghostShoot(ghost, target);
                }

                useRound();
            }
        }

        @Override
        public void showPuff(int cell) {
            // does nothing
        }

        @Override
        protected void rangedHit(Char enemy, int cell) {
            enemy.sprite.centerEmitter().burst( LaserParticle.BURST, Random.Int(10+buffedLvl()) );
            enemy.sprite.flash();

            super.rangedHit(enemy, cell);
        }

        @Override
        public void throwSound() {
            Sample.INSTANCE.play(Assets.Sounds.RAY, 1f);
        }
    }

}
