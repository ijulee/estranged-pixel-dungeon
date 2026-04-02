package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.GL.GL;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class GL_T6 extends GL implements AlchemyWeapon {
    {
        image = ItemSpriteSheet.GL_T6;

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
