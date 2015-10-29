### Analysis Document 
---
This will be used and modified for now. Can be copy and pasted into a .doc file later when homework is complete.

#### Requirements
--- 
- Describe the algorithm you implemented. Why does it make sense to play the game this way?
Is this the way a human would play, or is the strategy fundamentally different? Did you need
to use heuristics, if so what did you choose and why?
- Describe in detail how you tested your strategy. How did you detect issues with your AI’s
approach? Give me some specific examples of testing you did and what these tests helped
you learn about your approach to the game.
- Give me some data on how well your AI plays against some baselines. Maybe implement a
quick random AI and see how well your implementation does. Analyze your results and
discuss. If you tested against others, give me some data on how well your AI did. Where does
your code fail and/or succeed? Why is that the case?

#### Algorithm 
---
The basic algorithmic approach that we took was to manipulate the Markov Design Processes (MDP) to fit our purposes. MDP is a model that contains: 

1. Set of possible states, *S*.
2. Set of possible actions, *A*. 
3. A reward function *R(s,a)*.
4. A description *T* of each action's effects in each state.

Similarly in our algorithm, we used those 4 attributes, with the following definitions:

1. Set of possible states would be either *buy-able* or *un-buy-able*.
2. From the *buy-able* state, there would be 2 actions, able to claim more than one route and only able to claim one route.
From the *un-buy-able* state, there would be 3 actions, draw a new train card to be able to claim a route, draw a new train card but still be unable to claim a route, and draw two new destination cards.
3. The reward function is 

#### Testing
---


#### Data Analysis
---
