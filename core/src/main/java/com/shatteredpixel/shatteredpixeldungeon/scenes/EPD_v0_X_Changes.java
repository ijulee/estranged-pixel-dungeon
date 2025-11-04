package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.ChangeButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.ChangeInfo;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class EPD_v0_X_Changes {
    public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ) {
        add_v0_1_Changes(changeInfos);
    }

    private static void add_v0_1_Changes(ArrayList<ChangeInfo> changeInfos) {
        ChangeInfo changes = new ChangeInfo("v0.0.1", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "new"), false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.ESTRANGED.get(), "Dev Commentary",
                "**-** Released [DATE]\n" +
                "**-** Based on Re-Arranged v3.39.0 and Shattered v3.2.5\n\n" +
                "Hi there. Long time player, first time modder. Re-Arranged is what I'd call an " +
                "\"add-on\" mod to Shattered, one that keeps up with SPD's content updates, and " +
                "only seeks to add content, rather than taking the game in a different " +
                "direction.\n\n" +
                "It was sad to hear that Hoto-Mocha (Cocoa) will not continue with Re-Arranged, " +
                "when there was still so much content waiting to be fleshed out. I thought that " +
                "the game design could use more nuance as well, and that is what I will try to do " +
                "with EstRanged. At the same time, I will keep this mod up-to-date with Shattered " +
                "releases as well.\n\n" +
                "This first update will consist mostly of a Shattered rebase to v3.2.5, plus " +
                "translation and bug fixes, but I\'ll try to implement more interesting changes " +
                "over time.\n\n" +
                "_-- miaomix_") );

        changes.addButton( new ChangeButton(Icons.SHPX.get(), "SPD v3.2.5",
                "Rebased to Shattered Pixel Dungeon v3.2.5. I'll try to keep up, as long as " +
                        "there aren't any major conflicts.") );

        changes.addButton( new ChangeButton(Icons.DISPLAY.get(), "Visual & Interface Changes",
                "**-** Changed title splash! Graphic design is my passion :^)\n" +
                "**-** No seriously, I totally botched the title banner.\n" +
                "**-** Brought back Shattered's Changelogs, so you can see the changes that come " +
                "with the SPD base version.\n" +
                "**-** Tabs in the Changelog! Click each mod version's icon to look at each " +
                "version's changes.\n" +
                "**-** Tabs in the About page for each mod.\n"+
                "**-** Brought in the new and improved background animation that came with " +
                "Shattered v3.2."));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, null);
        changes.hardlight(CharSprite.WARNING);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.ARCHER, 2),"Archer Translation",
                "Added translated English strings for the Archer, including descriptions, " +
                "talents, quiver potion infusion effects, and more."));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.ARCHER, 4),"Archer Subclasses",
                "Bowmaster changes:\n" +
                "**-** Action indicator is now always present when combo is active, but disabled " +
                "unless Powershot is available.\n" +
                "**-** Action indicator now shows bow combo count, and has 3 different highlight colors indicating Powershot status.\n" +
                "**-** Fixed oddities with Powershot usage and Moving Focus talent.",

                "Juggler changes:\n" +
                "**-** Action indicator now shows Juggling stack count, and has a highlight color " +
                "for a full stack.\n" +
                "**-** Fixed bug where arrow weapons are mistakenly dropped when the Juggler is " +
                "holding a bow with insufficient Strength, instead of arrow ammo items.\n" +
                "**-** When the Juggler does not have a bow equipped, Habitual Hand and Tour " +
                "Performance talents now selects the appropriate bow based on current Strength."));

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.LONGBOW), "Bow Weapon Changes",
                "**-** The implementation has been changed fundamentally, along with the " +
                "implementation of gun weapons, so there may be some unexpected bugs. Please let " +
                "me know if anything strange happens.\n" +
                "**-** Bow weapons now gain identification progress when shooting. " +
                "(Note that it normally takes 20 uses to identify, like all melee weapons.)\n" +
                "**-** Bow weapons can now be equipped from the quickslot with Swift Equip talent.\n" +
                "**-** Bow weapons now prioritize the Duelist's ability when used from the " +
                "quickslot on the hero.\n" +
                "**-** Added shooting critical chance description for bow weapons.\n" +
                "**-** Reworked Bow Fatigue and added clarification text. It counts bow arrows " +
                "fired in the last 5 turns, and arrow damage is reduced by 10% for each arrow " +
                "after the third.\n" +
                "**-** Reworked Arrow Attached debuff. It tracks cooldown separately for each " +
                "arrow, and drops an arrow ammo item when a cooldown expires."));

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.HG_T5), "Gun Weapon Changes",
                "**-** Like bow weapons, the implementation was fundamentally changed, " +
                "so there may be bugs.\n" +
                "**-** Gun weapons also gain identification progress when shooting. (Again, melee " +
                "weapons normally take 20 uses to identify.)\n" +
                "**-** Gun weapons also can be equipped from the quickslot with Swift Equip talent.\n" +
                "**-** Gun weapons also prioritize the Duelist's ability when used from the " +
                "quickslot on the hero.\n" +
                "**-** Also added shooting critical chance description for gun weapons."));

        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "**-** Added Metamorphed effect for Unexpected Slash talent."));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16),
                Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed the following bugs:\n" +
                "**Weapons**\n" +
                "**-** Cursed bow weapons causing \"cursed thrown weapon\" message.\n" +
                "**-** Gun and bow weapons not working with Metamorphed Seer Shot talent.\n" +
                "**-** Bow weapons having excess Strength bonus damage on surprise attack " +
                "when shooting.\n" +
                "**-** Bow Weapons not having thrown weapon accuracy modifiers.\n" +
                "**-** Actions that load gun weapons \"removing\" rounds from an over-loaded gun.\n\n" +
                "**Talents**\n" +
                "**-** Samurai's Weapon Mastery talent not applying when Metamorphed.\n" +
                "**-** Samurai's Parrying talent name. (was: \"Parring\")\n" +
                "**-** Talents requiring excess Strength on armor incorrectly applying when armor " +
                "is not kept through Lost Inventory.\n\n" +
                "**Miscellaneous**\n" +
                "**-** Slayer's Awakening not accounting for buffs and Ferret Tuft.\n" +
                "**-** Mystic Grindstone trinket resulting in \"negative\" blocking and increased " +
                "damage."));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "nerfs"), false, null);
        changes.hardlight(CharSprite.NEGATIVE);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.LONGBOW), "Bow Weapon Nerf",
                "Bow weapon knockback can no longer knock enemies into pits."));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.ARCHER, 2),"Archer Nerf",
                "Similarly, Pushback talent can no longer knock enemies into pits."));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.ARCHER, 4),"Bowmaster Nerf",
                "Bowmaster combo now breaks with partial-turn actions that do not build combo."));

    }
}
