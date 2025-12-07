package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SMG;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class SMG extends Gun {

    {
        maxRounds = 4;
        rounds = maxRounds;
        shotsPerRound = 3;
        shootingAcc = 1.2f;
        adjShootingAcc = 1.5f;
    }

    @Override
    public int baseMissileMax(int lvl) {
        return 2 * (tier()+1) +
                Math.round(0.5f * lvl * (tier()+1)); //2강 당 2/3/4/5/6 증가
    }

    @Override
    public Bullet getMissile(){
        return new SMGBullet();
    }

    public class SMGBullet extends Bullet {
        {
            image = ItemSpriteSheet.TRIPLE_BULLET;
        }
    }

}
