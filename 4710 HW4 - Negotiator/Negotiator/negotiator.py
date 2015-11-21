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
            print "I agree that you can take " + str(self.offer)
            self.offer = BaseNegotiator.set_diff(self)
            print "I will take: " + str(self.offer)
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
class jw6dz(BaseNegotiator):

    # Override the make_offer method from BaseNegotiator to accept a given offer 20%
    # of the time, and return a random subset the rest of the time.
    desires_set_up = False
    enemy_desires = {}
    total_util = 0
    acceptance_rate = 0.8
    num_elements = 0
    turn_number = 0
    did_opponent_raise = False  # did the opponent demand more this turn than the last turn?
    curr_opponent_util = None   # current opponent perceived utility
    opponent_util_list = []     # list of opponent utilities based on their offers continuous from multiple negotiations

    def make_offer(self, offer):
        self.num_elements = len(self.preferences)

        # initiate to 1's if not initiated
        if len(self.enemy_desires) == 0:
            for s in self.preferences:
                self.total_util += self.preferences.get(s)
            for s in self.preferences:
                self.enemy_desires[s] = (1, self.total_util/self.num_elements)
        # if already initiated, increment/change based on offer
        else:
            num_occurences = 0
            for s in self.enemy_desires:
                if s in offer:
                    self.enemy_desires[s] = (self.enemy_desires.get(s)[0] + 1, self.enemy_desires.get(s)[1])
                temp_tuple = self.enemy_desires.get(s)
                num_occurences += temp_tuple[0]
            calc_point = float(self.total_util) / float(num_occurences)
            for s in self.enemy_desires:
                self.enemy_desires[s] = (self.enemy_desires.get(s)[0], self.enemy_desires.get(s)[0]*calc_point)
        # print self.enemy_desires



        self.offer = offer

        # if we're initiating the offer
        if offer is None:
            self.offer = self.compute_offer()
            return self.offer



        if random() < 0.2 and offer is not None:
            # Very important - we save the offer we're going to return as self.offer
            print "I, jw6dz, agree that you can take " + str(self.offer)  ### IMPORTANT remove computing ID later
            self.offer = BaseNegotiator.set_diff(self)
            print "I, jw6dz, will take: " + str(self.offer)
            return self.offer
        else:
            ordering = self.preferences
            ourOffer = []
            for item in ordering.keys():
                if random() < .5:
                    ourOffer = ourOffer + [item]
            self.offer = ourOffer
            return self.offer

    def compute_offer(self):
        tempList = self.preferences
        sortedList = sorted(tempList.items(), key=operator.itemgetter(1))
        sortedList.reverse()
        returnList = []
        goalVal = self.acceptance_rate * self.total_util;
        tempVal = 0
        for s in sortedList:
            if tempVal + s[1] <= int(goalVal+1):
                returnList.append(s[0])
                tempVal += s[1]
        return returnList

    def update_desires(self, offer):
        pass

    # receive_utility(self : BaseNegotiator, utility : Float)
        # Store the utility the other negotiator received from their last offer
    def receive_utility(self, utility):
        self.opponent_util_list.append(utility)
        if self.curr_opponent_util is not None and utility > self.curr_opponent_util:
            self.did_opponent_raise = True
        elif self.curr_opponent_util is not None and utility <= self.curr_opponent_util:
            self.did_opponent_raise = False

        self.curr_opponent_util = utility

    # receive_results(self : BaseNegotiator, results : (Boolean, Float, Float, count))
        # Store the results of the last series of negotiation (points won, success, etc.)
    def receive_results(self, results):
        pass