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
    public MyAlgoLogic(Integer maxOrder){
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
    
        BidLevel nearTouch = state.getBidAt(0); // Highest buy price
        AskLevel farTouch = state.getAskAt(0); // Lowest sell price
    
        if (nearTouch == null || farTouch == null|| nearTouch.price <= 0 || farTouch.price <= 0) {
            logger.error("[MYALGO] Invalid or missing bid/ask data. ");
            return NoAction.NoAction;
        }
    
        double tradeSpread = (farTouch.price - nearTouch.price);
        logger.info("[MYALGO] The Trade spread: " + tradeSpread);
        spreadThreshold = 0.02 * nearTouch.price;
        wideSpreadThreshold = 0.05 * nearTouch.price;

        SpreadStatus spreadStatus = Spread.getSpreadStatus(state, tradeSpread, spreadThreshold, wideSpreadThreshold);
        boolean childOrdersSizeLessThanMax = state.getChildOrders().size() < maxChildOrder;
        double stopLossPrice = entryPrice - (0.04 * entryPrice); // 4% below entry price

        if (nearTouch.price <= stopLossPrice) {
            return OrderAction.createOrder(Side.SELL, nearTouch.quantity, nearTouch.price);

        }


        //WHEN SPREAD<0, SELL IF MARKET PRICE IS HIGHER THAN BOUGHT PRICE 
        if (spreadStatus == SpreadStatus.NEGATIVE && childOrdersSizeLessThanMax){
            marketPrice = nearTouch.price;
            boughtPrice = entryPrice;
    
            if (marketPrice > boughtPrice) {
                long bidQuantity = nearTouch.quantity;
                long currentbidPrice = nearTouch.price;

               
                this.entryPrice = currentbidPrice;
                double takeProfitPrice = entryPrice + (0.02 * entryPrice); // 2% above entry price
               
                logger.info(String.format("[MYALGO] Placing Sell Order of Price: %s,  Take Profit: %s", 
                        currentbidPrice, takeProfitPrice));

                        if (currentbidPrice >= takeProfitPrice) {
    
                return OrderAction.createOrder(Side.SELL, bidQuantity, currentbidPrice);
                        }
                        else {
                            return NoAction.NoAction;
                        }
            } 


            //BUY WHEN FAVORABLE
          if (spreadStatus == SpreadStatus.FAVOURABLE && childOrdersSizeLessThanMax) {
            long askQuantity = farTouch.quantity;
            long askPrice = farTouch.price;

           // this.entryPrice= askPrice; //updating entry price on Buy
    
            logger.info("[MYALGO]Spread is favorable. Placing Buy Order at market price" );
           // this.entryPrice=askPrice;
    
            return OrderAction.createOrder(Side.BUY, askQuantity, askPrice);

        /*  } if (nearTouch.price <= stopLossPrice) {
                logger.info("[MYALGO] Stop-loss triggered. Selling at price: " + nearTouch.price);
                return OrderAction.createOrder(Side.SELL, nearTouch.quantity, nearTouch.price);
            } */

            //WAIT WHEN UNFAVORABLE
        } 
    }if (spreadStatus == SpreadStatus.UNFAVOURABLE && childOrdersSizeLessThanMax) {
            logger.info("[MYALGO] Spread is below Threshold. Waiting for favorable spread");
            return NoAction.NoAction;
    
            //CANCEL OLDEST CHILD ORDER WHEN WIDE 
        }   if (spreadStatus == SpreadStatus.WIDE) {
            logger.warn("[MYALGO] Spread is too wide. Cancelling oldest active order.");
            return OrderAction.cancelActiveOrder(state);
        }
    
        return NoAction.NoAction;
    }
}