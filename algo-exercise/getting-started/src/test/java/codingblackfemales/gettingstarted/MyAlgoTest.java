package codingblackfemales.gettingstarted;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import codingblackfemales.action.Action;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;
import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;

/**
 * This test is designed to check your algo behavior in isolation of the order book.
 *
 * You can tick in market data messages by creating new versions of createTick() (ex. createTick2, createTickMore etc..)
 *
 * You should then add behaviour to your algo to respond to that market data by creating or cancelling child orders.
 *
 * When you are comfortable you algo does what you expect, then you can move on to creating the MyAlgoBackTest.
 */
public class MyAlgoTest extends AbstractAlgoTest {

    private SimpleAlgoState mockState;
    private BidLevel mockNearTouch;
    private AskLevel mockFarTouch;

    @Override
    public AlgoLogic createAlgoLogic() {
        // This adds your algo logic to the container classes
        return new MyAlgoLogic(1);
    }

    
    @Before
    public void setUp() {
        // Initialize the mock state and levels before tests
        mockState = mock(SimpleAlgoState.class);  // Mock the state
        mockNearTouch = mock(BidLevel.class);     // Mock for near touch (best bid)
        mockFarTouch = mock(AskLevel.class);      // Mock for far touch (best ask)
        
        // Mock the best bid and ask levels
        when(mockState.getBidAt(0)).thenReturn(mockNearTouch);  // Mock bid at level 0
        when(mockState.getAskAt(0)).thenReturn(mockFarTouch);   // Mock ask at level 0
    }
   
    

     @Test
public void testCanPlaceBuyOrder_SpreadFavorable() {
     //Arrange: favorable conditions
   double tradeSpread = 3.0;  // Above the spread threshold
    double spreadThreshold = 1.5;
    double wideSpreadThreshold = 4;
    int maxChildOrder = 3;

    // Mocking child orders
    List<ChildOrder> childOrders = Mockito.mock(List.class);
    Mockito.when(childOrders.size()).thenReturn(2);  // Less than maxChildOrder
    Mockito.when(mockState.getActiveChildOrders()).thenReturn(childOrders);

    //Act: call the method
    SpreadStatus spreadStatus = Spread.getSpreadStatus(mockState, tradeSpread, spreadThreshold, wideSpreadThreshold);

        boolean canPlaceBuyOrder = (spreadStatus == SpreadStatus.FAVOURABLE) && 
                                   (childOrders.size() < maxChildOrder);

    // Assert: ensure that the result is true
    assertTrue("Expected the buy order condition to be true", canPlaceBuyOrder);
    System.out.println("Test result for testCanPlaceBuyOrder_SpreadFavorable: " + canPlaceBuyOrder);
}


@Test
public void testCanPlaceSellOrder_SpreadNegative() {
     //Arrange: favorable conditions
   double tradeSpread = -2.0;  // Negative spread
    double spreadThreshold = 1.5;
    double wideSpreadThreshold = 4;
    int maxChildOrder = 3;

    // Mocking child orders
    List<ChildOrder> childOrders = Mockito.mock(List.class);
    Mockito.when(childOrders.size()).thenReturn(2);  // Less than maxChildOrder
    Mockito.when(mockState.getActiveChildOrders()).thenReturn(childOrders);

    //Act: call the method
    SpreadStatus spreadStatus = Spread.getSpreadStatus(mockState, tradeSpread, spreadThreshold, wideSpreadThreshold);

        boolean canPlaceSellOrder = (spreadStatus == SpreadStatus.NEGATIVE) && 
                                   (childOrders.size() < maxChildOrder);

    // Assert: ensure that the result is true
    assertTrue("Sell order should be allowed when spread is negative and child orders are below limit", canPlaceSellOrder);
                                
    System.out.println("Test result for testCanPlaceSellOrder_SpreadNegative: " + canPlaceSellOrder);
}


  

@Test
public void testAlgorithmWaits_WhenSpreadUnfavourable() {
    //  unfavorable conditions
    double tradeSpread = 0.2;  // below the spread threshold
    double spreadThreshold = 1.5;
    double wideSpreadThreshold = 4.0;
    int maxChildOrder = 3;

    // Mocking child orders
    @SuppressWarnings("unchecked")
    List<ChildOrder> childOrders = Mockito.mock(List.class);
    Mockito.when(childOrders.size()).thenReturn(2);  // Less than maxChildOrder
    Mockito.when(mockState.getActiveChildOrders()).thenReturn(childOrders);

    SpreadStatus spreadStatus = Spread.getSpreadStatus(mockState, tradeSpread, spreadThreshold, wideSpreadThreshold);

    boolean algorithmWaits = (spreadStatus == SpreadStatus.UNFAVOURABLE) && 
                               (childOrders.size() < maxChildOrder);

    // Assert: ensure that the result is True
    assertTrue("Expected the wait condition to be true", algorithmWaits);
    System.out.println("Test result for testAlgorithmWaits_WhenSpreadUnfavourable: " + algorithmWaits);
}


@Test
public void testAlgorithmCancelsActiveOrder_WhenSpreadIsTooWide() {
    double tradeSpread = 5.0; 
    double spreadThreshold = 1.5;
    double wideSpreadThreshold = 4.0;  
    int maxChildOrder = 3;

    @SuppressWarnings("unchecked")
    List<ChildOrder> childOrders = Mockito.mock(List.class);
    Mockito.when(childOrders.size()).thenReturn(2);  
    Mockito.when(mockState.getActiveChildOrders()).thenReturn(childOrders);

    
    SpreadStatus spreadStatus = Spread.getSpreadStatus(mockState, tradeSpread, spreadThreshold, wideSpreadThreshold);
    boolean cancelsActiveOrder = (spreadStatus == SpreadStatus.WIDE) && 
                                (childOrders.size() < maxChildOrder);
    assertTrue("The algorithm should cancels oldest active order when spread is too wide", cancelsActiveOrder);
    System.out.println("Test result for testAlgorithmCancelsActiveOrder_WhenSpreadIsTooWide: " + cancelsActiveOrder);
}

  


    

