package codingblackfemales.gettingstarted;

public class  StopLossManager {
    private double stopLossPercentage;
    private double entryPrice;
    private long quantity;

    public StopLossManager(double stopLossPercentage, double entryPrice, long quantity) {
        this.stopLossPercentage = stopLossPercentage;
        this.entryPrice = entryPrice;
        this.quantity = quantity;
    }

    public double calculateStopLossPrice() {
        return entryPrice * (1 - stopLossPercentage);
    }

    public boolean isStopLossTriggered(double currentPrice) {
        return currentPrice <= calculateStopLossPrice();
    }

    // You can also implement methods for handling stop-loss orders
}

    

