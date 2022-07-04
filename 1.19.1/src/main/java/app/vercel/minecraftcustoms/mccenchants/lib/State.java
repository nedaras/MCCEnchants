package app.vercel.minecraftcustoms.mccenchants.lib;

public class State<T> {

    private T state;

    public State(T defaultState) {
        state = defaultState;

    }

    public void setState(T state) {
        this.state = state;

    }

    public T getState() {
        return state;

    }

}
