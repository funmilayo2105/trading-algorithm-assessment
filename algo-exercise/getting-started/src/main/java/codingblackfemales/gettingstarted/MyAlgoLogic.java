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
    private Spread spread;
    private User currentUser;
    private static final int maxChildOrder = 3;
    private static final double SpreadThreshold = 1.5;
    
    //The spread is the gap between the highest price someone wants to buy at 
    //and the lowest price someone is willing to sell

    
   // public MyAlgoLogic(User user) {
        //this.currentUser = user;
   // }
 
    


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
    
       

        if (Spread.isFavorable(state, tradeSpread, SpreadThreshold, maxChildOrder))  {
            // Place a buy order if the spread is favorable
           long askQuantity = nearTouch.quantity;
            long askPrice = nearTouch.price;
            return OrderAction.createBuyOrder(Side.BUY, askQuantity, askPrice);
        
        } 
        else if (Spread.isUnfavorable(state, tradeSpread, maxChildOrder)) {
            
            long bidQuantity = farTouch.quantity;
            long bidPrice = farTouch.price;
            return OrderAction.createSellOrder(Side.SELL, bidQuantity, bidPrice);
        
        } else if (Spread.isTight(state, tradeSpread, SpreadThreshold, maxChildOrder)) {
            // Cancel active orders if we have <=3 orders
            return OrderAction.cancelActiveOrder(state);
        } else {
            logger.info("[MYALGO] No trading opportunity, taking no action.");
            return NoAction.NoAction;
        }
        
     } }


    