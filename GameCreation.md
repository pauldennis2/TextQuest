## Game Creation

One of the features of TextQuest is the ability for anyone to create their own content. TextQuest is basically just an engine, and I'm trying to make it so that anyone can write content for that engine. There's a separate document on the specifics of how to create your own dungeons in JSON. This is an important first step, but you might want to do more that just create a dungeon - you want to create a whole game or story. Enter the GamePlan.

### GamePlan

The "GamePlan" is a collection of all the higher-level information about how your "game" works. There are five components to the GamePlan:
	
* Leveling Plan - how much experience does it take to level up and what happens when you do?
* Look And Feel - the overall "Look and Feel" of your game - are we in a sci-fi universe? Do we refer to "magic" or "force powers"? Should the lighting level of the room be described? Etc. Not yet implemented.
* heroStartingInfo - hero's starting items, skills, spells, etc.
* spellbooks - what spells are available in your game?
* dungeonGroups - available dungeons
* Shops and other stuff eventually

Note that you're not defining these features directly. The gameplan just knows the file paths where they can be located. 

### Level Up Plan

Now *you* get to decide what leveling looks like in the context of your game.
	
Components:
* expAmounts - a List of the total amount of experience needed to reach successive levels. By default the hero starts at level 0. The amounts should increase since the represent the total. Your
* levelUpActions - at each level up, what are the hero's available options? Each level has a list, so you can define the same action multiple times (for example if you want to give the hero 3 chances to increase their stats). 
* levelingRestoresHealth - true by default. If you want a more "endurance" type game you can change it so that the Hero's health is NOT restored to full when leveling up.
* levelingRestoresSpells - true by default. See above, but for spells.

### Look and Feel

### Hero Starting Info

One of the components you want to be able to determine is the starting configuration for the hero - what level they are, what items they have, what spells they know (if any), etc. This really just comes in the form of a serialized Hero. The player will still be able to choose a name for their Hero, but all other properties (stats, items, spells) will be copied over.

### Dungeon Groups

### Spellbooks