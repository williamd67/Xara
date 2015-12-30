package nl.marayla.Xara.ElementEffects;

public final class NoEffect implements ElementEffect {
    public static final ElementEffect INSTANCE = new NoEffect();

    public void execute() {
        // Nothing do
    }

    private NoEffect() {
    }
}