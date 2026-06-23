package sudoku;

import generator.BackgroundGeneratorThread;
import generator.GeneratorPattern;

import java.awt.*;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Options {
    public static final String FILE_NAME = "hodoku.hcfg";
    public static final DifficultyLevel[] DEFAULT_DIFFICULTY_LEVELS = new DifficultyLevel[]{
            new DifficultyLevel(DifficultyType.INCOMPLETE, 0, ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.incomplete"), Color.BLACK, Color.WHITE),
            new DifficultyLevel(DifficultyType.EASY, 800, ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.easy"), Color.WHITE, Color.BLACK),
            new DifficultyLevel(
                    DifficultyType.MEDIUM, 1000, ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.medium"), new Color(100, 255, 100), Color.BLACK
            ),
            new DifficultyLevel(
                    DifficultyType.HARD, 1600, ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.hard"), new Color(255, 255, 100), Color.BLACK
            ),
            new DifficultyLevel(
                    DifficultyType.UNFAIR, 1800, ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.unfair"), new Color(255, 150, 80), Color.BLACK
            ),
            new DifficultyLevel(
                    DifficultyType.EXTREME,
                    Integer.MAX_VALUE,
                    ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.extreme"),
                    new Color(255, 100, 100),
                    Color.BLACK
            )
    };
    public static final StepConfig[] DEFAULT_SOLVER_STEPS = new StepConfig[]{
            new StepConfig(
                    2147483646, SolutionType.INCOMPLETE, DifficultyType.INCOMPLETE.ordinal(), SolutionCategory.LAST_RESORT, 0, 0, false, false, 2147483646, false, false
            ),
            new StepConfig(
                    Integer.MAX_VALUE,
                    SolutionType.GIVE_UP,
                    DifficultyType.EXTREME.ordinal(),
                    SolutionCategory.LAST_RESORT,
                    20000,
                    0,
                    true,
                    false,
                    Integer.MAX_VALUE,
                    true,
                    false
            ),
            new StepConfig(100, SolutionType.FULL_HOUSE, DifficultyType.EASY.ordinal(), SolutionCategory.SINGLES, 4, 0, true, true, 100, true, false),
            new StepConfig(200, SolutionType.NAKED_SINGLE, DifficultyType.EASY.ordinal(), SolutionCategory.SINGLES, 4, 0, true, true, 200, true, false),
            new StepConfig(300, SolutionType.HIDDEN_SINGLE, DifficultyType.EASY.ordinal(), SolutionCategory.SINGLES, 14, 0, true, true, 300, true, false),
            new StepConfig(1000, SolutionType.LOCKED_PAIR, DifficultyType.MEDIUM.ordinal(), SolutionCategory.INTERSECTIONS, 40, 0, true, true, 1000, true, false),
            new StepConfig(1100, SolutionType.LOCKED_TRIPLE, DifficultyType.MEDIUM.ordinal(), SolutionCategory.INTERSECTIONS, 60, 0, true, true, 1100, true, false),
            new StepConfig(
                    1200, SolutionType.LOCKED_CANDIDATES_1, DifficultyType.MEDIUM.ordinal(), SolutionCategory.INTERSECTIONS, 50, 0, true, true, 1200, true, false
            ),
            new StepConfig(1300, SolutionType.NAKED_PAIR, DifficultyType.MEDIUM.ordinal(), SolutionCategory.SUBSETS, 60, 0, true, true, 1300, true, false),
            new StepConfig(1400, SolutionType.NAKED_TRIPLE, DifficultyType.MEDIUM.ordinal(), SolutionCategory.SUBSETS, 80, 0, true, true, 1400, true, false),
            new StepConfig(1500, SolutionType.HIDDEN_PAIR, DifficultyType.MEDIUM.ordinal(), SolutionCategory.SUBSETS, 70, 0, true, true, 1500, true, false),
            new StepConfig(1600, SolutionType.HIDDEN_TRIPLE, DifficultyType.MEDIUM.ordinal(), SolutionCategory.SUBSETS, 100, 0, true, true, 1600, true, false),
            new StepConfig(2000, SolutionType.NAKED_QUADRUPLE, DifficultyType.HARD.ordinal(), SolutionCategory.SUBSETS, 120, 0, true, true, 2000, true, false),
            new StepConfig(2100, SolutionType.HIDDEN_QUADRUPLE, DifficultyType.HARD.ordinal(), SolutionCategory.SUBSETS, 150, 0, true, true, 2100, true, false),
            new StepConfig(2200, SolutionType.X_WING, DifficultyType.HARD.ordinal(), SolutionCategory.BASIC_FISH, 140, 0, true, false, 2200, false, false),
            new StepConfig(2300, SolutionType.SWORDFISH, DifficultyType.HARD.ordinal(), SolutionCategory.BASIC_FISH, 150, 0, true, false, 2300, false, false),
            new StepConfig(2400, SolutionType.JELLYFISH, DifficultyType.HARD.ordinal(), SolutionCategory.BASIC_FISH, 160, 0, true, false, 2400, false, false),
            new StepConfig(2500, SolutionType.SQUIRMBAG, DifficultyType.UNFAIR.ordinal(), SolutionCategory.BASIC_FISH, 470, 0, false, false, 2500, false, false),
            new StepConfig(2600, SolutionType.WHALE, DifficultyType.UNFAIR.ordinal(), SolutionCategory.BASIC_FISH, 470, 0, false, false, 2600, false, false),
            new StepConfig(2700, SolutionType.LEVIATHAN, DifficultyType.UNFAIR.ordinal(), SolutionCategory.BASIC_FISH, 470, 0, false, false, 2700, false, false),
            new StepConfig(2800, SolutionType.REMOTE_PAIR, DifficultyType.HARD.ordinal(), SolutionCategory.CHAINS_AND_LOOPS, 110, 0, true, true, 2800, false, false),
            new StepConfig(2900, SolutionType.BUG_PLUS_1, DifficultyType.HARD.ordinal(), SolutionCategory.UNIQUENESS, 100, 0, true, true, 2900, false, false),
            new StepConfig(
                    3000, SolutionType.SKYSCRAPER, DifficultyType.HARD.ordinal(), SolutionCategory.SINGLE_DIGIT_PATTERNS, 130, 0, true, true, 3000, false, false
            ),
            new StepConfig(3200, SolutionType.W_WING, DifficultyType.HARD.ordinal(), SolutionCategory.WINGS, 150, 0, true, true, 3200, false, false),
            new StepConfig(
                    3100, SolutionType.TWO_STRING_KITE, DifficultyType.HARD.ordinal(), SolutionCategory.SINGLE_DIGIT_PATTERNS, 150, 0, true, true, 3100, false, false
            ),
            new StepConfig(3300, SolutionType.XY_WING, DifficultyType.HARD.ordinal(), SolutionCategory.WINGS, 160, 0, true, true, 3300, false, false),
            new StepConfig(3400, SolutionType.XYZ_WING, DifficultyType.HARD.ordinal(), SolutionCategory.WINGS, 180, 0, true, true, 3400, false, false),
            new StepConfig(3500, SolutionType.UNIQUENESS_1, DifficultyType.HARD.ordinal(), SolutionCategory.UNIQUENESS, 100, 0, true, true, 3500, false, false),
            new StepConfig(3600, SolutionType.UNIQUENESS_2, DifficultyType.HARD.ordinal(), SolutionCategory.UNIQUENESS, 100, 0, true, true, 3600, false, false),
            new StepConfig(3700, SolutionType.UNIQUENESS_3, DifficultyType.HARD.ordinal(), SolutionCategory.UNIQUENESS, 100, 0, true, true, 3700, false, false),
            new StepConfig(3800, SolutionType.UNIQUENESS_4, DifficultyType.HARD.ordinal(), SolutionCategory.UNIQUENESS, 100, 0, true, true, 3800, false, false),
            new StepConfig(3900, SolutionType.UNIQUENESS_5, DifficultyType.HARD.ordinal(), SolutionCategory.UNIQUENESS, 100, 0, true, true, 3900, false, false),
            new StepConfig(4000, SolutionType.UNIQUENESS_6, DifficultyType.HARD.ordinal(), SolutionCategory.UNIQUENESS, 100, 0, true, true, 4000, false, false),
            new StepConfig(
                    4100, SolutionType.FINNED_X_WING, DifficultyType.HARD.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 130, 0, true, false, 4100, false, false
            ),
            new StepConfig(
                    4200, SolutionType.SASHIMI_X_WING, DifficultyType.HARD.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 150, 0, true, false, 4200, false, false
            ),
            new StepConfig(
                    4300, SolutionType.FINNED_SWORDFISH, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 200, 0, true, false, 4300, false, false
            ),
            new StepConfig(
                    4400, SolutionType.SASHIMI_SWORDFISH, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 240, 0, true, false, 4400, false, false
            ),
            new StepConfig(
                    4500, SolutionType.FINNED_JELLYFISH, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 250, 0, true, false, 4500, false, false
            ),
            new StepConfig(
                    4600, SolutionType.SASHIMI_JELLYFISH, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 260, 0, true, false, 4600, false, false
            ),
            new StepConfig(
                    4700, SolutionType.FINNED_SQUIRMBAG, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 470, 0, false, false, 4700, false, false
            ),
            new StepConfig(
                    4800, SolutionType.SASHIMI_SQUIRMBAG, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 470, 0, false, false, 4800, false, false
            ),
            new StepConfig(
                    4900, SolutionType.FINNED_WHALE, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 470, 0, false, false, 4900, false, false
            ),
            new StepConfig(
                    5000, SolutionType.SASHIMI_WHALE, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 470, 0, false, false, 5000, false, false
            ),
            new StepConfig(
                    5100, SolutionType.FINNED_LEVIATHAN, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 470, 0, false, false, 5100, false, false
            ),
            new StepConfig(
                    5200, SolutionType.SASHIMI_LEVIATHAN, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 470, 0, false, false, 5200, false, false
            ),
            new StepConfig(5300, SolutionType.SUE_DE_COQ, DifficultyType.UNFAIR.ordinal(), SolutionCategory.MISCELLANEOUS, 250, 0, true, true, 5300, false, false),
            new StepConfig(5400, SolutionType.X_CHAIN, DifficultyType.UNFAIR.ordinal(), SolutionCategory.CHAINS_AND_LOOPS, 260, 0, true, true, 5400, false, false),
            new StepConfig(5500, SolutionType.XY_CHAIN, DifficultyType.UNFAIR.ordinal(), SolutionCategory.CHAINS_AND_LOOPS, 260, 0, true, true, 5500, false, false),
            new StepConfig(5600, SolutionType.NICE_LOOP, DifficultyType.UNFAIR.ordinal(), SolutionCategory.CHAINS_AND_LOOPS, 280, 0, true, true, 5600, false, false),
            new StepConfig(5700, SolutionType.ALS_XZ, DifficultyType.UNFAIR.ordinal(), SolutionCategory.ALMOST_LOCKED_SETS, 300, 0, true, true, 5700, false, false),
            new StepConfig(
                    5800, SolutionType.ALS_XY_WING, DifficultyType.UNFAIR.ordinal(), SolutionCategory.ALMOST_LOCKED_SETS, 320, 0, true, true, 5800, false, false
            ),
            new StepConfig(
                    5900, SolutionType.ALS_XY_CHAIN, DifficultyType.UNFAIR.ordinal(), SolutionCategory.ALMOST_LOCKED_SETS, 340, 0, true, true, 5900, false, false
            ),
            new StepConfig(
                    6000, SolutionType.DEATH_BLOSSOM, DifficultyType.UNFAIR.ordinal(), SolutionCategory.ALMOST_LOCKED_SETS, 360, 0, false, true, 6000, false, false
            ),
            new StepConfig(6100, SolutionType.FRANKEN_X_WING, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FRANKEN_FISH, 300, 0, true, false, 6100, false, false),
            new StepConfig(
                    6200, SolutionType.FRANKEN_SWORDFISH, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FRANKEN_FISH, 350, 0, true, false, 6200, false, false
            ),
            new StepConfig(
                    6300, SolutionType.FRANKEN_JELLYFISH, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FRANKEN_FISH, 370, 0, false, false, 6300, false, false
            ),
            new StepConfig(
                    6400, SolutionType.FRANKEN_SQUIRMBAG, DifficultyType.EXTREME.ordinal(), SolutionCategory.FRANKEN_FISH, 470, 0, false, false, 6400, false, false
            ),
            new StepConfig(
                    6500, SolutionType.FRANKEN_WHALE, DifficultyType.EXTREME.ordinal(), SolutionCategory.FRANKEN_FISH, 470, 0, false, false, 6500, false, false
            ),
            new StepConfig(
                    6600, SolutionType.FRANKEN_LEVIATHAN, DifficultyType.EXTREME.ordinal(), SolutionCategory.FRANKEN_FISH, 470, 0, false, false, 6600, false, false
            ),
            new StepConfig(
                    6700,
                    SolutionType.FINNED_FRANKEN_X_WING,
                    DifficultyType.UNFAIR.ordinal(),
                    SolutionCategory.FINNED_FRANKEN_FISH,
                    390,
                    0,
                    true,
                    false,
                    6700,
                    false,
                    false
            ),
            new StepConfig(
                    6800,
                    SolutionType.FINNED_FRANKEN_SWORDFISH,
                    DifficultyType.UNFAIR.ordinal(),
                    SolutionCategory.FINNED_FRANKEN_FISH,
                    410,
                    0,
                    true,
                    false,
                    6800,
                    false,
                    false
            ),
            new StepConfig(
                    6900,
                    SolutionType.FINNED_FRANKEN_JELLYFISH,
                    DifficultyType.UNFAIR.ordinal(),
                    SolutionCategory.FINNED_FRANKEN_FISH,
                    430,
                    0,
                    false,
                    false,
                    6900,
                    false,
                    false
            ),
            new StepConfig(
                    7000,
                    SolutionType.FINNED_FRANKEN_SQUIRMBAG,
                    DifficultyType.EXTREME.ordinal(),
                    SolutionCategory.FINNED_FRANKEN_FISH,
                    470,
                    0,
                    false,
                    false,
                    7000,
                    false,
                    false
            ),
            new StepConfig(
                    7100,
                    SolutionType.FINNED_FRANKEN_WHALE,
                    DifficultyType.EXTREME.ordinal(),
                    SolutionCategory.FINNED_FRANKEN_FISH,
                    470,
                    0,
                    false,
                    false,
                    7100,
                    false,
                    false
            ),
            new StepConfig(
                    7200,
                    SolutionType.FINNED_FRANKEN_LEVIATHAN,
                    DifficultyType.EXTREME.ordinal(),
                    SolutionCategory.FINNED_FRANKEN_FISH,
                    470,
                    0,
                    false,
                    false,
                    7200,
                    false,
                    false
            ),
            new StepConfig(7300, SolutionType.MUTANT_X_WING, DifficultyType.EXTREME.ordinal(), SolutionCategory.MUTANT_FISH, 450, 0, false, false, 7300, false, false),
            new StepConfig(
                    7400, SolutionType.MUTANT_SWORDFISH, DifficultyType.EXTREME.ordinal(), SolutionCategory.MUTANT_FISH, 450, 0, false, false, 7400, false, false
            ),
            new StepConfig(
                    7500, SolutionType.MUTANT_JELLYFISH, DifficultyType.EXTREME.ordinal(), SolutionCategory.MUTANT_FISH, 450, 0, false, false, 7500, false, false
            ),
            new StepConfig(
                    7600, SolutionType.MUTANT_SQUIRMBAG, DifficultyType.EXTREME.ordinal(), SolutionCategory.MUTANT_FISH, 470, 0, false, false, 7600, false, false
            ),
            new StepConfig(7700, SolutionType.MUTANT_WHALE, DifficultyType.EXTREME.ordinal(), SolutionCategory.MUTANT_FISH, 470, 0, false, false, 7700, false, false),
            new StepConfig(
                    7800, SolutionType.MUTANT_LEVIATHAN, DifficultyType.EXTREME.ordinal(), SolutionCategory.MUTANT_FISH, 470, 0, false, false, 7800, false, false
            ),
            new StepConfig(
                    7900,
                    SolutionType.FINNED_MUTANT_X_WING,
                    DifficultyType.EXTREME.ordinal(),
                    SolutionCategory.FINNED_MUTANT_FISH,
                    470,
                    0,
                    false,
                    false,
                    7900,
                    false,
                    false
            ),
            new StepConfig(
                    8000,
                    SolutionType.FINNED_MUTANT_SWORDFISH,
                    DifficultyType.EXTREME.ordinal(),
                    SolutionCategory.FINNED_MUTANT_FISH,
                    470,
                    0,
                    false,
                    false,
                    8000,
                    false,
                    false
            ),
            new StepConfig(
                    8100,
                    SolutionType.FINNED_MUTANT_JELLYFISH,
                    DifficultyType.EXTREME.ordinal(),
                    SolutionCategory.FINNED_MUTANT_FISH,
                    470,
                    0,
                    false,
                    false,
                    8100,
                    false,
                    false
            ),
            new StepConfig(
                    8200,
                    SolutionType.FINNED_MUTANT_SQUIRMBAG,
                    DifficultyType.EXTREME.ordinal(),
                    SolutionCategory.FINNED_MUTANT_FISH,
                    470,
                    0,
                    false,
                    false,
                    8200,
                    false,
                    false
            ),
            new StepConfig(
                    8300,
                    SolutionType.FINNED_MUTANT_WHALE,
                    DifficultyType.EXTREME.ordinal(),
                    SolutionCategory.FINNED_MUTANT_FISH,
                    470,
                    0,
                    false,
                    false,
                    8300,
                    false,
                    false
            ),
            new StepConfig(
                    8400,
                    SolutionType.FINNED_MUTANT_LEVIATHAN,
                    DifficultyType.EXTREME.ordinal(),
                    SolutionCategory.FINNED_MUTANT_FISH,
                    470,
                    0,
                    false,
                    false,
                    8400,
                    false,
                    false
            ),
            new StepConfig(
                    8700, SolutionType.TEMPLATE_SET, DifficultyType.EXTREME.ordinal(), SolutionCategory.LAST_RESORT, 10000, 0, false, false, 8700, false, false
            ),
            new StepConfig(
                    8800, SolutionType.TEMPLATE_DEL, DifficultyType.EXTREME.ordinal(), SolutionCategory.LAST_RESORT, 10000, 0, false, false, 8800, false, false
            ),
            new StepConfig(8500, SolutionType.FORCING_CHAIN, DifficultyType.EXTREME.ordinal(), SolutionCategory.LAST_RESORT, 500, 0, true, false, 8500, false, false),
            new StepConfig(8600, SolutionType.FORCING_NET, DifficultyType.EXTREME.ordinal(), SolutionCategory.LAST_RESORT, 700, 0, true, false, 8600, false, false),
            new StepConfig(8900, SolutionType.BRUTE_FORCE, DifficultyType.EXTREME.ordinal(), SolutionCategory.LAST_RESORT, 10000, 0, true, false, 8900, false, false),
            new StepConfig(
                    5650, SolutionType.GROUPED_NICE_LOOP, DifficultyType.UNFAIR.ordinal(), SolutionCategory.CHAINS_AND_LOOPS, 300, 0, true, true, 5650, false, false
            ),
            new StepConfig(
                    3170, SolutionType.EMPTY_RECTANGLE, DifficultyType.HARD.ordinal(), SolutionCategory.SINGLE_DIGIT_PATTERNS, 120, 0, true, true, 3170, false, false
            ),
            new StepConfig(4010, SolutionType.HIDDEN_RECTANGLE, DifficultyType.HARD.ordinal(), SolutionCategory.UNIQUENESS, 100, 0, true, true, 4010, false, false),
            new StepConfig(
                    4020, SolutionType.AVOIDABLE_RECTANGLE_1, DifficultyType.HARD.ordinal(), SolutionCategory.UNIQUENESS, 100, 0, true, true, 4020, false, false
            ),
            new StepConfig(
                    4030, SolutionType.AVOIDABLE_RECTANGLE_2, DifficultyType.HARD.ordinal(), SolutionCategory.UNIQUENESS, 100, 0, true, true, 4030, false, false
            ),
            new StepConfig(5330, SolutionType.SIMPLE_COLORS, DifficultyType.HARD.ordinal(), SolutionCategory.COLORING, 150, 0, true, true, 5330, false, false),
            new StepConfig(5360, SolutionType.MULTI_COLORS, DifficultyType.HARD.ordinal(), SolutionCategory.COLORING, 200, 0, true, true, 5360, false, false),
            new StepConfig(8450, SolutionType.KRAKEN_FISH, DifficultyType.EXTREME.ordinal(), SolutionCategory.LAST_RESORT, 500, 0, false, false, 8450, false, false),
            new StepConfig(
                    3120, SolutionType.TURBOT_FISH, DifficultyType.HARD.ordinal(), SolutionCategory.SINGLE_DIGIT_PATTERNS, 120, 0, true, true, 3120, false, false
            ),
            new StepConfig(
                    1210, SolutionType.LOCKED_CANDIDATES_2, DifficultyType.MEDIUM.ordinal(), SolutionCategory.INTERSECTIONS, 50, 0, true, true, 1210, true, false
            )
    };
    public static final int CACHE_SIZE = 10;
    public static final int RESTRICT_CHAIN_LENGTH = 20;
    public static final int RESTRICT_NICE_LOOP_LENGTH = 10;
    public static final boolean RESTRICT_CHAIN_SIZE = true;
    public static final int MAX_TABLE_ENTRY_LENGTH = 1000;
    public static final int ANZ_TABLE_LOOK_AHEAD = 4;
    public static final boolean ONLY_ONE_CHAIN_PER_STEP = true;
    public static final boolean ALLOW_ALS_IN_TABLING_CHAINS = false;
    public static final boolean ALL_STEPS_ALLOW_ALS_IN_TABLING_CHAINS = true;
    public static final boolean ONLY_ONE_ALS_PER_STEP = true;
    public static final boolean ALLOW_ALS_OVERLAP = false;
    public static final boolean ALL_STEPS_ONLY_ONE_ALS_PER_STEP = true;
    public static final boolean ALL_STEPS_ALLOW_ALS_OVERLAP = true;
    public static final int MAX_FINS = 5;
    public static final int MAX_ENDO_FINS = 2;
    public static final boolean CHECK_TEMPLATES = true;
    public static final int KRAKEN_MAX_FISH_TYPE = 1;
    public static final int KRAKEN_MAX_FISH_SIZE = 4;
    public static final int MAX_KRAKEN_FINS = 2;
    public static final int MAX_KRAKEN_ENDO_FINS = 0;
    public static final boolean ONLY_ONE_FISH_PER_STEP = true;
    public static final int FISH_DISPLAY_MODE = 0;
    public static final boolean ALL_STEPS_SEARCH_FISH = true;
    public static final int ALL_STEPS_MAX_FISH_TYPE = 1;
    public static final int ALL_STEPS_MIN_FISH_SIZE = 2;
    public static final int ALL_STEPS_MAX_FISH_SIZE = 4;
    public static final int ALL_STEPS_MAX_FINS = 5;
    public static final int ALL_STEPS_MAX_ENDO_FINS = 2;
    public static final boolean ALL_STEPS_CHECK_TEMPLATES = true;
    public static final int ALL_STEPS_MAX_KRAKEN_FISH_TYPE = 1;
    public static final int ALL_STEPS_MIN_KRAKEN_FISH_SIZE = 2;
    public static final int ALL_STEPS_MAX_KRAKEN_FISH_SIZE = 4;
    public static final int ALL_STEPS_MAX_KRAKEN_FINS = 2;
    public static final int ALL_STEPS_MAX_KRAKEN_ENDO_FINS = 0;
    public static final String ALL_STEPS_FISH_CANDIDATES = "111111111";
    public static final String ALL_STEPS_KRAKEN_FISH_CANDIDATES = "111111111";
    public static final int ALL_STEPS_SORT_MODE = 4;
    public static final int ALL_STEPS_ALS_CHAIN_LENGTH = 6;
    public static final boolean ALL_STEPS_ALS_CHAIN_FORWARD_ONLY = true;
    public static final Color[] COLORING_COLORS = new Color[]{
            new Color(255, 192, 89),
            new Color(247, 222, 143),
            new Color(177, 165, 243),
            new Color(220, 212, 252),
            new Color(247, 165, 167),
            new Color(255, 210, 210),
            new Color(134, 232, 208),
            new Color(206, 251, 237),
            new Color(134, 242, 128),
            new Color(215, 255, 215)
    };
    public static final boolean COLOR_VALUES = true;
    public static final boolean ALLOW_ERS_WITH_ONLY_TWO_CANDIDATES = false;
    public static final boolean ALLOW_DUALS_AND_SIAMESE = false;
    public static final boolean ALLOW_UNIQUENESS_MISSING_CANDIDATES = true;
    public static final boolean SHOW_CANDIDATES = true;
    public static final boolean SHOW_WRONG_VALUES = true;
    public static final boolean SHOW_DEVIATIONS = true;
    public static final boolean SHOW_COLORKU = false;
    public static final boolean INVALID_CELLS = false;
    public static final boolean COLOR_CELLS = true;
    public static final boolean SAVE_WINDOW_LAYOUT = true;
    public static final boolean USE_SHIFT_FOR_REGION_SELECT = true;
    public static final boolean ALTERNATIVE_MOUSE_MODE = false;
    public static final boolean DELETE_CURSOR_DISPLAY = false;
    public static final int DELETE_CURSOR_DISPLAY_LENGTH = 1000;
    public static final boolean USE_OR_INSTEAD_OF_AND_FOR_FILTER = false;
    public static final boolean ONLY_SMALL_FILTERS = false;
    public static final boolean USE_DEFAULT_FONT_SIZE = true;
    public static final int CUSTOM_FONT_SIZE = 12;
    public static final int DRAW_MODE = 1;
    public static final int INITIAL_HEIGHT = 844;
    public static final int INITIAL_WIDTH = 643;
    public static final int INITIAL_VERT_DIVIDER_LOC = -1;
    public static final int INITIAL_HORZ_DIVIDER_LOC = 627;
    public static final int INITIAL_DISP_MODE = 0;
    public static final int INITIAL_X_POS = -1;
    public static final int INITIAL_Y_POS = -1;
    public static final boolean INITIAL_SHOW_HINT_PANEL = true;
    public static final boolean INITIAL_SHOW_TOOLBAR = true;
    public static final int ACT_LEVEL = DEFAULT_DIFFICULTY_LEVELS[1].getOrdinal();
    public static final boolean SHOW_SUDOKU_SOLVED = false;
    public static final boolean EDIT_MODE_AUTO_ADVANCE = false;
    public static final boolean USE_ZERO_INSTEAD_OF_DOT = false;
    public static final Color GRID_COLOR = Color.BLACK;
    public static final Color INNER_GRID_COLOR = Color.LIGHT_GRAY;
    public static final Color WRONG_VALUE_COLOR = Color.RED;
    public static final Color DEVIATION_COLOR = new Color(255, 185, 185);
    public static final Color CELL_FIXED_VALUE_COLOR = Color.BLACK;
    public static final Color CELL_VALUE_COLOR = Color.BLUE;
    public static final Color CANDIDATE_COLOR = new Color(100, 100, 100);
    public static final Color DEFAULT_CELL_COLOR = Color.WHITE;
    public static final Color ALTERNATE_CELL_COLOR = Color.WHITE;
    public static final Color AKT_CELL_COLOR = new Color(255, 255, 150);
    public static final Color INVALID_CELL_COLOR = new Color(255, 185, 185);
    public static final Color POSSIBLE_CELL_COLOR = new Color(185, 255, 185);
    public static final Color HINT_CANDIDATE_BACK_COLOR = new Color(63, 218, 101);
    public static final Color HINT_CANDIDATE_DELETE_BACK_COLOR = new Color(255, 118, 132);
    public static final Color HINT_CANDIDATE_CANNIBALISTIC_BACK_COLOR = new Color(235, 0, 0);
    public static final Color HINT_CANDIDATE_FIN_BACK_COLOR = new Color(127, 187, 255);
    public static final Color HINT_CANDIDATE_ENDO_FIN_BACK_COLOR = new Color(216, 178, 255);
    public static final Color HINT_CANDIDATE_COLOR = Color.BLACK;
    public static final Color HINT_CANDIDATE_DELETE_COLOR = Color.BLACK;
    public static final Color HINT_CANDIDATE_CANNIBALISTIC_COLOR = Color.BLACK;
    public static final Color HINT_CANDIDATE_FIN_COLOR = Color.BLACK;
    public static final Color HINT_CANDIDATE_ENDO_FIN_COLOR = Color.BLACK;
    public static final Color[] HINT_CANDIDATE_ALS_BACK_COLORS = new Color[]{
            new Color(197, 232, 140), new Color(255, 203, 203), new Color(178, 223, 223), new Color(252, 220, 165)
    };
    public static final Color[] COLORKU_COLORS = new Color[]{
            new Color(252, 20, 16),
            new Color(251, 153, 0),
            new Color(255, 218, 27),
            new Color(0, 192, 41),
            new Color(0, 45, 255),
            new Color(221, 84, 177),
            new Color(159, 252, 51),
            new Color(144, 246, 249),
            new Color(255, 175, 252),
            Color.BLACK,
            new Color(128, 128, 128)
    };
    public static final Color[] HINT_CANDIDATE_ALS_COLORS = new Color[]{Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK};
    public static final Color ARROW_COLOR = Color.RED;
    public static final double VALUE_FONT_FACTOR = 0.6;
    public static final double CANDIDATE_FONT_FACTOR = 0.25;
    public static final double HINT_BACK_FACTOR = 1.6;
    public static final double BOX_LINE_FACTOR = 1.5;
    public static final String DEFAULT_FILE_DIR = System.getProperty("user.home");
    public static final String DEFAULT_IMAGE_DIR = System.getProperty("user.home");
    public static final String DEFAULT_LANGUAGE = "";
    public static final String DEFAULT_LAF = "";
    public static final boolean ONLY_SMALL_CURSORS = true;
    public static final double CURSOR_FRAME_SIZE = 0.08;
    public static final GameMode GAME_MODE = GameMode.PLAYING;
    public static final boolean SHOW_HINT_BUTTONS_IN_TOOLBAR = false;
    public static final int HISTORY_SIZE = 50;
    public static final boolean HISTORY_PREVIEW = true;
    public static final boolean BDS_SEARCH_FOR_CELLS = true;
    public static final boolean BDS_SEARCH_FOR_CANDIDATES = false;
    public static final int BDS_SEARCH_CANDIDATES_ANZ = 0;
    public static final int GENERATOR_PATTERN_INDEX = -1;
    private static final Options.ProgressComparator progressComparator = new Options.ProgressComparator();
    public static Font DEFAULT_VALUE_FONT = new Font("Tahoma", 0, 10);
    public static Font DEFAULT_CANDIDATE_FONT = new Font("Tahoma", 0, 10);
    public static Font BIG_FONT = new Font("Arial", 1, 18);
    public static Font SMALL_FONT = new Font("Arial", 0, 14);
    public static Options instance = null;
    private static String[] availableFontNames = null;
    public StepConfig[] solverSteps = null;
    public StepConfig[] solverStepsProgress = null;
    private DifficultyLevel[] difficultyLevels = null;
    private StepConfig[] orgSolverSteps = null;
    private String[][] normalPuzzles = new String[5][10];
    private String[] learningPuzzles = new String[10];
    private String[] practisingPuzzles = new String[10];
    private int practisingPuzzlesLevel = -1;
    private int restrictChainLength = 20;
    private int restrictNiceLoopLength = 10;
    private boolean restrictChainSize = true;
    private int maxTableEntryLength = 1000;
    private int anzTableLookAhead = 4;
    private boolean onlyOneChainPerStep = true;
    private boolean allowAlsInTablingChains = false;
    private boolean allStepsAllowAlsInTablingChains = true;
    private boolean onlyOneAlsPerStep = true;
    private boolean allowAlsOverlap = false;
    private boolean allStepsOnlyOneAlsPerStep = true;
    private boolean allStepsAllowAlsOverlap = true;
    private int maxFins = 5;
    private int maxEndoFins = 2;
    private boolean checkTemplates = true;
    private int krakenMaxFishType = 1;
    private int krakenMaxFishSize = 4;
    private int maxKrakenFins = 2;
    private int maxKrakenEndoFins = 0;
    private boolean onlyOneFishPerStep = true;
    private int fishDisplayMode = 0;
    private boolean allStepsSearchFish = true;
    private int allStepsMaxFishType = 1;
    private int allStepsMinFishSize = 2;
    private int allStepsMaxFishSize = 4;
    private int allStepsMaxFins = 5;
    private int allStepsMaxEndoFins = 2;
    private boolean allStepsCheckTemplates = true;
    private int allStepsKrakenMaxFishType = 1;
    private int allStepsKrakenMinFishSize = 2;
    private int allStepsKrakenMaxFishSize = 4;
    private int allStepsMaxKrakenFins = 2;
    private int allStepsMaxKrakenEndoFins = 0;
    private String allStepsFishCandidates = "111111111";
    private String allStepsKrakenFishCandidates = "111111111";
    private int allStepsSortMode = 4;
    private int allStepsAlsChainLength = 6;
    private boolean allStepsAlsChainForwardOnly = true;
    private Color[] coloringColors = null;
    private boolean colorValues = true;
    private boolean allowErsWithOnlyTwoCandidates = false;
    private boolean allowDualsAndSiamese = false;
    private boolean allowUniquenessMissingCandidates = true;
    private boolean showCandidates = true;
    private boolean showWrongValues = true;
    private boolean showDeviations = true;
    private boolean showColorKu = false;
    private boolean showColorKuAct = false;
    private boolean invalidCells = false;
    private boolean colorCells = true;
    private boolean saveWindowLayout = true;
    private boolean useShiftForRegionSelect = true;
    private boolean alternativeMouseMode = false;
    private boolean deleteCursorDisplay = false;
    private int deleteCursorDisplayLength = 1000;
    private boolean useDefaultFontSize = true;
    private int customFontSize = 12;
    private boolean useOrInsteadOfAndForFilter = false;
    private boolean onlySmallFilters = false;
    private int drawMode = 1;
    private int initialHeight = 844;
    private int initialWidth = 643;
    private int initialVertDividerLoc = -1;
    private int initialHorzDividerLoc = 627;
    private int initialDisplayMode = 0;
    private int initialXPos = -1;
    private int initialYPos = -1;
    private boolean showHintPanel = true;
    private boolean showToolBar = true;
    private int actLevel = ACT_LEVEL;
    private boolean showSudokuSolved = false;
    private boolean editModeAutoAdvance = false;
    private boolean useZeroInsteadOfDot = false;
    private Color gridColor = GRID_COLOR;
    private Color innerGridColor = INNER_GRID_COLOR;
    private Color wrongValueColor = WRONG_VALUE_COLOR;
    private Color deviationColor = DEVIATION_COLOR;
    private Color cellFixedValueColor = CELL_FIXED_VALUE_COLOR;
    private Color cellValueColor = CELL_VALUE_COLOR;
    private Color candidateColor = CANDIDATE_COLOR;
    private Color defaultCellColor = DEFAULT_CELL_COLOR;
    private Color alternateCellColor = ALTERNATE_CELL_COLOR;
    private Color aktCellColor = AKT_CELL_COLOR;
    private Color invalidCellColor = INVALID_CELL_COLOR;
    private Color possibleCellColor = POSSIBLE_CELL_COLOR;
    private Color hintCandidateBackColor = HINT_CANDIDATE_BACK_COLOR;
    private Color hintCandidateDeleteBackColor = HINT_CANDIDATE_DELETE_BACK_COLOR;
    private Color hintCandidateCannibalisticBackColor = HINT_CANDIDATE_CANNIBALISTIC_BACK_COLOR;
    private Color hintCandidateFinBackColor = HINT_CANDIDATE_FIN_BACK_COLOR;
    private Color hintCandidateEndoFinBackColor = HINT_CANDIDATE_ENDO_FIN_BACK_COLOR;
    private Color hintCandidateColor = HINT_CANDIDATE_COLOR;
    private Color hintCandidateDeleteColor = HINT_CANDIDATE_DELETE_COLOR;
    private Color hintCandidateCannibalisticColor = HINT_CANDIDATE_CANNIBALISTIC_COLOR;
    private Color hintCandidateFinColor = HINT_CANDIDATE_FIN_COLOR;
    private Color hintCandidateEndoFinColor = HINT_CANDIDATE_ENDO_FIN_COLOR;
    private Color[] hintCandidateAlsBackColors = null;
    private Color[] hintCandidateAlsColors = null;
    private Color[] colorKuColors = null;
    private Color arrowColor = ARROW_COLOR;
    private double valueFontFactor = 0.6;
    private double candidateFontFactor = 0.25;
    private double hintBackFactor = 1.6;
    private double boxLineFactor = 1.5;
    private Font defaultValueFont = new Font(DEFAULT_VALUE_FONT.getName(), DEFAULT_VALUE_FONT.getStyle(), DEFAULT_VALUE_FONT.getSize());
    private Font defaultCandidateFont = new Font(DEFAULT_CANDIDATE_FONT.getName(), DEFAULT_CANDIDATE_FONT.getStyle(), DEFAULT_CANDIDATE_FONT.getSize());
    private Font bigFont = new Font(BIG_FONT.getName(), BIG_FONT.getStyle(), BIG_FONT.getSize());
    private Font smallFont = new Font(SMALL_FONT.getName(), SMALL_FONT.getStyle(), SMALL_FONT.getSize());
    private String defaultFileDir = DEFAULT_FILE_DIR;
    private String defaultImageDir = DEFAULT_IMAGE_DIR;
    private String language = "";
    private String laf = "";
    private boolean onlySmallCursors = true;
    private double cursorFrameSize = 0.08;
    private GameMode gameMode = GAME_MODE;
    private boolean showHintButtonsInToolbar = false;
    private int historySize = 50;
    private boolean historyPreview = true;
    private List<String> historyOfCreatedPuzzles = new ArrayList<>(this.historySize);
    private boolean bdsSearchForCells = true;
    private boolean bdsSearchForCandidates = false;
    private int bdsSearchCandidatesAnz = 0;
    private ArrayList<GeneratorPattern> generatorPatterns = new ArrayList<>();
    private int generatorPatternIndex = -1;

    public Options() {
        this.difficultyLevels = this.copyDifficultyLevels(DEFAULT_DIFFICULTY_LEVELS);
        this.orgSolverSteps = this.copyStepConfigs(DEFAULT_SOLVER_STEPS, false, false, true);
        this.solverSteps = this.copyStepConfigs(DEFAULT_SOLVER_STEPS, false, false, false);
        this.solverStepsProgress = this.copyStepConfigs(DEFAULT_SOLVER_STEPS, false, false, false, true);
        this.hintCandidateAlsBackColors = new Color[HINT_CANDIDATE_ALS_BACK_COLORS.length];

        for (int i = 0; i < HINT_CANDIDATE_ALS_BACK_COLORS.length; i++) {
            this.hintCandidateAlsBackColors[i] = new Color(HINT_CANDIDATE_ALS_BACK_COLORS[i].getRGB());
        }

        this.hintCandidateAlsColors = new Color[HINT_CANDIDATE_ALS_COLORS.length];

        for (int i = 0; i < HINT_CANDIDATE_ALS_COLORS.length; i++) {
            this.hintCandidateAlsColors[i] = new Color(HINT_CANDIDATE_ALS_COLORS[i].getRGB());
        }

        this.coloringColors = new Color[COLORING_COLORS.length];

        for (int i = 0; i < COLORING_COLORS.length; i++) {
            this.coloringColors[i] = new Color(COLORING_COLORS[i].getRGB());
        }

        this.colorKuColors = new Color[COLORKU_COLORS.length];

        for (int i = 0; i < COLORKU_COLORS.length; i++) {
            this.colorKuColors[i] = new Color(COLORKU_COLORS[i].getRGB());
        }
    }

    public static void resetAll() {
        instance = new Options();
    }

    public static Options getInstance() {
        if (instance == null) {
            readOptions();
        }

        return instance;
    }

    public static void readOptions() {
        String tmp = System.getProperty("java.io.tmpdir");
        String fileName = null;
        if (tmp.endsWith(File.separator)) {
            fileName = tmp + "hodoku.hcfg";
        } else {
            fileName = tmp + File.separator + "hodoku.hcfg";
        }

        readOptions(fileName);
    }

    public static void readOptions(String fileName) {
        Logger.getLogger(Options.class.getName()).log(Level.INFO, "Reading options from {0}", fileName);

        try {
            XMLDecoder in = new XMLDecoder(new BufferedInputStream(new FileInputStream(fileName)));
            instance = (Options) in.readObject();
            in.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Options.class.getName()).log(Level.INFO, "No config file found");
            instance = new Options();

            try {
                instance.writeOptions();
            } catch (FileNotFoundException exi) {
                Logger.getLogger(Options.class.getName()).log(Level.SEVERE, "Error writing options", exi);
            }
        }

        instance.solverSteps = instance.copyStepConfigs(instance.orgSolverSteps, false, false, false);
        instance.solverStepsProgress = instance.copyStepConfigs(instance.orgSolverSteps, false, false, false, true);
        boolean changed = false;
        int maxScore = instance.difficultyLevels[1].getMaxScore();

        for (int i = 2; i < instance.difficultyLevels.length; i++) {
            if (instance.difficultyLevels[i].getMaxScore() <= maxScore) {
                instance.difficultyLevels[i].setMaxScore(maxScore + 100);
                changed = true;
            }

            maxScore = instance.difficultyLevels[i].getMaxScore();
        }

        if (changed) {
            BackgroundGeneratorThread.getInstance().resetAll();
        }
    }

    public static void main(String[] args) {
        Options options = new Options();

        try {
            XMLEncoder out = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("L:\\dummy.xml")));
            out.writeObject(options);
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            XMLDecoder in = new XMLDecoder(new BufferedInputStream(new FileInputStream("L:\\dummy.xml")));
            options = (Options) in.readObject();
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println(options.solverSteps.length);

        for (int i = 0; i < options.solverSteps.length; i++) {
            System.out.println(i + ": " + options.solverSteps[i]);
        }
    }

    public void addSudokuToHistory(Sudoku2 sudoku) {
        if (sudoku.getLevel() != null) {
            List<String> history = this.getHistoryOfCreatedPuzzles();

            while (history.size() > this.getHistorySize() - 1) {
                history.remove(history.size() - 1);
            }

            String str = sudoku.getSudoku(ClipboardMode.CLUES_ONLY) + "#" + sudoku.getLevel().getOrdinal() + "#" + sudoku.getScore() + "#" + new Date().getTime();
            history.add(0, str);
        }
    }

    public void checkAllFonts() {
        if (!this.checkFont(DEFAULT_CANDIDATE_FONT)) {
            DEFAULT_CANDIDATE_FONT = new Font("SansSerif", DEFAULT_CANDIDATE_FONT.getStyle(), DEFAULT_CANDIDATE_FONT.getSize());
            this.defaultCandidateFont = new Font(DEFAULT_CANDIDATE_FONT.getName(), DEFAULT_CANDIDATE_FONT.getStyle(), DEFAULT_CANDIDATE_FONT.getSize());
        }

        if (!this.checkFont(DEFAULT_VALUE_FONT)) {
            DEFAULT_VALUE_FONT = new Font("SansSerif", DEFAULT_VALUE_FONT.getStyle(), DEFAULT_VALUE_FONT.getSize());
            this.defaultValueFont = new Font(DEFAULT_VALUE_FONT.getName(), DEFAULT_VALUE_FONT.getStyle(), DEFAULT_VALUE_FONT.getSize());
        }

        if (!this.checkFont(this.defaultCandidateFont)) {
            this.defaultCandidateFont = new Font(DEFAULT_CANDIDATE_FONT.getName(), DEFAULT_CANDIDATE_FONT.getStyle(), DEFAULT_CANDIDATE_FONT.getSize());
        }

        if (!this.checkFont(this.defaultValueFont)) {
            this.defaultValueFont = new Font(DEFAULT_VALUE_FONT.getName(), DEFAULT_VALUE_FONT.getStyle(), DEFAULT_VALUE_FONT.getSize());
        }

        if (!this.checkFont(BIG_FONT)) {
            BIG_FONT = new Font("SansSerif", BIG_FONT.getStyle(), BIG_FONT.getSize());
            this.bigFont = new Font(BIG_FONT.getName(), BIG_FONT.getStyle(), BIG_FONT.getSize());
        }

        if (!this.checkFont(SMALL_FONT)) {
            SMALL_FONT = new Font("SansSerif", SMALL_FONT.getStyle(), SMALL_FONT.getSize());
            this.smallFont = new Font(SMALL_FONT.getName(), SMALL_FONT.getStyle(), SMALL_FONT.getSize());
        }

        if (!this.checkFont(this.bigFont)) {
            this.bigFont = new Font(BIG_FONT.getName(), BIG_FONT.getStyle(), BIG_FONT.getSize());
        }

        if (!this.checkFont(this.smallFont)) {
            this.smallFont = new Font(SMALL_FONT.getName(), SMALL_FONT.getStyle(), SMALL_FONT.getSize());
        }
    }

    public boolean checkFont(Font font) {
        return this.checkFont(font.getName());
    }

    public boolean checkFont(String fontName) {
        if (availableFontNames == null) {
            availableFontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        }

        return Arrays.binarySearch(availableFontNames, fontName) >= 0;
    }

    public DifficultyLevel[] copyDifficultyLevels(DifficultyLevel[] src) {
        DifficultyLevel[] dest = new DifficultyLevel[src.length];

        for (int i = 0; i < src.length; i++) {
            DifficultyLevel act = src[i];
            dest[i] = new DifficultyLevel(act.getType(), act.getMaxScore(), act.getName(), act.getBackgroundColor(), act.getForegroundColor());
        }

        return dest;
    }

    public StepConfig[] copyStepConfigs(StepConfig[] src, boolean noLastTwo, boolean addLastTwo) {
        return this.copyStepConfigs(src, noLastTwo, addLastTwo, false);
    }

    public StepConfig[] copyStepConfigs(StepConfig[] src, boolean noLastTwo, boolean addLastTwo, boolean noSort) {
        return this.copyStepConfigs(src, noLastTwo, addLastTwo, noSort, false);
    }

    public StepConfig[] copyStepConfigs(StepConfig[] src, boolean noLastTwo, boolean addLastTwo, boolean noSort, boolean sortProgress) {
        int length = src.length;
        if (noLastTwo) {
            length -= 2;
        }

        if (addLastTwo) {
            length += 2;
        }

        StepConfig[] dest = new StepConfig[length];
        if (src == DEFAULT_SOLVER_STEPS && noLastTwo && !addLastTwo && !noSort) {
            for (int i = 0; i < length; i++) {
                StepConfig act = src[i + 2];
                dest[i] = new StepConfig(
                        act.getIndex(),
                        act.getType(),
                        act.getLevel(),
                        act.getCategory(),
                        act.getBaseScore(),
                        act.getAdminScore(),
                        act.isEnabled(),
                        act.isAllStepsEnabled(),
                        act.getIndexProgress(),
                        act.isEnabledProgress(),
                        act.isEnabledTraining()
                );
            }
        } else {
            for (int i = 0; i < (addLastTwo ? length - 2 : length); i++) {
                StepConfig act = src[i];
                dest[i] = new StepConfig(
                        act.getIndex(),
                        act.getType(),
                        act.getLevel(),
                        act.getCategory(),
                        act.getBaseScore(),
                        act.getAdminScore(),
                        act.isEnabled(),
                        act.isAllStepsEnabled(),
                        act.getIndexProgress(),
                        act.isEnabledProgress(),
                        act.isEnabledTraining()
                );
            }
        }

        if (addLastTwo) {
            StepConfig act = DEFAULT_SOLVER_STEPS[0];
            dest[dest.length - 2] = new StepConfig(
                    act.getIndex(),
                    act.getType(),
                    act.getLevel(),
                    act.getCategory(),
                    act.getBaseScore(),
                    act.getAdminScore(),
                    act.isEnabled(),
                    act.isAllStepsEnabled(),
                    act.getIndexProgress(),
                    act.isEnabledProgress(),
                    act.isEnabledTraining()
            );
            act = DEFAULT_SOLVER_STEPS[1];
            dest[dest.length - 1] = new StepConfig(
                    act.getIndex(),
                    act.getType(),
                    act.getLevel(),
                    act.getCategory(),
                    act.getBaseScore(),
                    act.getAdminScore(),
                    act.isEnabled(),
                    act.isAllStepsEnabled(),
                    act.getIndexProgress(),
                    act.isEnabledProgress(),
                    act.isEnabledTraining()
            );
        }

        if (!noSort) {
            if (sortProgress) {
                Arrays.sort(dest, progressComparator);
            } else {
                Arrays.sort(dest);
            }
        }

        return dest;
    }

    public void adjustOrgSolverSteps() {
        boolean somethingChanged = false;

        for (StepConfig step : this.solverSteps) {
            StepConfig orgStep = null;

            for (int i = 0; i < this.orgSolverSteps.length; i++) {
                if (this.orgSolverSteps[i].getType() == step.getType()) {
                    orgStep = this.orgSolverSteps[i];
                    break;
                }
            }

            if (orgStep == null) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "StepConfig not found!");
            } else {
                if (step.getAdminScore() != orgStep.getAdminScore()
                        || step.getBaseScore() != orgStep.getBaseScore()
                        || step.getCategory() != orgStep.getCategory()
                        || step.isEnabled() != orgStep.isEnabled()
                        || step.getIndex() != orgStep.getIndex()
                        || step.getLevel() != orgStep.getLevel()) {
                    somethingChanged = true;
                }

                orgStep.setAdminScore(step.getAdminScore());
                orgStep.setBaseScore(step.getBaseScore());
                orgStep.setCategory(step.getCategory());
                orgStep.setEnabled(step.isEnabled());
                orgStep.setIndex(step.getIndex());
                orgStep.setLevel(step.getLevel());
            }
        }

        if (somethingChanged) {
            BackgroundGeneratorThread.getInstance().resetAll();
        }
    }

    public void sortProgressSteps() {
        Arrays.sort(this.solverStepsProgress, progressComparator);
    }

    public void resetDifficultyLevelStrings() {
        DEFAULT_DIFFICULTY_LEVELS[0].setName(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.incomplete"));
        DEFAULT_DIFFICULTY_LEVELS[1].setName(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.easy"));
        DEFAULT_DIFFICULTY_LEVELS[2].setName(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.medium"));
        DEFAULT_DIFFICULTY_LEVELS[3].setName(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.hard"));
        DEFAULT_DIFFICULTY_LEVELS[4].setName(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.unfair"));
        DEFAULT_DIFFICULTY_LEVELS[5].setName(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.extreme"));
        this.difficultyLevels[0].setName(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.incomplete"));
        this.difficultyLevels[1].setName(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.easy"));
        this.difficultyLevels[2].setName(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.medium"));
        this.difficultyLevels[3].setName(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.hard"));
        this.difficultyLevels[4].setName(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.unfair"));
        this.difficultyLevels[5].setName(ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.extreme"));
    }

    public String getTrainingStepsString(boolean ellipsis) {
        return this.getTrainingStepsString(this.orgSolverSteps, ellipsis);
    }

    public String getTrainingStepsString(StepConfig[] stepArray, boolean ellipsis) {
        StringBuilder tmp = new StringBuilder();
        boolean first = true;

        for (StepConfig step : stepArray) {
            if (step.isEnabledTraining()) {
                if (first) {
                    first = false;
                } else {
                    if (ellipsis) {
                        tmp.append("...");
                        break;
                    }

                    tmp.append(", ");
                }

                tmp.append(step.getType().getStepName());
            }
        }

        return tmp.toString();
    }

    public void writeOptions() throws FileNotFoundException {
        String tmp = System.getProperty("java.io.tmpdir");
        String fileName = null;
        if (tmp.endsWith(File.separator)) {
            fileName = tmp + "hodoku.hcfg";
        } else {
            fileName = tmp + File.separator + "hodoku.hcfg";
        }

        this.writeOptions(fileName);
    }

    public void writeOptions(String fileName) throws FileNotFoundException {
        Logger.getLogger(Options.class.getName()).log(Level.INFO, "Writing options to {0}", fileName);
        XMLEncoder out = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(fileName)));
        out.writeObject(this);
        out.close();
    }

    public List<String> getHistoryOfCreatedPuzzles() {
        return this.historyOfCreatedPuzzles;
    }

    public void setHistoryOfCreatedPuzzles(List<String> historyOfCreatedPuzzles) {
        this.historyOfCreatedPuzzles = historyOfCreatedPuzzles;
    }

    public boolean isHistoryPreview() {
        return this.historyPreview;
    }

    public void setHistoryPreview(boolean historyPreview) {
        this.historyPreview = historyPreview;
    }

    public boolean isInvalidCells() {
        return this.invalidCells;
    }

    public void setInvalidCells(boolean invalidCells) {
        this.invalidCells = invalidCells;
    }

    public int getAllStepsSortMode() {
        return this.allStepsSortMode;
    }

    public void setAllStepsSortMode(int allStepsSortMode) {
        this.allStepsSortMode = allStepsSortMode;
    }

    public boolean isBdsSearchForCells() {
        return this.bdsSearchForCells;
    }

    public void setBdsSearchForCells(boolean bdsSearchForCells) {
        this.bdsSearchForCells = bdsSearchForCells;
    }

    public boolean isBdsSearchForCandidates() {
        return this.bdsSearchForCandidates;
    }

    public void setBdsSearchForCandidates(boolean bdsSearchForCandidates) {
        this.bdsSearchForCandidates = bdsSearchForCandidates;
    }

    public int getBdsSearchCandidatesAnz() {
        return this.bdsSearchCandidatesAnz;
    }

    public void setBdsSearchCandidatesAnz(int bdsSearchCandidatesAnz) {
        this.bdsSearchCandidatesAnz = bdsSearchCandidatesAnz;
    }

    public int getFishDisplayMode() {
        return this.fishDisplayMode;
    }

    public void setFishDisplayMode(int fishDisplayMode) {
        this.fishDisplayMode = fishDisplayMode;
    }

    public boolean isUseShiftForRegionSelect() {
        return this.useShiftForRegionSelect;
    }

    public void setUseShiftForRegionSelect(boolean useShiftForRegionSelect) {
        this.useShiftForRegionSelect = useShiftForRegionSelect;
    }

    public boolean isAllowUniquenessMissingCandidates() {
        return this.allowUniquenessMissingCandidates;
    }

    public void setAllowUniquenessMissingCandidates(boolean allowUniquenessMissingCandidates) {
        this.allowUniquenessMissingCandidates = allowUniquenessMissingCandidates;
    }

    public boolean isOnlySmallCursors() {
        return this.onlySmallCursors;
    }

    public void setOnlySmallCursors(boolean onlySmallCursors) {
        this.onlySmallCursors = onlySmallCursors;
    }

    public double getCursorFrameSize() {
        return this.cursorFrameSize;
    }

    public void setCursorFrameSize(double cursorFrameSize) {
        this.cursorFrameSize = cursorFrameSize;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public boolean isShowHintButtonsInToolbar() {
        return this.showHintButtonsInToolbar;
    }

    public void setShowHintButtonsInToolbar(boolean showHintButtonsInToolbar) {
        this.showHintButtonsInToolbar = showHintButtonsInToolbar;
    }

    public boolean isColorValues() {
        return this.colorValues;
    }

    public void setColorValues(boolean colorValues) {
        this.colorValues = colorValues;
    }

    public boolean isAlternativeMouseMode() {
        return this.alternativeMouseMode;
    }

    public void setAlternativeMouseMode(boolean alternativeMouseMode) {
        this.alternativeMouseMode = alternativeMouseMode;
    }

    public int getActLevel() {
        return this.actLevel;
    }

    public void setActLevel(int actLevel) {
        this.actLevel = actLevel;
    }

    public String[][] getNormalPuzzles() {
        return this.normalPuzzles;
    }

    public void setNormalPuzzles(String[][] normalPuzzles) {
        this.normalPuzzles = normalPuzzles;
    }

    public String[] getLearningPuzzles() {
        return this.learningPuzzles;
    }

    public void setLearningPuzzles(String[] learningPuzzles) {
        this.learningPuzzles = learningPuzzles;
    }

    public String[] getPractisingPuzzles() {
        return this.practisingPuzzles;
    }

    public void setPractisingPuzzles(String[] practisingPuzzles) {
        this.practisingPuzzles = practisingPuzzles;
    }

    public int getPractisingPuzzlesLevel() {
        return this.practisingPuzzlesLevel;
    }

    public void setPractisingPuzzlesLevel(int practisingPuzzlesLevel) {
        this.practisingPuzzlesLevel = practisingPuzzlesLevel;
    }

    public ArrayList<GeneratorPattern> getGeneratorPatterns() {
        return this.generatorPatterns;
    }

    public void setGeneratorPatterns(ArrayList<GeneratorPattern> generatorPatterns) {
        this.generatorPatterns = generatorPatterns;
    }

    public int getGeneratorPatternIndex() {
        return this.generatorPatternIndex;
    }

    public void setGeneratorPatternIndex(int generatorPatternIndex) {
        this.generatorPatternIndex = generatorPatternIndex;
    }

    public boolean isShowSudokuSolved() {
        return this.showSudokuSolved;
    }

    public void setShowSudokuSolved(boolean showSudokuSolved) {
        this.showSudokuSolved = showSudokuSolved;
    }

    public boolean isDeleteCursorDisplay() {
        return this.deleteCursorDisplay;
    }

    public void setDeleteCursorDisplay(boolean deleteCursorDisplay) {
        this.deleteCursorDisplay = deleteCursorDisplay;
    }

    public int getDeleteCursorDisplayLength() {
        return this.deleteCursorDisplayLength;
    }

    public void setDeleteCursorDisplayLength(int deleteCursorDisplayLength) {
        this.deleteCursorDisplayLength = deleteCursorDisplayLength;
    }

    public Color getAlternateCellColor() {
        return this.alternateCellColor;
    }

    public void setAlternateCellColor(Color alternateCellColor) {
        this.alternateCellColor = alternateCellColor;
    }

    public boolean isUseOrInsteadOfAndForFilter() {
        return this.useOrInsteadOfAndForFilter;
    }

    public void setUseOrInsteadOfAndForFilter(boolean useOrInsteadOfAndForFilter) {
        this.useOrInsteadOfAndForFilter = useOrInsteadOfAndForFilter;
    }

    public boolean isUseDefaultFontSize() {
        return this.useDefaultFontSize;
    }

    public void setUseDefaultFontSize(boolean useDefaultFontSize) {
        this.useDefaultFontSize = useDefaultFontSize;
    }

    public int getCustomFontSize() {
        return this.customFontSize;
    }

    public void setCustomFontSize(int customFontSize) {
        this.customFontSize = customFontSize;
    }

    public int getAllStepsAlsChainLength() {
        return this.allStepsAlsChainLength;
    }

    public void setAllStepsAlsChainLength(int allStepsAlsChainLength) {
        this.allStepsAlsChainLength = allStepsAlsChainLength;
    }

    public Color[] getColorKuColors() {
        return this.colorKuColors;
    }

    public void setColorKuColors(Color[] colorKuColors) {
        this.colorKuColors = colorKuColors;
    }

    public Color getColorKuColor(int n) {
        return n >= 1 && n <= this.colorKuColors.length ? this.colorKuColors[n - 1] : Color.black;
    }

    public boolean isColorCells() {
        return this.colorCells;
    }

    public void setColorCells(boolean colorCells) {
        this.colorCells = colorCells;
    }

    public boolean isAllStepsAlsChainForwardOnly() {
        return this.allStepsAlsChainForwardOnly;
    }

    public void setAllStepsAlsChainForwardOnly(boolean allStepsAlsChainForwardOnly) {
        this.allStepsAlsChainForwardOnly = allStepsAlsChainForwardOnly;
    }

    public DifficultyLevel nextDifficultyLevel(DifficultyLevel level) {
        int i = 0;
        i = 0;

        while (i < this.difficultyLevels.length && level != this.difficultyLevels[i]) {
            i++;
        }

        return i >= this.difficultyLevels.length - 1 ? null : this.difficultyLevels[i + 1];
    }

    public DifficultyLevel getDifficultyLevel(int ordinal) {
        int i = 0;
        i = 0;

        while (i < this.difficultyLevels.length && ordinal != this.difficultyLevels[i].getOrdinal()) {
            i++;
        }

        return i >= this.difficultyLevels.length ? null : this.difficultyLevels[i];
    }

    public StepConfig[] getOrgSolverSteps() {
        return this.orgSolverSteps;
    }

    public void setOrgSolverSteps(StepConfig[] orgSolverSteps) {
        this.orgSolverSteps = orgSolverSteps;
    }

    public int getRestrictChainLength() {
        return this.restrictChainLength;
    }

    public void setRestrictChainLength(int restrictChainLength) {
        this.restrictChainLength = restrictChainLength;
    }

    public int getRestrictNiceLoopLength() {
        return this.restrictNiceLoopLength;
    }

    public void setRestrictNiceLoopLength(int restrictNiceLoopLength) {
        this.restrictNiceLoopLength = restrictNiceLoopLength;
    }

    public boolean isRestrictChainSize() {
        return this.restrictChainSize;
    }

    public void setRestrictChainSize(boolean restrictChainSize) {
        this.restrictChainSize = restrictChainSize;
    }

    public int getMaxFins() {
        return this.maxFins;
    }

    public void setMaxFins(int maxFins) {
        this.maxFins = maxFins;
    }

    public int getMaxEndoFins() {
        return this.maxEndoFins;
    }

    public void setMaxEndoFins(int maxEndoFins) {
        this.maxEndoFins = maxEndoFins;
    }

    public boolean isCheckTemplates() {
        return this.checkTemplates;
    }

    public void setCheckTemplates(boolean checkTemplates) {
        this.checkTemplates = checkTemplates;
    }

    public boolean isShowCandidates() {
        return this.showCandidates;
    }

    public void setShowCandidates(boolean showCandidates) {
        this.showCandidates = showCandidates;
    }

    public boolean isShowWrongValues() {
        return this.showWrongValues;
    }

    public void setShowWrongValues(boolean showWrongValues) {
        this.showWrongValues = showWrongValues;
    }

    public boolean isShowDeviations() {
        return this.showDeviations;
    }

    public void setShowDeviations(boolean showDeviations) {
        this.showDeviations = showDeviations;
    }

    public int getDrawMode() {
        return this.drawMode;
    }

    public void setDrawMode(int drawMode) {
        this.drawMode = drawMode;
    }

    public Color getGridColor() {
        return this.gridColor;
    }

    public void setGridColor(Color gridColor) {
        this.gridColor = gridColor;
    }

    public Color getInnerGridColor() {
        return this.innerGridColor;
    }

    public void setInnerGridColor(Color innerGridColor) {
        this.innerGridColor = innerGridColor;
    }

    public Color getWrongValueColor() {
        return this.wrongValueColor;
    }

    public void setWrongValueColor(Color wrongValueColor) {
        this.wrongValueColor = wrongValueColor;
    }

    public Color getDeviationColor() {
        return this.deviationColor;
    }

    public void setDeviationColor(Color deviationColor) {
        this.deviationColor = deviationColor;
    }

    public Color getCellFixedValueColor() {
        return this.cellFixedValueColor;
    }

    public void setCellFixedValueColor(Color cellFixedValueColor) {
        this.cellFixedValueColor = cellFixedValueColor;
    }

    public Color getCellValueColor() {
        return this.cellValueColor;
    }

    public void setCellValueColor(Color cellValueColor) {
        this.cellValueColor = cellValueColor;
    }

    public Color getCandidateColor() {
        return this.candidateColor;
    }

    public void setCandidateColor(Color candidateColor) {
        this.candidateColor = candidateColor;
    }

    public Color getDefaultCellColor() {
        return this.defaultCellColor;
    }

    public void setDefaultCellColor(Color defaultCellColor) {
        this.defaultCellColor = defaultCellColor;
    }

    public Color getAktCellColor() {
        return this.aktCellColor;
    }

    public void setAktCellColor(Color aktCellColor) {
        this.aktCellColor = aktCellColor;
    }

    public Color getInvalidCellColor() {
        return this.invalidCellColor;
    }

    public void setInvalidCellColor(Color invalidCellColor) {
        this.invalidCellColor = invalidCellColor;
    }

    public Color getPossibleCellColor() {
        return this.possibleCellColor;
    }

    public void setPossibleCellColor(Color possibleCellColor) {
        this.possibleCellColor = possibleCellColor;
    }

    public Color getHintCandidateBackColor() {
        return this.hintCandidateBackColor;
    }

    public void setHintCandidateBackColor(Color hintCandidateBackColor) {
        this.hintCandidateBackColor = hintCandidateBackColor;
    }

    public Color getHintCandidateDeleteBackColor() {
        return this.hintCandidateDeleteBackColor;
    }

    public void setHintCandidateDeleteBackColor(Color hintCandidateDeleteBackColor) {
        this.hintCandidateDeleteBackColor = hintCandidateDeleteBackColor;
    }

    public Color getHintCandidateCannibalisticBackColor() {
        return this.hintCandidateCannibalisticBackColor;
    }

    public void setHintCandidateCannibalisticBackColor(Color hintCandidateCannibalisticBackColor) {
        this.hintCandidateCannibalisticBackColor = hintCandidateCannibalisticBackColor;
    }

    public Color getHintCandidateFinBackColor() {
        return this.hintCandidateFinBackColor;
    }

    public void setHintCandidateFinBackColor(Color hintCandidateFinBackColor) {
        this.hintCandidateFinBackColor = hintCandidateFinBackColor;
    }

    public Color getHintCandidateEndoFinBackColor() {
        return this.hintCandidateEndoFinBackColor;
    }

    public void setHintCandidateEndoFinBackColor(Color hintCandidateEndoFinBackColor) {
        this.hintCandidateEndoFinBackColor = hintCandidateEndoFinBackColor;
    }

    public Color getHintCandidateColor() {
        return this.hintCandidateColor;
    }

    public void setHintCandidateColor(Color hintCandidateColor) {
        this.hintCandidateColor = hintCandidateColor;
    }

    public Color getHintCandidateDeleteColor() {
        return this.hintCandidateDeleteColor;
    }

    public void setHintCandidateDeleteColor(Color hintCandidateDeleteColor) {
        this.hintCandidateDeleteColor = hintCandidateDeleteColor;
    }

    public Color getHintCandidateCannibalisticColor() {
        return this.hintCandidateCannibalisticColor;
    }

    public void setHintCandidateCannibalisticColor(Color hintCandidateCannibalisticColor) {
        this.hintCandidateCannibalisticColor = hintCandidateCannibalisticColor;
    }

    public Color getHintCandidateFinColor() {
        return this.hintCandidateFinColor;
    }

    public void setHintCandidateFinColor(Color hintCandidateFinColor) {
        this.hintCandidateFinColor = hintCandidateFinColor;
    }

    public Color getHintCandidateEndoFinColor() {
        return this.hintCandidateEndoFinColor;
    }

    public void setHintCandidateEndoFinColor(Color hintCandidateEndoFinColor) {
        this.hintCandidateEndoFinColor = hintCandidateEndoFinColor;
    }

    public Color[] getHintCandidateAlsBackColors() {
        return this.hintCandidateAlsBackColors;
    }

    public void setHintCandidateAlsBackColors(Color[] hintCandidateAlsBackColors) {
        this.hintCandidateAlsBackColors = hintCandidateAlsBackColors;
    }

    public Color[] getHintCandidateAlsColors() {
        return this.hintCandidateAlsColors;
    }

    public void setHintCandidateAlsColors(Color[] hintCandidateAlsColors) {
        this.hintCandidateAlsColors = hintCandidateAlsColors;
    }

    public Color getArrowColor() {
        return this.arrowColor;
    }

    public void setArrowColor(Color arrowColor) {
        this.arrowColor = arrowColor;
    }

    public double getValueFontFactor() {
        return this.valueFontFactor;
    }

    public void setValueFontFactor(double valueFontFactor) {
        this.valueFontFactor = valueFontFactor;
    }

    public double getCandidateFontFactor() {
        return this.candidateFontFactor;
    }

    public void setCandidateFontFactor(double candidateFontFactor) {
        this.candidateFontFactor = candidateFontFactor;
    }

    public double getHintBackFactor() {
        return this.hintBackFactor;
    }

    public void setHintBackFactor(double hintBackFactor) {
        this.hintBackFactor = hintBackFactor;
    }

    public Font getDefaultValueFont() {
        return this.defaultValueFont;
    }

    public void setDefaultValueFont(Font defaultValueFont) {
        this.defaultValueFont = defaultValueFont;
    }

    public Font getDefaultCandidateFont() {
        return this.defaultCandidateFont;
    }

    public void setDefaultCandidateFont(Font defaultCandidateFont) {
        this.defaultCandidateFont = defaultCandidateFont;
    }

    public Font getBigFont() {
        return this.bigFont;
    }

    public void setBigFont(Font bigFont) {
        this.bigFont = bigFont;
    }

    public Font getSmallFont() {
        return this.smallFont;
    }

    public void setSmallFont(Font smallFont) {
        this.smallFont = smallFont;
    }

    public DifficultyLevel[] getDifficultyLevels() {
        return this.difficultyLevels;
    }

    public void setDifficultyLevels(DifficultyLevel[] difficultyLevels) {
        this.difficultyLevels = difficultyLevels;
    }

    public String getDefaultFileDir() {
        return this.defaultFileDir;
    }

    public void setDefaultFileDir(String defaultFileDir) {
        this.defaultFileDir = defaultFileDir;
    }

    public int getMaxTableEntryLength() {
        return this.maxTableEntryLength;
    }

    public void setMaxTableEntryLength(int maxTableEntryLength) {
        this.maxTableEntryLength = maxTableEntryLength;
    }

    public int getAnzTableLookAhead() {
        return this.anzTableLookAhead;
    }

    public void setAnzTableLookAhead(int anzTableLookAhead) {
        this.anzTableLookAhead = anzTableLookAhead;
    }

    public Color[] getColoringColors() {
        return this.coloringColors;
    }

    public void setColoringColors(Color[] coloringColors1) {
        this.coloringColors = coloringColors1;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLaf() {
        return this.laf;
    }

    public void setLaf(String laf) {
        this.laf = laf;
    }

    public int getInitialHeight() {
        return this.initialHeight;
    }

    public void setInitialHeight(int initialHeight) {
        this.initialHeight = initialHeight;
    }

    public int getInitialWidth() {
        return this.initialWidth;
    }

    public void setInitialWidth(int initialWidth) {
        this.initialWidth = initialWidth;
    }

    public int getInitialVertDividerLoc() {
        return this.initialVertDividerLoc;
    }

    public void setInitialVertDividerLoc(int initialVertDividerLoc) {
        this.initialVertDividerLoc = initialVertDividerLoc;
    }

    public int getInitialHorzDividerLoc() {
        return this.initialHorzDividerLoc;
    }

    public void setInitialHorzDividerLoc(int initialHorzDividerLoc) {
        this.initialHorzDividerLoc = initialHorzDividerLoc;
    }

    public int getInitialDisplayMode() {
        return this.initialDisplayMode;
    }

    public void setInitialDisplayMode(int initialDisplayMode) {
        this.initialDisplayMode = initialDisplayMode;
    }

    public int getInitialXPos() {
        return this.initialXPos;
    }

    public void setInitialXPos(int initialXPos) {
        this.initialXPos = initialXPos;
    }

    public int getInitialYPos() {
        return this.initialYPos;
    }

    public void setInitialYPos(int initialYPos) {
        this.initialYPos = initialYPos;
    }

    public boolean isShowHintPanel() {
        return this.showHintPanel;
    }

    public void setShowHintPanel(boolean showHintPanel) {
        this.showHintPanel = showHintPanel;
    }

    public boolean isShowToolBar() {
        return this.showToolBar;
    }

    public void setShowToolBar(boolean showToolBar) {
        this.showToolBar = showToolBar;
    }

    public boolean isSaveWindowLayout() {
        return this.saveWindowLayout;
    }

    public void setSaveWindowLayout(boolean saveWindowLayout) {
        this.saveWindowLayout = saveWindowLayout;
    }

    public boolean isUseZeroInsteadOfDot() {
        return this.useZeroInsteadOfDot;
    }

    public void setUseZeroInsteadOfDot(boolean useZeroInsteadOfDot) {
        this.useZeroInsteadOfDot = useZeroInsteadOfDot;
    }

    public boolean isAllowErsWithOnlyTwoCandidates() {
        return this.allowErsWithOnlyTwoCandidates;
    }

    public void setAllowErsWithOnlyTwoCandidates(boolean allowErsWithOnlyTwoCandidates) {
        this.allowErsWithOnlyTwoCandidates = allowErsWithOnlyTwoCandidates;
    }

    public int getKrakenMaxFishType() {
        return this.krakenMaxFishType;
    }

    public void setKrakenMaxFishType(int krakenMaxFishType) {
        this.krakenMaxFishType = krakenMaxFishType;
    }

    public int getMaxKrakenFins() {
        return this.maxKrakenFins;
    }

    public void setMaxKrakenFins(int maxKrakenFins) {
        this.maxKrakenFins = maxKrakenFins;
    }

    public int getMaxKrakenEndoFins() {
        return this.maxKrakenEndoFins;
    }

    public void setMaxKrakenEndoFins(int maxKrakenEndoFins) {
        this.maxKrakenEndoFins = maxKrakenEndoFins;
    }

    public int getKrakenMaxFishSize() {
        return this.krakenMaxFishSize;
    }

    public void setKrakenMaxFishSize(int krakenMaxFishSize) {
        this.krakenMaxFishSize = krakenMaxFishSize;
    }

    public boolean isAllStepsSearchFish() {
        return this.allStepsSearchFish;
    }

    public void setAllStepsSearchFish(boolean allStepsSearchFish) {
        this.allStepsSearchFish = allStepsSearchFish;
    }

    public int getAllStepsMaxFishType() {
        return this.allStepsMaxFishType;
    }

    public void setAllStepsMaxFishType(int allStepsMaxFishType) {
        this.allStepsMaxFishType = allStepsMaxFishType;
    }

    public int getAllStepsMinFishSize() {
        return this.allStepsMinFishSize;
    }

    public void setAllStepsMinFishSize(int allStepsMinFishSize) {
        this.allStepsMinFishSize = allStepsMinFishSize;
    }

    public int getAllStepsMaxFishSize() {
        return this.allStepsMaxFishSize;
    }

    public void setAllStepsMaxFishSize(int allStepsMaxFishSize) {
        this.allStepsMaxFishSize = allStepsMaxFishSize;
    }

    public int getAllStepsMaxFins() {
        return this.allStepsMaxFins;
    }

    public void setAllStepsMaxFins(int allStepsMaxFins) {
        this.allStepsMaxFins = allStepsMaxFins;
    }

    public int getAllStepsMaxEndoFins() {
        return this.allStepsMaxEndoFins;
    }

    public void setAllStepsMaxEndoFins(int allStepsMaxEndoFins) {
        this.allStepsMaxEndoFins = allStepsMaxEndoFins;
    }

    public boolean isAllStepsCheckTemplates() {
        return this.allStepsCheckTemplates;
    }

    public void setAllStepsCheckTemplates(boolean allStepsCheckTemplates) {
        this.allStepsCheckTemplates = allStepsCheckTemplates;
    }

    public int getAllStepsKrakenMaxFishType() {
        return this.allStepsKrakenMaxFishType;
    }

    public void setAllStepsKrakenMaxFishType(int allStepsKrakenMaxFishType) {
        this.allStepsKrakenMaxFishType = allStepsKrakenMaxFishType;
    }

    public int getAllStepsKrakenMinFishSize() {
        return this.allStepsKrakenMinFishSize;
    }

    public void setAllStepsKrakenMinFishSize(int allStepsKrakenMinFishSize) {
        this.allStepsKrakenMinFishSize = allStepsKrakenMinFishSize;
    }

    public int getAllStepsKrakenMaxFishSize() {
        return this.allStepsKrakenMaxFishSize;
    }

    public void setAllStepsKrakenMaxFishSize(int allStepsKrakenMaxFishSize) {
        this.allStepsKrakenMaxFishSize = allStepsKrakenMaxFishSize;
    }

    public int getAllStepsMaxKrakenFins() {
        return this.allStepsMaxKrakenFins;
    }

    public void setAllStepsMaxKrakenFins(int allStepsMaxKrakenFins) {
        this.allStepsMaxKrakenFins = allStepsMaxKrakenFins;
    }

    public int getAllStepsMaxKrakenEndoFins() {
        return this.allStepsMaxKrakenEndoFins;
    }

    public void setAllStepsMaxKrakenEndoFins(int allStepsMaxKrakenEndoFins) {
        this.allStepsMaxKrakenEndoFins = allStepsMaxKrakenEndoFins;
    }

    public boolean isAllowDualsAndSiamese() {
        return this.allowDualsAndSiamese;
    }

    public void setAllowDualsAndSiamese(boolean allowDualsAndSiamese) {
        this.allowDualsAndSiamese = allowDualsAndSiamese;
    }

    public boolean isOnlyOneFishPerStep() {
        return this.onlyOneFishPerStep;
    }

    public void setOnlyOneFishPerStep(boolean onlyOneFishPerStep) {
        this.onlyOneFishPerStep = onlyOneFishPerStep;
    }

    public boolean isOnlyOneAlsPerStep() {
        return this.onlyOneAlsPerStep;
    }

    public void setOnlyOneAlsPerStep(boolean onlyOneAlsPerStep) {
        this.onlyOneAlsPerStep = onlyOneAlsPerStep;
    }

    public boolean isAllowAlsOverlap() {
        return this.allowAlsOverlap;
    }

    public void setAllowAlsOverlap(boolean allowAlsOverlap) {
        this.allowAlsOverlap = allowAlsOverlap;
    }

    public boolean isAllStepsOnlyOneAlsPerStep() {
        return this.allStepsOnlyOneAlsPerStep;
    }

    public void setAllStepsOnlyOneAlsPerStep(boolean allStepsOnlyOneAlsPerStep) {
        this.allStepsOnlyOneAlsPerStep = allStepsOnlyOneAlsPerStep;
    }

    public boolean isAllStepsAllowAlsOverlap() {
        return this.allStepsAllowAlsOverlap;
    }

    public void setAllStepsAllowAlsOverlap(boolean allStepsAllowAlsOverlap) {
        this.allStepsAllowAlsOverlap = allStepsAllowAlsOverlap;
    }

    public String getAllStepsFishCandidates() {
        return this.allStepsFishCandidates;
    }

    public void setAllStepsFishCandidates(String allStepsFishCandidates) {
        this.allStepsFishCandidates = allStepsFishCandidates;
    }

    public String getAllStepsKrakenFishCandidates() {
        return this.allStepsKrakenFishCandidates;
    }

    public void setAllStepsKrakenFishCandidates(String allStepsKrakenFishCandidates) {
        this.allStepsKrakenFishCandidates = allStepsKrakenFishCandidates;
    }

    public boolean isOnlyOneChainPerStep() {
        return this.onlyOneChainPerStep;
    }

    public void setOnlyOneChainPerStep(boolean onlyOneChainPerStep) {
        this.onlyOneChainPerStep = onlyOneChainPerStep;
    }

    public boolean isAllowAlsInTablingChains() {
        return this.allowAlsInTablingChains;
    }

    public void setAllowAlsInTablingChains(boolean allowAlsInTablingChains) {
        this.allowAlsInTablingChains = allowAlsInTablingChains;
    }

    public boolean isAllStepsAllowAlsInTablingChains() {
        return this.allStepsAllowAlsInTablingChains;
    }

    public void setAllStepsAllowAlsInTablingChains(boolean allStepsAllowAlsInTablingChains) {
        this.allStepsAllowAlsInTablingChains = allStepsAllowAlsInTablingChains;
    }

    public int getHistorySize() {
        return this.historySize;
    }

    public void setHistorySize(int aHistorySize) {
        this.historySize = aHistorySize;
    }

    public boolean isShowColorKu() {
        return this.showColorKu;
    }

    public void setShowColorKu(boolean showColorKu) {
        this.showColorKu = showColorKu;
    }

    public String getDefaultImageDir() {
        return this.defaultImageDir;
    }

    public void setDefaultImageDir(String defaultImageDir) {
        this.defaultImageDir = defaultImageDir;
    }

    public boolean isShowColorKuAct() {
        return this.showColorKuAct;
    }

    public void setShowColorKuAct(boolean showColorKuAct) {
        this.showColorKuAct = showColorKuAct;
    }

    public boolean isOnlySmallFilters() {
        return this.onlySmallFilters;
    }

    public void setOnlySmallFilters(boolean onlySmallFilters) {
        this.onlySmallFilters = onlySmallFilters;
    }

    public boolean isEditModeAutoAdvance() {
        return this.editModeAutoAdvance;
    }

    public void setEditModeAutoAdvance(boolean editModeAutoAdvance) {
        this.editModeAutoAdvance = editModeAutoAdvance;
    }

    public double getBoxLineFactor() {
        return this.boxLineFactor;
    }

    public void setBoxLineFactor(double boxLineFactor) {
        this.boxLineFactor = boxLineFactor;
    }

    private static class ProgressComparator implements Comparator<StepConfig> {
        private ProgressComparator() {
        }

        public int compare(StepConfig o1, StepConfig o2) {
            return o1.getIndexProgress() - o2.getIndexProgress();
        }
    }
}
