import { Placeholder } from "../placeholder";
import { MarketDepthRow, useMarketDepthData } from "./useMarketDepthData";
import { schemas } from "../../data/algo-schemas";
import "./MarketDepthFeature.css";

// prettier-ignore
// const testData: MarketDepthRow[] = [
//   { symbolLevel:"1230", level: 0, bid: 1000, bidQuantity: 500, offer: 1010, offerQuantity: 700 },
//   { symbolLevel:"1231", level: 1, bid: 990, bidQuantity: 700, offer: 1012, offerQuantity: 400 },
//   { symbolLevel:"1232", level: 2, bid: 985, bidQuantity: 1200, offer: 1013, offerQuantity: 800 },
//   { symbolLevel:"1233", level: 3, bid: 984, bidQuantity: 1300, offer: 1018, offerQuantity: 750 },
//   { symbolLevel:"1234", level: 4, bid: 970, bidQuantity: 800, offer: 1021, offerQuantity: 900 },
//   { symbolLevel:"1235", level: 5, bid: 969, bidQuantity: 700, offer: 1026, offerQuantity: 1500 },
//   { symbolLevel:"1236", level: 6, bid: 950, bidQuantity: 750, offer: 1027, offerQuantity: 1500 },
//   { symbolLevel:"1237", level: 7, bid: 945, bidQuantity: 900, offer: 1029, offerQuantity: 2000 },
//   { symbolLevel:"1238", level: 8, bid: 943, bidQuantity: 500, offer: 1031, offerQuantity: 500 },
//   { symbolLevel:"1239", level: 9, bid: 940, bidQuantity: 200, offer: 1024, offerQuantity: 800 },
// ];

/**
 * TODO
 */
export const MarketDepthFeature = () => {
  //fetching market depth data
   const data = useMarketDepthData(schemas.prices);
  
   if (!data) {
    return <Placeholder />};

 // Render the table of market depth data
 return (
  <div className="market-depth-feature">
    <h2>Market Depth</h2>
    <table className="market-depth-table">
      <thead>
        <tr>
          <th>Level</th>
          <th>Bid</th>
          <th>Bid Quantity</th>
          <th>Offer</th>
          <th>Offer Quantity</th>
        </tr>
      </thead>
      <tbody>
        {data.map((row: MarketDepthRow) => (
          <tr key={row.symbolLevel}>
            <td>{row.level}</td>
            <td>{row.bid}</td>
            <td>{row.bidQuantity}</td>
            <td>{row.offer}</td>
            <td>{row.offerQuantity}</td>
          </tr>
        ))}
      </tbody>
    </table>
  </div>
);
};