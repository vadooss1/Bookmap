package exchangetask;

public interface AdvancedExchangeInterface extends ExchangeInterface {
    public boolean modify(long orderId, int newPrice, int newSize) throws RequestRejectedException;
}
