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
    enemy_desires = {}
    total_util = 0
    acceptance_rate = 0.8
    num_elements = 0
    turn_number = 0

    def make_offer(self, offer):
        self.num_elements = len(self.preferences)

        # initiate to 1's if not initiated
        if len(self.enemy_desires) == 0:
            for s in self.preferences:
                self.total_util += self.preferences.get(s)
            for s in self.preferences:
                self.enemy_desires[s] = (1, self.total_util/self.num_elements)
            # print(self.enemy_desires)

        # print (self.preferences)
        # print (self.iter_limit)
        self.offer = offer

        # if we're initiating the offer

        if (offer is None):
            self.offer = self.compute_offer()
            return self.offer


        if random() < 0.2 and offer is not None:
            # Very important - we save the offer we're going to return as self.offer
            print "I, jw6dz, agree that you can take " + str(self.offer) ###IMPORTANT remove computing ID later
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
            # print s
            if tempVal + s[1] <= int(goalVal+1):
                returnList.append(s[0])
                tempVal += s[1]
        return returnList

