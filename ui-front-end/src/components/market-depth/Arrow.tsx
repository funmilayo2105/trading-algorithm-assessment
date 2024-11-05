
import React from 'react';

interface ArrowProps {
  currentQuantity: number;
  previousQuantity: number;
}

export const Arrow: React.FC<ArrowProps> = ({ currentQuantity, previousQuantity }) => {
  let arrow = '';
  let color = '';

  if (previousQuantity !== undefined) {
    if (currentQuantity > previousQuantity) {
      arrow = '↑'; // Upward arrow
      color = 'black';
    } else if (currentQuantity < previousQuantity) {
      arrow = '↓'; // Downward arrow
      color = 'black'; 
    }
  }

  return <span style={{ color }}>{arrow}</span>;
};
