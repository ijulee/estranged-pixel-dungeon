package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CountCooldownBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RadioactiveMutation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SaviorAllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.StimPack;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.medic.AngelWing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PoisonParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfCleansing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.bow.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class GammaRayGun extends Item {

    private static final ItemSprite.Glowing WHITE_FAST = new ItemSprite.Glowing( 0xFFFFFF, 0.33f );
    private static final ItemSprite.Glowing WHITE_SLOW = new ItemSprite.Glowing( 0xFFFFFF, 1f );

    {
        image = ItemSpriteSheet.GAMMA_RAY_GUN;

        defaultAction = AC_USE;
        usesTargeting = true;

        bones = false;
        unique = true;
    }

    private static final String AC_USE = "USE";

    private static final int BASE_COOLDOWN = 4;

    private float powerMulti() {
        float multi = 1f;
        if (Dungeon.hero.hasTalent(Talent.HIGH_POWER)) {
            multi += 0.25f * Dungeon.hero.pointsInTalent(Talent.HIGH_POWER);
        }
        if (Dungeon.hero.hasTalent(Talent.HEALING_WING) && Dungeon.hero.buff(AngelWing.AngelWingBuff.class) != null) {
            multi += Dungeon.hero.pointsInTalent(Talent.HEALING_WING);
        }
        return multi;
    }

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        actions.add(AC_USE);
        return actions;
    }

    @Override
    public void execute( Hero hero, String action ) {

        super.execute( hero, action );

        if (action.equals(AC_USE)) {
            usesTargeting = true;
            curUser = hero;
            curItem = this;
            GameScene.selectCell(shooter);
        }
    }

    @Override
    public ItemSprite.Glowing glowing() {
        if (Dungeon.hero != null) {
            GammaRayCooldown cooldown = Dungeon.hero.buff(GammaRayCooldown.class);
            if (cooldown != null) {
                if (cooldown.count() <= 1) {
                    return WHITE_SLOW;
                } else {
                    return WHITE_FAST;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public String info() {
        String info = super.info();

        if (Dungeon.hero != null) {
            GammaRayCooldown cooldown = Dungeon.hero.buff(GammaRayCooldown.class);
            if (cooldown != null) {
                if (cooldown.count() <= 1) {
                    info += "\n\n" + Messages.get(this, "warning_low");
                } else {
                    info += "\n\n" + Messages.get(this, "warning_high");
                }
            } else {
                info += "\n\n" + Messages.get(this, "warning_none");
            }
        }

        return info;
    }

    private static int getCooldown() {
        return BASE_COOLDOWN + Dungeon.hero.pointsInTalent(Talent.HIGH_POWER);
    }

    private CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {
            if (target != null) {
                if (target == curUser.pos) {
                    GLog.w(Messages.get(this, "cannot_self"));
                    return;
                } else {
                    Ballistica beam = new Ballistica(curUser.pos, target, Ballistica.PROJECTILE);
                    Char ch = Actor.findChar(beam.collisionPos);
                    if (ch != null) {
                        if (ch.alignment == Char.Alignment.ENEMY ||
                                (ch instanceof Mimic && ch.alignment == Char.Alignment.NEUTRAL)) {
                            if (Dungeon.level.heroFOV[ch.pos]) {
                                CellEmitter.center( ch.pos ).burst( PoisonParticle.SPLASH, 3 );
                            }

                            Buff.affect(ch, Poison.class).set( Math.round((3f + Dungeon.depth * 0.67f) * powerMulti()) );

                            if (curUser.hasTalent(Talent.RADIATION)) {
                                Buff.affect(ch, RadioactiveMutation.class).set(6-curUser.pointsInTalent(Talent.RADIATION));
                            }

                            if (ch instanceof Mob) {
                                ((Mob) ch).aggro(curUser);
                            }

                            if (curUser.subClass == HeroSubClass.SAVIOR) {
                                //FIXME cache ally count? Or at least have a function
                                int allyNumber = 0;
                                for (Char mob : Actor.chars()) {
                                    if (mob.buff(SaviorAllyBuff.class) != null) {
                                        allyNumber++;
                                    }
                                }

                                if (ch instanceof Mob &&
                                        allyNumber < 2 + curUser.pointsInTalent(Talent.RECRUIT) &&
                                        !ch.isImmune(SaviorAllyBuff.class) &&
                                        Random.Float() < (ch.HT - ch.HP + 5*(1+curUser.pointsInTalent(Talent.APPEASE))) / (float)ch.HT) {
                                    
                                    AllyBuff.affectAndLoot((Mob)ch, curUser, SaviorAllyBuff.class);

                                    PotionOfCleansing.cleanse(ch);
                                    
                                    if (curUser.hasTalent(Talent.DELAYED_HEALING)) {
                                        Buff.affect(ch, Healing.class).setHeal(
                                                (int)(0.2f*curUser.pointsInTalent(Talent.DELAYED_HEALING))*ch.HT,
                                                0,
                                                1);
                                    }
                                }
                            }
                        }

                        if (ch.alignment == Char.Alignment.ALLY && (ch != curUser)) {
                            int healAmt = Math.round((5f+curUser.lvl/2f) * powerMulti());
                            if (curUser.hasTalent(Talent.MEDICAL_RAY)) {
                                healAmt = Math.round(healAmt * (1f + 0.2f*curUser.pointsInTalent(Talent.MEDICAL_RAY)));
                            }
                            ch.heal(healAmt);

                            if (curUser.hasTalent(Talent.ADRENALINE)) {
                                Buff.prolong(ch, Adrenaline.class, 3*curUser.pointsInTalent(Talent.ADRENALINE));
                                Buff.affect(ch, Poison.class).set(3*curUser.pointsInTalent(Talent.ADRENALINE));
                            }

                            if (curUser.hasTalent(Talent.STIMPACK)) {
                                Buff.prolong(ch, StimPack.class, curUser.pointsInTalent(Talent.STIMPACK));
                            }
                        }
                    }
                    curUser.sprite.zap(target);
                    curUser.sprite.parent.add( new Beam.GreenRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(beam.collisionPos)) );
                }

                GammaRayCooldown cooldown = curUser.buff(GammaRayCooldown.class);
                if (cooldown != null) {
                    //1/2-chance per shot to poison user
                    float poisonChance = (float) Math.pow(0.5f, cooldown.count());
                    if (Random.Float() > poisonChance) {
                        float poison = 5+Math.round(curUser.lvl/2f);
                        if (curUser.buff(Poison.class) != null) {
                            Buff.affect(curUser, Poison.class).extend(poison);
                        } else {
                            Buff.affect(curUser, Poison.class).set(poison);
                        }
                        BuffIndicator.refreshHero();
                        CellEmitter.center( curUser.pos ).burst( PoisonParticle.SPLASH, 3 );
                    }
                }
                //add to cooldown count
                Buff.affect(curUser, GammaRayCooldown.class, getCooldown());

                curUser.spendAndNext(Actor.TICK);
            }
        }

        @Override
        public String prompt() {
            return Messages.get(SpiritBow.class, "prompt");
        }
    };

    public static class GammaRayCooldown extends CountCooldownBuff {
        @Override
        public boolean act() {
            updateQuickslot();
            return super.act();
        }

        @Override
        public int icon() {
            // normally not visible; show only in debug builds.
            return (DeviceCompat.isDebug())? BuffIndicator.RADIOACTIVE : BuffIndicator.NONE;
        }

        @Override
        public float iconFadePercent() {
            return 1 - cooldown() / getCooldown();
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
}