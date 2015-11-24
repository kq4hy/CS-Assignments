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


#### Conclusions
---
