/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;

import com.shatteredpixel.shatteredpixeldungeon.SeedFinder.SeedfinderLogResult;

public class WndSeedfinderLog extends WndTabbed {

	protected static final int WIDTH_MIN = 120;
	protected static final int WIDTH_MAX = 280;
	protected static final int TTL_HEIGHT = 11;
	protected static final int BTN_HEIGHT = 16;

	protected static final int GAP = 2;
	private final int fontSize = SPDSettings.seedfinderFontSize();

	private RenderedTextBlock text;
	private ScrollPane scroll;
	private int selectedCategory = 0;
	private static final int ITEMS = 0;
	private static final int ROOMS = 1;
	private int selectedIndex = 0;

	private final SeedfinderLogResult result;

	public WndSeedfinderLog(Image icon, String title, SeedfinderLogResult result) {
		super();

		this.result = result;

		int width = WIDTH_MIN;
		int height = PixelScene.uiCamera.height - 20 - tabHeight();

		PointerArea blocker = new PointerArea(0, 0, PixelScene.uiCamera.width, PixelScene.uiCamera.height);
		//do not go back on screen click
		blocker.camera = PixelScene.uiCamera;
		add(blocker);

		IconTitle titlebar = new IconTitle(icon, title);
		titlebar.setRect(0, 0, width - TTL_HEIGHT, 0);
		add(titlebar);

		IconButton btnClose = new IconButton(Icons.CLOSE.get()) {
			@Override
			protected void onClick() {
				WndSeedfinderLog.this.hide();
			}
		};
		btnClose.setRect(titlebar.right(), 0, TTL_HEIGHT, TTL_HEIGHT);
		add( btnClose );

		RedButton btnItems = new RedButton("Items") {
			@Override
			protected void onClick() {
				selectedCategory = ITEMS;
				updateText();
			}
		};
		btnItems.setRect(0, titlebar.bottom() + GAP, (width-GAP)/2f, BTN_HEIGHT);
		add( btnItems );

		RedButton btnRooms = new RedButton("Rooms") {
			@Override
			protected void onClick() {
				selectedCategory = ROOMS;
				updateText();
			}
		};
		btnRooms.setRect((width+GAP)/2f, titlebar.bottom() + GAP, (width-GAP)/2f, BTN_HEIGHT);
		add( btnRooms );

		text = PixelScene.renderTextBlock( fontSize );
		text.text(result.main[selectedIndex]);
		text.maxWidth( width );
		text.setPos( titlebar.left(), btnItems.bottom() + 2*GAP );

		while (PixelScene.landscape()
				&& text.bottom() > (PixelScene.MIN_HEIGHT_L - 10)
				&& width < WIDTH_MAX) {
			width += 20;
			titlebar.setRect(0, 0, width - TTL_HEIGHT, 0);
			btnClose.setRect(titlebar.right(), 0, TTL_HEIGHT, TTL_HEIGHT);
			btnItems.setRect(0, titlebar.bottom() + GAP, (width-GAP)/2f, BTN_HEIGHT);
			btnRooms.setRect((width+GAP)/2f, titlebar.bottom() + GAP, (width-GAP)/2f, BTN_HEIGHT);

			text.setPos( titlebar.left(), btnItems.bottom() + 2*GAP );
			text.maxWidth(width);
		}

		scroll = new ScrollPane(text);
		add( scroll );

		bringToFront(titlebar);
		resize( width, height );
		scroll.setRect(0, btnItems.bottom() + 2*GAP,
				width, height - (btnItems.bottom() + 2*GAP));

		for (int i = 0; i < this.result.main.length; i++) {
			final int finalI = i;
			add(new LabeledTab(Integer.toString(i)) {
				@Override
				protected void select(boolean value) {
					super.select(value);
					if(value) {
						selectedIndex = finalI;
					}
					updateText();
				}
			});
		}

		layoutTabs();
		select(0);
	}

	private void updateText() {
        if (selectedCategory == ITEMS) {
            text.text(result.main[selectedIndex]);
        } else if (selectedCategory == ROOMS) {
            text.text(result.rooms[selectedIndex]);
        }
		scroll.scrollTo(0, 0);
	}
}
