# TextQuest JSON Dungeon Documentation

Hello and welcome! One of the features of TextQuest is that it allows anyone familiar with some basic JSON and general design concepts to create their own dungeons. This document will explain how all of that works. First let's cover a couple of important concepts.

### JSON

JSON (JavaScript Object Notation) is basically just a way to store data - in our case, all the data associated with a dungeon. If you're unfamiliar with JSON, you can read about it [here](https://www.w3schools.com/js/js_json_intro.asp). Rest assured, even if you're not a programmer it is fairly easy to pick up.

### Triggers and Mapping

A core concept of designing a dungeon is the concept of **triggers**. As the dungeon designer you want to be able to have events trigger (or cause) other events. For example, the hero enters a room, tries to open the chest, and all the doors lock and monsters show up and attack. Or maybe the hero decides to smash a barrel, but it was filled with explosives so when they do they'll take damage. The (theoretical) possibilities of triggers are endless, and they're essential to create dynamic dungeons that "react" to actions of the player.

So how as a dungeon designer do you access these wonderful triggers? That brings us to the concept of a **map**. A map is a core concept in computer science, but we don't need to dive into deep details. A mapping is basically just a connection between data. A common example is a dictionary, which you can think of as a map from Words to Definitions. If you look up the Word "banana" you would get a Definition like "a yellow tropical fruit". 

In TextQuest, triggers are mapped by character strings. One simple example is "giveExp", which allows us to directly give experience points to the hero. We provide as a **paramater** the amount of experience we wish to give. All triggers use `lowerCamelCase`, so the words are all squashed together with the first letter of each word except the first capitalized. We'll talk about how to use triggers more specifically later on.

### Optional vs Required Properties

The last and easiest concept to understand is that many properties are optional (not required). This means that you do not have to include that property for things to work. One example of an optional property is the "tutorial" property. This is a message displayed in the "tutorial" text area when the hero enters a given room. If you don't include this property, no message will be displayed.

I highly recommend leaving optional properties out unless you need them. This will keep your JSON cleaner and easier to read. I'll do my best to indicate which properties are required, as well as indicating defaults in cases where it makes sense.

## Creating A Dungeon

A dungeon has a "dungeonName" property and a List of "rooms".

### Dungeon Rooms

The dungeon is organized into "rooms". Each room can theoretically be as large and complex as you want it to be. Rooms are connected to each other by directions (North, East, South, West, Up, and Down). Most rooms will be connected to only only a few other rooms.

#### Room Properties

* name - The "display name" for a room. **Required**
* id - Each room needs a unique identifier. You can number rooms sequentially (1, 2, 3... etc), or by floor (Rooms on Floor 1 11, 12, 13... Floor 2 21, 22, 23 -  recommended). **Required**
* description - A basic description of the room in general (not all the items as these are described separately)
* tutorial - A tutorial message that will be displayed the first two times the hero enters the room. Optional
* lighting - The initial light level of the room, from 0.0 (pitch black) to 1.0 (fully lit). Affects visibility of items/monsters in the room. Required?
* items - a List of items that are just sort of laying around in the room (on a table, on the floor, on a shelf, etc). The hero will pick up all these items when they type "loot". Optional
* monsters - a List of monsters in the room. By default they won't attack but their presence prevents the hero from exploring further. Optional
* connectedRoomIds - a Map between Directions (like "EAST") and the id of the room it's connected to in that direction. This is a very important paramater as it defines how everything connects. **Required**, but you only need to list directions that are connected (if there's nothing to the North of a room, you don't need to list that). **Note**: if you want connections to be two-way connections you need each room connected to the other (so Room 4 is connected via EAST to Room 5, and Room 5 connects to Room 4 via WEST). Be careful with one-way connections since they could leave the hero trapped. **Note 2**: Technically there's no geography enforced. So Room 4 could go east to Room 5, and Room 5 could lead "East" back to Room 4. I recommend avoiding this as it will confuse the designer and the player both.
* obstacles - a List of Obstacles in the room. Obstacles represent things the hero can interact with (puzzles to solve, chasms to jump, etc) that often prevent progress. Obstacles are an important part of designing interesting dungeons. Optional
* chest - Optional
* bossFightFileLocation - boss fights are stored separately as their own JSON file. This is a name of a file in the "encounters" directory. Optional
* hiddenItems - you can "hide" items here in a map corresponding to their location. So if something is hidden by the fountain, it won't turn up when the player types "loot". They would have to "search fountain".

