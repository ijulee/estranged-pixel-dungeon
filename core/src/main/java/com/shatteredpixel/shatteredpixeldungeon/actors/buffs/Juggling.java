package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import static com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton.lastTarget;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.bow.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.bow.BowWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class Juggling extends TargetingAction {
    {
        type = buffType.NEUTRAL;
    }

    public static final String TXT_STACK = "%d/%d";

    private LinkedList<MissileWeapon> weapons = new LinkedList<>();

    private int maxWeapons() {
        return 3 + ((Hero) target).pointsInTalent(Talent.SKILLFUL_JUGGLING);
    }

    public void juggle(MissileWeapon wep, boolean useTurn) {
        Hero hero = ((Hero) target);

        weapons.offer(wep);
        if (weapons.size() > maxWeapons()) {
            MissileWeapon lastWep = weapons.poll();
            if (lastWep != null && !(lastWep instanceof BowWeapon.Arrow)) {
                if(lastWep.doPickUp(hero, hero.pos)) {
                    hero.spend(-lastWep.pickupDelay());
                } else {
                    Dungeon.level.drop(lastWep, hero.pos).sprite.drop();
                }
            }
        }
        hero.sprite.zap(hero.pos);
        Sample.INSTANCE.play(Assets.Sounds.MISS);

        if (useTurn) {
            hero.spendAndNext(Math.max(0, 1f - hero.pointsInTalent(Talent.SWIFT_JUGGLING)/3f));
        }

        ActionIndicator.setAction(this);
    }

    @Override
    public void detach() {
        for (MissileWeapon weapon : weapons) {
            if (!(weapon instanceof BowWeapon.Arrow)) {
                Dungeon.level.drop(weapon, target.pos);
            }
        }

        ActionIndicator.clearAction();

        super.detach();
    }

    @Override
    public boolean act() {
        if (weapons.isEmpty()) {
            detach();
        }

        spend(TICK);

        return true;
    }

    @Override
    public String desc() {
        StringBuilder sb = new StringBuilder();
        Iterator<MissileWeapon> iterator = weapons.iterator();
        while (iterator.hasNext()) {
            MissileWeapon weapon = iterator.next();

            sb.append(weapon.name());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        return Messages.get(this, "desc", sb.toString());
    }

    @Override
    public int icon() {
        return BuffIndicator.JUGGLING;
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }

    @Override
    public int actionIcon() {
        return HeroIcon.JUGGLING;
    }

    @Override
    public Visual secondaryVisual() {
        BitmapText txt = new BitmapText(PixelScene.pixelFont);
        txt.text(Messages.format(TXT_STACK, weapons.size(), maxWeapons()));
        txt.hardlight(CharSprite.POSITIVE);
        txt.measure();
        return txt;
    }

    @Override
    public int indicatorColor() {
        if (weapons.size() == maxWeapons())
            return 0xE8E8E8;
        else
            return 0xB3B3B3;
    }

    @Override
    public void doAction() {
        if (!GameScene.isCellSelecterActive(shooter)) {
            showCross();

            GameScene.selectCell(shooter);
        } else {
            if (canAutoAim(lastTarget)) {
                int cell = QuickSlotButton.autoAim(lastTarget);
                if (cell != -1) {
                    GameScene.handleCell(cell);
                } else {
                    //couldn't auto-aim, just target the position and hope for the best.
                    GameScene.handleCell(lastTarget.pos);
                }
            }
        }
    }

    private static boolean canAutoAim(Char ch) {
        return ch != null &&
                ch.isAlive() && ch.isActive() &&
                ch.alignment != Char.Alignment.ALLY &&
                Dungeon.hero.fieldOfView[ch.pos];
    }

    private static final String WEAPONS = "weapons";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);

        bundle.put(WEAPONS, weapons);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);

        for (Bundlable item : bundle.getCollection( WEAPONS )) {
            if (item != null){
                weapons.add((MissileWeapon) item);
            }
        }
        ActionIndicator.setAction(this);
    }

    private final CellSelector.Listener shooter = new CellSelector.Listener() {

        @Override
        public void onSelect(Integer cell) {
            if (cell != null) {
                Hero hero = (Hero) target;

                if (cell != -1) {
                    JugglingTracker tracker = Buff.affect(hero, JugglingTracker.class, 0f);
                    tracker.weapons = new HashSet<>(Juggling.this.weapons);
                    while (!weapons.isEmpty()) {
                        MissileWeapon wep = weapons.poll();
                        if (wep.STRReq() <= hero.STR()) {
                            wep.cast(hero, cell);
                        } else {
                            if (!(wep instanceof BowWeapon.Arrow)) {
                                Dungeon.level.drop(wep, hero.pos);
                            }
                        }
                    }

                    removeCross();
                    hero.spend(TICK);
                    detach();
                }
            }
        }

        @Override
        public String prompt() {
            return Messages.get(SpiritBow.class, "prompt");
        }
    };

    public static float accuracyFactor(Hero hero) {
        if (hero.buff(Juggling.class) != null) {
            return 0.5f + 0.2f*Dungeon.hero.pointsInTalent(Talent.FOCUS_MAINTAIN);
        } else {
            return 1;
        }
    }

    public static BowWeapon getBow() {
        BowWeapon bow;
        if (!(Dungeon.hero.belongings.weapon() instanceof BowWeapon)) {
            // create a bow based on hero strength
            bow = new BowWeapon();
            bow.tier = Math.max(0, (Dungeon.hero.STR() - 10) / 2 + 1);
        } else {
            bow = (BowWeapon) Dungeon.hero.belongings.weapon();
        }
        return bow;
    }

    public static void onKill() {
        if (Dungeon.hero.subClass == HeroSubClass.JUGGLER && Dungeon.bullet > 1 && Dungeon.hero.hasTalent(Talent.HABITUAL_HAND)) {
            for (int i = 0; i < Dungeon.hero.pointsInTalent(Talent.HABITUAL_HAND); i++) {
                if (Dungeon.bullet <= 0) break;
                BowWeapon.Arrow arrow = getBow().getMissile();
                Buff.affect(Dungeon.hero, Juggling.class).juggle(arrow, false);
            }
            Item.updateQuickslot();
        }
    }

    public static void onMove() {
        if (Dungeon.bullet >= 1 &&
                Random.Float() < 0.01f*Dungeon.hero.pointsInTalent(Talent.TOUR_PERFORMANCE)) {

            BowWeapon.Arrow arrow = getBow().getMissile();
            Buff.affect(Dungeon.hero, Juggling.class).juggle(arrow, false);
            Item.updateQuickslot();
        }
    }

    public static class JugglingTracker extends FlavourBuff {
        public HashSet<MissileWeapon> weapons;
    }
}