    @Test
    public void testNoActiveOrder() {
       
        Mockito.when(mockState.getActiveChildOrders()).thenReturn(null);  
        Action cancelAction = OrderAction.cancelActiveOrder(mockState); 
        System.out.println("Cancel Action Result: " + cancelAction);
        assertEquals("Expected NoAction when there are no active orders", NoAction.NoAction, cancelAction);
    }


     @Test
     public void testTradeSpreadAndSpreadThreshold() {
         // MOCK PRICE VALUES
         long nearTouchPrice = 100;  
         long farTouchPrice = 102;   
         
         // Set mock price values
         when(mockNearTouch.getPrice()).thenReturn(nearTouchPrice); 
         when(mockFarTouch.getPrice()).thenReturn(farTouchPrice);    
 
         // Calculate tradeSpread and spreadThreshold
         double tradeSpread = farTouchPrice - nearTouchPrice;
         double spreadThreshold = 0.02 * nearTouchPrice;
         double wideSpreadThreshold = 0.05 * nearTouchPrice;
         System.out.println("Near Touch Price: " + nearTouchPrice);
         System.out.println("Far Touch Price: " + farTouchPrice);
         System.out.println("Trade Spread: " + tradeSpread);
         System.out.println("Spread Threshold: " + spreadThreshold);
         System.out.println("Wide Spread Threshold: " + wideSpreadThreshold);
         
    assertEquals("Trade spread should be 2.0", 2.0, tradeSpread, 0.001);
    assertEquals("Spread threshold should be 2.0", 2.0, spreadThreshold, 0.001);
    assertEquals("Wide spread threshold should be 5.0", 5.0, wideSpreadThreshold, 0.001);
     }
}




