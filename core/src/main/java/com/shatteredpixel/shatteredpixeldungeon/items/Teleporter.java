package com.shatteredpixel.shatteredpixeldungeon.items;

import static com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping.discover;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.levels.TempleChasmLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.TempleLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DistortionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Objects;
//This is from Elemental PD

public class Teleporter extends Item {

    String AC_TELEPORT = "teleport";
    String AC_RETURN = "return";
    String AC_SPAWN = "spawn";
    String AC_RANDOMSPAWN = "randomSpawn";
    String AC_GETITEM = "getItem";
    String AC_MAPPING = "mapping";
    String AC_LEVELUP = "levelUp";
    String AC_TEST = "test";

    static ArrayList<Class<?>> itemClass = new ArrayList<>();

    public static int[] secretRooms = new int[32];

    {
        defaultAction = AC_TELEPORT;
        image = ItemSpriteSheet.TELEPORTER;

        for (Catalog cat : Catalog.values()) {
            if (cat != Catalog.GLYPHS && cat != Catalog.ENCHANTMENTS) {
                itemClass.addAll(cat.items());
            }
        }
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_TELEPORT);
        actions.add(AC_RETURN);
        actions.add(AC_SPAWN);
        actions.add(AC_RANDOMSPAWN);
        actions.add(AC_GETITEM);
        actions.add(AC_MAPPING);
        actions.add(AC_LEVELUP);
        actions.add(AC_TEST);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);
        if (action.equals(AC_TELEPORT)) {
            InterlevelScene.mode = InterlevelScene.Mode.RETURN;
            if (Dungeon.level instanceof TempleLevel && Dungeon.depth == 14) {
                InterlevelScene.returnDepth = 14;
                InterlevelScene.returnBranch = 3;
            } else if (Dungeon.level instanceof TempleChasmLevel && Dungeon.depth == 14) {
                InterlevelScene.returnDepth = 14;
                InterlevelScene.returnBranch = 0;
            } else {
                InterlevelScene.returnDepth = Math.max(1, (Dungeon.depth + 1));
                InterlevelScene.returnBranch = Dungeon.branch;
            }
            InterlevelScene.returnPos = -1;
            Game.switchScene( InterlevelScene.class );
        }
        if (action.equals(AC_RETURN)) {
            InterlevelScene.mode = InterlevelScene.Mode.RETURN;
            if (Dungeon.level instanceof TempleLevel) {
                InterlevelScene.returnDepth = 14;
                InterlevelScene.returnBranch = 0;
            } else if (Dungeon.level instanceof TempleChasmLevel) {
                InterlevelScene.returnDepth = 14;
                InterlevelScene.returnBranch = 2;
            } else {
                InterlevelScene.returnDepth = Math.max(1, (Dungeon.depth - 1));
                InterlevelScene.returnBranch = Dungeon.branch;
            }
            InterlevelScene.returnPos = -2;
            Game.switchScene( InterlevelScene.class );
        }
        if (action.equals(AC_SPAWN)) {
            SummoningTrap trap1 = new SummoningTrap();
            trap1.pos = hero.pos;
            trap1.activate();
            hero.next();
        }
        if (action.equals(AC_RANDOMSPAWN)) {
            DistortionTrap trap2 = new DistortionTrap();
            trap2.pos = hero.pos;
            trap2.activate();
            hero.next();
        }
        if (action.equals(AC_GETITEM)) {

            ShatteredPixelDungeon.scene().addToFront(new WndTextInput(Messages.get(Teleporter.class, "getitem_title"),
                    Messages.get(Teleporter.class, "getitem_desc"),
                    Messages.get(Teleporter.class, "getitem_default_item")+"\n"+
                            Messages.get(Teleporter.class, "getitem_amount")+" \n"+
                            Messages.get(Teleporter.class, "getitem_upgrade")+" \n"+
                            Messages.get(Teleporter.class, "getitem_identify")+" ",
                    100,
                    true,
                    Messages.get(Teleporter.class, "getitem_yes"),
                    Messages.get(Teleporter.class, "getitem_no")){
                @Override
                public void onSelect(boolean positive, String text) {
                    if (positive && !Objects.equals(text, "")) {
                        if (text.trim().equals("help")) {
                            GameScene.show(new WndTitledMessage(
                                    Icons.get(Icons.INFO),
                                    Messages.titleCase(Messages.get(Teleporter.class, "help_title")),
                                    Messages.get(Teleporter.class, "help_desc")));
                        } else if (text.trim().equals("list")) {
                            StringBuilder itemList = new StringBuilder();
                            for (Class classes : itemClass) {
                                itemList.append(Messages.get(classes, "name")).append(", ");
                            }
                            GameScene.show(new Window() {
                                {
                                    IconTitle titlebar = new IconTitle(Icons.get(Icons.INFO),
                                        Messages.titleCase(Messages.get(Teleporter.class, "list_title")));
                                    int width = 120;
                                    int height = PixelScene.uiCamera.height - 20;

                                    titlebar.setRect( 0, 0, width, 0 );
                                    add(titlebar);

                                    RenderedTextBlock text = PixelScene.renderTextBlock( 6 );
                                    text.setHightlighting(false);
                                    text.text( itemList.toString(), width );
                                    text.setPos( titlebar.left(), titlebar.bottom() + 4 );

                                    while (PixelScene.landscape()
                                            && text.bottom() > 150
                                            && width < 220){
                                        width += 20;
                                        titlebar.setRect(0, 0, width, 0);
                                        text.setPos( titlebar.left(), titlebar.bottom() + 4 );
                                        text.maxWidth(width);
                                    }

                                    ScrollPane scroll = new ScrollPane(text);
                                    add( scroll );

                                    bringToFront(titlebar);
                                    resize( width, height );
                                    scroll.setRect(titlebar.left(), titlebar.bottom() + 4,
                                            width, height - titlebar.height() - 4);
                                }
                            });
                        } else {
                            //받은 문장을 엔터 단위로 끊는다.
                            String[] strInput = text.split("\n");
                            if (strInput.length < 4) {
                                GLog.w(Messages.get(Teleporter.class, "too_short"));
                                hide();
                                return;
                            }

                            //아이템 이름 결정 메커니즘
                            String itemName = strInput[0]; //1번째 줄에 들어가는 문장(아이템 이름)을 저장

                            Class finalItemClass = null;
                            for (Class classes : itemClass) {
                                if (itemName.equalsIgnoreCase(Messages.get(classes, "name"))) {
                                    finalItemClass = classes;
                                    break;
                                }
                            }
                            if (finalItemClass == null) { //null check
                                GLog.w(Messages.get(Teleporter.class, "wrong_itemname_1", itemName));
                                hide();
                                return;
                            }

                            Item item = (Item) Reflection.newInstance(finalItemClass);
                            if (item == null) {
                                GLog.w(Messages.get(Teleporter.class, "wrong_itemname_2"));
                                hide();
                                return;
                            }

                            //아이템 수량 결정 메커니즘
                            String amount = strInput[1].replaceAll(Messages.get(Teleporter.class, "getitem_amount"), "").replaceAll(" ", ""); //기본 문장과 공백을 제거
                            int itemAmount = 1;
                            if (!amount.equals("")) {
                                try {
                                    itemAmount = Integer.parseInt(amount);
                                } catch (NumberFormatException e) {
                                    GLog.w(Messages.get(Teleporter.class, "wrong_amount"));
                                    hide();
                                }
                            }
                            if (!item.stackable) {
                                itemAmount = 1;
                            }

                            //아이템 강화수치 결정 메커니즘
                            String upgrade = strInput[2].replaceAll(Messages.get(Teleporter.class, "getitem_upgrade"), "").replaceAll(" ", ""); //기본 문장과 공백을 제거
                            int itemUpgrade = 0;
                            if (!upgrade.equals("")) {
                                try {
                                    itemUpgrade = Integer.parseInt(upgrade);
                                } catch (NumberFormatException e) {
                                    GLog.w(Messages.get(Teleporter.class, "wrong_upgrade"));
                                    hide();
                                }
                            }
                            if (!item.isUpgradable()) {
                                itemUpgrade = 0;
                            }

                            //아이템 감정 여부 결정 메커니즘
                            String identify = strInput[3].replaceAll(Messages.get(Teleporter.class, "getitem_identify"), "").replaceAll(" ", ""); //기본 문장과 공백을 제거
                            System.out.println(Messages.get(Teleporter.class, "true"));
                            if (identify.equals(Messages.get(Teleporter.class, "true"))) {
                                identify = "true";
                            }
                            boolean isIdentified = Boolean.parseBoolean(identify);

                            //최종 아이템 지급
                            if (isIdentified) {
                                item.quantity(itemAmount).upgrade(itemUpgrade).identify();
                            } else {
                                item.quantity(itemAmount).upgrade(itemUpgrade);
                            }
                            if (!item.doPickUp( hero )) {
                                Dungeon.level.drop(item, hero.pos).sprite.drop();
                            }
                        }
                    } else {
                        hide();
                    }
                }
            });
        }
        if (action.equals(AC_MAPPING)) {
            int length = Dungeon.level.length();
            int[] map = Dungeon.level.map;
            boolean[] mapped = Dungeon.level.mapped;
            boolean[] discoverable = Dungeon.level.discoverable;

            for (int i=0; i < length; i++) {

                int terr = map[i];

                if (discoverable[i]) {

                    mapped[i] = true;
                    if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {

                        Dungeon.level.discover( i );

                        if (Dungeon.level.heroFOV[i]) {
                            GameScene.discoverTile( i, terr );
                            discover( i );
                        }
                    }
                }
            }
            GameScene.updateFog();

            GLog.i( Messages.get(this, "layout") );

            SpellSprite.show( curUser, SpellSprite.MAP );
        }
        if (action.equals(AC_LEVELUP)) {
            for (int lvl = hero.lvl; lvl < 30; lvl++) {
                Potion expPotion = new PotionOfExperience();
                expPotion.apply(hero);
            }
        }
        if (action.equals(AC_TEST)) {

        }
    }

    CellSelector.Listener listener = new CellSelector.Listener() {

        @Override
        public void onSelect(Integer cell) {
            if (cell != null) {
                Heap h = Dungeon.level.heaps.get(cell);

                Item item = h.peek();
                GLog.i(item.toString());
            }
        }

        @Override
        public String prompt() {
            return "test";
        }
    };

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }
}