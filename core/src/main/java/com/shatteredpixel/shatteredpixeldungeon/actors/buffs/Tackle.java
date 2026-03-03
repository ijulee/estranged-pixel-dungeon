/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
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

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;
import static com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton.lastTarget;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Swiftness;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class Tackle extends TargetingAction {
	{
		actPriority = HERO_PRIO+1;
		type = buffType.POSITIVE;
	}

	public int object = 0;

	public static final float DURATION = 4f;

	private static final String OBJECT    = "object";


	public void set(int object, float time) {
		this.object = object;
		spend(time - cooldown() - 1);
	}

	public static float damageMulti() {
		return 0.6f + 0.2f* hero.pointsInTalent(Talent.POWERFUL_TACKLE);
	}

	public static float recoilMulti() {
		return 0.2f*hero.pointsInTalent(Talent.POWERFUL_TACKLE);
	}

	public static int knockbackDist() {
		return (hero.pointsInTalent(Talent.IMPROVED_TACKLE) >= 2) ? 2 : 1;
	}

	@Override
	public boolean act() {
		detach();
		return true;
	}

	@Override
	public boolean attachTo(Char target) {
		ActionIndicator.setAction(this);
		return super.attachTo(target);
	}

	@Override
	public void detach() {
		super.detach();
		ActionIndicator.clearAction(this);
	}

	@Override
	public int icon() {
		return BuffIndicator.SEAL_SHIELD;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(0xFFDB65);
	}

	@Override
	public float iconFadePercent() {
		return 1-visualcooldown()/(DURATION-1);
	}

	@Override
	public String iconTextDisplay() {
		return Integer.toString((int)visualcooldown());
	}

	@Override
	public String desc() {
		if (hero == null) {
			return super.desc();
		}

		String desc;
		if (hero.pointsInTalent(Talent.IMPROVED_TACKLE) >= 3) {
            desc = Messages.get(this, "desc_improved");
        } else {
            desc = Messages.get(this, "desc");
        }

		desc += "\n\n" + Messages.get(this, "stats_desc", 100*damageMulti(), knockbackDist());
		if (hero.hasTalent(Talent.POWERFUL_TACKLE)) {
			desc += " " + Messages.get(this, "recoil", 100*recoilMulti());
		}

		Armor amr = hero.belongings.armor();
		if (amr != null) {
            if (amr.glyph != null) {
				desc += "\n\n" + Messages.get(this, "has_glyph");
				desc += " " + Messages.get(this, "glyph_desc", amr.glyph.name(),
						Messages.get(amr.glyph, "tackle_desc"));
				if (amr.sealGlyph != null && amr.glyph.getClass() != amr.sealGlyph.getClass()) {
					desc += " " + Messages.get(this, "glyph_desc", amr.sealGlyph.name(),
							Messages.get(amr.sealGlyph, "tackle_desc"));

				}
            } else if (amr.sealGlyph != null) {
				desc += "\n\n" + Messages.get(this, "has_glyph");
				desc += " " + Messages.get(this, "glyph_desc", amr.sealGlyph.name(),
						Messages.get(amr.sealGlyph, "tackle_desc"));

			}
        }

		desc += "\n\n" + Messages.get(this, "cooldown", dispTurns(visualcooldown()));
        return desc;
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( OBJECT, object );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		object = bundle.getInt( OBJECT );
	}

	@Override
	public String actionName() {
		return Messages.get(this, "name");
	}

	@Override
	public int actionIcon() {
		return HeroIcon.TACKLE;
	}

	@Override
	public int indicatorColor() {
		return 0xFFDB65;
	}

	@Override
	public void doAction() {
		Char ch = (Char) Actor.findById(object);
		if (hero.pointsInTalent(Talent.IMPROVED_TACKLE) < 3) {

            if ( ch == null || !ch.isAlive() ) {
                detach();
            } else if ( target.isCharmedBy(ch) ||
						!Dungeon.level.adjacent(ch.pos, target.pos) ) {
                GLog.w(Messages.get(Combo.class, "bad_target"));
            } else {
				doTackle(ch);
			}
        } else if (!GameScene.isCellSelecterActive(listener)) {
			if (ch != null) {
				QuickSlotButton.target(ch);
			}

			showCross();
			GameScene.selectCell(listener);
        } else {
			if (canAutoAim(lastTarget)) {
				//target must be adjacent
				listener.onSelect(lastTarget.pos);
			} else if (ch != null) {
				listener.onSelect(ch.pos);
			}
		}
    }

	public void doTackle(final Char ch) {
		hero.busy();

		target.sprite.attack(ch.pos, () -> {
			int damage = Math.round(hero.drRoll() * damageMulti());

			AttackIndicator.target(ch);
			TackleTracker tracker = Buff.affect(hero, TackleTracker.class);

			boolean hit = hero.attack(ch, 0f, damage, Char.INFINITE_ACCURACY);

			Invisibility.dispel();

			hero.spendAndNext(tackleDelay());

			if (hit) {
				onTackle(ch);
			}

			tracker.detach();

			Tackle.this.detach();
        });
	}

	public static int procTackle(Char attacker, Char defender, int damage) {
		if (hero.hasTalent(Talent.POWERFUL_TACKLE)) {
			int recoil = Math.round( (damage - attacker.drRoll()) * Tackle.recoilMulti() );

			attacker.damage(Math.min(recoil, attacker.HP + attacker.shielding() - 1), Tackle.class);
		}

		return damage;
	}

	private void onTackle(Char ch) {
		RingOfSharpshooting.pushEnemy(hero, ch, knockbackDist(), true, false, false);

		BrokenSeal.WarriorShield shieldBuff = hero.buff(BrokenSeal.WarriorShield.class);
		if (shieldBuff != null && hero.hasTalent(Talent.IMPROVED_TACKLE)) {
			Buff.affect(hero, BrokenSeal.WarriorShield.class).reduceCooldown(0.1f); // 15-turns
		}

		if (ch.isAlive()) {
			switch (hero.pointsInTalent(Talent.INCAPACITATION)) {
				case 3:
					Buff.affect(ch, Paralysis.class, 2f);
				case 2:
					Buff.affect(ch, Cripple.class, 2f);
				case 1:
					Buff.affect(ch, Vulnerable.class, 2f);
				case 0:
				default:
					break;
			}
		} else { //!ch.isAlive()
			if (hero.hasTalent(Talent.DELAYED_GRENADE)) {
				int minDamage = 4 + 3 * hero.pointsInTalent(Talent.DELAYED_GRENADE);
				int maxDamage = 10 + 10 * hero.pointsInTalent(Talent.DELAYED_GRENADE);

				ArrayList<Char> affected = new ArrayList<>();

				for (int i : PathFinder.NEIGHBOURS8) {
					int c = ch.pos + i;
					if (c >= 0 && c < Dungeon.level.length()) {
						Char enemy = Actor.findChar(c);
						if (enemy != null && enemy != hero &&
								enemy.alignment != Char.Alignment.ALLY) {
							affected.add(enemy);
						}
					}
				}

				for (Char enemy : affected) {
					int dmg = Hero.heroDamageIntRange(minDamage, maxDamage);
					if (enemy instanceof DwarfKing) {
						//change damage type for DK so that tackle AOE doesn't count for DK's challenge badge
						enemy.damage(dmg, Tackle.this);
					} else {
						enemy.damage(dmg, hero);
					}
					enemy.sprite.bloodBurstA(ch.sprite.center(), dmg);
					enemy.sprite.flash();
				}

				Sample.INSTANCE.play(Assets.Sounds.BLAST);
			}
		}

		Buff.affect(hero, PostTackleTracker.class, TICK);
	}


	private static boolean canAutoAim(Char lastTarget) {
		return lastTarget != null &&
				lastTarget.isAlive() && lastTarget.isActive() &&
				lastTarget.alignment != Char.Alignment.ALLY &&
				hero.fieldOfView[lastTarget.pos] &&
				Dungeon.level.adjacent(lastTarget.pos, hero.pos);
	}

	public float tackleDelay() {
		Armor armor = hero.belongings.armor();
		if (armor.hasGlyph(Swiftness.class, hero)) {
			int level = Math.max(0, armor.buffedLvl());
			float speed = (1.25f + 0.06f * level) * Armor.Glyph.genericProcChanceMultiplier(hero);
			return 1f / speed;
		} else {
			return 1f;
		}
	}

	public static class TackleTracker extends Buff{}
	public static class PostTackleTracker extends FlavourBuff{
		{ actPriority = HERO_PRIO+1; }
	}

	protected CellSelector.Listener listener = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer cell) {
			if (cell == null) return;
			final Char enemy = Actor.findChar( cell );
			if ( enemy == null || enemy == target ||
				 !Dungeon.level.adjacent(enemy.pos, target.pos) ||
				 target.isCharmedBy( enemy ) ) {
				GLog.w(Messages.get(Combo.class, "bad_target"));

			} else {
				removeCross();
				doTackle( enemy );
			}
		}

		@Override
		public String prompt() {
			return Messages.get(Combo.class, "prompt");
		}

	};
}
