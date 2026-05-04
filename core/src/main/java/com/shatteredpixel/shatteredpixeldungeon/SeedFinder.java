package com.shatteredpixel.shatteredpixeldungeon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfAwareness;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfHealth;
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
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CeremonialCandle;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CorpseDust;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Embers;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
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
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.watabou.noosa.Game;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class SeedFinder {
	public static final boolean CONDITION_ANY = true;
	public static final boolean CONDITION_ALL = false;

	public static class Options {
		public static int floors;
		public static boolean condition;
		public static long seed;

		public static boolean searchForDaily;

		public static boolean ignoreBlacklist;
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

		public static boolean exactLevels;
		public static boolean multirange;
	}

	static final List<Class<? extends Item>> blacklist = Arrays.asList(
			Gold.class, Dewdrop.class, IronKey.class, GoldenKey.class,
			CrystalKey.class, EnergyCrystal.class, CorpseDust.class, Embers.class,
			CeremonialCandle.class, Pickaxe.class, Guidebook.class
	);

	public static List<Room> roomList;

	public static void loadConfig() {
		// pull options from SPDSettings
		Options.floors = SPDSettings.seedfinderFloors();
		Options.condition = SPDSettings.seedfinderConditionANY();

		Options.searchForDaily = false;

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

		Options.exactLevels = SPDSettings.seedfinderExact();
		Options.multirange = SPDSettings.seedfinderMultirange();

		Options.ignoreBlacklist = SPDSettings.ignoreBlacklist();
		Options.challenges = SPDSettings.challenges();
	}

	private static ArrayList<Heap> getMobDrops(Level l) {
		ArrayList<Heap> heaps = new ArrayList<>();

		for (Mob m : l.mobs) {
			if (m instanceof Statue && Options.logEquipment) {
				Heap h = new Heap();
				h.items = new LinkedList<>();
				h.items.add(((Statue) m).weapon);
				if (m instanceof ArmoredStatue) {
					h.items.add(((ArmoredStatue) m).armor);
				}
				h.type = Type.STATUE;
				heaps.add(h);
			} else if (m instanceof Mimic) {
				Heap h = new Heap();
				h.items = new LinkedList<>(((Mimic) m).items);

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

	public static class SeedfinderLogResult {
		public String[] main;
		public String[] rooms;
	}

	public static class SeedLog {
		public static int logDepth;

		public String seed;

		public int maxDepth;

		public LinkedList<Item> rolledTrinkets;

		public List<HashSet<ItemLog>> items;
		public List<List<Item>> forSale;
		public List<HashMap<Room, String>> roomList;
		public List<Level.Feeling> feelings;

		public int ghostDepth = -1;
		public Item ghostWeapon;
		public Item ghostArmor;

		public int wandmakerDepth = -1;
		public int wandmakerType = -1;
		public Item wandmakerWand1;
		public Item wandmakerWand2;

		public int blacksmithDepth = -1;
		public int blacksmithType = -1;
		public ArrayList<Item> blacksmithSmithRewards;

		public int impDepth = -1;
		public Boolean impType;
		public Item impReward;

		public SeedfinderLogResult result;

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
			if (result != null) return result;

			String[] main = new String[maxDepth + 1];
			Arrays.fill(main, "");
			String[] rooms = new String[maxDepth + 1];
			Arrays.fill(rooms, "");

			//seed text in 0th entry
			main[0] += Messages.get(this, "custom_seed", seed);

			//trinkets in 0th entry
			if (Options.logTrinkets && rolledTrinkets != null) {
				LinkedList<String> trinketStrings = new LinkedList<>();
				for (Item trinket : rolledTrinkets) {
					trinketStrings.add(checkTarget(trinket.title()));
				}

				main[0] +=  "\n\n";
				main[0] += Messages.get(this, "trinkets", trinketStrings.toArray());
			}
			//copy entry just so that content will always change when clicking entry 0/*
			rooms[0] = main[0];

			for (logDepth = 1; logDepth <= maxDepth; logDepth++) {
				//depth and floor feeling
				String depthFeeling;
				if (logDepth % 5 == 0) {
					depthFeeling = Messages.get(this, "boss_floor");
				} else {
					Level.Feeling feeling = feelings.get(logDepth-1);
					depthFeeling = (feeling == Level.Feeling.NONE) ?
							Messages.get(this, "none_floor") : feeling.title();
				}
				main[logDepth] += Messages.get(this, "items_title", logDepth, depthFeeling);

				//add shop items
				if (Options.checkShops) {
                    switch (logDepth) {
                        case 6: case 11: case 16: case 20: case 26:
                            main[logDepth] += "\n\n";
                            main[logDepth] += itemsToString("shop", forSale.get(logDepth/5-1));
					}
                }

				//add floor items
                if (!items.get(logDepth-1).isEmpty()) {
                    main[logDepth] += "\n\n";
                }
				for (ItemLog entry : items.get(logDepth-1)) {
					main[logDepth] += entry.toString();
					main[logDepth] += "\n";
				}

				//handle quests
				if (logDepth == ghostDepth) {
					String questType = Messages.get(this, "ghost_type_"+logDepth);
					main[logDepth] += "\n\n";
                    if (Options.logEquipment) {
                        main[logDepth] += Messages.get(this, "ghost", questType,
								checkTarget(ghostWeapon.title()), checkTarget(ghostArmor.title()));
                    } else {
						main[logDepth] += Messages.get(this, "ghost_filtered", questType);
					}
                } else if (logDepth == wandmakerDepth) {
					String questType = Messages.get(this, "wandmaker_type_"+wandmakerType);
					main[logDepth] += "\n\n";
                    if (Options.logWands) {
                        main[logDepth] += Messages.get(this, "wandmaker", questType,
								checkTarget(wandmakerWand1.title()), checkTarget(wandmakerWand2.title()));
                    } else {
						main[logDepth] += Messages.get(this, "wandmaker_filtered", questType);
					}
                } else if (logDepth == blacksmithDepth) {
					String questType = Messages.get(this, "blacksmith_type_"+blacksmithType);
					main[logDepth] += "\n\n";
                    if (Options.logEquipment) {
                        main[logDepth] += itemsToString(
								Messages.get(this, "blacksmith", questType),
								blacksmithSmithRewards);
                    } else {
						main[logDepth] += Messages.get(this, "blacksmith_filtered", questType);
					}
                } else if (logDepth == impDepth) {
					String questType = impType ? "monks" : "golems";
					main[logDepth] += "\n\n";
                    if (Options.logRings) {
                        main[logDepth] += Messages.get(this, "imp", questType,
								checkTarget(impReward.title()));
                    } else {
						main[logDepth] += Messages.get(this, "imp_filtered", questType);
					}
                }

				//add rooms
				rooms[logDepth] += Messages.get(this, "rooms_title", logDepth, depthFeeling);
				rooms[logDepth] += "\n\n";

				for (Room room : roomList.get(logDepth-1).keySet()) {
					String roomName = room.getClass().getSimpleName()
							.replaceAll("([a-z])([A-Z])", "$1 $2").toLowerCase();
					if (Options.useRooms) {
						roomName = checkTarget(roomName);
					}

					if (!roomList.get(logDepth-1).get(room).isEmpty()) {
						rooms[logDepth] += Messages.format("**-** %s (%s)\n", roomName, roomList.get(logDepth-1).get(room));
					} else {
						rooms[logDepth] += Messages.format("**-** %s\n", roomName);
					}
				}
			}

			result = new SeedfinderLogResult();
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
				caption = ((Type) src).name().replaceAll("_", " ").toLowerCase();
			} else {
				caption = src.getClass().getSimpleName()
						.replaceAll("([a-z])([A-Z])", "$1 $2").toLowerCase();
			}

			return itemsToString(caption, content);
        }
	}

	public static ArrayList<String> targets;
	public static ArrayList<Integer> targetDepths;
	public static ArrayList<Integer> targetLevels;
	public static ArrayList<Boolean> targetMatches;
	public static final ArrayList<String> DEFAULT_TARGETS = new ArrayList<>(
			List.of("upgrade", "strength", "entrance", "exit")
	);

	public static SeedLog findSeed() {
		SeedFinder.loadConfig();
		parsePrompt();

		long start = Random.Long(DungeonSeed.TOTAL_SEEDS);
		for (long i = start; i < DungeonSeed.TOTAL_SEEDS; i++) {
			final long count = i - start + 1;
			if (Thread.currentThread().isInterrupted()) {
				Gdx.app.postRunnable(() -> ShatteredPixelDungeon.scene().addToFront(
						new WndMessage("Searched interrupted after _" + count + "_ seeds.")));
				return null;
			}

			resetMatches();
			SPDSettings.customSeed(DungeonSeed.convertToCode(i));
			SeedLog log = scoutDungeon();
			log.toLogResult();

			if ((Options.condition == CONDITION_ALL && !targetMatches.contains(false)) ||
				(Options.condition == CONDITION_ANY && targetMatches.contains(true))) {
				log.result.main[0] += "\n\n";
				log.result.main[0] += "Searched through _" + count + "_ seeds.";
				log.result.rooms[0] = log.result.main[0];
				return log;
			}
		}

		return null;
	}

	private static void parsePrompt() {
		int maxDepth = Options.floors;
		targets = new ArrayList<>();
		targetDepths = new ArrayList<>();
		targetLevels = new ArrayList<>();

		for (String line : SPDSettings.seedfinderPrompt().toLowerCase().split("\n")) {
			String[] splitLine = line.split(", ?");
			String[] targetTitle = splitLine[0].split(" ?\\+");
			String targetName = targetTitle[0].strip();
			if (targetName.isEmpty()) continue;

			targets.add(targetName);

			//ignore range specifier if multirange flag not set
            if (Options.multirange) {
                int depth = (splitLine.length == 2) ? Integer.parseInt(splitLine[1]) : maxDepth;
                targetDepths.add(depth);
            }

            int level = (targetTitle.length == 2) ? Integer.parseInt(targetTitle[1]) : -1;
			targetLevels.add(level);
		}
	}

	private static void resetMatches() {
		targetMatches = new ArrayList<>();
		for (String item : targets) {
			targetMatches.add(false);
		}
	}

	private static final LinkedHashMap<Integer, String> scroll2rune = new LinkedHashMap<>() {
		{
			put(ItemSpriteSheet.SCROLL_KAUNAN, "KAUNAN");
			put(ItemSpriteSheet.SCROLL_SOWILO, "SOWILO");
			put(ItemSpriteSheet.SCROLL_LAGUZ, "LAGUZ");
			put(ItemSpriteSheet.SCROLL_YNGVI, "YNGVI");
			put(ItemSpriteSheet.SCROLL_GYFU, "GYFU");
			put(ItemSpriteSheet.SCROLL_RAIDO, "RAIDO");
			put(ItemSpriteSheet.SCROLL_ISAZ, "ISAZ");
			put(ItemSpriteSheet.SCROLL_MANNAZ, "MANNAZ");
			put(ItemSpriteSheet.SCROLL_NAUDIZ, "NAUDIZ");
			put(ItemSpriteSheet.SCROLL_BERKANAN, "BERKANAN");
			put(ItemSpriteSheet.SCROLL_ODAL, "ODAL");
			put(ItemSpriteSheet.SCROLL_TIWAZ, "TIWAZ");
		}
	};

	private static final LinkedHashMap<Integer, String> pot2color = new LinkedHashMap<>() {
        {
            put(ItemSpriteSheet.POTION_CRIMSON, "crimson");
            put(ItemSpriteSheet.POTION_AMBER, "amber");
            put(ItemSpriteSheet.POTION_GOLDEN, "golden");
            put(ItemSpriteSheet.POTION_JADE, "jade");
            put(ItemSpriteSheet.POTION_TURQUOISE, "turquoise");
            put(ItemSpriteSheet.POTION_AZURE, "azure");
            put(ItemSpriteSheet.POTION_INDIGO, "indigo");
            put(ItemSpriteSheet.POTION_MAGENTA, "magenta");
            put(ItemSpriteSheet.POTION_BISTRE, "bistre");
            put(ItemSpriteSheet.POTION_CHARCOAL, "charcoal");
            put(ItemSpriteSheet.POTION_SILVER, "silver");
            put(ItemSpriteSheet.POTION_IVORY, "ivory");
        }
    };

	private static final LinkedHashMap<Integer, String> ring2gem = new LinkedHashMap<>() {
        {
            put(ItemSpriteSheet.RING_GARNET, "garnet");
            put(ItemSpriteSheet.RING_RUBY, "ruby");
            put(ItemSpriteSheet.RING_TOPAZ, "topaz");
            put(ItemSpriteSheet.RING_EMERALD, "emerald");
            put(ItemSpriteSheet.RING_ONYX, "onyx");
            put(ItemSpriteSheet.RING_OPAL, "opal");
            put(ItemSpriteSheet.RING_TOURMALINE, "tourmaline");
            put(ItemSpriteSheet.RING_SAPPHIRE, "sapphire");
            put(ItemSpriteSheet.RING_AMETHYST, "amethyst");
            put(ItemSpriteSheet.RING_QUARTZ, "quartz");
            put(ItemSpriteSheet.RING_AGATE, "agate");
            put(ItemSpriteSheet.RING_DIAMOND, "diamond");
        }
    };

	public static String checkTarget(String title) {
		boolean match = false;
		for (int i = 0, targetsSize = targets.size(); i < targetsSize; i++) {
			if (Options.multirange && SeedLog.logDepth > targetDepths.get(i)) continue;

			String target = targets.get(i);

            if (title.contains(target)) {
				int level = targetLevels.get(i);
				if (level != -1) {
                    int titleLevel = title.contains("+") ? Integer.parseInt(title.split(" ?\\+")[1]) : 0;
                    if ((Options.exactLevels && titleLevel != level) || titleLevel < level) continue;
                }
				match = true;
                if (!targetMatches.get(i)) {
                    targetMatches.set(i, true);
                    break;
                }
            }
        }

		if (match) return Messages.format("_%s_", title);
		else	   return title;
	}

	private static final long SECOND = 1000;
	private static final long MINUTE = 60 * SECOND;
	private static final long HOUR = 60 * MINUTE;
	private static final long DAY = 24 * HOUR;

	public static SeedLog scoutDaily() {
		SeedFinder.loadConfig();
		long lastDaily = SPDSettings.lastDaily();
		long time = Game.realTime - (Game.realTime % DAY);
		time = Math.max(time, 20_148 * DAY);
		SPDSettings.lastDaily(time);
		Dungeon.daily = Options.searchForDaily = true;
		targets = DEFAULT_TARGETS;
		targetDepths = new ArrayList<>();
		targetMatches = new ArrayList<>();
		for (String item : DEFAULT_TARGETS) {
			targetDepths.add(Options.floors);
			targetMatches.add(false);
		}
		SeedLog log = scoutDungeon();
		Dungeon.daily = Options.searchForDaily = false;
		SPDSettings.lastDaily(lastDaily);
		return log;
	}

	public static SeedLog scoutSeed(String seed) {
		SeedFinder.loadConfig();
		targets = DEFAULT_TARGETS;
		targetDepths = new ArrayList<>();
		targetMatches = new ArrayList<>();
		for (String item : DEFAULT_TARGETS) {
			targetDepths.add(Options.floors);
			targetMatches.add(false);
		}
		SPDSettings.customSeed(seed);
		return scoutDungeon();
	}

	public static SeedLog scoutDungeon() {
		Dungeon.initSeed();
		SPDSettings.challenges(Options.challenges);
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
					forSale = filterItems(forSale);
					if (Options.checkShops && !forSale.isEmpty()) {
						log.addForSale(forSale);
					}
			}

			//skip below for boss floors
			if (Dungeon.depth % 5 == 0) {
				continue;
			}

			//check rooms
			for (Room room : roomList) {
                String caption = "";
                if (room instanceof SacrificeRoom && Options.logEquipment) {
                    //special case
                    SacrificialFire fire = (SacrificialFire) level.blobs.get(SacrificialFire.class);
                    if (fire != null) {
                        log.addEntry(Dungeon.depth, fire, List.of(fire.getPrize().identify(false)));
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

            //check quest NPC presence and grab quest info
			for (Mob mob : level.mobs) {
				if (mob instanceof Ghost && Ghost.Quest.armor != null) {
					log.ghostDepth = Dungeon.depth;
					log.ghostWeapon = Ghost.Quest.weapon.enchant(Ghost.Quest.enchant).identify(false);
					log.ghostArmor = Ghost.Quest.armor.inscribe(Ghost.Quest.glyph).identify(false);
				} else if (mob instanceof Wandmaker && Wandmaker.Quest.wand1 != null) {
					log.wandmakerDepth = Dungeon.depth;
					log.wandmakerType = Wandmaker.Quest.type;
					log.wandmakerWand1 = Wandmaker.Quest.wand1.identify(false);
					log.wandmakerWand2 = Wandmaker.Quest.wand2 .identify(false);
				} else if (mob instanceof Blacksmith && !Blacksmith.Quest.smithRewards.isEmpty()) {
					log.blacksmithDepth = Dungeon.depth;
					log.blacksmithType = Blacksmith.Quest.type;
					log.blacksmithSmithRewards = new ArrayList<>(Blacksmith.Quest.smithRewards);
					for (Item i : log.blacksmithSmithRewards) i.identify(false);
				} else if (mob instanceof Imp && Imp.Quest.reward != null) {
					log.impDepth = Dungeon.depth;
					log.impType = Imp.Quest.alternative;
					log.impReward = Imp.Quest.reward.identify(false);
				}
			}
		}

		return log;
	}

	private static LinkedList<Item> rollTrinkets() {
		//simulate rolling for trinkets
		TrinketCatalyst cata = new TrinketCatalyst();

		//roll new trinkets if trinkets were not already rolled
		while (cata.rolledTrinkets.size() < TrinketCatalyst.WndTrinket.NUM_TRINKETS) {
			cata.rolledTrinkets.add((Trinket) Generator.random(Generator.Category.TRINKET));
		}

		return new LinkedList<>(cata.rolledTrinkets);
	}

	private static LinkedList<Heap> filterHeaps(ArrayList<Heap> heaps) {
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

	private static LinkedList<Item> filterItems(LinkedList<Item> items) {
		LinkedList<Item> filtered = new LinkedList<>();
		for (Item i : items) {
			i.identify(false);
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

	private static String itemsToString(String caption, List<Item> content) {
		LinkedList<String> itemStrings = new LinkedList<>();
		for (Item item: content) {
			String result = checkTarget(item.title());
			if (item instanceof Scroll) {
				result = Messages.format("%s (%s)", result, scroll2rune.get(item.image()));
			} else if (item instanceof Potion) {
				result = Messages.format("%s (%s)", result, pot2color.get(item.image()));
			} else if (item instanceof Ring) {
				result = Messages.format("%s (%s)", result, ring2gem.get(item.image()));
				if (item.cursed) result = "cursed " + result;
			}
			itemStrings.add(result);
        }

		return Messages.format("%s: %s", caption, String.join(", ", itemStrings));
	}
}