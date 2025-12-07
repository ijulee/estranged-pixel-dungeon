package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.GL;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class GL extends Gun {

    {
        maxRounds = 2;
        rounds = maxRounds;
        explode = true;
    }

    @Override
    public int baseMissileMax(int lvl) {
        return 6 * (tier() + 1) +
                lvl * (tier() + 1);
    }

    @Override
    public Bullet getMissile(){
        return new GLBullet();
    }

    public class GLBullet extends Bullet {
        {
            image = ItemSpriteSheet.GRENADE_RED;
        }
    }

}
