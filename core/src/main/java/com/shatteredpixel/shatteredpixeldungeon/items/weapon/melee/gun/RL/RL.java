package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.RL;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RL extends Gun {

    {
        maxRounds = 2;
        rounds = maxRounds;
        ammoPerRound = 3;
        explode = true;
    }

    @Override
    public int baseMissileMax(int lvl) {
        return 6 * (tier() + 2) +
                lvl * (tier() + 2);
    }

    @Override
    public Bullet getMissile(){
        return new RLBullet();
    }

    public class RLBullet extends Bullet {
        {
            image = ItemSpriteSheet.ROCKET;
        }
    }

}
