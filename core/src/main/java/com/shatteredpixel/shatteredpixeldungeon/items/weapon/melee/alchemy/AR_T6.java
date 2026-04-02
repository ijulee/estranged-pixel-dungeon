package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.AR;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class AR_T6 extends AR implements AlchemyWeapon {
    {
        image = ItemSpriteSheet.AR_T6;

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
