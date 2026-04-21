package com.shatteredpixel.shatteredpixeldungeon;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfAwareness;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfHealth;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ArmoredStatue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalMimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GoldenMimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap.Type;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.Guidebook;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.CrystalKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CeremonialCandle;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CorpseDust;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Embers;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.ConnectionRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretWellRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicWellRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SacrificeRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.noosa.Game;

public class SeedFinder {
	public enum Condition {
		ANY, ALL
	}

	public static class Options {
		public static int floors;
		public static Condition condition;
		public static long seed;

		public static boolean searchForDaily;
		public static int DailyOffset;

		public static boolean ignoreBlacklist;
		public static boolean useChallenges;
		public static int challenges;

		public static boolean useRooms;
		public static boolean logPotions;
		public static boolean logScrolls;
		public static boolean logTrinkets;
		public static boolean logEquipment;
		public static boolean logRings;
		public static boolean logWands;
		public static boolean logArtifacts;
		public static boolean logOther;
		public static boolean checkShops;

		public static boolean trueRandom;
		public static boolean sequentialMode;
		public static long startingSeed;
		public static int infoSpacing;
		public static String spacingChar;
	}

	public static class HeapItem {

		public Item item;
		public Heap heap;

		public HeapItem(Item item, Heap heap) {
			this.item = item;
			this.heap = heap;
		}

		public String name() {
			return item.name();
		}

	}

	static List<Class<? extends Item>> blacklist = Arrays.asList(Gold.class, Dewdrop.class, IronKey.class, GoldenKey.class,
			CrystalKey.class, EnergyCrystal.class, CorpseDust.class, Embers.class,
			CeremonialCandle.class, Pickaxe.class, Guidebook.class);

	ArrayList<String> itemList;

	public static List<Room> roomList;
	private static void loadConfig() {

		// pull options from SPDSettings
		Options.floors = SPDSettings.seedfinderFloors();
		Options.condition = SPDSettings.seedfinderConditionANY() ? Condition.ANY : Condition.ALL;

		Options.searchForDaily = false;

		// TODO: retire useRooms option
		// the option still controls matching of rooms when finding seeds, even though rooms are always displayed
		Options.useRooms = SPDSettings.useRooms();

		Options.logTrinkets = SPDSettings.logTrinkets();
		Options.logEquipment = SPDSettings.logEquipment();
		Options.logScrolls = SPDSettings.logScrolls();
		Options.logPotions = SPDSettings.logPotions();
		Options.logRings = SPDSettings.logRings();
		Options.logWands = SPDSettings.logWands();
		Options.logArtifacts = SPDSettings.logArtifacts();
		Options.logOther = SPDSettings.logMisc();

		Options.checkShops = SPDSettings.checkShops();

		Options.ignoreBlacklist = SPDSettings.ignoreBlacklist();
		Options.challenges = SPDSettings.challenges();

		// defaults, only adjustable in CLI seedfinder
		Options.useChallenges = true;
		Options.trueRandom = false;
		Options.sequentialMode = false;
		Options.startingSeed = 0;
		Options.infoSpacing = 1;
		Options.spacingChar = " ";
	}

	private ArrayList<String> getItemList(String text) {
		ArrayList<String> itemList = new ArrayList<>();

		if (text.isEmpty())
			return itemList;

		String[] itemList_s = text.toLowerCase().split(System.lineSeparator());
		itemList = new ArrayList<>(Arrays.asList(itemList_s));

		return itemList;
	}

	private void addTextItems(String caption, ArrayList<HeapItem> items, StringBuilder builder, String padding) {
		if (!items.isEmpty()) {
			builder.append(caption).append(":\n");

			for (HeapItem item : items) {
				Item i = item.item;
				Heap h = item.heap;

				String cursed = "";

				if (((i instanceof Armor && ((Armor) i).hasGoodGlyph())
						|| (i instanceof Weapon && ((Weapon) i).hasGoodEnchant()) || (i instanceof Wand)
						|| (i instanceof Artifact)) && i.cursed) {

					cursed = "cursed ";
				}

				if (i instanceof Scroll || i instanceof Potion || i instanceof Ring) {
					int txtLength = i.title().length();

					if (i.cursed) {
						builder.append("- cursed ");
						txtLength += 7;
					} else {
						builder.append("- ");
					}

					// make anonymous names show in the same column to look nice
                    String tabstring = String.valueOf(Options.spacingChar).repeat(Math.max(1, Options.infoSpacing - txtLength));

					builder.append(i.title().toLowerCase()).append(tabstring); // item
					//builder.append(i.anonymousName().toLowerCase().replace(" potion", "").replace("scroll of ", "")
					//		.replace(" ring", "")); // color, rune or gem

					// if both location and type are logged only space to the right once
					if (h.type != Type.HEAP) {
						builder.append(" (").append(h.title().toLowerCase()).append(")");
					}
				} else {
					String name = cursed + i.title().toLowerCase();
					builder.append("- ").append(name);

					// also make item location log in the same column
					if (h.type != Type.HEAP) {

                        builder.append(String.valueOf(Options.spacingChar).repeat(Math.max(1, Options.infoSpacing - name.length()))).append("(").append(h.title().toLowerCase()).append(")");
					}
				}
				builder.append("\n");
			}

			builder.append(padding);
		}
	}

