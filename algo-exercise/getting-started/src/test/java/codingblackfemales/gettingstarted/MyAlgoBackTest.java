package codingblackfemales.gettingstarted;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;

/**
 * This test plugs together all of the infrastructure, including the order book (which you can trade against)
 * and the market data feed.
 *
 * If your algo adds orders to the book, they will reflect in your market data coming back from the order book.
 *
 * If you cross the srpead (i.e. you BUY an order with a price which is == or > askPrice()) you will match, and receive
 * a fill back into your order from the order book (visible from the algo in the childOrders of the state object.
 *
 * If you cancel the order your child order will show the order status as cancelled in the childOrders of the state object.
 *
 */
public class MyAlgoBackTest extends AbstractAlgoBackTest {
    private final static int maximumOrders = 1;
    

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyAlgoLogic(maximumOrders);

    }

    @Test
    public void testPositiveSpreadTick() throws Exception {
        // Arrange: Set up a favorable market condition with positive spread
       
        send(positiveSpreadTick());

        // Act: Get the state after the tick
        var state = container.getState();

        // Assert: Verify that a buy order was placed
        assertEquals(container.getState().getChildOrders().size(),maximumOrders);

        
         // Calculate filled quantity
    long filledQuantity = state.getChildOrders().stream()
    .map(ChildOrder::getFilledQuantity) // Extract filled quantities from child orders
        .reduce(Long::sum).get(); // Sum the filled quantities

        System.out.println("Filled Quantity: " + filledQuantity);
    System.out.println("Child Orders: " + state.getChildOrders());
    // Step 4: Check the filled quantity against an expected value
   //assertEquals(55, filledQuantity);


    }

    @Test
    public void testNegativeSpreadTick() throws Exception {
       
        send(negativeSpreadTick());

     
        var state = container.getState();
        assertEquals("Expected child orders count to be 1", 1, state.getChildOrders().size());


        long filledQuantity = state.getChildOrders().stream()
        .map(ChildOrder::getFilledQuantity)
            .reduce(Long::sum).get(); 
    
        System.out.println("Filled Quantity: " + filledQuantity);
        System.out.println("Child Orders: " + state.getChildOrders());
        // Step 4: Check the filled quantity against an expected value
       //assertEquals(55, filledQuantity);
    
    }
    @Test
    public void testbelowThresholdTick() throws Exception {
       send(belowThresholdTick());
        var state = container.getState();
        assertEquals("Expected child orders count to be 0", 0, state.getChildOrders().size());
    }

    @Test
    public void testWideSpreadTick() throws Exception {
        send(wideSpreadTick());
        var state = container.getState();
        assertEquals("Expected child orders count to be 0", 0, state.getChildOrders().size());
    }
   
    


  
}


