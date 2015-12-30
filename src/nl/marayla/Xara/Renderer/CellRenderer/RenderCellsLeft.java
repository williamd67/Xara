package nl.marayla.Xara.Renderer.CellRenderer;

import nl.marayla.Xara.Field;
import org.jetbrains.annotations.Contract;

public class RenderCellsLeft extends RenderCellsXMajor {
    @Contract("_ -> !null")
    @Override
    protected final Field.ConstantPosition initialCell(final Field.ConstantSize size) {
        return new Field.Position(0, size.getHeight() - 1 - 2);
    }

    @Contract("_ -> !null")
    @Override
    protected final Field.ConstantPosition endCell(final Field.ConstantSize size) {
        return new Field.Position(size.getWidth(), -1);
    }

    @Contract("_ -> !null")
    @Override
    protected final Field.ConstantPosition nextLine(final Field.ConstantSize size) {
        return new Field.Position(1, size.getHeight() - 2);
    }
}
