#version.md


###β

######β0.8.2 (5.3.15)
* Fixed many minor bugs

######β0.8.1 (5.1.15)
* Collision works cleanly without bugs.
* Added labels for other quantities
* Reworked save system (no third-party libraries! yay?)

######β0.8.0 (4.2.15)
* more block data available when you select a block
* you can now see(preview) the magnitude of the force you’re adding to the block
* use the arrow keys to actively add forces to a selected* block
* fixed gravity ENTIRELY.
* added friction
* fixed clipping of textboxes
* moving ‘camera’ that follows a block (type ‘c’ after you select* it)
	* ground is longer to encompass the bigger simulation area
	* you can add blocks and forces as you would normally even while following a block
* working on a new Block-Block collision mechanism
* cleanup
* and more changes that I cannot remember

*P.S. You can select a block by clicking on it. You’ll then see lots of text on top of that selected object*

######β0.7.1 (1.14.15)
* enabled gravity mode (temporarily)

######β0.7.0 (1.13.15)
* the program is now officially useful (as far as I can think, it could have been useful before)
	* because you can view some of the properties of a block!
	* more coming!
* many bug fixes and code cleanup.
* and more.

######β0.6.6 (1.12.15)
* fixed an annoying pause/play bug.

######β0.6.5 (1.11.15)
* Split the graphics loop and the animation loop for efficiency and speed
* Themes now also store acceleration arrow color data (other colors will be supported, coming in the near future)

######β0.6.4 (1.9.15)
* Added THEME support
* pressing shift while adding a force will add perpendicular forces -even while running the simulation
	
######β0.6.3 (1.8.15)
* Fixed a SILLY mistake that happened because I didn’t know physics properly. (about acceleration)
* Rewrote the inefficient graphics loop code (but in turn added a new class)
* made the block smaller
* Removed the ‘physics concepts’ pane.

***Check version.txt for the old version history (in Korean only)***