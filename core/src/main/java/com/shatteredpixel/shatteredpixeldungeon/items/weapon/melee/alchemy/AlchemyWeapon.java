package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.changer.BluePrint;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public interface AlchemyWeapon {
    default BluePrint.Recipe weaponRecipe() {
        return BluePrint.recipeMap.get(this);
    }

    static String hintString(Class<? extends MeleeWeapon> wepClass) {
        Class<? extends Item>[] requirements = BluePrint.recipeMap.get(wepClass).requirements;
        if (requirements[2] == MeleeWeapon.class) {
            return Messages.get(Item.class, "discover_hint_alchemy_one",
                        Messages.get(requirements[1], "name"));
        } else {
            return Messages.get(Item.class, "discover_hint_alchemy_two",
                        Messages.get(requirements[1], "name"),
                        Messages.get(requirements[2], "name"));
        }
    }

}
