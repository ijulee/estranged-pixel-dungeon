package com.shatteredpixel.shatteredpixeldungeon.items.trinkets;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Necklace extends Trinket {
    {
        image = ItemSpriteSheet.NECKLACE;
    }

    private static final String AC_INSERT = "INSERT";

    private static final LinkedHashMap<Integer, Integer> gems = new LinkedHashMap<>() {
        {
            put(ItemSpriteSheet.RING_GARNET     , ItemSpriteSheet.NECKLACE_GARNET);
            put(ItemSpriteSheet.RING_RUBY       , ItemSpriteSheet.NECKLACE_RUBY);
            put(ItemSpriteSheet.RING_TOPAZ      , ItemSpriteSheet.NECKLACE_TOPAZ);
            put(ItemSpriteSheet.RING_EMERALD    , ItemSpriteSheet.NECKLACE_EMERALD);
            put(ItemSpriteSheet.RING_ONYX       , ItemSpriteSheet.NECKLACE_ONYX);
            put(ItemSpriteSheet.RING_OPAL       , ItemSpriteSheet.NECKLACE_OPAL);
            put(ItemSpriteSheet.RING_TOURMALINE , ItemSpriteSheet.NECKLACE_TOURMALINE);
            put(ItemSpriteSheet.RING_SAPPHIRE   , ItemSpriteSheet.NECKLACE_SAPPHIRE);
            put(ItemSpriteSheet.RING_AMETHYST   , ItemSpriteSheet.NECKLACE_AMETHYST);
            put(ItemSpriteSheet.RING_QUARTZ     , ItemSpriteSheet.NECKLACE_QUARTZ);
            put(ItemSpriteSheet.RING_AGATE      , ItemSpriteSheet.NECKLACE_AGATE);
            put(ItemSpriteSheet.RING_DIAMOND    , ItemSpriteSheet.NECKLACE_DIAMOND);
        }
    };

    private Ring ring = null;

    @Override
    protected int upgradeEnergyCost() {
        //6 -> 15(21) -> 23(44) -> 31(75)
        return 15+8*level();
    }

    @Override
    public String desc() {
        if (ring == null) {
            return Messages.get(this, "desc");
        } else {
            return Messages.get(this, "desc_ring");
        }
    }

    @Override
    public String statsDesc() {
        String stats;
        if (ring == null) {
            stats = Messages.get(this, "stats_desc");
        } else {
            stats = Messages.get(this, "stats_desc_ring", buffedLvl(), ring.trueName(), ring.statsInfo());
        }
        return stats + "\n\n" + Messages.get(this, "stats_desc_cost");
    }

    @Override
    public String defaultAction() {
        if (ring instanceof RingOfForce &&
                Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.DUELIST) {
            return RingOfForce.AC_ABILITY;
        } else {
            return super.defaultAction();
        }
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_INSERT);
        if (ring instanceof RingOfForce && hero.heroClass == HeroClass.DUELIST) {
            actions.add(RingOfForce.AC_ABILITY);
        }
        return actions;
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(RingOfForce.AC_ABILITY)){
            return Messages.upperCase(Messages.get(RingOfForce.class, "ability_name"));
        } else {
            return super.actionName(action, hero);
        }
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_INSERT)) {
            GameScene.selectItem(itemSelector);
        } else if (action.equals(RingOfForce.AC_ABILITY)) {
            if (hero.buff(RingOfForce.BrawlersStance.class) != null){
                if (!hero.buff(RingOfForce.BrawlersStance.class).active){
                    hero.buff(RingOfForce.BrawlersStance.class).reset();
                } else {
                    hero.buff(RingOfForce.BrawlersStance.class).active = false;
                }
                BuffIndicator.refreshHero();
            } else {
                Buff.affect(hero, RingOfForce.BrawlersStance.class).reset();
            }
            AttackIndicator.updateState();
            hero.sprite.operate(hero.pos);
        }
    }

    public void setRing(Ring newRing) {
        if (ring!=null) ring.deactivate();

        ring = newRing;
        if (ring != null) {
            ring.level(buffedLvl());
            if (Dungeon.hero.belongings.contains(this)) {
                ring.activate( Dungeon.hero );
            }
            image = gems.get(ring.image());
        } else {
            image = ItemSpriteSheet.NECKLACE;
        }

        Dungeon.hero.updateHT( false );

    }

    private static final String IMAGE	= "image";
    private static final String RING	= "ring";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(IMAGE, image());
        bundle.put(RING, ring);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        image = bundle.getInt(IMAGE);
        ring = (Ring) bundle.get(RING);
        if (ring != null) ring.level(buffedLvl());
    }

    @Override
    public boolean collect(Bag container) {
        if (Dungeon.hero != null && ring != null) {
            ring.activate(Dungeon.hero);
            Dungeon.hero.updateHT(false);
        }
        return super.collect(container);
    }

    @Override
    protected void onDetach() {
        if (Dungeon.hero != null && ring != null) {
            ring.deactivate();
            if (Dungeon.hero.buff(RingOfForce.BrawlersStance.class) != null &&
                    Dungeon.hero.buff(RingOfForce.Force.class) == null) {
                //clear brawler's stance if no ring of force is equipped
                Dungeon.hero.buff(RingOfForce.BrawlersStance.class).active = false;
            }
            Dungeon.hero.updateHT(false);
        }
        super.onDetach();
    }

    @Override
    public Item upgrade() {
        super.upgrade();
        if (ring != null) ring.level(buffedLvl());
        return this;
    }

    public static Ring getNecklaceRing() {
        if (Dungeon.hero != null) {
            Necklace n = Dungeon.hero.belongings.getItem(Necklace.class);
            if (n != null && n.ring != null) {
                return n.ring;
            }
        }
        return null;
    }

    protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

        @Override
        public String textPrompt() {
            return Messages.get(Necklace.class, "inv_title");
        }

        @Override
        public Class<? extends Bag> preferredBag() {
            return Belongings.Backpack.class;
        }

        @Override
        public boolean itemSelectable(Item item) {
            return item instanceof Ring && item.isIdentified() && !item.cursed && !item.isEquipped(Dungeon.hero);
        }

        @Override
        public void onSelect( Item item ) {
            if (item != null) {
                GameScene.show(new WndInsert(Necklace.this, (Ring) item));
            }
        }
    };

    public static class WndInsert extends WndOptions {

        Necklace necklace;
        Ring ring;

        public WndInsert(Necklace necklace, Ring ring){
            super(new ItemSprite(necklace),
                    Messages.titleCase(new Necklace().name()),
                    Messages.get(Necklace.class, "insert_desc", ring.trueName()),
                    Messages.get(Necklace.class, "insert_ok"),
                    Messages.get(Necklace.class, "insert_cancel"));
            this.necklace = necklace;
            this.ring = ring;
        }

        @Override
        protected void onSelect(int index) {
            if (index == 0) {
                necklace.setRing(ring);

                ring.detach(Dungeon.hero.belongings.backpack);
                updateQuickslot();

                evoke( Dungeon.hero );
                Sample.INSTANCE.play(Assets.Sounds.EVOKE);
                Dungeon.hero.spendAndNext(Actor.TICK);
                Dungeon.hero.sprite.operate(Dungeon.hero.pos);
            }

            hide();
        }

    }
}
