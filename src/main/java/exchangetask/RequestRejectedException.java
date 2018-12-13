package exchangetask;

public class RequestRejectedException extends Exception {
    public RequestRejectedException() {
        super("This order is not working order!");
    }
}
