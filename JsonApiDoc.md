# TextQuest JSON Dungeon Documentation

Hello and welcome! One of the features of TextQuest is that it allows anyone familiar with some basic JSON and general design concepts to create their own dungeons. This document will explain how all of that works. First let's cover a couple of important concepts.

### Intended Audience and Technical Level

This document is intended for people with some degree of technical proficiency. When describing how stuff works, I will do my best to assume you're not a programmer. But there are some basic concepts of programming such as "variables" and "maps" and some degree of syntax. Again, I'll do my best to provide examples, but if you're already glazing over, this probably isn't for you.

### JSON

JSON (JavaScript Object Notation) is basically just a way to store data - in our case, all the data associated with a dungeon. If you're unfamiliar with JSON, you can read about it [here](https://www.w3schools.com/js/js_json_intro.asp). Rest assured, even if you're not a programmer it is fairly easy to pick up. One note is that JSON does not enforce any ordering on the properties; it's just as happy with `"name","description","items"` as with `"items", "description", "name"`. I recommend trying to stick with an order when you're creating your dungeon in order to keep things organized; see existing dungeons for examples/suggested order.

### A Note on Conventions

A convention in this case just means a general agreement (not a fun gathering of people who share a passion for something nerdy). First, I'd like to briefly describe some of the conventions I've used:

* Events are all named with `lowerCamelCase` meaning the words are squished together with no spaces, with each word capitalized except the first. Example: `addMonster`. You have to use these event names to access events. If you tried `AddMonster` or `add_monster` the game wouldn't know what to do.

### Triggers, Events and Mapping

A core concept of designing a dungeon is the concept of **triggers**. As the dungeon designer you want to be able to have circumstances trigger (or cause) **events**. For example, the hero enters a room, tries to open the chest, and all the doors lock and monsters show up and attack. Or maybe the hero decides to smash a barrel, but it was filled with explosives so when they do they'll take damage. The trigger is the circumstance that causes the event. The event is what actually happens. The (theoretical) possibilities of triggers are endless, and they're essential to create dynamic dungeons that "react" to actions of the player. It's up to the dungeon designer to create triggers and events that "make sense" within the context of the dungeon. So in the case of this barrel smashing hero, the trigger would be `onSmash`, and the event would be `explode 10` to deal 10 damage to the hero.

So how as a dungeon designer do you access these wonderful triggers? That brings us to the concept of a **map**. A map is a core concept in computer science, but we don't need to dive into deep details. A mapping is basically just a connection between data. A common example is a dictionary, which you can think of as a map from Words to Definitions. If you look up the Word "banana" you would get a Definition like "a yellow tropical fruit". 

In TextQuest, triggers are mapped by character strings. One simple example is "giveExp", which allows us to directly give experience points to the hero. We provide as a **paramater** the amount of experience we wish to give. All triggers use `lowerCamelCase`, so the words are all squashed together with the first letter of each word except the first capitalized. We'll talk about how to use triggers more specifically later on.

