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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.ChangeButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.ChangeInfo;

import java.util.ArrayList;

public class RPD_Changes {

    public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){
        add_RPD_Changes(changeInfos);
    }

    private static void add_RPD_Changes(ArrayList<ChangeInfo> changeInfos) {
        ChangeInfo rpdTitle = new ChangeInfo("Re-Arranged Pixel Dungeon", true, "");
        rpdTitle.hardlight(Window.TITLE_COLOR);
        changeInfos.add(rpdTitle);

        String rpdMsg = Messages.get(ChangesScene.class, "rpd_prefix") + "\n\n" +
                Messages.get(ChangesScene.class, "rpd_changes");
        ChangeInfo rpdChanges = new ChangeInfo("", true, rpdMsg);
        rpdChanges.hardlight(0xCCCCCC);
        changeInfos.add(rpdChanges);

        String link = "https://github.com/Hoto-Mocha/Re-ARranged-Pixel-Dungeon/releases/tag/v3.2.0_based_v3.40.0-FINAL";
        ChangeButton rpdGHLink = new ChangeButton(Icons.GITHUB.get(), "RPD GitHub Link",
                "The GitHub icon should lead to the RPD repository. URL:\n" +
                link){
            @Override
            protected void onClick() {
                super.onClick();
                ShatteredPixelDungeon.platform.openURI(link);
            }
        };
        rpdChanges.addButton(rpdGHLink);
    }
}
