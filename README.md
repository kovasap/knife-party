# Draft Concept

## TODOs

 - Add drag/drop functionality to make characters move and attack each other.
 - Add money that characters can use to trade for items or new allies.
 - Make the map a lot bigger, and add an objective to it.

## The Map

The game takes place on a interconnected graph map (like the path of exile skill
tree).
Each node is a location that contains enemies, items to acquire, shops, or other
points of interest.
```
                                         +-------------------+          
                                         |                   |          
                     +------------+      |   Plains          |          
+-------------+      |  Plains    |      |   Highland        |          
| Foliage     |      |  Rocky     +------+   Space for 2     |          
|             +------+            |      +-------------------+          
|             |      +------------+---+                                 
+-----------+-+                       |                                 
            |                    +----+------+                          
            |                    |           |                          
            |                    | Cliff     +--+                       
            |                    |           |  |                       
            |                    +-+---------+  +----------------------+
     +------+-----------+          |            |                      |
     |                  +----------+            |   Swamp              |
     |  River           |                       |   Space for 2        |
     |                  |                       |                      |
     +------------------+                       +----------------------+
```

Characters should roam around the map in predictable patterns, making it feel
alive.

On the map there should be objectives that the player is racing to against
enemies trying to destroy them.

## Game Flow

At the start of the game, you choose a location for your character to start.
Perhaps you will have multiple characters that start at different locations.

Then the game proceeds, with all characters (player-controlled, friendly, enemy,
neutral) using abilities on a timeline based on a `:next-ready-time` stat
attached to each character.
Using abilities on a character's turn will increase this stat based on the
action, giving other characters a chance to go.

Each character has an inventory of items.
Items grant two main things to the character:

1. Traits, which can impact how that character interacts with locations, other
   characters, and anything else.
2. Abilities, which the character can use one of on their turn.

For example, the :mace item allows the character to do a melee attack, and also
grants the :anti-armor trait, which makes all attacks that character makes against
characters with the :armored trait stronger.

### Day/Night and Ability Charges

This section is still just an idea.

Abilities have charges that are only refreshed at the end of a day when a
character rests for the night.
Characters can skip resting and take actions instead if they have the charges.

### Trait Interactions

Based on a character's traits, the traits of the other characters sharing that
character's location, and the traits of the location itself, certain **effects**
can be applied to that character:

 - If a character has the :armored trait and an enemy at their location has
   :anti-armor, then that character will have the :vulnerable effect (take
   double damage from attacks).
 - 

### Player Interaction

If it the turn of a player character, the game will pause and let the player
make some changes to influence their character's behavior.
These changes may include:

 - Re-ordering items in a character's inventory to influence their action (boots
   first to flee, weapon first to fight).
 - Selecting locations that a character to move towards.
 - Picking up an item on the location they are currently at.
 - Trading items between themselves and another character on their location.

Once all the interactions are specified, the player will hit "next turn" to
advance the game.

## Worldbuilding and Character Design

*Experimental, may not even want to put this in this game in particular.*

All living beings are composed of:

 - Light from the **Sun**
 - **Earth**
 - **Water**
 - **Stone**

Every being has a primary, secondary, tertiary, and 4th-order balance of these
elements.

Different biomes tend to produce beings with similar balance.  For instance,

Biome | Primary
----- | -------
Island| Water
Mountain | Stone
Desert | Sun
Forest | Earth

The ordering of elements for a specific character will affect their stats and
personality (mirroring MBTI):

 - **Sun**: Extroverted, energetic, MTG red
 - **Earth**: Judging, MTG white
 - **Water**: Intuitive, MTG green?
 - **Stone**: Thinking, MTG blue


## Lessons Learned

I had the clever idea of "embedding" the world map with the characters
(replacing the ids in the map locations with the character data itself).
This made it so i had to pass less arguments around to functions that visualized
the map.
But then it also meant i needed two map data types to manage, one of which was
not really designed to be modified (the embedded map).

## Technical Stuff

### Setup

Follow https://github.com/binaryage/cljs-devtools/blob/master/docs/installation.md

First, install dependencies:

    # Linuxbrew and clojure
    /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
    brew install clojure/tools/clojure

    yarn

Then, start up all the servers with:

    ./run.zsh

You might need to open ports 3000, 5000, and 9630 to connect from another
machine.
On linux with UFW you can do this with:

```
sudo ufw allow 3000
sudo ufw allow 5000
sudo ufw allow 9630
```

### Deployment

#### As a static web page (frontend only)

Use a "release" script like at
https://github.com/kovasap/reddit-tree/blob/main/release.bash.

#### On Raspberry Pi

```
curl -sL https://deb.nodesource.com/setup_18.x | sudo bash -
sudo apt-get install nodejs
curl -sS https://dl.yarnpkg.com/debian/pubkey.gpg | sudo apt-key add -
sudo apt install yarn
curl -O https://download.clojure.org/install/linux-install-1.11.1.1273.sh
chmod +x linux-install-1.11.1.1273.sh
sudo ./linux-install-1.11.1.1273.sh
sudo cp app.service /etc/systemd/system/
systemctl enable app.service
systemctl start app.service
# To read logs:
journalctl -u app.service
```

Set up port forwarding on router to forward ports 3000, 5000, 9630 to the
raspberry pi's IP address.

Set up duckdns to point to the IP at https://www.whatismyip.com/.

Now anyone can access the game at kovas.duckdns.org:3000!

See info about setting up a static domain name at
https://gist.github.com/taichikuji/6f4183c0af1f4a29e345b60910666468.

#### Individual Server Startup

You can start the frontend service with:

    clj -M:frontend

You find the application at http://localhost:8700.

Start the backend API with this alias:

    clj -M:api

Find the backend server's documentation at: http://localhost:3000
