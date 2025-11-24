package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
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
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class Juggling extends Buff implements ActionIndicator.Action {
    {
        type = buffType.NEUTRAL;
    }

    private final static Image cross = Icons.TARGET.get();
    private static Char lastTarget = null;
    Queue<MissileWeapon> weapons = new LinkedList<>();

    @Override
    public int icon() {
        return BuffIndicator.JUGGLING;
    }

    private int maxWeapons() {
        return 3 + ((Hero) target).pointsInTalent(Talent.SKILLFUL_JUGGLING);
    }

    public void juggle(MissileWeapon wep, boolean useTurn) {
        Hero hero = ((Hero) target);

        weapons.offer(wep);
        if (weapons.size() > maxWeapons()) {
            MissileWeapon lastWep = weapons.poll();
            if (lastWep != null) {
                if(lastWep.doPickUp(hero, hero.pos)) {
                    hero.spend(-lastWep.pickupDelay());
                    if (!(lastWep instanceof BowWeapon.Arrow)) {
                        GLog.i(Messages.capitalize(Messages.get(hero, "you_now_have", lastWep.name())));
                    }
                } else {
                    Dungeon.level.drop(lastWep, hero.pos).sprite.drop();
                    GLog.newLine();
                    GLog.i(Messages.capitalize(Messages.get(hero, "you_cant_have", lastWep.name())));
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
            if (weapon instanceof BowWeapon.Arrow) {
                BowWeapon.dropArrow(target.pos);
            } else {
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
        txt.text(String.format("%d/%d", weapons.size(), maxWeapons()));
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
            if (canAutoAim(lastTarget)) {
                CharSprite sprite = lastTarget.sprite;
                if (sprite != null && sprite.parent != null) {
                    sprite.parent.addToFront(cross);
                    cross.point(sprite.center(cross));
                }
            }

            GameScene.selectCell(shooter);
        } else {
            if (canAutoAim(lastTarget)) {
                int cell = QuickSlotButton.autoAim(lastTarget);
                if (cell == -1) return;
                shooter.onSelect(cell);
            }
        }
    }

    private static boolean canAutoAim(Char ch) {
        return ch != null &&
                ch.isAlive() && ch.isActive() &&
                ch.alignment != Char.Alignment.ALLY &&
                Dungeon.hero.fieldOfView[ch.pos];
    }

    public static void target( Char target ) {
        if (target != null && target.alignment != Char.Alignment.ALLY) {
            lastTarget = target;

            Juggling j = Dungeon.hero.buff(Juggling.class);
            if (j != null && GameScene.isCellSelecterActive(j.shooter)) {
                CharSprite sprite = lastTarget.sprite;
                if (sprite.parent != null) {
                    sprite.parent.addToFront(cross);
                    cross.point(sprite.center(cross));
                }
            }
        }
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
                    while (!weapons.isEmpty()) {
                        MissileWeapon wep = weapons.poll();
                        if (wep.STRReq() <= hero.STR()) {
                            int dst = wep.throwPos(hero, cell);
                            wep.cast(hero, dst, false, 0, new Callback() {
                                @Override
                                public void call() {
                                    if (hero.hasTalent(Talent.FANCY_PERFORMANCE)) {
                                        Char ch = Actor.findChar(dst);
                                        if (ch != null && ch.alignment == Char.Alignment.ENEMY ||
                                                (ch instanceof Mimic && ch.alignment == Char.Alignment.NEUTRAL)) {
                                            lastTarget = ch;
                                            Dungeon.level.drop(
                                                    new Gold(5 * hero.pointsInTalent(Talent.FANCY_PERFORMANCE)), dst)
                                                    .sprite.drop();
                                        }
                                    }
                                }
                            });
                        } else {
                            if (wep instanceof BowWeapon.Arrow) {
                                BowWeapon.dropArrow(hero.pos);
                            } else {
                                Dungeon.level.drop(wep, hero.pos);
                            }
                        }
                    }

                    hero.spend(TICK);
                    detach();
                }
            }

            cross.remove();
        }

        @Override
        public String prompt() {
            return Messages.get(SpiritBow.class, "prompt");
        }
    };

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

    public static float accuracyFactor(Hero hero) {
        if (hero.buff(Juggling.class) != null) {
            return 0.5f + 0.2f*Dungeon.hero.pointsInTalent(Talent.FOCUS_MAINTAIN);
        } else {
            return 1;
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
}
