package sudoku;

import generator.BackgroundGeneratorThread;
import solver.SudokuSolver;
import solver.SudokuSolverFactory;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.logging.*;

public class Main {
    public static String OS_NAME = "";
    private static List<Logger> loggers = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        Handler fh = new FileHandler("%t/hodoku.log", false);
        fh.setFormatter(new SimpleFormatter());
        fh.setLevel(Level.SEVERE);
        Logger rootLogger = Logger.getLogger("");
        rootLogger.addHandler(fh);
        rootLogger.setLevel(Level.CONFIG);
        Handler[] handlers = rootLogger.getHandlers();

        for (Handler handler : handlers) {
            if (handler instanceof ConsoleHandler) {
                handler.setLevel(Level.ALL);
            }

            handler.setLevel(Level.ALL);
        }

        Logger logger = null;
        loggers.add(Logger.getLogger(SudokuSolver.class.getName()));
        Logger.getLogger(Main.class.getName()).log(Level.CONFIG, "java.io.tmpdir={0}", System.getProperty("java.io.tmpdir"));
        Logger.getLogger(Main.class.getName()).log(Level.CONFIG, "user.dir={0}", System.getProperty("user.dir"));
        Logger.getLogger(Main.class.getName()).log(Level.CONFIG, "user.home={0}", System.getProperty("user.home"));
        Logger.getLogger(Main.class.getName()).log(Level.CONFIG, "launch4j.exedir={0}", System.getProperty("launch4j.exedir"));
        OS_NAME = System.getProperty("os.name");
        if (OS_NAME != null) {
            OS_NAME = OS_NAME.toLowerCase();
        }

        Options.getInstance();
        String path = System.getProperty("launch4j.exedir");
        if (path == null) {
            URL startURL = Main.class.getResource("/sudoku/Main.class");
            path = startURL.getPath();
            Logger.getLogger(Main.class.getName()).log(Level.CONFIG, "Startup path: {0}", path);
            if (path.contains(".jar!")) {
                int startIndex = 5;
                if (OS_NAME.startsWith("windows")) {
                    startIndex = 6;
                }

                String tmp = path.substring(startIndex, path.indexOf(".jar!"));
                int index = tmp.lastIndexOf(47);
                if (index > 0) {
                    path = tmp.substring(0, index);
                }
            } else if (path.contains("/build/classes")) {
                int startIndex = 0;
                if (OS_NAME.startsWith("windows")) {
                    startIndex = 1;
                }

                path = path.substring(startIndex, path.indexOf("/build/classes"));
            } else if (path.contains("/bin/sudoku/Main")) {
                int startIndex = 0;
                if (OS_NAME.startsWith("windows")) {
                    startIndex = 1;
                }

                path = path.substring(startIndex, path.indexOf("/bin/sudoku/Main"));
            }

            path = path.replaceAll("%20", " ");
        }

        File configFile = new File(path + File.separator + "hodoku.hcfg");
        boolean needToResetPuzzles = false;
        if (configFile.exists()) {
            Logger.getLogger(Main.class.getName()).log(Level.CONFIG, "Reading options from {0}", configFile.getPath());
            Options.readOptions(configFile.getPath());
            needToResetPuzzles = true;
        } else {
            Logger.getLogger(Main.class.getName()).log(Level.CONFIG, "No config file found: <{0}>", configFile.getPath());
        }

        if (!Options.getInstance().getLanguage().isEmpty()) {
            Locale.setDefault(new Locale(Options.getInstance().getLanguage()));
        }

