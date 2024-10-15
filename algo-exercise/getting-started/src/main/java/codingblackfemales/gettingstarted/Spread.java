package codingblackfemales.gettingstarted;

import codingblackfemales.sotw.SimpleAlgoState;


public class Spread {
   // private static final Logger logger = LoggerFactory.getLogger(Spread.class);
   // private double spreadThreshold;

    // Constructor to initialize threshold
    //public Spread(double spreadThreshold) {
      //  this.spreadThreshold = spreadThreshold;
   // }

    // Calculate the spread (logic to determine spread)
 //   public double calculateSpread(SimpleAlgoState state) {
      //  BidLevel nearTouch = state.getBidAt(0); // Best bid
       // AskLevel farTouch = state.getAskAt(0);  // Best ask

       // if (nearTouch == null || farTouch == null) {
         //   logger.warn("[SPREAD] Bid or Ask levels are null, unable to calculate spread.");
          //  return Double.NaN;  // Return NaN if calculation is impossible
       // }

       /*  double spread = farTouch.price - nearTouch.price;
        logger.info("[SPREAD] Calculated spread: " + spread);
        return spread;
    }*/

    //BUY
    public static boolean isFavorable(SimpleAlgoState state, double tradeSpread, double spreadThreshold, int maxChildOrder) {
        if (state.getActiveChildOrders() == null) {
            return false;}
        return tradeSpread >= spreadThreshold && state.getChildOrders().size() < maxChildOrder;}


       //WAIT FOR SPREAD, TAKE NO ACTION WHEN SPREAD IS <THRESHOLD
        public static boolean isUnfavorable(SimpleAlgoState state, double tradeSpread,double spreadThreshold, int maxChildOrder) 
        {return tradeSpread < spreadThreshold && state.getChildOrders().size() <= maxChildOrder; }


        //SPREAD WIDE
        public static boolean isWide (SimpleAlgoState state, double tradeSpread,double spreadThreshold, 
        double wideSpreadThreshold, int maxChildOrder){
            return tradeSpread>= wideSpreadThreshold ;
        }


   //SELL ORDER 
    public static boolean isNegative(SimpleAlgoState state, double tradeSpread, int maxChildOrder) {
        if (state.getActiveChildOrders() == null) {
            return false;
        }
        return tradeSpread < 0 && state.getChildOrders().size() < maxChildOrder; 
    
    }

    
    
    

}
