/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Clipboard;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.SeedFinder;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.TitleBackground;
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSeedfinderLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSeedfinderMenu;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Group;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.RectF;

public class SeedFindScene extends PixelScene {

	@Override
	public void create() {
		super.create();

		int w = Camera.main.width;
		int h = Camera.main.height;

		RectF insets = getCommonInsets();

		TitleBackground BG = new TitleBackground(w, h);
		//background added later

		w -= insets.left + insets.right;
		h -= insets.top + insets.bottom;

		IconTitle title = new IconTitle(Icons.CHANGES.get(), Messages.get(this, "title"));
		title.setSize(200, 0);
		title.setPos(
				insets.left + (w - title.reqWidth()) / 2f,
				insets.top + (20 - title.height()) / 2f
		);
		align(title);
		add(title);

		ExitButton btnExit = new ExitButton() {
			@Override
			protected void onClick() {
				ShatteredPixelDungeon.switchNoFade( HeroSelectScene.class );
			}
		};
		btnExit.setPos( insets.left + w - btnExit.width(), insets.top );
		add( btnExit );

		final Chrome.Type GREY_TR = Chrome.Type.GREY_BUTTON_TR;

		StyledButton btnSeedfinder = new StyledButton(GREY_TR, Messages.get(this, "find_seed_button")) {
			@Override
			protected void onClick() {
				SeedFindScene.this.addToFront(
						new WndTextInput(
								Messages.get(SeedFindScene.class, "seedfinder_title"),
								Messages.get(SeedFindScene.class, "seedfinder_info"),
								SPDSettings.seeditemsText(), 1000, true,
								Messages.get(SeedFindScene.class, "seedfinder_yes"),
								Messages.get(SeedFindScene.class, "seedfinder_no")) {
							@Override
							public void onSelect(boolean positive, String itemsPrompt) {
								if (positive) {
									SPDSettings.seeditemsText(itemsPrompt);

									final Thread[] searchThread = new Thread[1];
									final WndOptions[] progressWnd = new WndOptions[1];

									//run in new thread
									searchThread[0] = new Thread(() -> {
										final SeedFinder.SeedLog foundSeed = SeedFinder.findSeed();

										//process results in rendering thread
										Gdx.app.postRunnable(() -> {
											if (progressWnd[0].parent == null) {
												//search cancelled
												return;
											}
											progressWnd[0].hide();

                                            if (foundSeed == null) {
                                                return;
                                            }

											//copy seed to clipboard on success
											Clipboard clipboard = Gdx.app.getClipboard();
											clipboard.setContents(foundSeed.seed);

											//show log
											SeedFinder.SeedfinderLogResult result = foundSeed.toLogResult();

											ShatteredPixelDungeon.scene().addToFront(
													new WndSeedfinderLog(Icons.get(Icons.BACKPACK),
															Messages.get(SeedFindScene.class, "result_title"),
															result));
										});
									});

									progressWnd[0] = new WndOptions(
											Icons.get(Icons.MAGNIFY),
											Messages.get(SeedFindScene.class, "searching_title"),
											Messages.get(SeedFindScene.class, "searching_text"),
											Messages.get(SeedFindScene.class, "searching_cancel") ) {
										@Override
										protected void onSelect(int index) {
											if (index == 0) {
                                                if (searchThread[0] != null && searchThread[0].isAlive()) {
                                                    searchThread[0].interrupt();
                                                }
                                                hide();
											}
										}

										@Override
										public void onBackPressed() {
											// do nothing to prevent accidental cancellation
										}
									};

									SeedFindScene.this.addToFront(progressWnd[0]);
									searchThread[0].start();
								} else {
									SPDSettings.seeditemsText("");
								}
							}
						});
			}
		};
		btnSeedfinder.icon(Icons.MAGNIFY_GRAY.get());
		add(btnSeedfinder);

		StyledButton btnScout = new StyledButton(GREY_TR, Messages.get(this, "scout_seed_button")) {
			@Override
			protected void onClick() {
				SeedFindScene.this.addToFront( new WndTextInput(
						Messages.get(SeedFindScene.class, "scout_title"),
						Messages.get(SeedFindScene.class, "scout_info"),
						SPDSettings.customSeed(), 20, false,
						Messages.get(SeedFindScene.class, "scout_yes"),
						Messages.get(SeedFindScene.class, "scout_no")) {
					@Override
					public void onSelect(boolean positive, String text) {
						if (positive && text != null && !text.isEmpty()) {
							text = DungeonSeed.formatText(text);

							SeedFinder.SeedLog result = SeedFinder.scoutSeed(text);

							ShatteredPixelDungeon.scene().addToFront(
									new WndSeedfinderLog(
											Icons.get(Icons.BACKPACK),
											Messages.get(SeedFindScene.class, "result_title"),
											result.toLogResult()) );
						} else {
							SPDSettings.seedinputText("");
						}
					}
				});
			}
		};
		btnScout.icon(Icons.JOURNAL_GRAY.get());
		add(btnScout);

		StyledButton btnScoutDaily = new StyledButton(GREY_TR, Messages.get(this, "scout_daily_button")) {
			@Override
			protected void onClick() {
				SeedFinder.SeedLog result = SeedFinder.scoutDaily();

				ShatteredPixelDungeon.scene().addToFront(
						new WndSeedfinderLog(
								Icons.get(Icons.BACKPACK),
								Messages.get(SeedFindScene.class, "result_title"),
								result.toLogResult()));
			}
		};
		btnScoutDaily.icon(Icons.CALENDAR.get());
		add(btnScoutDaily);

		StyledButton btnOptions = new StyledButton(GREY_TR, Messages.get(SeedFindScene.class, "options_button")) {
			@Override
			protected void onClick() {
				SeedFindScene.this.addToFront(new WndSeedfinderMenu());
			}
		};
		btnOptions.icon(Icons.PREFS.get());
		add(btnOptions);

		float topRegion = Math.max(title.height() - 6, h*0.45f);
		final int BTN_HEIGHT = 20;
		int GAP = (int)(h - topRegion - (landscape() ? 3 : 4)*BTN_HEIGHT)/3;
		GAP /= landscape() ? 3 : 5;
		GAP = Math.max(GAP, 2);

		float buttonAreaWidth = landscape() ? PixelScene.MIN_WIDTH_L-6 : PixelScene.MIN_WIDTH_P-2;
		float btnAreaLeft = insets.left + (w - buttonAreaWidth) / 2f;

		btnSeedfinder.setRect(btnAreaLeft, insets.top + topRegion + GAP, buttonAreaWidth, BTN_HEIGHT);
		align(btnSeedfinder);
		btnScout.setRect(btnAreaLeft, btnSeedfinder.bottom() + GAP, buttonAreaWidth, BTN_HEIGHT);
		btnScoutDaily.setRect(btnAreaLeft, btnScout.bottom() + GAP, buttonAreaWidth, BTN_HEIGHT);
		btnOptions.setRect(btnAreaLeft, btnScoutDaily.bottom() + GAP, buttonAreaWidth, BTN_HEIGHT);

		addToBack( BG );

		//fadeIn();
	}

	@Override
	protected void onBackPressed() {
		ShatteredPixelDungeon.switchScene(HeroSelectScene.class);
	}

	public static class CreditsBlock extends Component {

		boolean large;

		RenderedTextBlock body;

		public CreditsBlock(boolean large, int highlight, String body) {
			super();

			this.large = large;

			this.body = PixelScene.renderTextBlock(body, 6);
			if (highlight != -1)
				this.body.setHightlighting(true, highlight);
			if (large)
				this.body.align(RenderedTextBlock.CENTER_ALIGN);
			add(this.body);
		}

		@Override
		protected void layout() {
			super.layout();

			float topY = top();

			if (large){
				body.maxWidth((int)width());
				body.setPos( x + (width() - body.width())/2f, topY);
			} else {
				topY += 1;
				body.maxWidth((int)width());
				body.setPos( x, topY);
			}

			topY += body.height();

			height = Math.max(height, topY - top());
		}
	}
}