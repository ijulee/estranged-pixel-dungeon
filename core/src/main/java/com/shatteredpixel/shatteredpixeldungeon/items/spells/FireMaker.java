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

package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfDragonsBreath;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

public class FireMaker extends TargetedSpell {
	
	{
		image = ItemSpriteSheet.FIREMAKER;
		usesTargeting = true;
		talentChance = 1/(float) Recipe.OUT_QUANTITY;
	}
	
	@Override
	protected void affectTarget(Ballistica bolt, Hero hero) {
		int cell = bolt.collisionPos;

		//only ignite source cell if it's flammable
		if (cell != bolt.sourcePos || Dungeon.level.flamable[cell]) {
            GameScene.add( Blob.seed( cell, 2, Fire.class ) );
        }

		//ignite cells that share a side with target cell, are flammable, and are further from the source pos
		//This prevents short-range casts not igniting barricades or bookshelves
		for (int i : PathFinder.NEIGHBOURS4) {
			if (Dungeon.level.trueDistance(cell+i, bolt.sourcePos) > Dungeon.level.trueDistance(cell, bolt.sourcePos)
					&& Dungeon.level.flamable[cell+i]
					&& Fire.volumeAt(cell+i, Fire.class) == 0) {
				GameScene.add( Blob.seed( cell+i, 2, Fire.class ) );
			}
		}

		onSpellused();
	}

	@Override
	protected void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar( curUser.sprite.parent,
				MagicMissile.FIRE,
				curUser.sprite,
				bolt.collisionPos,
				callback);
		Sample.INSTANCE.play( Assets.Sounds.BURNING );
	}

	@Override
	public int value() {
		//prices of ingredients, divided by output quantity
		return Math.round((30+20) * (quantity/(float) Recipe.OUT_QUANTITY));
	}

	@Override
	public int energyVal() {
		return (int)((6+4+4) * (quantity/(float) Recipe.OUT_QUANTITY));
	}
	
	public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

		private static final int OUT_QUANTITY = 4;
		
		{
			inputs =  new Class[]{PotionOfDragonsBreath.class};
			inQuantity = new int[]{1};

			cost = 4;
			
			output = FireMaker.class;
			outQuantity = OUT_QUANTITY;
		}
		
	}
}
