# A-Star-Analysis-Slide-Puzzle
This is an Analysis of A-Star searching algorithms using different heuristics as they relate to finding an optimal solution to a slide puzzle. This program also includes an IDDFS search for optimal solutions that represents the need for better searching algorithms due to its slow search time and unrefined space usage.

The program expects a Puzzles.txt file to contain all unsolved puzzles. An example is:

3\
1 2 X\
6 3 8\
7 5 4\

Where the leading 3 tells the program it is a 3x3 puzzle, the following lines are the rows of values where 'X' represents the blank space.

I would highly recommend commenting out the IDDFS when running as it is very slow and will often run out of depth before it finds an optimal solution, if you use simple puzzle it will work fine (i.e. just 5 moves to solved).

I recommend https://jweilhammer.github.io/sliding-puzzle-solver/ to check the path solutions the program gives you (the direction representing where the blank space will go) and to generate other random puzzle to import your own!

An accompanying video is WIP.
