/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Reflection;

// A buff that keeps track of a list of individual cooldowns
public abstract class CountCooldownBuff extends FlavourBuff {

    @Override
    protected void postpone(float time) {
        Buff.append(target, getCounterClass(), time);
        super.postpone(time);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString(getCount());
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", getCount(), dispTurns());
    }

    public static int getCount(Char target, Class<? extends CountCooldownBuff> cls) {
        Class<? extends FlavourBuff> counterClass = Reflection.newInstance(cls).getCounterClass();
        return target.buffs(counterClass).size();
    }

    public int getCount() {
        return getCount(target, this.getClass());
    }

    public abstract Class<? extends FlavourBuff> getCounterClass();
}
