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

    private MyAlgoLogic algoLogic;
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
    public void testDispatchThroughSequencer() throws Exception {
        // Create a sample market data tick
        //send(createTick());

        var state = container.getState();

        send(createWideningTick2());

        // Simple assert to check we had 3 orders created
        assertEquals("Expected 1 child orders after the tick", 1, container.getState().getChildOrders().size());
    }
    

    @Test
public void testCanPlaceBuyOrder_SpreadFavorable() {
     //Arrange: favorable conditions
   double tradeSpread = 3.0;  // Above the spread threshold
    double spreadThreshold = 1.5;
    int maxChildOrder = 3;

    // Mocking child orders
    List<ChildOrder> childOrders = Mockito.mock(List.class);
    Mockito.when(childOrders.size()).thenReturn(2);  // Less than maxChildOrder
    Mockito.when(mockState.getActiveChildOrders()).thenReturn(childOrders);

    //Act: call the method
    boolean result = Spread.isFavorable(mockState, tradeSpread, spreadThreshold, maxChildOrder);

    // Assert: ensure that the result is true
    assertTrue("Expected the buy order condition to be true", result);
    System.out.println("Test result for testCanPlaceBuyOrder_SpreadFavorable: " + result);
}


@Test
public void testCannotPlaceBuyOrder_SpreadIsUnfavorable() {
    // Arrange: unfavorable conditions
    double tradeSpread = 0.5;  // below the spread threshold
    double spreadThreshold = 1.5;
    int maxChildOrder = 3;

    // Mocking child orders
    List<ChildOrder> childOrders = Mockito.mock(List.class);
    Mockito.when(childOrders.size()).thenReturn(2);  // Less than maxChildOrder
    Mockito.when(mockState.getActiveChildOrders()).thenReturn(childOrders);

    // Act: call the method
    boolean result = Spread.isUnfavorable(mockState, tradeSpread, spreadThreshold, maxChildOrder);

    // Assert: ensure that the result is True
    assertTrue("Expected the buy order condition to be true", result);
    System.out.println("Test result for testCannotPlaceBuyOrder_SpreadnotFavorable: " + result);
}
  

  

@Test
public void testCannotPlaceSellOrder_SpreadIsUnfavorable() {
    // Arrange: unfavorable conditions
    double tradeSpread = 0.2;  // below the spread threshold
    double spreadThreshold = 1.5;
    int maxChildOrder = 3;

    // Mocking child orders
    List<ChildOrder> childOrders = Mockito.mock(List.class);
    Mockito.when(childOrders.size()).thenReturn(2);  // Less than maxChildOrder
    Mockito.when(mockState.getActiveChildOrders()).thenReturn(childOrders);

    // Act: call the method
    boolean result = Spread.isUnfavorable(mockState, tradeSpread, spreadThreshold, maxChildOrder);

    // Assert: ensure that the result is True
    assertTrue("Expected the sell order condition to be true", result);
    System.out.println("Test result for testCannotPlaceSellOrder_SpreadnotFavorable: " + result);
}
  


    

    @Test
    public void testNoActiveOrder() {
        // Arrange: simulate a state with no active child orders
        Mockito.when(mockState.getActiveChildOrders()).thenReturn(null);  // No active orders
    
        // Act: call the method that should attempt to cancel an order
        Action cancelAction = OrderAction.cancelActiveOrder(mockState);  // Adjust to your logic in Spread
    
        // Print the action result
        System.out.println("Cancel Action Result: " + cancelAction);
    
        // Assert: ensure that no cancellation occurs (returns NoAction or similar)
        assertEquals("Expected NoAction when there are no active orders", NoAction.NoAction, cancelAction);
    }

     @Test
     public void testTradeSpreadAndSpreadThreshold() {
         // Arrange: Define price values
         long nearTouchPrice = 100;  // Example near touch price
         long farTouchPrice = 102;   // Example far touch price
         
         // Set mock price values
         when(mockNearTouch.getPrice()).thenReturn(nearTouchPrice);  // mock price for near touch
         when(mockFarTouch.getPrice()).thenReturn(farTouchPrice);    // mock price for far touch
 
         // Act: Calculate tradeSpread and spreadThreshold
         double tradeSpread = farTouchPrice - nearTouchPrice;
         double spreadThreshold = 0.02 * nearTouchPrice;
         System.out.println("Near Touch Price: " + nearTouchPrice);
         System.out.println("Far Touch Price: " + farTouchPrice);
         System.out.println("Trade Spread: " + tradeSpread);
         System.out.println("Spread Threshold: " + spreadThreshold);
 
         // Assert: Check if tradeSpread and spreadThreshold are calculated as expected
         assertEquals("Trade spread should be 2.0", 2.0, tradeSpread, 0.001);
         assertEquals("Spread threshold should be 2.0", 2.0, spreadThreshold, 0.001);
     }
}




