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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
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
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
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

	private Thread seedThread;

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

		StyledButton btnSeedfinder = new StyledButton(GREY_TR, Messages.get(this, "seedfinder_button")) {
			@Override
			protected void onClick() {
				SeedFindScene.this.addToFront(
						new WndTextInput(
								Messages.get(SeedFindScene.class, "seedfinder_title"),
								Messages.get(SeedFindScene.class, "seedfinder_info_text"),
								SPDSettings.seeditemsText(), 1000, true,
								Messages.get(SeedFindScene.class, "seedfinder_button_yes"),
								Messages.get(SeedFindScene.class, "seedfinder_button_no")) {
							@Override
							public void onSelect(boolean positive, String itemsPrompt) {
								if (positive) {
									SPDSettings.seeditemsText(itemsPrompt);

									final Thread[] searchThread = new Thread[1];
									final WndOptions[] progressWnd = new WndOptions[1];

									//run in new thread
									searchThread[0] = new Thread(() -> {
										final String foundSeed = new SeedFinder().find_seed(itemsPrompt);

										//process results in rendering thread
										Gdx.app.postRunnable(() -> {
											if (progressWnd[0].parent == null) {
												//search cancelled
												return;
											}
											progressWnd[0].hide();

                                            if (foundSeed == null) {
                                                SeedFindScene.this.addToFront(new WndMessage("Error: seed not found."));
                                                return;
                                            } else if (foundSeed.startsWith("error")) {
												SeedFindScene.this.addToFront(new WndMessage(foundSeed));
                                                return;
                                            }

											//copy seed to clipboard on success
											Clipboard clipboard = Gdx.app.getClipboard();
											clipboard.setContents(foundSeed);

											long seed = DungeonSeed.convertFromText(foundSeed);

											//show log
											SeedFinder.SeedfinderLogResult result = new SeedFinder().logSeedItemsSeededRun(seed);

											ShatteredPixelDungeon.scene().addToFront(
													new WndSeedfinderLog(Icons.get(Icons.BACKPACK),
															"Found seed " + DungeonSeed.convertToCode(Dungeon.seed),
															result));
										});
									});

									progressWnd[0] = new WndOptions(
											Icons.get(Icons.MAGNIFY),
											Messages.get(SeedFindScene.class, "seedfinder_searching_title"),
											Messages.get(SeedFindScene.class, "seedfinder_searching_text"),
											Messages.get(SeedFindScene.class, "seedfinder_searching_cancel") ) {
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
		btnSeedfinder.icon(Icons.get(Icons.MAGNIFY));
		add(btnSeedfinder);

		StyledButton btnScout = new StyledButton(GREY_TR, Messages.get(this, "scout_seed_button")) {
			@Override
			protected void onClick() {
				SeedFindScene.this.addToFront( new WndTextInput(
						Messages.get(SeedFindScene.class, "scout_custom_seed_title"),
						Messages.get(SeedFindScene.class, "scout_info_text"),
						SPDSettings.seedinputText(), 20, false,
						Messages.get(SeedFindScene.class, "scout_button_yes"),
						Messages.get(SeedFindScene.class, "scout_button_no")) {
					@Override
					public void onSelect(boolean positive, String text) {
						if (positive && text != null && !text.isEmpty()) {
							SPDSettings.seedinputText(text);

							text = DungeonSeed.formatText(text);
							long seed = DungeonSeed.convertFromText(text);

							SeedFinder.SeedfinderLogResult result = SeedFinder.scoutDungeon(text).toLogResult();

							ShatteredPixelDungeon.scene().addToFront(
									new WndSeedfinderLog(Icons.get(Icons.BACKPACK),
											"Results for Seed: " + text,
											result));
						} else {
							SPDSettings.seedinputText("");
						}
					}
				});
			}
		};
		btnScout.icon(Icons.get(Icons.ENTER));
		add(btnScout);

		StyledButton btnScoutDaily = new StyledButton(GREY_TR, Messages.get(this, "scout_daily")) /*{
			@Override
			protected void onClick() {
				SeedfinderLogResult result = new SeedFinder().logSeedItemsDailyRunRun(0);

				long DAY = 1000 * 60 * 60 * 24;
				long currentDay = (long) Math.floor(Game.realTime / DAY) + SeedFinder.Options.DailyOffset;
				SPDSettings.lastDaily(DAY * currentDay);
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
				format.setTimeZone(TimeZone.getTimeZone("UTC"));
				String date = format.format(new Date(SPDSettings.lastDaily()));

				ShatteredPixelDungeon.scene().addToFront(
						new WndSeedfinderLog(Icons.get(Icons.BACKPACK),
								"Items for daily run " + date,
								result));
			}
		}*/;
		btnScoutDaily.icon(Icons.get(Icons.ENTER));
		add(btnScoutDaily);
		Dungeon.daily = Dungeon.dailyReplay = false;

		StyledButton btnOptions = new StyledButton(GREY_TR, "Options") {
			@Override
			protected void onClick() {
				SeedFindScene.this.addToFront(new WndSeedfinderMenu());
			}
		};
		btnOptions.icon(Icons.get(Icons.ENTER));
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
		/*ShatteredPixelDungeon.scene().addToFront(new WndTextInput(Messages.get(this, "title"), Messages.get(this, "body"), Messages.get(this, "initial_value")+"\n", 1000, true, Messages.get(this, "find"), Messages.get(HeroSelectScene.class, "custom_seed_clear")) {
			@Override
			public void onSelect(boolean positive, String text) {
				int floor = 31;
				boolean floorOption = false;
				text = text.toLowerCase(); //대문자를 소문자로 변경
				text = text.replaceAll(" ", ""); //공백 제거
				String up_to_floor;
				if (Messages.lang() == Languages.KOREAN) {
					up_to_floor = "층까지";
				} else {
					up_to_floor = "floor end";
				}
				String strFloor;
				if (Messages.lang() == Languages.KOREAN) {
					strFloor = "층";
				} else {
					strFloor = "floor";
				}
				if (text.contains(up_to_floor)) {
					floorOption = true;
					String fl = text.split(strFloor)[0].trim();
					try {
						floor = Integer.parseInt(fl);
					} catch (
							NumberFormatException e) {
					}
				}
				if (positive && text != "") {
					String[] itemList = floorOption ? Arrays.copyOfRange(text.split("\n"), 1, text.split("\n").length) : text.split("\n");

					Component content = list.content();
					content.clear();

					CreditsBlock alertMsg = new CreditsBlock(true,
							Window.TITLE_COLOR,
							Messages.get(SeedFinder.class, "seedfind_warning"));
					alertMsg.setRect((Camera.main.width - colWidth)/2f, (Camera.main.height-12)/2f, colWidth, 0);
					content.add(alertMsg);

					if(!Objects.isNull(seedThread) && seedThread.isAlive()){
						SeedFinder.stopFindSeed();
						seedThread.interrupt();
					}
					int finalFloor = floor;
					String finalText = text;
					seedThread = new Thread(new Runnable() {
						@Override
						public void run() {
							String resultContent;
							try {
								resultContent = new SeedFinder().findSeed(itemList, finalFloor);
							} catch (NullPointerException e) {
								//스택 트레이스를 문자열로 받음
								StringWriter sw = new StringWriter();
								PrintWriter pw = new PrintWriter(sw);
								e.printStackTrace(pw);
								String stackTrace = sw.toString();
								//결과 문자열을 에러 메시지로 변경
								resultContent = Messages.get(SeedFinder.class, "error", finalText, stackTrace);
							}
							String finalResultContent = resultContent;
							Gdx.app.postRunnable(new Runnable() {
								@Override
								public void run() {
									if(!(ShatteredPixelDungeon.scene() instanceof SeedFindScene)) return;
									CreditsBlock txt = new CreditsBlock(true,
											Window.TITLE_COLOR,
											finalResultContent);
									txt.setRect((Camera.main.width - colWidth)/2f, 12, colWidth, 0);
									content.add(txt);
									content.remove(alertMsg);
									content.setSize( fullWidth, txt.bottom()+10 );
								}
							});
						}
					});
					seedThread.start();

					list.setRect( 0, 0, w, h );
					list.scrollTo(0, 0);

				} else {
					SPDSettings.customSeed("");
					ShatteredPixelDungeon.switchNoFade(HeroSelectScene.class);
				}
			}
		});*/

		addToBack( BG );

		//fadeIn();
	}

	@Override
	protected void onBackPressed() {
		ShatteredPixelDungeon.switchScene(HeroSelectScene.class);
	}

	private void addLine(float y, Group content) {
		ColorBlock line = new ColorBlock(Camera.main.width, 1, 0xFF333333);
		line.y = y;
		content.add(line);
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