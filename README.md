# dd1349 - The train game
**students:** @vals @grundb

**programming language:** Java

## Project Description
The train game is a simple reaction-based game that draws heavy inspiration from the [trolley problem.](https://en.wikipedia.org/wiki/Trolley_problem) The player is forced to make split-second decisions with potentially lethal outcome for people having been tied to the tracks with a train approaching very quickly. The game is implemented in **java** using the **swing** graphics library from the standard java library. 

The project makes use of a n interactive subway map and simulator created mainly by the same students as part of a different course. The files from the master branch of [that repository](https://github.com/grundb/MDI-C4) were simply copied over  this one at 2018-04-06. The intent of this project is to modify said repository into a game. The game will make use of many classes, some of which were part of the initial subway map. 

We intend to use the following classes (both old and new ones) for the project: 

1. GUI (with subclasses) 
2. Game - current game status and game loop
3. Main class? (might not be necessary)
4. Field - a grid system for the game objects
5. Location
6. Station
7. Rail
8. Player
9. StationGraph - graph of all stations/destinations
10. Victim (for trolley problem, with subclasses)

## How-to-run
Currently,  you need to have a java runtime installed on your computer to play the game. Clone the repo to your hard drive, build the project and then run the Main.java class to launch the game. Alternatively, build/run the game inside an IDE of your choice. 

## Testing strategy

We are going to implement unit tests for the game logic. The graphics will be tested by observations (on various platforms).
> Written with [StackEdit](https://stackedit.io/).
Icons provided by wwww.icons8.com
