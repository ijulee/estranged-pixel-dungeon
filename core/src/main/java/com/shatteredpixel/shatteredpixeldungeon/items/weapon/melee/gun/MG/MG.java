package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.MG;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class MG extends Gun {

    {
        maxRounds = 4;
        rounds = maxRounds;
        shotsPerRound = 3;
        shootingAcc = 0.9f;
        adjShootingAcc = 0.3f;
    }

    @Override
    public int baseMissileMax(int lvl) {
        return 2 * (tier()+2) +
                Math.round(0.5f * lvl * (tier()+2)); //2강 당 3/4/5/6/7 증가
    }

    @Override
    public Bullet getMissile(){
        return new MGBullet();
    }

    public class MGBullet extends Bullet {
        {
            image = ItemSpriteSheet.TRIPLE_BULLET;
        }
    }

}