Triggers persist by default (unless otherwise noted). That means that if there's a trigger in a room when a hero uses a particular spell, it'll keep triggering every time the hero casts the spell (it doesn't go away after the first time).

### Adding and Removing Triggers

An important feature of triggers/events is the ability to **dynamically** add and remove triggers from the dungeon. For example, let's say you've defined a room with a healing fountain. You've defined a `specialRoomAction` so that when the hero drinks from the fountain they are healed. But let's say you want this fountain set up so that if the hero casts an ice spell, it will freeze over and be un-usable. Casting a fire spell unfreezes the fountain.
```
"onSpellCast":
      {
      	"any":"modStat numAvailableSpells 2",
      	"ice":"removeTrigger specialRoomActions drink;print \"The Fountain freezes over.\"",
      	"fire":"addTrigger specialRoomActions drink heal 10;print \"The fountain is unfrozen.\""
      }
```

The possibilities here are endless, but it's worth specifically noting that you can use this capability to override the default persistence of triggers. So if you have a trigger that you want to only happen once you could add `;removeTrigger <info>`. You can also think of this as enabling/disabling triggers (there is no alternative way to enable/disable triggers).

### Optional vs Required Properties

The last and easiest concept to understand is that many properties are optional (not required). This means that you do not have to include that property for things to work. One example of an optional property is the "tutorial" property. This is a message displayed in the "tutorial" text area when the hero enters a given room. If you don't include this property, no message will be displayed.

I highly recommend leaving optional properties out unless you need them. This will keep your JSON cleaner and easier to read. I'll do my best to indicate which properties are required, as well as indicating defaults in cases where it makes sense.

When I say a property is required, it means that leaving it off (except in special circumstances) will cause problems.

### Room IDs

As we'll discuss below your dungeon is made up of "rooms". Each room must have a unique identification/ID number. You can use whatever numbering scheme you want. One easy suggestion for small dungeons is to assign each floor a block of 10 IDs (So all the rooms on the first floor are 1-9, second floor 10-19, etc). This helps keep things organized. Alternatively for bigger dungeons you could do 101, 102, 103, 201, 202, 203, etc. Whatever works for you. A couple important notes:

* Each id must be unique. You can't have two rooms with id 50.
* By default the dungeon entrance (where the player starts) will be the room with ID 1. You can define this as a custom property (see Dungeon Properties).

### Quotes, Spaces, and General Syntax

This section will discuss some best practices and common problems.

**Special Characters** - First, it's just generally good principle to avoid using any special characters in regular names. So don't try to call a room "The Boss Room!" - instead just use "The Boss Room". Special characters with specific meanings: @, $, ;, {}, [], "", \. Special characters without specific meanings: _, :, ,, %, #, !, &, *, ^, <>, ?, ', /, +.

Here is a quick reference of the special meanings of the various characters (more explanation below):

* @ - used to indicate remote instructions (triggered event should happen in a different room)
* $ - used to start a conditional event
* ; - used to group multiple events together
* {} - used to refer to a dungeon variable
* [] - used to define a condition
* "" - used to define a String
* \ - used to "escape" quotes.

What characters are safe? You can use any alphanumeric character (a-z, A-Z, 0-9) in names. Dashes are OK too. 

#### Spaces and Quotes

All of the instructions in your JSON file need to be parsed. The parsing is largely based on spaces. So in the event statement `heal 10`, it sees the first "word" is "heal", and finds the appropriate action. It also knows it needs an amount to heal, so it tries to parse the "10" to a number. **Note**: it's very easy to screw things up by adding even just one extra space. So while the difference between `heal 10` and `heal  10` (two spaces) may seem insignificant, the first one works and the second doesn't. Also, when using semi-colons to include multiple events, don't add an extra space after the semi-colon (I know it's tempting). `print Boom;explode 10` works. `print Boom; explode 10` fails.

So don't use extra spaces!

But what happens if you want to define something that does have spaces? One of the most basic events is `print`, which allows us to print out some information to the user. You could just put your message as one "word", using underscores: `print Something_happened`. This is sort of ugly however and doesn't look very professional. The better way to do this is to use the backslash `\` and quotes `""` to define a String. This allows us to define one "word" with spaces in it. Because we're already inside a string, we have to use the backslash to escape the quotes we're going to add. So the new message would be:

`print \"Something happened\"`

This is still encased in quotes as normal, so in your JSON this would look like:

```json
"onHeroEnter":"print \"Something happened\""
```

Note that if you just want one word you don't need the quotes. `print BOOM` works fine.

So here are the quick and dirty rules:
1. Avoid using special characters (especially @, $, ;, {}, [], "", and \) except for their intended purpose.
2. Be very careful not to have any  extra   spaces.
3. Use **escaped** quotes to define strings that can have spaces.

#### Advanced Functionality Combo Warning

This guide will cover several different "advanced" functions (things that are a bit more complicated than just creating a monster when the hero enters a room). Examples include triggering events in other rooms, conditional triggers, and triggering multiple events at the same time. At this point in development, the behavior when trying to combine these functions is hard to predict. For example, I'm not sure what would happen if you tried to have a conditional event triggered remotely. You can always give it a try, but your mileage may vary. Consider combining these functions to be unsupported at this time.

