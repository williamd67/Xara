package nl.marayla.Xara.Renderer.CellRenderer;

import nl.marayla.Xara.Field;
import nl.marayla.Xara.GameElements.GameElement;
import nl.marayla.Xara.Levels.LevelGamePlay;
import nl.marayla.Xara.Renderer.RenderData;

// Cell rendering
public abstract class RenderCells {
    public final void render(
            final LevelGamePlay levelGamePlay,
            final GameElement[] cells,
            final Field.ConstantSize size,
            final int topLine,
            final RenderData renderData
    ) {
        Field.ConstantPosition cell = initialCell(size);
        Field.ConstantPosition nextCell = nextCell();
        Field.ConstantPosition endCell = endCell(size);
        Field.ConstantPosition nextLine = nextLine(size);

        int maxIndex = cells.length;
        int firstIndex = (topLine + 1) * lineSize(size); // + 1 to skip hidden line
        if (firstIndex >= maxIndex) {
            firstIndex -= maxIndex;
        }
        int lastIndex = ((topLine - 1) * lineSize(size)) - 1; // -1 to skip hidden line
        if (lastIndex < 0) {
            lastIndex += maxIndex;
        }
        if (lastIndex > firstIndex) { // If this is the case then all cells are handled in one loop
            maxIndex = lastIndex;
            lastIndex = -1;
        }
        int index = firstIndex + 1; // skip first hidden cell
        while (index < maxIndex) {
            GameElement element = cells[index];
            if (element != null) {
                levelGamePlay.renderElement(element, renderData, cell);
            }
            index++;
            cell = new Field.Position(cell.getX() + nextCell.getX(), cell.getY() + nextCell.getY());
            if ((cell.getX() == endCell.getX()) || (cell.getY() == endCell.getY())) {
                index += 2; // skip two hidden cells
                cell = new Field.Position(cell.getX() + nextLine.getX(), cell.getY() + nextLine.getY());
            }
        }
        // TODO: remove duplication
        index = 1; // skip first hidden cell
        while (index < lastIndex) {
            GameElement element = cells[index];
            if (element != null) {
                levelGamePlay.renderElement(element, renderData, cell);
            }
            index++;
            cell = new Field.Position(cell.getX() + nextCell.getX(), cell.getY() + nextCell.getY());
            if ((cell.getX() == endCell.getX()) || (cell.getY() == endCell.getY())) {
                index += 2; // skip two hidden cells
                cell = new Field.Position(cell.getX() + nextLine.getX(), cell.getY() + nextLine.getY());
            }
        }
    }
    protected abstract int lineSize(Field.ConstantSize size);
    protected abstract Field.ConstantPosition initialCell(Field.ConstantSize size);
    protected abstract Field.ConstantPosition nextCell();
    protected abstract Field.ConstantPosition endCell(Field.ConstantSize size);
    protected abstract Field.ConstantPosition nextLine(Field.ConstantSize size);
}