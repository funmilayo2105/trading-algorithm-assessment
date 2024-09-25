import React from 'react';
import { MarketDepthRow } from './useMarketDepthData';

interface MarketDepthPanelProps {
  data: MarketDepthRow[];  // Expecting an array of MarketDepthRow
}

export const MarketDepthPanel: React.FC<MarketDepthPanelProps> = ({ data }) => {
  return (
    <div>
      {data.map((row, index) => (
        <div key={index}>
          <span>Level: {row.level}</span>
          <span>Bid: {row.bid} / Bid Qty: {row.bidQuantity}</span>
          <span>Offer: {row.offer} / Offer Qty: {row.offerQuantity}</span>
        </div>
      ))}
    </div>
  );
};
