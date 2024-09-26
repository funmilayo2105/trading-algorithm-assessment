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
    private static final int MaxChildOrder = 3;
    private static final double SpreadThreshold = 1.5;
    

    @Override
    public Action evaluate(SimpleAlgoState state) {
        if (state == null) {
            logger.error("[MYALGO] Received null state.");
            return NoAction.NoAction;
        }
    
        var orderBookAsString = Util.orderBookToString(state);
        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);
    
        BidLevel nearTouch = state.getBidAt(0);
        AskLevel farTouch = state.getAskAt(0);
    
        if (nearTouch == null || farTouch == null) {
            logger.error("[MYALGO] Missing bid or ask level data.");
            return NoAction.NoAction;
        }
    
        double tradeSpread = (nearTouch.price - farTouch.price);
        logger.info("[MYALGO] The Trade spread: " + tradeSpread);
    
       
       
    

        if (OrderActionUtils.canPlaceBuyOrder(state, tradeSpread, SpreadThreshold, MaxChildOrder)) {
            // Place a buy order if the spread is favorable
            long askQuantity = farTouch.quantity;
            long askPrice = farTouch.price;
            return OrderActionUtils.createBuyOrder(Side.BUY, askQuantity, askPrice);
        
        } else if (OrderActionUtils.canPlaceSellOrder(state, tradeSpread, MaxChildOrder)) {
            // Place a sell order if the spread is unfavorable and we can place more orders
            long bidQuantity = nearTouch.quantity;
            long bidPrice = nearTouch.price;
            return OrderActionUtils.createSellOrder(Side.SELL, bidQuantity, bidPrice);
        
        } else if (OrderActionUtils.shouldCancelOrder(state, 2, 1.5, 3)) {
            // Cancel active orders if the conditions are met
            return OrderActionUtils.cancelActiveOrder(state);
        } else {
            logger.info("[MYALGO] No trading opportunity, taking no action.");
            return NoAction.NoAction;
        }
        
} }
    