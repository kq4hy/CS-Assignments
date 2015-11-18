__author__ = 'lifereborn'

from negotiator_base import BaseNegotiator
from random import random

class jw6dz(BaseNegotiator):

    # Override the make_offer method from BaseNegotiator to accept a given offer 20%
    # of the time, and return a random subset the rest of the time.
    def make_offer(self, offer):
        print (self.preferences)
        print (self.iter_limit)
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
