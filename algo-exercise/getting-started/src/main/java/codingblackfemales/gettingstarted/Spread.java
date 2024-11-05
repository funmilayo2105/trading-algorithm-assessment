package codingblackfemales.gettingstarted;

import codingblackfemales.sotw.SimpleAlgoState;

 enum SpreadStatus {
    UNFAVOURABLE,
    FAVOURABLE,
    WIDE,
    NEGATIVE,
    UNTRADABLE
}
 
public class Spread {

    public  static SpreadStatus getSpreadStatus(SimpleAlgoState state, double tradeSpread, double spreadThreshold, double wideSpreadThreshold){

     if(isFavorable(state, tradeSpread, spreadThreshold, wideSpreadThreshold))
        return SpreadStatus.FAVOURABLE;
     else if (isUnfavorable(tradeSpread, spreadThreshold))
        return SpreadStatus.UNFAVOURABLE;
     else if(isWide(tradeSpread, spreadThreshold))
        return SpreadStatus.WIDE;
        else if(isNegative(state, tradeSpread))
        return SpreadStatus.NEGATIVE;

     return SpreadStatus.UNTRADABLE;
  }
    
  //FAVORABLE SPREAD
  private static boolean isFavorable(SimpleAlgoState state, double tradeSpread, double spreadThreshold, double wideSpreadThreshold) {
        if (state.getActiveChildOrders() == null)
        {
            return false;
        }
        return (tradeSpread >= spreadThreshold) && (tradeSpread < wideSpreadThreshold);
    }

//UNFAVORABLE SPREAD;TOO TIGHT 
      private static boolean isUnfavorable(double tradeSpread,double spreadThreshold) 
        {
            return (tradeSpread < spreadThreshold) && (tradeSpread >=0);
        }

 //WIDE SPREAD
 
      private  static boolean isWide (double tradeSpread, double wideSpreadThreshold)
      { 
        return (tradeSpread>= wideSpreadThreshold);
      }


//SELL ORDER 
   private  static boolean isNegative(SimpleAlgoState state, double tradeSpread){
        if (state.getActiveChildOrders() == null) 
        {
            return false;
        }
        return tradeSpread < 0; 
    }

    
    
    

}
