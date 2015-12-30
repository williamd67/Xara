package nl.marayla.Xara.Renderer.CellRenderer;

import nl.marayla.Xara.Field;
import org.jetbrains.annotations.Contract;

public abstract class RenderCellsYMajor extends RenderCells {
    @Override
    protected final int lineSize(final Field.ConstantSize size) {
        return size.getWidth();
    }

    @Contract(" -> !null")
    @Override
    protected final Field.ConstantPosition nextCell() {
        return new Field.Position(1, 0);
    }
}