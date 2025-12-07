package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SR;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class SR extends Gun {

    {
        maxRounds = 2;
        rounds = maxRounds;
        reloadTime = 3f;
        shootingAcc = 2f;
        adjShootingAcc = 0.3f;
    }

    @Override
    public int baseMissileMax(int lvl) {
        return 4 * (tier()+2) +
                lvl * (tier()+2);
    }

    @Override
    public Bullet getMissile(){
        return new SRBullet();
    }

    public class SRBullet extends Bullet {
        {
            image = ItemSpriteSheet.SNIPER_BULLET;
        }
    }
}