	private void addTextQuest(String caption, ArrayList<Item> items, StringBuilder builder) {
		if (!items.isEmpty()) {
			builder.append(caption).append(":\n");

			for (Item i : items) {
				if (i.cursed)
					builder.append("- cursed ").append(i.title().toLowerCase()).append("\n");

				else
					builder.append("- ").append(i.title().toLowerCase()).append("\n");
			}

			builder.append("\n");
		}
	}

	public SeedFinder() {

	}

	public String find_seed(String items) {
		loadConfig();
		itemList = getItemList(items);

		try {
			// only generate natural seeds (NOT AVAILABLE)
			if (Options.trueRandom) {
				for (int i = 0; i < DungeonSeed.TOTAL_SEEDS; i++) {
					if (Thread.currentThread().isInterrupted())
						throw new InterruptedException();
					final int finalI = i;
					Gdx.app.postRunnable(() -> ShatteredPixelDungeon.scene()
                            .addToFront(new WndMessage("searched through _" + Long.toString(finalI) + "_ seeds.")));
					return DungeonSeed.convertToCode(Dungeon.seed);
				}

			// sequential mode: start at 0 (NOT AVAILABLE)
			} else if (Options.sequentialMode) {
				for (long i = Options.startingSeed; i < DungeonSeed.TOTAL_SEEDS; i++) {
					if (Thread.currentThread().isInterrupted())
						throw new InterruptedException();
					if (testSeed(Long.toString(i), Options.floors)) {
						final long finalI = i;
						Gdx.app.postRunnable(() -> ShatteredPixelDungeon.scene().addToFront(new WndMessage(
                                "searched through _" + (finalI - Options.startingSeed) + "_ seeds.")));
						return DungeonSeed.convertToCode(Dungeon.seed);
					}
				}

			// default (random) mode
			} else {
				long start = Random.Long(DungeonSeed.TOTAL_SEEDS);
				for (long i = start; i < DungeonSeed.TOTAL_SEEDS; i++) {
					if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }

					if (testSeed(Long.toString(i), Options.floors)) {
						final long count = i - start;
						Gdx.app.postRunnable(() -> ShatteredPixelDungeon.scene()
                                .addToFront(new WndMessage("searched through _" + count + "_ seeds.")));
						return DungeonSeed.convertToCode(Dungeon.seed);
					}
				}
			}
		} catch (InterruptedException e) {
			return "Error: search cancelled";
		}

