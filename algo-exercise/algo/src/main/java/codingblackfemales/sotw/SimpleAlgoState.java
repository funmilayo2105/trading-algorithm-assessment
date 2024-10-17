package codingblackfemales.sotw;

import java.util.List;

import codingblackfemales.sotw.marketdata.AskLevel;
import codingblackfemales.sotw.marketdata.BidLevel;

public interface SimpleAlgoState {

  

    public String getSymbol();

    public int getBidLevels();
    public int getAskLevels();

    public BidLevel getBidAt(int index);
    public AskLevel getAskAt(int index);

    public List<ChildOrder> getChildOrders();

    public List<ChildOrder> getActiveChildOrders();

    public long getInstrumentId();



    
    





}
