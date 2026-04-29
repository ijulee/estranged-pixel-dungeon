package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.GammaRayGun;
import com.shatteredpixel.shatteredpixeldungeon.items.KnightsShield;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Afterimage;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Satisfying;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.bow.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.alchemy.PotOThunder;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.ChangeButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.ChangeInfo;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class EPD_v0_X_Changes {
    public static Image bugfix = new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16);
    public static Image bookshelf = new Image(Assets.Environment.TILES_CITY, 240, 96, 16, 16);

    public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ) {
        add_v0_0_Changes(changeInfos);
    }

    private static void add_v0_0_Changes(ArrayList<ChangeInfo> changeInfos) {
        ChangeInfo changes = new ChangeInfo("v0.0.8", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "new"), false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(Icons.ESTRANGED.get(), "Dev Commentary",
                Messages.get(EPD_v0_X_Changes.class, "v0_0_8a_comments")));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, null);
        changes.hardlight(CharSprite.WARNING);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(
                Icons.PREFS.get(), Messages.get(ChangesScene.class, "misc"),
                Messages.get(EPD_v0_X_Changes.class, "v0_0_8a_misc")));

        changes.addButton(new ChangeButton(
                new Image(bugfix), Messages.get(ChangesScene.class, "bugfixes"),
                Messages.get(EPD_v0_X_Changes.class, "v0_0_8a_bugfixes")));

        changes = new ChangeInfo("v0.0.8", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "new"), false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(Icons.ESTRANGED.get(), "Dev Commentary",
                Messages.get(EPD_v0_X_Changes.class, "v0_0_8_comments")));

        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.SAMURAI, 3), "Samurai Rework",
                Messages.get(EPD_v0_X_Changes.class, "v0_0_8_samurai_1"),
                Messages.get(EPD_v0_X_Changes.class, "v0_0_8_samurai_2"),
                Messages.get(EPD_v0_X_Changes.class, "v0_0_8_samurai_3"),
                Messages.get(EPD_v0_X_Changes.class, "v0_0_8_samurai_4")));

        changes.addButton(new ChangeButton(new HeroIcon(HeroSubClass.SLASHER), "Auraslasher Rework",
                Messages.get(EPD_v0_X_Changes.class, "v0_0_8_slasher_1"),
                Messages.get(EPD_v0_X_Changes.class, "v0_0_8_slasher_2")));

        changes.addButton(new ChangeButton(Icons.MAGNIFY_GRAY.get(), "Seedfinder is Back!",
                Messages.get(EPD_v0_X_Changes.class, "v0_0_8_seedfinder")));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, null);
        changes.hardlight(CharSprite.WARNING);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(
                Icons.PREFS.get(), Messages.get(ChangesScene.class, "misc"),
                Messages.get(EPD_v0_X_Changes.class, "v0_0_8_misc")));

        changes.addButton(new ChangeButton(
                new Image(bugfix), Messages.get(ChangesScene.class, "bugfixes"),
                Messages.get(EPD_v0_X_Changes.class, "v0_0_8_bugfixes")));

        changes = new ChangeInfo("v0.0.7", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "new"), false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(Icons.ESTRANGED.get(), "Dev Commentary",
                "**-** Released April 12, 2026\n\n" +
                "Here is the first stage of reworks (likely of many) to Blueprints to make them " +
                "more accessible and less complex. The end result will probably be a rare item, " +
                "that you have guaranteed access to but a limited number of, that takes in 2 " +
                "items to transform one of them into a stronger version. But that takes time " +
                "and planning, so I've made adjustments in the time being.\n" +
                "You'll also notice that there have been various Samurai changes. It's mostly " +
                "because I've been playing with the class in preparation of a rework. I might " +
                "start with the base class and 1/2 subclasses for the next update. Stay tuned.\n\n" +
                "_-- miaomix_") );

        changes.addButton(new ChangeButton(
                new ItemSprite(ItemSpriteSheet.BLUEPRINT), "Blueprints Rework 1",
                "Blueprints have been reworked to make them more accessible and reduce complexity. " +
                "I'm not happy with how it looks yet, so an overhaul will come.\n\n" +
                "**-** Added naturally spawning Blueprints:\n" +
                "  - 1 guaranteed spawn between 11-19F.\n" +
                "  - Can spawn in secret library rooms with 50% chance\n" +
                "  - Can spawn in locked armory rooms with 5% chance.\n" +
                "  - Can spawn in destroyed bookshelves with 5% chance (0.25% overall)\n" +
                "**-** Moved _Blueprint Weapons_ to its own category in Catalog.\n" +
                "**-** Changed Upgrade Dust requirement in 1-weapon recipes to any uncursed " +
                "melee weapon. If it's identified and upgraded, the upgrade will be transferred to " +
                "the Blueprint, which increases success rate.\n" +
                "**-** Blueprint name will now show the weapon name (e.g. \"_Chain Flail_ Blueprint\").\n" +
                "**-** If multiple weapon Blueprints can be crafted, they will all be shown in the " +
                "alchemy scene.\n" +
                "**-** Changed _Dual Dagger_ into a Blueprint weapon that requires a Dirk.") );

        changes.addButton(new ChangeButton(
                new ItemSprite(ItemSpriteSheet.SPEAR_N_SHIELD), "Spear and Shield Rework",
                "**-** Removed stance changing.\n" +
                "**-** Instead the weapon always provides blocking (same as Round Shield).\n" +
                "**-** Always has extra reach. Base damage, damage scaling, and attack delay " +
                "at range are the same as Spear.\n" +
                "**-** As close range, it has the same Base damage, damage scaling, and attack " +
                "delay as Round Shield.\n" +
                "**-** New Duelist ability: _Counter Spike_, which parries the next physical or " +
                "magical attack from a target enemy, then attacks and knocks back the " +
                "target up to 3 tiles away.") );

        changes.addButton(new ChangeButton(
                new ItemSprite(ItemSpriteSheet.CHAIN_FLAIL), "Chain Flail Rework",
                "**-** Now Tier 5 and has extra reach (3 tiles).\n" +
                "**-** Requirement changed to Whip from Chain Whip.\n" +
                "**-** Same accuracy, base damage, and damage scaling as Flail.") );

        changes.addButton(new ChangeButton(
                new TalentIcon(Talent.BASIC_PRACTICE), "Hit, Miss, and Critical Icons",
                "**-** Implemented critical hit damage icons, including versions for different " +
                "hit reasons and for armor penetration.\n" +
                "**-** In exchange, the _\"!\"_ crit indicator in text has been removed.\n" +
                "**-** Added new cases for armor penetration damage icons (Duelist Penetration " +
                "Shot ability, Samurai Shadow Blade armor ability, guns with AP bullet mod).\n" +
                "**-** Added logic to display more hit and miss icons.") );

        changes.addButton(new ChangeButton(Icons.CELL_LABEL.get(), "Cell Labels",
                "Added cell labels for certain actions with different behavior when selecting the hero:\n" +
                "**-** Sharpshooter's _Burst Shot_ for random targeting.\n" +
                "**-** Duelist's melee bow _SHOOT_ action for _Penetration Shot_ ability.\n" +
                "**-** Horseman's _Mounted Charge_ for dismounting the horse.\n" +
                "**-** Gun _SHOOT_ action for reloading (changes color when Duelist's " +
                "Quick Reload is available).\n" +
                "**-** Juggler's thrown weapons _THROW_ action for juggling.") );

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, null);
        changes.hardlight(CharSprite.WARNING);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.PREFS.get(), Messages.get(ChangesScene.class, "misc"),
                "**-** \n" +
                "**-** Changed Medic's Breakthrough talent (1-5) to give a bonus 2/3 points of " +
                "evasion per debuff.\n" +
                "**-** Slight adjustment to Spirit Horse animations.\n" +
                "**-** Clarified some Samurai T2 talent descriptions.\n" +
                "**-** Changed Blueprint weapon descriptions.\n" +
                "**-** Changed all katanas' stat descriptions to reflect their critical chance.\n" +
                "**-** Spirit Horse now has infinite accuracy.\n" +
                "**-** Changed description of Gunslinger's Moving Shot talent (3-8) for clarity.") );

        changes.addButton(new ChangeButton(
                new Image(bugfix), Messages.get(ChangesScene.class, "bugfixes"),
                "**-** Corrected fix for time stasis effects showing satiation specks.\n" +
                "**-** Fixed Spirit Horse sprite taking too long to attack.\n" +
                "**-** Fixed debug teleporter oddities.\n" +
                "**-** Fixed the lack of an energy value on Electricity Imbue Spell.\n" +
                "**-** Fixed sell value and energy of Rapid Growth, Freezing, and Ignition.\n" +
                "**-** Fixed bug where certain spells do not correctly trigger spell talents.\n" +
                "**-** Fixed Honeyed Healing energizing bug (present in ShatteredPD).\n" +
                "**-** Added missing name and desc for Duelist's Unholy Bible ability buff.\n" +
                "**-** Fixed bug where Duelist's Chain Flail Spin ability consumes Precise " +
                "Assault and Liquid Agility.",

                "**-** Fixed rare cases where Explorer with Durable Rope talent (3-8) can't attack " +
                "despite having enough rope after discount.\n" +
                "**-** Fixed Auraslasher Sword Aura autotarget oddities.\n" +
                "**-** Fixed Auraslasher's Sword Aura recovering more energy than maximum.\n" +
                "**-** Fixed bug where targeting cross sometimes isn't removed for Swordmaster's " +
                "Dash-Draw.\n" +
                "**-** Fixed incorrect crit chance in buff description of Swordmaster's Quick-Draw.\n" +
                "**-** Fixed potential bug where Swordsmaster has infinite accuracy with missile " +
                "weapons when Sheathed.\n" +
                "**-** Fixed bug where Samurai Shadow Blade ability's Piercing Shadow Talent (4-2) " +
                "bonus damage is halved.\n",

                "**-** Fixed Juggling generating arrow ammo when it shouldn't because arrows " +
                "don't consume ammo until they are thrown.\n" +
                "**-** Fixed Juggling buff potentially affecting non-juggling accuracy when active.\n" +
                "**-** Fixed Adrenaline on hero not affecting thrown weapon attack speed.\n" +
                "**-** Fixed hero button layout in Hero Select scene when there is a bottom inset " +
                "and future-proofed it (!), hopefully.\n" +
                "**-** Fixed bug where last chosen Challenges are not correctly loaded in some cases.") );

        changes.addButton(new ChangeButton(new HeroIcon(HeroSubClass.JUGGLER), "Juggler",
                "**-** Changed default action of thrown weapons for Juggler back to _THROW_.\n" +
                "**-** Added function to juggle instead of throw by selecting the hero.\n" +
                "**-** Juggling buff description now shows the enchantments and upgrade values of " +
                "thrown weapons.\n" +
                "**-** Changed Juggler talent names and updated descriptions."));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "buffs"), false, null);
        changes.hardlight(CharSprite.POSITIVE);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(
                new ItemSprite(ItemSpriteSheet.ARTIFACT_TOOLKIT), "Spell Buffs",
                "**-** Ignition spell can now ignite barricades and bookshelves even in storm " +
                "clouds. When targeting the hero, it will only ignite if current tile is flammable.\n" +
                "**-** Fire Imbue Spell now triggers Inscribed talents and has increased duration " +
                "of 20 turns (was 10). Cooldown increased to 250 turns in exchange.") );

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.NECKLACE_RUBY), "Gem Necklace",
                "Duelist using Gem Necklace with Ring of Force can now use Brawler Stance.") );

        changes.addButton(new ChangeButton(
                new ItemSprite(ItemSpriteSheet.ARMOR_SCALE, new Satisfying().glowing()),
                "Satiation Glyph",
                "Satiation glyph can now grant Well Fed buff if hero is fully satiated.") );

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.BIBLE), "Bible",
                "Changed Bible proc effect to instead randomly choose a buff to proc. If the buff " +
                "already applies, the chance is lowered. Healing amount also increased to (1+lvl).") );

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "nerfs"), false, null);
        changes.hardlight(CharSprite.NEGATIVE);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(
                new TalentIcon(Talent.BASIC_PRACTICE), "Critical Chance Calculation",
                "Changed crit chance calculation order, so \"bonus\" increases are not " +
                "affected by multipliers. Most notably, Samurai's Deadly Throw talent (2-6)."));

        changes.addButton(new ChangeButton(
                new ItemSprite(ItemSpriteSheet.ARMOR_SCALE, new Afterimage().glowing()),
                "Afterimage Glyph",
                "**-** Changed Afterimage glyph calculations to also apply to Ghost ally and " +
                "armored statues.\n" +
                "**-** Nerfed Afterimage glyph multiplier (1.125x per level, same as Ring of " +
                "Evasion) and applied before any \"bonus\" points of evasion.") );

        changes.addButton(new ChangeButton(new Image(bookshelf), "Bookshelf Loot",
                "Ring of Wealth only increases regular scroll drops from destroying bookshelves, " +
                "not other prizes.") );

        changes.addButton(new ChangeButton(
                new ItemSprite(ItemSpriteSheet.ARTIFACT_ROSE3), "Ghost Ally",
                "Ghost ally using melee bows and guns now uses the hero's ammo."));

        changes = new ChangeInfo("v0.0.6", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "new"), false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(Icons.ESTRANGED.get(), "Dev Commentary",
                "**-** Released March 30, 2026\n\n" +
                "Here's the non-debug Android build, as requested. There's a few minor changes that " +
                "I've already made, so I will release this as v0.0.6.\n\n" +
                "_-- miaomix_") );

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, null);
        changes.hardlight(CharSprite.WARNING);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.PREFS.get(), Messages.get(ChangesScene.class, "misc"),
                "**-** Corrected old Celesti icon.\n" +
                "**-** Changed patch messages in Welcome Scene and a few other places.\n" +
                "**-** Added Estrange PD Github repo link in About Page.\n" +
                "**-** Changed Re-Arranged PD icon to Estranged PD icon in the Title Scene.\n" +
                "**-** Added Gray icons for Seed Search and Analysis options in Hero Selection. " +
                "Actually fixing the features will take a while longer."));

        changes.addButton(new ChangeButton(new Image(bugfix), Messages.get(ChangesScene.class, "bugfixes"),
                "**-** Fixed Chaser's Lethal Surprise talent (3-8) not proccing at all."));

        changes = new ChangeInfo("v0.0.5", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "new"), false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(Icons.ESTRANGED.get(), "Dev Commentary",
                "**-** Released March 27, 2026\n\n" +
                "I always try to fix a little bug in one thing, then end up refactoring the " +
                "entire feature.\n\nV0.0.5 has probably the most significant changes since the " +
                "start of this mod: **weapon reworks**. Some Re-Arranged weapons have been removed " +
                "from the pool, while Katanas and Sniper Rifles have a new mechanic now. At the " +
                "same time, ammo generation has been reduced so that it won't be as abundant now. " +
                "Further reworks are being planned so stay tuned.\n\n" +
                "_-- miaomix_") );

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.ARTIFACT_ROSE3), "Dried Rose Ghost Rework",
                "Reworked the Ghost ally's AI:\n" +
                "**-** The Ghost will **also shoot bows** until bow fatigue reaches 80% damage.\n" +
                "**-** The Ghost now has a reloading animation, which will play when reloading a gun " +
                "until their next action.\n" +
                "**-** The Ghost can **shoot explosive guns, flamethrowers, and laser guns** with all " +
                "animations (if visible) and side effects.\n" +
                "**-** The Ghost will avoid hitting the hero and themselves while shooting the " +
                "those guns.\n" +
                "**-** The Ghost now will say exactly how they will behave when Auto-Reloading is " +
                "toggled ON and OFF. Dried Rose description has also changed accordingly.\n" +
                "**-** The Ghost now says that they will reload during combat, when you manually " +
                "direct them to do so by selecting their current position (this part is " +
                "existing function).\n" +
                "**-** Fixed quirks like reloading or shooting faster than expected.\n" +
                "**-** Dried Rose now prevents detaching an equipped gun if Ghost is still " +
                "reloading."));

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.LARGE_KATANA), "Re-Arranged Weapons Rework 1",
                "Here is the first part of likely many waves of reworks to weapons introduced in " +
                "Re-Arranged, including a more comprehensive gun rework being planned.\n\n" +
                "Removed the following melee weapons from random generation:\n" +
                "**-** **Tier 2:** Dual Dagger, Kitchen Knife, Short Katana, Antique Handgun\n" +
                "**-** **Tier 3:** Machine Gun, Grenade Launcher\n" +
                "**-** **Tier 4:** Long Katana\n" +
                "**-** **Tier 5:** Broadsword",

                "In addition, **Katanas** (not the Shattered one) and **Sniper Rifle bullets** have been " +
                "changed, so they're not simply weapons that are 1 tier higher with lower " +
                "strength requirements.\n" +
                "**-** Katanas now have lower base max damage, but with a base crit chance that " +
                "applies regardless of class.\n" +
                "**-** Both now have a \"flat\" damage distribution, which means they are more " +
                "likely to roll closer to min and max damage (doubled chances to roll lower than " +
                "25% or higher than 75% towards max, in fact).",

                "Miscellaneous:\n" +
                "**-** Drop rates for all melee bows are reduced to 50%, like guns.\n" +
                "**-** Scalpel base damage reduced to 1-6 (was 1-8).\n" +
                "**-** Some of the removed weapons may be reworked or returned as blueprint weapons " +
                "in later updates."));

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.BOOK_OF_FIRE), "Spellbook in Shops",
                "Added Spellbooks to shop item generation. It takes the same \"rare\" item " +
                "slot as wands, artifacts, rings, and arcane stylus, occurring with a 10% chance."));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, null);
        changes.hardlight(CharSprite.WARNING);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(new HeroIcon(HeroSubClass.HORSEMAN), "Horseman",
                "Fixes and changes to Horseman's Charge attack:\n" +
                "**-** Added action name (now named **Mounted Charge**).\n" +
                "**-** Fixed Mounted Charge pushing immobile characters.\n" +
                "**-** Removed hidden feature that instantly kills characters at the Charge " +
                "destination that has no tile to move on.\n" +
                "**-** Instead, characters are successively pushed out of the way after attacks " +
                "if the Charge destination is occupied.\n" +
                "**-** Added confirmation window when the Charge recoil (before armor and damage " +
                "reduction) is greater than 90% HP.\n" +
                "**-** Horse ally can now attack (for 0 damage) and aggro enemies."));

        changes.addButton(new ChangeButton(new HeroIcon(HeroSubClass.BOWMASTER), "Bowmaster",
                "**-** Made Bowmaster's Arrow Combo/Powershot descriptions more detailed.\n" +
                "**-** Added descriptive game log text when pressing the Action Indicator at " +
                "each stage of Arrow Combo/Powershot.\n"));

        changes.addButton(new ChangeButton(new HeroIcon(HeroSubClass.MASTER), "Swordmaster",
                "**-** Changed Swordmaster's Dash Draw to use new action targeting function.\n" +
                "**-** Fixed behavior when clicking Dash Draw with target selected.\n" +
                "**-** Dash Draw will now blink next to the target tile if it is a wall or chasm."));

        changes.addButton(new ChangeButton(new HeroIcon(HeroSubClass.FIGHTER), "Fighter",
                "Adjusted Figher's talent attack talent procs (3-9, 3-10, 3-11).\n" +
                "**-** Text fix for Ring Knuckle (3-9) to indicate that it will not apply only for " +
                "Ring of Force.\n" +
                "**-** Mystic Punch (3-10) can now proc separately for each ring equipped.\n" +
                "**-** Added buff name, desc, and announcement on proc for Quick Step (3-11)."));

        changes.addButton( new ChangeButton(Icons.PREFS.get(), Messages.get(ChangesScene.class, "misc"),
                "**-** Knight's Kinetic Battle talent was reworked slightly. Max bonus damage is " +
                "now 3 but lasts for 5/8 turns.\n" +
                "**-** Various small text changes."));

        changes.addButton(new ChangeButton(new Image(bugfix), Messages.get(ChangesScene.class, "bugfixes"),
                "**-** Fixed issue with Bowmaster Moving Focus talent (3-9) not working as intended.\n" +
                "**-** Fixed challenge randomizer not able to select more than 9 challenges or any " +
                "of the RPD challenges.\n" +
                "**-** Fixed Talent menu's tier 3 randomize button not being aligned correctly.\n" +
                "**-** Fixed Duelist Quick Reload ability exceeding double capacity in some cases.\n" +
                "**-** Fixed guns equipped by Duelist not having correct round count text color " +
                "according to weapon charge.\n" +
                "**-** Fixed Pocket Knife being able to attack mobs that charmed the hero.",
                "**-** Fixed bug with Auraslahser's Sword Aura not penetrating characters without " +
                "Projection enchantment.\n" +
                "**-** Attempted to fix visual glitches from Swordmaster's Inner Eye talent " +
                "(3-11) not keeping tiles revealed after expiration.\n" +
                "**-** Fixed Afterimage glyph on Knight's Shield and Armor Adaptation talent (2-3) " +
                "applying to every armor in existence.\n" +
                "**-** Fixed bug where Ghost ally will identify guns and bows by shooting them."));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "buffs"), false, null);
        changes.hardlight(CharSprite.POSITIVE);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.LONGBOW), "Melee Bow buff",
                "**-** Removed adjacency requirement for melee knockback. It should now work with " +
                "Projecting enchantment, Book of Disintegration, etc.\n" +
                "**-** Also fixed the debug duration for Arrow Attached debuff, and changed it to a more " +
                "reasonable 30 turns."));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "nerfs"), false, null);
        changes.hardlight(CharSprite.NEGATIVE);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.AMMO_BELT), "Availability of Ammo",
                "I thought ammo is way too abundant, and have reduced the availability. Hopefully, " +
                "the changes incentivize people to actually buy and craft ammo belts. These " +
                "numbers aren't final and may change when I see how it tests. More comprehensive " +
                "gun reworks may prompt changes here as well.\n\n" +
                "**-** A bugfix that's effectively a nerf: Ammo Belts were supposed to be " +
                "limited to 1 per region.\n" +
                "**-** Shops in each region will only sell half the amount of Ammo Belts (1/1/2/2/3 " +
                "belts for each shop)."));

        changes.addButton( new ChangeButton(new BuffIcon(BuffIndicator.BOW_FATIGUE, true), "Bow Fatigue",
                "The damage drop is now a bit harsher, starting after the first shot, but " +
                "has a cap of 50% damage reduction. It also has a new buff icon."));

        changes = new ChangeInfo("v0.0.4", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "new"), false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.ESTRANGED.get(), "Dev Commentary",
                "**-** Released March 10, 2026\n\n" +
                "I was working on something bigger, but decided to do this hotfix to fix a few " +
                "glaring bugs.\n\n" +
                "_-- miaomix_") );

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, null);
        changes.hardlight(CharSprite.WARNING);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.HERO_SWORD), "Duelist's Hero Sword",
                "The Duelist's Hero Sword can now have an enchantment glow if the source weapon " +
                "has an enchantment, and dual glow if the sword itself has a different one. The " +
                "item name will also contain the source weapon's enchantment name."));

        changes.addButton(new ChangeButton(new Image(bugfix), Messages.get(ChangesScene.class, "bugfixes"),
                "**-** Fixed Skeleton Key causing crash when picking up keys in Lab region. " +
                "I tried to salvage EPD v0.0.3 save files as best I could.\n" +
                "**-** Fixed Skeleton Key incorrectly discarding Crystal Keys in Old Temple.\n" +
                "**-** Fixed City quest Escape Crystal missing sprite.\n" +
                "**-** Fixed certain targeted spells freezing the game when casted, unless " +
                "the game is reloaded."));

        changes = new ChangeInfo("v0.0.3", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "new"), false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.ESTRANGED.get(), "Dev Commentary",
                "**-** Released March 6, 2026\n\n" +
                "Getting back into the flow of things. As a follow-up to the Old Amulet " +
                "transformation for Warrior, I have now reworked talents and added QoL changes to " +
                "Veteran. Up next is probably pruning the bloated weapon pool.\n\n" +
                "As an aside, I am back on Discord for now, for as long as Discord delays the " +
                "unnecessary and intrusive enforcement of age verification.\n\n" +
                "_-- miaomix_") );

        changes.addButton( new ChangeButton(Icons.SHPX.get(), "SPD v3.3.6",
                "Rebased to Shattered Pixel Dungeon v3.3.6.") );

        changes.addButton( new ChangeButton(new BuffIcon(BuffIndicator.TACKLING, true),
                "Veteran Rework",
                "Tackle mechanics and subclass talents have been reworked:\n\n" +
                "**-** Veteran subclass talents have been renamed and icons changed.\n" +
                "**-** Tackle is now triggered when attacking a mob with a melee weapon at any range, " +
                "and lasts up to 3 turns after the attack.\n" +
                "**-** There is now a buff icon and description for Tackle when it is active.\n" +
                "**-** Tackle now identifies the armor you're wearing.\n" +
                "**-** Tackle can no longer stun enemies by knocking them into walls. In exchange," +
                "the Vital Point Strike talent (3-10) now applies paralysis at +3.\n" +
                "**-** Increased Tackle damage to 60% of damage blocking power.",
                "Veteran rework (cont.):\n\n" +
                "**-** Bull Rush talent (3-7) increases Tackle damage to 80%/100%/120% of damage blocking " +
                "power. In exchange, you now also take 20%/40%/60% recoil damage (reduced by armor).\n" +
                "**-** Instead of using weapon enchantment, Tackle will now apply a bonus effect " +
                "based on armor glyph, if you have the Enchanted Armor talent (3-8).\n" +
                "**-** Brace for Impact talent (3-11) has been reduced to 40%/60%/80%.\n" +
                "**-** Strength Training talent (3-12) effect to knock enemies into pits at +3 was " +
                "removed. Instead, you are able to Tackle any adjacent enemy at +3."));

        changes.addButton( new ChangeButton(new TalentIcon(Talent.BETTER_CHOICE), "Better Choice Rework",
                "The Better Choice generic Tier 3 talent seems a bit plain and uninspiring. " +
                "Trading 3 points for essentially 2 upgrades doesn't feel like a strategic decision " +
                "at all. As an alternative, I have reworked it into _Trial by Fire_, which rewards " +
                "you for completing boss challenge badges. You no longer receive any upgrades, but " +
                "you do get items that could help you complete your loadout."));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, null);
        changes.hardlight(CharSprite.WARNING);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.TARGET.get(), "Hero Action Targeting",
                "Improved targeting functionality for hero actions:\n" +
                "**-** Crosshair appears on the Action Indicator tag to signal cell selection.\n" +
                "**-** Targets can be switched using the Danger Indicator tag.\n" +
                "**-** Should work seamlessly without leaving \"phantom\" crosshairs.\n\n" +
                "These changes apply to the Auraslasher's Sword Aura, the Juggler's Juggling, and " +
                "the Veteran's Tackle."));

        changes.addButton( new ChangeButton(Icons.PREFS.get(), Messages.get(ChangesScene.class, "misc"),
                "**-** The Medic's Gamma Ray Gun will now aggro enemies, like damage-dealing wands.\n" +
                "**-** Spellbooks can now be transmuted into other flavors."));

        changes.addButton(new ChangeButton(new Image(bugfix), Messages.get(ChangesScene.class, "bugfixes"),
                "**-** Some mobs and allies were not affected by Mystic Grindstone.\n" +
                "**-** Fixed bug where Sword Aura can't be used.\n" +
                "**-** Fixed Satiation glyph giving 0 satiation at +0 (and triggering starvation " +
                "damage). Effectively a 1 level buff for the glyph.\n" +
                "**-** Removed erroneous satiation gain visual effect during time stasis effects.\n" +
                "**-** Fixed armor and broken seal with glyphs not having the correct name, desc, and glow.\n" +
                "**-** Fixed Fighter's Mystic Punch talent bonus effect for Ring of Sharpshooting " +
                "incorrectly chasming enemies.\n" +
                "**-** Fixed Arrow Pincushion dropping \"0 arrows\"."));

        changes = new ChangeInfo("v0.0.2", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "new"), false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.ESTRANGED.get(), "Dev Commentary",
                "**-** Released February 18, 2026\n" +
                "**-** Based on Re-Arranged v3.40.0-FINAL and Shattered v3.3.1\n\n" +
                "Sorry to have taken this long to release a new update. Life happened. Also I wanted " +
                "to implement a major change, rather than just fixes and QoL changes, which took a " +
                "while to sort out properly.\n\n" +
                "One thing I must mention. I haven't been on Discord, but the news about " +
                "requiring ID or face verification is concerning. I may decide to leave " +
                "Discord entirely, which is unfortunate as it has the biggest PD community " +
                "that I know, but it can't be helped.\n\n" +
                "_-- miaomix_") );

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.SEAL2), "Old Amulet effect for Warrior",
                "Added a new Old Amulet transformation for Warrior: the restored " +
                "Warrior's Seal. The bonus effects are very simple, but Warrior is meant to be a " +
                "simple class in the first place.\n\n" +
                "The Broken Seal becomes the Warrior's seal, restored by the power of the Old Amulet. " +
                "It can carry an additional upgrade and, if you have the Runic Transference talent, " +
                "hold and apply its own glyph, in addition to the Armor's glyph."));

        changes.addButton( new ChangeButton(new ItemSprite(ItemSpriteSheet.MASTERY), "New Guidebook Page",
                "Added a new guidebook page explaining critical hits, and removed " +
                "some of that info from item, hero, and talent descriptions. Doing my best to " +
                "avoid walls of text in descriptions."));

        changes.addButton( new ChangeButton(new SpiritBow(),
                "Added a confirmation window for Spirit Bow's Old Amulet transformation. " +
                "Now you have a choice of 2 special bows. You can view the description of each bow " +
                "before choosing it. The choices remain the same even if you decide to use the " +
                "Amulet later."));

        changes.addButton( new ChangeButton(new PotOThunder(),
                "Renamed Pot'o'Thunder to Thorhammer (sprite is WIP), and changed the " +
                "recipe requirement to Throwing Hammer. It just makes more sense to me (sorry in " +
                "advance if there's a reference I'm not aware of), and gives Throwing Hammer " +
                "a use in alchemy instead of Force Cube, which is already used for Force Glove."));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, null);
        changes.hardlight(CharSprite.WARNING);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.SAMURAI, 4),"Swordmaster Changes",
                "**-** Change talent descriptions for accuracy and clarity.\n" +
                "**-** As Swordmaster sheathing buff now indicates if next attack will be Quick Draw.\n" +
                "**-** Separate, clearer warning messages for Dash Draw."));

        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.HUNTRESS, 4),"Fighter Changes",
                "**-** Changed talent descriptions for accuracy and clarity.\n\n" +
                "Changed Mystic Punch ring bonus effect descriptions and effects:\n" +
                "**-** Ring of Arcana: now simply applies an Unstable enchantment, fixed Kinetic " +
                "conserved damage timing\n" +
                "**-** Ring of Accuracy: no longer inflicts both debuffs at the same time\n" +
                "**-** Ring of Elements: no longer cleanses hunger\n" +
                "**-** Ring of Sharpshooting: fixed knockback and throwie repair"));

        changes.addButton(new ChangeButton(new GammaRayGun(),
                "**-** Now has less opaque RNG for cooldown (visible in debug builds).\n" +
                "**-** Changed Radioactive debuff icon, name, and descriptions."));

        changes.addButton(new ChangeButton(new KnightsShield(),
                "**-** Knight's shield can now be inscribed by Stone/Scroll of Enchantment."));

        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "**-** Various text changes.\n" +
                "**-** Changed how guns process multiple targets again.\n" +
                "**-** Multi-target Elastic projectile weapons no longer knocks back on " +
                "self-damage.\n" +
                "**-** Renamed \"Satisfying Glyph\" to \"Glyph of Satiation\".\n" +
                "**-** Swapped rarities of Satiation and Afterimage (now uncommon and rare, " +
                "respectively).\n" +
                "**-** Added ReARranged additional enchantments (Stunning, Eldritch, " +
                "Venomous, and Vorpal) to potential Unstable effects."));

        changes.addButton(new ChangeButton(new Image(bugfix), Messages.get(ChangesScene.class, "bugfixes"),
                "**-** Fixed Swordmaster Quick Draw proccing incorrectly, sometimes " +
                "alongside Dash Draw.\n" +
                "**-** Fixed incorrect critical indicator for a non-critical physical attack " +
                "occurring after a critical missile attack.\n" +
                "**-** Fixed missing \"Randomize\" icon."));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "nerfs"), false, null);
        changes.hardlight(CharSprite.NEGATIVE);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.TRUE_RUNIC_BLADE), "True Runic Blade Nerf",
                "True Runic Blade is now a Tier 6 weapon, while stats remain the same. " +
                "Its power is just too high for a Tier 5 blueprint melee weapon. I plan to make " +
                "lower tier weapon blueprints more accessible in the future."));

        changes = new ChangeInfo("v0.0.1", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "new"), false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton( new ChangeButton(Icons.ESTRANGED.get(), "Dev Commentary",
                "**-** Released December 15, 2025\n" +
                "**-** Based on Re-Arranged v3.40.0-FINAL and Shattered v3.3.0\n\n" +
                "Hi there. Long time player, first time modder. Re-Arranged is what I'd call an " +
                "\"add-on\" mod to Shattered, one that keeps up with SPD's content updates, and " +
                "only seeks to add content, rather than taking the game in a different " +
                "direction.\n\n" +
                "I was sad to hear that Hoto-Mocha (Cocoa) will not continue developing " +
                "Re-Arranged due to personal issues. It's clear that there was still a lot of " +
                "content waiting to be fleshed out and refined, and that's what I will aim to " +
                "do with EstRanged. At the same time, I will keep this mod up-to-date with " +
                "Shattered releases as well.\n\n" +
                "This first update will consist mostly of a Shattered rebase to v3.3.0, plus " +
                "translation and bug fixes, but I'll try to implement more interesting changes " +
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
                "boost (The last 2 you can see in Elixir of Talents)."));

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
                "**-** Moved some info from bow item descriptions into Adventurer's Guide to make " +
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
                "**-** Moved some info from gun item descriptions into Adventurer's Guide to make " +
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

        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "**- ALL** classes now start with 50 ammo. Gunner and Archer both start " +
                "with 1 less Ammo belt to compensate. This is to prevent shooting weapons being " +
                "unusable in Sewers for the other classes if no ammo belt is found.\n" +
                "**-** Changed Elixir of Talent description to be more clear and concise " +
                "(felt like writing a modern Yu-Gi-Oh! card)."));

        changes.addButton(new ChangeButton(new Image(bugfix), Messages.get(ChangesScene.class, "bugfixes"),
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
                "**-** Equipped ring descriptions now properly account for Ring Necklace.\n" +
                "**-** Potential bug where Ring Necklace provided extra benefit on game load.",

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
        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.CROSS),"Cross Nerf",
                "The Cross is no longer infinite durability (10 base durability), and " +
                "now takes 5 turns to return like the boomerang."));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.ARCHER, 2),"Archer Nerf",
                "Similarly, Pushback talent can no longer knock enemies into pits."));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.ARCHER, 4),"Bowmaster Nerf",
                "Bowmaster combo now breaks with partial-turn actions that do not build combo."));
        changes.addButton(new ChangeButton(HeroSprite.avatar(HeroClass.MEDIC, 2),"Medic Nerf",
                "Medic's Healing Meal +1 no longer removes Hunger and Starving debuffs."));


    }
}
