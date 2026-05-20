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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Teleporter;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.utils.Reflection;

public class WndJournalItem extends WndTitledMessage {

	public WndJournalItem(Image icon, String title, String message ) {
		super( icon, title, message);

		PointerArea blocker = new PointerArea( 0, 0, PixelScene.uiCamera.width, PixelScene.uiCamera.height ) {
			@Override
			protected void onClick( PointerEvent event ) {
				onBackPressed();
			}
		};
		blocker.camera = PixelScene.uiCamera;
		add(blocker);

	}

	public static final int BTN_HEIGHT = 16;

	public WndJournalItem(Image icon, String title, String message, Class<Item> itemClass) {
		this(icon, title, message);

		RedButton btnGet = new RedButton("GET") {
			@Override
			protected void onClick() {
				Item item = null;
				try {
					if (Key.class.isAssignableFrom(itemClass)) {
						Constructor keyConstructor = ClassReflection.getConstructor(itemClass, int.class, int.class);
						item = (Item) keyConstructor.newInstance(Dungeon.depth, Dungeon.branch);
					} else {
						item = Reflection.newInstance(itemClass);
					}
				} catch (Exception e) {
					Game.reportException(e);
				}

                if (item != null) {
                    if (item.identify(false).doPickUp(Dungeon.hero)) {
						GLog.i(Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", item.name())));
                        Dungeon.hero.spend(-item.pickupDelay());
                    } else {
						GLog.n(Messages.capitalize(Messages.get(Dungeon.hero, "you_cant_have", item.name())));
                        Dungeon.level.drop(item, Dungeon.hero.pos).sprite.drop();
                    }
                } else {
					GLog.n(Messages.capitalize(Messages.get(Teleporter.class, "warn_item_create",
							Messages.get(itemClass, "name"))));
				}
			}
		};
		add(btnGet);

		btnGet.setRect(0, height + GAP, btnGet.reqWidth()+ 2*GAP, BTN_HEIGHT);
		resize(width, (int) (height + GAP + btnGet.height()));
	}

}
