package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.HG.HG;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class HG_T6 extends HG implements AlchemyWeapon {
    {
        image = ItemSpriteSheet.HG_T6;

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
