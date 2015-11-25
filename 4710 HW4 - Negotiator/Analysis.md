### Analysis Document 
---
This will be used and modified for now. Can be copy and pasted into a .doc file later when homework is complete. Player, agent and AI are used interchangeably in this file because assuming it is AI vs. AI, it is essentially the same as Player vs. Player and agent vs. agent.

#### Requirements
--- 
- Describe, in detail, the functionality of your agent and defend the motivations for your implementation.
- Present some quantifiable results of your agent negotiating with other agents. Why do you see the results you see? What does this tell you about the quality of your agents? How would you improve your implementation?
- Briefly discuss some conclusions you make given the data you’ve presented.

#### Algorithm 
---
Initially, there are only two approaches that can be taken. Either the AI is player 1 and it must present an offer first or the AI is player 2 and it receives an offer from player 1. The original acceptance rate, the threshold in which the AI will accept the offer, is set to 80% of the total utility. If the player is player 1, then the offer is constructed based on sorting the list of items by utility amount, from highest to lowest, and taking the least number of items to reach the acceptance rate. This is a greedy approach and will always ensure that the least number of items is used to reach the threshold, which we thought would give the highest chance of the opponent accepting because they are receiving more (comparatively) items and hopefully, more points. If the player is player 2 or if the player is evaluating an offer, the algorithm is simple. First, there is a dictionary that contains all the items in the set as key values and their pair as a list. The first index of the list is the number of times the other agent has asked for that item, starting at 1. The second index of the list is the perceived utility value of the other agent, which is total utility / sum of (total number of times all items in the set have been asked for) * number of times that specific item has been asked for (an example is shown below). The third index of the list is the utlity of that specific item for us, which will be used for sorting items that have the same perceived utility. 

For the rest of this description let us use the data set:
- 3
- Turkey, 1, 4 
- Gravy, 4, 1
- Stuffing, 3, 2
- Jam, 2, 3

Turn 0 will have a perceived utility of: 
{[Turkey, [1, 2.5, 1]], [Gravy, [1, 2.5, 4]], [Stuffing, [1, 2.5, 3]], [Jam, [1, 2.5, 2]]} w/ threshold of 0.8, 80%. 

Perceived Utility based on Turn#: 

1. If the other player demands Turkey, Stuffing, and Jam then: {[Turkey, [2, 2.85, 1]], [Gravy, [1, 1.43, 4]], [Stuffing, [2, 2.85, 3]], [Jam, [2, 2.85, 2]]} w/ threshold of 0.7, 70%.
2. If the other player demands Turkey and Jam then: {[Turkey, [3, 3.33, 1]], [Gravy, [1, 1.11, 4]], [Stuffing, [2, 2.22, 3]], [Jam, [3, 3.33, 2]]} w/ threshold of 0.6, 60%. 

Because the original acceptance rate is relatively high (80%), it makes sense for the percentage to go down as negotiation continues due to the extreme punishment of receiving negative points for not coming to an agreement. The final threshold is set to be 50%, so as the negotations continue, the threshold is decreasing at a constant rate, which is equal to (starting - 0.5) / # of turns. When an offer is received, the first thing that is done is the AI calculates the amount of utility that the it is being offered. If the amount of utility is greater than or equal to the threshold * total utility, then there is no more need for negoatiating and the offer can be accepted. So, for example, in turn 2 in the above example, the utility of gravy and stuffing is equal to 7 and that is greater than the 0.6 * 10 = 6, so negotiations would end there. However, if the utility offered is less than the threshold * total utility, such as in turn 1 where the received utility would be just 4, then the player calculates the total perceived utility of the other player and takes the items that the other player does not value as much. In an ideal situation, the saying "one man's trash is another man's treausre" would apply and both agents would be satisfied. However, due to the nature of the negotations, it is most likely that the ideal situation will not apply that often. So, our agent will sort the perceived utility dictionary based on perceived utility from lowest to highest and in cases where the perceived utilities are the same, the algorithm will sort based on our agent's values. So, for example, in turn 1, our agent will sort the dictionary as {[Gravy, [1, 1.43, 4], [Stuffing, [2, 2.85, 3]], [Jam, [3, 2.85, 2]], [Turkey, [3, 2.85, 1]]} and have a threshold of 0.8 - 0.1 = 0.7, 70% which in this case, is satisfied by Gravy and Stuffing. Thus, the return offer will be [Gravy, Stuffing]. The reasoning behind sorting the same perceived utility amounts by the amount of utility that we receive is because that way, our agent will always take the least number of items to reach the threshold, similar to the initial offer. By choosing to take less items, it is more likely that the other agent will be satisfied because they are receiving more items and hopefully, more points for them as well, similar to the above reasoning when sending the first offer. 

Since the AI knows which turn it is on and how many turns left, it is simple to take into consideration what should happen on the last turn. The consequence for not coming up with an agreement is extremely damaging to the agent's total number of points so it will do its best to come to an agreement. However, we also took into the consideration where if a player continually demands everything and we receive a utility of 0, then we should reject in those situations. Taking both of those into account, we decided to implement a feature where if no agreement has been reached by the end, there will be a 50% chance that the acceptance rate drops to 30%, thus giving the agent some leeway in terms of accepting an offer yet not being "bullied" too much. If the acceptance rate does not drop to 30%, then it will remain at 50%, which should be equal for both players. 

