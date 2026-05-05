package com.shatteredpixel.shatteredpixeldungeon.items;

import static com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping.discover;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DistortionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//This is from Elemental PD

public class Teleporter extends Item {
    {
        defaultAction = AC_TELEPORT;
        image = ItemSpriteSheet.TELEPORTER;

        for (Catalog cat : Catalog.values()) {
            if (cat != Catalog.GLYPHS && cat != Catalog.ENCHANTMENTS) {
                itemClasses.addAll(cat.items());
            }
        }
    }

    public static final String AC_TELEPORT = "teleport";
    public static final String AC_RETURN = "return";
    public static final String AC_SPAWN = "spawn";
    public static final String AC_RANDOMSPAWN = "randomSpawn";
    public static final String AC_GETITEM = "getItem";
    public static final String AC_MAPPING = "mapping";
    public static final String AC_LEVELUP = "levelUp";
    public static final String AC_TEST = "test";

    public static int[] secretRooms = new int[32];
    private static ArrayList<Class<?>> itemClasses = new ArrayList<>();

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
            //old temple branch level
            if (Dungeon.depth == 14 && Dungeon.branch == 2) {
                InterlevelScene.returnDepth = 14;
                InterlevelScene.returnBranch = 3;

            //old temple branch dead end
            } else if (Dungeon.depth == 14 && Dungeon.branch == 3) {
                InterlevelScene.returnDepth = 14;
                InterlevelScene.returnBranch = 0;

            //always return to main branch without descending if on side branch
            } else if (Dungeon.branch != 0) {
                InterlevelScene.returnDepth = Dungeon.depth;
                InterlevelScene.returnBranch = 0;

            //descend normally
            } else {
                InterlevelScene.returnDepth = Math.max(1, (Dungeon.depth + 1));
                InterlevelScene.returnBranch = 0;
            }

