package com.shatteredpixel.shatteredpixeldungeon.items.changer;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Transmuting;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.AR_T6;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.AlchemyWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.AssassinsSpear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.BeamSaber;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.ChainFlail;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.ChainWhip;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.DualGreatSword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.ForceGlove;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.GL_T6;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.HG_T6;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.HolySword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.HugeSword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.Lance;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.LanceNShield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.MeisterHammer;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.ObsidianShield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.RL_T6;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.SR_T6;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.SharpKatana;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.SpearNShield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.TacticalShield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.TrueRunicBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.UnformedBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.UnholyBible;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndChanger;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;

public class BluePrint extends Item {

    private static final String AC_USE		= "USE";

    {
        image = ItemSpriteSheet.BLUEPRINT;
        defaultAction = AC_USE;
        stackable = false;
        levelKnown = true;

        unique = true;
        bones = false;
    }

    private MeleeWeapon newWeapon;

    public BluePrint(MeleeWeapon wep) {
        this.newWeapon = wep;
    }

    public BluePrint() {}

    private static final String NEW_WEAPON	= "newWeapon";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( NEW_WEAPON, newWeapon );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        newWeapon = (MeleeWeapon) bundle.get( NEW_WEAPON );
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

    public void reShowSelector () {
        GameScene.selectItem( itemSelector );
    }

    /*private Item changeItem( Item item ){
        if (item instanceof MeleeWeapon) {
            return changeWeapon((MeleeWeapon) item);
        } else {
            return null;
        }
    }*/

    private MeleeWeapon changeWeapon(MeleeWeapon wep) {
        MeleeWeapon result = this.newWeapon;

        result.level(0);
        result.quantity(1);
        int level = wep.trueLevel();
        if (level > 0) {
            result.upgrade( level );
        } else if (level < 0) {
            result.degrade( -level );
        }

        if (wep instanceof Gun && result instanceof Gun) {
            ((Gun) result).copyGunMods((Gun) wep);
            ((Gun) result).inscribeMod = ((Gun) wep).inscribeMod;
        }

        result.enchantment = wep.enchantment;
        result.curseInfusionBonus = wep.curseInfusionBonus;
        result.masteryPotionBonus = wep.masteryPotionBonus;
        result.levelKnown = wep.levelKnown;
        result.cursedKnown = wep.cursedKnown;
        result.cursed = wep.cursed;
        result.augment = wep.augment;
        result.enchantHardened = wep.enchantHardened;

        return result;

    }
    
    private String inventoryTitle(){
        return Messages.get(this, "inv_title");
    }

    @Override
    public String desc() {
        String desc = super.desc();
        if (this.newWeapon != null) {
            desc += "\n\n" + Messages.get(this, "item_desc",
                    newWeapon.tier,
                    newWeapon.trueName(),
                    Math.min(100, 100-20*(newWeapon.tier-1)+10*this.level()),
                    Math.min(100, 100-20*(newWeapon.tier-2)+10*this.level()),
                    Math.min(100, 100-20*(newWeapon.tier-3)+10*this.level()),
                    Math.min(100, 100-20*(newWeapon.tier-4)+10*this.level()),
                    Math.min(100, 100-20*(newWeapon.tier-5)+10*this.level()));
        }

        return desc;
    }

    public float transmuteChance(MeleeWeapon weapon) {
        return Math.min(1, 1 - 0.2f * (newWeapon.tier - weapon.tier) + 0.1f * this.level());
    }

