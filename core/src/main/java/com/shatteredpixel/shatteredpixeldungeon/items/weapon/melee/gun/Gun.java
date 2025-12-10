package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.gun;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ElectroBullet;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ElementalBullet;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FireBullet;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostBullet;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.InfiniteBullet;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RouletteOfDeath;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.gunner.Riot;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.bow.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.GunWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class Gun extends GunWeapon {
	public static final String AC_RELOAD    = "RELOAD";

	protected int rounds;
	protected int maxRounds;
	protected float reloadTime = 2f;
	protected int shotsPerRound = 1;
	protected int ammoPerRound = 1;
	protected float shootingSpeed = 1f;
	protected float shootingAcc = 1f;
	protected float adjShootingAcc = 1f;
	protected boolean explode = false;
	protected boolean spread = false; //산탄 여부 = 거리에 따른 탄환 위력 감소 여부.

	public interface GunMod<M extends Enum<M> & GunMod<M>> {
		String name();
	}

	public enum BarrelMod implements GunMod<BarrelMod> {
		NORMAL_BARREL(1, 1),
		SHORT_BARREL(1.5f, 0.5f),
		LONG_BARREL(0.75f, 1.25f);

		private final float adjAccFactor;
		private final float rangedAccFactor;

		BarrelMod(float meleeMulti, float rangedMulti) {
			this.adjAccFactor = meleeMulti;
			this.rangedAccFactor = rangedMulti;
		}
	}

	public enum MagazineMod implements GunMod<MagazineMod> {
		NORMAL_MAGAZINE(1, 0),
		LARGE_MAGAZINE(1.5f, +1),
		QUICK_MAGAZINE(0.5f, -1);

		private final float magazineFactor;
		private final int reloadTimeFactor;

		MagazineMod(float magMulti, int reloadAdd) {
			this.magazineFactor = magMulti;
			this.reloadTimeFactor = reloadAdd;
		}

		public int magazineFactor(int magazine) {
			return (int)Math.floor(magazine*magazineFactor);
		}
		public float reloadTimeFactor(float time) {
			return time + reloadTimeFactor;
		}
	}

	public enum BulletMod implements GunMod<BulletMod> {
		NORMAL_BULLET(1, 1),
		AP_BULLET(0, 0.8f),
		HP_BULLET(2, 1.3f);

		private final float armorMulti;
		private final float dmgMulti;

		BulletMod(float armorMulti, float dmgMulti) {
			this.armorMulti = armorMulti;
			this.dmgMulti = dmgMulti;
		}

		public float armorFactor() {
			return armorMulti;
		}

        public int damageFactor(int damage) {
			return Math.round(damage*dmgMulti);
		}
	}

	public enum WeightMod implements GunMod<WeightMod> {
		NORMAL_WEIGHT,
		LIGHT_WEIGHT,
		HEAVY_WEIGHT


    }

	public enum AttachMod implements GunMod<AttachMod> {
		NORMAL_ATTACH,
		LASER_ATTACH,
		FLASH_ATTACH
	}

	public enum EnchantMod implements GunMod<EnchantMod> {
		NORMAL_ENCHANT(1, 1),
		AMP_ENCHANT(2, 0.75f),
		SUP_ENCHANT(0.5f, 1.25f);

		private final float enchantMulti;
		private final float dmgMulti;

		EnchantMod(float enchantMulti, float dmgMulti) {
			this.enchantMulti = enchantMulti;
			this.dmgMulti = dmgMulti;
		}

		public float enchantFactor() {
			return enchantMulti;
		}

        public int damageFactor(int damage) {
			return Math.round(damage*dmgMulti);
		}
	}

	public enum InscribeMod implements GunMod<InscribeMod> {
		NORMAL(0),
		INSCRIBED(1);

		private final int shotBonus;

		InscribeMod(int shotBonus) {
			this.shotBonus = shotBonus;
		}

		public int shotBonus() {
			return shotBonus;
		}
	}

	public InscribeMod inscribeMod = InscribeMod.NORMAL;

	private Enum<? extends GunMod<?>>[] gunMods = new Enum[]{
            BarrelMod.NORMAL_BARREL,
            MagazineMod.NORMAL_MAGAZINE,
            BulletMod.NORMAL_BULLET,
            WeightMod.NORMAL_WEIGHT,
            AttachMod.NORMAL_ATTACH,
            EnchantMod.NORMAL_ENCHANT,
            /*InscribeMod.NORMAL*/
    };
	
	public static final Class<GunMod<?>>[] gunModClasses = new Class[]{BarrelMod.class,
            MagazineMod.class,
            BulletMod.class,
            WeightMod.class,
            AttachMod.class,
            EnchantMod.class/*,
            InscribeMod.class*/};

    {
		hitSound = Assets.Sounds.HIT_CRUSH;
		hitSoundPitch = 0.8f;
	}


	private static final String ROUND = "round";
	private static final String BARREL_MOD = "barrelMod";
	private static final String MAGAZINE_MOD = "magazineMod";
	private static final String BULLET_MOD = "bulletMod";
	private static final String WEIGHT_MOD = "weightMod";
	private static final String ATTACH_MOD = "attachMod";
	private static final String ENCHANT_MOD = "enchantMod";
	private static final String INSCRIBE_MOD = "inscribeMod";

	public <T extends GunMod<?>> T getGunMod(Class<T> modType) {
		int index = Arrays.asList(gunModClasses).indexOf(modType);
		if (index != -1) {
			return (T) gunMods[index];
		} else {
			return null;
		}
	}

	public void setGunMod(GunMod<?> mod) {
		
		int index = Arrays.asList(gunModClasses).indexOf(mod.getClass());
		if (index != -1) {
			gunMods[index] = (Enum<? extends GunMod<?>>) mod;
		}
	}

	public void copyGunMods(Gun source) {
		this.gunMods = Arrays.copyOf(source.gunMods, this.gunMods.length);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(ROUND, rounds);
		bundle.put(BARREL_MOD, 		gunMods[0]);
		bundle.put(MAGAZINE_MOD, 	gunMods[1]);
		bundle.put(BULLET_MOD, 		gunMods[2]);
		bundle.put(WEIGHT_MOD, 		gunMods[3]);
		bundle.put(ATTACH_MOD,  	gunMods[4]);
		bundle.put(ENCHANT_MOD,  	gunMods[5]);
		bundle.put(INSCRIBE_MOD, 	inscribeMod);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		rounds = bundle.getInt(ROUND);
		gunMods[0] = bundle.getEnum(BARREL_MOD, BarrelMod.class);
		gunMods[1] = bundle.getEnum(MAGAZINE_MOD, MagazineMod.class);
		gunMods[2] = bundle.getEnum(BULLET_MOD, BulletMod.class);
		gunMods[3] = bundle.getEnum(WEIGHT_MOD, WeightMod.class);
		gunMods[4] = bundle.getEnum(ATTACH_MOD, AttachMod.class);
		gunMods[5] = bundle.getEnum(ENCHANT_MOD, EnchantMod.class);
		inscribeMod = bundle.getEnum(INSCRIBE_MOD, InscribeMod.class);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped( hero )) {
			actions.add(AC_RELOAD);
		}
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (action.equals(AC_RELOAD)) {
			usesTargeting = false;
			if (fullyLoaded()){
				if (hero.heroClass == HeroClass.DUELIST) {
					execute(hero, AC_ABILITY);
				} else {
					GLog.w(Messages.get(this, "already_loaded"));
				}
			} else {
				reloadAction(hero);
			}
		}
	}

	@Override
	public boolean canShoot() {
		if (isEquipped(Dungeon.hero) && Dungeon.hero.buff(InfiniteBullet.class)!=null)
			return true;
		else
			return rounds >= 1;
	}

	@Override
	public void noAmmoAction(Hero hero) {
		execute(hero, AC_RELOAD);
	}

	public boolean fullyLoaded() {
		return rounds >= maxRounds();
	}

	@Override
	protected int baseChargeUse(Hero hero, Char target){
		return 2;
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		if (rounds >= maxRounds()*2) {
			GLog.w(Messages.get(this, "overloaded"));
			return;
		}

		beforeAbilityUsed(hero, null);

		onReload(hero);
		if (fullyLoaded()) {
			manualReload(maxRounds(), true);
		} else {
			quickReload();
		}
		hero.sprite.operate(hero.pos);
		Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
		hero.next();

		afterAbilityUsed(hero);
	}

	public void reloadAction(Hero hero) {
		onReload(hero);

		if (Dungeon.bullet < ammoPerRound()) {
			if (hero.heroClass == HeroClass.DUELIST) {
				execute(hero, AC_ABILITY);
				return;
			}
			GLog.w(Messages.get(this, "less_bullet"));
			return;
		}

		if (Dungeon.bullet < reloadAmmoUse()) {
			while (Dungeon.bullet >= ammoPerRound()) {
				Dungeon.bullet -= ammoPerRound();
				singleReload();
			}
		} else {
			Dungeon.bullet -= reloadAmmoUse();
			quickReload();
		}

		hero.busy();
		hero.sprite.operate(hero.pos);
		Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
		hero.spendAndNext(reloadTime(hero));
		GLog.i(Messages.get(this, "reload"));
	}

	private int ammoPerRound() {
		return ammoPerRound + inscribeMod.shotBonus();
	}

	public void onReload(Hero hero) {
		Buff.detach(hero, ElementalBullet.class);

		if (hero.hasTalent(Talent.SAFE_RELOAD)) {
			Buff.affect(hero, Barrier.class).setShield( 1 + 2 * hero.pointsInTalent(Talent.SAFE_RELOAD));
		}

		if (hero.hasTalent(Talent.ELEMENTAL_BULLET) && rounds == 0) {
			int chance = Random.Int(6);
			int point = Dungeon.hero.pointsInTalent(Talent.ELEMENTAL_BULLET);
			switch (chance) {
                case 0:
					if (point >= 1) {
						Buff.affect(hero, FrostBullet.class, FrostBullet.DURATION);
					}
					break;
				case 1:
					if (point >= 2) {
						Buff.affect(hero, FireBullet.class, FireBullet.DURATION);
					}
					break;
				case 2:
					if (point >= 3) {
						Buff.affect(hero, ElectroBullet.class, ElectroBullet.DURATION);
					}
					break;
                default:
                    break;
            }
		}
	}

	public void quickReload() {
		rounds = maxRounds();
		updateQuickslot();
	}

	public void singleReload() {
		manualReload(1, false);
	}

	public void manualReload(int amount, boolean overload) {
		if (overload) {
			// only overload if it would exceed current rounds
			if (rounds < maxRounds() + amount) {
				rounds += amount;
			}
		} else {
            if (rounds + amount < maxRounds()) {
                rounds += amount;
            } else {
                rounds = maxRounds();
            }
        }

		updateQuickslot();
	}

	public boolean isReloaded() {
		return rounds >= maxRounds();
	}

	public int shotsPerRound() {
		return shotsPerRound + inscribeMod.shotBonus();
	}

	public int maxRounds() {
		//int max = this.magazineMod.magazineFactor(maxRounds);
		int max = getGunMod(MagazineMod.class).magazineFactor(maxRounds);

		if (isEquipped(Dungeon.hero)) {
			max += Dungeon.hero.pointsInTalent(Talent.LARGER_MAGAZINE);
		}

		return max;
	}

	public int rounds() {
		return rounds;
	}

	public void useRound() {
		rounds--;
	}

	public float reloadTime(Char user) {
		float time = reloadTime;

		time = getGunMod(MagazineMod.class).reloadTimeFactor(time);
		//time = this.magazineMod.reloadTimeFactor(time);

		if (isEquipped(Dungeon.hero)) {
			time -= Dungeon.hero.pointsInTalent(Talent.FAST_RELOAD);
			if (((Hero)user).heroClass == HeroClass.GUNNER) {
				time -= 1;
			}
		}

		time = Math.max(0, time);
		return time;
	}

	public int reloadAmmoUse() {
		return Math.max(0, (maxRounds() - rounds) * ammoPerRound());
	}

	@Override
	public int tier() {
		WeightMod weightMod = getGunMod(WeightMod.class);
		switch (weightMod) {
			case NORMAL_WEIGHT: default:
				return tier;
			case HEAVY_WEIGHT:
				return tier + 1;
			case LIGHT_WEIGHT:
				return tier - 1;
		}
	}

	@Override
	public int STRReq(int lvl) {
		int req = STRReq(tier(), lvl);
		if (masteryPotionBonus){
			req -= 2;
		}
		return req;
	}

	@Override
	public int max(int lvl) {
		int talentBonus = 0;
		if (Dungeon.hero != null && isEquipped(Dungeon.hero)) {
			talentBonus += 2 * Dungeon.hero.pointsInTalent(Talent.CLOSE_COMBAT);
		}
		return (tier()+1) * (lvl + 3) + talentBonus;

	}

	@Override
    public int missileMin(int lvl) {
		return tier() + lvl +
				(isEquipped(Dungeon.hero) ? RingOfSharpshooting.levelDamageBonus(Dungeon.hero) : 0);
	}

	//need to be overridden
	protected int baseMissileMax(int lvl) {
		return 0;
	}

	@Override
    public int missileMax(int lvl) {
		return baseMissileMax(lvl) +
				(isEquipped(Dungeon.hero) ? RingOfSharpshooting.levelDamageBonus(Dungeon.hero) : 0);
	}

	public int modDamageFactor(int damage) {
		damage = getGunMod(BulletMod.class).damageFactor(damage);
		damage = getGunMod(EnchantMod.class).damageFactor(damage);
		return damage;
	}

	@Override
	protected int missileDamageRoll(Char owner) {
		int damage = super.missileDamageRoll(owner);
		return modDamageFactor(damage);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		AttachMod attachMod = getGunMod(AttachMod.class);
		if (attachMod == AttachMod.FLASH_ATTACH) {
			if (Random.Int(10) > 5+Dungeon.level.distance(attacker.pos, defender.pos)-1) {
				Buff.prolong(defender, Blindness.class, 2f);
			}
		}

		return super.proc(attacker, defender, damage);
	}

	@Override
	public String info() {
		String info = super.info();

		if (levelKnown) {
			int min = modDamageFactor(augment.damageFactor(missileMin())),
					max = modDamageFactor(augment.damageFactor(missileMax()));
			info += "\n\n" + Messages.get(this, "gun_desc", shotsPerRound(), min, max);
		} else {
			int min = modDamageFactor(augment.damageFactor(missileMin(0))),
					max = modDamageFactor(augment.damageFactor(missileMax(0)));
			info += "\n\n" + Messages.get(this, "gun_typical_desc", shotsPerRound(), min, max);
		}
		info += " " + Messages.get(this, "gun_reload_info",
				new DecimalFormat("#.##").format(reloadTime(Dungeon.hero)), ammoPerRound());

		StringBuilder gunModsSB = new StringBuilder();

		for (Enum<?> mod : gunMods) {
            if (mod.ordinal() != 0) {
                gunModsSB.append(Messages.get(mod, mod.name())).append(", ");
            }
        }

		if (gunModsSB.length() != 0) {
			info += " " + Messages.get(this, "mods_list",
					gunModsSB.substring(0, gunModsSB.length()-2));
		}

		if (inscribeMod == InscribeMod.INSCRIBED) {
			info += " " + Messages.get(this, "inscribed_desc");
		}

		if (isEquipped(Dungeon.hero)) {
			float shootingCritChance = Dungeon.hero.critChance(getMissile());
			if (shootingCritChance > 0 && shootingCritChance != Dungeon.hero.critChance(this)) {
				info += " " + Messages.get(this, "shooting_critchance", 100 * shootingCritChance);
				if (shootingCritChance > 1) {
					info += " " + Messages.get(this, "critbonus");
				}
			}
		}

		return info;
	}

	@Override
	public String status() {
		return Messages.format("%d/%d", rounds, maxRounds());
	}

	//needs to be overridden
	public Bullet getMissile(){
		return new Bullet();
	}

	public class Bullet extends GunMissile {

		{
			hitSound = Assets.Sounds.PUFF;
			tier = Gun.this.tier();
			levelKnown = true;
		}

        @Override
		public int min(int lvl) {
			return missileMin(lvl);
		}

        @Override
		public int max(int lvl) {
			return missileMax(lvl);
		}

		public BulletMod bulletMod() {
			//return Gun.this.bulletMod;
			return getGunMod(BulletMod.class);
		}

		public EnchantMod enchantMod() {
			//return Gun.this.enchantMod;
			return getGunMod(EnchantMod.class);
		}

		@Override
		public boolean isIdentified() {
			return true;
		}

		@Override
		public int proc(Char attacker, Char defender, int damage) {
			int distance = Dungeon.level.distance(attacker.pos, defender.pos) - 1;

			if (spread) {
				damage = Math.round(damage * (float) Math.pow(0.9f, distance));
			}

			if (isEquipped(Dungeon.hero)) {
				HashSet<ElementalBullet> bulletBuffs = Dungeon.hero.buffs(ElementalBullet.class);
				for (ElementalBullet b : bulletBuffs) {
					b.proc(defender);
				}

				if (Dungeon.hero.subClass == HeroSubClass.SPECIALIST && Dungeon.hero.hasTalent(Talent.RANGED_SNIPING)) {
					float snipingBonus = 1 + 0.025f * Dungeon.hero.pointsInTalent(Talent.RANGED_SNIPING);
					float snipingMulti = Math.min(2.5f, (float) Math.pow(snipingBonus, distance));
					damage = Math.round(damage * snipingMulti);
				}

				if (Dungeon.hero.buff(Riot.RiotTracker.class) != null) {
					if (Dungeon.hero.hasTalent(Talent.SHOT_CONCENTRATION)) {
						Dungeon.hero.buff(Riot.RiotTracker.class).extend();
					}
					damage = Math.round(damage * 0.5f);
				}
            }

			return super.proc(attacker, defender, damage);
		}

		@Override
		public int buffedLvl(){
			return Gun.this.buffedLvl();
		}

		@Override
		public float delayFactor(Char user) {
			float speed = Gun.this.delayFactor(user) * shootingSpeed;
			if (Dungeon.hero.buff(Riot.RiotTracker.class) != null) {
				speed *= 0.5f;
			}
			return speed;
		}

		@Override
		public float castDelay(Char user, int dst) {
			if (Dungeon.hero.subClass == HeroSubClass.GUNSLINGER) {
				if (user instanceof Hero && ((Hero) user).justMoved)  return 0;
				else                                                  return delayFactor( user );
			} else {
				return delayFactor(user);
			}
		}

		@Override
		public float accuracyFactor(Char owner, Char target) {
			float ACC = super.accuracyFactor(owner, target);

			AttachMod attachMod = getGunMod(AttachMod.class);
			if (attachMod == AttachMod.LASER_ATTACH) {
				ACC *= 1.25f;
			}

			if (Dungeon.hero.hasTalent(Talent.INEVITABLE_DEATH) && Dungeon.hero.buff(RouletteOfDeath.class) != null && Dungeon.hero.buff(RouletteOfDeath.class).timeToDeath()) {
				ACC *= 1 + Dungeon.hero.pointsInTalent(Talent.INEVITABLE_DEATH);
			}

			return ACC;
		}

		@Override
		protected float adjacentAccFactor(Char owner, Char target) {
			BarrelMod mod = getGunMod(BarrelMod.class);
			if (Dungeon.level.adjacent( owner.pos, target.pos ) || owner.pos == target.pos) {
				// not affected by Point Blank talent
				return adjShootingAcc * mod.adjAccFactor;
			} else {
				return shootingAcc * mod.rangedAccFactor;
			}
		}

		@Override
		public int STRReq(int lvl) {
			return Gun.this.STRReq();
		}

		@Override
		protected void onThrow( int cell ) {
			// shoot target cell first
			Char enemy = Actor.findChar(cell);
			for (int i = 0; i < shotsPerRound(); i++) {
				if (enemy == null || enemy == curUser) {
					showPuff(cell);
					super.onThrow(cell);
                } else {
                    super.onThrow(enemy.pos);
                }
            }

			// process explosion if needed
			if (explode) {
				ArrayList<Char> targets = new ArrayList<>();
				int[] aoe = PathFinder.NEIGHBOURS8;
				for (int i : aoe){
					int c = cell + i;
					if (Dungeon.level.insideMap(c)) {
						if (Dungeon.level.heroFOV[c]) {
							showPuff(c);
						}
						if (Dungeon.level.flamable[c]) {
							Dungeon.level.destroy(c);
							GameScene.updateMap(c);
						}
						Char ch = Actor.findChar(c);
						if (ch != null) {
							targets.add(ch);
						}
					}
				}

				//furthest to closest, mainly for elastic
				Collections.sort(targets, (a, b) -> Float.compare(
						Dungeon.level.trueDistance(b.pos, curUser.pos),
						Dungeon.level.trueDistance(a.pos, curUser.pos)));

				for (Char target : targets){
					for (int i = 0; i < shotsPerRound(); i++) {
						if(target.isAlive()) {
                            if (target != curUser) {
                                super.onThrow(target.pos);
                            } else {
                                // special case not processed by super method
                                if (curUser.shoot(target, this)) {
                                    rangedHit(target, target.pos);
                                } else {
                                    rangedMiss(target.pos);
                                }
                            }
                        }
                    }
				}

				Sample.INSTANCE.play( Assets.Sounds.BLAST );
			}

			onShoot();
		}

		@Override
		protected void rangedHit(Char enemy, int cell) {
			super.rangedHit(enemy, cell);

			if (explode && enemy == curUser && !enemy.isAlive()) {
				Dungeon.fail(Gun.this.getClass());
				Badges.validateDeathFromFriendlyMagic();
				GLog.n(Messages.get(Gun.class, "ondeath"));
			}
		}

		public void showPuff(int cell) {
			CellEmitter.get(cell).burst(SmokeParticle.FACTORY, (explode)?4:2);
			CellEmitter.center(cell).burst(BlastParticle.FACTORY, (explode)?4:2);
		}

		public void onShoot() {
			boolean willAggro = true;
			boolean willUseRound = true;

			if (curUser != null) {
				if (curUser.hasTalent(Talent.ROLLING)) {
					Buff.prolong(curUser, Talent.RollingTracker.class, curUser.pointsInTalent(Talent.ROLLING));
				}

				if (curUser.buff(InfiniteBullet.class) != null ||
						(curUser.buff(Riot.RiotTracker.class) != null &&
								Random.Float() < 0.1f * curUser.pointsInTalent(Talent.ROUND_PRESERVE))) {
					willUseRound = false;
				}

				if (curUser.subClass == HeroSubClass.SPECIALIST && curUser.buff(Invisibility.class) != null ||
						curUser.hasTalent(Talent.STEALTH_MASTER)) {
					willAggro = false;
				}
			}

			if (willUseRound) {
				rounds--;
			}

			if (rounds == 0 && curUser != null && curUser.hasTalent(Talent.IMPROVISATION)) {
				Buff.affect(curUser, Barrier.class).setShield(8*curUser.pointsInTalent(Talent.IMPROVISATION));
			}

			if (willAggro) {
				aggro();
			}

			updateQuickslot();
		}

		private void aggro() {
			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
				if (mob.paralysed <= 0
						&& Dungeon.level.distance(curUser.pos, mob.pos) <= 4
						&& mob.state != mob.HUNTING) {
					mob.beckon( curUser.pos );
				}
			}
		}

		@Override
		public void throwSound() {
			Sample.INSTANCE.play( Assets.Sounds.HIT_CRUSH, 1, Random.Float(0.33f, 0.66f) );
		}

		@Override
		public void cast(final Hero user, int dst) {
			super.cast(user, dst);
		}
	}

	public CellSelector.Listener getShooter() {
		return shooter;
	}

	protected CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override
		public void onSelect( Integer target ) {
			if (target != null) {
				if (target == curUser.pos ) {
					if (curUser.heroClass == HeroClass.DUELIST) {
						execute(Dungeon.hero, AC_ABILITY);
					} else {
						execute(Dungeon.hero, AC_RELOAD);
					}
				} else {
					getMissile().cast(curUser, target);
				}
			}
		}
		@Override
		public String prompt() {
			return Messages.get(SpiritBow.class, "prompt");
		}
	};
}