There is also a feature in which the AI will choose not to add an item to its offer because by adding another item will bring the utility to something larger than the threshold. There is a 50% chance that the AI will not add the item on because for example, if the threshold is 6 utility and by adding on another item will cause the utility received that turn to become 8, then it does not make too much sense and most likely, the other agent will refuse the offer anyways. Thus, we set it so that there is a chance the threshold is never hit when computing a return offer. This will, however, sometimes cause the AI to jump up and down when offering to its opponent. 

#### Testing
---
In class, we ran our AI against several other bots that other students had created and the results were as follows: 
- DecreasingIntelligent: 521
- DecreasingStubborn: 522
- jr3fs (Joo Ro): 526
- ConstantStubborn: 376
- ConstantReasonable: 410
- ConstantSoftie: 455
- Tricky: 379
- **jw6dz: 481**
- yn9sa: 533
- daniel/upal saha: 360
- LilBullystin: 378
- DA BEST ONE: 261

If you sorted the list by the number of points, our agent would have been in 5th place out of the 12 agents. We were not given explicit information on the test cases that the agents were run against but Joo (the person who ran the test suite and sent us the data), said there were over 50 test cases which is why the points are so high. Based on the names of some of the AIs, some inferences can be made. Surprisingly, our AI does not do as well as DecreasingIntelligent and DecreasingStubborn. Assuming the robots perform similarly as their name indicates, our AI may not perform as well on agents that do not converge to a 50%. Although this may seem counterintuitive, placing ourselves before others and not hitting a 50% threshold by the end may boost our points by a little bit. However, if the threshold is set too high, there is also the possibility of becoming like ConstantStubborn or ConstantReasonable. 

Another agent that we tested against was Daniel Saha + group's agent. The first sample set we used was sample_scenario.csv that was provided but changed the number of turns to 8. The second sample set we used was sample_scenario.csv that was provided but changed the number of turns to 5. The third sample set we used was a created set with the following data:
- 6
- SPKids, 4, 5
- Sleep, 3, 2
- GPA, 2, 4
- Coffee, 1, 2
- Break 8, 5

All sample sets were run three times overall with 4 for each time the set was run. So, the command was similar to `python negotiator_framework.py set1.csv set1.csv set1.csv set1.csv`. The results were as follows:

1. sample_scenario.csv (where player1 = our's and player2 = Daniel Saha's)
  - Round 1: 34, 26 (individual results include {9, 6}, {9, 6}, {9, 6}, {7, 8})
  - Round 2: 17, 18 (individual results include {6, 9}, {-5, -5}, {7, 8}, {9, 6})
  - Round 3: 32, 28 (individual results include {7, 8}, {9, 6}, {7, 8}, {9, 6})
2. sample_scenario2.csv (where player1 = Daniel Saha's and player2 = our's)
  - Round 1: 16, 36 (individual results include {4, 9}, {4, 9}, {4, 9}, {4, 9})
  - Round 2: 24, 29 (individual results include {4, 9}, {7, 7}, {6, 6}, {7, 7})
  - Round 3: 17, 32 (individual results include {4, 9}, {4, 9}, {5, 5}, {4, 9})
3. test1.csv (where player1 = our's and player2 = Daniel Saha's)
  - Round 1: 39, 29 (individual results include {13, 7}, {12, 8}, {7, 7}, {7, 7})
  - Round 2: 40, 35 (individual results include {8, 9}, {12, 8}, {9, 7}, {11, 11})
  - Round 3: 34, 39 (individual results include {10, 9}, {12, 8}, {12, 8}, {4, 13})

Overall, the results indicate that both of our agents are relatively equal although it does seem that our agent does negotiate a little bit better because only 2/9 of the rounds did our agent lose and they were not as drastic as the other's losses. Because we take into consideration what the opponent wants by keeping track of their demands, our agent is able to work around it and demand the things that are worth less to the other player. Originally, we though that by removing the drastic drop in threshold to 30% would improve our performance and in some circumstances it did. In the instances that our agent won, it would win by a larger percentage but if negotiations went wrong, then there would be significant point loss. The only situation that would allow for removing the 30% threshold to work would be in a situation where everyone else drops their threshold thus allowing us to take more points. 

Another action that we could have implemented is taking into consideration previous round's information such as the player's final utility or number of turns it took to reach a final conclusion. Our initial approach was to increase our threshold if an agreement was made or decrease our threshold if an agreement was never made. The thought process behind it was if an agreement was made, there may be a possibility we could get more if our threshold was slightly higher. However, if no agreement was made, then maybe our threshold was set too high so lower it and see if our agent can start receiving more utility (negative utility is always worse for us than accepting a less-than-ideal offer). 

#### Conclusions
---
Overall, our AI does relatively well when placed against other AIs. The algorithm we implemented uses perceived utility based on how many times the other player asks for a certain item. By using that information, our agent is able to work around their preferences and provide an offer that will be more likely agreeable. Furthermore, because the threshold is dropping at a constant rate to 50%, our AI is fair in that by the last turn, we would expect to receive 50% of the total utility. There are instances in which our AI will receive less than half but overall, that point difference is still a gain when compared to if no agreement is reached. However, there are still areas in which our agent can be stricter or more "bully"-like which may or may not result in more points as slightly mentioned above. 
