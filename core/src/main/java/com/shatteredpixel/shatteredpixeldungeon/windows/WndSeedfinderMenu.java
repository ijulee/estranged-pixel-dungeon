package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.MiniCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.OptionSlider;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;

public class WndSeedfinderMenu extends Window {

    private static final int WIDTH_P	    = 122;
    private static final int WIDTH_L	    = 223;

    protected static final int MARGIN 		= 2;

    private static final int SLIDER_HEIGHT	= 24;
    private static final int BTN_HEIGHT	    = 16;
    private static final float GAP          = 1;

    //a simple scene change callback that does nothing since we need to do this a few times
    private static Game.SceneChangeCallback CALLBACK = new Game.SceneChangeCallback() {
        @Override
        public void beforeCreate() {}

        @Override
        public void afterCreate() {}
    };

    RenderedTextBlock title;

    ColorBlock sep1;
    OptionSlider slideFloors;
    RenderedTextBlock infoFloors;
    RedButton btnLoggingSettings;
    RedButton btnChallenges;
    RedButton btnMode;
    OptionSlider slideFontSize;
    
    public WndSeedfinderMenu() {
        super();

        title = PixelScene.renderTextBlock(Messages.get(this, "title"), 9);
        title.hardlight(TITLE_COLOR);
        add(title);

        sep1 = new ColorBlock(1, 1, 0xFF000000);
        add(sep1);

        slideFloors = new OptionSlider(Messages.get(this, "floors_title", SPDSettings.seedfinderFloors()),
                "1", "29", 1, 29) {
            @Override
            protected void onChange() {
                SPDSettings.seedfinderFloors(getSelectedValue());

                ShatteredPixelDungeon.seamlessResetScene(CALLBACK);
            }
        };
        slideFloors.setSelectedValue(SPDSettings.seedfinderFloors());
        add(slideFloors);

        infoFloors = PixelScene.renderTextBlock(Messages.get(this, "floors_info"), 7);
        add(infoFloors);

        btnLoggingSettings = new RedButton(Messages.get(this, "logoptions_button"), 9) {
            @Override
            protected void onClick() {
                ShatteredPixelDungeon.scene().addToFront(new WndLoggingOpts());
            }
        };
        add(btnLoggingSettings);

        btnChallenges = new RedButton(Messages.get(this, "challenges")) {
            @Override
            protected void onClick() {
                ShatteredPixelDungeon.scene().addToFront(new WndChallenges(SPDSettings.challenges(), true) {
                    public void onBackPressed() {
                        super.onBackPressed();

                        ShatteredPixelDungeon.seamlessResetScene(CALLBACK);
                    }
                });
            }
        };
        btnChallenges.textColor(SPDSettings.challenges() == 0 ? WHITE : TITLE_COLOR);
        add(btnChallenges);

        String modeBtnDescKey = SPDSettings.seedfinderConditionANY() ? "mode_any" : "mode_all";
        btnMode = new RedButton(Messages.get(this, modeBtnDescKey)) {
            @Override
            protected void onClick() {
                SPDSettings.seedfinderConditionANY(!SPDSettings.seedfinderConditionANY());

                ShatteredPixelDungeon.seamlessResetScene(CALLBACK);
            }
        };
        add(btnMode);

        slideFontSize = new OptionSlider(Messages.get(this, "fontsize_title", SPDSettings.seedfinderFontSize()),
                "tiny", "smaller", 3, 6) {
            @Override
            protected void onChange() {
                SPDSettings.seedfinderFontSize(getSelectedValue());

                ShatteredPixelDungeon.seamlessResetScene(CALLBACK);
            }
        };
        slideFontSize.setSelectedValue(SPDSettings.seedfinderFontSize());
        add(slideFontSize);

        layout();
    }

    private void layout() {
        int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

        title.setPos((width - title.width())/2, GAP);

        sep1.size(width, 1);
        sep1.y = title.bottom() + 3*GAP;

        slideFloors.setRect(0, sep1.y + GAP, width, SLIDER_HEIGHT);
        infoFloors.maxWidth(width);
        infoFloors.setPos(0, slideFloors.bottom() + GAP);

        btnLoggingSettings.setRect(0, infoFloors.bottom() + GAP, width, BTN_HEIGHT);

        btnChallenges.setRect(0, btnLoggingSettings.bottom() + GAP, width / 2, BTN_HEIGHT);
        btnMode.setRect(width / 2 + GAP, btnLoggingSettings.bottom() + GAP, width / 2 - GAP, BTN_HEIGHT);
        slideFontSize.setRect(0, btnMode.bottom() + GAP, width, SLIDER_HEIGHT);

        resize(width, (int) slideFontSize.bottom());
    }

    public static class WndLoggingOpts extends Window {

        private static final int BTN_HEIGHT = 11;

        RenderedTextBlock logOptsDesc;
        ColorBlock sep2;

        MiniCheckBox chkTrinkets;
        MiniCheckBox chkEquipment;
        MiniCheckBox chkScrolls;
        MiniCheckBox chkPotions;
        MiniCheckBox chkRings;
        MiniCheckBox chkWands;
        MiniCheckBox chkArtifacts;
        MiniCheckBox chkMisc;
        ColorBlock sep3;

