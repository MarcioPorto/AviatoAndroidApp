package com.aviato.android.aviato.beacons;

/**
 * Created by Ruthie "Reverent Bayes" Berman and Lasse "R.A. Fisher" Vuursteen on 10/8/15.
 * Most important class. Used in combination with Machine Learning, Maximum Likelihood Estimation,
 * Neural Networks and more.
 */
public class WaitingTimeEstimator {

    /**
     *
     * @param time_until_flight How many minutes before the flight leaves
     * @param numPeople Number of people near beacon
     * @param numFlights Number of flights leaving in the next 2 hours
     * @return
     */
    public static double estimateWaitingTime(int time_until_flight, int numPeople, int numFlights, double mu, double mle_lambda) {
        double lambda = 1 - 0.03 * time_until_flight * mle_lambda;
        if (lambda < 0) { lambda = 0;}
        double w_c = 2 * mu * numPeople * lambda + 3;
        double w_f = 0.4 * numFlights * (1 - lambda);
        if (w_f > mu*numPeople) { w_f = 2* mu * numPeople + 0.08 * numFlights;}
        return w_c + w_f;
    }

}
