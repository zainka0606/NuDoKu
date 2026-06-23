package sudoku;

import java.util.List;

class StepType {
    static final int UNDEFINED = -1;
    static final int EQUAL = 0;
    static final int LT = 1;
    static final int GT = 2;
    SolutionType type;
    int puzzleType = 0;
    boolean isRemove = false;
    int compType = -1;
    int compAnz = 0;

    private StepType(SolutionType type, int puzzleType, boolean isRemove, int compType, int compAnz) {
        this.type = type;
        this.puzzleType = puzzleType;
        this.isRemove = isRemove;
        this.compType = compType;
        this.compAnz = compAnz;
    }

    public static void parseTypeStr(List<StepType> stepList, String inputStr) {
        int puzzleType = 0;
        boolean isRemove = false;
        int compType = -1;
        int compAnz = 0;
        inputStr = inputStr.toLowerCase();
        if (inputStr.startsWith("-")) {
            isRemove = true;
            inputStr = inputStr.substring(1);
        }

        String compStr = null;
        String typeStr = null;
        int compIndex = -1;
        compIndex = inputStr.indexOf(43);
        int typeIndex = inputStr.indexOf(58);
        if (typeIndex == -1 && compIndex != -1) {
            compStr = inputStr.substring(compIndex + 1);
            inputStr = inputStr.substring(0, compIndex);
        } else if (typeIndex != -1 && compIndex == -1) {
            typeStr = inputStr.substring(typeIndex);
            inputStr = inputStr.substring(0, typeIndex);
        } else if (typeIndex != -1 && compIndex != -1) {
            if (typeIndex < compIndex) {
                compStr = inputStr.substring(compIndex + 1);
                typeStr = inputStr.substring(typeIndex, compIndex);
                inputStr = inputStr.substring(0, typeIndex);
            } else {
                typeStr = inputStr.substring(typeIndex);
                compStr = inputStr.substring(compIndex + 1, typeIndex);
                inputStr = inputStr.substring(0, compIndex);
            }
        }

        int var18 = 0;
        if (typeStr != null) {
            if (typeStr.length() < 2) {
                System.out.println("Puzzle type missing (assuming '0')!");
            } else {
                char typeModeChar = typeStr.charAt(1);
                switch (typeModeChar) {
                    case '0':
                        var18 = 0;
                        break;
                    case '1':
                        var18 = 1;
                        break;
                    case '2':
                        var18 = 2;
                        break;
                    case '3':
                        var18 = 3;
                        break;
                    default:
                        System.out.println("Invalid puzzle type: " + typeModeChar + " (assuming '0')");
                }
            }
        }

        if (compStr != null) {
            if (compStr.length() < 2) {
                System.out.println("Invalid comparison spec - ignored!");
            } else {
                switch (compStr.charAt(0)) {
                    case 'e':
                        compType = 0;
                        break;
                    case 'g':
                        compType = 2;
                        break;
                    case 'l':
                        compType = 1;
                        break;
                    default:
                        System.out.println("Invalid comparison mode: " + compStr.charAt(0) + " (ignored)");
                }

                if (compType != -1) {
                    String compAnzStr = compStr.substring(1);

                    try {
                        compAnz = Integer.parseInt(compAnzStr);
                    } catch (NumberFormatException ex) {
                        System.out.println("Invalid comparison digit: " + compAnzStr + " (comparison ignored)");
                        compType = -1;
                    }
                }
            }
        }

        SolutionType type = null;
        SolutionType[] values = SolutionType.values();

        for (int j = 0; j < values.length; j++) {
            if (values[j].getArgName().equals(inputStr)) {
                type = values[j];
            }
        }

        if (type == null) {
            if (inputStr.equals("all")) {
                for (SolutionType tmpType : SolutionType.values()) {
                    if (!tmpType.isSingle()) {
                        addDeleteStepInList(stepList, new StepType(tmpType, var18, isRemove, compType, compAnz));
                    }
                }
            } else if (inputStr.equals("nssts")) {
                for (SolutionType tmpType : SolutionType.values()) {
                    if (!tmpType.isSingle() && !tmpType.isSSTS()) {
                        addDeleteStepInList(stepList, new StepType(tmpType, var18, isRemove, compType, compAnz));
                    }
                }
            } else if (inputStr.equals("nssts1")) {
                for (SolutionType tmpType : SolutionType.values()) {
                    if (!tmpType.isSingle()
                            && !tmpType.isSSTS()
                            && !tmpType.equals(SolutionType.TWO_STRING_KITE)
                            && !tmpType.equals(SolutionType.SKYSCRAPER)
                            && !tmpType.equals(SolutionType.BUG_PLUS_1)
                            && !tmpType.equals(SolutionType.EMPTY_RECTANGLE)
                            && !tmpType.equals(SolutionType.W_WING)
                            && !tmpType.equals(SolutionType.UNIQUENESS_1)
                            && !tmpType.equals(SolutionType.XYZ_WING)
                            && !tmpType.equals(SolutionType.REMOTE_PAIR)) {
                        addDeleteStepInList(stepList, new StepType(tmpType, var18, isRemove, compType, compAnz));
                    }
                }
            } else {
                System.out.println("Invalid step name: " + inputStr + " (ignored!)");
            }
        } else {
            addDeleteStepInList(stepList, new StepType(type, var18, isRemove, compType, compAnz));
        }
    }

    private static void addDeleteStepInList(List<StepType> stepList, StepType step) {
        if (step.type != null) {
            boolean found = false;

            for (int i = 0; i < stepList.size(); i++) {
                StepType tmpStep = stepList.get(i);
                if (tmpStep.type == step.type && tmpStep.puzzleType == step.puzzleType) {
                    found = true;
                    if (step.isRemove) {
                        stepList.remove(i);
                        i--;
                    }
                }
            }

            if (step.isRemove) {
                if (!found) {
                    System.out.println("Could not remove step " + step.type.getArgName() + ":" + step.puzzleType + ": was not set yet.");
                }
            } else {
                stepList.add(step);
            }
        }
    }

    @Override
    public String toString() {
        char compChar = '-';
        switch (this.compType) {
            case 0:
                compChar = '=';
                break;
            case 1:
                compChar = '<';
                break;
            case 2:
                compChar = '>';
        }

        return this.compType != -1
                ? this.type.getStepName() + " (" + this.puzzleType + ", " + compChar + this.compAnz + ")"
                : this.type.getStepName() + " (" + this.puzzleType + ", -)";
    }
}
