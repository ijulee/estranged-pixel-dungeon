package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public abstract class ElementalBullet extends FlavourBuff{
    {
        type = buffType.POSITIVE;
        announced = true;
    }

    public static final float DURATION	= 100f;

    @Override
    public int icon() {
        return BuffIndicator.BULLET;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0, (DURATION - visualcooldown()) / DURATION);
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    public abstract void proc(Char enemy);
}
