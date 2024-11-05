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
    private final  int maxChildOrder;
    private double spreadThreshold;
    private double wideSpreadThreshold;
    private double entryPrice; // The price at which i enter the trade (by/sell)
    private double boughtPrice;
    private long marketPrice;
    private User user;

    public MyAlgoLogic(Integer maxOrder) {
        maxChildOrder=maxOrder;
    }

    @Override
    public Action evaluate(SimpleAlgoState state) {
        if (state == null) {
            logger.error("[MYALGO] Received null state.");
            return NoAction.NoAction;
    }

        var orderBookAsString = Util.orderBookToString(state);
        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        AskLevel farTouch = state.getAskAt(0); 
        BidLevel nearTouch = state.getBidAt(0);
        
        if (farTouch == null || nearTouch == null) { 
            logger.error("[MYALGO] Missing bid or ask level data.");
            return NoAction.NoAction;
        }
    
        double tradeSpread = (farTouch.price - nearTouch.price);
        logger.info("[MYALGO] The Trade spread: " + tradeSpread);
        spreadThreshold = 0.02 * nearTouch.price;//2% of best bid
        wideSpreadThreshold = 0.05 * nearTouch.price;

        SpreadStatus spreadStatus = Spread.getSpreadStatus(
            state, 
            tradeSpread, 
            spreadThreshold, 
            wideSpreadThreshold);

        boolean childOrdersSizeLessThanMax = state.getChildOrders().size() < maxChildOrder;
        
       double stopLossPrice = entryPrice - (0.04 * entryPrice);
       double takeProfitPrice = entryPrice + (0.04 * entryPrice); 
      
//1. WAIT WHEN UNFAVORABLE
    if (spreadStatus == SpreadStatus.UNFAVOURABLE && childOrdersSizeLessThanMax) {
    logger.info("[MYALGO] Spread is below Threshold. Waiting for favorable spread");
    return NoAction.NoAction;
}
        
//2. CANCEL OLDEST CHILD ORDER WHEN WIDE 
    if (spreadStatus == SpreadStatus.WIDE) {
            logger.warn("[MYALGO] Spread is too wide. Cancelling first active order");
            return OrderAction.cancelActiveOrder(state);
        }

//3. BUY WHEN FAVORABLE
    if (spreadStatus == SpreadStatus.FAVOURABLE && childOrdersSizeLessThanMax) {
        long askQuantity = farTouch.quantity;
        long currentaskPrice = farTouch.price;

        this.entryPrice = currentaskPrice;

        return OrderAction.createOrder(Side.BUY, askQuantity, currentaskPrice);
    } 

    //Implementing Risk management after Buying
    Action riskManagementAction = TradeRiskManager.checkStopLossOrTakeProfit(
        nearTouch, 
        stopLossPrice,
        takeProfitPrice,
        childOrdersSizeLessThanMax,
        takeProfitPrice);
    
    if (riskManagementAction != NoAction.NoAction) {
        return riskManagementAction; 
    }

//4. SELL BID>ASK-NEGATVE SPREAD 
    if (spreadStatus == SpreadStatus.NEGATIVE && childOrdersSizeLessThanMax) {
            marketPrice = nearTouch.price; //current bid price
            boughtPrice = entryPrice; //previous buy price
    
    if (marketPrice > boughtPrice) {
            long bidQuantity = nearTouch.quantity;
            long currentbidPrice = nearTouch.price;

            this.entryPrice = currentbidPrice;    

            return OrderAction.createOrder(Side.SELL, bidQuantity, currentbidPrice);
            } 
        }       
        return NoAction.NoAction;
    }
}