package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.LG;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LaserParticle;
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

        @Override
        protected void onThrow(int cell) {
            if (cell != curUser.pos) {
                Ballistica aim = new Ballistica(curUser.pos, cell, Ballistica.WONT_STOP);
                ArrayList<Char> targets = new ArrayList<>();
                int maxDist = 2*(LG.this.tier+1);
                int dist = Math.min(aim.dist, maxDist);
                int cells = aim.path.get(Math.min(aim.dist, dist));
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
                curUser.sprite.parent.add(new Beam.SuperNovaRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld( cells ), multi));

                //furthest to closest, mainly for elastic
                Collections.sort(targets, (a, b) -> Float.compare(
                        Dungeon.level.trueDistance(b.pos, curUser.pos),
                        Dungeon.level.trueDistance(a.pos, curUser.pos)));

                for (Char ch : targets) {
                    super.onThrow(ch.pos);
                }
            }
        }

        @Override
        public void showPuff(int cell) {
            return; // does nothing
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
