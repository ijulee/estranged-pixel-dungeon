package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAugmentation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.bow.TacticalBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class GunSmithingTool extends Item {

    public static final String AC_USE		= "USE";
    public static Gun gun = null;

    {
        image = ItemSpriteSheet.GUNSMITHING_TOOL;
        defaultAction = AC_USE;
        stackable = true;

        unique = true;
        bones = false;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_USE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_USE)) {
            GameScene.selectItem( itemSelector );
        }
    }

    private String inventoryTitle(){
        return Messages.get(this, "inv_title");
    }

    protected Class<?extends Bag> preferredBag = Belongings.Backpack.class;

    protected boolean usableOnItem( Item item ){
        return item instanceof Gun || item instanceof TacticalBow;
    }

    protected static void onItemSelected() {
        curUser.spend( 1f );
        curUser.busy();
        (curUser.sprite).operate( curUser.pos );

        Sample.INSTANCE.play( Assets.Sounds.EVOKE );
        CellEmitter.center( curUser.pos ).burst( Speck.factory( Speck.STAR ), 7 );
        Invisibility.dispel();

        updateQuickslot();

        Statistics.gunModified = true;
        Badges.validateGunnerUnlock();

        Catalog.countUse(GunSmithingTool.class);

        Item tool = Dungeon.hero.belongings.getItem(GunSmithingTool.class);
        if (tool != null) {
            tool.detach(Dungeon.hero.belongings.backpack);
        }
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int value() {
        return (30+20)*quantity;
    }

    public static class WndModSelect extends WndOptions {

        public WndModSelect(){
            super(new ItemSprite(new GunSmithingTool()),
                    Messages.titleCase(new GunSmithingTool().name()),
                    Messages.get(GunSmithingTool.class, "mod_select"),
                    Messages.titleCase(Messages.get(Gun.gunModClasses[0], "name")),
                    Messages.titleCase(Messages.get(Gun.gunModClasses[1], "name")),
                    Messages.titleCase(Messages.get(Gun.gunModClasses[2], "name")),
                    Messages.titleCase(Messages.get(Gun.gunModClasses[3], "name")),
                    Messages.titleCase(Messages.get(Gun.gunModClasses[4], "name")),
                    Messages.titleCase(Messages.get(Gun.gunModClasses[5], "name")),
                    Messages.get(GunSmithingTool.class, "cancel"));
        }

        @Override
        protected void onSelect(int index) {
            if (index < 6) {
                Class<Gun.GunMod<?>> modType = Gun.gunModClasses[index];
                Gun.GunMod<?>[] options = modType.getEnumConstants();
                Gun.GunMod<?> current = GunSmithingTool.gun.getGunMod(modType);

                ArrayList<String> optsStr = new ArrayList<>();
                for (Gun.GunMod<?> m : options) {
                    if (m != current) {
                        optsStr.add(Messages.titleCase(Messages.get(m, m.name())));
                    } else {
                        optsStr.add(Messages.titleCase("* "+Messages.get(m, m.name())));

                    }
                }
                optsStr.add("Back");

                String descStr = Messages.get(modType, "desc") +
                        "\n\n" + Messages.get(GunSmithingTool.class, "current");

                GameScene.show(new WndOptions(
                        new ItemSprite(GunSmithingTool.gun),
                        GunSmithingTool.gun.name(),
                        descStr,
                        optsStr.toArray(new String[0]))
                {
                    private float elapsed = 0f;

                    @Override
                    public synchronized void update() {
                        super.update();
                        elapsed += Game.elapsed;
                    }

                    @Override
                    public void hide() {
                        if (elapsed > 0.2f) {
                            super.hide();
                        }
                    }

                    @Override
                    protected void onSelect(int index) {
                        if (elapsed > 0.2f) {
                            if (index < optsStr.size() - 1) {
                                GunSmithingTool.gun.setGunMod(options[index]);
                                GunSmithingTool.gun = null;
                                onItemSelected();
                            } else if (index == optsStr.size() - 1) {
                                GameScene.show(new WndModSelect());
                            }
                        }

                    }

                    @Override
                    protected boolean enabled(int index) {
                        return index == optsStr.size()-1 || options[index] != current;
                    }

                    @Override
                    protected void onInfo(int index) {
                        GameScene.show(new WndTitledMessage(
                                Icons.get(Icons.INFO),
                                Messages.titleCase(Messages.get(modType, options[index].name())),
                                Messages.titleCase(Messages.get(modType, options[index].name()+"_desc"))));
                    }
                });
            } else {
                hide();
            }
        }

        @Override
        protected boolean hasInfo(int index) {
            return index < 6;
        }

        @Override
        protected void onInfo( int index ) {
            Class<Gun.GunMod<?>> modType = Gun.gunModClasses[index];
            GameScene.show(new WndTitledMessage(
                    Icons.get(Icons.INFO),
                    Messages.titleCase(Messages.get(modType, "name")),
                    Messages.get(modType, "desc")));
        }

    }

    protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

        @Override
        public String textPrompt() {
            return inventoryTitle();
        }

        @Override
        public Class<? extends Bag> preferredBag() {
            return preferredBag;
        }

        @Override
        public boolean itemSelectable(Item item) {
            return usableOnItem(item);
        }

        @Override
        public void onSelect( Item item ) {

            //FIXME this safety check shouldn't be necessary
            //it would be better to eliminate the curItem static variable.
            if (!(curItem instanceof GunSmithingTool)){
                return;
            }

            if (item != null && itemSelectable(item)) {
                if (item instanceof TacticalBow) {
                    ((TacticalBow) item).modify();
                    onItemSelected();
                } else if (item instanceof Gun) {
                    GunSmithingTool.gun = (Gun) item;
                    GameScene.show(new WndModSelect());
                }
            }
        }
    };

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{StoneOfAugmentation.class, LiquidMetal.class};
            inQuantity = new int[]{1, 20};

            cost = 3;

            output = GunSmithingTool.class;
            outQuantity = 1;
        }

    }
}
