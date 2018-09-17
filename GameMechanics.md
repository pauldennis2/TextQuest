# Game Mechanics

Hello and welcome! This document will provide some general explanation of the game mechanics in TextQuest. We won't cover parsing (how the game interprets user commands) here - this is more focused on how the dungeon works at a mechanical level.

### Intended Audience

This document is intended for people with some degree of knowledge about RPG/action-type games. I'm going to take for granted the fact that you understand basic concepts like health, taking damage, gaining experience, leveling up, using and equipping items *in a general sense* (the point of the document is to describe these more specifically). If these terms leave you confused, well, go play some Legend of Zelda - you've been missing out.

## Basic Game Concepts

### Hero

#### Stats

There are currently 3 core stats that the game tracks for the hero and monsters:

* Might - representing physical power. Increases damage and accuracy in combat. (Each point +1-2 damage, every 4 points increases accuracy 5%)
* Defense - representing physical defense. Decreases damage taken and increases avoidance/reduces chance for opponent to hit (Every 2 points 5% avoidance, -1 damage taken)
* Health - when it gets to zero, dead.

The Hero has two additional stats that don't much matter right now: stealth and magic.

#### Experience and Leveling Up

The hero gets experience from fighting monsters, solving puzzles, and so on. On level up, depending on level, you'll have the ability to increase your stats and learn new spells and skills. The game will provide prompts asking what stats to increase and what type of spell you want to learn.

Coming Eventually: the ability to define custom level up actions and operations (basically allow the dungeon designer to decide what leveling up looks like in their game). 

### Dungeon

TextQuest is organized around "dungeons". A dungeon is a self-contained unit of adventure for the player to explore. You can only save your game between dungeons. The dungeon is divided into rooms, which are connected to each other by passages. When you enter a room, you'll see a description of the room's features and a list of the ways out. The hero has no physical coordinates within a room; they're basically considered to be standing at the entrance of the room, and (unless scripts intervene) they have a choice about whether to engage in combat, loot items, etc.

Dungeons can be designed in a lot of different ways. The dungeons I've created are generally modeled after Legend of Zelda (albeit much smaller). That is, at some point roughly halfway through you'll find a special item in the dungeon that is required to solve crucial puzzles and beat the boss. Again, it's pretty important for dungeons to be small because the player can't save during a dungeon. If you want to create a bigger dungeon, you could break it up into a few smaller contained dungeons. (You **can** have a dungeon be as big as you want, but again, no saving).

## Magic - Or Whatever You Want to Call It

TextQuest is based on a swords and sorcery dungeon crawling concept. And even though you can "paint" it however you want (like making a Star Wars/Jedi-themed adventure) it wouldn't be missing something if it didn't have magic. Here's how magic works:

The hero has a `magic` stat that does nothing.

The hero can have knowledge of various areas of magic (defined by the designer). These could be traditional elements like fire magic, earth magic, whatever, or whatever categories you want to come up with.

Spells are defined in JSON like dungeons and make use of the same library of events.

Properties:
* prereqs - The areas of magic the hero must know in order to cast the spell. Optionally can be specified with a number like "fire 2" meaning the hero needs a 2nd level of fire mastery to cast the spell.
* reagents - any items that are required for and consumed by the spell. If missing, spell will fail. If present they'll be consumed.
* requiredItems - any items that are required for the spell. If missing, spell will fail. If present spell will be cast without consuming the item.
* actions - the actual effects of thespell.

The hero will be offered opportunities on levelup to learn new areas of magic or perhaps to advance in an area in which they already have some skill.