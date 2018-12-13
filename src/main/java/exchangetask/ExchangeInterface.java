package exchangetask;

public interface ExchangeInterface {
    public void send(long orderId);
    public boolean cancel(long orderId) throws RequestRejectedException;
}
