/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
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

import static com.shatteredpixel.shatteredpixeldungeon.items.Item.updateQuickslot;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class EnhancedRingsCombo extends CountCooldownBuff {

	{
		type = buffType.POSITIVE;
	}

	public int getCombo() {
		return getCount();
	}

	public static int maxCombo() {
		return Dungeon.hero.pointsInTalent(Talent.RING_KNUCKLE);
	}

	public static int maxDuration() {
		return (maxCombo() >= 2) ? 2 : 1;
	}

	@Override
	public void detach() {
		super.detach();
		if (target instanceof Hero) ((Hero) target).updateHT(false);
		updateQuickslot();
	}

	@Override
	public int icon() {
		return BuffIndicator.UPGRADE;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(0, 1, 0);
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, 1 - visualcooldown() / maxDuration());
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", visualcooldown(), getCombo());
	}

	@Override
	public Class<? extends FlavourBuff> getCounterClass() {
		return EnhancedRingsCounter.class;
	}

	public static class EnhancedRingsCounter extends FlavourBuff {}
}
