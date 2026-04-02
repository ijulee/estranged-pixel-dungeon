package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.RL.RL;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RL_T6 extends RL implements AlchemyWeapon {
    {
        image = ItemSpriteSheet.RL_T6;

        tier = 6;
    }

    @Override
    public String discoverHint() {
        return AlchemyWeapon.hintString(this.getClass());
    }

    @Override
    public String desc() {
        return super.desc() + "\n\n" + discoverHint();
    }

}
