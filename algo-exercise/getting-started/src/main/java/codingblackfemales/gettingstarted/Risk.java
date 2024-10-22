package codingblackfemales.gettingstarted;

import java.util.List;
import java.util.stream.Collectors;

import codingblackfemales.action.Action;
import codingblackfemales.action.NoAction;
import codingblackfemales.service.MarketDataService;
import codingblackfemales.service.OrderService;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.OrderState;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;

public class Risk implements SimpleAlgoState {

    private final MarketDataService marketDataService;
    private final OrderService orderService;
    private final double stopLossLimit;
    private final double takeProfitLimit;

    public Risk(MarketDataService marketDataService, OrderService orderService, double stopLossLimit, double takeProfitLimit) {
        this.marketDataService = marketDataService;
        this.orderService = orderService;
        this.stopLossLimit = stopLossLimit;
        this.takeProfitLimit = takeProfitLimit;
    }

    public boolean isPositionOpen() {
        return getActiveChildOrders().size() > 0;
    }


    public double getStopLossLimit() {
        return stopLossLimit;
    }

    
    public Action triggerStopLoss() {
        double currentMarketPrice = marketDataService.getCurrentMarketPrice();
        if (isPositionOpen() && currentMarketPrice <= stopLossLimit) {
            orderService.closePosition();
            return OrderAction.closeOrder(this);
        }
        return NoAction.NoAction;
    }


    public double getTakeProfitLimit() {
        return takeProfitLimit;
    }

    
    public Action triggerTakeProfit() {
        double currentMarketPrice = marketDataService.getCurrentMarketPrice();
        if (isPositionOpen() && currentMarketPrice >= takeProfitLimit) {
            orderService.closePosition();
            return OrderAction.closeOrder(this);
        }
        return NoAction.NoAction;
    }
    

    @Override
    public int getBidLevels() {
        return marketDataService.getBidLength();  // Assuming this returns the number of bid levels
    }

    @Override
    public int getAskLevels() {
        return marketDataService.getAskLength();  // Assuming this returns the number of ask levels
    }

    @Override
    public BidLevel getBidAt(int index) {
        return marketDataService.getBidLevel(index);  // Fetch bid level at the given index
    }

    @Override
    public AskLevel getAskAt(int index) {
        return marketDataService.getAskLevel(index);  // Fetch ask level at the given index
    }

    @Override
    public List<ChildOrder> getChildOrders() {
        return orderService.children();  // Assuming this returns all child orders
    }

    @Override
    public List<ChildOrder> getActiveChildOrders() {
        return orderService.children().stream()
            .filter(order -> order.getState() != OrderState.CANCELLED)  // Filter out cancelled orders
            .collect(Collectors.toList());
    }

    @Override
    public long getInstrumentId() {
        return marketDataService.getInstrumentId();  // Fetch the instrument ID from MarketDataService
    }

    @Override
    public String getSymbol() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSymbol'");
    }
   
}