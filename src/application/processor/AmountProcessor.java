package application.processor;

public abstract class AmountProcessor extends OnCloseProcessor {
    AmountProcessor(OnCloseProcessor processor) {
        super(processor);
    }
}