Triggers (all Optional)
* onHeroLeave - a place to add triggers for when the Hero leaves a room. Mapped by direction.
* onLightingChange - add triggers for when the lighting level changes. Requires specifying the lighting level
* specialRoomActions - you can define special action words unique to this room. For example, if there's a fountain you could allow the player to "drink" from it. It's up to you to decide what happens when they take that action
* onFightStart - triggered when combat starts
* onFightEnd - triggered at the end of combat (if the hero survives!)
* onSpellCast - triggered when a spell of a particular type is cast. Like lighting change, this is a map and requires specifying the spell type. Multiple types can be specified (Player casts ice spell -> event A, Player casts fire spell -> event B). You can use "any" for a wildcard.

### Other Things With Properties

#### Monster Properties

* name - The monster's name. **Required**
* health, might, defense - the monster's stats. Might increases the power and accuracy of physical attacks. Defense reduces damage and increases chance to dodge. Health is how much damage the monster can take before death. **Required**
* isMiniboss - a flag indicating if the monster is a miniboss. Optional
* abilities Optional Together with the behavior property, can be used to define more interesting behaviors for a monster.
* behavior Optional

Triggers (all Optional)
* onDeath - when a monster dies
* onTakeDamage - when a monster takes damage (does trigger when monster dies from damage)
* onDisable - triggered when a monster is disabled (i.e. stunned)

#### Item Properties
 
* name - Item's name. **Required**. Note that some item names have special meaning that does not need to be added. For example just naming an item "Potion" is enough to make a drinkable healing potion that will restore 9 health.
* darklight - A special property for the Darklight Dungeon - item is only visible in complete darkness. Optional
* value - monetary value of this item. Optional (default 0)

Triggers (all Optional)
* onPickup - a trigger for an event to happen when the item is picked up (looted) by the player.

There's not a lot you can currently do with items. We'll work on this!


#### Obstacle Properties

* @class - defines the Class or Type of this obstacle. A full discussion of Java classes is way outside of the scope of this document, but the long and short is that this needs to be the "fully qualified class name" of a class that extends Obstacle. All such classes live in the package `paul.TextQuest.entities.obstacles` package. Example: "paul.TextQuest.entities.obstacles.Chasm". **Required**
* name - The name of the obstacle as displayed to the player. Can be simple ("Chasm") or descriptive ("Dark Deep Chasm of Scariness"), as you like. **Required**
* solution - A String representing the associated action that solves the obstacle. **Required**
* blocksLooting - Optional flag as to whether this obstacle should prevent the Hero from freely looting the room (default - false)
* displayIfCleared - Optional flag as to whether we should continue to include this obstacle in the room description once it's been cleared. For something like a smashed barrel, we probably don't want to keep displaying it. For a riddle, it's good to remind the player that this was the room where they solved it.
* blockedDirections - A List of directions that are blocked until this obstacle is cleared. Optional
* expAmount - the amount of experience the hero should receive when clearing the obstacle. Different types have different defaults, but the base default is 25 experience points.

Triggers (all Optional)
* onSmash - Some obstacles are "smashable". This trigger defines what happens when they're smashed.

### List of Triggered Events

All possible events must be hard coded into the game. That means you're limited to the (currently very small) set of events I've programmed into the game. Keep in mind that the events are just one side of it. In other words, these are just "some things that can happen". **How** and why they happen is determined by the trigger. Here are the current possible events:

#### Param Events

These events need an extra bit of information, often a number. For example if you want to giveExp - how much? If you want to spawn a monster, I need to know the name. The parameter is listed in parentheses here:

* createMonster (name) - currently the only monster that can be created is a skeleton. More flexibility to come!
* explode (damage amount) - the hero takes some damage from an explosion
* giveExp (exp amount) - the hero gets experience
* bump (object) - Used to momentarily delay the hero from leaving the room.
* heal (healing amount) - heals the player


#### Void Events

These events just happen, and they don't need any extra information.

* douse - the room becomes completely dark
* light - the room becomes fully bright
* makeMinibossStrong/makeMinibossWeak - created custom events for Darklight, these alter the stats of any miniboss in the room
* startFight - starts combat with any monsters in the room
* victory - Ends the game. Use with caution, I guess.
* crackFloor - creates a Chasm obstacle and prevents retreating. The hero will be stuck unless they have Boots of Vaulting

Hopefully soon we'll add many more possible events, and even the ability to create custom events.

### Future Triggers

Here are some triggers that I hope to add to the game soon:

Room:
* speechTriggers - similar to Riddles, triggered when a hero speaks a given phrase
* onSearch - when a Hero searches (a specific location)
* onEnter - when a Hero enters the room

Items:
* onDrop - when an item is dropped by the player
* onUse - when an item is used

Monsters:
* onDealDamage - triggered when the monster deals damage to the player

Obstacles:
* onAttempt - when someone attempts an obstacle
* onClear - when an obstacle is cleared

