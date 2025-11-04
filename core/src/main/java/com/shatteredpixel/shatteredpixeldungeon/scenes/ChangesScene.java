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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.TitleBackground;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.ChangeInfo;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.WndChanges;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.WndChangesTabbed;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_1_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_2_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_3_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_4_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_5_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_6_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_7_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_8_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v0_9_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v1_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v2_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.v3_X_Changes;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.Scene;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.RectF;

import java.util.ArrayList;

public class ChangesScene extends PixelScene {

	public static enum MOD_VERS {
		SPD,
		RPD,
		EPD;
	}
	public static enum CHANGE_ID {
		V3_X(MOD_VERS.SPD),
		V2_X(MOD_VERS.SPD),
		V1_X(MOD_VERS.SPD),
		V0_9_X(MOD_VERS.SPD),
		V0_8_X(MOD_VERS.SPD),
		V0_7_X(MOD_VERS.SPD),
		V0_6_X(MOD_VERS.SPD),
		V_OLD(MOD_VERS.SPD),
		RPD_MSG(MOD_VERS.RPD),
		EPD_V0_X(MOD_VERS.EPD);

		public final MOD_VERS mod;
		CHANGE_ID(MOD_VERS mod){
			this.mod = mod;
		}
	}

	public static final CHANGE_ID LATEST = CHANGE_ID.EPD_V0_X;
	public static CHANGE_ID changesSelected = LATEST;
	private NinePatch rightPanel;
	private ScrollPane rightScroll;
	private IconTitle changeTitle;

	private RenderedTextBlock changeBody;

