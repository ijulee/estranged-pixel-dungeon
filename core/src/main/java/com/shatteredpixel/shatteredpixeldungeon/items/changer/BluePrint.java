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
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Evolution;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.AssassinsBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Bible;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dirk;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Flail;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gauntlet;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Glaive;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatshield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.LargeKatana;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RoundShield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RunicBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Spear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WarHammer;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Whip;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.AR_T6;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.AssassinsSpear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.BeamSaber;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.ChainFlail;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.ChainWhip;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.DualDagger;
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
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.AR.AR_T5;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.GL.GL_T5;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.HG.HG_T5;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.RL.RL_T5;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.SR.SR_T5;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ForceCube;
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
import java.util.HashMap;


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
    public String name() {
        if (newWeapon == null) {
            return super.name();
        } else {
            return Messages.get(this, "crafted_name", newWeapon.trueName());
        }
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

    public static Recipe[] recipes = new Recipe[] {
            //Tier 2
            new Recipe(DualDagger.class,       new Class[] {Evolution.class, Dirk.class, MeleeWeapon.class},           0),
            //Tier 3
            new Recipe(SpearNShield.class,     new Class[] {Evolution.class, Spear.class, RoundShield.class},          0),
            //Tier 4
            new Recipe(ChainWhip.class,        new Class[] {Evolution.class, Whip.class, MeleeWeapon.class},           0),
            new Recipe(UnholyBible.class,      new Class[] {Evolution.class, Bible.class, MeleeWeapon.class},          0),
            //Tier 5
            new Recipe(UnformedBlade.class,    new Class[] {Evolution.class, AssassinsBlade.class, MeleeWeapon.class}, 0),
            new Recipe(ChainFlail.class,       new Class[] {Evolution.class, Whip.class, Flail.class},                 0),
            //Tier 6
            new Recipe(AR_T6.class,            new Class[] {Evolution.class, AR_T5.class, MeleeWeapon.class},          0),
            new Recipe(AssassinsSpear.class,   new Class[] {Evolution.class, AssassinsBlade.class, Glaive.class},      0),
            new Recipe(BeamSaber.class,        new Class[] {Evolution.class, Gauntlet.class, MeleeWeapon.class},       0),
            new Recipe(DualGreatSword.class,   new Class[] {Evolution.class, Greatsword.class, Greatsword.class},      0),
            new Recipe(ForceGlove.class,       new Class[] {Evolution.class, Gauntlet.class, ForceCube.class},         0),
            new Recipe(GL_T6.class,            new Class[] {Evolution.class, GL_T5.class, MeleeWeapon.class},          0),
            new Recipe(HG_T6.class,            new Class[] {Evolution.class, HG_T5.class, MeleeWeapon.class},          0),
            new Recipe(HugeSword.class,        new Class[] {Evolution.class, Greatsword.class, MeleeWeapon.class},     0),
            new Recipe(Lance.class,            new Class[] {Evolution.class, Glaive.class, MeleeWeapon.class},         0),
            new Recipe(MeisterHammer.class,    new Class[] {Evolution.class, WarHammer.class, MeleeWeapon.class},      0),
            new Recipe(ObsidianShield.class,   new Class[] {Evolution.class, Greatshield.class, MeleeWeapon.class},    0),
            new Recipe(RL_T6.class,            new Class[] {Evolution.class, RL_T5.class, MeleeWeapon.class},          0),
            new Recipe(SharpKatana.class,      new Class[] {Evolution.class, LargeKatana.class, MeleeWeapon.class},    0),
            new Recipe(SR_T6.class,            new Class[] {Evolution.class, SR_T5.class, MeleeWeapon.class},          0),
            new Recipe(TrueRunicBlade.class,   new Class[] {Evolution.class, RunicBlade.class, MeleeWeapon.class},     0),
            //Tier 7
            new Recipe(HolySword.class,        new Class[] {Evolution.class, HugeSword.class, Bible.class},            0),
            new Recipe(LanceNShield.class,     new Class[] {Evolution.class, Lance.class, ObsidianShield.class},       0),
            new Recipe(TacticalShield.class,   new Class[] {Evolution.class, ObsidianShield.class, HG_T6.class},       0)
    };

    public static HashMap<Class<?extends MeleeWeapon>, Recipe> recipeMap = new HashMap<>();
    static {
        for (Recipe recipe : recipes) {
            recipeMap.put(recipe.output, recipe);
        }
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe {
        public Class<?extends MeleeWeapon> output;
        public Class<?extends Item>[] requirements;
        public int cost;

        public Recipe() {
            super();
        }

        public Recipe(Class<?extends MeleeWeapon> output, Class<?extends Item>[] requirements, int cost) {
            this.output = output;
            this.requirements = requirements;
            this.cost = cost;
        }

        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            ArrayList<Item> inList = new ArrayList<>(ingredients);
            boolean[] matches = new boolean[requirements.length];
            while(!inList.isEmpty()) {
                Item in = inList.remove(0);
                for (int k = 0; k < matches.length; k++) {
                    if (!matches[k] && requirements[k].isAssignableFrom(in.getClass())) {
                        matches[k] = true;
                        break;
                    }
                }
            }

            for (boolean req : matches) {
                if (!req) return false;
            }
            return true;
        }

        @Override
        public int cost(ArrayList<Item> ingredients) {
            return cost;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            for (Item in : ingredients) {
                in.quantity(in.quantity()-1);
            }

            return sampleOutput(ingredients);
        }

        @Override
        public Item sampleOutput(ArrayList<Item> ingredients) {
            Item result = new BluePrint(Reflection.newInstance(output));

            for (Item in : ingredients) {
                if (in.isIdentified()) {
                    result.upgrade(in.level());
                }
            }

            return result;
        }
    }
}
