package codingblackfemales.gettingstarted;

    
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codingblackfemales.action.Action;
import codingblackfemales.action.NoAction;
import codingblackfemales.sotw.marketdata.BidLevel;
import messages.order.Side;
    
    public class TradeRiskManager {
        
    
        private static final Logger logger = LoggerFactory.getLogger(TradeRiskManager.class);
    
        public static Action checkStopLossOrTakeProfit(BidLevel nearTouch, double stopLossPrice, double takeProfitPrice, boolean childOrdersSizeLessThanMax, double entryPrice) {
            // IF BEST BID BECOMES LESS THAN STOPLOSS PRICE, SELL AT BESTBID TO AVOID MORE LOSSES.
            if (nearTouch.price <= stopLossPrice && childOrdersSizeLessThanMax) {
                logger.info("[TRADE RISK] Stop-loss triggered. Selling at price: " + nearTouch.price);
                return OrderAction.createOrder(Side.SELL, nearTouch.quantity, nearTouch.price);
            }
    
            // IF BEST BID BECOMES HIGHER THAN MY TAKEPROFIT PRICE, SELL TO LOCK GAINS.
            if (nearTouch.price >= takeProfitPrice && childOrdersSizeLessThanMax) {
                double profit = (nearTouch.price - entryPrice) * nearTouch.quantity;

                logger.info("[TRADE RISK] Take profit triggered. Selling at price: " + nearTouch.price);
                logger.info("[TRADE RISK] Profit made:£ " + profit);
                return OrderAction.createOrder(Side.SELL, nearTouch.quantity, nearTouch.price);
            }
    
            // If neither condition is met, return NoAction.
            return NoAction.NoAction;
        }
    }
    


