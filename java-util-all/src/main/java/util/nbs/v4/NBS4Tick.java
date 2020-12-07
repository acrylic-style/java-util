package util.nbs.v4;

import org.jetbrains.annotations.NotNull;
import util.nbs.NBSNote;
import util.nbs.NBSTick;

import java.util.List;

public class NBS4Tick implements NBSTick {

    protected int startingTick;
    @NotNull
    protected List<NBSNote> layers;

    public NBS4Tick(int startingTick, @NotNull List<NBSNote> layers){
        this.startingTick = startingTick;
        this.layers = layers;
    }

    @Override
    public int getStartingTick() { return startingTick; }

    @NotNull
    @Override
    public List<NBSNote> getLayers() { return layers; }

    @Override
    public String toString() {
        return "NBSVersion4Tick{tick="+startingTick+", notes="+ layers+"}";
    }
}
