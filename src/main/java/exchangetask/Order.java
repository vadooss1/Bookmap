package exchangetask;

public class Order {
    private long orderId;
    private boolean sideIsBuy;
    private int price;
    private int size;
    private boolean isWorking;

    public Order() {
        orderId = System.currentTimeMillis();
    }

    public Order(boolean sideIsBuy, int price, int size, boolean isWorking) {
        orderId = System.currentTimeMillis();
        this.sideIsBuy = sideIsBuy;
        this.price = price;
        this.size = size;
        this.isWorking = isWorking;
    }

    public long getId() {
        return orderId;
    }

    public void setId(long orderId) {
        this.orderId = orderId;
    }

    public boolean isSideIsBuy() {
        return sideIsBuy;
    }

    public void setSideIsBuy(boolean sideIsBuy) {
        this.sideIsBuy = sideIsBuy;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public void setWorking(boolean working) {
        isWorking = working;
    }
}