	@Override
	public void create() {
		super.create();

		Music.INSTANCE.playTracks(
				new String[]{Assets.Music.THEME_1, Assets.Music.THEME_2},
				new float[]{1, 1},
				false);

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

		ExitButton btnExit = new ExitButton();
		btnExit.setPos( insets.left + w - btnExit.width(), insets.top );
		add( btnExit );

		final int modBtnSize = 19;

		StyledButton spd = new StyledButton(
				(changesSelected.mod==MOD_VERS.SPD) ? Chrome.Type.RED_BUTTON : Chrome.Type.GREY_BUTTON,
				"", 8) {
			@Override
			protected void onClick() {
				super.onClick();
				if (changesSelected != CHANGE_ID.V3_X) {
					changesSelected = CHANGE_ID.V3_X;
					ShatteredPixelDungeon.seamlessResetScene();
				}
			}
		};
		spd.icon(Icons.SHPX.get());
		spd.setRect(insets.left + w/2 + (modBtnSize+1)*0.5f, title.bottom(), modBtnSize, modBtnSize);
		add(spd);

		StyledButton rpd = new StyledButton(
				(changesSelected.mod==MOD_VERS.RPD) ? Chrome.Type.RED_BUTTON : Chrome.Type.GREY_BUTTON,
				"", 8) {
			@Override
			protected void onClick() {
				super.onClick();
				if (changesSelected != CHANGE_ID.RPD_MSG) {
					changesSelected = CHANGE_ID.RPD_MSG;
					ShatteredPixelDungeon.seamlessResetScene();
				}
			}
		};
		rpd.icon(Icons.ARRANGED.get());
		rpd.setRect(insets.left + w/2 - (modBtnSize+1)*0.5f, title.bottom(), modBtnSize, modBtnSize);
		add(rpd);

		StyledButton epd = new StyledButton(
				(changesSelected.mod==MOD_VERS.EPD) ? Chrome.Type.RED_BUTTON : Chrome.Type.GREY_BUTTON,
				"", 8) {
			@Override
			protected void onClick() {
				super.onClick();
				if (changesSelected != CHANGE_ID.EPD_V0_X) {
					changesSelected = CHANGE_ID.EPD_V0_X;
					ShatteredPixelDungeon.seamlessResetScene();
				}
			}
		};
		epd.icon(Icons.ESTRANGED.get());
		epd.setRect(insets.left + w/2 - (modBtnSize+1)*1.5f, title.bottom(), modBtnSize, modBtnSize);
		add(epd);

		NinePatch panel = Chrome.get(Chrome.Type.TOAST);

		int pw = 135 + panel.marginLeft() + panel.marginRight() - 2;
		int ph = h - 36 - (modBtnSize+1) + 5;

		if (h >= PixelScene.MIN_HEIGHT_FULL && w >= 300) {
			panel.size( pw, ph );
			panel.x = insets.left + (w - pw) / 2f - pw/2 - 1;
			panel.y = insets.top + 20 + (modBtnSize+1);

			rightPanel = Chrome.get(Chrome.Type.TOAST);
			rightPanel.size( pw, ph );
			rightPanel.x = (w - pw) / 2f + pw/2 + 1;
			rightPanel.y = 20 + (modBtnSize+1);
			add(rightPanel);

			rightScroll = new ScrollPane(new Component());
			add(rightScroll);
			rightScroll.setRect(
					rightPanel.x + rightPanel.marginLeft(),
					rightPanel.y + rightPanel.marginTop()-1,
					rightPanel.innerWidth() + 2,
					rightPanel.innerHeight() + 2);
			rightScroll.scrollTo(0, 0);

			changeTitle = new IconTitle(Icons.get(Icons.CHANGES), Messages.get(this, "right_title"));
			changeTitle.setPos(0, 1);
			changeTitle.setSize(pw, 20);
			rightScroll.content().add(changeTitle);

			String body = Messages.get(this, "right_body");

			changeBody = PixelScene.renderTextBlock(body, 6);
			changeBody.maxWidth(pw - panel.marginHor());
			changeBody.setPos(0, changeTitle.bottom()+2);
			rightScroll.content().add(changeBody);

		} else {
			panel.size( pw, ph );
			panel.x = insets.left + (w - pw) / 2f;
			panel.y = insets.top + 20 + (modBtnSize+1);
		}
		align( panel );
		add( panel );
		
		final ArrayList<ChangeInfo> changeInfos = new ArrayList<>();

		if (Messages.lang() != Languages.ENGLISH){
			ChangeInfo langWarn = new ChangeInfo("", true, Messages.get(this, "lang_warn"));
			langWarn.hardlight(CharSprite.WARNING);
			changeInfos.add(langWarn);
		}
		
		switch (changesSelected){
			case V3_X: default:
				v3_X_Changes.addAllChanges(changeInfos);
				break;
			case V2_X:
				v2_X_Changes.addAllChanges(changeInfos);
				break;
			case V1_X:
				v1_X_Changes.addAllChanges(changeInfos);
				break;
			case V0_9_X:
				v0_9_X_Changes.addAllChanges(changeInfos);
				break;
			case V0_8_X:
				v0_8_X_Changes.addAllChanges(changeInfos);
				break;
			case V0_7_X:
				v0_7_X_Changes.addAllChanges(changeInfos);
				break;
			case V0_6_X:
				v0_6_X_Changes.addAllChanges(changeInfos);
				break;
			case V_OLD:
				v0_5_X_Changes.addAllChanges(changeInfos);
				v0_4_X_Changes.addAllChanges(changeInfos);
				v0_3_X_Changes.addAllChanges(changeInfos);
				v0_2_X_Changes.addAllChanges(changeInfos);
				v0_1_X_Changes.addAllChanges(changeInfos);
				break;
			case RPD_MSG:
				RPD_Changes.addAllChanges(changeInfos);
				break;
			case EPD_V0_X:
				EPD_v0_X_Changes.addAllChanges(changeInfos);
		}

		ScrollPane list = new ScrollPane( new Component() ){

			@Override
			public void onClick(float x, float y) {
				for (ChangeInfo info : changeInfos){
					if (info.onClick( x, y )){
						return;
					}
				}
			}

		};
		add( list );

		Component content = list.content();
		content.clear();

		float posY = 0;
		float nextPosY = 0;
		boolean second = false;
		for (ChangeInfo info : changeInfos){
			if (info.major) {
				posY = nextPosY;
				second = false;
				info.setRect(0, posY, panel.innerWidth(), 0);
				content.add(info);
				posY = nextPosY = info.bottom();
			} else {
				if (!second){
					second = true;
					info.setRect(0, posY, panel.innerWidth()/2f, 0);
					content.add(info);
					nextPosY = info.bottom();
				} else {
					second = false;
					info.setRect(panel.innerWidth()/2f, posY, panel.innerWidth()/2f, 0);
					content.add(info);
					nextPosY = Math.max(info.bottom(), nextPosY);
					posY = nextPosY;
				}
			}
		}

		content.setSize( panel.innerWidth(), (int)Math.ceil(posY) );

		list.setRect(
				panel.x + panel.marginLeft(),
				panel.y + panel.marginTop() - 1,
				panel.innerWidth() + 2,
				panel.innerHeight() + 2);
		list.scrollTo(0, 0);

		if (changesSelected.mod == MOD_VERS.SPD) {


			StyledButton btn3_X = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "3.X", 8) {
				@Override
				protected void onClick() {
					super.onClick();
					if (changesSelected != CHANGE_ID.V3_X) {
						changesSelected = CHANGE_ID.V3_X;
						ShatteredPixelDungeon.seamlessResetScene();
					}
				}
			};
			if (changesSelected != CHANGE_ID.V3_X) btn3_X.textColor(0xBBBBBB);
			btn3_X.setRect(list.left() - 4f, list.bottom(), 19, changesSelected == CHANGE_ID.V3_X ? 19 : 15);
			addToBack(btn3_X);

			StyledButton btn2_X = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "2.X", 8) {
				@Override
				protected void onClick() {
					super.onClick();
					if (changesSelected != CHANGE_ID.V2_X) {
						changesSelected = CHANGE_ID.V2_X;
						ShatteredPixelDungeon.seamlessResetScene();
					}
				}
			};
			if (changesSelected != CHANGE_ID.V2_X) btn2_X.textColor(0xBBBBBB);
			btn2_X.setRect(btn3_X.right() - 2, list.bottom(), 19, changesSelected == CHANGE_ID.V2_X ? 19 : 15);
			addToBack(btn2_X);

