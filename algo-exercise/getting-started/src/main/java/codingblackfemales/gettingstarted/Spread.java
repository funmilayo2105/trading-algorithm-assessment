package codingblackfemales.gettingstarted;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;

public class Spread {
    private static final Logger logger = LoggerFactory.getLogger(Spread.class);
    private double spreadThreshold;

    // Constructor to initialize threshold
    public Spread(double spreadThreshold) {
        this.spreadThreshold = spreadThreshold;
    }

    // Calculate the spread (logic to determine spread)
    public double calculateSpread(SimpleAlgoState state) {
        BidLevel nearTouch = state.getBidAt(0); // Best bid
        AskLevel farTouch = state.getAskAt(0);  // Best ask

        if (nearTouch == null || farTouch == null) {
            logger.warn("[SPREAD] Bid or Ask levels are null, unable to calculate spread.");
            return Double.NaN;  // Return NaN if calculation is impossible
        }

        double spread = farTouch.price - nearTouch.price;
        logger.info("[SPREAD] Calculated spread: " + spread);
        return spread;
    }

    // Check if the spread is favorable
    public boolean isFavorable(SimpleAlgoState state, int maxChildOrder) {
        double calculatedSpread = calculateSpread(state); // Calculate the spread
        
        // Handle NaN case
        if (Double.isNaN(calculatedSpread)) {
            return false;
        }

        // Check if the spread is favorable based on the spread threshold and the number of child orders
        if (state.getActiveChildOrders() == null) {
            logger.info("[SPREAD] No active child orders.");
            return false; // No active child orders, return false
        }

        return calculatedSpread >= spreadThreshold && state.getChildOrders().size() < maxChildOrder;
        
      
    }

    // Check if the spread is unfavorable
    public boolean isUnfavorable(SimpleAlgoState state, int maxChildOrder) {
        double calculatedSpread = calculateSpread(state);
        
        // Handle NaN case
        if (Double.isNaN(calculatedSpread) || state.getActiveChildOrders() == null) {
            return false;
        }

        return calculatedSpread < 0 && state.getChildOrders().size() <= maxChildOrder;
        
        
       
    }

    // Check if the spread is tight
    public boolean isTight(SimpleAlgoState state, int maxChildOrder) {
        double spread = calculateSpread(state);

        // Handle NaN case
        if (Double.isNaN(spread) || state.getActiveChildOrders() == null) {
            return false;
        }

       return spread < spreadThreshold && state.getChildOrders().size() >= maxChildOrder;
        
    }
}
