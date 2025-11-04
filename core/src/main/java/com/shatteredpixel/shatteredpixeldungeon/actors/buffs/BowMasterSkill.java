package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class BowMasterSkill extends Buff implements ActionIndicator.Action {
    {
        type = buffType.NEUTRAL;
    }

    private boolean justShot = false;
    private boolean justMoved = false;
    private boolean powerShot = false;
    private int charge = 0;
    private final int MAX_CHARGE = 4;

    private int maxCharge() {
        return MAX_CHARGE + Dungeon.hero.pointsInTalent(Talent.EXPANDED_POWER);
    }

    @Override
    public int icon() {
        return BuffIndicator.ARROW_EMPOWER;
    }

    @Override
    public void tintIcon(Image icon) {
        if (isPowerShot()) {
            icon.tint(1, 1, 1, 0.4f);
        } else {
            float tint = Math.min(1, 0.2f*(charge+1));
            icon.hardlight(tint, tint, tint);
        }
    }

    @Override
    public float iconFadePercent() {
        return Math.min(1, (maxCharge() - charge) / ((float) maxCharge()));
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString(charge);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", charge, maxCharge(), 100*dmgMulti(), comboDmgBonus());
    }

    public void onShoot() {
        if (isPowerShot()) {
            this.justMoved = false;
            this.justShot = false;
            this.powerShot = false;
            this.charge = 0;
        } else {
            this.justMoved = false;
            this.justShot = true;
            this.charge = Math.min(maxCharge(), charge+1);
        }

        ActionIndicator.setAction(this);
    }

    public static void onMove(Hero hero) {
        BowMasterSkill bm = hero.buff(BowMasterSkill.class);

        if (bm != null && !bm.justMoved) {
            switch (hero.pointsInTalent(Talent.MOVING_FOCUS)) {
                case 3:
                    // behave as if the hero has made a shot
                    if (!bm.isPowerShot()) {
                        bm.onShoot();
                    }
                    break;
                case 2:
                    // do nothing
                    break;
                case 1:
                    if (Random.Float() < 0.8f) {
                        // do nothing
                    } else {
                        bm.detach();
                    }
                    break;
                case 0:
                default:
                    bm.detach();
                    break;
            }
            bm.justShot = false;
            bm.justMoved = true;
        } else {
            Buff.detach(hero, BowMasterSkill.class);
        }
    }

    public boolean isPowerShot() {
        return this.powerShot;
    }

    private float powerShotMulti() {
        return (isPowerShot()) ? 1f : 0f;
    }

    private float comboMulti() {
        return (float) Math.pow(1.05f, charge);
    }
    private int comboDmgBonus() {
        return 2 * charge;
    }

    private float dmgMulti() {
        return comboMulti() + powerShotMulti();
    }

    public int proc(int damage) {
        float result = damage * dmgMulti() + comboDmgBonus();
        return Math.round(result);
    }

    @Override
    public boolean act() {
        if (!justShot) detach();
        spend(target.cooldown()); // hopefully acts after next hero action?
        justShot = false;

        return true;
    }

    @Override
    public void detach() {
        ActionIndicator.clearAction();
        super.detach();
    }
    private static final String CHARGE   = "charge";
    private static final String MOVED   = "moved";
    private static final String SHOOT   = "shoot";
    private static final String POWER_SHOT   = "powerShot";


    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);

        bundle.put(CHARGE, charge);
        bundle.put(MOVED, justMoved);
        bundle.put(SHOOT, justShot);
        bundle.put(POWER_SHOT, powerShot);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);

        charge = bundle.getInt(CHARGE);
        justMoved = bundle.getBoolean(MOVED);
        justShot = bundle.getBoolean(SHOOT);
        powerShot = bundle.getBoolean(POWER_SHOT);

        ActionIndicator.setAction(this);
    }

    public static boolean isFastShot(Hero hero) {
        return hero.buff(BowMasterSkill.class) == null && hero.hasTalent(Talent.FASTSHOT);
    }

    public static float fastShotDamageMultiplier(Hero hero) {
        if (!hero.hasTalent(Talent.FASTSHOT)) return 1;
        else return 0.2f * hero.pointsInTalent(Talent.FASTSHOT);
    }

    // FIXME this doesn't seem to belong here
    public static float speedBoost(Hero hero){
        if (!hero.hasTalent(Talent.UNENCUMBERED_STEP)){
            return 1;
        }

        boolean enemyNear = false;
        //for each enemy, check if they are adjacent, or within 2 tiles and an adjacent cell is open
        for (Char ch : Actor.chars()){
            if ( Dungeon.level.distance(ch.pos, hero.pos) <= 2 && hero.alignment != ch.alignment && ch.alignment != Char.Alignment.NEUTRAL){
                if (Dungeon.level.adjacent(ch.pos, hero.pos)){
                    enemyNear = true;
                    break;
                } else {
                    for (int i : PathFinder.NEIGHBOURS8){
                        if (Dungeon.level.adjacent(hero.pos+i, ch.pos) && !Dungeon.level.solid[hero.pos+i]){
                            enemyNear = true;
                            break;
                        }
                    }
                }
            }
        }

        if (enemyNear){
            return 1;
        } else {
            return (1 + 0.1f*hero.pointsInTalent(Talent.UNENCUMBERED_STEP));
        }
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }

    @Override
    public int actionIcon() {
        return HeroIcon.POWER_SHOT;
    }

    @Override
    public Visual secondaryVisual() {
        BitmapText txt = new BitmapText(PixelScene.pixelFont);
        txt.text(String.format("%d", charge));
        if (charge >= MAX_CHARGE) {
            txt.hardlight(CharSprite.POSITIVE);
        }
        txt.measure();
        return txt;
    }

    @Override
    public int indicatorColor() {
        if (isPowerShot()) {
            return 0xEE8091;
        } else if (charge >= MAX_CHARGE) {
            return 0xCC0022;
        } else {
            return 0x808080;
        }
    }

    @Override
    public void doAction() {
        if (charge >= MAX_CHARGE && !isPowerShot()) {
            ScrollOfRecharging.charge(Dungeon.hero);
            powerShot = true;
            BuffIndicator.refreshHero();
            Dungeon.hero.sprite.operate(Dungeon.hero.pos);
            Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
        }
    }
}
