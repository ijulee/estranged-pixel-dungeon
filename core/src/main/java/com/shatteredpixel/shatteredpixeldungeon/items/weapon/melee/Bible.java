/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfCleansing;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Bible extends MeleeWeapon {

	{
		image = ItemSpriteSheet.BIBLE;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1.1f;

		tier = 3;
	}

	public static final float[] defaultChances = new float[] {3, 3, 3, 1};

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		float[] chances = defaultChances.clone();
		if (attacker.buff(Bless.class) != null) {
			chances[0] /= 2;
		} else if (attacker.buff(PotionOfCleansing.Cleanse.class) != null) {
			chances[1] /= 2;
		} else if (attacker.buff(Adrenaline.class) != null) {
			chances[2] /= 2;
		}

		switch (Random.chances(chances)) {
			case 0: default:
				Buff.affect( attacker, Bless.class, 2f );
				break;
			case 1:
				PotionOfCleansing.cleanseButHunger(attacker, 2f);
				break;
			case 2:
				Buff.affect( attacker, Adrenaline.class, 2f);
				break;
			case 3:
				attacker.heal(1+buffedLvl());
		}

		return super.proc( attacker, defender, damage );
	}

	@Override
	public int max(int lvl) {
		return  3*(tier+1) +    //12 base, down from 20
				lvl*(tier);     //+3 per level, down from +4
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		angelAbility(hero, 5+buffedLvl(), this);
	}

	public static void angelAbility(Hero hero, int duration, MeleeWeapon wep){
		wep.beforeAbilityUsed(hero, null);
		Buff.prolong(hero, Angel.class, duration);
		hero.next();
		((HeroSprite)hero.sprite).read();
		CellEmitter.get( Dungeon.hero.pos ).burst( Speck.factory( Speck.WOOL ), 6 );
		new Flare( 6, 32 ).color(0xFFFF00, true).show( curUser.sprite, 2f );
		Sample.INSTANCE.play( Assets.Sounds.PUFF );
		Sample.INSTANCE.play( Assets.Sounds.READ );
		wep.afterAbilityUsed(hero);
	}

	@Override
	public String abilityInfo() {
		if (levelKnown){
			return Messages.get(this, "ability_desc", 6+buffedLvl());
		} else {
			return Messages.get(this, "typical_ability_desc", 6);
		}
	}

	public static class Angel extends FlavourBuff {

		{
			announced = true;
			type = buffType.POSITIVE;
		}

		@Override
		public int icon() {
			return BuffIndicator.DUEL_ANGEL;
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (6 - visualcooldown()) / 6);
		}
	}

}
