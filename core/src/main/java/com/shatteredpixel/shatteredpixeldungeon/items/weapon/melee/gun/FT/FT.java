package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.FT;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;
import java.util.Collections;

public class FT extends Gun {

    {
        maxRounds = 2;
        rounds = maxRounds;
        ammoPerRound = 2;
        shootingAcc = 1.5f;
    }

    @Override
    public int baseMissileMax(int lvl) {
        return 3 * (tier() + 1) +
                lvl * (tier() + 1);
    }

    @Override
    public Bullet getMissile(){
        return new FTBullet();
    }

    public class FTBullet extends Bullet {
        {
            hitSound = Assets.Sounds.BURNING;
            image = ItemSpriteSheet.NO_BULLET;
        }

        @Override
        protected void onThrow(int cell) {
            if (cell != curUser.pos) {
                Ballistica aim = new Ballistica(curUser.pos, cell, Ballistica.WONT_STOP);
                int maxDist = FT.this.tier + 1;
                int dist = Math.min(aim.dist, maxDist);
                ConeAOE cone = new ConeAOE(aim,
                        dist,
                        30,
                        Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID);
                //cast to cells at the tip, rather than all cells, better performance.
                for (Ballistica ray : cone.outerRays){
                    ((MagicMissile)curUser.sprite.parent.recycle( MagicMissile.class )).reset(
                            MagicMissile.FIRE_CONE,
                            curUser.sprite,
                            ray.path.get(ray.dist),
                            null
                    );
                }
                ArrayList<Char> targets = new ArrayList<>();
                for (int cells : cone.cells){
                    //knock doors open
                    if (Dungeon.level.map[cells] == Terrain.DOOR){
                        Level.set(cells, Terrain.OPEN_DOOR);
                        GameScene.updateMap(cells);
                    }

                    //only ignite cells directly near caster if they are flammable
                    if (!(Dungeon.level.adjacent(curUser.pos, cells) && !Dungeon.level.flamable[cells])) {
                        GameScene.add(Blob.seed(cells, 2, Fire.class));
                    }

                    Char ch = Actor.findChar(cells);
                    if (ch != null && ch.alignment != hero.alignment){
                        targets.add(ch);
                    }
                }

                //furthest to closest, mainly for elastic
                Collections.sort(targets, (a, b) -> Float.compare(
                        Dungeon.level.trueDistance(b.pos, curUser.pos),
                        Dungeon.level.trueDistance(a.pos, curUser.pos)));

                for (Char ch : targets) {
                    super.onThrow(ch.pos);
                }

                //final zap at 2/3 distance, for timing of the actual effect
                MagicMissile.boltFromChar(curUser.sprite.parent,
                        MagicMissile.FIRE_CONE,
                        curUser.sprite,
                        cone.coreRay.path.get(dist * 2 / 3),
                        () -> { });
            }
        }

        @Override
        public void showPuff(int cell) {
            return; // does nothing
        }

        @Override
        public void throwSound() {
            Sample.INSTANCE.play(Assets.Sounds.BURNING, 1f);
        }
    }
}
