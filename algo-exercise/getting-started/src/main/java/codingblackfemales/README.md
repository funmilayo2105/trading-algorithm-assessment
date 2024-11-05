# Funmi's Trading ALGO

Welcome to **Funmi's Trading ALGO**! This project implements a trading algorithm designed to manage trading actions effectively. This algorithm makes trading decisions based on the calculated trade spread.

## Main Logic

The core logic is encapsulated in the `MyAlgoLogic.java` file. I've separated different concerns into their respective files for better organization:

- **OrderAction.java**: Contains the logic for order actions.
- **Spread.java**: Handles the determination of trade spreads.
- **TradeRiskManager.java**: Implements the logic for managing stop-loss and take-profit actions.
- **User.java**: Implements user management features to enhance security measures.

## Installation

Please follow the instructions provided in the project's README for installation details.

## Usage

To ensure the robustness of my trading algorithm, I have implemented both unit testing and backtesting:

- **Unit Testing**: Mock data has been created to test the various components of the algorithm. This allows for validating the logic in isolation and ensuring that each part functions as expected. 
  - Please open `MyAlgoTest.java` and run all tests at once by executing the test on line 30.

- **Backtesting**: Different market ticks have been simulated to evaluate how the algorithm handles various market conditions. This process helps to assess the performance and reliability of the algorithm before deploying it in a live trading environment.
  - Please open `MyAlgoBackTest.java` and run each test separately by executing the tests on lines 36, 63, 79, and 86.
  - Ticks for backtesting are created in `AbstractAlgoBackTest.java`. You can implement a tick here if you wish! :)


Thank you !