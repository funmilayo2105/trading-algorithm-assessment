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
    private RiskManagement riskManagement;
    private double entryPrice; // The price at which you enter a trade
    private BidLevel currentBid;
    private double boughtPrice;
    private long marketPrice;
    private Object stopLossPrice;
    private Object takeProfitPrice;
    private double bidPrice;
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
    
        // Initialize risk management with parameters
        if (riskManagement == null) {
            this.riskManagement = new RiskManagement(10000, 500, 0.01, 0.02, 0.04);
        }
    
        BidLevel nearTouch = state.getBidAt(0); // Highest buy price
        AskLevel farTouch = state.getAskAt(0); // Lowest sell price
    
        if (nearTouch == null || farTouch == null) {
            logger.error("[MYALGO] Missing bid or ask level data.");
            return NoAction.NoAction;
        }
    
        double tradeSpread = (farTouch.price - nearTouch.price);
        logger.info("[MYALGO] The Trade spread: " + tradeSpread);
        spreadThreshold = 0.02 * nearTouch.price;
        double wideSpreadThreshold = 0.05 * nearTouch.price;
    
        // Handle negative spread scenario
        if (Spread.isNegative(state, tradeSpread, maxChildOrder)) {
            marketPrice = nearTouch.price;
            boughtPrice = entryPrice;
    
            if (marketPrice > boughtPrice) {
                long bidQuantity = nearTouch.quantity;
                long bidPrice = nearTouch.price;
    
                this.entryPrice = bidPrice;
                double stopLossPrice = entryPrice + (0.02 * entryPrice); // 2% above entry price
                double takeProfitPrice = entryPrice - (0.04 * entryPrice); // 4% below entry price
    
                logger.info(String.format("[MYALGO] Placing Sell Order of Price: %s, Stop Loss: %s, Take Profit: %s", 
                        bidPrice, stopLossPrice, takeProfitPrice));
    
                return OrderAction.createSellOrder(Side.SELL, bidQuantity, bidPrice);
            } 
        } else if (Spread.isFavorable(state, tradeSpread, spreadThreshold, maxChildOrder)) {
            long askQuantity = 55;
            long askPrice = farTouch.price;
    
            // Risk management measures
            this.entryPrice = askPrice;
            double stopLossPrice = entryPrice - (0.02 * entryPrice); // 2% below entry price
            double takeProfitPrice = entryPrice + (0.04 * entryPrice); // 4% above entry price
    
            logger.info(String.format("[MYALGO] Placing Buy Order of Price: %s, Stop Loss: %s, Take Profit: %s",
                    askPrice, stopLossPrice, takeProfitPrice));
    
            return OrderAction.createBuyOrder(Side.BUY, askQuantity, askPrice);
    
        } else if (Spread.isUnfavorable(state, tradeSpread, spreadThreshold, maxChildOrder)) {
            logger.info("[MYALGO] Spread is below Threshold. Waiting for favorable spread");
            return NoAction.NoAction;
    
        } else if (Spread.isWide(state, tradeSpread, spreadThreshold, wideSpreadThreshold, maxChildOrder)) {
            logger.warn("[MYALGO] Spread is too wide. Cancelling active order due to high volatility.");
            return OrderAction.cancelActiveOrder(state);
        }
    
        return NoAction.NoAction;
    }}
    