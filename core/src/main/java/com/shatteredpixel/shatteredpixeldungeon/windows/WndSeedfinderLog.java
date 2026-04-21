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
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;

import com.shatteredpixel.shatteredpixeldungeon.SeedFinder.SeedfinderLogResult;

import java.util.ArrayList;

public class WndSeedfinderLog extends WndTabbedCategories {

	protected static final int WIDTH_MIN = 120;
	protected static final int WIDTH_MAX = 280;
	protected static final int TTL_HEIGHT = 11;
	protected static final int GAP = 2;
	private final int fontSize = SPDSettings.seedfinderFontSize();

	private ArrayList<RenderedTextBlock> itemsBlocks = new ArrayList<>();
	private ArrayList<RenderedTextBlock> roomsBlocks = new ArrayList<>();

	private enum Category {ITEMS, ROOMS}
	private Category selectedCategory = Category.ITEMS;
	private int selectedIndex = 0;

	public WndSeedfinderLog(Image icon, String title, SeedfinderLogResult result) {

		super();

		int width = WIDTH_MIN;

		PointerArea blocker = new PointerArea(0, 0, PixelScene.uiCamera.width, PixelScene.uiCamera.height);
		//do not go back on screen click
		blocker.camera = PixelScene.uiCamera;
		add(blocker);

		IconTitle titlebar = new IconTitle(icon, title);
		titlebar.setRect(0, 0, width - TTL_HEIGHT, TTL_HEIGHT);
		add(titlebar);

		IconButton btnClose = new IconButton(Icons.CLOSE.get()) {
			@Override
			protected void onClick() {
				WndSeedfinderLog.this.hide();
			}
		};
		btnClose.setRect(titlebar.right(), 0, TTL_HEIGHT, TTL_HEIGHT);
		add( btnClose );

		RenderedTextBlock largest = null;
		for (int i = 0; i < result.main.length; i++) {
			RenderedTextBlock textblock = PixelScene.renderTextBlock(fontSize);
			textblock.text(result.main[i], width);
			textblock.setPos(titlebar.left(), titlebar.bottom() + 2 * GAP);
			add(textblock);
			itemsBlocks.add(textblock);

			RenderedTextBlock textblock_room = PixelScene.renderTextBlock(fontSize);
			textblock_room.text(result.rooms[i], width);
			textblock_room.setPos(titlebar.left(), titlebar.bottom() + 2 * GAP);
			add(textblock_room);
			roomsBlocks.add(textblock_room);

			if (largest == null || textblock.height() > largest.height()) {
				largest = textblock;
			}
			if (largest == null || textblock_room.height() > largest.height()) {
				largest = textblock_room;
			}

			final int finalI = i;
			add(new LabeledTab(numToNumeral(finalI + 1)) {
				@Override
				protected void select(boolean value) {
					super.select(value);
					if(value) {
						selectedIndex = finalI;
					}
					update_text_visibility();
				}
			});
		}

		add_category(new LabeledTab("items") {
			@Override
			protected void select(boolean value) {
				super.select(value);
				if(value) {
					selectedCategory = Category.ITEMS;
				}
				update_text_visibility();
			}
		});

		add_category(new LabeledTab("rooms") {
			@Override
			protected void select(boolean value) {
				super.select(value);
				if(value) {
					selectedCategory = Category.ROOMS;
				}
				update_text_visibility();
			}
		});

		while (PixelScene.landscape()
				&& largest.bottom() > (PixelScene.MIN_HEIGHT_L - 20)
				&& width < WIDTH_MAX) {
			width += 20;
			titlebar.setRect(0, 0, width - TTL_HEIGHT, TTL_HEIGHT);
			btnClose.setRect(titlebar.right(), 0, TTL_HEIGHT, TTL_HEIGHT);

			largest = null;
			for (RenderedTextBlock text : itemsBlocks) {
				text.setPos(titlebar.left(), titlebar.bottom() + GAP);
				text.maxWidth(width);
				if (largest == null || text.height() > largest.height()) {
					largest = text;
				}
			}
		}

		bringToFront(titlebar);

		resize(width, (int) largest.bottom() + GAP);

		layoutTabs();
		select(0);

	}

	private void update_text_visibility() {
		for (int i = 0; i < itemsBlocks.size(); i++) {
			itemsBlocks.get(i).visible = false;
			roomsBlocks.get(i).visible = false;
		}

		switch(selectedCategory) {
			case ITEMS:
				itemsBlocks.get(selectedIndex).visible = true;
				break;
			case ROOMS:
				roomsBlocks.get(selectedIndex).visible = true;
				break;
		}
	}

	private String numToNumeral(int num) {
		return Integer.toString(num);

	}
}
