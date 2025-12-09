package CodeVsZombies;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest {

    @Test
    void testHumanKillPrevention() throws IOException {
        // step 1
        String input = """
                8000 4500
                4
                0 4000 2250
                1 4000 6750
                2 12000 2250
                3 12000 6750
                12
                0 4000 3375 4000 2975
                1 12000 3375 12000 2975
                2 4000 4500 4000 4100
                3 12000 4500 12000 4100
                4 4000 5625 4000 6025
                5 12000 5625 12000 6025
                6 6000 2250 5600 2250
                7 8000 2250 8000 2650
                8 10000 2250 10400 2250
                9 6000 6750 5600 6750
                10 8000 6750 8000 6350
                11 10000 6750 10400 6750""";
        Player.br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(input.getBytes())));

        Player.readInitialInput();
        assertEquals("4000 3375\r\n", Player.simulate());

        // Step 2
        input = """
                7000 4500
                4
                0 4000 2250
                1 4000 6750
                2 12000 2250
                3 12000 6750
                12
                0 4000 2975 4000 2575
                1 12000 2975 12000 2575
                2 4000 4100 4000 3700
                3 12000 4100 12000 3700
                4 4000 6025 4000 6425
                5 12000 6025 12000 6425
                6 5600 2250 5200 2250
                7 8000 2650 7809 3001
                8 10400 2250 10800 2250
                9 5600 6750 5200 6750
                10 8000 6350 7809 5998
                11 10400 6750 10800 6750""";
        Player.br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(input.getBytes())));

        Player.readInput();
        assertEquals("4000 2975\r\n", Player.simulate());
    }

    @Test
    void testHumanKillPrevention2() throws IOException {
        String input = """
                8000 0
                3
                0 0 4500
                1 15999 4500
                2 8000 7999
                23
                0 2000 1200 1792 1542
                1 3000 1800 2702 2067
                2 4000 2400 3645 2585
                3 5000 3000 5282 2717
                4 6000 3600 6194 3250
                5 9000 5400 8856 5773
                6 10000 6000 9717 6282
                7 11000 6600 10637 6769
                8 12000 7200 11607 7278
                9 13000 7800 13269 7503
                10 14000 8400 14182 8044
                11 14000 600 14182 955
                12 13000 1200 13269 1496
                13 12000 1800 11635 1635
                14 11000 2400 10687 2150
                15 10000 3000 9778 2667
                16 9000 3600 8892 3214
                17 6000 5400 6243 5717
                18 5000 6000 5332 6221
                19 4000 6600 4377 6732
                20 3000 7200 2702 6932
                21 2000 7800 1792 7457
                22 1000 8400 900 8012""";
        Player.br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(input.getBytes())));

        Player.readInitialInput();
        assertEquals("9000 5400\r\n", Player.simulate());

        input = """
                8445 4966
                3
                0 0 4500
                1 15999 4500
                2 8000 7999
                19
                0 961 2910 754 3252
                1 1511 3136 1214 3404
                2 2225 3329 1871 3515
                3 6733 2749 6977 3065
                5 8280 7265 8137 7638
                6 8585 7412 8302 7695
                7 9185 7445 8822 7614
                8 10035 7590 9642 7668
                9 14345 6319 14614 6023
                10 14910 6620 15092 6264
                11 14910 2375 15092 2730
                12 14345 2680 14614 2976
                13 10219 2100 10008 2440
                17 7217 6981 7460 7298
                18 6660 7105 6992 7326
                19 5885 7259 6262 7391
                20 1510 5860 1212 5592
                21 960 6085 752 5742
                22 500 6460 401 6072""";
        Player.br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(input.getBytes())));

        Player.readInput();
        assertEquals("8280 7265\r\n", Player.simulate());

        input = """
                8373 5963
                3
                0 0 4500
                1 15999 4500
                2 8000 7999
                14
                0 754 3252 547 3594
                1 1214 3404 917 3672
                2 1871 3515 1517 3701
                3 6977 3065 7150 3425
                8 9642 7668 9249 7747
                9 14614 6023 14883 5727
                10 15092 6264 15274 5908
                11 15092 2730 15274 3085
                12 14614 2976 14883 3272
                13 10008 2440 9839 2802
                19 6262 7391 6639 7523
                20 1212 5592 914 5324
                21 752 5742 544 5399
                22 401 6072 302 5684""";
        Player.br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(input.getBytes())));

        Player.readInput();
        assertEquals("9642 7668\r\n", Player.simulate());
    }
}
