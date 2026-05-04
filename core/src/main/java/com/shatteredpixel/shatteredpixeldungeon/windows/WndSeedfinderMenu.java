package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
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

public class WndSeedfinderMenu extends Window {

    private static final int WIDTH_P	    = 122;
    private static final int WIDTH_L	    = 223;

    private static final int TTL_HEIGHT = 16;
    private static final int SLIDER_HEIGHT	= 24;
    private static final int FLOORS_HEIGHT	= 20;
    private static final int BTN_HEIGHT	    = 16;
    private static final int INFO_SIZE      = 14;
    private static final float GAP          = 2;

    IconTitle title;

    OptionSlider slideFloors;
    RedButton btnFloorsMinus;
    RenderedTextBlock textFloors;
    RedButton btnFloorsPlus;
    IconButton infoFloors;
    OptionSlider slideFontSize;
    IconButton infoFontSize;
    RedButton btnSearchOpts;
    RedButton btnLoggingSettings;
    RedButton btnChallenges;

    public WndSeedfinderMenu() {
        super();

        title = new IconTitle(Icons.PREFS.get(), Messages.get(this, "title"));
        add(title);

        slideFloors = new OptionSlider("", "1F", "29F", 1, 29) {
            @Override
            protected void onChange() {
                SPDSettings.seedfinderFloors(getSelectedValue());
                ShatteredPixelDungeon.seamlessResetScene();
            }
        };
        slideFloors.setSelectedValue(SPDSettings.seedfinderFloors());
        add(slideFloors);

        btnFloorsMinus = new RedButton("-") {
            @Override
            protected void onClick() {
                SPDSettings.seedfinderFloors(SPDSettings.seedfinderFloors()-1);
                ShatteredPixelDungeon.seamlessResetScene();
            }
        };
        add(btnFloorsMinus);

        textFloors = GameScene.renderTextBlock(
                Messages.get(this, "floors_title", SPDSettings.seedfinderFloors()), 9);
        add(textFloors);

        btnFloorsPlus = new RedButton("+") {
            @Override
            protected void onClick() {
                SPDSettings.seedfinderFloors(SPDSettings.seedfinderFloors()+1);
                ShatteredPixelDungeon.seamlessResetScene();
            }
        };
        add(btnFloorsPlus);

        infoFloors = new IconButton(Icons.INFO.get()) {
            @Override
            protected void onClick() {
                ShatteredPixelDungeon.scene().addToFront(
                        new WndMessage(Messages.get(WndSeedfinderMenu.class, "floors_info")) );
            }
        };
        add(infoFloors);

        slideFontSize = new OptionSlider(Messages.get(this, "fontsize_title", SPDSettings.seedfinderFontSize()),
                "small", "large", 3, 9) {
            @Override
            protected void onChange() {
                SPDSettings.seedfinderFontSize(getSelectedValue());

                ShatteredPixelDungeon.seamlessResetScene();
            }
        };
        slideFontSize.setSelectedValue(SPDSettings.seedfinderFontSize());
        add(slideFontSize);

        infoFontSize = new IconButton(Icons.INFO.get()) {
            @Override
            protected void onClick() {
                ShatteredPixelDungeon.scene().addToFront(
                        new WndMessage(Messages.get(WndSeedfinderMenu.class, "fontsize_info")) );
            }
        };
        add(infoFontSize);

        btnSearchOpts = new RedButton(Messages.get(WndSearchOpts.class, "title")) {
            @Override
            protected void onClick() {
                ShatteredPixelDungeon.scene().addToFront(new WndSearchOpts());
            }
        };
        add(btnSearchOpts);

        btnLoggingSettings = new RedButton(Messages.get(WndLoggingOpts.class, "title"), 9) {
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

                        ShatteredPixelDungeon.seamlessResetScene();
                    }
                });
            }
        };
        btnChallenges.textColor(SPDSettings.challenges() == 0 ? WHITE : TITLE_COLOR);
        add(btnChallenges);

        layout();
    }

    private void layout() {
        int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

        title.setSize(width, 0);
        title.setPos((width - title.reqWidth())/2, 0);

        btnFloorsMinus.setRect(0, title.bottom() + GAP, INFO_SIZE, INFO_SIZE);
        textFloors.setPos((width - textFloors.width() - INFO_SIZE - GAP)/2,
                btnFloorsMinus.top() + (INFO_SIZE - textFloors.height())/2);
        btnFloorsPlus.setRect(width - 2*INFO_SIZE - GAP, title.bottom() + GAP, INFO_SIZE, INFO_SIZE);
        infoFloors.setRect(btnFloorsPlus.right() + GAP, title.bottom() + GAP, INFO_SIZE, INFO_SIZE);

        slideFloors.setRect(0, btnFloorsMinus.bottom() + GAP, width, FLOORS_HEIGHT);

        slideFontSize.setRect(0, slideFloors.bottom() + GAP, width - INFO_SIZE - GAP, SLIDER_HEIGHT);
        infoFontSize.setRect(slideFontSize.right() + GAP, slideFontSize.centerY() - INFO_SIZE/2f, INFO_SIZE, INFO_SIZE);

        btnSearchOpts.setRect(0, slideFontSize.bottom() + GAP, width, BTN_HEIGHT);

        btnLoggingSettings.setRect(0, btnSearchOpts.bottom() + GAP, width, BTN_HEIGHT);
        btnChallenges.setRect(0, btnLoggingSettings.bottom() + GAP, width, BTN_HEIGHT);

        resize(width, (int) btnChallenges.bottom());
    }

    public static class WndLoggingOpts extends Window {
        private static final int TTL_HEIGHT = 16;
        private static final int BTN_HEIGHT = 11;

        RenderedTextBlock title;

        MiniCheckBox chkTrinkets;
        MiniCheckBox chkEquipment;
        MiniCheckBox chkScrolls;
        MiniCheckBox chkPotions;
        MiniCheckBox chkRings;
        MiniCheckBox chkWands;
        MiniCheckBox chkArtifacts;
        MiniCheckBox chkMisc;

        ColorBlock sep3;

        MiniCheckBox chkBlacklist;
        MiniCheckBox chkShops;
        IconButton infoBlacklist;
        IconButton infoShops;

        public WndLoggingOpts() {
            title = PixelScene.renderTextBlock(Messages.get(this, "title"), 9);
            title.hardlight( TITLE_COLOR );
            add(title);

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

            chkBlacklist = new MiniCheckBox(Messages.get(this, "blacklist")) {
                @Override
                protected void onClick() {
                    super.onClick();
                    SPDSettings.ignoreBlacklist(checked());
                }
            };
            chkBlacklist.checked(SPDSettings.ignoreBlacklist());
            add(chkBlacklist);

            infoBlacklist = new IconButton(Icons.MINI_INFO.get()) {
                @Override
                protected void onClick() {
                    super.onClick();
                    ShatteredPixelDungeon.scene().add(
                            new WndMessage(Messages.get(WndLoggingOpts.class, "blacklist_info"))
                    );
                }
            };
            add( infoBlacklist );

            chkShops = new MiniCheckBox(Messages.get(this, "shops")) {
                @Override
                protected void onClick() {
                    super.onClick();
                    SPDSettings.checkShops(checked());
                }
            };
            chkShops.checked(SPDSettings.checkShops());
            add(chkShops);

            infoShops = new IconButton(Icons.MINI_INFO.get()) {
                @Override
                protected void onClick() {
                    super.onClick();
                    ShatteredPixelDungeon.scene().add(
                            new WndMessage(Messages.get(WndLoggingOpts.class, "shops_info"))
                    );
                }
            };
            add( infoShops );

            layout();
        }

        public void layout() {
            int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

            title.setPos((width - title.width()) / 2f, (TTL_HEIGHT - title.height()) / 2);
            PixelScene.align(title);

            chkTrinkets.setRect(0, TTL_HEIGHT + GAP, width, BTN_HEIGHT);
            chkEquipment.setRect(0, chkTrinkets.bottom() + GAP, width, BTN_HEIGHT);
            chkScrolls.setRect(0, chkEquipment.bottom() + GAP, width, BTN_HEIGHT);
            chkPotions.setRect(0, chkScrolls.bottom() + GAP, width, BTN_HEIGHT);
            chkRings.setRect(0, chkPotions.bottom() + GAP, width, BTN_HEIGHT);
            chkWands.setRect(0, chkRings.bottom() + GAP, width, BTN_HEIGHT);
            chkArtifacts.setRect(0, chkWands.bottom() + GAP, width, BTN_HEIGHT);
            chkMisc.setRect(0, chkArtifacts.bottom() + GAP, width, BTN_HEIGHT);

            sep3.size(width, 1);
            sep3.y = chkMisc.bottom() + GAP;

            chkBlacklist.setRect(0, sep3.y + 1 + GAP, width - BTN_HEIGHT, BTN_HEIGHT);
            chkShops.setRect(0, chkBlacklist.bottom() + GAP, width - BTN_HEIGHT, BTN_HEIGHT);

            infoBlacklist.setRect(chkBlacklist.right(), chkBlacklist.top(), BTN_HEIGHT, BTN_HEIGHT);
            infoShops.setRect(chkShops.right(), chkShops.top(), BTN_HEIGHT, BTN_HEIGHT);

            resize(width, (int)chkShops.bottom());

        }
    }

    public static class WndSearchOpts extends Window {
        RenderedTextBlock title;

        CheckBox chkMode;
        IconButton infoMode;
        CheckBox chkMulti;
        IconButton infoMulti;
        CheckBox chkExact;
        IconButton infoExact;
        CheckBox chkRooms;
        IconButton infoRooms;


        public WndSearchOpts() {
            title = PixelScene.renderTextBlock(Messages.get(this, "title"), 9);
            title.hardlight( TITLE_COLOR );
            add(title);

            String modeBtnDescKey = SPDSettings.seedfinderConditionANY() ? "mode_any" : "mode_all";
            chkMode = new CheckBox(Messages.get(WndSeedfinderMenu.class, modeBtnDescKey)) {
                @Override
                protected void onClick() {
                    super.onClick();
                    SPDSettings.seedfinderConditionANY(checked());
                    ShatteredPixelDungeon.seamlessResetScene();
                }
            };
            chkMode.checked(SPDSettings.seedfinderConditionANY());
            add( chkMode );

            infoMode = new IconButton(Icons.INFO.get()) {
                @Override
                protected void onClick() {
                    ShatteredPixelDungeon.scene().addToFront(
                            new WndMessage(Messages.get(WndSeedfinderMenu.class, "mode_info")) );
                }
            };
            add( infoMode );

            chkMulti = new CheckBox(Messages.get(WndSearchOpts.class, "multi")) {
                @Override
                protected void onClick() {
                    super.onClick();
                    //FIXME Add and change the option here.
                }
            };
            chkMulti.checked(/*FIXME Add and change the option here.*/);
            add( chkMulti );

            infoMulti = new IconButton(Icons.INFO.get()) {
                @Override
                protected void onClick() {
                    super.onClick();
                    ShatteredPixelDungeon.scene().addToFront(
                            new WndMessage(Messages.get(WndSearchOpts.class, "multi_info"))
                    );
                }
            };
            add( infoMulti );

            chkExact = new CheckBox(Messages.get(WndSearchOpts.class, "exact")) {
                @Override
                protected void onClick() {
                    super.onClick();
                    //FIXME Add and change the option here.
                }
            };
            chkExact.checked(/*FIXME Add and change the option here.*/);
            add( chkExact );

            infoExact = new IconButton(Icons.INFO.get()) {
                @Override
                protected void onClick() {
                    super.onClick();
                    ShatteredPixelDungeon.scene().addToFront(
                            new WndMessage(Messages.get(WndSearchOpts.class, "exact_info"))
                    );
                }
            };
            add( infoExact );

            chkRooms = new CheckBox(Messages.get(WndSearchOpts.class, "use_rooms")) {
                @Override
                protected void onClick() {
                    super.onClick();
                    SPDSettings.useRooms(checked());
                }
            };
            chkRooms.checked(SPDSettings.useRooms());
            add(chkRooms);

            infoRooms = new IconButton(Icons.INFO.get()) {
                @Override
                protected void onClick() {
                    super.onClick();
                    ShatteredPixelDungeon.scene().addToFront(
                            new WndMessage(Messages.get(WndSearchOpts.class, "rooms_info"))
                    );
                }
            };
            add( infoRooms );

            layout();
        }

        public void layout() {
            int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

            title.setPos((width - title.width()) / 2f, (TTL_HEIGHT - title.height()) / 2);
            PixelScene.align(title);

            chkMode.setRect(0, title.bottom() + 2*GAP, width - INFO_SIZE - GAP, BTN_HEIGHT);
            infoMode.setRect(chkMode.right() + GAP, chkMode.centerY() - INFO_SIZE/2f, INFO_SIZE, INFO_SIZE);

            chkMulti.setRect(0, chkMode.bottom() + GAP, width - INFO_SIZE - GAP, BTN_HEIGHT);
            infoMulti.setRect(chkMulti.right() + GAP, chkMulti.centerY() - INFO_SIZE/2f, INFO_SIZE, INFO_SIZE);

            chkExact.setRect(0, chkMulti.bottom() + GAP, width - INFO_SIZE - GAP, BTN_HEIGHT);
            infoExact.setRect(chkExact.right() + GAP, chkExact.centerY() - INFO_SIZE/2f, INFO_SIZE, INFO_SIZE);

            chkRooms.setRect(0, chkExact.bottom() + GAP, width - INFO_SIZE - GAP, BTN_HEIGHT);
            infoRooms.setRect(chkRooms.right() + GAP, chkRooms.centerY() - INFO_SIZE/2f, INFO_SIZE, INFO_SIZE);

            resize(width, (int)chkRooms.bottom());
        }
    }
}
