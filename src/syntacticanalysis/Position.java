package syntacticanalysis;

public class Position {
    private Long index;
    private Long row;
    private Long column;

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