			StyledButton btn1_X = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "1.X", 8) {
				@Override
				protected void onClick() {
					super.onClick();
					if (changesSelected != CHANGE_ID.V1_X) {
						changesSelected = CHANGE_ID.V1_X;
						ShatteredPixelDungeon.seamlessResetScene();
					}
				}
			};
			if (changesSelected != CHANGE_ID.V1_X) btn1_X.textColor(0xBBBBBB);
			btn1_X.setRect(btn2_X.right() - 2, list.bottom(), 19, changesSelected == CHANGE_ID.V1_X ? 19 : 15);
			addToBack(btn1_X);

			StyledButton btn0_9 = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "0.9", 8) {
				@Override
				protected void onClick() {
					super.onClick();
					if (changesSelected != CHANGE_ID.V0_9_X) {
						changesSelected = CHANGE_ID.V0_9_X;
						ShatteredPixelDungeon.seamlessResetScene();
					}
				}
			};
			if (changesSelected != CHANGE_ID.V0_9_X) btn0_9.textColor(0xBBBBBB);
			btn0_9.setRect(btn1_X.right() - 2, list.bottom(), 19, changesSelected == CHANGE_ID.V0_9_X ? 19 : 15);
			addToBack(btn0_9);

			StyledButton btn0_8 = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "0.8", 8) {
				@Override
				protected void onClick() {
					super.onClick();
					if (changesSelected != CHANGE_ID.V0_8_X) {
						changesSelected = CHANGE_ID.V0_8_X;
						ShatteredPixelDungeon.seamlessResetScene();
					}
				}
			};
			if (changesSelected != CHANGE_ID.V0_8_X) btn0_8.textColor(0xBBBBBB);
			btn0_8.setRect(btn0_9.right() - 2, list.bottom(), 19, changesSelected == CHANGE_ID.V0_8_X ? 19 : 15);
			addToBack(btn0_8);

			StyledButton btn0_7 = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "0.7", 8) {
				@Override
				protected void onClick() {
					super.onClick();
					if (changesSelected != CHANGE_ID.V0_7_X) {
						changesSelected = CHANGE_ID.V0_7_X;
						ShatteredPixelDungeon.seamlessResetScene();
					}
				}
			};
			if (changesSelected != CHANGE_ID.V0_7_X) btn0_7.textColor(0xBBBBBB);
			btn0_7.setRect(btn0_8.right() - 2, btn0_8.top(), 19, changesSelected == CHANGE_ID.V0_7_X ? 19 : 15);
			addToBack(btn0_7);

			StyledButton btn0_6 = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "0.6", 8) {
				@Override
				protected void onClick() {
					super.onClick();
					if (changesSelected != CHANGE_ID.V0_6_X) {
						changesSelected = CHANGE_ID.V0_6_X;
						ShatteredPixelDungeon.seamlessResetScene();
					}
				}
			};
			if (changesSelected != CHANGE_ID.V0_6_X) btn0_6.textColor(0xBBBBBB);
			btn0_6.setRect(btn0_7.right() - 2, btn0_8.top(), 19, changesSelected == CHANGE_ID.V0_6_X ? 19 : 15);
			addToBack(btn0_6);

			StyledButton btnOld = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "0.5-", 8) {
				@Override
				protected void onClick() {
					super.onClick();
					if (changesSelected != CHANGE_ID.V_OLD) {
						changesSelected = CHANGE_ID.V_OLD;
						ShatteredPixelDungeon.seamlessResetScene();
					}
				}
			};
			if (changesSelected != CHANGE_ID.V_OLD) btnOld.textColor(0xBBBBBB);
			btnOld.setRect(btn0_6.right() - 2, btn0_8.top(), 22, changesSelected == CHANGE_ID.V_OLD ? 19 : 15);
			addToBack(btnOld);
		} else if (changesSelected.mod == MOD_VERS.RPD) {
			// no tabs
		} else { // changesSelected.mod == MOD_VERS.EPD
			// currently no tabs
		}
		addToBack( BG );

		fadeIn();
	}

	private void updateChangesText(Image icon, String title, String... messages){
		if (changeTitle != null){
			changeTitle.icon(icon);
			changeTitle.label(title);
			changeTitle.setPos(changeTitle.left(), changeTitle.top());

			String message = "";
			for (int i = 0; i < messages.length; i++){
				message += messages[i];
				if (i != messages.length-1){
					message += "\n\n";
				}
			}
			changeBody.text(message);
			rightScroll.content().setSize(rightScroll.width(), changeBody.bottom()+2);
			rightScroll.setSize(rightScroll.width(), rightScroll.height());
			rightScroll.scrollTo(0, 0);

		} else {
			if (messages.length == 1) {
				addToFront(new WndChanges(icon, title, messages[0]));
			} else {
				addToFront(new WndChangesTabbed(icon, title, messages));
			}
		}
	}

	public static void showChangeInfo(Image icon, String title, String... messages){
		Scene s = ShatteredPixelDungeon.scene();
		if (s instanceof ChangesScene){
			((ChangesScene) s).updateChangesText(icon, title, messages);
			return;
		}
		if (messages.length == 1) {
			s.addToFront(new WndChanges(icon, title, messages[0]));
		} else {
			s.addToFront(new WndChangesTabbed(icon, title, messages));
		}
	}
	
	@Override
	protected void onBackPressed() {
		ShatteredPixelDungeon.switchNoFade(TitleScene.class);
	}

}
