package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SR;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornKatana;
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

        @Override
        public int damageRoll(Char owner) {
            int damage = augment.damageFactor(WornKatana.damageRoll(owner, min(), max()));
            if (owner instanceof Hero) {
                int exStr = ((Hero)owner).STR() - STRReq();
                if (exStr > 0) {
                    damage += Hero.heroDamageIntRange( 0, exStr );
                }
            }

            return damage;
        }
    }
}
