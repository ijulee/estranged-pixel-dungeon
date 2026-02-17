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

package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Enchanting;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KnightsShield;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class StoneOfEnchantment extends InventoryStone {
	
	{
		preferredBag = Belongings.Backpack.class;
		image = ItemSpriteSheet.STONE_ENCHANT;

		unique = true;
	}

	@Override
	protected boolean usableOnItem(Item item) {
		return ScrollOfEnchantment.enchantable(item);
	}
	
	@Override
	protected void onItemSelected(Item item) {
		if (!anonymous) {
			curItem.detach(curUser.belongings.backpack);
			Catalog.countUse(getClass());
			Talent.onRunestoneUsed(curUser, curUser.pos, getClass());
		}
		
		if (item instanceof Weapon) {
			
			((Weapon)item).enchant();
			
		} else if (item instanceof Armor) {

			BrokenSeal seal = ((Armor) item).checkSeal();
			if (seal != null && seal.canTransferGlyph() && seal.amuletApplied) {
				chooseInscribe((Armor) item);
				return;
			} else {
				((Armor)item).inscribe();
			}
		} else { // item instanceof KnightsShield

			((KnightsShield) item).inscribe();

		}
		
		curUser.sprite.emitter().start( Speck.factory( Speck.LIGHT ), 0.1f, 5 );
		Enchanting.show( curUser, item );
		
		if (item instanceof Weapon) {
			GLog.p(Messages.get(this, "weapon"));
		} else if (item instanceof Armor) {
			GLog.p(Messages.get(this, "armor"));
		} else { //item instanceof KnightsShield
			GLog.p(Messages.get(this, "shield"));
		}
		
		useAnimation();
		
	}

	private void chooseInscribe(Armor armor) {
		GameScene.show(new Armor.WndChooseInscribe(armor) {
			@Override
			public void chooseArmor() {
				armor.inscribe();
				curUser.sprite.emitter().start( Speck.factory( Speck.LIGHT ), 0.1f, 5 );
				Enchanting.show( curUser, armor );

				GLog.p(Messages.get(StoneOfEnchantment.class, "armor"));

				useAnimation();
			}

			@Override
			public void chooseSeal() {
				armor.inscribeSeal();
				curUser.sprite.emitter().start( Speck.factory( Speck.LIGHT ), 0.1f, 5 );
				Enchanting.show( curUser, armor.checkSeal() );

				GLog.p(Messages.get(StoneOfEnchantment.class, "seal"));

				useAnimation();
			}
		});
	}

	@Override
	public int value() {
		return 30 * quantity;
	}

	@Override
	public int energyVal() {
		return 5 * quantity;
	}

}
