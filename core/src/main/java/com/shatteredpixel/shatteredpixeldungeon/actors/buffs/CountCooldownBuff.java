/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
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

import com.watabou.utils.Bundle;

import java.util.PriorityQueue;

// A buff that keeps track of a list of individual cooldowns
public class CountCooldownBuff extends FlavourBuff{
    private PriorityQueue<Float> members = new PriorityQueue<>();

    @Override
    public boolean act(){
        if (members.isEmpty()) {
            // detach if no member left
            detach();
        } else {
            Float next = members.poll();

            // remove other members expiring at the same time
            while (next != null && next == 0) {
                next = members.poll();
            }

            if (next != null) {
                // assign next cooldown
                super.spend(next);

                // update remaining cooldowns
                PriorityQueue<Float> newMembers = new PriorityQueue<>();
                while (!members.isEmpty()) {
                    newMembers.add(members.poll() - next);
                }
                members = newMembers;
            }
        }
        return true;
    }

    @Override
    protected void spend( float time ) {
        // store new cooldown in relation to current cooldown
        if (members.isEmpty() && cooldown() == 0) {
            super.spend(time);
        } else {
            members.add(time-cooldown());
        }
    }

    private static final String MEMBERS = "members";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        float[] membersArray = new float[members.size()];
        int i = 0;
        for (Object f : members.toArray()){
            membersArray[i] = (Float) f;
            i++;
        }
        bundle.put(MEMBERS, membersArray);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        float[] membersArray = bundle.getFloatArray(MEMBERS);
        for (float f : membersArray) {
            members.add(f);
        }

    }

    public int count(){
        return members.size() + ((cooldown() > 0)? 1 : 0);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString(count());
    }
}
