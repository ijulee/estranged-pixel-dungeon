package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class AR extends Gun {

    {
        maxRounds = 4;
        rounds = maxRounds;
    }

    @Override
    public int baseMissileMax(int lvl) {
        return 4 * (tier() + 1) +
                lvl * (tier() + 1);
    }

    @Override
    public Bullet getMissile(){
        return new ARBullet();
    }

    public class ARBullet extends Bullet {
        {
            image = ItemSpriteSheet.SINGLE_BULLET;
        }
    }
}