		return "error: invalid finding mode";
	}

	private ArrayList<String> getRooms() {
		ArrayList<String> rooms = new ArrayList<>();
		for (Room room : roomList) {
			String roomName = room.getClass().getName().replaceAll("^com.*?rooms\\.", "");

			//camel case to normal text
			roomName = Messages.lowerCase(roomName.replaceAll("([a-z])([A-Z])", "$1 $2"));

			String roomType = "standard";
			if (roomName.contains("special")) {
				roomName = roomName.replace("special.", "");
				roomType = "special";
			} else if (roomName.contains("secret")) {
				roomName = roomName.replace("secret.", "");
				roomType = "secret";
			} else if (roomName.contains("entrance")) {
				roomName = roomName.replaceAll("entrance[. ]", "");
				roomType = "_entrance_";
			} else if (roomName.contains("exit")) {
				roomName = roomName.replaceAll("exit[. ]", "");
				roomType = "_exit_";
			} else if (roomName.contains("connection")) {
				roomName = roomName.replace("connection.", "");
				roomType = "connection";
			} else if (room instanceof MagicWellRoom || room instanceof SecretWellRoom) {
				int wellCell;
				if (room instanceof MagicWellRoom) {
					wellCell = room.center().x + Dungeon.level.width() * room.center().y;
				} else {
					Point door = ((SecretWellRoom) room).entrance();
					Point well;
					if (door.x == room.left) {
						well = new Point(room.right-2, door.y);
					} else if (door.x == room.right) {
						well = new Point(room.left+2, door.y);
					} else if (door.y == room.top) {
						well = new Point(door.x, room.bottom-2);
					} else {
						well = new Point(door.x, room.top+2);
					}
					wellCell = well.x + Dungeon.level.width() * well.y;
				}

				WaterOfAwareness aware = (WaterOfAwareness) Dungeon.level.blobs.get(WaterOfAwareness.class);
				WaterOfHealth health = (WaterOfHealth) Dungeon.level.blobs.get(WaterOfHealth.class);

				if (aware != null && aware.cur[wellCell] != 0) {
					roomType += "_awareness_";
				} else if (health != null && health.cur[wellCell] != 0) {
					roomType += "_health_";
				} else {
					roomType += "_?_";
				}
			}

			if (roomName.contains("standard")) {
				roomName = roomName.replace("standard.", "");
			}

			rooms.add(Messages.format("%s (%s)", roomName, roomType));
		}

		Collections.sort(rooms);
		return rooms;
	}

	private ArrayList<HeapItem> getTrinkets() {
		TrinketCatalyst cata = new TrinketCatalyst();
		int NUM_TRINKETS = TrinketCatalyst.WndTrinket.NUM_TRINKETS;

		// roll new trinkets if trinkets were not already rolled
		while (cata.rolledTrinkets.size() < NUM_TRINKETS) {
			cata.rolledTrinkets.add((Trinket) Generator.random(Generator.Category.TRINKET));
		}

		ArrayList<HeapItem> trinkets = new ArrayList<>();

		for (int i = 0; i < NUM_TRINKETS; i++) {
			Heap h = new Heap();
			h.type = Type.CATALYST;
			trinkets.add(new HeapItem(cata.rolledTrinkets.get(i), h));
		}

		return trinkets;
	}

	private static ArrayList<Heap> getMobDrops(Level l) {
		ArrayList<Heap> heaps = new ArrayList<>();

		for (Mob m : l.mobs) {
			if (m instanceof Statue && Options.logEquipment) {
				Heap h = new Heap();
				h.items = new LinkedList<>();
				h.items.add(((Statue) m).weapon.identify(false));
				if (m instanceof ArmoredStatue) {
					h.items.add(((ArmoredStatue) m).armor.identify(false));
				}
				h.type = Type.STATUE;
				heaps.add(h);
			} else if (m instanceof Mimic) {
				Heap h = new Heap();
				h.items = new LinkedList<>();

				for (Item item : ((Mimic) m).items) {
					h.items.add(item.identify(false));
				}

				if (m instanceof GoldenMimic) {
					h.type = Type.GOLDEN_MIMIC;
				} else if (m instanceof CrystalMimic) {
					h.type = Type.CRYSTAL_MIMIC;
				} else {
					h.type = Type.MIMIC;
				}
				heaps.add(h);
			}
		}

		return heaps;
	}

	private boolean testSeed(String seed, int floors) throws InterruptedException {
		SPDSettings.customSeed(seed);
		Dungeon.initSeed();
		SPDSettings.challenges(Options.challenges);
		Dungeon.daily = Options.searchForDaily;
		GamesInProgress.selectedClass = HeroClass.WARRIOR;
		Dungeon.init();

		HashSet<String> itemsToFind = new HashSet<>(itemList);

		// check trinkets
		if (Options.logTrinkets) {
			ArrayList<HeapItem> trinkets = getTrinkets();
			for (HeapItem trinket : trinkets) {
				for (String item : itemsToFind) {
					if (trinket.name().toLowerCase().contains(item)) {
						itemsToFind.remove(item);
						break;
					}
				}
			}
		}

		for (int f = 0; f < floors; f++) {
			if (Thread.currentThread().isInterrupted())
				throw new InterruptedException();

			Level l = Dungeon.level = Dungeon.newLevel();

            ArrayList<Heap> heaps = new ArrayList<>(l.heaps.valueList());
			heaps.addAll(getMobDrops(l));

			//check rooms
			if (Options.useRooms) {
				ArrayList<String> rooms = getRooms();
				for (String room : rooms) {
					for (String item : itemsToFind) {
						if (room.contains(item)) {
							itemsToFind.remove(item);
							break;
						}
					}
				}
			}

			//check heaps
			for (Heap h : heaps) {
				for (Item i : h.items) {
					i.identify(false);

					for (String item : itemsToFind) {
						if (i.title().toLowerCase().contains(item)) {
							itemsToFind.remove(item);
							break;
						}
					}
				}
			}

			//check sacrificial fire
			//FIXME doesn't work right now
			/*if (l.sacrificialFireItem != null) {
				l.sacrificialFireItem.identify(false);
				for (String item : itemsToFind) {
					if (l.sacrificialFireItem.title().toLowerCase().contains(item)) {
						itemsToFind.remove(item);
						break;
					}
				}
			}*/

			Dungeon.depth++;
		}

		//check quests
		//FIXME add Blacksmith Quest rewards
		Item[] questItems = {
				Ghost.Quest.armor,
				Ghost.Quest.weapon,
				Wandmaker.Quest.wand1,
				Wandmaker.Quest.wand2,
				Imp.Quest.reward
		};

		if (Ghost.Quest.armor != null) {
			questItems[0] = Ghost.Quest.armor.inscribe(Ghost.Quest.glyph);
			questItems[1] = Ghost.Quest.weapon.enchant(Ghost.Quest.enchant);
		}

		for (Item i: questItems) {
			if (i != null) {
				i.identify(false);
				for (String item : itemsToFind) {
					if (i.title().toLowerCase().contains(item)) {
						itemsToFind.remove(item);
						break;
					}
				}
			}
		}

		if (Options.condition == Condition.ANY) {
			return itemsToFind.size() < itemList.size();
		} else { //Options.condition == Condition.ALL
			return itemsToFind.isEmpty();
		}
	}

	public SeedfinderLogResult logSeedItems(String seed, int floors) {
		SeedfinderLogResult result = new SeedfinderLogResult();
		String[] itemLog = new String[floors];
		String[] roomLog = new String[floors];

        Arrays.fill(itemLog, "");
		Arrays.fill(roomLog, "");

		if (Options.searchForDaily) {
			Dungeon.daily = true;
			Dungeon.initSeed();
			long DAY = 1000 * 60 * 60 * 24;
			long currentDay = (long) Math.floor(Game.realTime / DAY) + Options.DailyOffset;
			SPDSettings.lastDaily(DAY * currentDay);
			SPDSettings.challenges(Options.challenges);
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
			format.setTimeZone(TimeZone.getTimeZone("UTC"));

        } else {
			Dungeon.daily = false;
			SPDSettings.customSeed(seed);
			Dungeon.initSeed();
			SPDSettings.challenges(Options.challenges);
        }
        GamesInProgress.selectedClass = HeroClass.WARRIOR;
        Dungeon.init();

        if (Options.ignoreBlacklist) {
            blacklist = List.of();
        } else {
			blacklist = Arrays.asList(Gold.class, Dewdrop.class, IronKey.class, GoldenKey.class,
					CrystalKey.class, EnergyCrystal.class, CorpseDust.class, Embers.class,
					CeremonialCandle.class, Pickaxe.class, Guidebook.class);
		}

        if (Options.logTrinkets) {
			ArrayList<HeapItem> trinkets = getTrinkets();
			StringBuilder builder = new StringBuilder();
			addTextItems("Trinkets", trinkets, builder, " ");
			itemLog[0] += builder.toString();
		}

		for (int i = 0; i < floors; i++) {

			Level l = Dungeon.level = Dungeon.newLevel();
			ArrayList<Heap> heaps = new ArrayList<>(l.heaps.valueList());
			StringBuilder builder = new StringBuilder();
			ArrayList<HeapItem> scrolls = new ArrayList<>();
			ArrayList<HeapItem> potions = new ArrayList<>();
			ArrayList<HeapItem> equipment = new ArrayList<>();
			ArrayList<HeapItem> rings = new ArrayList<>();
			ArrayList<HeapItem> artifacts = new ArrayList<>();
			ArrayList<HeapItem> wands = new ArrayList<>();
			ArrayList<HeapItem> others = new ArrayList<>();

			String feeling = l.feeling.toString();

			switch (Dungeon.depth) {
				case 5:
					feeling = "GOO";
					break;
				case 10:
					feeling = "TENGU";
					break;
				case 15:
					feeling = "DM-300";
					break;
				case 20:
					feeling = "DWARVEN KING";
					break;
				case 25:
					feeling = "YOG DZEWA";
					break;
			}

			itemLog[i] += String.format("%dF (%s)\n\n",Dungeon.depth, feeling);

			//list all rooms on non-boss levels
            if (Dungeon.depth % 5 != 0) {
                ArrayList<String> rooms = getRooms();
                roomLog[i] += ("Rooms: \n");

                for (int k = 0; k < rooms.size(); k++) {
                    roomLog[i] += ("- " + rooms.get(k) + "\n");
                }

                roomLog[i] += ("\n");
            }

            // list quest rewards
			if (Ghost.Quest.armor != null) {
				//infer quest type from depth
				String caption;
				switch (Dungeon.depth) {
					case 2: default:
						caption = "Ghost Quest (Fetid Rat)";
						break;
					case 3:
						caption = "Ghost Quest (Gnoll Trickster)";
						break;
					case 4:
						caption = "Ghost Quest (Great Crab)";
						break;
				}

				ArrayList<Item> rewards = new ArrayList<>();
				rewards.add(Ghost.Quest.armor.inscribe(Ghost.Quest.glyph).identify(false));
				rewards.add(Ghost.Quest.weapon.enchant(Ghost.Quest.enchant).identify(false));
				Ghost.Quest.complete();

				addTextQuest(caption, rewards, builder);
			}

			if (Wandmaker.Quest.wand1 != null) {
				ArrayList<Item> rewards = new ArrayList<>();
				rewards.add(Wandmaker.Quest.wand1.identify(false));
				rewards.add(Wandmaker.Quest.wand2.identify(false));
				Wandmaker.Quest.complete();

				String caption;
				switch (Wandmaker.Quest.type) {
					case 1: default:
						caption = "Wandmaker Quest (corpse dust)";
						break;
					case 2:
						caption = "Wandmaker Quest (fresh embers)";
						break;
					case 3:
						caption = "Wandmaker Quest (rotberry seed)";
						break;
				}

				addTextQuest(caption, rewards, builder);
			}

			if (Blacksmith.Quest.Type() != 0) {
				builder.append("Blacksmith quest: ");
				switch (Blacksmith.Quest.Type()) {
					case 0:
						builder.append("old (pre-2.3)");
						break;
					case 1:
						builder.append("crystal cave");
						break;
					case 2:
						builder.append("gnoll geomancer");
						break;
					case 3:
						builder.append("fungus monster");
						break;
				}
				builder.append("\n\n");
				Blacksmith.Quest.reset();
			}

			if (Imp.Quest.reward != null) {
				ArrayList<Item> rewards = new ArrayList<>();
				rewards.add(Imp.Quest.reward.identify(false));
				Imp.Quest.complete();

				addTextQuest("Imp quest reward", rewards, builder);
			}

			heaps.addAll(getMobDrops(l));

			// list items
			for (Heap h : heaps) {
				for (Item item : h.items) {
					item.identify(false);

					if (h.type == Type.FOR_SALE)
						continue;
					else if (blacklist.contains(item.getClass()))
						continue;
					else if (item instanceof Scroll)
						scrolls.add(new HeapItem(item, h));
					else if (item instanceof Potion)
						potions.add(new HeapItem(item, h));
					else if (item instanceof MeleeWeapon || item instanceof Armor)
						equipment.add(new HeapItem(item, h));
					else if (item instanceof Ring)
						rings.add(new HeapItem(item, h));
					else if (item instanceof Wand)
						wands.add(new HeapItem(item, h));
					else if (item instanceof Artifact) {
						artifacts.add(new HeapItem(item, h));
					} else
						others.add(new HeapItem(item, h));
				}
			}

			if (Options.logEquipment) {
				addTextItems("Equipment", equipment, builder, "");

				// sacrificial fire
				//FIXME doesn't work right now
				/*if (l.sacrificialFireItem != null) {
					if (equipment.isEmpty()) {
						builder.append("Equipment:\n");
					}
					Item fireItem = l.sacrificialFireItem.identify(false);

                    String tabstring = String.valueOf(Options.spacingChar).repeat(Math.max(1,
                            Options.infoSpacing - fireItem.title().toLowerCase().length()));

					builder.append("- ").append(fireItem.title().toLowerCase()).append(tabstring).append("(sacrificial fire)");
					builder.append("\n\n");
				} else*/ {
					builder.append("\n");
				}
			}

			if (Options.logScrolls)
				addTextItems("Scrolls", scrolls, builder, "\n");
			if (Options.logPotions)
				addTextItems("Potions", potions, builder, "\n");
			if (Options.logRings)
				addTextItems("Rings", rings, builder, "\n");
			if (Options.logWands)
				addTextItems("Wands", wands, builder, "\n");
			if (Options.logArtifacts)
				addTextItems("Artifacts", artifacts, builder, "\n");
			if (Options.logOther)
				addTextItems("Other", others, builder, "\n");

			itemLog[i] += (builder.toString());

			Dungeon.depth++;
		}

		result.main = itemLog;
		result.rooms = roomLog;
		return result;
	}

	// logging without arguments uses SHPDSettings

	public SeedfinderLogResult logSeedItemsSeededRun(Long seed) {
		loadConfig();
		return logSeedItems(Long.toString(seed), SPDSettings.seedfinderFloors());
	}
	public SeedfinderLogResult logSeedItemsDailyRunRun(int offset) {
		loadConfig();
		Options.searchForDaily = true;
		Options.DailyOffset = offset;
		return logSeedItems("0", SPDSettings.seedfinderFloors());
	}

	public static class SeedfinderLogResult {

		public String[] main;
		public String[] rooms;
	}

	public static class SeedLog {
		public String seed;

		public int maxDepth;

		public LinkedList<Item> rolledTrinkets;

		public List<HashSet<ItemLog>> items;
		public List<List<Item>> forSale;
		public List<HashMap<Room, String>> roomList;
		public List<Level.Feeling> feelings;

		public int ghostDepth = -1;
		public Weapon ghostWeapon;
		public Armor ghostArmor;

		public int wandmakerDepth = -1;
		public int wandmakerType = -1;
		public Wand wandmakerWand1;
		public Wand wandmakerWand2;

		public int blacksmithDepth = -1;
		public int blacksmithType = -1;
		public ArrayList<Item> blacksmithSmithRewards;

		public int impDepth = -1;
		public Boolean impType;
		public Ring impReward;


		public SeedLog(String seed, int maxDepth) {
			this.seed = seed;
			this.maxDepth = maxDepth;

			this.items = new ArrayList<>();
			this.forSale = new ArrayList<>();
			this.roomList = new ArrayList<>();
			for (int i = 0; i < maxDepth; i++) {
				this.items.add(new HashSet<>());
				this.roomList.add(new HashMap<>());
			}
			this.feelings = new ArrayList<>();
		}

		public SeedLog() {
			this("", 29);
		}

		public void addEntry(int depth, Object src, List<Item> content) {
			this.items.get(depth-1).add(new ItemLog(src, content));
		}

		public void addForSale(List<Item> content) {
			this.forSale.add(content);
		}

		public void addRoom(int depth, Room room, String caption) {
			this.roomList.get(depth-1).put(room, caption);
		}

        public SeedfinderLogResult toLogResult() {
			String[] main = new String[maxDepth + 1];
			Arrays.fill(main, "");
			String[] rooms = new String[maxDepth + 1];
			Arrays.fill(rooms, "");

			//put trinkets in 0th entry
			if (Options.logTrinkets && rolledTrinkets != null) {
				LinkedList<String> trinketStrings = new LinkedList<>();
				for (Item trinket : rolledTrinkets) {
					trinketStrings.add("**-** "+Messages.titleCase(trinket.title()));
				}
				main[0] += "_Trinkets:_\n" + String.join("\n", trinketStrings);
			}

			for (int depth = 1; depth <= maxDepth; depth++) {
				//depth and floor feeling
				String depthFeeling;
				if (depth % 5 == 0) {
					depthFeeling = "boss";
				} else {
					Level.Feeling feeling = feelings.get(depth-1);
					depthFeeling = feeling == Level.Feeling.NONE ? "no feeling": feeling.title();
				}
				String depthString = Messages.format("_%dF:_ (%s)\n", depth, depthFeeling);

				main[depth] += depthString;

				//add shop items
                if (Options.checkShops) {
                    switch (depth) {
                        case 6: case 11: case 16: case 20: case 26:
                            main[depth] += "\n";
                            main[depth] += objectsToString("Shop", forSale.get(depth/5-1));
                    }
                }

				//add floor items
				main[depth] += "\n";
				for (ItemLog entry : items.get(depth-1)) {
					main[depth] += entry.toString();
				}

				//handle quests
				if (depth == ghostDepth) {
					String questType = "?";
					switch (depth) {
						case 2:
							questType = "fetid rat";
							break;
						case 3:
							questType = "gnoll trickster";
							break;
						case 4:
							questType = "great crab";
							break;
					}
					main[depth] += Messages.format("\nGhost quest (%s): %s, %s",
							questType, ghostWeapon.title(), ghostArmor.title());
				} else if (depth == wandmakerDepth) {
					String questType = "?";
					switch (wandmakerType) {
						case 1:
							questType = "corpse dust";
							break;
						case 2:
							questType = "elemental embers";
							break;
						case 3:
							questType = "rotberry";
							break;
					}
					main[depth] += Messages.format("\nWandmaker quest (%s): %s, %s",
							questType, wandmakerWand1.title(), wandmakerWand2.title());
				} else if (depth == blacksmithDepth) {
					String questType = "?";
					switch (blacksmithType) {
						case 1:
							questType = "crystal spire";
							break;
						case 2:
							questType = "gnoll geomancer";
							break;
					}
					main[depth] += objectsToString(
							Messages.format("\nBlacksmith quest (%s)", questType),
							blacksmithSmithRewards);
				} else if (depth == impDepth) {
					String questType = impType ? "monks" : "golems";
					main[depth] += Messages.format("\nBlacksmith quest (%s): %s", questType, impReward.title());
				}

				//add rooms
				rooms[depth] += depthString;
				rooms[depth] += "\n";

				for (Room room : roomList.get(depth-1).keySet()) {
					String roomName = room.getClass().getSimpleName().replaceAll("([a-z])([A-Z])", "$1 $2");
					if (!roomList.get(depth-1).get(room).isEmpty()) {
						rooms[depth] += Messages.format("**-** %s (%s)\n", roomName, roomList.get(depth-1).get(room));
					} else if (roomName.contains("Entrance") || roomName.contains("Exit")) {
						rooms[depth] += Messages.format("**-** _%s_\n", roomName);
					} else {
						rooms[depth] += Messages.format("**-** %s\n", roomName);
					}
				}
			}

			SeedfinderLogResult result = new SeedfinderLogResult();
			result.main = main;
			result.rooms = rooms;
			return result;
		}
	}

	public static class ItemLog {
		Object src;
		List<Item> content;

		public ItemLog(Object src, List<Item> content) {
			this.src = src;
			this.content = content;
		}

		@Override
		public String toString() {
			String caption;
			if (src instanceof Heap.Type) {
				caption = Messages.titleCase(((Type) src).name().replaceAll("_", " ").toLowerCase());
			} else {
				caption = Messages.titleCase(src.getClass().getSimpleName().replaceAll("([a-z])([A-Z])", "$1 $2"));
			}
			LinkedList<String> itemStrings = new LinkedList<>();
			for (Item item: content) {
				item.identify(false);
				if (item instanceof PotionOfStrength || item instanceof ScrollOfUpgrade) {
					itemStrings.add(Messages.format("_%s_", item.title()));
				} else {
					itemStrings.add(item.title());
				}
			}

			return Messages.format("%s: %s\n", caption, String.join(", ", itemStrings));
        }
	}

	public static SeedLog scoutDungeon(String seed) {
		loadConfig();

		Dungeon.daily = Options.searchForDaily;
		if (!Dungeon.daily) {
			SPDSettings.customSeed(seed);
		}
		Dungeon.initSeed();
		SPDSettings.challenges(Options.challenges);
		GamesInProgress.selectedClass = HeroClass.WARRIOR;
		Dungeon.init();

		SeedLog log = new SeedLog(Dungeon.customSeedText, Options.floors);

		//check trinkets
		if (Options.logTrinkets) {
			log.rolledTrinkets = rollTrinkets();
		}

		//check each floor
		for ( ;Dungeon.depth <= Options.floors; Dungeon.depth++) {

			Level level = Dungeon.level = Dungeon.newLevel();

			log.feelings.add(level.feeling);

			//check generated items and mob drops (only statues and mimics)
			ArrayList<Heap> heaps = new ArrayList<>(level.heaps.valueList());

			heaps.addAll(getMobDrops(level));

			LinkedList<Item> forSale = new LinkedList<>();
			for (Heap heap : filterHeaps(heaps)) {
				if (heap.type != Type.FOR_SALE) {
					log.addEntry(Dungeon.depth, heap.type, heap.items);
				} else if (Options.checkShops) {
					forSale.addAll(heap.items);
				}
			}

			//add shop items separately
			switch (Dungeon.depth) {
				case 6: case 11: case 16: case 20: case 26:
					if (Options.checkShops && !forSale.isEmpty()) {
						log.addForSale(forSale);
					}
			}

			//check rooms
			if (Options.useRooms) {
				for (Room room : roomList) {
					String caption = "";
					if (room instanceof SacrificeRoom && Options.logEquipment) {
						//special case
						SacrificialFire fire = (SacrificialFire) level.blobs.get(SacrificialFire.class);
						if (fire != null) {
							log.addEntry(Dungeon.depth, fire, List.of(fire.getPrize()));
						}
					}

					if (room instanceof MagicWellRoom || room instanceof SecretWellRoom) {
						int wellPos;
                        if (room instanceof MagicWellRoom) {
                            Point c = room.center();
                            wellPos = c.x + level.width() * c.y;
                        } else {
							Point door = ((SecretWellRoom) room).entrance();
							Point well;
							if (door.x == room.left){
								well = new Point(room.right-2, door.y);
							} else if (door.x == room.right){
								well = new Point(room.left+2, door.y);
							} else if (door.y == room.top){
								well = new Point(door.x, room.bottom-2);
							} else {
								well = new Point(door.x, room.top+2);
							}
							wellPos = well.x + level.width() * well.y;
						}
                        WaterOfHealth health = (WaterOfHealth) level.blobs.get(WaterOfHealth.class);
						WaterOfAwareness aware = (WaterOfAwareness) level.blobs.get(WaterOfAwareness.class);
						if (health != null && health.cur[wellPos] != 0) {
							caption = "health";
						} else if (aware != null && aware.cur[wellPos] != 0) {
							caption = "awareness";
						}
					} else {
						Package roomType = room.getClass().getPackage();
						if (roomType == SecretRoom.class.getPackage()) {
							caption = "secret";
						} else if (roomType == SpecialRoom.class.getPackage()) {
							caption = "special";
						} else if (roomType == ConnectionRoom.class.getPackage()) {
							continue;
						}
					}

					log.addRoom(Dungeon.depth, room, caption);
				}
			}

			//check quest NPC presence and grab quest info
			for (Mob mob : level.mobs) {
				if (mob instanceof Ghost && Ghost.Quest.armor != null) {
					log.ghostDepth = Dungeon.depth;
					log.ghostWeapon = Ghost.Quest.weapon.enchant(Ghost.Quest.enchant);
					log.ghostArmor = Ghost.Quest.armor.inscribe(Ghost.Quest.glyph);
				} else if (mob instanceof Wandmaker && Wandmaker.Quest.wand1 != null) {
					log.wandmakerDepth = Dungeon.depth;
					log.wandmakerType = Wandmaker.Quest.type;
					log.wandmakerWand1 = Wandmaker.Quest.wand1;
					log.wandmakerWand2 = Wandmaker.Quest.wand2;
				} else if (mob instanceof Blacksmith && !Blacksmith.Quest.smithRewards.isEmpty()) {
					log.blacksmithDepth = Dungeon.depth;
					log.blacksmithType = Blacksmith.Quest.type;
					log.blacksmithSmithRewards = new ArrayList<>(Blacksmith.Quest.smithRewards);
				} else if (mob instanceof Imp && Imp.Quest.reward != null) {
					log.impDepth = Dungeon.depth;
					log.impType = Imp.Quest.alternative;
					log.impReward = Imp.Quest.reward;
				}
			}
		}

		return log;
	}

	public static LinkedList<Item> rollTrinkets() {
		//simulate rolling for trinkets
		TrinketCatalyst cata = new TrinketCatalyst();

		//roll new trinkets if trinkets were not already rolled
		while (cata.rolledTrinkets.size() < TrinketCatalyst.WndTrinket.NUM_TRINKETS) {
			cata.rolledTrinkets.add((Trinket) Generator.random(Generator.Category.TRINKET));
		}

		return new LinkedList<>(cata.rolledTrinkets);
	}

	public static LinkedList<Heap> filterHeaps(ArrayList<Heap> heaps) {
		LinkedList<Heap> filtered = new LinkedList<>();
		for (Heap h : heaps) {
			LinkedList<Item> remaining = filterItems(h.items);
			if (!remaining.isEmpty()) {
				h.items = remaining;
				filtered.add(h);
			}
		}
		return filtered;
	}

	public static LinkedList<Item> filterItems(LinkedList<Item> items) {
		LinkedList<Item> filtered = new LinkedList<>();
		for (Item i : items) {
            if (Options.logArtifacts && i instanceof Artifact) {
				filtered.add(i);
			} else if (Options.logRings && i instanceof Ring) {
				filtered.add(i);
            } else if (Options.logEquipment && (i instanceof Weapon || i instanceof Armor)) {
                filtered.add(i);
            } else if (Options.logWands && i instanceof Wand) {
				filtered.add(i);
			} else if (Options.logPotions && i instanceof Potion) {
				filtered.add(i);
			} else if (Options.logScrolls && i instanceof Scroll) {
				filtered.add(i);
			} else if (Options.logOther && (Options.ignoreBlacklist || !blacklist.contains(i.getClass()))) {
				filtered.add(i);
			}
		}
		return filtered;
	}

	public static String objectsToString(String caption, List<?> content) {
		LinkedList<String> itemStrings = new LinkedList<>();
		for (Object item: content) {
            if (item instanceof Item) {
                itemStrings.add(((Item) item).title());
            } else {
				itemStrings.add(Messages.lowerCase(item.getClass().getSimpleName().replaceAll("([a-z])([A-Z])", "$1 $2")));
			}
        }

		return Messages.format("%s: %s\n", caption, String.join(", ", itemStrings));
	}
}