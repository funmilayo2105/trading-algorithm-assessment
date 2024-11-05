import "./MarketDepthFeature.css";
import { MarketDepthRow } from './useMarketDepthData';
import { Arrow } from "./Arrow";
import "./Arrow.css";
import { useEffect, useState } from "react";

interface MarketDepthPanelProps {
  data: MarketDepthRow[];  // Expecting an array of MarketDepthRow
}

export const MarketDepthPanel = ({ data }: MarketDepthPanelProps) => {
  // Calculate maximum quantities for bids and asks
  const maxBidQuantity = Math.max(...data.map(row => row.bidQuantity));
  const maxAskQuantity = Math.max(...data.map(row => row.offerQuantity));
  const [prevQuantities, setPrevQuantities] = useState<{ bid: number; ask: number }[]>([]);

  useEffect(() => {
    // Initialize previous quantities
    if (data.length > 0) {
      setPrevQuantities(data.map(row => ({ bid: row.bidQuantity, ask: row.offerQuantity })));
    }
  }, [data]); // Add `data` as a dependency so it updates when data changes

  return (
    <table>
      <thead>
        <tr>
          <th rowSpan={2}>Level</th>
          <th colSpan={2}>Bid</th>
          <th colSpan={2}>Ask</th>
        </tr>
        <tr>
          <th>Quantity</th>
          <th>Price</th>
          <th>Price</th>
          <th>Quantity</th>
        </tr>
      </thead>
      <tbody>
        {data.map((row, index) => {
          const prevBid = prevQuantities[index]?.bid;
          const prevAsk = prevQuantities[index]?.ask;

          return (
            <tr key={index}>
              <td>{row.level}</td>
              <td>
                <div
                  className="quantity-box bid-quantity"
                  style={{ width: `${(row.bidQuantity / maxBidQuantity) * 100}%` }}
                >
                  {row.bidQuantity}
                  <Arrow currentQuantity={row.bidQuantity} previousQuantity={prevBid || 0} />
                </div>
              </td>
              <td>{row.bid}</td>
              <td>{row.offer}</td>
              <td>
                <div
                  className="quantity-box ask-quantity"
                  style={{ width: `${(row.offerQuantity / maxAskQuantity) * 100}%` }}
                >
                  {row.offerQuantity}
                  <Arrow currentQuantity={row.offerQuantity} previousQuantity={prevAsk || 0} />
                </div>
              </td>
            </tr>
          );
        })}
      </tbody>
    </table>
  );
};
