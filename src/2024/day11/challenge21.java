import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.String.format;

void main(String[] args) throws IOException {
    if (args.length != 1) {
        throw new InvalidParameterException("Please pass the path of the input file as argument.");
    }

    // Store information on configuration
    List<Long> stones = getData(args[0]);

    // Blink 25 times
    for (int blink = 1; blink <= 25; blink ++) {
        stones = blink(stones);
    }
    System.out.println(stones.size());
}

List<Long> getData(String filename) throws IOException {
    List<Long> stones = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
        String line;
        while ((line = reader.readLine()) != null) {
            for (String token: line.split(" ")) {
                if (token.isEmpty()) {
                    continue;
                }
                stones.add(parseLong(token));
            }
        }
    }

    return stones;
}

List<Long> blink(List<Long> stones) {
    List<Long> newStones = new ArrayList<>();

    for (long stone: stones) {
        if (stone == 0) {
            newStones.add(1L);
        } else if (String.valueOf(stone).length() % 2 == 0) {
            String stringStone = String.valueOf(stone);
            newStones.add(parseLong(stringStone.substring(0, stringStone.length()/2)));
            newStones.add(parseLong(stringStone.substring(stringStone.length()/2)));
        } else {
            newStones.add(stone * 2024);
        }
    }

    return newStones;
}