            InterlevelScene.returnPos = -1; //entrance stairs
            Game.switchScene( InterlevelScene.class );
        }

        if (action.equals(AC_RETURN)) {
            InterlevelScene.mode = InterlevelScene.Mode.RETURN;
            //old temple branch level
            if (Dungeon.depth == 14 && Dungeon.branch == 2)  {
                InterlevelScene.returnDepth = 14;
                InterlevelScene.returnBranch = 0;

            //old temple branch dead end
            } else if (Dungeon.depth == 14 && Dungeon.branch == 3) {
                InterlevelScene.returnDepth = 14;
                InterlevelScene.returnBranch = 2;

            //always return to main branch without ascending if on side branch
            } else if (Dungeon.branch != 0) {
                InterlevelScene.returnDepth = Dungeon.depth;
                InterlevelScene.returnBranch = 0;

            //ascend normally
            } else {
                InterlevelScene.returnDepth = Math.max(1, (Dungeon.depth - 1));
                InterlevelScene.returnBranch = 0;
            }

            InterlevelScene.returnPos = -2; //exit stairs
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
            GameScene.show(new WndGetItem());
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
            int lvl = hero.lvl;
            do {
                hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(hero.maxExp()), FloatingText.EXPERIENCE);
                hero.earnExp( hero.maxExp(), getClass() );
                new Flare( 6, 32 ).color(0xFFFF00, true).show( curUser.sprite, 2f );
                lvl++;
            } while (lvl < 30);
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

    public static class WndGetItem extends WndTextInput {
        public static String lastPrompt = Messages.get(Teleporter.class, "getitem_prompt");

        public WndGetItem() {
            super(Messages.get(Teleporter.class, "getitem_title"),
                    Messages.get(Teleporter.class, "getitem_desc"),
                    lastPrompt,
                    100, true,
                    Messages.get(Teleporter.class, "getitem_yes"),
                    Messages.get(Teleporter.class, "getitem_no"));
        }

        @Override
        public void onSelect(boolean positive, String text) {
            if (positive && !text.isEmpty()) {
                if (text.trim().equals("help")) {
                    GameScene.show(new WndTitledMessage(
                            Icons.get(Icons.INFO),
                            Messages.titleCase(Messages.get(Teleporter.class, "help_title")),
                            Messages.get(Teleporter.class, "help_desc")));
                } else if (text.trim().equals("list")) {
                    StringBuilder itemList = new StringBuilder();
                    for (Catalog cat : Catalog.values()) {
                        if (cat != Catalog.ENCHANTMENTS && cat != Catalog.GLYPHS) {
                            itemList.append(Messages.format("_%s_:\n",
                                    Messages.get(Catalog.class, cat.name()+".title").toUpperCase()));

                            for (Class<?> itemClass : cat.items()) {
                                itemList.append(Messages.get(itemClass, "name").toLowerCase()).append(", ");
                            }
                            itemList.delete(itemList.length()-2, itemList.length()).append("\n\n");
                        }
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
                    lastPrompt = text;
                    String promptRegex = Messages.get( Teleporter.class, "getitem_regex",
                            Messages.get(Teleporter.class, "getitem_amount"),
                            Messages.get(Teleporter.class, "getitem_upgrade"),
                            Messages.get(Teleporter.class, "getitem_identify") );
                    Pattern pattern = Pattern.compile(promptRegex);
                    Matcher matcher = pattern.matcher(Messages.lowerCase(text));

                    if (!matcher.matches()) {
                        GameScene.show(new WndGetError(Messages.get(Teleporter.class, "warn_bad_prompt")));
                        hide();
                        return;
                    }

                    String nameStr = matcher.group("name");
                    Class<?> itemClass = null;
                    for (Class<?> cls : Teleporter.itemClasses) {
                        if (nameStr.equalsIgnoreCase(Messages.get(cls, "name"))) {
                            itemClass = cls;
                            break;
                        }
                    }

                    if (itemClass == null) {
                        GameScene.show(new WndGetError(Messages.get(Teleporter.class, "warn_item_name", nameStr)));
                        hide();
                        return;
                    }

                    Item item = null;
                    try {
                        if (Key.class.isAssignableFrom(itemClass)) {
                            Constructor keyConstructor = ClassReflection.getConstructor(itemClass, int.class, int.class);
                            item = (Item) keyConstructor.newInstance(Dungeon.depth, Dungeon.branch);
                        } else {
                            item = (Item) Reflection.newInstance(itemClass);
                        }
                    } catch (Exception e) {
                        Game.reportException(e);
                    }

                    if (item == null) {
                        GameScene.show(new WndGetError(Messages.get(
                                Teleporter.class, "warn_item_create", Messages.get(itemClass, "name")
                        )));
                        hide();
                        return;
                    }

                    String amountStr = matcher.group("amt");
                    int amount = 1;
                    if (!amountStr.isEmpty() && item.stackable) try {
                        amount = Integer.parseInt(amountStr);
                    } catch (NumberFormatException ignored) {
                        GLog.w(Messages.get(Teleporter.class, "warn_amount"));
                    }

                    String levelStr = matcher.group("level");
                    int level = 0;
                    if (!levelStr.isEmpty() && item.isUpgradable()) try {
                        level = Integer.parseInt(levelStr);
                    } catch (NumberFormatException e) {
                        GLog.w(Messages.get(Teleporter.class, "warn_upgrade"));
                    }

                    String idStr = matcher.group("id");
                    boolean identify = idStr.equalsIgnoreCase("true");

                    item.quantity(amount).upgrade(level);
                    if (identify) item.identify(false);

                    GLog.p(Messages.get(Teleporter.class, "getitem_success", Messages.titleCase(item.title())));
                    if (item.doPickUp(Dungeon.hero)) {
                        Dungeon.hero.spend(-item.pickupDelay());
                    } else {
                        Dungeon.level.drop(item, Dungeon.hero.pos).sprite.drop();
                    }
                }
            } else {
                lastPrompt = Messages.get(Teleporter.class, "getitem_prompt");
                hide();
            }
        }
    }

    public static class WndGetError extends WndOptions {
        public WndGetError(String text) {
            super(Icons.WARNING.get(),
                    Messages.get(Teleporter.class, "getitem_error"),
                    text,
                    Messages.get(Teleporter.class, "getitem_retry"),
                    Messages.get(Teleporter.class, "getitem_no"));
        }

        @Override
        protected void onSelect(int index) {
            if (index == 0) {
                GameScene.show(new WndGetItem());
            }
            hide();
        }
    }
}