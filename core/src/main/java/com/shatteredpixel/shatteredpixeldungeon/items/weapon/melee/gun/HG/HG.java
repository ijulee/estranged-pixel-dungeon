package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.HG;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
public class HG extends Gun {

    {
        maxRounds = 4;
        rounds = maxRounds;
        shootingSpeed = 0.5f;
        reloadTime = 1f;
        adjShootingAcc = 2f;
    }

    @Override
    public int baseMissileMax(int lvl) {
        return 2 * (tier() + 1) +
                lvl * (tier() + 1);
    }

    @Override
    public Bullet getMissile(){
        return new HGBullet();
    }

    public class HGBullet extends Bullet {
        {
            image = ItemSpriteSheet.SINGLE_BULLET;
        }
    }
}
