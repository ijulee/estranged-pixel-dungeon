package com.shatteredpixel.shatteredpixeldungeon.items.changer;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Transmuting;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.KnightsShield;
import com.shatteredpixel.shatteredpixeldungeon.items.Rosary;
import com.shatteredpixel.shatteredpixeldungeon.items.Saddle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.bow.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.DeathSword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.EnhancedMachete;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.HeroSword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Machete;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Shovel;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Spade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSadGhost;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class OldAmulet extends Item {

    public static final String AC_USE		= "USE";

    ArrayList<Integer> abilityList = new ArrayList<>();

    {
        image = ItemSpriteSheet.OLD_AMULET;
        defaultAction = AC_USE;
        stackable = false;

        unique = true;
        bones = false;

        while (abilityList.size() < 3) {
            int index = Random.Int(16);
            if (!abilityList.contains(index)) {
                abilityList.add(index);
            }
        }
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (hero.buff(TempleCurse.class) != null) {
            actions.remove(AC_USE);
        } else {
            actions.add(AC_USE);
        }
        return actions;
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {
        if (super.doPickUp(hero, pos)) {
            if (Dungeon.depth == 14 && Dungeon.branch == 2 && hero.buff(TempleCurse.class) == null) {
                Dungeon.templeCompleted = true;
                Dungeon.level.playLevelMusic();
                Buff.affect(hero, TempleCurse.class);
            }
            return true;
        }
        return false;
    }

    @Override
    public String desc() {
        String desc = super.desc();

        if (Dungeon.hero != null && Dungeon.hero.buff(TempleCurse.class) != null) {
            desc += "\n\n" + Messages.get(this, "cannot_use");
        }

        return desc;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_USE)) {
            if (hero.buff(TempleCurse.class) != null) {
                GLog.w(Messages.get(this, "cannot_use"));
            } else {
                GameScene.selectItem( itemSelector );
            }
        }
    }

    private static final String ABILITY_LIST_0	= "abilityList_0";
    private static final String ABILITY_LIST_1	= "abilityList_1";
    private static final String ABILITY_LIST_2	= "abilityList_2";
    private static final String BOW1 = "bow1";
    private static final String BOW2 = "bow2";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( ABILITY_LIST_0, abilityList.get(0) );
        bundle.put( ABILITY_LIST_1, abilityList.get(1) );
        bundle.put( ABILITY_LIST_2, abilityList.get(2) );
        bundle.put( BOW1, bow1);
        bundle.put( BOW2, bow2);
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        abilityList.clear();
        abilityList.add(bundle.getInt(ABILITY_LIST_0));
        abilityList.add(bundle.getInt(ABILITY_LIST_1));
        abilityList.add(bundle.getInt(ABILITY_LIST_2));
        bow1 = (SpiritBow) bundle.get(BOW1);
        bow2 = (SpiritBow) bundle.get(BOW2);
    }

    private String inventoryTitle(){
        return Messages.get(this, "inv_title");
    }

    public static Item changeItem( Item item ){
        /*if (item instanceof SpiritBow) {
            return changeBow((SpiritBow)item);
        } else*/ if (item instanceof Gun) {
            return changeGun((Gun)item);
        } else if (item instanceof Shovel) {
            return changeShovel((Shovel)item);
        } else if (item instanceof Machete) {
            return changeMachete((Machete)item);
        } else if (item instanceof KnightsShield) {
            return changeShield();
        } else if (item instanceof Armor) {
            return changeSeal(((Armor) item).checkSeal());
        } else if (item instanceof BrokenSeal) {
            return changeSeal((BrokenSeal) item);
        } else {
            return null;
        }
    }

    private static BrokenSeal changeSeal(BrokenSeal seal) {
        seal.amuletApplied = true;
        return seal;
    }

    private static Gun changeGun(Gun gun) {
        gun.inscribeMod = Gun.InscribeMod.INSCRIBED;
        return gun;
    }

    private static Spade changeShovel(Shovel shovel) {
        Spade newShovel = new Spade();

        newShovel.level(0);
        newShovel.quantity(1);
        int level = shovel.trueLevel();
        if (level > 0) {
            newShovel.upgrade( level );
        } else if (level < 0) {
            newShovel.degrade( -level );
        }

        newShovel.enchantment = shovel.enchantment;
        newShovel.curseInfusionBonus = shovel.curseInfusionBonus;
        newShovel.masteryPotionBonus = shovel.masteryPotionBonus;
        newShovel.levelKnown = shovel.levelKnown;
        newShovel.cursedKnown = shovel.cursedKnown;
        newShovel.cursed = shovel.cursed;
        newShovel.augment = shovel.augment;
        newShovel.enchantHardened = shovel.enchantHardened;

        return newShovel;
    }

    private static Machete changeMachete(Machete machete) {
        EnhancedMachete newMachete = new EnhancedMachete();

        newMachete.level(0);
        newMachete.quantity(1);
        int level = machete.trueLevel();
        if (level > 0) {
            newMachete.upgrade( level );
        } else if (level < 0) {
            newMachete.degrade( -level );
        }

        newMachete.enchantment = machete.enchantment;
        newMachete.curseInfusionBonus = machete.curseInfusionBonus;
        newMachete.masteryPotionBonus = machete.masteryPotionBonus;
        newMachete.levelKnown = machete.levelKnown;
        newMachete.cursedKnown = machete.cursedKnown;
        newMachete.cursed = machete.cursed;
        newMachete.augment = machete.augment;
        newMachete.enchantHardened = machete.enchantHardened;

        return newMachete;
    }

    private static Item changeShield() {
        Item newItem;
        switch (Dungeon.hero.subClass) {
            case DEATHKNIGHT:
                newItem = new DeathSword();
                break;
            case HORSEMAN:
                newItem = new Saddle();
                break;
            case CRUSADER:
                newItem = new Rosary();
                break;
            default:
                newItem = null;
                break;
        }

        return newItem;
    }

    protected void onItemSelected(Item item) {
        Item result = changeItem(item);

        if (result == null){
            //This shouldn't ever trigger
            GLog.n( Messages.get(this, "nothing") );
            curItem.collect( curUser.belongings.backpack );
        } else {
            if (result != item) {
                int slot = Dungeon.quickslot.getSlot(item);
                if (item.isEquipped(Dungeon.hero)) {
                    item.cursed = false; //to allow it to be unequipped
                    if (item instanceof Artifact && result instanceof Ring){
                        //if we turned an equipped artifact into a ring, ring goes into inventory
                        ((EquipableItem) item).doUnequip(Dungeon.hero, false);
                        if (!result.collect()){
                            Dungeon.level.drop(result, curUser.pos).sprite.drop();
                        }
                    } else if (item instanceof KindOfWeapon && Dungeon.hero.belongings.secondWep() == item){
                        ((EquipableItem) item).doUnequip(Dungeon.hero, false);
                        ((KindOfWeapon) result).equipSecondary(Dungeon.hero);
                    } else {
                        ((EquipableItem) item).doUnequip(Dungeon.hero, false);
                        ((EquipableItem) result).doEquip(Dungeon.hero);
                    }
                    Dungeon.hero.spend(-Dungeon.hero.cooldown()); //cancel equip/unequip time
                } else {
                    item.detach(Dungeon.hero.belongings.backpack);
                    if (!result.collect()) {
                        Dungeon.level.drop(result, curUser.pos).sprite.drop();
                    } else if (Dungeon.hero.belongings.getSimilar(result) != null){
                        result = Dungeon.hero.belongings.getSimilar(result);
                    }
                }
                if (slot != -1
                        && result.defaultAction() != null
                        && !Dungeon.quickslot.isNonePlaceholder(slot)
                        && Dungeon.hero.belongings.contains(result)){
                    Dungeon.quickslot.setSlot(slot, result);
                }
            }
            if (result.isIdentified()){
                Catalog.setSeen(result.getClass());
            }

            onChangeComplete(item, result);
        }
    }

    protected void onChangeComplete(Item oldItem, Item newItem) {
        Sample.INSTANCE.play(Assets.Sounds.EVOKE);
        Dungeon.hero.sprite.operate(Dungeon.hero.pos);
        CellEmitter.center( Dungeon.hero.pos ).burst( Speck.factory( Speck.STAR ), 7 );
        new Flare( 6, 32 ).color(0xFFFF00, true).show( Dungeon.hero.sprite, 2f );
        Transmuting.show(Dungeon.hero, oldItem, newItem);
        Dungeon.hero.sprite.emitter().start(Speck.factory(Speck.CHANGE), 0.2f, 10);
        GLog.p( Messages.get(OldAmulet.class, "morph") );

        if (Dungeon.hero.belongings.contains(this)) {
            detach(Dungeon.hero.belongings.backpack);
        }
        Dungeon.hero.spendAndNext(Actor.TICK);
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
        return 2000;
    }

    protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

        @Override
        public String textPrompt() {
            return inventoryTitle();
        }

        @Override
        public Class<? extends Bag> preferredBag() {
            return Belongings.Backpack.class;
        }

        @Override
        public boolean itemSelectable(Item item) {
            if (!item.isIdentified()) return false;
            switch (Dungeon.hero.heroClass) {
                case WARRIOR: default:
                    return item instanceof BrokenSeal ||
                            (item instanceof Armor && ((Armor) item).checkSeal() != null);
                case MAGE:
                    return item instanceof MagesStaff;
                case ROGUE:
                    return item instanceof Ring;
                case HUNTRESS:
                    return item instanceof SpiritBow;
                case DUELIST:
                case SAMURAI:
                    return item instanceof MeleeWeapon && !(item instanceof MagesStaff) && !(item instanceof Gun);
                case GUNNER:
                    return item instanceof Gun;
                case ADVENTURER:
                    return item instanceof Machete || item instanceof Shovel;
                case KNIGHT:
                    return item instanceof KnightsShield;
            }
        }

        @Override
        public void onSelect( Item item ) {

            //FIXME this safety check shouldn't be necessary
            //it would be better to eliminate the curItem static variable.
            if (!(curItem instanceof OldAmulet)){
                return;
            }

            if (item != null && itemSelectable(item)) {
                switch (Dungeon.hero.heroClass) {
                    case DUELIST:
                        GameScene.show(new WndAbilitySelect((MeleeWeapon)item, abilityList.get(0), abilityList.get(1), abilityList.get(2)));
                        break;
                    case HUNTRESS:
                        GameScene.show(new WndBowSelect((SpiritBow) item));
                        break;
                    default:
                        onItemSelected(item);
                        break;
                }
            }
        }
    };

    public class WndAbilitySelect extends WndOptions {

        private MeleeWeapon wep;
        private ArrayList<Integer> ability = new ArrayList<>();

        public WndAbilitySelect(MeleeWeapon wep, int ability_1, int ability_2, int ability_3) {
            super(new ItemSprite(new HeroSword()),
                    Messages.titleCase(new HeroSword().name()),
                    Messages.get(HeroSword.class, "ability_select"),
                    new HeroSword(ability_1, wep).abilityName(),
                    new HeroSword(ability_2, wep).abilityName(),
                    new HeroSword(ability_3, wep).abilityName(),
                    Messages.get(HeroSword.class, "cancel"));
            ability.add(ability_1);
            ability.add(ability_2);
            ability.add(ability_3);
            this.wep = wep;
        }

        @Override
        protected void onSelect(int index) {
            if (index < 3) {
                HeroSword heroSword = new HeroSword(ability.get(index), wep);

                heroSword.level(0);
                heroSword.quantity(1);
                int level = wep.trueLevel();
                if (level > 0) {
                    heroSword.upgrade( level );
                } else if (level < 0) {
                    heroSword.degrade( -level );
                }

                heroSword.enchantment = wep.enchantment;
                heroSword.curseInfusionBonus = wep.curseInfusionBonus;
                heroSword.masteryPotionBonus = wep.masteryPotionBonus;
                heroSword.levelKnown = wep.levelKnown;
                heroSword.cursedKnown = wep.cursedKnown;
                heroSword.cursed = wep.cursed;
                heroSword.augment = wep.augment;
                heroSword.enchantHardened = wep.enchantHardened;

                int slot = Dungeon.quickslot.getSlot(wep);
                if (wep.isEquipped(Dungeon.hero)) {
                    wep.cursed = false; //to allow it to be unequipped
                    if (Dungeon.hero.belongings.secondWep() == wep){
                        wep.doUnequip(Dungeon.hero, false);
                        heroSword.equipSecondary(Dungeon.hero);
                    } else {
                        wep.doUnequip(Dungeon.hero, false);
                        heroSword.doEquip(Dungeon.hero);
                    }
                    Dungeon.hero.spend(-Dungeon.hero.cooldown()); //cancel equip/unequip time
                } else {
                    wep.detach(Dungeon.hero.belongings.backpack);
                    if (!heroSword.collect()) {
                        Dungeon.level.drop(heroSword, curUser.pos).sprite.drop();
                    } else if (Dungeon.hero.belongings.getSimilar(heroSword) != null){
                        heroSword = (HeroSword) Dungeon.hero.belongings.getSimilar(heroSword);
                    }
                }
                if (slot != -1
                        && heroSword.defaultAction() != null
                        && !Dungeon.quickslot.isNonePlaceholder(slot)
                        && Dungeon.hero.belongings.contains(heroSword)){
                    Dungeon.quickslot.setSlot(slot, heroSword);
                }

                onChangeComplete(wep, heroSword);
            } else {
                hide();
            }
        }

        @Override
        protected boolean hasInfo(int index) {
            return index < 3;
        }

        @Override
        protected void onInfo( int index ) {
            HeroSword heroSword = new HeroSword(ability.get(index), wep);
            if (wep.isIdentified()) {
                heroSword.level(wep.buffedLvl());
                heroSword.identify();
            }
            GameScene.show(new WndTitledMessage(
                    Icons.get(Icons.INFO),
                    Messages.titleCase(heroSword.abilityName()),
                    heroSword.abilityInfo()));
        }

    }

    public static SpiritBow bow1, bow2;

    public class WndBowSelect extends Window {
        private static final int WIDTH		= 120;
        private static final int BTN_SIZE	= 32;
        private static final int BTN_GAP	= 5;
        private static final int GAP		= 2;

        public SpiritBow curBow;
        public WndBowSelect(SpiritBow bow) {
            super();

            curBow = bow;
            if (bow1 == null || bow1.getClass() == bow.getClass()) {
                bow1 = Generator.randomBow(bow.getClass());
            }
            if (bow2 == null || bow2.getClass() == bow.getClass() ||
                    bow2.getClass() == bow1.getClass()) {
                bow2 = Generator.randomBow(bow.getClass());
            }
            bow1.clone(curBow);
            bow2.clone(curBow);

            IconTitle titlebar = new IconTitle();
            titlebar.icon(new ItemSprite(OldAmulet.this.image));
            titlebar.label(Messages.titleCase(OldAmulet.this.name()));
            titlebar.setRect(0, 0, WIDTH, 0);
            add( titlebar );

            String msg = Messages.get(this, "desc");
            RenderedTextBlock message = PixelScene.renderTextBlock( msg, 6 );
            message.maxWidth(WIDTH);
            message.setPos(0, titlebar.bottom() + GAP);
            add( message );

            ItemButton btnBow1 = new ItemButton() {
                @Override
                protected void onClick() {
                    if (Dungeon.hero.belongings.contains(curBow) && item() != null) {
                        GameScene.show(new RewardWindow(item()));
                    } else {
                        hide();
                    }
                }
            };
            btnBow1.item(bow1);
            btnBow1.setRect( (WIDTH - BTN_GAP) / 2 - BTN_SIZE, message.top() + message.height() + BTN_GAP, BTN_SIZE, BTN_SIZE );
            add( btnBow1 );

            ItemButton btnBow2 = new ItemButton() {
                @Override
                protected void onClick() {
                    if (Dungeon.hero.belongings.contains(curBow) && item() != null) {
                        GameScene.show(new RewardWindow(item()));
                    } else {
                        hide();
                    }
                }
            };
            btnBow2.item(bow2);
            btnBow2.setRect( btnBow1.right() + BTN_GAP, btnBow1.top(), BTN_SIZE, BTN_SIZE );
            add( btnBow2 );

            RedButton btnCancel = new RedButton(Messages.get(this, "no")) {
                @Override
                protected void onClick() {
                    hide();

                    GameScene.selectItem( itemSelector );
                }
            };
            btnCancel.setRect(0, btnBow2.bottom() + BTN_GAP, WIDTH, BTN_SIZE / 2);
            add( btnCancel );

            resize(WIDTH, (int) btnCancel.bottom());
        }

        @Override
        public void onBackPressed() {
            super.onBackPressed();

            GameScene.selectItem( itemSelector );
        }

        private void selectReward(Item newBow ) {

            if (newBow == null){
                return;
            }

            hide();

            int slot = Dungeon.quickslot.getSlot(curBow);

            curBow.detach( Dungeon.hero.belongings.backpack );
            if (newBow.doPickUp( Dungeon.hero )) {
                GLog.i( Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", newBow.name())) );

                if (newBow.defaultAction() != null &&
                        slot != -1 && !Dungeon.quickslot.isNonePlaceholder(slot)) {
                    Dungeon.quickslot.setSlot(slot, newBow);
                }
            } else {
                Dungeon.level.drop( newBow, Dungeon.hero.pos ).sprite.drop();
            }

            if (newBow.isIdentified()){
                Catalog.setSeen(newBow.getClass());
            }

            onChangeComplete(curBow, newBow);
        }

        private class RewardWindow extends WndInfoItem {

            public RewardWindow(Item item) {
                super(item);


                RedButton btnConfirm = new RedButton(Messages.get(WndSadGhost.class, "confirm")){
                    @Override
                    protected void onClick() {
                        RewardWindow.this.hide();

                        selectReward( item );
                    }
                };
                btnConfirm.setRect(0, height+2, width/2-1, 16);
                add(btnConfirm);

                RedButton btnCancel = new RedButton(Messages.get(WndSadGhost.class, "cancel")){
                    @Override
                    protected void onClick() {
                        hide();
                    }
                };
                btnCancel.setRect(btnConfirm.right()+2, height+2, btnConfirm.width(), 16);
                add(btnCancel);

                resize(width, (int)btnCancel.bottom());
            }
        }
    }

    public static class TempleCurse extends Buff {
        public void saySwitch(){
            GLog.i(Messages.get(this, "escape"));
        }
    }
}
