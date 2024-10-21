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

public class Algologic22 implements AlgoLogic {

    private static final Logger logger = LoggerFactory.getLogger(Algologic22.class);
    private final int maxChildOrder;
    private double spreadThreshold;
    private double wideSpreadThreshold;
    private double entryPrice; // The price at which you enter the trade (buy/sell)
    private double boughtPrice;
    private long marketPrice;
    private final Risk risk; // RiskAssessment instance
    private final SimpleAlgoState state; // Your existing SimpleAlgoState reference

    public Algologic22(Risk risk, SimpleAlgoState state, int maxOrder) {
        this.maxChildOrder = maxOrder; // Initialize maxChildOrder
        this.risk = risk; // Initialize RiskAssessment instance
        this.state = state; // Initialize the state
    }

    
    public Action evaluate() {
        if (state == null) {
            logger.error("[MYALGO] Received null state.");
            return NoAction.NoAction;
        }

        var orderBookAsString = Util.orderBookToString(state);
        logger.info("[MYALGO] The state of the order book is:\n" + orderBookAsString);

        if (risk.isPositionOpen()) { // Reference the risk instance
            double currentPrice = state.getBidAt(0).price; // Use best bid for stop-loss

            // Check stop-loss and take-profit conditions
            if (currentPrice <= risk.getStopLossLimit()) {
                logger.info("[PASSIVEALGO] Stop-loss triggered at price: " + currentPrice);
                return risk.triggerStopLoss(); // Use the risk instance
            } else if (currentPrice >= risk.getTakeProfitLimit()) {
                logger.info("[PASSIVEALGO] Take-profit triggered at price: " + currentPrice);
                return risk.triggerTakeProfit(); // Use the risk instance
            }
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
        wideSpreadThreshold = 0.05 * nearTouch.price;

        SpreadStatus spreadStatus = Spread.getSpreadStatus(state, tradeSpread, spreadThreshold, wideSpreadThreshold);
        boolean childOrdersSizeLessThanMax = state.getChildOrders().size() < maxChildOrder;

        // SELL WHEN SPREAD < 0
        if (spreadStatus == SpreadStatus.NEGATIVE && childOrdersSizeLessThanMax) {
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

                return OrderAction.createOrder(Side.SELL, bidQuantity, bidPrice);
            }

        // BUY WHEN FAVORABLE
        } else if (spreadStatus == SpreadStatus.FAVOURABLE && childOrdersSizeLessThanMax) {
            long askQuantity = farTouch.quantity;
            long askPrice = farTouch.price;

            this.entryPrice = askPrice;
            double stopLossPrice = entryPrice - (0.02 * entryPrice); // 2% below entry price
            double takeProfitPrice = entryPrice + (0.04 * entryPrice); // 4% above entry price

            logger.info(String.format("[MYALGO] Placing Buy Order of Price: %s, Stop Loss: %s, Take Profit: %s",
                    askPrice, stopLossPrice, takeProfitPrice));

            return OrderAction.createOrder(Side.BUY, askQuantity, askPrice);

        // WAIT WHEN UNFAVOURABLE
        } else if (spreadStatus == SpreadStatus.UNFAVOURABLE) {
            logger.info("[MYALGO] Spread is below Threshold. Waiting for favorable spread");
            return NoAction.NoAction;

        // CANCEL OLDEST CHILD ORDER WHEN WIDE
        } else if (spreadStatus == SpreadStatus.WIDE) {
            logger.warn("[MYALGO] Spread is too wide. Cancelling active order due to high volatility.");
            return OrderAction.cancelActiveOrder(state);
        }

        return NoAction.NoAction; // Default return action if no conditions are met
    }

    @Override
    public Action evaluate(SimpleAlgoState state) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'evaluate'");
    }
}
