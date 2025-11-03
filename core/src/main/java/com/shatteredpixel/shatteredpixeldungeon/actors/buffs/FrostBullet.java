/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnowParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Random;

public class FrostBullet extends ElementalBullet {
    public void proc(Char enemy){
        if (Random.Float() < 0.75f) {
            Buff.affect(enemy, Chill.class, 2f);
        } else {
            //need to delay this through an actor so that the freezing isn't broken by taking damage from the bullet hit.
            new FlavourBuff() {
                {
                    actPriority = VFX_PRIO;
                }

                public boolean act() {
                    Buff.affect(target, Frost.class, Math.round(Frost.DURATION/2));
                    return super.act();
                }
            }.attachTo(enemy);
        }

        enemy.sprite.emitter().burst( SnowParticle.FACTORY, 2 );
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0.2f, 0.2f, 1f);
    }
}
