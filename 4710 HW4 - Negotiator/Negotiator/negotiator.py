from negotiator_base import BaseNegotiator
from random import random

import operator


# Example negotiator implementation, which randomly chooses to accept
# an offer or return with a randomized counteroffer.
# Important things to note: We always set self.offer to be equal to whatever
# we eventually pick as our offer. This is necessary for utility computation.
# Second, note that we ensure that we never accept an offer of "None".
class Negotiator(BaseNegotiator):

    # Override the make_offer method from BaseNegotiator to accept a given offer 20%
    # of the time, and return a random subset the rest of the time.
    def make_offer(self, offer):
        self.offer = offer
        if random() < 0.2 and offer is not None:
            # Very important - we save the offer we're going to return as self.offer
            print "I, RandomNego, agree that you can take " + str(self.offer)
            self.offer = BaseNegotiator.set_diff(self)
            print "I, RandomNego, will take: " + str(self.offer)
            return self.offer
        else:
            ordering = self.preferences
            ourOffer = []
            for item in ordering.keys():
                if random() < .5:
                    ourOffer = ourOffer + [item]
            self.offer = ourOffer
            return self.offer


# Put into separate file when submitting
# class jw6dz(BaseNegotiator):
#
#     # Override the make_offer method from BaseNegotiator to accept a given offer 20%
#     # of the time, and return a random subset the rest of the time.
#     desires_set_up = False
#     enemy_desires = {}
#     total_util = 0
#     acceptance_rate = 0.8
#     num_elements = 0
#     turn_number = 0
#     did_opponent_raise = False  # did the opponent demand more this turn than the last turn?
#     curr_opponent_util = None   # current opponent perceived utility
#     opponent_util_list = []     # list of opponent utilities based on their offers continuous from multiple negotiations
#
#     def make_offer(self, offer):
#         self.num_elements = len(self.preferences)
#
#         # if we're initiating the offer
#         if offer is None:
#             # initiate to 1's if not initiated
#             if len(self.enemy_desires) == 0:
#                 self.total_util = self.compute_total_util()
#                 for s in self.preferences:
#                     self.enemy_desires[s] = (1, self.total_util/self.num_elements)
#
#             self.offer = self.compute_offer()
#             print "THIS IS MY RETURN OFFER (when we initiate) turn:" + str(self.turn_number)
#             print self.offer
#             return self.offer
#
#         # initiate to 1's if not initiated
#         if len(self.enemy_desires) == 0:
#             self.total_util = self.compute_total_util()
#             for s in self.preferences:
#                 self.enemy_desires[s] = (1, self.total_util/self.num_elements)
#         # if already initiated, increment/change based on offer
#         else:
#             num_occurences = 0
#             for s in self.enemy_desires:
#                 if s in offer:
#                     self.enemy_desires[s] = (self.enemy_desires.get(s)[0] + 1, self.enemy_desires.get(s)[1])
#                 temp_tuple = self.enemy_desires.get(s)
#                 num_occurences += temp_tuple[0]
#             calc_point = float(self.total_util) / float(num_occurences)
#             for s in self.enemy_desires:
#                 self.enemy_desires[s] = (self.enemy_desires.get(s)[0], self.enemy_desires.get(s)[0] * calc_point)
#         print "ENEMY DESIRES"
#         print self.enemy_desires
#
#         # if it's the last turn, 50% (TBD) chance to reduce acceptance rate to 30%
#         if self.iter_limit == self.turn_number:
#             if random() < .5:
#                 self.acceptance_rate = .3
#
#         # calculate the goal utility we want to reach with this offer
#         goal_util = self.acceptance_rate * self.total_util
#         enemy_desires_copy = self.enemy_desires.copy()
#         lowest_enemy_desires = [] # list of tuples of (name, util)
#         while len(enemy_desires_copy) > 0:  # Complete lowest_enemy_desires list
#             lowest_util = float(1000)
#             lowest_name = ""
#             lowest_occu = 0
#             for k, v in enemy_desires_copy.iteritems():
#                 if float(v[1]) <= lowest_util:
#                     lowest_name = k
#                     lowest_util = float(v[1])
#                     lowest_occu = v[0]
#             enemy_desires_copy.pop(lowest_name)
#             lowest_enemy_desires.append((lowest_name, lowest_occu, lowest_util, self.preferences.get(lowest_name)))
#         print "LOWEST ENEMY DESIRES"
#         print lowest_enemy_desires
#         print "min utility we want this turn: " + str(goal_util)
#
#         # after completion, sort lowest_enemy_desires such that same util's are sorted by our preferences G->L
#         iter_count = 0
#         inside_count = 1
#         new_sorted_list = []
#         while iter_count < len(lowest_enemy_desires):
#             temp_list = []
#             initialized = False
#             while inside_count < len(lowest_enemy_desires):
#                 if not initialized:
#                     temp_list.append(lowest_enemy_desires[inside_count-1])
#                     initialized = True
#                 else:
#                     if lowest_enemy_desires[inside_count][2] != lowest_enemy_desires[inside_count-1][2]:
#                         inside_count += 1
#                         break
#                     else:
#                         temp_list.append(lowest_enemy_desires[inside_count])
#                         inside_count += 1
#
#             temp_list = sorted(temp_list, key=operator.itemgetter(3), reverse=True) # sort temp_list G->L before adding to new_sorted_list
#             for t in temp_list:
#                 new_sorted_list.append(t)
#             iter_count = inside_count
#         print "NEW SORTED LIST WITH 4-TUPLE"
#         print new_sorted_list
#
#         # update lowest_enemy_desires with finalized sorted list
#         lowest_enemy_desires = new_sorted_list
#
#         temp_return_offer = []
#         current_turn_util = 0
#         count = 0
#         while current_turn_util <= goal_util and count < len(lowest_enemy_desires):
#             # print self.preferences.get(lowest_enemy_desires[count][0])
#             # print lowest_enemy_desires[count][2]
#             current_turn_util += self.preferences.get(lowest_enemy_desires[count][0])
#             temp_return_offer.append(lowest_enemy_desires[count][0])
#             count += 1
#         print "THIS IS MY RETURN OFFER!!! for turn #" + str(self.turn_number)
#         print temp_return_offer
#
#         self.offer = temp_return_offer
#         self.acceptance_rate -= .1
#         self.turn_number += 1
#         return self.offer
#
#     def compute_offer(self):
#         self.turn_number += 1
#         tempList = self.preferences.copy()
#         sortedList = sorted(tempList.items(), key=operator.itemgetter(1))
#         sortedList.reverse()
#         returnList = []
#         goalVal = self.acceptance_rate * self.compute_total_util()
#         tempVal = 0
#         for s in sortedList:
#             if tempVal + s[1] <= int(goalVal+1):
#                 returnList.append(s[0])
#                 tempVal += s[1]
#         return returnList
#
#     # not currently used
#     def sort_desires(self):
#         tempList = self.enemy_desires
#         sortedList = sorted(tempList.items(), key=operator.itemgetter(1))
#         print "ENEMY DESIRES TURNED INTO A LIST WHY"
#         print sortedList
#         return sortedList
#
#     def update_desires(self, offer):
#         pass
#
#     def compute_total_util(self):
#         util = 0
#         for s in self.preferences:  # get total utility
#             util += self.preferences.get(s)
#         return util
#
#     # receive_utility(self : BaseNegotiator, utility : Float)
#         # Store the utility the other negotiator received from their last offer
#     def receive_utility(self, utility):
#         self.opponent_util_list.append(utility)
#         if self.curr_opponent_util is not None and utility > self.curr_opponent_util:
#             self.did_opponent_raise = True
#         elif self.curr_opponent_util is not None and utility <= self.curr_opponent_util:
#             self.did_opponent_raise = False
#
#         self.curr_opponent_util = utility
#
#     # receive_results(self : BaseNegotiator, results : (Boolean, Float, Float, count))
#         # Store the results of the last series of negotiation (points won, success, etc.)
#     def receive_results(self, results):
#         self.acceptance_rate = 0.8  # reset acceptance rate
#         self.turn_number = 0
#         # IDEAS:
#         # - if the opponent accepts the offer on the first turn (the initial offer), up the aceptance rate to 0.9 at the very least.