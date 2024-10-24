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

    //CLOSING POSITIONS
    public static Action closeOrder(SimpleAlgoState state) 
    {
      if (state.getActiveChildOrders() != null && !state.getActiveChildOrders().isEmpty()) {
          logger.info("[MYALGO] Closing the active order: " + state.getActiveChildOrders().get(0).getOrderId());
         
          return new CancelChildOrder(state.getActiveChildOrders().get(0)); 
      } else {
          logger.error("[MYALGO] No active orders to close.");
          return NoAction.NoAction;
      }
    }
    //CANCEL ORDER 
    public static boolean shouldCancelOrder(SimpleAlgoState state, double tradeSpread, double spreadThreshold, int maxChildOrder)
    {
      return tradeSpread < spreadThreshold && state.getChildOrders().size() >= maxChildOrder; 
    } 
       
    //CREATING SELL OR BUY ORDERS
    public static Action createOrder(Side side, long quantity, long price) 
    {
      if(side == Side.SELL)
      logger.info("[MYALGO] Negative spread detected, Placing a " + side + " order with quantity: " + quantity + " and price: " + price); 
        else
      logger.info("[MYALGO] Favourable spread detected, Placing a " + side + " order with quantity: " + quantity + " and price: " + price);       
        return new CreateChildOrder(side, quantity, price); 
          }

    // CANCELLING ORDERS 
    public static Action cancelActiveOrder(SimpleAlgoState state) { 
    
      if (state.getActiveChildOrders() != null && !state.getActiveChildOrders().isEmpty()) { 
      logger.info("[MYALGO] canceling an active child order..."); 
        return new CancelChildOrder(state.getActiveChildOrders().get(0)); 
      }  
      else if (state.getActiveChildOrders() == null || state.getActiveChildOrders().isEmpty()) { 
      logger.error("[MYALGO] No active child orders to cancel."); 
        return NoAction.NoAction;  
      }  
          return NoAction.NoAction; 
    
        } 
    
      } 
    
     
    