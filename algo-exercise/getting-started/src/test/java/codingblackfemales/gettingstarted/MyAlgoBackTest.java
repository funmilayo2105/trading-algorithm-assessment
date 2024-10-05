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
    public void testExampleBackTest() throws Exception {
        
        //create a sample market data tick....
        send(createTick());

        //ADD asserts when you have implemented your algo logic
        assertEquals(container.getState().getChildOrders().size(),maximumOrders);

        // Simulate spread widening by sending the custom tick
       // send(createWideningSpreadTick());
        //then: get the state
        //var state = container.getState();
        var state = container.getState();
         // Calculate filled quantity
    long filledQuantity = state.getChildOrders().stream()
    .map(ChildOrder::getFilledQuantity) // Extract filled quantities from child orders
        .reduce(Long::sum).get(); // Sum the filled quantities

        //System.out.println("Filled Quantity: " + filledQuantity);
    //System.out.println("Child Orders: " + state.getChildOrders());
    // Step 4: Check the filled quantity against an expected value
    assertEquals(55, filledQuantity);

  //  send(belowThresholdTick2());
    send (createNegativeSpreadTick());
    assertEquals(container.getState().getChildOrders().size(),maximumOrders);
    
  //  assertEquals(container.getState().getChildOrders().size(), 3);


    //TODO: CREATE TICKS THAT WOULD SIMULATE CHEAPER,ORDERBOOK to sell the 101 quantity.
    //check why getting stack overflow 
    
}

        

        //Check things like filled quantity, cancelled order count etc....
        //long filledQuantity = state.getChildOrders().stream().map(ChildOrder::getFilledQuantity).reduce(Long::sum).get();
        //and: check that our algo state was updated to reflect our fills when the market data
        //assertEquals(225, filledQuantity);
    }


