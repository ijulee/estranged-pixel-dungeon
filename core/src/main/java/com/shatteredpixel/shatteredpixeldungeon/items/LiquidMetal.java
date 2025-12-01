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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

//these aren't considered potions internally as most potion effects shouldn't apply to them
//mainly due to their high quantity
public class LiquidMetal extends Item {

	{
		image = ItemSpriteSheet.LIQUID_METAL;

		stackable = true;

		defaultAction = AC_APPLY;

		bones = true;
	}

	private static final String AC_APPLY = "APPLY";
	private static final String AC_MAKE = "MAKE";

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_APPLY );
		if (hero.heroClass == HeroClass.GUNNER) {
			actions.add( AC_MAKE );
		}
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals(AC_APPLY)) {

			curUser = hero;
			GameScene.selectItem( itemSelector );

		}

		if (action.equals(AC_MAKE)) {
			int baseCost = 25 + 25 * (1+Dungeon.depth/5);
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show(
							new WndOptions( new ItemSprite(LiquidMetal.this),
									Messages.get(LiquidMetal.class, "make_title"),
									Messages.get(LiquidMetal.class, "make_desc", quantity, baseCost),
									Messages.get(LiquidMetal.class, "make_no_boost"),
									Messages.get(LiquidMetal.class, "make_boost"),
									Messages.get(LiquidMetal.class, "make_cancel") ) {

								private float elapsed = 0f;

								@Override
								public synchronized void update() {
									super.update();
									elapsed += Game.elapsed;
								}

								@Override
								public void hide() {
									if (elapsed > 0.2f){
										super.hide();
									}
								}

								@Override
								protected void onSelect( int opt ) {
									if (elapsed > 0.2f && opt != 2) {
										int craftCost = (opt == 0) ? baseCost : 2 * baseCost;
										// detach and collect since qty changes don't update backpack
										detachAll(Dungeon.hero.belongings.backpack);
										quantity(quantity-craftCost);
										if (!collect()) {
											Dungeon.level.drop( LiquidMetal.this, hero.pos ).sprite.drop();
										}

										MeleeWeapon wep = null;

										while (!(wep instanceof Gun)) {
                                            if (opt == 0) {
                                                wep = Generator.randomWeapon();
                                            } else {
                                                wep = Generator.randomWeapon(Dungeon.depth / 5 + 1);
                                                if (wep.cursed) {
                                                    wep = null;
                                                }
                                            }
                                        }

                                        if (opt == 0) {
											new Flare(6, 20).color(0x00FF00, true).show(hero.sprite, 3f);
                                        } else {
											wep.cursedKnown = true;
											if (Random.Int(3) < 1) {
                                                wep.upgrade(true);
                                            }
											new Flare(6, 28).color(0xAA00FF, true).show(hero.sprite, 3.67f);
										}

                                        if (wep.doPickUp(hero)) {
											hero.spend(-wep.pickupDelay());
											GLog.i(Messages.capitalize(Messages.get(hero, "you_now_have", wep.name())));
                                        } else {
                                            Dungeon.level.drop( wep, hero.pos ).sprite.drop();
                                        }

										hero.busy();
										hero.sprite.operate(hero.pos);
										Sample.INSTANCE.play(Assets.Sounds.EVOKE);
										CellEmitter.center( hero.pos ).burst( Speck.factory( Speck.STAR ), 7);
										hero.spendAndNext(1);
									}
								}

								@Override
								protected boolean hasInfo(int index) {
									return index < 2;
								}

								@Override
								protected void onInfo( int index ) {
									GameScene.show(new WndTitledMessage(
											Icons.get(Icons.INFO),
											Messages.titleCase(Messages.get(LiquidMetal.class, "make_info_title_" + (index+1))),
											Messages.get(LiquidMetal.class, "make_info_desc_" + (index+1))));
								}

								@Override
								protected boolean enabled( int index ){
									switch (index) {
										case 0:
											return quantity >= baseCost;
										case 1:
											return quantity >= 2*baseCost;
										case 2: default:
											return true;
									}
								}
							}
					);
				}
			});
		}
	}

	@Override
	protected void onThrow( int cell ) {
		if (Dungeon.level.map[cell] == Terrain.WELL || Dungeon.level.pit[cell]) {

			super.onThrow( cell );

		} else  {

			Dungeon.level.pressCell( cell );
			if (Dungeon.level.heroFOV[cell]) {
				GLog.i( Messages.get(Potion.class, "shatter") );
				Sample.INSTANCE.play( Assets.Sounds.SHATTER );
				Splash.at( cell, 0xBFBFBF, 5 );
			}

		}
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int value() {
		return quantity;
	}

	private final WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return Messages.get(LiquidMetal.class, "prompt");
		}

		@Override
		public Class<?extends Bag> preferredBag(){
			return MagicalHolster.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item instanceof MissileWeapon && !(item instanceof Dart);
		}

		@Override
		public void onSelect( Item item ) {
			if (item != null && item instanceof MissileWeapon) {
				MissileWeapon m = (MissileWeapon)item;

				float maxToUse = 5*(m.tier+1);
				maxToUse *= Math.pow(1.35f, m.level());

				float durabilityPerMetal = 100 / maxToUse;

				//we remove a tiny amount here to account for rounding errors
				float percentDurabilityLost = 0.999f - (m.durabilityLeft()/100f);
				int toUse = (int)Math.ceil(maxToUse*percentDurabilityLost);
				if (toUse == 0 ||
						Math.ceil(m.durabilityLeft()/ m.durabilityPerUse()) >= Math.ceil(m.MAX_DURABILITY/ m.durabilityPerUse()) ){

					if (m.quantity() < m.defaultQuantity()){
						if (quantity()*durabilityPerMetal >= m.durabilityPerUse()){
							m.quantity(m.quantity()+1);
							if (maxToUse < quantity()){
								Catalog.countUses(LiquidMetal.class, (int)Math.ceil(maxToUse));
								GLog.i(Messages.get(LiquidMetal.class, "apply", (int)Math.ceil(maxToUse)));
								quantity -= (int)Math.ceil(maxToUse);
							} else {
								Catalog.countUses(LiquidMetal.class, quantity());
								m.damage(100f);
								m.repair(quantity()*durabilityPerMetal-1);
								GLog.i(Messages.get(LiquidMetal.class, "apply", quantity()));
								detachAll(Dungeon.hero.belongings.backpack);
							}
						} else {
							GLog.w(Messages.get(LiquidMetal.class, "already_fixed"));
							return;
						}
					} else {
						GLog.w(Messages.get(LiquidMetal.class, "already_fixed"));
						return;
					}
				} else if (toUse < quantity()) {
					Catalog.countUses(LiquidMetal.class, toUse);
					m.repair(maxToUse*durabilityPerMetal);
					quantity(quantity()-toUse);
					GLog.i(Messages.get(LiquidMetal.class, "apply", toUse));

				} else {
					Catalog.countUses(LiquidMetal.class, quantity());
					m.repair(quantity()*durabilityPerMetal);
					GLog.i(Messages.get(LiquidMetal.class, "apply", quantity()));
					detachAll(Dungeon.hero.belongings.backpack);
				}

				curUser.sprite.operate(curUser.pos);
				Sample.INSTANCE.play(Assets.Sounds.DRINK);
				updateQuickslot();
				curUser.sprite.emitter().start(Speck.factory(Speck.LIGHT), 0.1f, 10);
			}
		}
	};

	public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe {

		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			return ingredients.size() == 1
					&& ingredients.get(0) instanceof MissileWeapon
					&& ingredients.get(0).cursedKnown
					&& !ingredients.get(0).cursed;
		}

		@Override
		public int cost(ArrayList<Item> ingredients) {
			return 3;
		}

		@Override
		public Item brew(ArrayList<Item> ingredients) {
			Item result = sampleOutput(ingredients);
			MissileWeapon m = (MissileWeapon) ingredients.get(0);
			if (!m.levelKnown){
				result.quantity(metalQuantity(m));
			}

			m.quantity(0);
			Buff.affect(Dungeon.hero, MissileWeapon.UpgradedSetTracker.class).levelThresholds.put(m.setID, Integer.MAX_VALUE);

			return result;
		}

		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			MissileWeapon m = (MissileWeapon) ingredients.get(0);

			if (m.levelKnown){
				return new LiquidMetal().quantity(metalQuantity(m));
			} else {
				return new LiquidMetal();
			}
		}

		private int metalQuantity(MissileWeapon m){
			float quantityPerWeapon = 5*(m.tier+1);
			if (m.defaultQuantity() != 3){
				quantityPerWeapon = 3f / m.defaultQuantity();
			}
			quantityPerWeapon *= Math.pow(1.35f, Math.min(5, m.level()));

			float quantity = m.quantity()-1;
			quantity += 0.25f + 0.0075f*m.durabilityLeft();

			return Math.round(quantity * quantityPerWeapon);
		}
	}

}
