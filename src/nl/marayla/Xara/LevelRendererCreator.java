package nl.marayla.Xara;

@FunctionalInterface
public interface LevelRendererCreator {
    LevelRenderer create(FigureInfo figureInfo);
}