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
//        System.out.println(result);
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
                starts.add(new Position(grid.size(), line.indexOf('0', index), 1));
                index = line.indexOf('0', index) + 1;
            }
            grid.add(line);
        }
    }

    return starts;
}

int explorePaths(List<String> grid, Set<Position> positions, int nextDigit, int rowBound, int colBound) {
//    System.out.print("nextDigit: " + nextDigit + "|: ");
//    for (Position position: positions) {
//        System.out.print(format("%s, ", position));
//    }
//    System.out.println();
    Set<Position> newPositions = new HashSet<>();
    for (Position position: positions) {
        int row = position.row;
        int col = position.col;
        char digit = Character.forDigit(nextDigit, 10);
        if (row + 1 < rowBound && grid.get(row + 1).charAt(col) == digit) {
            addPositionToSet(newPositions, new Position(row + 1, col, position.rating));
        }
        if (col + 1 < colBound && grid.get(row).charAt(col + 1) == digit) {
            addPositionToSet(newPositions, new Position(row, col + 1, position.rating));
        }
        if (row - 1 >= 0 && grid.get(row - 1).charAt(col) == digit) {
            addPositionToSet(newPositions, new Position(row - 1, col, position.rating));
        }
        if (col - 1 >= 0 && grid.get(row).charAt(col - 1) == digit) {
            addPositionToSet(newPositions, new Position(row, col - 1, position.rating));
        }
    }
    if (nextDigit != 9) {
        return explorePaths(grid, newPositions, nextDigit + 1, rowBound, colBound);
    }
    int result = 0;
    for (Position position: newPositions) {
        result += position.rating;
    }
    return result;
}

void addPositionToSet(Set<Position> positions, Position newPosition) {
    if (positions.contains(newPosition)) {
        for (Position position : positions) {
            if (position.equals(newPosition)) {
                position.rating += newPosition.rating;
            }
        }
    } else {
        positions.add(newPosition);
    }
}

static class Position {
    public int row;
    public int col;
    public int rating;

    public Position(int row, int col, int rating) {
        this.row = row;
        this.col = col;
        this.rating = rating;
    }

    @Override
    public String toString() {
        return format("%s, %s (rating: %s)", row, col, rating);
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