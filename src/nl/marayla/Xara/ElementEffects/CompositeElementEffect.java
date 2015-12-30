package nl.marayla.Xara.ElementEffects;

import java.util.ArrayList;

public class CompositeElementEffect implements ElementEffect {
    public final void add(final ElementEffect effect) {
        effects.add(effect);
    }

    public final void execute() {
        for (ElementEffect effect : effects) {
            effect.execute();
        }
    }
    private ArrayList<ElementEffect> effects = new ArrayList<>();
}