    public void onItemSelected(Item item) {
        MeleeWeapon original = (MeleeWeapon) item;
        MeleeWeapon result;

        if (Random.Float() < transmuteChance(original) || DeviceCompat.isDebug()) {
            result = changeWeapon(original);

            if (result != original) {
                int slot = Dungeon.quickslot.getSlot(original);

                if (original.isEquipped(Dungeon.hero)) {
                    original.cursed = false; //to allow it to be unequipped
                    if (Dungeon.hero.belongings.secondWep() == original){
                        original.doUnequip(Dungeon.hero, false);
                        result.equipSecondary(Dungeon.hero);
                    } else {
                        original.doUnequip(Dungeon.hero, false);
                        result.doEquip(Dungeon.hero);
                    }
                    Dungeon.hero.spend(-Dungeon.hero.cooldown()); //cancel equip/unequip time
                } else {
                    item.detach(Dungeon.hero.belongings.backpack);
                    if (!result.collect()) {
                        Dungeon.level.drop(result, curUser.pos).sprite.drop();
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

            Sample.INSTANCE.play(Assets.Sounds.READ);
            Dungeon.hero.spendAndNext(Actor.TICK);
            Transmuting.show(curUser, original, result);
            curUser.sprite.emitter().start(Speck.factory(Speck.CHANGE), 0.2f, 10);

            GLog.p( Messages.get(this, "morph") );
        } else {
            GLog.n( Messages.get(this, "nothing") );
        }

        detach(Dungeon.hero.belongings.backpack);

        Catalog.countUse(getClass());
    }

    @Override
    public boolean isUpgradable() {
        return true;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int value() {
        return -1;
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
            return item instanceof MeleeWeapon;
        }

        @Override
        public void onSelect( Item item ) {

            //FIXME this safety check shouldn't be necessary
            //it would be better to eliminate the curItem static variable.
            if (!(curItem instanceof BluePrint)){
                return;
            }

            if (itemSelectable(item)) {
                GameScene.show(new WndChanger(BluePrint.this, item, BluePrint.this.newWeapon));
                //onItemSelected(item);
            }
        }
    };

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe {
        static {
            // ensures that RecipeInfo is initialized
            validIngredients = RecipeInfo.getIngredientsList();
        }

        public enum RecipeInfo {
            TRUE_RUNIC_BLADE(new TrueRunicBlade(), 0),
            LANCE(new Lance(), 0 ),
            OBSIDIAN_SHIELD(new ObsidianShield(), 0 ),
            CHAIN_WHIP(new ChainWhip(), 0 ),
            LANCE_N_SHIELD(new LanceNShield(), 0 ),
            CHAIN_FLAIL(new ChainFlail(), 0 ),
            UNFORMED_BLADE(new UnformedBlade(), 0 ),
            AR_T6(new AR_T6(), 0 ),
            SR_T6(new SR_T6(), 0 ),
            HG_T6(new HG_T6(), 0 ),
            SPEAR_N_SHIELD(new SpearNShield(), 0 ),
            TACTICAL_SHIELD(new TacticalShield(), 0 ),
            ASSASSINS_SPEAR(new AssassinsSpear(), 0 ),
            FORCE_GLOVE(new ForceGlove(), 0 ),
            UNHOLY_BIBLE(new UnholyBible(), 0 ),
            HUGE_SWORD(new HugeSword(), 0 ),
            MEISTER_HAMMER(new MeisterHammer(), 0 ),
            BEAM_SABER(new BeamSaber(), 0 ),
            HOLY_SWORD(new HolySword(), 0 ),
            DUAL_GREATSWORD(new DualGreatSword(), 0 ),
            SHARP_KATANA(new SharpKatana(), 0 ),
            GL_T6(new GL_T6(), 0 ),
            RL_T6(new RL_T6(), 0 );
            public final Class<? extends MeleeWeapon> outputClass;
            public final ArrayList<Class<? extends Item>> ingredients;
            public final int brewCost;

            RecipeInfo(MeleeWeapon weapon, int cost) {
                this.outputClass = weapon.getClass();
                this.ingredients = ((AlchemyWeapon) weapon).weaponRecipe();
                this.brewCost = cost;
            }

            public static ArrayList<ArrayList<Class<?extends Item>>> getIngredientsList () {
                ArrayList<RecipeInfo> recipeList = new ArrayList<>(Arrays.asList(RecipeInfo.values()));

                ArrayList<ArrayList<Class<?extends Item>>> ingredientsList = new ArrayList<>();
                for (RecipeInfo recipe : recipeList) {
                    ingredientsList.add(recipe.ingredients);
                }

                return ingredientsList;
            }
        }

        public static ArrayList<ArrayList<Class<?extends Item>>> validIngredients;

        public static RecipeInfo ingredientsGetRecipe(ArrayList<Item> ingredients) {
            ArrayList<Class <? extends Item>> ingredientsClassList = ingredientsGetClass(ingredients);

            int index = 0;
            for (ArrayList<Class<? extends Item>> a : validIngredients) {
                if (ingredientsClassList.containsAll(a) && a.containsAll(ingredientsClassList)) {
                    return RecipeInfo.values()[index];
                } else {
                    index++;
                }
            }

            return null; // no valid recipe
        }

        public static ArrayList<Class<? extends Item>> ingredientsGetClass(ArrayList<Item> ingredients) {

            ArrayList<Class<? extends Item>> ingredientsClassList = new ArrayList<>();

            for (Item i : ingredients) {
                ingredientsClassList.add(i.getClass());
            }

            return ingredientsClassList;
        }

        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            return ingredientsGetRecipe(ingredients) != null;
        }

        @Override
        public int cost(ArrayList<Item> ingredients) {
            RecipeInfo recipe = ingredientsGetRecipe(ingredients);
            return (recipe != null) ? recipe.brewCost : 0;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            for (Item i : ingredients) {

                i.quantity(i.quantity()-1);
            }

            return sampleOutput(ingredients);
        }

        @Override
        public Item sampleOutput(ArrayList<Item> ingredients) {
            RecipeInfo recipe = ingredientsGetRecipe(ingredients);
            Item result = null;

            if (recipe != null) {
                result = new BluePrint(Reflection.newInstance(recipe.outputClass));
            }

            int outputLevel = 0;
            for (Item i : ingredients) {
                if (i instanceof MeleeWeapon && i.isIdentified()) {
                    outputLevel += i.level();
                }
            }

            if (result != null) {
                result.level(outputLevel);
            }

            return result;
        }
    }

}
