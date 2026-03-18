package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DirectableAlly;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.Lance;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.LanceNShield;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SpiritHorseSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class HorseRiding extends Buff implements ActionIndicator.Action, Hero.Doom {

    {
        revivePersists = true;

        announced = true;
    }

    private HorseAlly horse = null;
    private int horseHP = 0;
    private int horseHT = 0;

    public void set() {
        set(-1);
    }

    public void set(int HP) {
        horseHT = (15+Dungeon.hero.lvl*5);
        if (HP != -1) {
            horseHP = HP;
        } else {
            horseHP = horseHT;
        }
    }

    public void onLevelUp() {
        horseHT = (15+Dungeon.hero.lvl*5);
        BuffIndicator.refreshHero();
    }

    public void healHorse(int amount) {
        this.horseHP = Math.min(HorseRiding.this.horseHP + amount, HorseRiding.this.horseHT);;
    }

    @Override
    public int icon() {
        return BuffIndicator.HORSE_RIDING;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", horseHP, horseHT);
    }

    public void onDamage(int damage) {
        //FIXME the horse only takes damage from physical attacks in Hero.defenseProc()
        //  and not other sources.
        damage -= drRoll();
        damage = Math.max(damage, 0);
        horseHP -= damage;
        if (horseHP <= 0) {
            detach();
            PixelScene.shake( 2, 1f );
            GLog.n(Messages.get(this, "fall"));
            float dmgMulti = 1-0.25f*Dungeon.hero.pointsInTalent(Talent.PARKOUR);
            Buff.prolong( target, Cripple.class, Cripple.DURATION );

            //The lower the hero's HP, the more bleed and the less upfront damage.
            //Hero has a 50% chance to bleed out at 66% HP, and begins to risk instant-death at 25%
            int bleedAmt = Math.round(target.HT / (6f + (6f*(target.HP/(float)target.HT))) * dmgMulti);
            int fallDmg = Math.round(Math.max( target.HP / 2, Random.NormalIntRange( target.HP / 2, target.HT / 4 ))*dmgMulti);
            Buff.affect( target, Bleeding.class).set( bleedAmt, RideFall.class);
            target.damage( fallDmg, new RideFall() );
            Buff.affect(target, RidingCooldown.class).set();
        }
    }

    public static int drRoll() {
        int baseDr = Random.NormalIntRange(2, 16);
        return baseDr + Random.NormalIntRange(Dungeon.hero.pointsInTalent(Talent.ARMORED_HORSE), 8*Dungeon.hero.pointsInTalent(Talent.ARMORED_HORSE));
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0, 1 - horseHP/(float)horseHT);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString(horseHP);
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }

    @Override
    public int actionIcon() {
        return HeroIcon.RIDE;
    }

    @Override
    public int indicatorColor() {
        return 0x26058C;
    }

    @Override
    public void doAction() {
        GameScene.selectCell(dashDirector);
    }

    public void doCharge(List<Integer> path) {
        Hero hero = (Hero) target;

        //process in reverse, mainly for elastic
        Collections.reverse(path);

        final List<Integer> dashPath = List.copyOf(path);
        final int dashDist = path.size()-1;
        final int dashCell = dashPath.get(0);

        hero.busy();
        Sample.INSTANCE.play(Assets.Sounds.MISS);
        hero.sprite.emitter().start(Speck.factory(Speck.JET), 0.01f, Math.round(4 + 2*Dungeon.level.trueDistance(hero.pos, dashCell)));

        hero.sprite.jump(hero.pos, dashCell, 0, 0.1f, () -> {
            //press cells and collect chars in path
            ArrayList<Char> charsInPath = new ArrayList<>();
            for (int p : dashPath) {
                if (!hero.flying) {
                    Dungeon.level.pressCell( p );
                }

                Char enemy = Actor.findChar( p );
                if (enemy != null && enemy != hero) {
                    charsInPath.add( enemy );
                }
            }

            //attack chars
            for (Char enemy : charsInPath) {
                hero.attack(enemy, 1f+0.2f*hero.pointsInTalent(Talent.DASH_ENHANCE), 1, 1);
            }

            //clear destination cell by pushing chars
            Char pushChar = Actor.findChar( dashCell );
            HashSet<Integer> excludeCells = new HashSet<>(List.of(dashCell));
            while (pushChar != null && pushChar != hero && pushChar.isAlive()) {
                int pushPos = findPushTile(pushChar, excludeCells);
                Char nextChar = Actor.findChar( pushPos );
                excludeCells.add( pushPos );

                Actor.add(new Pushing(pushChar, pushChar.pos, pushPos));
                pushChar.pos = pushPos;
                Dungeon.level.occupyCell(pushChar);
                pushChar = nextChar;
            }

            //move and update fog
            hero.move(dashCell);
            Dungeon.observe();
            GameScene.updateFog();

            //inflict recoil
            int recoil = dashDist * 5;
            recoil -= hero.drRoll();
            for (int i = 0; i < charsInPath.size(); i++) {
                recoil -= Random.NormalIntRange(hero.pointsInTalent(Talent.BUFFER), 3*hero.pointsInTalent(Talent.BUFFER));
            }
            hero.damage(recoil, HorseRiding.this);

            Invisibility.dispel();
            hero.spendAndNext(Actor.TICK);

            if (hero.hasTalent(Talent.SHOCKWAVE)) {
                int strength = (int) Math.floor(dashDist/(float)(8-2*hero.pointsInTalent(Talent.SHOCKWAVE)));
                if (strength > 0) {
                    WandOfBlastWave.BlastWave.blast(hero.pos);

                    for (int i : PathFinder.NEIGHBOURS8) {
                        Char mob = Actor.findChar(hero.pos + i);
                        if (mob != null && mob != hero && mob.alignment != Char.Alignment.ALLY) {
                            Ballistica trajectory = new Ballistica(mob.pos, mob.pos + i, Ballistica.MAGIC_BOLT);
                            WandOfBlastWave.throwChar(mob, trajectory, strength, false, true, HorseRiding.this);
                        }
                    }
                }
            }

            if ((hero.belongings.weapon() instanceof Lance ||
                (hero.belongings.weapon() instanceof LanceNShield && ((LanceNShield)hero.belongings.weapon()).stance))) {
                Buff.affect(hero, Lance.LanceBuff.class).setDamageFactor(dashDist, false);
            }

            Sample.INSTANCE.play(Assets.Sounds.BLAST);
            CellEmitter.get( hero.pos ).start(Speck.factory(Speck.ROCK), 0.03f, Math.min(dashDist, 10));
        });
    }

    public static int findPushTile(Char ch, HashSet<Integer> excludes) {
        int emptyPos = -1;
        int occupiedPos = -1;

        for (int c : PathFinder.NEIGHBOURS8) {
            if (!excludes.contains(ch.pos + c) &&
                    Dungeon.level.passable[ch.pos + c] &&
                    (Dungeon.level.openSpace[ch.pos + c] || !Char.hasProp(ch, Char.Property.LARGE))) {
                if (Actor.findChar(ch.pos + c) == null) {
                    emptyPos = ch.pos + c;
                    break;
                } else if (occupiedPos == -1) {
                    occupiedPos = ch.pos + c;
                }
            }
        }

        //prioritize an empty tile
        return (emptyPos != -1) ? emptyPos : occupiedPos;
    }

    @Override
    public boolean attachTo(Char target) {
        ActionIndicator.setAction(this);
        return super.attachTo(target);
    }

    @Override
    public void detach() {
        ActionIndicator.clearAction();
        super.detach();
    }

    private void spawnHorse() {
        Hero hero = (Hero) target;
        ArrayList<Integer> spawnPoints = new ArrayList<>();
        for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
            int p = hero.pos + PathFinder.NEIGHBOURS8[i];
            if (Actor.findChar(p) == null && Dungeon.level.passable[p]) {
                spawnPoints.add(p);
            }
        }

        if (!spawnPoints.isEmpty()) {
            this.horse = new HorseAlly(hero, this.horseHP);

            horse.pos = Random.element(spawnPoints);

            GameScene.add(horse, 1f);
            Dungeon.level.occupyCell(horse);

            Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
            CellEmitter.get(horse.pos).start( Speck.factory(Speck.LIGHT), 0.2f, 3 );

            hero.spend(1f);
            hero.busy();
            hero.sprite.operate(hero.pos);

            detach();
        } else {
            GLog.i( Messages.get(this, "no_space") );
        }
    }

    public CellSelector.Listener dashDirector = new CellSelector.Listener(){

        @Override
        public void onSelect(Integer cell) {
            if (cell == null) return;
            if (cell == Dungeon.hero.pos) {
                spawnHorse();
            } else {
                Hero hero = (Hero) target;

                if (hero.rooted) {
                    PixelScene.shake( 1, 1f );
                    GLog.w(Messages.get(HorseRiding.class, "rooted"));
                    return;
                }

                Ballistica dash = new Ballistica(hero.pos, cell, Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID);

                //check that any char at destination can be pushed
                //back up if impossible
                int dist = dash.dist;
                Char ch;
                while ( dist > 1 && (ch = Actor.findChar(dash.path.get(dist))) != null &&
                        (ch.rooted || Char.hasProp(ch, Char.Property.IMMOVABLE))) {
                    dist--;
                }

                List<Integer> path = dash.subPath(0, dist);

                if (dist == 1) {
                    //do nothing
                } else if (5*dist > Math.round((hero.HP + hero.shielding())*0.9f)) {
                    GameScene.show(new WndOptions(
                            new HeroIcon(HeroSubClass.HORSEMAN),
                            Messages.get(HorseRiding.class, "action_name"),
                            Messages.get(HorseRiding.class, "charge_confirm"),
                            Messages.get(HorseRiding.class, "yes"),
                            Messages.get(HorseRiding.class, "no")) {
                        private float elapsed = 0f;

                        @Override
                        public synchronized void update() {
                            super.update();
                            elapsed += Game.elapsed;
                        }

                        @Override
                        public void hide() {
                            if (elapsed > 0.2f){
                                super.hide();
                            }
                        }

                        @Override
                        protected void onSelect( int index ) {
                            if (index == 0 && elapsed > 0.2f) {
                                doCharge(path);
                            }
                        }
                    });
                } else {
                    doCharge(path);
                }
            }
        }

        @Override
        public String prompt() {
            return Messages.get(HorseRiding.class, "direct_prompt");
        }
    };

    @Override
    public void onDeath() {
        Dungeon.fail( this );
        GLog.n( Messages.get(this, "ondeath") );
    }

    private static final String HORSE_HP = "horseHP";
    private static final String HORSE_HT = "horseHT";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(HORSE_HP, horseHP);
        bundle.put(HORSE_HT, horseHT);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        horseHP = bundle.getInt(HORSE_HP);
        horseHT = bundle.getInt(HORSE_HT);
    }

    public static class HorseAlly extends DirectableAlly {
        {
            spriteClass = SpiritHorseSprite.class;

            alignment = Alignment.ALLY;

            //before other mobs
            actPriority = MOB_PRIO + 1;

            followHero();
        }

        private float partialCharge = 0f;
        private int heroLvl = 0;

        public HorseAlly() {
            super();
        }

        public HorseAlly(Hero hero, int HP) {
            this.HT = (15+hero.lvl*5);
            this.defenseSkill = (hero.lvl+4);
            this.HP = HP;
            this.heroLvl = hero.lvl;
        }

        @Override
        protected boolean act() {
            if (this.HP < this.HT && Regeneration.regenOn()) {
                partialCharge += 0.1f;
                if (Dungeon.level.map[this.pos] == Terrain.GRASS) {
                    partialCharge += 0.4f;
                }
                while (partialCharge > 1) {
                    this.HP++;
                    partialCharge--;
                }
            } else {
                partialCharge = 0;
            }
            if (Dungeon.hero != null && Dungeon.hero.lvl != this.heroLvl) updateHorse(Dungeon.hero);
            return super.act();
        }

        @Override
        public boolean canInteract(Char c) {
            return super.canInteract(c); //can use ALLY_WARP talent
        }

        @Override
        public boolean interact(Char c) {
            if (c instanceof Hero) {
                Buff.affect(c, HorseRiding.class).set(this.HP);
                destroy();
                sprite.die();
            }
            return true;
        }

        @Override
        public void die(Object cause) {
            Buff.affect(Dungeon.hero, RidingCooldown.class).set();
            super.die(cause);
        }

        @Override
        public int damageRoll() {
            return 0;
        }

        public void updateHorse(Hero hero){
            //same dodge as the hero
            defenseSkill = (hero.lvl+4);
            HT = (15+hero.lvl*5);
            this.heroLvl = hero.lvl;
        }

        @Override
        public float speed() {
            float speed = super.speed();

            //moves 2 tiles at a time when returning to the hero
            if (state == WANDERING
                    && defendingPos == -1
                    && Dungeon.level.distance(pos, Dungeon.hero.pos) > 1){
                speed *= 2;
            }

            return speed;
        }

        @Override
        public int posDRRoll() {
            return super.posDRRoll() + HorseRiding.drRoll();
        }

        @Override
        public int attackProc(Char enemy, int damage) {
            if (enemy instanceof Mob) {
                ((Mob)enemy).aggro( this );
            }

            return super.attackProc(enemy, damage);
        }
    }

    public static class RideFall implements Hero.Doom {
        @Override
        public void onDeath() {
            Dungeon.fail( this );
            GLog.n( Messages.get(this, "ondeath") );
        }
    }

    public static class RidingCooldown extends Buff {

        {
            type = buffType.NEUTRAL;
            announced = false;
        }

        private int kills;
        private static final int MAX_KILLS = 5;

        @Override
        public int icon() {
            return BuffIndicator.HORSE_RIDING;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0xFF8000);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, 1 - kills /(float) MAX_KILLS);
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString(kills);
        }

        public void onKill() {
            kills--;
            if (kills <= 0) {
                detach();
            }
            BuffIndicator.refreshHero();
        }

        public void set() {
            kills = MAX_KILLS;
        }

        @Override
        public void detach() {
            Buff.affect(target, HorseRiding.class).set();
            super.detach();
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", kills, MAX_KILLS);
        }

        private static final String KILLS = "cooldown";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(KILLS, kills);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            kills = bundle.getInt(KILLS);
        }
    }
}
