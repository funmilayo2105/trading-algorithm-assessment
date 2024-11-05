package codingblackfemales.gettingstarted;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import codingblackfemales.algo.AlgoLogic;
import codingblackfemales.sotw.ChildOrder;


public class MyAlgoBackTest extends AbstractAlgoBackTest {
    private final static int maximumOrders = 1;

     /* Create roles for the user
     List<String> roles = Arrays.asList("TRADE", "SELL", "PLACE_LARGE_ORDERS");
     User user = new User("Funmilayo", roles); */

    

    @Override
    public AlgoLogic createAlgoLogic() {
        return new MyAlgoLogic(maximumOrders);

    }

    @Test
    public void testFavourableSpreadTick() throws Exception {
        // Arrange: Set up a favorable market condition with positive spread
       
        send(FavourableSpreadTick());

        // Act: Get the state after the tick
        var state = container.getState();

        // Assert: Verify that a buy order was placed
        assertEquals(container.getState().getChildOrders().size(),maximumOrders);
       // var state = container.getState();
        assertEquals("Expected child orders count to be 1", 1, state.getChildOrders().size());
        
         // Calculate filled quantity
    long filledQuantity = state.getChildOrders().stream()
    .map(ChildOrder::getFilledQuantity) // Extract filled quantities from child orders
        .reduce(Long::sum).get(); // Sum the filled quantities

        System.out.println("Filled Quantity: " + filledQuantity);
    System.out.println("Child Orders: " + state.getChildOrders());
    // Step 4: Check the filled quantity against an expected value
   assertEquals(101, filledQuantity);


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
       
    
    }

    @Test
    public void testUnfavorableSpreadTick() throws Exception {
       send(unFavourableSpreadTick());
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


