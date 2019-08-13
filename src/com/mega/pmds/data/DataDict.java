package com.mega.pmds.data;

public class DataDict {
	public static final String[] directions = {
			"south", "southeast", "east", "northeast", "north", "northwest", "west", "southwest"
	};
	public static final String[] functions = {
			"END_TALK",						"WAIT_END_TALK_FUNC",			"WAIT_END_EVENT_FUNC",			"WAIT_START_FUNC",
			"INCOMPLETE_TALK",				"NORMAL_WAIT_END_TALK",			"LIVES_REPLY_NORMAL",			"LIVES_REPLY",
			"OBJECT_REPLY_NORMAL",			"EXAMINE_MISS",					"ENTER_WAIT_FUNC",				"UNIT_TALK",
			"HABITAT_TALK",					"HABITAT_TALK_S01E02A",			"HABITAT_MOVE1",				"HABITAT_MOVE2",
			"HABITAT_MOVE_PAUSE",			"HABITAT_MOVE_STAY_FIX",		"HABITAT_MOVE_STAY_TURN",		"LIVES_MOVE_NORMAL",
			"LIVES_MOVE_CHANGE",			"OBJECT_MOVE_NORMAL",			"OBJECT_MOVE_CHANGE",			"EFFECT_MOVE_NORMAL",
			"EFFECT_MOVE_CHANGE",			"MOVE_INIT",					"MOVE_PAUSE",					"MOVE_STAY",
			"MOVE_SLEEP",					"MOVE_RANDOM",					"MOVE_BOY",						"MOVE_GIRL",
			"WAKEUP_FUNC",					"LOOK_AROUND_FUNC",				"LOOK_AROUND_DOWN_FUNC",		"LOOK_AROUND_RIGHT_FUNC",
			"LOOK_AROUND_LEFT_FUNC",		"JUMP_HAPPY_FUNC",				"JUMP_SURPRISE_FUNC",			"JUMP_ANGRY_FUNC",
			"NOTICE_FUNC",					"QUESTION_FUNC",				"SWEAT_FUNC",					"SHOCK_FUNC",
			"SPREE_START_FUNC",				"SPREE_END_FUNC",				"SMILE_START_FUNC",				"SMILE_END_FUNC",
			"ANGRY_START_FUNC",				"ANGRY_END_FUNC",				"MOVE_PLAZA_SLEEP",				"INIT_PLAZA_SLEEP_STAY_FUNC",
			"INIT_PLAZA_SLEEP_TALK_FUNC",	"INIT_SLEEP_FUNC",				"INIT_BASE_FUNC",				"INIT_DEBUG_HABITAT",
			"NORMAL_MESSAGE",				"NORMAL_EVENT",					"NORMAL_CAMERA",				"DISMISSAL_SALLY_MEMBER_FUNC",
			"DISMISSAL_SALLY_MEMBER2_FUNC",	"DISMISSAL_SALLY_MEMBER3_FUNC",	"DISMISSAL_SALLY_MEMBER4_FUNC",	"NEXT_SAVE_FUNC",
			"NEXT_SAVE2_FUNC",				"SAVE_POINT",					"WAREHOUSE_POINT",				"SAVE_AND_WAREHOUSE_POINT",
			"WORLD_MAP_POINT",				"FORMATION_HERO",				"EVOLUTION_HERO",				"WARP_LIVES_START",
			"WARP_LIVES_START2",			"WARP_LIVES_START3",			"WARP_LIVES_ARRIVE",			"WARP_LIVES_ARRIVE2",
			"WARP_LIVES_ARRIVE3",			"LIVES_WARP_START_FUNC",		"LIVES_WARP_START2_FUNC",		"LIVES_WARP_START3_FUNC",
			"LIVES_WARP_START_SUB",			"LIVES_WARP_ARRIVE_FUNC",		"LIVES_WARP_ARRIVE2_FUNC",		"LIVES_WARP_ARRIVE3_FUNC",
			"LIVES_WARP_ARRIVE_SUB",		"GET_ITEM_FUNC",				"GET_ITEM_WAIT_FUNC",			"GET_ITEM2_FUNC",
			"GET_ITEM2_WAIT_FUNC",			"JOIN_FUNC",					"LODGE_START_FUNC",				"LODGE_WAIT_FUNC",
			"LODGE_END_FUNC",				"LODGE_SOUND_FUNC",				"SAVE_START_FUNC",				"SAVE_WAIT_FUNC",
			"SAVE_END_FUNC",				"SAVE_SOUND_FUNC",				"EFFECT_TEST1",					"EFFECT_TEST2",
			"EFFECT_MOVE_DIVE",				"EFFECT_MOVE_WAVE",				"EVENT_DIVIDE",					"EVENT_DIVIDE_NEXT",
			"EVENT_DIVIDE_INIT_FUNC",		"EVENT_DIVIDE_NEXT_DAY_FUNC",	"EVENT_DIVIDE_NEXT_DAY2_FUNC",	"EVENT_DIVIDE_FIRST",
			"EVENT_DIVIDE_SECOND",			"EVENT_DIVIDE_AFTER",			"EVENT_DIVIDE_WARP_LOCK_FUNC",	"EVENT_RESCUE_ENTER_CHECK",
			"EVENT_RESCUE",					"DEBUG_SCRIPT"	
	};
	public static final String[] dungeons = {
			"Tiny Woods",		"Thunderwave Cave",	"Mt. Steel",		"Sinister Woods",	"Silent Chasm",			"Mt. Thunder",		"Mt. Thunder Peak",	"Great Canyon",
			"Lapis Cave",		"Mt. Blaze",		"Mt. Blaze Peak",	"Frosty Forest",	"Frosty Grotto",		"Mt. Freeze",		"Mt. Freeze Peak",	"Magma Cavern",
			"Magma Cavern Pit",	"Sky Tower",		"Sky Tower Summit",	"Stormy Sea",		"Silver Trench",		"Meteor Cave",		"Mt. Freeze Peak",	"Western Cave",
			"Boss 3",			"Boss 4",			"Wish Cave",		"Buried Relic",		"Pitfall Valley",		"Northern Range",	"Boss 9",			"Desert Region",
			"Southern Cavern",	"Wyvern Hill",		"Fiery Field",		"Northwind Field",	"Solar Cave",			"Lightning Field",	"Darknight Relic",	"Wondrous Sea",
			"Murky Cave",		"Grand Sea",		"Uproar Forest",	"Oddity Cave",		"Remains Island",		"Marvelous Sea",	"Fantasy Strait",	"Rock Path",
			"Snow Path",		"Autopilot",		"D50",				"D51",				"Dojo Registration",	"Howling Forest",	"D54",				"Fantasy Strait",
			"Waterfall Pond",	"Unown Relic",		"Joyous Tower",		"Far-off Sea",		"Mt. Faraway",			"D61",				"Purity Forest",	"Out on Rescue",
			"???",				"Tiny Woods",		"Unknown World",	"Frosty Grotto",	"Howling Forest",		"Pok�mon Square",	"Pok�mon Square",	"Rescue Team Base",
			"Rescue Team Base",	"D73",				"Client Pok�mon",	"Normal Maze",		"Fire Maze",			"Water Maze",		"Grass Maze",		"Electric Maze",
			"Ice Maze",			"Fighting Maze",	"Ground Maze",		"Flying Maze",		"Psychic Maze",			"Poison Maze",		"Bug Maze",			"Rock Maze",
			"Ghost Maze",		"Dragon Maze",		"Dark Maze",		"Steel Maze",		"Team Shifty",			"Team Constrictor",	"Team Hydro",		"Team Rumblerock",
			"Rescue Team 2",	"Rescue Team Maze"
	};
	public static final String[] items = {
			"Nothing",		"Stick",		"Iron Thorn",	"Silver Spike",	"Gold Fang",	"Cacnea Spike",	"Corsola Twig",	"Gravelerock",
			"Geo Pebble",	"Mobile Scarf",	"Heal Ribbon",	"Twist Band",	"Scope Lens",	"Patsy Band",	"No-Stick Cap",	"Pierce Band",
			"Joy Ribbon",	"X-Ray Specs",	"Persim Band",	"Power Band",	"Pecha Scarf",	"Insomniscope",	"Warp Scarf",	"Tight Belt",
			"Sneak Scarf",	"Gold Ribbon",	"Goggle Specs",	"Diet Ribbon",	"Trap Scarf",	"Racket Band",	"Def. Scarf",	"Stamina Band",
			"Plain Ribbon",	"Special Band",	"Zinc Band",	"Detect Band",	"Alert Specs",	"Dodge Scarf",	"Bounce Band",	"Curve Band",
			"Whiff Specs",	"No-Aim Scope",	"Lockon Specs",	"Munch Belt",	"Pass Scarf",	"Weather Band",	"Friend Bow",	"Beauty Scarf",
			"Sun Ribbon",	"Lunar Ribbon",	"Ring D",		"Ring E",		"Ring F",		"Heal Seed",	"Wish Stone",	"Oran Berry",
			"Sitrus Berry",	"Eyedrop Seed",	"Reviver Seed",	"Blinker Seed",	"Doom Seed",	"Allure Seed",	"Life Seed",	"Rawst Berry",
			"Hunger Seed",	"Quick Seed",	"Pecha Berry",	"Cheri Berry",	"Totter Seed",	"Sleep Seed",	"Plain Seed",	"Warp Seed",
			"Blast Seed",	"Ginseng",		"Joy Seed",		"Chesto Berry",	"Stun Seed",	"Max Elixir",	"Protein",		"Calcium",
			"Iron",			"Zinc",			"Apple",		"Big Apple",	"Grimy Food",	"Huge Apple",	"White Gummi",	"Red Gummi",
			"Blue Gummi",	"Grass Gummi",	"Yellow Gummi",	"Clear Gummi",	"Orange Gummi",	"Pink Gummi",	"Brown Gummi",	"Sky Gummi",
			"Gold Gummi",	"Green Gummi",	"Gray Gummi",	"Purple Gummi",	"Royal Gummi",	"Black Gummi",	"Silver Gummi",	"Banana",
			"Chestnut",		"Pok�",			"Upgrade",		"King's Rock",	"Thunderstone",	"Deepseascale",	"Deepseatooth",	"Sun Stone",
			"Moon Stone",	"Fire Stone",	"Water Stone",	"Metal Coat",	"Leaf Stone",	"Dragon Scale",	"Link Cable",	"Ice Part",
			"Steel Part",	"Rock Part",	"Music Box",	"Key",			"Used TM",		"Focus Punch",	"Dragon Claw",	"Water Pulse",
			"Calm Mind",	"Roar",			"Toxic",		"Hail Orb",		"Bulk Up",		"Bullet Seed",	"Hidden Power",	"Sunny Orb",
			"Taunt",		"Ice Beam",		"Blizzard",		"Hyper Beam",	"Light Screen",	"Protect",		"Rainy Orb",	"Giga Drain",
			"Safeguard",	"Frustration",	"Solarbeam",	"Iron Tail",	"Thunderbolt",	"Thunder",		"Earthquake",	"Return",
			"Dig",			"Psychic",		"Shadow Ball",	"Brick Break",	"Evasion Orb",	"Reflect",		"Shock Wave",	"Flamethrower",
			"Sludge Bomb",	"Sandy Orb",	"Fire Blast",	"Rocky Orb",	"Aerial Ace",	"Torment",		"Facade",		"Secret Power",
			"Rest",			"Attract",		"Thief",		"Steel Wing",	"Skill Swap",	"Snatch Orb",	"Overheat",		"Wide Slash",
			"Excavate",		"Spin Slash",	"See-Trap Orb",	"Mug Orb",		"Rebound Orb",	"Lob Orb",		"Switcher Orb",	"Blowback Orb",
			"Warp Orb",		"Transfer Orb",	"Slow Orb",		"Quick Orb",	"Luminous Orb",	"Petrify Orb",	"Stayaway Orb",	"Pounce Orb",
			"Trawl Orb",	"Cleanse Orb",	"Observer Orb",	"Decoy Orb",	"Slumber Orb",	"Totter Orb",	"Two-Edge Orb",	"Silence Orb",
			"Escape Orb",	"Scanner Orb",	"Radar Orb",	"Drought Orb",	"Trapbust Orb",	"Rollcall Orb",	"Invisify Orb",	"One-Shot Orb",
			"Identify Orb",	"Vacuum-Cut",	"Reviver Orb",	"Shocker Orb",	"Sizebust Orb",	"One-Room Orb",	"Fill-In Orb",	"Trapper Orb",
			"Possess Orb",	"Itemizer Orb",	"Hurl Orb",		"Mobile Orb",	"Toss Orb",		"Stairs Orb",	"Longtoss Orb",	"Pierce Orb",
			"Cut",			"Fly",			"Surf",			"Strength",		"Flash",		"Rock Smash",	"Waterfall",	"Dive",
			"Link Box",		"Switch Box",	"Weavile Fig",	"Mime Jr. Fig",	"Beatup Orb",	"G Machine 6",	"G Machine 7",	"G Machine 8"
	};
}