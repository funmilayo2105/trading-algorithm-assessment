package codingblackfemales.gettingstarted;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import codingblackfemales.action.Action;
import codingblackfemales.action.NoAction;
import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;
import codingblackfemales.sotw.SimpleAlgoState;

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

    @Override
    public AlgoLogic createAlgoLogic() {
        // This adds your algo logic to the container classes
        return new MyAlgoLogic();
    }

    @Before
    public void setUp() {
        mockState = Mockito.mock(SimpleAlgoState.class);  // Initialize the mock state before tests
    }

    @Test
    public void testDispatchThroughSequencer() throws Exception {
        // Create a sample market data tick
        send(createTick());

        // Simple assert to check we had 3 orders created
        assertEquals("Expected 3 child orders after the tick", 3, container.getState().getChildOrders().size());
    }

    @Test
public void testCanPlaceBuyOrder_SpreadFavorable() {
    // Arrange: favorable conditions
    double tradeSpread = 2.0;  // Above the spread threshold
    double spreadThreshold = 1.5;
    int maxChildOrder = 3;

    // Mocking child orders
    List<ChildOrder> childOrders = Mockito.mock(List.class);
    Mockito.when(childOrders.size()).thenReturn(2);  // Less than maxChildOrder
    Mockito.when(mockState.getActiveChildOrders()).thenReturn(childOrders);

    // Act: call the method
    boolean result = Spread.isFavorable(mockState, tradeSpread, spreadThreshold, maxChildOrder);

    // Assert: ensure that the result is true
    assertTrue("Expected the buy order condition to be true", result);
    System.out.println("Test result for testCanPlaceBuyOrder_SpreadFavorable: " + result);
}



     @Test
    public void testCanPlaceBuyOrder_SpreadNotFavorable() {
        // Arrange: unfavorable conditions
        double tradeSpread = 1.0;  // Below the spread threshold
        double spreadThreshold = 1.5;
        int maxChildOrder = 3;
    
        // Mocking child orders
        List<ChildOrder> childOrders = Mockito.mock(List.class);
        Mockito.when(childOrders.size()).thenReturn(2);  // Less than maxChildOrder
        Mockito.when(mockState.getActiveChildOrders()).thenReturn(childOrders);
    
        // Act: call the method
        boolean result = Spread.isUnfavorable(mockState, tradeSpread, maxChildOrder);
    
        // Assert: ensure that the result is false
        assertTrue("Expected the buy order condition to be false", !result);
        System.out.println("Test result for testCanPlaceBuyOrderWhenSpreadNotFavorable: " + result);
    }
    

    @Test
public void testCanPlaceSellOrder_UnfavorableSpread() {
    // Arrange: unfavorable conditions
    double tradeSpread = -0.5;  // Negative spread (unfavorable for buy)
    int maxChildOrder = 3;

    // Mocking child orders
    List<ChildOrder> childOrders = Mockito.mock(List.class);
    Mockito.when(childOrders.size()).thenReturn(2);  // Less than maxChildOrder
    Mockito.when(mockState.getActiveChildOrders()).thenReturn(childOrders);

    // Act: call the method (using isUnfavorable for selling condition)
    boolean result = Spread.isUnfavorable(mockState, tradeSpread, maxChildOrder);

    // Assert: ensure that the result is true
    assertTrue("Expected the sell order condition to be true", result);
    System.out.println("Test result for testCanPlaceSellOrder_UnfavorableSpread: " + result);
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



}
