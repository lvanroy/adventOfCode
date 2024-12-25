import static java.lang.Integer.parseInt;
import static java.lang.String.format;

void main(String[] args) throws IOException {
    if (args.length != 1) {
        throw new InvalidParameterException("Please pass the path of the input file as argument.");
    }

    // Track the specification of the environment
    List<String> grid = new ArrayList<>();

    // Store information on the grid
    Set<Position> startingPositions = getData(args[0], grid);

    int rowBound = grid.size();
    int colBound = grid.getFirst().length();
    int result = 0;
    for (Position position: startingPositions) {
        result += explorePaths(grid, Set.of(position), 1, rowBound, colBound);
    }
    System.out.println(result);
}

Set<Position> getData(String filename, List<String> grid) throws IOException {
    Set<Position> starts = new HashSet<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
        String line;
        while ((line = reader.readLine()) != null) {
            int index = 0;
            while (line.indexOf('0', index) != -1) {
                starts.add(new Position(grid.size(), line.indexOf('0', index)));
                index = line.indexOf('0', index) + 1;
            }
            grid.add(line);
        }
    }

    return starts;
}

int explorePaths(List<String> grid, Set<Position> positions, int nextDigit, int rowBound, int colBound) {
    Set<Position> newPositions = new HashSet<>();
    for (Position position: positions) {
        int row = position.row;
        int col = position.col;
        char digit = Character.forDigit(nextDigit, 10);
        if (row + 1 < rowBound && grid.get(row + 1).charAt(col) == digit) {
            newPositions.add(new Position(row + 1, col));
        }
        if (col + 1 < colBound && grid.get(row).charAt(col + 1) == digit) {
            newPositions.add(new Position(row, col + 1));
        }
        if (row - 1 >= 0 && grid.get(row - 1).charAt(col) == digit) {
            newPositions.add(new Position(row - 1, col));
        }
        if (col - 1 >= 0 && grid.get(row).charAt(col - 1) == digit) {
            newPositions.add(new Position(row, col - 1));
        }
    }
    if (nextDigit != 9) {
        return explorePaths(grid, newPositions, nextDigit + 1, rowBound, colBound);
    }
    return newPositions.size();
}

static class Position {
    public int row;
    public int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public String toString() {
        return format("%s, %s", row, col);
    }

    @Override
    public boolean equals(Object o) {
        Position pos = (Position) o;
        return row == pos.row && col == pos.col;
    }

    @Override
    public int hashCode() {
        return row + col;
    }
}