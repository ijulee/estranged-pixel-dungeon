package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import static com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton.lastTarget;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.Image;
import com.watabou.utils.PointF;

public abstract class TargetingAction extends Buff implements ActionIndicator.Action {
    private Image crossTarget;
    private Image crossTag;

    protected void setCross() {
        crossTag = Icons.TARGET.get();
        crossTag.visible = false;
        ActionIndicator.instance.addToFront( crossTag );

        crossTarget = new Image();
        crossTarget.copy( crossTag );
    }

    protected void showCross() {
        setCross();

        if (lastTarget != null &&
                Actor.chars().contains( lastTarget ) &&
                lastTarget.isAlive() &&
                lastTarget.alignment != Char.Alignment.ALLY &&
                Dungeon.level.heroFOV[lastTarget.pos]) {

            CharSprite sprite = lastTarget.sprite;
            if (sprite.parent != null) {
                sprite.parent.addToFront(crossTarget);
                crossTarget.point(sprite.center(crossTarget));
            }

            ActionIndicator a = ActionIndicator.instance;
            PointF p = new PointF(
                    a.centerX() - crossTag.width()/2,
                    a.centerY() - crossTag.height()/2
            );
            crossTag.point(p);
            crossTag.visible = true;
            a.bringToFront(crossTag);

        } else {

            lastTarget = null;

        }
    }

    public static void removeCross() {
        if (ActionIndicator.action instanceof TargetingAction) {
            TargetingAction t = (TargetingAction) ActionIndicator.action;
            if (t.crossTag != null) {
                t.crossTag.remove();
            }
            if (t.crossTarget != null) {
                t.crossTarget.remove();
            }
        }
    }

    public static void updateCross() {
        if (ActionIndicator.action instanceof TargetingAction) {
            TargetingAction t = (TargetingAction) ActionIndicator.action;
            if (t.crossTarget != null) {
                if (lastTarget != null && lastTarget.sprite != null) {
                    t.crossTarget.point(lastTarget.sprite.center(t.crossTarget));
                } else {
                    t.crossTarget.remove();
                }
            }
        }
    }
}
