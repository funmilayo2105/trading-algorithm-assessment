package codingblackfemales.gettingstarted;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
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
    
        long askQuantity = farTouch.quantity;
        long askPrice = farTouch.price;
    
        if (tradeSpread >= SpreadThreshold && state.getChildOrders().size() < MaxChildOrder) {
            logger.info("[MYALGO] Placing a buy order with quantity: " + askQuantity + " and price: " + askPrice);
            return new CreateChildOrder(Side.BUY, askQuantity, askPrice);
    
        } else if (tradeSpread < 0 && state.getChildOrders().size() < MaxChildOrder) {
            long bidQuantity = nearTouch.quantity;
            long bidPrice = nearTouch.price;
    
            logger.info("[MYALGO] Unfavorable spread, placing a sell order with quantity: " + bidQuantity + " and price: " + bidPrice);
            return new CreateChildOrder(Side.SELL, bidQuantity, bidPrice);
    
        } else if (tradeSpread < SpreadThreshold && state.getChildOrders().size() > MaxChildOrder) {
            if (state.getActiveChildOrders() != null && !state.getActiveChildOrders().isEmpty()) {
                logger.info("[MYALGO] Spread is tight, canceling an active child order...");
                return new CancelChildOrder(state.getActiveChildOrders().get(0));
            }
        } else {
            logger.info("[MYALGO] No trading opportunity, taking no action.");
        }
        return NoAction.NoAction;
    }}
    