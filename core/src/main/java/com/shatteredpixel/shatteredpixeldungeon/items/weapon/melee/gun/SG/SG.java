package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SG;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class SG extends Gun {

    {
        maxRounds = 2;
        rounds = maxRounds;
        shotsPerRound = 5;
        shootingAcc = 1f;
        adjShootingAcc = 3f;
        spread = true;
    }

    @Override
    public int reloadAmmoUse() {
        return maxRounds()- rounds;
    }

    @Override
    public int baseMissileMax(int lvl) {
        return  (tier()+1) +
                Math.round(0.5f * lvl * (tier()+1)); //2강 당 2/3/4/5/6 증가
    }

    @Override
    public Bullet getMissile(){
        return new SGBullet();
    }

    public class SGBullet extends Bullet {
        {
            image = ItemSpriteSheet.TRIPLE_BULLET;
        }

        @Override
        protected float adjacentAccFactor(Char owner, Char target) {
            return super.adjacentAccFactor(owner, target)*3f;
        }
    }
}