        MiniCheckBox chkRooms;
        MiniCheckBox chkBlacklist;

        public WndLoggingOpts() {
            logOptsDesc = PixelScene.renderTextBlock(Messages.get(this, "title"), 7);
            add(logOptsDesc);

            sep2 = new ColorBlock(1, 1, 0xFF000000);
            add(sep2);

            chkTrinkets = new MiniCheckBox(Messages.get(this, "trinkets")) {
                @Override
                protected void onClick() {
                    super.onClick();
                    SPDSettings.logTrinkets(checked());
                }
            };
            chkTrinkets.checked(SPDSettings.logTrinkets());
            add(chkTrinkets);

            chkEquipment = new MiniCheckBox(Messages.get(this, "equipment")) {
                @Override
                protected void onClick() {
                    super.onClick();
                    SPDSettings.logEquipment(checked());
                }
            };
            chkEquipment.checked(SPDSettings.logEquipment());
            add(chkEquipment);

            chkScrolls = new MiniCheckBox(Messages.get(this, "scrolls")) {
                @Override
                protected void onClick() {
                    super.onClick();
                    SPDSettings.logScrolls(checked());
                }
            };
            chkScrolls.checked(SPDSettings.logScrolls());
            add(chkScrolls);

            chkPotions = new MiniCheckBox(Messages.get(this, "potions")) {
                @Override
                protected void onClick() {
                    super.onClick();
                    SPDSettings.logPotions(checked());
                }
            };
            chkPotions.checked(SPDSettings.logPotions());
            add(chkPotions);

            chkRings = new MiniCheckBox(Messages.get(this, "rings")) {
                @Override
                protected void onClick() {
                    super.onClick();
                    SPDSettings.logRings(checked());
                }
            };
            chkRings.checked(SPDSettings.logRings());
            add(chkRings);

            chkWands = new MiniCheckBox(Messages.get(this, "wands")) {
                @Override
                protected void onClick() {
                    super.onClick();
                    SPDSettings.logWands(checked());
                }
            };
            chkWands.checked(SPDSettings.logWands());
            add(chkWands);

            chkArtifacts = new MiniCheckBox(Messages.get(this, "artifacts")) {
                @Override
                protected void onClick() {
                    super.onClick();
                    SPDSettings.logArtifacts(checked());
                }
            };
            chkArtifacts.checked(SPDSettings.logArtifacts());
            add(chkArtifacts);

            chkMisc = new MiniCheckBox(Messages.get(this, "misc")) {
                @Override
                protected void onClick() {
                    super.onClick();
                    SPDSettings.logMisc(checked());
                }
            };
            chkMisc.checked(SPDSettings.logMisc());
            add(chkMisc);

            sep3 = new ColorBlock(1, 1, 0xFF000000);
            add(sep3);

            chkRooms = new MiniCheckBox(Messages.get(this, "use_rooms")) {
                @Override
                protected void onClick() {
                    super.onClick();
                    SPDSettings.useRooms(checked());
                }
            };
            chkRooms.checked(SPDSettings.useRooms());
            add(chkRooms);

            chkBlacklist = new MiniCheckBox(Messages.get(this, "blacklist")) {
                @Override
                protected void onClick() {
                    super.onClick();
                    SPDSettings.ignoreBlacklist(checked());
                }
            };
            chkBlacklist.checked(SPDSettings.ignoreBlacklist());
            add(chkBlacklist);

            layout();
        }

        public void layout() {
            int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

            logOptsDesc.setPos((width - logOptsDesc.width()) / 2f, GAP);
            PixelScene.align(logOptsDesc);

            sep2.size(width, 1);
            sep2.y = logOptsDesc.bottom() + GAP;

            chkTrinkets.setRect(0, sep2.y + 1 + GAP, width, BTN_HEIGHT);
            chkEquipment.setRect(0, chkTrinkets.bottom() + GAP, width, BTN_HEIGHT);
            chkScrolls.setRect(0, chkEquipment.bottom() + GAP, width, BTN_HEIGHT);
            chkPotions.setRect(0, chkScrolls.bottom() + GAP, width, BTN_HEIGHT);
            chkRings.setRect(0, chkPotions.bottom() + GAP, width, BTN_HEIGHT);
            chkWands.setRect(0, chkRings.bottom() + GAP, width, BTN_HEIGHT);
            chkArtifacts.setRect(0, chkWands.bottom() + GAP, width, BTN_HEIGHT);
            chkMisc.setRect(0, chkArtifacts.bottom() + GAP, width, BTN_HEIGHT);

            sep3.size(width, 1);
            sep3.y = chkMisc.bottom() + GAP;

            chkRooms.setRect(0, sep3.y + 1 + GAP, width, BTN_HEIGHT);
            chkBlacklist.setRect(0, chkRooms.bottom() + GAP, width, BTN_HEIGHT);

            resize(width, (int)chkBlacklist.bottom());

        }
    }
}