        Options.getInstance().resetDifficultyLevelStrings();
        SudokuUtil.setLookAndFeel();
        boolean launch4jUsed = false;
        boolean launchGui = false;
        String launchFile = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("/launch4j")) {
                launch4jUsed = true;
            }

            if (args[i].equalsIgnoreCase("/gui")) {
                launchGui = true;
            }

            if (args[i].toLowerCase().endsWith("hsol") || args[i].toLowerCase().endsWith("hcfg")) {
                launchFile = args[i];
            }
        }

        SudokuConsoleFrame consoleFrame = null;
        if (!launchGui && (launch4jUsed && args.length > 1 || !launch4jUsed && args.length > 0)) {
            if (launch4jUsed) {
                consoleFrame = new SudokuConsoleFrame();
                consoleFrame.setVisible(true);
            }

            System.out.println("HoDoKu - v2.2.0 - " + MainFrame.BUILD);
            System.out
                    .println(
                            "Copyright (C) 2008-12  Bernhard Hobiger\r\n\r\nHoDoKu is free software: you can redistribute it and/or modify\r\nit under the terms of the GNU General Public License as published by\r\nthe Free Software Foundation, either version 3 of the License, or\r\n(at your option) any later version.\r\n\r\n"
                    );
            List<String> options = new ArrayList<>();

            for (int i = 0; i < args.length; i++) {
                if (!args[i].equals("/launch4j")) {
                    if (args[i].equals("/f")) {
                        if (i + 1 >= args.length) {
                            System.out.println("No options file given: /f ignored");
                        } else {
                            try {
                                System.out.println("reading options from file '" + args[i + 1] + "'");
                                BufferedReader in = new BufferedReader(new FileReader(args[i + 1]));
                                StringBuilder tmpOptions = new StringBuilder();
                                String line = null;

                                while ((line = in.readLine()) != null) {
                                    tmpOptions.append(line.trim()).append(" ");
                                }

                                in.close();
                                String[] tmpOptionsArray = getOptionsFromStringBuilder(tmpOptions);

                                for (int j = 0; j < tmpOptionsArray.length; j++) {
                                    String opt = tmpOptionsArray[j].trim();
                                    if (!opt.isEmpty()) {
                                        options.add(tmpOptionsArray[j]);
                                    }
                                }
                            } catch (Exception ex) {
                                System.out.println("Can't read from file '" + args[i + 1] + "': /f ignored");
                            }

                            i++;
                        }
                    } else if (args[i].equals("/stdin")) {
                        try {
                            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                            StringBuilder tmpOptions = new StringBuilder();
                            String line = null;

                            while ((line = in.readLine()) != null) {
                                tmpOptions.append(line.trim()).append(" ");
                            }

                            in.close();
                            String[] tmpOptionsArray = getOptionsFromStringBuilder(tmpOptions);

                            for (int j = 0; j < tmpOptionsArray.length; j++) {
                                String opt = tmpOptionsArray[j].trim();
                                if (!opt.isEmpty()) {
                                    options.add(tmpOptionsArray[j]);
                                }
                            }
                        } catch (Exception ex) {
                            System.out.println("Can't read from stdin: /stdin ignored");
                        }
                    } else {
                        options.add(args[i]);
                    }
                }
            }

            if (consoleFrame != null) {
                consoleFrame.setIn();
            }

            String puzzleString = null;
            Map<String, String> argMap = new TreeMap<>();
            String[] var34 = null;

            for (int i = 0; i < options.size(); i++) {
                String arg = options.get(i).trim().toLowerCase();
                if (arg.equals("/bs")
                        || arg.equals("/vg")
                        || arg.equals("/sc")
                        || arg.equals("/sl")
                        || arg.equals("/so")
                        || arg.equals("/c")
                        || arg.equals("/o")
                        || arg.equals("/bsaf")
                        || arg.equals("/bts")
                        || arg.equals("/bt")
                        || arg.equals("/test")
                        || arg.equals("/testf")
                        || arg.equals("/vf")
                        || arg.equals("/s") && i + 1 < options.size() && options.get(i + 1).trim().charAt(0) != '/') {
                    if (i + 1 < options.size() && options.get(i + 1).trim().charAt(0) != '/') {
                        if (arg.equals("/s")) {
                            argMap.put("/s", null);
                            argMap.put("/sc", options.get(i + 1));
                        } else {
                            argMap.put(arg, options.get(i + 1));
                        }

                        i++;
                    } else {
                        System.out.println("No value for parameter: '" + arg + "' ignored!");
                    }
                } else if (arg.charAt(0) == '/') {
                    argMap.put(arg, null);
                } else {
                    puzzleString = arg;
                }
            }

            String helpArg = null;
            if (argMap.containsKey("/h")) {
                helpArg = "/h";
            }

            if (argMap.containsKey("/?")) {
                helpArg = "/?";
            }

            if (helpArg != null) {
                printHelpScreen();
                printIgnoredOptions(helpArg, argMap);
                if (consoleFrame == null) {
                    System.exit(0);
                }
            } else if (argMap.containsKey("/testf")) {
                RegressionTester tester = new RegressionTester();
                tester.runTest(argMap.get("/testf"), true);
                if (consoleFrame == null) {
                    System.exit(0);
                }
            } else if (argMap.containsKey("/test")) {
                RegressionTester tester = new RegressionTester();
                tester.runTest(argMap.get("/test"));
                if (consoleFrame == null) {
                    System.exit(0);
                }
            } else if (argMap.containsKey("/lt")) {
                printIgnoredOptions("/lt", argMap);
                SortedMap<String, String> tmpMap = new TreeMap<>();

                for (SolutionType tmpType : SolutionType.values()) {
                    tmpMap.put(tmpType.getStepName(), tmpType.getArgName());
                }

                System.out.println("List of Techniques:");

                for (String stepName : tmpMap.keySet()) {
                    System.out.printf("%6s:%s\r\n", tmpMap.get(stepName), stepName);
                }

                System.out.println("Done!");
                if (consoleFrame == null) {
                    System.exit(0);
                }
            } else {
                if (argMap.containsKey("/c")) {
                    String fileName = argMap.get("/c");
                    if (fileName.toLowerCase().equals("default")) {
                        System.out.println("Using default config!");
                        Options.resetAll();
                        Options.getInstance();
                    } else {
                        System.out.println("Using configuration file '" + fileName + "'");
                        Options.readOptions(fileName);
                    }

                    argMap.remove("/c");
                }

                String outFile = null;
                if (argMap.containsKey("/o")) {
                    outFile = argMap.get("/o");
                    argMap.remove("/o");
                    if (outFile.equals("stdout")) {
                        System.out.println("Writing output to console");
                    } else {
                        System.out.println("Using output file '" + outFile + "'");
                    }
                }

                List<StepType> typeList = new ArrayList<>();
                if (argMap.containsKey("/sc")) {
                    String[] steps = argMap.get("/sc").toLowerCase().split(",");

                    for (int i = 0; i < steps.length; i++) {
                        StepType.parseTypeStr(typeList, steps[i]);
                    }

                    argMap.remove("/sc");
                }

                DifficultyLevel actLevel = null;
                if (argMap.containsKey("/sl")) {
                    int levelOrd = -1;

                    try {
                        levelOrd = Integer.parseInt(argMap.get("/sl"));
                        actLevel = Options.getInstance().getDifficultyLevel(levelOrd + 1);

                        for (StepType type : typeList) {
                            if (type.type.getStepConfig().getLevel() > levelOrd + 1) {
                                System.out
                                        .println("Invalid argument for option /sl: " + type.type.getStepName() + " requires at least difficulty level " + (levelOrd + 1));
                                if (consoleFrame == null) {
                                    System.exit(0);
                                }

                                return;
                            }
                        }
                    } catch (NumberFormatException ex) {
                        System.out.println("Invalid argument for option /sl: " + argMap.get("/sl") + " - option ignored!");
                        if (consoleFrame == null) {
                            System.exit(0);
                        }

                        return;
                    }

                    argMap.remove("/sl");
                }

                if (argMap.containsKey("/so")) {
                    printIgnoredOptions("/so", argMap);
                    new Main().sortPuzzleFile(argMap.get("/so"), typeList, outFile);
                    if (consoleFrame == null) {
                        System.exit(0);
                    }
                } else if (argMap.containsKey("/s")) {
                    printIgnoredOptions("/s", argMap);
                    if (typeList.isEmpty() && actLevel == null) {
                        System.out.println("No step name given and no difficulty level set!");
                        if (consoleFrame == null) {
                            System.exit(0);
                        }
                    } else {
                        new Main().searchForType(typeList, actLevel, outFile);
                        if (consoleFrame == null) {
                            System.exit(0);
                        }
                    }
                } else {
                    boolean printSolution = false;
                    if (argMap.containsKey("/vs")) {
                        printSolution = true;
                        argMap.remove("/vs");
                    }

                    boolean printSolutionPath = false;
                    if (argMap.containsKey("/vp")) {
                        printSolutionPath = true;
                        argMap.remove("/vp");
                    }

                    boolean printStatistics = false;
                    if (argMap.containsKey("/vst")) {
                        printStatistics = true;
                        argMap.remove("/vst");
                    }

                    if (argMap.containsKey("/vf")) {
                        String arg = argMap.get("/vf");
                        int fishFormat = 0;

                        try {
                            fishFormat = Integer.parseInt(arg);
                        } catch (NumberFormatException ex) {
                            System.out.println("Invalid argument for /vf ('" + arg + "'): '0' used instead!");
                        }

                        Options.getInstance().setFishDisplayMode(fishFormat);
                        argMap.remove("/vf");
                    }

                    ClipboardMode clipboardMode = null;
                    Set<SolutionType> outTypes = null;
                    if (argMap.containsKey("/vg") && printSolutionPath) {
                        String types = argMap.get("/vg").toLowerCase();
                        if (types.charAt(1) == ':') {
                            switch (types.charAt(0)) {
                                case 'c':
                                    clipboardMode = ClipboardMode.PM_GRID;
                                    break;
                                case 'l':
                                    clipboardMode = ClipboardMode.LIBRARY;
                                    break;
                                case 's':
                                    clipboardMode = ClipboardMode.PM_GRID_WITH_STEP;
                                    break;
                                default:
                                    System.out.println("Invalid argument ('" + types.charAt(1) + "'): 'c' used instead!");
                                    clipboardMode = ClipboardMode.PM_GRID;
                            }

                            types = types.substring(2);
                        } else {
                            System.out.println("No output mode set for '/vg': 'c' used as default!");
                            clipboardMode = ClipboardMode.PM_GRID;
                        }

                        String[] typesArr = types.split(",");

                        for (int i = 0; i < typesArr.length; i++) {
                            StepConfig[] steps = Options.getInstance().solverSteps;
                            boolean typeFound = false;

                            for (int j = 0; j < steps.length; j++) {
                                if (steps[j].getType().getArgName().equals(typesArr[i])) {
                                    if (outTypes == null) {
                                        outTypes = EnumSet.noneOf(SolutionType.class);
                                    }

                                    outTypes.add(steps[j].getType());
                                    typeFound = true;
                                    break;
                                }
                            }

                            if (!typeFound) {
                                System.out.println("Invalid solution type set for '/vg' (" + typesArr[i] + "): ignored!");
                            }
                        }

                        if (outTypes == null || outTypes.isEmpty()) {
                            System.out.println("No solution type set for '/vg': option ignored!");
                            clipboardMode = null;
                            outTypes = null;
                        }

                        argMap.remove("/vg");
                    }

                    if (argMap.containsKey("/bs")) {
                        printIgnoredOptions("/bs", argMap);
                        String fileName = argMap.get("/bs");
                        new Main().batchSolve(fileName, null, printSolution, printSolutionPath, printStatistics, clipboardMode, outTypes, outFile, false);
                        if (consoleFrame == null) {
                            System.exit(0);
                        }
                    } else if (argMap.containsKey("/bsaf")) {
                        printIgnoredOptions("/bsaf", argMap);
                        String fileName = argMap.get("/bsaf");
                        new Main().batchSolve(fileName, null, printSolution, printSolutionPath, printStatistics, clipboardMode, outTypes, outFile, true);
                        if (consoleFrame == null) {
                            System.exit(0);
                        }
                    } else if (argMap.containsKey("/bsa")) {
                        printIgnoredOptions("/bsa", argMap);
                        System.out.println("bsa: started");
                        if (puzzleString == null) {
                            System.out.println("No puzzle given with /bsa - ignored!");
                            if (consoleFrame == null) {
                                System.exit(0);
                            }
                        } else {
                            new Main().batchSolve(null, puzzleString, printSolution, printSolutionPath, printStatistics, clipboardMode, outTypes, outFile, true);
                            if (consoleFrame == null) {
                                System.exit(0);
                            }
                        }
                    } else {
                        List<SolutionType> testTypes = new ArrayList<>();
                        if (argMap.containsKey("/bts")) {
                            String testArgType = argMap.get("/bts");
                            String[] tmpTypes = testArgType.split(",");

                            for (int i = 0; i < tmpTypes.length; i++) {
                                SolutionType tmp = SolutionType.getTypeFromArgName(tmpTypes[i]);
                                if (tmp == null) {
                                    System.out.println("Invalid step: " + tmpTypes[i] + " with /bts - ignored!");
                                } else {
                                    testTypes.add(tmp);
                                }
                            }

                            if (testTypes.isEmpty()) {
                                System.out.println("Invalid step(s): <" + testArgType + "> with /bts - ignored!");
                            }

                            argMap.remove("/bts");
                        }

                        if (argMap.containsKey("/bt")) {
                            printIgnoredOptions("/bt", argMap);
                            String fileName = argMap.get("/bt");
                            if (testTypes.isEmpty()) {
                                System.out.println("/bt: nothing to do!");
                                if (consoleFrame == null) {
                                    System.exit(0);
                                }
                            } else {
                                new Main().batchSolve(fileName, null, false, false, true, clipboardMode, outTypes, outFile, false, true, testTypes);
                                if (consoleFrame == null) {
                                    System.exit(0);
                                }
                            }
                        } else if (puzzleString != null) {
                            printIgnoredOptions("", argMap);
                            new Main().batchSolve(null, puzzleString, printSolution, printSolutionPath, printStatistics, clipboardMode, outTypes, outFile, false);
                            if (consoleFrame == null) {
                                System.exit(0);
                            }
                        } else {
                            printIgnoredOptions("", argMap);
                            System.out.println("Don't know what to do...");
                            printHelpScreen();
                            if (consoleFrame == null) {
                                System.exit(0);
                            }
                        }
                    }
                }
            }
        } else {
            if (needToResetPuzzles) {
                BackgroundGeneratorThread.getInstance().resetAll();
            }

            final String lf = launchFile;
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new MainFrame(lf).setVisible(true);
                }
            });
        }
    }

    private static void printIgnoredOptions(String option, Map<String, String> argMap) {
        StringBuilder tmp = new StringBuilder();
        boolean found = false;

        for (String key : argMap.keySet()) {
            if (!key.equals(option)) {
                found = true;
                tmp.append(key);
                tmp.append(" ");
            }
        }

        if (found) {
            System.out.println("The following options were ignored: " + tmp.toString().trim());
        }
    }

    private static String[] getOptionsFromStringBuilder(StringBuilder in) {
        List<String> options = new ArrayList<>();
        char qualifier = '"';
        boolean qualifierSeen = false;
        int startIndex = -1;

        for (int i = 0; i < in.length(); i++) {
            char ch = in.charAt(i);
            if (ch == ' ') {
                if (qualifierSeen) {
                    continue;
                }

                if (startIndex != -1) {
                    options.add(in.substring(startIndex, i));
                    startIndex = -1;
                }
            }

            if (qualifierSeen && ch == qualifier) {
                if (i < in.length() - 1 && in.charAt(i + 1) == qualifier) {
                    in.delete(i, i);
                    i++;
                } else {
                    options.add(in.substring(startIndex, i));
                    startIndex = -1;
                    qualifierSeen = false;
                }
            }

            if (!qualifierSeen && (ch == '"' || ch == '\'')) {
                if (startIndex != -1) {
                    options.add(in.substring(startIndex, i));
                }

                startIndex = i + 1;
                qualifier = ch;
                qualifierSeen = true;
            }

            if (ch != ' ' && startIndex == -1) {
                startIndex = i;
            }
        }

        if (startIndex != -1 && startIndex < in.length()) {
            options.add(in.substring(startIndex, in.length()));
        }

        return options.toArray(new String[0]);
    }

    private static void printHelpScreen() {
        System.out
                .println(
                        "Usage: java -Xmx512m -jar hodoku.jar [options] [puzzle]\r\n\r\nOptions:\r\n  /h, /?: print this help screen\r\n  /f <file>: read options from file <file>\r\n  /c <hcfg file | 'default'>: use <file> for this console run\r\n      (current config of GUI program is not changed)\r\n  /lt: list internal names of techniques\r\n  /so <file>: sort puzzle file created with /s, write output to <file>.out.txt\r\n      or to a file given by /o; a filter can be applied with /sc\r\n  /s: create puzzles which contain steps according to /sc and/or /sl\r\n      and write them to <step>[_<step>...].txt or a file given by /o\r\n      (for compatibility reasons steps can be defined directly with /s)\r\n  /sc <step>[:0|1|2|3][+[e|l|g]n][,[-]<step>[:0|1|2|3][+[e|l|g]n]...]: define\r\n      puzzle properties for /s or /so\r\n      <step> is an internal name according to /lt or \"all\" (all steps except\r\n          singles), \"nssts\" (all steps except SSTS: singles, h2, h3, h4, n2,\r\n          n3, n4, l2, l3, lc1, lc2, bf2, bf3, bf4, xy, sc, mc) or \r\n          \"nssts1\" (nssts minus 2sk, sk, bug1, er, w, u1, xyz, rp)\r\n      -: exclude the step (not allowed with first <step> definition)\r\n      0: x <step> x (default)\r\n      1: ssts <step> ssts\r\n      2: ssts <step> s\r\n      3: s <step> s\r\n      with: 'x' - arbitrary steps, 'ssts' - SSTS, 's' - singles\r\n      +[e|l|g]n: number of candidates, <step> has to eliminate, equals |\r\n          is less than | is greater than n\r\n  /sl <level>: create only puzzles with difficulty level <level>\r\n      0: easy; 1: medium; 2: hard; 3: unfair; 4: extreme\r\n  /bs <file>: batch solve puzzles in <file> (output written to <file>.out.txt\r\n       or a file given by /o)\r\n  /bsaf <file>: batch process puzzles in <file> (output as in /bs);\r\n       for each puzzle \"Find all Steps\" is executed\r\n  /bsa: execute \"Find all Steps\" for [puzzle] (output written to\r\n       <file>.out.txt or a file given by /o)\r\n  /bt <file>: batch test using puzzle collection in <file> (output as in /bs)\r\n  /bts <step>[,<step>...]: find all occurences of <step> after any non single\r\n      step and check all eliminations against the solution of the puzzle\r\n  /vs: print solution in output file (only valid with /bs)\r\n  /vp: print complete solution for each puzzle (only valid with /bs)\r\n  /vst: print statistics (only valid with /bs)\r\n  /vf <0|1|2>: set fish output format (default, numbers, cells)\r\n  /vg [l|c|s:]<step>[,<step>...]: print pm before every <step> in the solution\r\n      (only valid with /bs and /vp)\r\n      l: print library format\r\n      c: print candidate grid\r\n      s: print candidate grid with step highlighted\r\n  /o <file>: write output to <file>; if <file> is \"stdout\", all output is\r\n      written to the console\r\n  /stdin: read options from stdin\r\n  /test <file>: run regression tester against test cases in <file>\r\n  /testf <file>: same as /test, but long running tests are ommitted\r\n\r\nPuzzle: If a puzzle is given it is solved as if it was read from a file with\r\n      /bs; if a PM is given it must be delimited by \" or '"
                );
    }

    public String getSrcDir() {
        String path = this.getClass().getClassLoader().getResource("sudoku").toExternalForm().toLowerCase();
        if (path.startsWith("jar")) {
            path = path.substring(10, path.indexOf("hodoku.jar"));
        } else {
            path = path.substring(6, path.indexOf("build"));
        }

        return path;
    }

    void searchForType(List<StepType> typeList, DifficultyLevel level, String outFile) {
        System.out.println("Starting search for:");
        if (typeList.size() > 0) {
            for (StepType tmpType : typeList) {
                System.out.println("   " + tmpType);
            }
        }

        if (level != null) {
            System.out.println("   " + level.getName());
        }

        SearchForTypeThread thread = new SearchForTypeThread(this, typeList, level, outFile);
        thread.start();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        try {
            String line = null;

            while ((line = in.readLine()) != null && line.compareTo("q") != 0) {
            }
        } catch (IOException ex) {
            System.out.println("Error reading from console");
        }

        thread.interrupt();

        try {
            thread.join();
        } catch (InterruptedException ex) {
            System.out.println("Interrupted waiting for search thread");
        }

        System.out.println("Gesamt: " + thread.getAnz() + " Sudoku erzeugt (" + thread.getAnzFound() + " Treffer)");
    }

    public void batchSolve(
            String fileName,
            String puzzleString,
            boolean printSolution,
            boolean printSolutionPath,
            boolean printStatistic,
            ClipboardMode cMode,
            Set<SolutionType> types,
            String outFile,
            boolean findAllSteps
    ) {
        this.batchSolve(fileName, puzzleString, printSolution, printSolutionPath, printStatistic, cMode, types, outFile, findAllSteps, false, null);
    }

    public void batchSolve(
            String fileName,
            String puzzleString,
            boolean printSolution,
            boolean printSolutionPath,
            boolean printStatistic,
            ClipboardMode cMode,
            Set<SolutionType> types,
            String outFile,
            boolean findAllSteps,
            boolean bruteForceTest,
            List<SolutionType> testTypes
    ) {
        BatchSolveThread thread = new BatchSolveThread(
                fileName, puzzleString, printSolution, printSolutionPath, printStatistic, cMode, types, outFile, findAllSteps, bruteForceTest, testTypes
        );
        thread.start();
        ShutDownThread st = new ShutDownThread(thread);
        Runtime.getRuntime().addShutdownHook(st);

        try {
            thread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "join interrupted...", ex);
        }

        try {
            Runtime.getRuntime().removeShutdownHook(st);
        } catch (Exception ex) {
        }

        int min = (int) (thread.getTicks() / 60000L);
        int sec = (int) (thread.getTicks() % 60000L);
        int ms = sec % 1000;
        sec /= 1000;
        int hours = min / 60;
        min -= hours * 60;
        System.out.printf("%d puzzles in %dms (%d:%02d:%02d.%03d)\r\n", thread.getCount(), thread.getTicks(), hours, min, sec, ms);
        System.out.printf("%.03f ms per puzzle\r\n", (double) thread.getTicks() / thread.getCount());
        System.out.println(thread.getBruteForceAnz() + " puzzles require guessing!");
        System.out.println(thread.getTemplateAnz() + " puzzles require templates!");
        System.out.println(thread.getGivenUpAnz() + " puzzles unsolved!");
        System.out.println(thread.getUnsolvedAnz() + " puzzles not solved logically!");
        System.out.println();

        for (int i = 1; i < thread.getResultLength(); i++) {
            System.out.println("   " + Options.DEFAULT_DIFFICULTY_LEVELS[i].getName() + ": " + thread.getResult(i));
        }

        if (printStatistic) {
            System.out.println();

            try {
                thread.printStatistic(null, false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            SudokuSolverFactory.getDefaultSolverInstance().getStepFinder().printStatistics();
        }
    }

    void sortPuzzleFile(String fileName, List<StepType> typeList, String outFileName) {
        try {
            if (typeList.size() > 0) {
                System.out.println("Filter:");

                for (StepType tmpType : typeList) {
                    System.out.println("   " + tmpType);
                }
            }

            BufferedReader in = new BufferedReader(new FileReader(fileName));
            BufferedWriter out = null;
            if (outFileName == null) {
                outFileName = fileName + ".out.txt";
            }

            if (!outFileName.equals("stdout")) {
                out = new BufferedWriter(new FileWriter(outFileName));
            }

            List<String> puzzleList = new ArrayList<>();
            String line = null;
            int gesAnz = 0;

            while (true) {
                boolean includePuzzle;
                label116:
                while (true) {
                    if ((line = in.readLine()) == null) {
                        in.close();
                        includePuzzle = puzzleList.size() > 0;
                        Collections.sort(puzzleList, new Comparator<String>() {
                            public int compare(String s1, String s2) {
                                int index1 = s1.indexOf(35);
                                int index2 = s2.indexOf(35);
                                if (index1 == -1 && index2 == -1) {
                                    return s1.compareTo(s2);
                                } else if (index1 == -1 && index2 != -1) {
                                    return -1;
                                } else {
                                    return index1 != -1 && index2 == -1 ? 1 : s1.substring(index1).compareTo(s2.substring(index2));
                                }
                            }
                        });

                        for (String key : puzzleList) {
                            if (out != null) {
                                out.write(key);
                                out.newLine();
                            } else {
                                System.out.println(key);
                            }
                        }

                        if (out != null) {
                            out.close();
                        }

                        System.out.println(includePuzzle + " puzzles sorted (" + gesAnz + ")!");
                        return;
                    }

                    gesAnz++;
                    includePuzzle = true;
                    if (!line.contains("#") || typeList.size() <= 0) {
                        break;
                    }

                    includePuzzle = false;
                    String inputStr = line.substring(line.indexOf(35) + 1).trim();
                    int puzzleType = 3;
                    String[] types = inputStr.split(" ");

                    for (int i = 0; i < types.length; i++) {
                        if (types[i].equals("x")) {
                            puzzleType = 0;
                            break;
                        }
                    }

                    if (puzzleType == 3) {
                        if (inputStr.startsWith("ssts")) {
                            puzzleType = 2;
                        }

                        if (inputStr.endsWith("ssts")) {
                            puzzleType = 1;
                        }
                    }

                    String typeStr = null;
                    int compAnz = 0;
                    String[] parts = inputStr.split(" ");
                    if (parts.length > 1) {
                        typeStr = parts[1];
                        int index1 = typeStr.indexOf(40);
                        int index2 = typeStr.indexOf(41);
                        String orgTypeStr = typeStr;
                        if (index1 != -1) {
                            typeStr = typeStr.substring(0, index1);
                            if (index2 != -1) {
                                String anzStr = orgTypeStr.substring(index1 + 1, index2);
                                if (anzStr.length() > 0) {
                                    compAnz = Integer.parseInt(anzStr);
                                }
                            }
                        }

                        Iterator i$ = typeList.iterator();

                        while (true) {
                            if (!i$.hasNext()) {
                                break label116;
                            }

                            StepType actType = (StepType) i$.next();
                            if (typeStr.equals(actType.type.getArgName()) && puzzleType >= actType.puzzleType) {
                                switch (actType.compType) {
                                    case 0:
                                        if (compAnz == actType.compAnz) {
                                            includePuzzle = true;
                                        }
                                        break;
                                    case 1:
                                        if (compAnz < actType.compAnz) {
                                            includePuzzle = true;
                                        }
                                        break;
                                    case 2:
                                        if (compAnz > actType.compAnz) {
                                            includePuzzle = true;
                                        }
                                        break;
                                    default:
                                        includePuzzle = true;
                                }
                            }

                            if (includePuzzle) {
                                break label116;
                            }
                        }
                    }
                }

                if (includePuzzle) {
                    puzzleList.add(line);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Error sorting puzzle file", ex);
        }
    }
}
