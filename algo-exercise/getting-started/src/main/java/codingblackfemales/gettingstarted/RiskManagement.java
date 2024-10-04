package codingblackfemales.gettingstarted;

public class RiskManagement {
    private double capital; // Total capital
    private double dailyLossLimit; // Daily loss limit
    private double totalLossForDay; // Track daily losses
    private double riskPerTrade; // Percentage risk per trade
    private double stopLossPercentage; // Stop-loss percentage
    private double takeProfitPercentage; // Take-profit percentage

    public RiskManagement(double capital, double dailyLossLimit, double riskPerTrade, 
                          double stopLossPercentage, double takeProfitPercentage) {
        this.capital = capital;
        this.dailyLossLimit = dailyLossLimit;
        this.riskPerTrade = riskPerTrade;
        this.stopLossPercentage = stopLossPercentage;
        this.takeProfitPercentage = takeProfitPercentage;
        this.totalLossForDay = 0; // Initialize daily loss tracker
    }

    // Method to calculate stop-loss price
    public double calculateStopLossPrice(double entryPrice) {
        return entryPrice * (1 - stopLossPercentage);
    }

    // Method to calculate take-profit price
    public double calculateTakeProfitPrice(double entryPrice) {
        return entryPrice * (1 + takeProfitPercentage);
    }

    // Method to update daily losses
    public void updateDailyLoss(double loss) {
        totalLossForDay += loss;
    }

    // Method to check if daily loss limit is reached
    public boolean isDailyLossLimitReached() {
        return totalLossForDay >= dailyLossLimit;
    }

    // Getters and setters (optional)
    public double getTotalLossForDay() {
        return totalLossForDay;
    }

    public void resetDailyLoss() {
        totalLossForDay = 0; // Reset daily losses
    }
}
