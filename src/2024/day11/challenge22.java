import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

//Map<Integer, Integer>

void main(String[] args) throws IOException {
    if (args.length != 1) {
        throw new InvalidParameterException("Please pass the path of the input file as argument.");
    }

    // Store information on configuration
    Map<Long, Long> stones = getData(args[0]);

    // Blink 25 times
//    int result = 0;
//    for (long stone: stones) {
//        result += blink(stone, 40);
//    }
//    System.out.println(result);
    for (int i = 0; i < 75; i ++) {
        stones = blink(stones);
    }
    long result = 0;
    for (long value: stones.values()) {
        result += value;
    }
    System.out.println(result);
}

Map<Long, Long> getData(String filename) throws IOException {
    Map<Long, Long> stones = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
        String line;
        while ((line = reader.readLine()) != null) {
            for (String token: line.split(" ")) {
                if (token.isEmpty()) {
                    continue;
                }
                stones.compute(parseLong(token), (_, v) -> (v == null) ? 1 : v + 1);
            }
        }
    }

    return stones;
}

Map<Long, Long> blink(Map<Long, Long> stones) {
    Map<Long, Long> newStones = new HashMap<>();
    for (Map.Entry<Long, Long> stone: stones.entrySet()) {
        if (stone.getKey() == 0) {
            storeInMap(newStones, 1, stone.getValue());
        } else if (String.valueOf(stone.getKey()).length() % 2 == 0) {
            String stringStone = String.valueOf(stone.getKey());
            storeInMap(newStones, parseLong(stringStone.substring(0, stringStone.length()/2)), stone.getValue());
            storeInMap(newStones, parseLong(stringStone.substring(stringStone.length()/2)), stone.getValue());
        } else {
            storeInMap(newStones, stone.getKey() * 2024, stone.getValue());
        }
    }
    return newStones;
}

void storeInMap(Map<Long, Long> stones, long digit, long count) {
    stones.compute(digit, (_, v) -> (v == null) ? count : v + count);
}