### Dungeon Variables/Values

(Note: if you're a programmer, the logic for this is very similar to storing information in the HttpSession)

To create a dynamic dungeon experience you might need to keep track of some variables that persist throughout the dungeon. Maybe you're creating a water dungeon and want to keep track of the water level. Or maybe you want to keep track of the number of monsters the hero's killed for some reason. There is now a great way to do that, using events described below. For now, let's go over the maps. These maps are attached to the dungeon. It's important to note that these variables don't DO anything by themselves. You'll need to refer to them in your events to have any effect. We'll see an example of that later. Both of these maps store things using a String key. So if you want to keep track of the water level you could use `waterLevel` as your key.

* Variables Map<String, String> - this map accepts String inputs (in other words text). The advantage of this map is that it's totally open. You can set `waterLevel` to "bananas" if you want. The downside is that you can only work with these as strings. 
* Values Map<String, Integer> - this map only accepts integers (whole numbers).  Here you could set the `waterLevel` to 1, 2, 0, -1, whatever. The advantage of this map is that you can work with the data as numbers - so it would be possible to create a condition such as `$if[{waterLevel} > 2]` -> whatever. You can also use `addToDungeonValue` to add or subtract. So if you want to increase the water level, `addToDungeonValue waterLevel 1` will add 1 to whatever the existing level is.

#### Referring to Values

Let's say for some arbitrary reason you've been keeping track of the dungeon's water level, and you want to create an event that heals the player an amount equal to the water level. You would use the appropriate events to set the `waterLevel` value, but how do we actually access that? The answer is that we use a special syntax. Whenever you want to refer to a value/variable, you use braces `{}` surrounding the value/variable's name: `{waterLevel}`. (You don't need to specify here whether it's a "variable" or a "value" - it'll look in both places, with preference for values). So, to heal the player an amount equal to the water level, we would just use `heal {waterLevel}`. It'll automatically look for a variable or value with that name and insert the appropriate value. So if the water level is '2', you would get `heal 2`. Note: you should definitely not use braces in any normal name. So don't try to name a room "{Special Room}". In general, avoid using special characters such as $, @, {}, |, :, and so on.

**Note**: the only way to work with values right now is adding or subtracting. But what if you want to multiply something? Well, it's limited, but you can use the value while you're modifying it. So let's say we want to *double* the water level: `addToDungeonValue waterLevel {waterLevel}` doubles the current water level.

#### Referring to the Hero - Using Dot Notation

You can also now refer to the Hero's stats using "dot notation". `{hero.might}` would be a dynamic reference to the hero's current might stat. These expressions can be included in conditions. Currently you cannot refer to any of the hero's "complex" attributes like their location or the contents of their backpack. You can only refer to integer properties (health, maxHealth, might, magic, sneak, defense, maxSpellsPerDay, level, and exp) and the hero's name.

You can also explicitly refer to dungeon variables like: `{dungeon.waterLevel}`. If you don't use any dot notation (just `{waterLevel}`) it'll assume you're referring to dungeon variables. 

#### Creating Conditional Events

In order to truly make use of the dungeon variables, we need to be able to check them in conditional statements. For example, maybe you're keeping track of the number of spells the hero has cast using a value named `numSpellsCast`. In order to make an event conditional on the number of spells cast, we use an `$if` statement:

`$if[{numSpellsCast} > 5}] heal 10` would make it so that the player would be healed if they have previously cast more than 5 spells.

Let's diagram this out. We basically have two parts here - the conditional and the actual event. The conditional is `$if[{numSpellsCast} > 5]`. The event is `heal 10` (you should already be comfortable with that part). Let's break down the conditional. It **always** starts with `$if` (lower case). This is just a token to let the parser know we're starting a conditional. The actual condition to be evaluated lives inside the brackets `[]`. In this case it is `{numSpellsCast} > 5`. The general shape of a condition is: {first parameter} {comparator} {second parameter}. You can use whatever combination of constants and variables you want (though the condition `5 < 3` would always evaluate to false, it's a legitimate condition). We use braces `{}` to refer to the variable/value we want (see above).

Possible comparators:
* = - equals. The only comparator that works with String variables.
* != - NOT equal. Returns true if the properties are not equal, or false if they are.
* Greater than. Only for numbers (as are all the rest)
* Greater than or equal to.
* < - less than
* <= - Less than or equal to.

**Else**: You can now define an event that will be triggered if your condition isn't met. 

`$if[{numShadowsKilled} = 3] print \"A barrier has been removed.\" $else print \"You feel a barrier weakening.\"`

This will print one message if the condition is met and a different one if it isn't.

#### Boolean Logic

Conditions now happily support simple [boolean logic](https://en.wikipedia.org/wiki/Boolean_algebra) (sorry, there are no good quick online boolean tutorials. Message me iff this is false). You can use "AND" and "OR" statements using those keywords or using C-style operators ("&&" for AND, "||" for OR). For example:

`$if[{numSpellsCast} > 3 OR {hero.magic} > 2] print Bigmagic` 

Boolean logic cannot currently be nested - so you couldn't have a statement like `if[(3 > 2) && (1 = 1 || 2 = 3)]

#### Designing Events

A note on designing events: most events are "lightweight" in what they print out. This allows you the freedom as the dungeon designer to decide how to describe the event. On the flip side, it means that (a lot of the time) if you don't add a custom `print` statement, the event won't be described to the player. Sometimes this is good - you might want to have things happen without the player being aware. `createMonster Skeleton;print \"A skeleton appears.\"`

Coming... maybe not soon, but eventually: the ability to define more complex events you can later refer to with keywords. (Scripts).

### Saving and How Dungeons Work

Out of a desire to keep the "saving" process simple, it's only possible for the player to save their game between dungeons. This makes things a lot simpler because we don't have to keep track of the hero's location within a dungeon, which items they have picked up, enemies fought, status of dungeon variables, etc. How this works in practice is that at some point you will need to set the dungeon to "cleared" after the hero is finished doing all of the essential things (killing the boss, saving the hostage, etc.). This means that the player has the option to leave the dungeon and save their progress. Once the hero leaves a dungeon they can't come back.

If the player decides to leave the dungeon before it is cleared they will lose all progress. This is a good incentive to keep dungeons relatively short. You can break a dungeon up into multiple sub-dungeons (though the player cannot travel between them).

At some point there will be an additional JSON file to define a group of dungeons as part of a larger story. Coming soon!

### Tick Tock Goes the Clock

In version 0.0.9, you can now define a "clock" for your dungeon. Nearly ever component now has two new properties: "onTick" and "onTock" (these properties won't be listed below since they are near-universal). You have access to two new events: `doTick` and `doTock`. These events will trigger the appropriate events for all the components. It's your job as the dungeon designer to decide what "advances the clock" in your dungeon. You could do a tick every time the hero moves, every time they cast a spell, etc. "tick" and "tock" just represent two different time-based events. You can use them however you want; by convention, I'd suggest that if one is more common, it should be ticks (ticks happen more than tocks). You could use both together to represent something like seconds/minutes. For example, every time the hero moves, do a tick. Every time a `tick` happens, we advance a dungeon variable called `time`, and if `time = 10` we reset time and do a `tock` (this then triggers whatever `onTock` events you've assigned.

**Note**: You **will** get stuck in an infinite loop if you do something like `"onTick":"doTick"` (it keeps triggering itself over and over). This might seem obvious, particular if you have experience programming. But it's also possible to get stuck in a less obvious way: `"onTick":"light" [...] "onLightingChange:{"WELL_LIT":"doTick"}` would have the same effect.

**Note 2**: If you have multiple tick/tock actions, it's hard to specify the order in which they will happen. In general, you should assume that tick/tock events will happen in a random order. (If A, B, and C all have `tick` events, then the actual order in which those events happen could be A, B, C; B, C, A; C, B, A - etc). 

## Creating A Dungeon

A dungeon has a "dungeonName" property and a List of "rooms".

### Dungeon Rooms

The dungeon is organized into "rooms". Each room can theoretically be as large and complex as you want it to be. Rooms are connected to each other by directions (North, East, South, West, Up, and Down). Most rooms will be connected to only only a few other rooms.

#### Dungeon Properties

* dungeonName - The display name for the dungeon. **Required**
* rooms - The core data describing the dungeon. **Required**
* itemLibrary - a Map that allows you to create custom items and reference them later. Optional
* monsterLibrary - a Map that allows you to create custom monsters and reference them later. Optional
* template - see the next section
* entranceRoomId - optional, allows you to define a custom ID for your entrance. Really optional and shouldn't be needed, but it's there if you want it.


The "library" properties really just provide shortcuts for items and monsters that you want to use in multiple places. If you just want a monster to be used once, you should just put it in the room's list of monsters (see below). They are also useful for defining "interesting" objects and monsters that are created by triggered events.

Triggers (optional):
* onVariableSet - triggered when any dungeon variable is changed. You'll probably need to use a conditional to make this behave how you want it to. `"onVariableSet": "$if[{numSpellsCast = 5}] print Awesome"` would print "Awesome" when the hero casts their 5th spell (assuming you are properly tracking that).

#### Dungeon Template

The Dungeon Template is a convenience feature that allows you to declare properties that will be applied to all rooms in the dungeon (this includes triggers). This means you don't have to manually declare those properties for every room. Templated properties won't overwrite manual properties. So for example, let's say that for the whole dungeon you want the hero to be healed when combat ends. You could add `"onCombatEnd": "heal 10"` to the template to create this effect. But if in a given room you added a different `onCombatEnd` event, that room's event would take precedence and the hero wouldn't be healed. All properties can be added to the template except:
* id - shouldn't be filled by templates.
* bossFight - same
* connectedRoomIds - same
* lighting - problem with simple/complex types

#### Room Properties

**Room Description Note**: Keep in mind when adding a description to the room that all the visible features, monsters, obstacles, will be described by the game dynamically. It's best to rely on these descriptions when possible. Consider an example: if you included an item about how the room is "dimly lit" in the description, what happens when the hero uses a torch? Well, the **actual** lighting level of the room would change, and you'd end up with a conflicting description: "The room is well-lit. This dim room is <blah blah>". Similarly, if you put something about a monster in the room description, when the monster is killed, that description either becomes incorrect or must be modified.

**In general try to avoid including anything changeable in the description.** Anything about the room's description you want to change can be set up as a feature, a monster, etc. Descriptions are optional, and if you don't have anything additional to say about the room, you can leave it off.

* name - The "display name" for a room. **Required**
* id - Each room needs a unique identifier. You can number rooms sequentially (1, 2, 3... etc), or by floor (Rooms on Floor 1 11, 12, 13... Floor 2 21, 22, 23 -  recommended). **Required**
* description - A basic description of the room in general (not all the items as these are described separately) Optional.
* tutorial - A tutorial message that will be displayed the first two times the hero enters the room. Optional
* lighting - The initial light level of the room, from 0.0 (pitch black) to 1.0 (fully lit). Affects visibility of items/monsters in the room. Required?
* items - a List of items that are just sort of laying around in the room (on a table, on the floor, on a shelf, etc). The hero will pick up all these items when they type "loot". Optional
* monsters - a List of monsters in the room. By default they won't attack but their presence prevents the hero from exploring further. Optional
* connectedRoomIds - a Map between Directions (like "EAST") and the id of the room it's connected to in that direction. This is a very important parameter as it defines how everything connects. **Required**, but you only need to list directions that are connected (if there's nothing to the North of a room, you don't need to list that). **Note**: if you want connections to be two-way connections you need each room connected to the other (so Room 4 is connected via EAST to Room 5, and Room 5 connects to Room 4 via WEST). Be careful with one-way connections since they could leave the hero trapped. **Note 2**: Technically there's no geography enforced. So Room 4 could go east to Room 5, and Room 5 could lead "East" back to Room 4. I recommend avoiding this as it will confuse the designer and the player both.
* obstacles - a List of Obstacles in the room. Obstacles represent things the hero can interact with (puzzles to solve, chasms to jump, etc) that often prevent progress. Obstacles are an important part of designing interesting dungeons. Optional
* chest - a Chest that can contain items for the player. Optional
* hiddenItems - you can "hide" items here in a map corresponding to their location. So if something is hidden by the fountain, it won't turn up when the player types "loot". They would have to "search fountain".
* features - every room can have a list of "Features". Features are mainly a part of how the room is described - they can also be used in other ways (such as Mirror features being used in the Shine Puzzle).

Triggers (all Optional)
* onHeroLeave - a place to add triggers for when the Hero leaves a room. Mapped by direction.
* onLightingChange - add triggers for when the lighting level changes. Requires specifying the lighting level
* specialRoomActions - you can define special action words unique to this room. For example, if there's a fountain you could allow the player to "drink" from it. It's up to you to decide what happens when they take that action
* onFightStart - triggered when combat starts
* onFightEnd - triggered at the end of combat (if the hero survives!)
* onSearch - when a Hero searches a specific location. **Not persistent** - by default this trigger goes away after it's triggered.
* onSpellCast - triggered when a spell of a particular type is cast. Like lighting change, this is a map and requires specifying the spell type. Multiple types can be specified (Player casts ice spell -> event A, Player casts fire spell -> event B). You can use "any" for a wildcard.
* onHeroEnter - triggered when the Hero enters the room. Optional flag to doOnce.
* onItemUse - triggered when the hero uses a specific item. Use "any" for a wildcard.
* onHeroAction - this allows you to define triggers for any specific hero action. For example you could add a trigger to take place when the hero searches, loots, etc. Note that the action will still be taken - but you can include the keyword "!STOPS" to prevent this from happening. Example: `onHeroAction: {"douse":"print \"You can't reach the torches to douse them.\"!STOPS"}` 

### Other Things With Properties

#### Monster Properties

* name - The monster's name. **Required**
* health, might, defense - the monster's stats. Might increases the power and accuracy of physical attacks. Defense reduces damage and increases chance to dodge. Health is how much damage the monster can take before death. **Required**
* isMiniboss - a flag indicating if the monster is a miniboss. Optional
* description - A custom description for the monster (i.e. perhaps describing their manner, position in the room, etc). Will be displayed *instead of* the monster's name. Optional
* behavior - can be used to define more interesting behaviors for a monster. Optional
* patrolRoute - can be used to define a patrol route for the monster (you will need to use TickTock or some system of events to actually trigger the patrolling). Optional. Properties:
	* patrolRoute - the IDs of the rooms that the monster visits in order. To have a monster return to the same room or stay in the same room, just use the ID multiple times: [1, 1, 1, 3, 1, 4] this monster would stay in Room 1 for 3 patrol commands, then move to 3, then back to 1, then to 4, before starting over and cycling back
	* loops - not yet implemented (concept to cover snaking back instead of looping. Can be implemented like this for now: [1, 2, 3, 4, 3, 2]
	* patrollerId - each monster that patrols needs a unique ID. This ID is then referenced when you run the patrol event: `patrol <id>`. **Note**: I highly recommend not trying to get smart with this (i.e. having monsters that patrol other monsters, having dynamic patrol IDs.

Triggers (all Optional)
* onDeath - when a monster dies
* onTakeDamage - when a monster takes damage (does trigger when monster dies from damage)
* onDisable - triggered when a monster is disabled (i.e. stunned)
* onDealDamage - when a monster successfully deals damage to the hero

#### Item Properties
 
* name - Item's name. **Required**. Note that some item names have special meaning that does not need to be added. For example just naming an item "Potion" is enough to make a drinkable healing potion that will restore 9 health.
* darklight - A special property for the Darklight Dungeon - item is only visible in complete darkness. Optional
* value - monetary value of this item. Optional (default 0)

Triggers (all Optional)
* onPickup - a trigger for an event to happen when the item is picked up (looted) by the player.
* onDrop - a trigger to happen when the item is dropped by the player

There's not a lot you can currently do with items. We'll work on this!


#### Obstacle Properties

* @class - defines the Class or Type of this obstacle. A full discussion of Java classes is way outside of the scope of this document, but the long and short is that this needs to be the "fully qualified class name" of a class that extends Obstacle. All such classes live in the package `paul.TextQuest.entities.obstacles` package. Example: "paul.TextQuest.entities.obstacles.Chasm". **Required**
* name - The name of the obstacle as displayed to the player. Can be simple ("Chasm") or descriptive ("Dark Deep Chasm of Scariness"), as you like. **Required**
* solution - A String representing the associated action that solves the obstacle. **Required**
* blocksLooting - Optional flag as to whether this obstacle should prevent the Hero from freely looting the room (default - false)
* displayIfCleared - Optional flag as to whether we should continue to include this obstacle in the room description once it's been cleared. For something like a smashed barrel, we probably don't want to keep displaying it. For a riddle, it's good to remind the player that this was the room where they solved it.
* blockedDirections - A List of directions that are blocked until this obstacle is cleared. Optional
* expAmount - the amount of experience the hero should receive when clearing the obstacle. Different types have different defaults, but the base default is 25 experience points.
* description - An optional detailed description of the obstacle that will be displayed *instead of* the name. Optional

Triggers (all Optional)
* onSmash - Some obstacles are "smashable". This trigger defines what happens when they're smashed.
* onAttempt - What should happen when the Hero **unsuccessfully** attempts the obstacle.
* onClear - What should happen when the Hero **successfully** clears the obstacle.

### List of Triggered Events

All possible events must be hard coded into the game. That means you're limited to the (currently very small) set of events I've programmed into the game. Keep in mind that the events are just one side of it. In other words, these are just "some things that can happen". **How** and why they happen is determined by the trigger.

Note: 

**Remote Triggers**: It's now possible to have events be triggered in other rooms. To do this you add an "@id" to the beginning of the event message. Adding "@1" would try to trigger the event in the room with Id 1. Note: it doesn't always make sense to do this. Use with caution.

**Multiple Events**: It's also possible to have one trigger activate multiple events. To do this you just separate the events with semi-colons: "<event a>;<event b>". Don't add any space after or before the semi-colon (in general don't add extra spaces).

Here are the current possible events:

#### Param Events

These events need an extra bit of information, often a number. For example if you want to giveExp - how much? If you want to spawn a monster, I need to know the name. The parameter is listed in parentheses here:

* print (text) - prints the given text to the normal text area
* debug (text) - prints the given text to the debug window
* tutorial (text) - prints the given text to the tutorial window

* createMonster (name) - currently the only monster that can be created is a skeleton. More flexibility to come!
* explode (damage amount) - Deprecated. Use takeDamage instead. The hero takes some damage from an explosion and a message is printed.
* giveExp (exp amount) - the hero gets experience
* bump (object) - Used to momentarily delay the hero from leaving the room.
* heal (healing amount) - heals the player
* teachSpell (spell name) - teaches the Hero the given spell if they don't already know it
* teleportHero (room name) - moves the Hero to the given room (if it exists). Use with caution I guess
* createItem (item name) - creates the given item and places it in the room. You will need to refer to an item in your itemLibrary for anything other than a blank item.
* swapChest (id of other room) - attempts to swap the chest from this room with the chest from the other room. If one of the rooms doesn't have a chest (it's null) the effect is just to move the existing chest around.
* removeItem (item name) - attempts to remove the given item from the room's list of items (won't find items in chest, hidden items, etc)
* removeItemFromHero (item name) - attempts to remove the given item from the Hero's backpack. Use with caution - you could remove essential quest items.
* changeRoomName (new name) - changes the display name of the room. Use with caution.
* removePassage (direction) - removes a passage from this room in the given direction (if any). Use with caution, player can become trapped.
* addFeature (feature name) - Adds a new Feature to the room with the given name
* removeFeature (feature name) - Removes **all** features with the given name.
* changeRoomDescription (new description) - modify the description of the room
* clearObstacle (obstacle name) - Clears **all** obstacles with the given name in the room.
* takeDamage (damage amount) - the Hero takes some damage. No message is printed. See takeTypedDamage, takeSourcedDamage, takeTypedSourcedDamage.
* disableHero (rounds) - stuns the Hero for a number of rounds. No behavior outside of combat. (Only use in combat).
* doTicks (number of ticks) - performs the given number of ticks. Use with caution. See "Tick Tock Goes the Clock" for info.
* doTocks (number of tocks) - performs the given number of tocks. Use with caution.
* removeMonster (monster name) - removes all monsters with the given name from the room.
* patrol (monster's patroller id) - moves the monster to the next room in its patrol route.
* randomPatrol (monster's patroller id) - moves the monster to a random place in its patrol route.

#### MultiParam Events

These events require multiple parameters.

* modStat - modifies the stats of the Hero. Currently WIP. First param is the stat to modify. Second is the way in which it's modified AND the amount. Modification can be absolute (i.e. set the stat to a given value) or relative (add or subtract a certain amount). 
* createHiddenItem - adds a hidden object to this room. First param is the item name (you will need to refer to your itemLibrary if you want the item to do anything interesting), second param the location where it is hidden.
* addTrigger - allows you to add a triggered event to this room. The first param is the trigger group (specialRoomActions, onLightingChange, onItemUse, onSpellCast, or onSearch - these are the only trigger groups that can be changed right now). The second param is the event that should be triggered, along with any parameters for that event.
* removeTrigger - allows you to remove a triggered event from the room. The first param is the trigger group (see addTrigger). The second param is the event to be removed.
* createPassage - allows you to add a connection to a new room. First param is the direction, the second param is the `id` of the room to connect. Use with caution, as you could trap the player if you're not careful.
* setDungeonVariable - allows you to modify the dungeon's variable map. First param is the name of the variable you want to set, second param is the value you want it to have.
* setDungeonValue - used interchangeably with setDungeonVariable. "Value" refers to integers (whole numbers) whereas "variable" refers to the string map.
* addToDungeonValue - allows you to modify existing values in the values map. First param is the name of the value to set, and the second is the amount to add. You can use negative numbers to subtract instead. `addToDungeonValue waterLevel -1` would decrease waterLevel by 1.
* setFeatureDescription - change the description of a given feature. First param is the name of the feature, second param is the new description.
* setFeatureStatus - change the status of a feature. First param feature name, second new status.
* moveMonster - allows you to transport a monster to a new room. First param is the name of the monster, second param is the ID of the new room.

Damaging Events:

If you want the hero to take some damage, you can always use the simple event `takeDamage` to do that, and print your own custom message ("A spike trap hits you for 10 damage"). However, for convenience, these methods print a quick message about the damage type (i.e. fire, acid, piercing, electrical, whatever) and/or the source (i.e. "a goblin", "a spike trap") depending on the method.

* takeTypedDamage - does damage and prints "You take <amount> <type> damage."
* takeSourcedDamage - does damage and prints "You take <amount> damage from <source>."
* takeTypedSourcedDamage - does damage and prints "You take <amount> <type> damage from <source>."

#### Void Events

These events just happen, and they don't need any extra information.

* douse - the room becomes completely dark
* light - the room becomes fully bright
* makeMinibossStrong/makeMinibossWeak - created custom events for Darklight, these alter the stats of any miniboss in the room. (Look for this to be deprecated or generified eventually). 
* startFight - starts combat with any monsters in the room
* victory - Ends the game. Use with caution, I guess.
* crackFloor - creates a Chasm obstacle and prevents retreating. The hero will be stuck unless they have Boots of Vaulting
* removeChest - removes the chest from the current room (if applicable)
* setDungeonCleared - Sets the dungeon to cleared status, meaning the hero can leave and save (see saving).
* doTick - does one tick (see "Tick Tock Goes the Clock")
* doTock - does one tock
* removeMonsters - removes ALL monsters from the room

Hopefully soon we'll add many more possible events, and even the ability to create custom events.

### Future Triggers

Here are some triggers that I hope to add to the game soon:
Room:
* speechTriggers - similar to Riddles, triggered when a hero speaks a given phrase

Items:
* onUse - when an item is used

### Future Events

* modMonsterStats
* equip - force the hero to equip a given item
* unequip - force the hero to unequip an item they currently have equipped
* addObstacle
* castSpell - generate a spell-like effect
