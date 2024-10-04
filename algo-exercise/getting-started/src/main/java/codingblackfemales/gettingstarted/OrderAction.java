package codingblackfemales.gettingstarted;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;
import messages.order.Side;

public class OrderAction {
    
   
    
    
 private static final Logger logger = LoggerFactory.getLogger(OrderAction.class);
private static double spreadThreshold;
 

  
        public static boolean shouldCancelOrder
        (SimpleAlgoState state, double tradeSpread, double spreadThreshold, int maxChildOrder) 
        {return tradeSpread < spreadThreshold && state.getChildOrders().size() >= maxChildOrder;
        }
    
        public static Action createBuyOrder(Side BUY, long quantity, long price) {
             logger.info("[MYALGO] Spread is favorable, Placing a " + BUY + " order with quantity: " + quantity + " and price: " + price);
            return new CreateChildOrder(Side.BUY, quantity, price);
        }
    
        public static Action createSellOrder(SimpleAlgoState state, long quantity, long price)
         {
             BidLevel farTouch = state.getBidAt(0);
        if (farTouch == null) {
            logger.error("[MYALGO] Unable to place SELL order, farTouch is null.");
            return NoAction.NoAction;
        }
        AskLevel nearTouch = state.getAskAt(0);
        // Calculate trade spread and set spread threshold
        double tradeSpread = nearTouch.price - farTouch.price;
        logger.info("[MYALGO] The Trade spread: " + tradeSpread);
        spreadThreshold = 0.02 * nearTouch.price;


        long bidQuantity = farTouch.quantity;
        long bidPrice = (long) (farTouch.price + (tradeSpread / 2)); // place a sell order slightly above the best ask
        logger.info("[MYALGO] Placing a SELL order with quantity: " + bidQuantity + " and price: " + bidPrice);
        return new CreateChildOrder(Side.SELL, bidQuantity, bidPrice);

            //logger.info("[MYALGO] Spread is unfavorable,  Placing a " + SELL + " order with quantity: " + quantity + " and price: " + price);
            //return new CreateChildOrder(Side.SELL, quantity, price);
        }
    
        public static Action cancelActiveOrder(SimpleAlgoState state) {
            if (state.getActiveChildOrders() != null && !state.getActiveChildOrders().isEmpty()) {
                logger.info("[MYALGO] Spread is negative, canceling an active child order...");
                return new CancelChildOrder(state.getActiveChildOrders().get(0));

                //logs an error if an active order is not present
               }   else
                 if 
                     (state.getActiveChildOrders() == null || state.getActiveChildOrders().isEmpty()) {
                        logger.error("[MYALGO] No active child orders to cancel.");
                  return NoAction.NoAction;
    
           }  
            
            return NoAction.NoAction;
        }
    }

 


