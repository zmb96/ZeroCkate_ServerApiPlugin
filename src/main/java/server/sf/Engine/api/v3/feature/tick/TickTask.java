package server.sf.model.api.v3.feature.tick;

@FunctionalInterface
public interface TickTask {
    void tick(long sfTick);
}
