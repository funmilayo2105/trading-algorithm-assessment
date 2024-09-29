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

public class MyAlgoTest extends AbstractAlgoTest {

    private SimpleAlgoState mockState;
    private double spreadThreshold = 1.5;  
    private Spread spread;

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyAlgoLogic();
    }

    @Before
    public void setUp() {
        mockState = Mockito.mock(SimpleAlgoState.class);
        
    }

    @Test
    public void testDispatchThroughSequencer() throws Exception {
        send(createTick());
        assertEquals("Expected 3 child orders after the tick", 3, container.getState().getChildOrders().size());
    }

    @Test
    public void testCanPlaceBuyOrder_SpreadFavorable() {
        // Arrange: favorable conditions
        int maxChildOrder = 3;

        // Mocking the behavior of the state for active child orders
        List<ChildOrder> childOrders = Mockito.mock(List.class);
        Mockito.when(childOrders.size()).thenReturn(2);  // Less than maxChildOrder
        Mockito.when(mockState.getActiveChildOrders()).thenReturn(childOrders);
        Mockito.when(spread.calculateSpread(mockState)).thenReturn(2.0); // Set a favorable spread

        // Act: call the method
        boolean result = spread.isFavorable(mockState, maxChildOrder);

        // Assert: ensure that the result is true
        assertTrue("Expected the buy order condition to be true", result);
        System.out.println("Test result for testCanPlaceBuyOrder_SpreadFavorable: " + result);
    }

    @Test
    public void testCanPlaceBuyOrder_UnFavorableSpread() {
        // Arrange: unfavorable conditions
        int maxChildOrder = 3;

        // Mocking the behavior of the state for active child orders
        List<ChildOrder> childOrders = Mockito.mock(List.class);
        Mockito.when(childOrders.size()).thenReturn(2);  // Less than maxChildOrder
        Mockito.when(mockState.getActiveChildOrders()).thenReturn(childOrders);

        // Act: call the method
        boolean result = spread.isUnfavorable(mockState, maxChildOrder);

        // Assert: ensure that the result is false
        assertTrue("Expected the buy order condition to be false", !result);
        System.out.println("Test result for testCanPlaceBuyOrder_UnFavorableSpread: " + result);
    }

    @Test
    public void testCanPlaceSellOrder_UnfavorableSpread() {
        // Arrange: unfavorable conditions
        double tradeSpread = -0.5; 
        int maxChildOrder = 3;

        // Mocking the behavior of the state for active child orders
        List<ChildOrder> childOrders = Mockito.mock(List.class);
        Mockito.when(childOrders.size()).thenReturn(2);  // Less than maxChildOrder
        Mockito.when(mockState.getActiveChildOrders()).thenReturn(childOrders);
        Mockito.when(spread.calculateSpread(mockState)).thenReturn(tradeSpread);

        // Act: call the method
        boolean result = spread.isUnfavorable(mockState, maxChildOrder);

        // Assert: ensure that the result is true
        assertTrue("Expected the sell order condition to be true", result);
        System.out.println("Test result for testCanPlaceSellOrder_UnfavorableSpread: " + result);
    }

    @Test
    public void testNoActiveOrder() {
        // Arrange: simulate a state with no active child orders
        Mockito.when(mockState.getActiveChildOrders()).thenReturn(null);

        // Act: call the method that should attempt to cancel an order
        Action cancelAction = OrderAction.cancelActiveOrder(mockState);

        // Print the action result
        System.out.println("Cancel Action Result: " + cancelAction);

        // Assert: ensure that no cancellation occurs (returns NoAction or similar)
        assertEquals("Expected NoAction when there are no active orders", NoAction.NoAction, cancelAction);
    }
}
