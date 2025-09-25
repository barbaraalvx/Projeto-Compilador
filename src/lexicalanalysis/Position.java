package lexicalanalysis;

public class Position {
    private final Long index;
    private final Long row;
    private final Long column;

    @Override
    public String toString() {
        return "{" +
                "index=" + index +
                ", row=" + row +
                ", column=" + column +
                '}';
    }

    public Position(long index, long row, long column) {
        this.index = index;
        this.row = row;
        this.column = column;
    }
}