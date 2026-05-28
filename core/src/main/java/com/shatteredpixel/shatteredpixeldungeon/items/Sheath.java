package com.shatteredpixel.shatteredpixeldungeon.items;

import static com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton.lastTarget;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TargetingAction;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroAction;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.bow.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class Sheath extends Item {

    public static final String AC_USE	= "USE";

    {
        image = ItemSpriteSheet.SHEATH;
        defaultAction = AC_USE;

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
        if (action.equals( AC_USE )) {
            if (hero.belongings.weapon() instanceof MeleeWeapon) {
                if (hero.buff(Sheathing.class) == null) {
                    Buff.affect(hero, Sheathing.class);
                    Talent.DrawingMasteryTracker mastery;
                    if (hero.buff(Talent.LethalFocusTracker.class) != null) {
                        hero.buff(Talent.LethalFocusTracker.class).detach();
                    } else if ((mastery = hero.buff(Talent.DrawingMasteryTracker.class)) != null &&
                        mastery.ready()) {
                        mastery.resetCooldown();
                    } else if (hero.buff(Talent.PreparedMealTracker.class) != null) {
                        hero.buff(Talent.PreparedMealTracker.class).use();
                    } else {
                        hero.spendAndNext(Actor.TICK);
                    }
                    Dungeon.observe();
                    GameScene.updateFog();
                    hero.checkVisibleMobs();
                } else {
                    hero.buff(Sheathing.class).detach();
                    hero.spendAndNext(Actor.TICK);
                }
            } else {
                GLog.w(Messages.get(this, "no_weapon"));
            }
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
        return -1;
    }

    public static boolean isQuickDraw() {
        return  Dungeon.hero.subClass == HeroSubClass.MASTER &&
                Dungeon.hero.belongings.attackingWeapon() instanceof MeleeWeapon &&
                Dungeon.hero.buff(Sheathing.class) != null &&
                Dungeon.hero.buff(QuickDrawCooldown.class) == null &&
                Dungeon.hero.buff(DashDrawTracker.class) == null;
    }

    public static boolean isSpecialDraw() {
        return Dungeon.hero.buff(QuickDrawTracker.class) != null ||
                Dungeon.hero.buff(DashDrawTracker.class) != null;
    }

    public static class Sheathing extends TargetingAction {
        {
            type = buffType.POSITIVE;
            announced = true;
        }

        public int pos = -1;

        @Override
        public boolean attachTo(Char target) {
            if (super.attachTo(target)){
                if (Dungeon.hero != null) {
                    Dungeon.observe();

                    if (Dungeon.hero.subClass == HeroSubClass.MASTER &&
                            Dungeon.hero.buff(DashDrawCooldown.class) == null) {
                        ActionIndicator.setAction(this);
                    }
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void detach() {
            super.detach();

            if (Dungeon.hero != null) {
                Dungeon.observe();
                GameScene.updateFog();
                removeCross();
                ActionIndicator.clearAction(this);
            }
        }

        @Override
        public boolean act() {
            // detach if hero has moved, has no weapon, or has no sheath
            if (pos == -1) pos = target.pos;
            if (pos != target.pos || Dungeon.hero.belongings.weapon() == null ||
                    Dungeon.hero.belongings.getItem(Sheath.class) == null) {
                detach();
                return true;
            }

            spend( target.cooldown() );

            if (Dungeon.hero.subClass == HeroSubClass.MASTER) {
                if (target.buff(DashDrawCooldown.class) == null) {
                    ActionIndicator.setAction(this);
                } else {
                    ActionIndicator.clearAction(this);
                }
            }
            return true;
        }

        @Override
        public String desc() {
            String desc;
            if (Dungeon.hero.subClass == HeroSubClass.MASTER) {
                Hero.testQuickDraw = true;
                float specialDrawCrit = Dungeon.hero.critChance((Weapon) Dungeon.hero.belongings.weapon());
                Hero.testQuickDraw = false;
                desc = Messages.get(this, "desc_master", 100f * specialDrawCrit);
            } else {
                desc = super.desc();
            }
            return desc;
        }

        @Override
        public String actionName() {
            return Messages.get(this, "action");
        }

        @Override
        public int actionIcon() {
            return HeroIcon.DASH_DRAW;
        }

        @Override
        public int indicatorColor() {
            return 0x88CCFF;
        }

        @Override
        public void doAction() {
            Sheath s = Dungeon.hero.belongings.getItem(Sheath.class);
            if (s == null) {
                detach();
            }

            if (!GameScene.isCellSelecterActive( attack )) {
                showCross();

                GameScene.selectCell( attack );
            } else {
                if (canAutoAim(lastTarget)) {
                    GameScene.handleCell(lastTarget.pos);
                }
            }
        }

        private boolean canAutoAim(Char lastTarget) {
            return  lastTarget != null &&
                    lastTarget.isAlive() && lastTarget.isActive() &&
                    lastTarget.alignment != Char.Alignment.ALLY &&
                    !Dungeon.level.adjacent(Dungeon.hero.pos, lastTarget.pos) &&
                    Dungeon.hero.fieldOfView[lastTarget.pos];
        }

        private static final String POS = "pos";
        private static final String CAN_DASH = "canDash";
        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put( POS, pos );

            bundle.put( CAN_DASH, ActionIndicator.action == this );
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            pos = bundle.getInt( POS );

            if (bundle.getBoolean(CAN_DASH)) {
                ActionIndicator.setAction(this);
            }
        }

        @Override
        public int icon() {
            return BuffIndicator.SHEATHING;
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        public int blinkDistance() {
            //4/6/8/10 tiles
            return 4+2*Dungeon.hero.pointsInTalent(Talent.ACCELERATION);
        }

        private final CellSelector.Listener attack = new CellSelector.Listener() {
            @Override
            public void onSelect( Integer cell ) {
                if (cell != null) {
                    final Char enemy = Actor.findChar(cell);

                    //check if hero can blink.
                    boolean canBlink = true;
                    if (enemy != null) {
                        if (Dungeon.level.adjacent(target.pos, enemy.pos)) {
                            //don't attack targets without blinking.
                            GLog.w(Messages.get(Sheathing.class, "bad_target"));
                            canBlink = false;
                        } else if (enemy instanceof NPC || enemy == Dungeon.hero) {
                            GLog.w(Messages.get(Sheathing.class, "cant_attack"));
                            canBlink = false;
                        } else if (Dungeon.hero.isCharmedBy(enemy)) {
                            GLog.w(Messages.get(Charm.class, "cant_attack"));
                            canBlink = false;
                        }
                    } else if (Dungeon.hero.rooted) {
                        PixelScene.shake(1, 1f);
                        canBlink = false;
                    }

                    //set destination
                    int dest = -1;

                    if (canBlink) {
                        PathFinder.buildDistanceMap(Dungeon.hero.pos, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null), blinkDistance());

                        if (enemy != null ||
                            (!Dungeon.level.passable[cell] && (!Dungeon.hero.flying || !Dungeon.level.avoid[cell]))) {
                            for (int i : PathFinder.NEIGHBOURS8) {
                                //cannot blink into a cell that's occupied or impassable, only over them
                                if (Actor.findChar(cell + i) != null) continue;
                                if (!Dungeon.level.passable[cell + i] && !(target.flying && Dungeon.level.avoid[cell + i])) {
                                    continue;
                                }

                                if (dest == -1 || PathFinder.distance[dest] > PathFinder.distance[cell + i]) {
                                    dest = cell + i;
                                    //if two cells have the same pathfinder distance, prioritize the one with the closest true distance to the hero
                                } else if (PathFinder.distance[dest] == PathFinder.distance[cell + i]) {
                                    if (Dungeon.level.trueDistance(Dungeon.hero.pos, dest) > Dungeon.level.trueDistance(Dungeon.hero.pos, cell + i)) {
                                        dest = cell + i;
                                    }
                                }
                            }
                        } else {
                            dest = cell;
                        }
                    }

                    //blink and attack if possible
                    if (dest != -1 && PathFinder.distance[dest] != Integer.MAX_VALUE) {
                        //prevents the hero from being interrupted by seeing new enemies
                        Dungeon.hero.pos = dest;
                        Dungeon.level.occupyCell(Dungeon.hero);
                        Dungeon.observe();
                        GameScene.updateFog();
                        Dungeon.hero.checkVisibleMobs();

                        Dungeon.hero.sprite.place( Dungeon.hero.pos );
                        Dungeon.hero.sprite.turnTo( Dungeon.hero.pos, cell);
                        CellEmitter.get( Dungeon.hero.pos ).burst( Speck.factory( Speck.WOOL ), 6 );

                        if (enemy != null) {
                            Sample.INSTANCE.play( Assets.Sounds.PUFF );

                            Buff.affect(Dungeon.hero, DashDrawTracker.class);

                            Dungeon.hero.curAction = new HeroAction.Attack(enemy);
                            Dungeon.hero.next();

                        } else {
                            Sample.INSTANCE.play( Assets.Sounds.MISS );

                            GLog.w(Messages.get(Sheathing.class, "no_target"));
                            Buff.prolong(Dungeon.hero, DashDrawCooldown.class, DashDrawCooldown.DURATION);
                            if (Dungeon.hero.buff(DashDrawAccel.class) != null) {
                                Dungeon.hero.buff(DashDrawAccel.class).detach();
                            }
                            removeCross();
                            ActionIndicator.clearAction(Sheathing.this);

                            Dungeon.hero.spendAndNext( Dungeon.hero.attackDelay() );
                        }
                    } else {
                        GLog.w(Messages.get(Sheathing.class, "cant_dash"));
                    }
                }

                removeCross();
            }

            @Override
            public String prompt() {
                return Messages.get(SpiritBow.class, "prompt");
            }
        };

    }

    public static class CriticalAttack extends Buff {}

    public static class CertainCrit extends Buff {
        {
            type = buffType.POSITIVE;
            announced = true;
        }

        private int hitsLeft = 0;

        public void set(int amount) {
            this.hitsLeft = amount;
        }

        public void hit() {
            this.hitsLeft--;
            if (this.hitsLeft <= 0) {
                detach();
            }
        }

        private static final String HIT_AMOUNT = "hitAmount";
        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put( HIT_AMOUNT, hitsLeft);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            hitsLeft = bundle.getInt( HIT_AMOUNT );
        }

        public String iconTextDisplay(){
            return String.valueOf(hitsLeft);
        }

        @Override
        public int icon() {
            return BuffIndicator.CRITICAL;
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", hitsLeft);
        }
    }

    public static class QuickDrawTracker extends Buff {}

    public static class QuickDrawCooldown extends FlavourBuff{
        public static final float DURATION = 30f;
        public int icon() { return BuffIndicator.TIME; }
        public void tintIcon(Image icon) { icon.hardlight(0x586EDB); }
        public float iconFadePercent() { return Math.max(0, 1 - visualcooldown() / DURATION); }
    }

    public static class DashDrawCooldown extends FlavourBuff {
        public static final float DURATION = 80f;
        public int icon() { return BuffIndicator.TIME; }
        public void tintIcon(Image icon) { icon.hardlight(0xFF7F00); }
        public float iconFadePercent() { return Math.max(0, 1 - visualcooldown() / DURATION); }
    }

    public static class DashDrawTracker extends Buff {}

    public static class DashDrawAccel extends FlavourBuff {
        {
            type = buffType.POSITIVE;
            announced = false;
        }

        public static final float DURATION = 10f;

        float dmgMulti = 1;

        public void hit() {
            dmgMulti += 0.05f;
            dmgMulti = Math.min(dmgMulti, 1+0.25f*Dungeon.hero.pointsInTalent(Talent.ACCELERATION));
        }

        public float getDmgMulti() {
            return dmgMulti;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, 1 - visualcooldown() / DURATION);
        }

        private static final String MULTI = "dmgMulti";
        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put( MULTI, dmgMulti );
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            dmgMulti = bundle.getFloat( MULTI );
        }

        @Override
        public int icon() {
            return BuffIndicator.CRITICAL;
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", Messages.decimalFormat("#", dmgMulti*100), dispTurns());
        }
    }
}
