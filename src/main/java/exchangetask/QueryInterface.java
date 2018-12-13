package exchangetask;

public interface QueryInterface {
    public int getTotalSizeAtPrice(int price); // Return sum of sizes of resting orders at <price> or zero
    public int getHighestBuyPrice(); // Return the highest price with at least one resting Buy order
    public int getLowestSellPrice(); // Return the lowest price with at least one resting Sell order
}
