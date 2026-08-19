package server.sf.model.api.v2.feature.tick;

@FunctionalInterface
public interface TickTask {
    void tick(long sfTick);
}
