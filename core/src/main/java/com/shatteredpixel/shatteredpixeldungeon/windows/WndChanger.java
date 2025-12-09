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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.changer.BluePrint;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.MagicalInfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Crossbow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatshield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RoundShield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.LanceNShield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.ObsidianShield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.SpearNShield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.alchemy.TacticalShield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.bow.BowWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun.Gun;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Tomahawk;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.alchemy.PotOThunder;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class WndChanger extends Window {

    private static final int WIDTH = 120;

    private static final float COL_1 = WIDTH/4f;
    private static final float COL_2 = 5*WIDTH/8f;
    private static final float COL_3 = 7*WIDTH/8f;

    private static final int GAP	= 2;
    private static final int ITEMSLOT_SIZE = 18;

    private Item changer;
    //private boolean force;

    private RedButton btnChange;
    private RedButton btnCancel;

    public WndChanger(Item changer, Item original, Item result){

        this.changer = changer;
        //this.force = force;

        IconTitle title = new IconTitle( new ItemSprite(changer), Messages.get(this, "title") );

        title.setRect(0, 0, WIDTH, 0);
        add(title);

        String mainText = Messages.get(this, "desc");

        RenderedTextBlock message = PixelScene.renderTextBlock( 6 );
        message.text( mainText, WIDTH);
        message.setPos(0, title.bottom()+GAP);
        add(message);

        // *** Computing level to display ***

        int level = original.isIdentified() ? original.level() : 0;

        boolean curseInfused = (original instanceof Weapon && ((Weapon) original).curseInfusionBonus)
                || (original instanceof Armor && ((Armor) original).curseInfusionBonus)
                || (original instanceof Wand && ((Wand) original).curseInfusionBonus);

        // *** Sprites, showing original and resulting items ***

        ColorBlock bg1 = new ColorBlock(ITEMSLOT_SIZE, ITEMSLOT_SIZE, 0x9953564D);
        bg1.x = COL_2 - ITEMSLOT_SIZE/2f;
        bg1.y = message.bottom() + 2*GAP;
        add(bg1);

        ColorBlock bg2 = new ColorBlock(ITEMSLOT_SIZE, ITEMSLOT_SIZE, 0x9953564D);
        bg2.x = COL_3 - ITEMSLOT_SIZE/2f;
        bg2.y = message.bottom() + 2*GAP;
        add(bg2);

        if (!original.isIdentified()){
            if (!original.cursed && original.cursedKnown){
                bg1.hardlight(1f, 1, 2f);
                bg2.hardlight(1f, 1, 2f);
            } else {
                bg1.hardlight(2f, 1, 2f);
                bg2.hardlight(2f, 1, 2f);
            }
        } else if (original.cursed && original.cursedKnown){
            bg1.hardlight(2f, 0.5f, 1f);
            bg2.hardlight(2f, 0.5f, 1f);
        }

        ItemSprite i1 = new ItemSprite();
        add(i1);
        i1.view(original);
        i1.x = COL_2 - i1.width()/2f;
        i1.y = bg1.y + (ITEMSLOT_SIZE-i1.height())/2f;
        PixelScene.align(i1);
        add(i1);

        ItemSprite i2 = new ItemSprite();
        add(i2);
        i2.view(result);
        i2.x = COL_3 - i2.width()/2f;
        i2.y = i1.y;
        PixelScene.align(i2);
        add(i2);

        BitmapText t1 = new BitmapText(PixelScene.pixelFont);
        BitmapText t2 = new BitmapText(PixelScene.pixelFont);
        if (original.isIdentified()){
            if (level > 0){
                t1.text("+" + level);
                t2.text("+" + level);

            } else {
                t1.text("");
                t2.text("");
            }
            t1.hardlight(ItemSlot.UPGRADED);
            t2.hardlight(ItemSlot.UPGRADED);

            if (curseInfused){
                t1.hardlight(ItemSlot.CURSE_INFUSED);
                t2.hardlight(ItemSlot.CURSE_INFUSED);
            }
        }

        t1.measure();
        t1.x = COL_2 + ITEMSLOT_SIZE/2f - t1.width();
        t1.y = bg1.y + ITEMSLOT_SIZE - t1.baseLine() - 1;
        add(t1);

        t2.measure();
        t2.x = COL_3 + ITEMSLOT_SIZE/2f - t2.width();
        t2.y = bg2.y + ITEMSLOT_SIZE - t2.baseLine() - 1;
        add(t2);

        float bottom = i1.y + ITEMSLOT_SIZE;

        // *** Various lines for stats, highlighting differences between original and result ***

        if (original instanceof MeleeWeapon) {
            MeleeWeapon meleeOriginal = ((MeleeWeapon) original);
            MeleeWeapon meleeResult = ((MeleeWeapon) result);

            Weapon.Augment aug = meleeOriginal.augment;

            // tier
            bottom = fillFields(Messages.get(this, "tier"),
                    Integer.toString(meleeOriginal.tier()),
                    Integer.toString(meleeResult.tier()),
                    bottom);

            //physical damage
            bottom = fillFields(Messages.get(this, "damage"),
                    aug.damageFactor(meleeOriginal.min(level)) + "-" + aug.damageFactor(meleeOriginal.max(level)),
                    aug.damageFactor(meleeResult.min(level)) + "-" + aug.damageFactor(meleeResult.max(level)),
                    bottom);

            // ability name
            if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.DUELIST) {
                bottom = fillFields(Messages.get(this, "ability"),
                        Messages.get(meleeOriginal, "ability_name"),
                        Messages.get(meleeResult, "ability_name"),
                        bottom);
            }

            //weight (i.e. strength requirement)
            bottom = fillFields(Messages.get(this, "weight"),
                    Integer.toString(meleeOriginal.STRReq(level)),
                    Integer.toString(meleeResult.STRReq(level)),
                    bottom);

            //gun stats
            if (result instanceof Gun) {
                String originalDmg = "n/a";
                String originalShots = "n/a";
                if (original instanceof Gun) {
                    Gun gun = (Gun) original;
                    int min = gun.modDamageFactor(aug.damageFactor(gun.missileMin(level)));
                    int max = gun.modDamageFactor(aug.damageFactor(gun.missileMax(level)));
                    originalDmg = min + "-" + max;
                    originalShots = Integer.toString(gun.shotsPerRound());
                }

                Gun gun = (Gun) result;
                int min = gun.modDamageFactor(aug.damageFactor(gun.missileMin(level)));
                int max = gun.modDamageFactor(aug.damageFactor(gun.missileMax(level)));
                String resultDmg = min + "-" + max;
                String resultShots = Integer.toString(gun.shotsPerRound());

                bottom = fillFields(Messages.get(this, "bullet_damage"), originalDmg, resultDmg, bottom);
                bottom = fillFields(Messages.get(this, "bullet_shots"), originalShots, resultShots, bottom);
            }

            //bow damage
            if (result instanceof BowWeapon) {
                String originalDmg = "n/a";
                if (original instanceof BowWeapon) {
                    BowWeapon bow = (BowWeapon) original;
                    int min = aug.damageFactor(bow.missileMin(level)),
                            max = aug.damageFactor(bow.missileMax(level));
                    originalDmg = min + "-" + max;
                }

                BowWeapon bow = (BowWeapon) result;
                int min = aug.damageFactor(bow.missileMin(level)),
                        max = aug.damageFactor(bow.missileMax(level));
                String resultDmg = min + "-" + max;

                bottom = fillFields(Messages.get(this, "arrow_damage"), originalDmg, resultDmg, bottom);
            }

            //blocking (shields)
            int resultBlk = meleeResult.defenseFactor(Dungeon.hero);
            if (resultBlk != 0) {
                int originalBlk = meleeOriginal.defenseFactor(Dungeon.hero);
                bottom = fillFields(Messages.get(this, "blocking"),
                        ((originalBlk == 0) ? "" : 0 + "-") + originalBlk,
                        0 + "-" + resultBlk,
                        bottom);
            }
        }

        //we use a separate reference for wand properties so that mage's staff can include them
        /*Item wand = original;
        if (original instanceof MagesStaff && ((MagesStaff) original).wandClass() != null){
            wand = Reflection.newInstance(((MagesStaff) original).wandClass());
        }

        //Various wand stats (varies by wand)
        if (wand instanceof Wand){
            if (((Wand) wand).upgradeStat1(level) != null){
                bottom = fillFields(Messages.get(wand, "upgrade_stat_name_1"),
                        ((Wand) wand).upgradeStat1(level),
                        ((Wand) wand).upgradeStat1(level),
                        bottom);
            }
            if (((Wand) wand).upgradeStat2(level) != null){
                bottom = fillFields(Messages.get(wand, "upgrade_stat_name_2"),
                        ((Wand) wand).upgradeStat2(level),
                        ((Wand) wand).upgradeStat2(level),
                        bottom);
            }
            if (((Wand) wand).upgradeStat3(level) != null){
                bottom = fillFields(Messages.get(wand, "upgrade_stat_name_3"),
                        ((Wand) wand).upgradeStat3(level),
                        ((Wand) wand).upgradeStat3(level),
                        bottom);
            }
        }

        //max charges
        if (wand instanceof Wand){
            int chargeboost = level + (original instanceof MagesStaff ? 1 : 0);
            bottom = fillFields(Messages.get(this, "charges"),
                    Integer.toString(Math.min(10, ((Wand) wand).initialCharges() + chargeboost)),
                    Integer.toString(Math.min(10, ((Wand) wand).initialCharges() + chargeboost + 1)),
                    bottom);
        }*/

        //Various ring stats (varies by ring)
        /*if (original instanceof Ring){
            if (((Ring) original).isKnown()) {
                if (((Ring) original).upgradeStat1(level) != null) {
                    bottom = fillFields(Messages.get(original, "upgrade_stat_name_1"),
                            ((Ring) original).upgradeStat1(level),
                            ((Ring) original).upgradeStat1(level),
                            bottom);
                }
                if (((Ring) original).upgradeStat2(level) != null) {
                    bottom = fillFields(Messages.get(original, "upgrade_stat_name_2"),
                            ((Ring) original).upgradeStat2(level),
                            ((Ring) original).upgradeStat2(level),
                            bottom);
                }
                if (((Ring) original).upgradeStat3(level) != null) {
                    bottom = fillFields(Messages.get(original, "upgrade_stat_name_3"),
                            ((Ring) original).upgradeStat3(level),
                            ((Ring) original).upgradeStat3(level),
                            bottom);
                }
            }
        }*/

        //visual separators for each column
        ColorBlock sep = new ColorBlock(1, 1, 0xFF222222);
        sep.size(1, bottom - message.bottom());
        sep.x = WIDTH/2f;
        sep.y = message.bottom() + GAP;
        add(sep);

        sep = new ColorBlock(1, 1, 0xFF222222);
        sep.size(1, bottom - message.bottom());
        sep.x = 3*WIDTH/4f;
        sep.y = message.bottom() + GAP;
        add(sep);

        // *** Various extra info texts that can appear underneath stats ***

        //warning relating to identification
        if (!original.isIdentified()) {
            if (original instanceof Ring && !((Ring) original).isKnown()){
                /*bottom = addMessage(Messages.get(this, "unknown_ring"), CharSprite.WARNING, bottom);*/
            } else {
                bottom = addMessage(Messages.get(this, "unided"), CharSprite.WARNING, bottom);
            }
        }

        //transmute chance for blueprints
        if (changer instanceof BluePrint && original instanceof MeleeWeapon) {
            float chance = 100 * ((BluePrint) changer).transmuteChance((MeleeWeapon) original);
            int color = (chance > 90) ? CharSprite.DEFAULT :
                        (chance > 60) ? CharSprite.NEUTRAL :
                                        CharSprite.WARNING;
            bottom = addMessage(
                    Messages.get(this, "blueprint_success", chance),
                    color, bottom);
        }

        // various messages relating to enchantments and curses
        /*if (!(changer instanceof MagicalInfusion)) {

            if ((original instanceof Weapon && ((Weapon) original).hasGoodEnchant())
                    || (original instanceof Armor && ((Armor) original).hasGoodGlyph())) {
                int lossChance;
                if ((original instanceof Weapon && ((Weapon) original).enchantHardened)
                        || (original instanceof Armor && ((Armor) original).glyphHardened)) {
                    lossChance = Math.min(100, 10 * (int) Math.pow(2, level - 6));
                } else {
                    lossChance = Math.min(100, 10 * (int) Math.pow(2, level - 4));
                    if (Dungeon.hero != null && Dungeon.hero.heroClass != HeroClass.WARRIOR && Dungeon.hero.hasTalent(Talent.RUNIC_TRANSFERENCE)){
                        if (level < 5+Dungeon.hero.pointsInTalent(Talent.RUNIC_TRANSFERENCE)){
                            lossChance = 0;
                        }
                    }
                }

                if (lossChance >= 10) {
                    String warn;
                    if (original instanceof Weapon) {
                        if (((Weapon) original).enchantHardened) {
                            warn = Messages.get(this, "harden", lossChance);
                        } else {
                            warn = Messages.get(this, "enchant", lossChance);
                        }
                    } else {
                        if (((Armor) original).glyphHardened) {
                            warn = Messages.get(this, "harden", lossChance);
                        } else {
                            warn = Messages.get(this, "glyph", lossChance);
                        }
                    }
                    bottom = addMessage(warn, CharSprite.WARNING, bottom);
                }
            }

            if ((original.cursed
                    || (original instanceof Weapon && ((Weapon) original).hasCurseEnchant())
                    || (original instanceof Armor && ((Armor) original).hasCurseGlyph()))
                    && original.cursedKnown) {

                if (original.cursed && (original instanceof MeleeWeapon && ((Weapon) original).hasCurseEnchant())
                        || (original instanceof Armor && ((Armor) original).hasCurseGlyph())){
                    bottom = addMessage(Messages.get(this, "cursed_weaken"), CharSprite.POSITIVE, bottom);
                } else {
                    bottom = addMessage(Messages.get(this, "cursed"), CharSprite.POSITIVE, bottom);
                }

                if (curseInfused) {
                    bottom = addMessage(Messages.get(this, "curse_infusion"), CharSprite.WARNING, bottom);
                }
            }
        }*/

        /*//warning relating to arcane resin
        if (original instanceof Wand && ((Wand) original).resinBonus > 0){
            bottom = addMessage(Messages.get(this, "resin"), CharSprite.WARNING, bottom);
        }

        if (original instanceof MissileWeapon && ((MissileWeapon) original).extraThrownLeft){
            bottom = addMessage(Messages.get(this, "thrown_dust"), CharSprite.WARNING, bottom);
        }*/

        // *** Buttons for confirming/cancelling ***

        btnChange = new RedButton(Messages.get(this, "transform")) {
            @Override
            protected void onClick() {
                super.onClick();

                if (changer instanceof BluePrint) {
                    ((BluePrint) changer).onItemSelected(original);
                }

                hide();
            }
        };
        btnChange.setRect(0, bottom+2*GAP, WIDTH/2f, 16);
        add(btnChange);

        btnCancel = new RedButton(Messages.get(this, "back")){
            @Override
            protected void onClick() {
                super.onClick();
                hide();
                if (changer instanceof BluePrint) {
                    ((BluePrint) changer).reShowSelector();
                }
            }

        };
        btnCancel.setRect(btnChange.right()+1, bottom+2*GAP, WIDTH/2f, 16);
        add(btnCancel);

        btnChange.enable(Dungeon.hero.ready);

        btnChange.icon(new ItemSprite(changer));
        btnCancel.icon(Icons.EXIT.get());

        bottom = (int)btnCancel.bottom();

        resize(WIDTH, (int)bottom);

    }

    @Override
    public synchronized void update() {
        super.update();
        if (!btnChange.active && Dungeon.hero.ready){
            btnChange.enable(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (changer instanceof BluePrint) {
            ((BluePrint) changer).reShowSelector();
        }
    }

    private float fillFields(String title, String msg1, String msg2, float bottom){

        //the ~ symbol is more commonly used in Chinese
        if (Messages.lang() == Languages.CHI_SMPL || Messages.lang() == Languages.CHI_TRAD){
            msg1 = msg1.replace('-', '~');
            msg2 = msg2.replace('-', '~');
        }

        RenderedTextBlock ttl = PixelScene.renderTextBlock(6);
        ttl.align(RenderedTextBlock.CENTER_ALIGN);
        ttl.text(title, WIDTH/2);
        ttl.setPos(COL_1 - ttl.width() / 2f, bottom + GAP);
        PixelScene.align(ttl);
        add(ttl);

        RenderedTextBlock m1 = PixelScene.renderTextBlock(msg1, 6);
        m1.setPos(COL_2 - m1.width() / 2f, ttl.top());
        PixelScene.align(m1);
        add(m1);

        RenderedTextBlock m2 = PixelScene.renderTextBlock(msg2, 6);
        m2.setPos(COL_3 - m2.width() / 2f, ttl.top());
        PixelScene.align(m2);
        add(m2);

        return ttl.bottom() + GAP;

    }

    private float addMessage(String text, int color, float bottom){
        RenderedTextBlock message = PixelScene.renderTextBlock(6);
        message.text(text, WIDTH);
        message.setPos(0, bottom + GAP);
        message.hardlight(color);
        add(message);

        return message.bottom();
    }

}
