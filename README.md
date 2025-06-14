## About
This fabric mod ports the [Gravity Changer (qouteall fork)](https://modrinth.com/mod/gravity-api-fork) based on [Gravity API](https://modrinth.com/mod/gravity-api) to higher **1.21** versions.

## Additions
Currently on **alpha**.  
Items are all for creative test only.  
Some of the testing items are not in the creative tab
- Gravity Changer (Stable)
- Gravity related Commands (Stable)
- Gravity Anchor (Untable)
- Gravity Plate (Unstable)
- Gravity Status Effect & Potions (Unstable)

## Commands

``
/gravity set_base_direction <direction> [entities]
``
 sets the base gravity direction. (The base direction can be overridden by other things including effects, gravity anchor and gravity plating). Without [entities] argument it will target the command sender (the same applies to all commands). Examples: /gravity set_base_direction up /gravity set_base_direction up @e[type=!minecraft:player]


``
/gravity set_base_strength <strength> [entities]
``
 sets the base gravity strength. The strength effects will multiply on the base strength (instead of overriding it). Examples: /gravity set_base_strength 0.5 /gravity set_base_strength 0.5 @e


``
/gravity view
``
 shows the base gravity direction and strength of the command sender.


``
/gravity reset [entities]
``
 reset the base gravity direction and strength.


``
/gravity randomize_base_direction [entities]
``
 sets the base direction as a random direction.


``
/gravity set_relative_base_direction <relativeDirection> [entities]
``
 sets the gravity direction as a direction relative to the entity's viewing direction. The <relativeDirection> can be forward, backward, left, right, up or down.


``
/gravity set_dimension_gravity_strength <strength>
``
 sets the dimensional gravity strength for the current dimension.


``
/gravity view_dimension_info
``
 shows the dimensional gravity strength for the current dimension.
