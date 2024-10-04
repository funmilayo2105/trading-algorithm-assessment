package codingblackfemales.gettingstarted;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codingblackfemales.action.Action;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import codingblackfemales.util.Util;
import messages.order.Side;

public class MyAlgoLogic implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(MyAlgoLogic.class);
    private static final int maxChildOrder =3;
    private double spreadThreshold;
    private RiskManagement riskManagement;
    private double entryPrice; // The price at which you enter a trade

    @Override
    public Action evaluate(SimpleAlgoState state) {
        if (state == null) {
            logger.error("[MYALGO] Received null state.");
            return NoAction.NoAction;
        }

        var orderBookAsString = Util.orderBookToString(state);
        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        // Initialize risk management with parameters
        if (riskManagement == null) {
            this.riskManagement = new RiskManagement(10000, 500, 0.01, 0.02, 0.04);
        }

        BidLevel nearTouch = state.getBidAt(0); //highet buy price 
        AskLevel farTouch = state.getAskAt(0); //lowest sell price

    if (nearTouch == null || farTouch == null) {
           logger.error("[MYALGO] Missing bid or ask level data.");
         return NoAction.NoAction;
       }

        double tradeSpread = (farTouch.price - nearTouch.price);
        logger.info("[MYALGO] The Trade spread: " + tradeSpread);
        spreadThreshold = 0.02 * nearTouch.price;

        if (Spread.isFavorable(state, tradeSpread, spreadThreshold, maxChildOrder)) {
            // Place a buy order if the spread is favorable
            long askQuantity = 55;
           // long askPrice = (long) (nearTouch.price - tradeSpread / 2);// Buy slightly below the best bid
            long askPrice = farTouch.price;

            // Set the entry price for risk management
            this.entryPrice = askPrice;
            double stopLossPrice = riskManagement.calculateStopLossPrice(entryPrice);
            double takeProfitPrice = riskManagement.calculateTakeProfitPrice(entryPrice);

            logger.info(String.format("[MYALGO] Placing Buy Order: %s, Stop Loss: %s, Take Profit: %s", askPrice, stopLossPrice, takeProfitPrice));

            return OrderAction.createBuyOrder(Side.BUY, askQuantity, askPrice);
        
        } 
         else if (Spread.isUnfavorable(state, tradeSpread, spreadThreshold, maxChildOrder)) {
            // Place a sell order if the spread is unfavorable
            long bidQuantity = nearTouch.quantity;
            long bidPrice = nearTouch.price; // Sell slightly above the best ask

            // Set the entry price for risk management
            this.entryPrice = bidPrice;
            double stopLossPrice = riskManagement.calculateStopLossPrice(entryPrice);
            double takeProfitPrice = riskManagement.calculateTakeProfitPrice(entryPrice);

            logger.info(String.format("[MYALGO] Placing Sell Order: %s, Stop Loss: %s, Take Profit: %s", bidPrice, stopLossPrice, takeProfitPrice));
            return OrderAction.createSellOrder(state, bidQuantity, bidPrice);
        
        } else if (Spread.isNegative(state, tradeSpread, maxChildOrder)) {
            // Cancel active orders if we have <=3 orders
            return OrderAction.cancelActiveOrder(state);
        } else {
            logger.info("[MYALGO] No trading opportunity, taking no action.");
            return NoAction.NoAction;
        }
    }
}
