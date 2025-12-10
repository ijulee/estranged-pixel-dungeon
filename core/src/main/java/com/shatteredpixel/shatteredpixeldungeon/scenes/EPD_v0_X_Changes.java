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
                "**-** Based on Re-Arranged v3.40.0-FINAL and Shattered v3.3.0\n\n" +
                "Hi there. Long time player, first time modder. Re-Arranged is what I'd call an " +
                "\"add-on\" mod to Shattered, one that keeps up with SPD's content updates, and " +
                "only seeks to add content, rather than taking the game in a different " +
                "direction.\n\n" +
                "It was sad to hear that Hoto-Mocha (Cocoa) will not continue with Re-Arranged, " +
                "when there was still so much content waiting to be fleshed out. I thought that " +
                "the game design could use more nuance as well, and that is what I will try to do " +
                "with EstRanged. At the same time, I will keep this mod up-to-date with Shattered " +
                "releases as well.\n\n" +
                "This first update will consist mostly of a Shattered rebase to v3.3.0, plus " +
                "translation and bug fixes, but I\'ll try to implement more interesting changes " +
                "over time.\n\n" +
                "_-- miaomix_") );

        changes.addButton( new ChangeButton(Icons.SHPX.get(), "SPD v3.3.0",
                "Rebased to Shattered Pixel Dungeon v3.3.0. I'll try to keep up, as long as " +
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
                "Shattered v3.2.\n" +
                "**-** Added floating text icons for ammo pickup, talent point gain, and max HP " +
                "boost."));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, null);
        changes.hardlight(CharSprite.WARNING);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.ARCHER, 2),"Archer Changes",
                "**-** Added translated English strings for the Archer, including descriptions, " +
                "talents, quiver potion infusion effects, and more.\n" +
                "**-** Added confirmation dialogue for scrapping.\n" +
                "**-** The tier of the crafted thrown weapon now depends on the scrapped weapon " +
                "tier, and is likely to be higher tier if the latter was visibly upgraded or " +
                "enchanted."));

        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.ARCHER, 4),"Archer Subclasses",
                "Bowmaster changes:\n" +
                "**-** Action indicator is now always present when combo is active, but disabled " +
                "unless Powershot is available.\n" +
                "**-** Action indicator now shows bow combo count, and has 3 different highlight colors indicating Powershot status.\n" +
                "**-** Fixed oddities with Powershot usage and Moving Focus talent.",

                "Juggler changes:\n" +
                "**-** Juggling attack can now auto-target.\n" +
                "**-** Action indicator now shows Juggling stack count, and has a highlight color " +
                "for a full stack.\n" +
                "**-** Fixed bug where arrow weapons are mistakenly dropped when the Juggler is " +
                "holding a bow with insufficient Strength, instead of arrow ammo items.\n" +
                "**-** When the Juggler does not have a bow, Habitual Hand and Tour Performance " +
                "talents now selects one based on current Strength, ensuring that the juggled " +
                "arrows can actually be used."));

        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.SAMURAI, 2),"Samurai Changes",
                "**-** Changed description of the Samurai and her T1 / T2 talents to be " +
                "more clear, hopefully.\n" +
                "**-** Added Metamorphed effect for Unexpected Slash talent."));

        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.SAMURAI, 4),"Samurai Subclasses",
                "All 3 subclasses have been renamed to better reflect what they do. " +
                "Descriptions of their respective talents have also been changed for clarity.\n" +
                "**-** Slasher has been renamed **Auraslasher**.\n" +
                "**-** Master has been renamed **Swordmaster**.\n" +
                "**-** Slayer has been renamed **Demonslayer**.",

                "**Auraslasher changes:**\n" +
                "**-** Her special attack is now named **Aura Slash**, powered by collected " +
                "**Sword Energy**.\n" +
                "**-** Action indicator now displays percentage of collected Sword Energy and " +
                "shows action name (**Aura Slash**) on hover.\n" +
                "**-** Sword Energy collection now scales with actual damage dealt to enemies, " +
                "meaning damage increases such as Lethal Power talent will increase Aura Slash's " +
                "energy recycling from the talent.\n" +
                "**-** Sword Energy cost reduction from the talent is now _87%/73%/60%_. There was " +
                "actually an unexplained 90% reduction at +0 that is now removed. The reduced " +
                "energy cost is also displayed in the buff description.\n" +
                "**-** Infinite Sword Energy exploit has been fixed.\n" +
                "**-** Auto-targeting has been fixed.\n" +
                "**-** Sword Aura interactions with Projecting enchantment has been fixed. ",

                "**Swordmaster changes:**\n" +
                "**-** Her special draw attacks are now named **Quick Draw** and **Dash Draw**.\n" +
                "**-** Added new action icon for Dash Draw.\n" +
                "**-** Added visual indication of Quick Draw attack.\n" +
                "**-** Added auto-targeting for Dash Draw.\n" +
                "**-** Dash Draw should apply damage increase after the current attack.\n" +
                "**-** Fixed Quick Draw not actually applying at all.",

                "**Demonslayer changes:**\n" +
                "**-** Awakening now accounts for evasion bonus from buffs and Ferret Tuft.\n" +
                "**-** Critical attacks with missile weapons should be instant during Awakening."));

        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.GUNNER, 2),"Gunner Changes",
                "**-** Reworked liquid metal yield for scrapping. Less is given per " +
                "item level, but the penalty for a cursed weapon is reduced, and a bonus for " +
                "enchanted equipment is added.\n" +
                "**-** If the scrapped item is identified, the confirmation dialogue should " +
                "show the exact yield.\n" +
                "**-** Added flare to distinguish gun crafting type."));

        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.DUELIST, 2),"Duelist Changes",
                "**-** Changed ability description of Guns for clarity.\n" +
                "**-** If the Champion metamorphs Reloading Meal talent, it also applies to her " +
                "secondary weapon."));

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.LONGBOW), "Bow Weapon Changes",
                "**-** The implementation has been changed fundamentally, along with the " +
                "implementation of gun weapons, so there may be some unexpected bugs. Please let " +
                "me know if anything strange happens.\n" +
                "**-** Moved some info from bow item descriptions into Adventurer's Guuide to make " +
                "them more concise.\n" +
                "**-** Added stats description for all bows.\n" +
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
                "**-** Moved some info from gun item descriptions into Adventurer's Guuide to make " +
                "them more concise.\n" +
                "**-** Added stats descriptions to all gun types, including info such as shooting " +
                "accuracy and ammo use.\n" +
                "**-** Gun weapons also gain identification progress when shooting. (Again, melee " +
                "weapons normally take 20 uses to identify.)\n" +
                "**-** Gun weapons also can be equipped from the quickslot with Swift Equip talent.\n" +
                "**-** Gun weapons also prioritize the Duelist's ability when used from the " +
                "quickslot on the hero.\n" +
                "**-** Also added shooting critical chance description for gun weapons.\n" +
                "**-** Exploding guns now apply in order of distance to hero like Force Cube.\n" +
                "**-** Renamed the Gunsmithing Kit. The interface was also reworked, and now allows " +
                "returning to mod type selection window."));

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.ARTIFACT_TOOLKIT), "Alchemy Changes",
                "Cross and Pot o' Thunderbolt crafting changes:\n" +
                "**-** They now destroy the thrown weapon set used.\n" +
                "**-** They are always identified, and crafted as set of 3.\n\n" +
                "Blueprint changes:\n" +
                "**-** If the weapons used are upgraded and identified, the blueprint " +
                "(not the crafted weapon!) will inherit the combined levels of those " +
                "weapons."));

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.BLUEPRINT), "Blueprint Changes",
                "Blueprint changes:\n" +
                "**-** Added confirmation dialogue showing success rate and the differences " +
                "in weapon stats.\n" +
                "**-** Changed item description to be more concise.\n" +
                "**-** Added info to the corresponding Adventurer's Guide tab."));

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
                "**-** Talents requiring excess Strength on armor incorrectly applying when armor " +
                "is not kept through Lost Inventory.\n" +
                "**-** Incorrect description for Chaser's Lethal Surprise.\n" +
                "**-** Battlemage's Magic Combo moves that do physical attacks should no longer " +
                "have infinite range.\n" +
                "**-** Veteran's Improved Tackle +1 incorrectly applying its effect." +
                "**Trinkets**\n" +
                "**-** Mystic Grindstone resulting in \"negative\" blocking and increased " +
                "damage.\n" +
                "**-** Ring of Wealth not properly accounting for Ring Necklace.\n" +
                "**-** Equipped ring descriptions now account for bonus from Ring Necklace.\n" +
                "**-** Potential bug where Ring Necklace provided extra benefit on game load",

                "**Miscellaneous**\n" +
                "**-** Researcher trampling furrowed grass in specific cases.\n" +
                "**-** Medic's Gamma Ray Gun not affecting inactive mimics.\n" +
                "**-** Gunner's crafting resulting in 0 liquid metal in inventory.\n" +
                "**-** Supply Rations not eaten instantly for eating talents of new RPD classes.\n" +
                "**-** Pitfall traps not working in Lab region (26F-29F).\n" +
                "**-** Sheathing should be impossible if no weapon is kept through Lost Inventory.\n" +
                "**-** Critical attacks with missile weapons causing incorrect critical indicator " +
                "for next physical attack."));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "nerfs"), false, null);
        changes.hardlight(CharSprite.NEGATIVE);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.LONGBOW), "Bow Weapon Nerf",
                "Bow weapon knockback can no longer knock enemies into pits."));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.ARCHER, 2),"Archer Nerf",
                "Similarly, Pushback talent can no longer knock enemies into pits."));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.ARCHER, 4),"Bowmaster Nerf",
                "Bowmaster combo now breaks with partial-turn actions that do not build combo."));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.MEDIC, 2),"Medic Nerf",
                "Medic's Healing Meal +1 no longer removes Hunger and Starving debuffs."));

    }
}
