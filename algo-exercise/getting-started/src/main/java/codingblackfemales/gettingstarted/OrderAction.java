package codingblackfemales.gettingstarted;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codingblackfemales.action.Action;
import codingblackfemales.action.CancelChildOrder;
import codingblackfemales.action.CreateChildOrder;
import codingblackfemales.action.NoAction;
import codingblackfemales.sotw.SimpleAlgoState;
import messages.order.Side;

public class OrderAction {
   
    
    
 private static final Logger logger = LoggerFactory.getLogger(OrderAction.class);
 

  
    
     

      /*   public static boolean canPlaceBuyOrder(SimpleAlgoState state, double tradeSpread, double spreadThreshold, int maxChildOrder) {
            if (state.getActiveChildOrders() == null) {
                return false;
            }
            return tradeSpread >= spreadThreshold && state.getChildOrders().size() < maxChildOrder;
        }
        
        public static boolean canPlaceSellOrder(SimpleAlgoState state, double tradeSpread, int maxChildOrder) {
            if (state.getActiveChildOrders() == null) {
                return false;
            }
            return tradeSpread < 0 && state.getChildOrders().size() <= maxChildOrder;
        } */

        public static boolean shouldCancelOrder
        (SimpleAlgoState state, double tradeSpread, double spreadThreshold, int maxChildOrder) 
        {return tradeSpread < spreadThreshold && state.getChildOrders().size() >= maxChildOrder;
        }
    
        public static Action createBuyOrder(Side BUY, long quantity, long price) {
             logger.info("[MYALGO] Spread is favorable, Placing a " + BUY + " order with quantity: " + quantity + " and price: " + price);
            return new CreateChildOrder(Side.BUY, quantity, price);
        }
    
        public static Action createSellOrder(Side SELL, long quantity, long price) {
            logger.info("[MYALGO] Spread is unfavorable,  Placing a " + SELL + " order with quantity: " + quantity + " and price: " + price);
            return new CreateChildOrder(Side.SELL, quantity, price);
        }
    
        public static Action cancelActiveOrder(SimpleAlgoState state) {
            if (state.getActiveChildOrders() != null && !state.getActiveChildOrders().isEmpty()) {
                logger.info("[MYALGO] Spread is tight, canceling an active child order...");
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

 
