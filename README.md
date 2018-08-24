## TextQuest Project

Hello and welcome! I started this project because I have an interest in 
[Natural Language Processing](https://en.wikipedia.org/wiki/Natural_language_processing) and wanted to learn more. I
came up with the idea of a "natural language" text dungeon (the user is "talking" to the dungeon master). Not a [new
concept](https://en.wikipedia.org/wiki/Interactive_fiction#Natural_language_processing) of course.

I started out by trying to query the Google Cloud Natural Language API, which was a definite learning experience. After
experimenting, I decided to try writing my own parser. It's still very much a work in progress.

The app is hosted at https://text-quest.herokuapp.com/ . It is a free Heroku instance so it may take a minute to
load. NOTE: the app is currently in a broken state as I've come back to it after a year away.

### Collaborators

I would be **very** interested in collaborating with another developer on this, particularly a front-end/graphic
designer (I am not a front-end guy as you can see). For more information, please see the heroku app.

Update: thanks to [Sean](https://github.com/SeanMcP) there have been substantial improvements to the front-end look and feel. Thanks Sean! Proving that in a few hours someone who knows what they're doing can make something that doesn't look like it's from the '90s. I am still open to further collaboration, if anyone wants to get involved. 

### Returning

I'm now (as of August 2018) returning to at least clean this project up and hopefully push a Version 0.1.0. Brushing the dust off!

### Git Workflow

I've done some reading about Git workflows recently, in particular GitFlow, which actually matches up closely with my initial approach. I'm going to formalize that here, even though I'm only working with myself at the moment (thanks Sean for the UI/front-end work previously).

In my development of TextQuest I'm going to use a GitFlow like system, but differing in a few ways:
* No release branches. The project isn't big and complicated enough to warrant them. When the develop branch is in a good status, it'll just get merged into master, and that's the new release.
* Minimal hotfix/bugfix branches. The project isn't important enough to warrant them.
* Feature branches should be regularly merged into develop, even if they aren't complete, as long as they don't break anything.
* Feature branches should be pushed daily to GitHub (not kept locally)