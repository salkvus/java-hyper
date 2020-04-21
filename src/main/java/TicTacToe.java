import java.util.Scanner;

class GameTicTacToe {

    static final int SIZE = 3;
    static final String blankCells = "_".repeat(SIZE * SIZE);

    GameField gameField;
    GameState gameState;

    private GameTicTacToe(GameField gameField) {
        this.gameField = gameField;
        this.gameState = new GameState(gameField);
    }

    public static void playGame() {
        GameField gameField = new GameField(blankCells, SIZE, 'X');
        GameTicTacToe game = new GameTicTacToe(gameField);
        game.run();
    }

    public void run() {
        gameField.draw();
        do {
            gameField.move();
        } while (gameState.isNotFinished());
        gameState.show();
    }
}

class GameState {

    enum GameStates {
        NOT_FINISHED, DRAW, X_WINS, O_WINS, IMPOSSIBLE, NOT_DEFINED;

        @Override
        public String toString() {
            String description = "";
            switch (this) {
                case NOT_FINISHED:
                    description = "Game not finished";
                    break;
                case DRAW:
                    description = "Draw";
                    break;
                case X_WINS:
                    description = "X wins";
                    break;
                case O_WINS:
                    description = "O wins";
                    break;
                case IMPOSSIBLE:
                    description = "Impossible";
                    break;
                case NOT_DEFINED:
                    description = "Not defined";
                    break;
            }
            return description;
        }
    }

    private GameField gameField;
    private GameStates gameState;
    private int size;

    GameState(GameField gameField) {
        this.gameField = gameField;
        this.size = gameField.getSize();
    }

    public boolean isNotFinished() {
        gameState = getGameState();
        return gameState == GameStates.NOT_FINISHED;
    }

    public void show() {
        System.out.println(gameState);
    }

    private GameStates getGameState() {
        GameStates rowsState = getRowsState();
        if (rowsState == GameStates.IMPOSSIBLE) {
            return GameStates.IMPOSSIBLE;
        }
        GameStates columnsState = getColumnsState();
        if (columnsState == GameStates.IMPOSSIBLE) {
            return  GameStates.IMPOSSIBLE;
        }
        GameStates diagsState = getDiagsState();
        if (diagsState == GameStates.IMPOSSIBLE) {
            return GameStates.IMPOSSIBLE;
        }

        GameStates state = GameStates.NOT_FINISHED;
        if (rowsState == GameStates.X_WINS || rowsState == GameStates.O_WINS) {
            state = rowsState;
        } else if (columnsState == GameStates.X_WINS || columnsState == GameStates.O_WINS) {
            state = columnsState;
        } else if (diagsState == GameStates.X_WINS || diagsState == GameStates.O_WINS) {
            state = diagsState;
        } else if (rowsState == GameStates.NOT_DEFINED && columnsState == GameStates.NOT_DEFINED && diagsState == GameStates.NOT_DEFINED) {
            state = GameStates.DRAW;
        }
        return  state;
    }

    private GameStates getRowsState() {
        GameStates[] rowsStates = new GameStates[size];
        int xCountsAll = 0, oCountsAll = 0, _CountsAll = 0;
        for (int i = 0; i < size; ++i) {
            int xCounts = 0, oCounts = 0, _Counts = 0;
            for (int j = 0; j < size; ++j) {
                char ch = gameField.getValueOf(i + 1, j + 1);
                xCounts += ch == 'X' ? 1 : 0;
                oCounts += ch == 'O' ? 1 : 0;
                _Counts += ch == '_' ? 1 : 0;
            }
            rowsStates[i] = getState(xCounts, oCounts, _Counts);
            xCountsAll += xCounts;
            oCountsAll += oCounts;
            _CountsAll += _Counts;
        }
        return packStates(rowsStates, xCountsAll, oCountsAll, _CountsAll);
    }

    private GameStates getColumnsState() {
        GameStates[] columnsStates = new GameStates[size];
        int xCountsAll = 0, oCountsAll = 0, _CountsAll = 0;
        for (int i = 0; i < size; ++i) {
            int xCounts = 0, oCounts = 0, _Counts = 0;
            for (int j = 0; j < size; ++j) {
                char ch = gameField.getValueOf(j + 1, i + 1);
                xCounts += ch == 'X' ? 1 : 0;
                oCounts += ch == 'O' ? 1 : 0;
                _Counts += ch == '_' ? 1 : 0;
            }
            columnsStates[i] = getState(xCounts, oCounts, _Counts);
            xCountsAll += xCounts;
            oCountsAll += oCounts;
            _CountsAll += _Counts;
        }
        return packStates(columnsStates, xCountsAll, oCountsAll, _CountsAll);
    }

    private GameStates getDiagsState() {
        GameStates[] diagsStates = new GameStates[2];
        int x1 = 0, o1 = 0, _1 = 0, x2 = 0, o2 = 0, _2 = 0;
        for (int i = 0; i < size; ++i) {
            char ch = gameField.getValueOf(i + 1, i + 1);
            x1 += ch == 'X' ? 1 : 0;
            o1 += ch == 'O' ? 1 : 0;
            _1 += ch == '_' ? 1 : 0;
            ch = gameField.getValueOf(i + 1, size - i - 1 + 1);
            x2 += ch == 'X' ? 1 : 0;
            o2 += ch == 'O' ? 1 : 0;
            _2 += ch == '_' ? 1 : 0;
        }
        diagsStates[0] = getState(x1, o1, _1);
        diagsStates[1] = getState(x2, o2, _2);
        return packStates(diagsStates);
    }

    private GameStates getState(int xCounts, int oCounts, int _Counts) {
        GameStates state = GameStates.IMPOSSIBLE;
        if (_Counts != 0) {
            state = GameStates.NOT_FINISHED;
        } else if (xCounts < size && oCounts < size) {
            state = GameStates.NOT_DEFINED;
        } else if (xCounts == size) {
            state = GameStates.X_WINS;
        } else if (oCounts == size) {
            state = GameStates.O_WINS;
        }
        return state;
    }

    private GameStates packStates(GameStates[] states, int... countsArray) {
        if (countsArray.length > 0) {
            if (Math.abs(countsArray[0] - countsArray[1]) > 1 || countsArray[0] + countsArray[1] + countsArray[2] != size * size) {
                return GameStates.IMPOSSIBLE;
            }
        }
        GameStates state = GameStates.NOT_DEFINED;
        for (int i = 0; i < states.length; ++i) {
            if (states[i] == GameStates.IMPOSSIBLE) {
                state = states[i];
                break;
            }
            if (states[i] == GameStates.X_WINS) {
                if (state == GameStates.NOT_DEFINED || state == GameStates.NOT_FINISHED) {
                    state = states[i];
                } else if (state == GameStates.O_WINS) {
                    state = GameStates.IMPOSSIBLE;
                }
            } else if (states[i] == GameStates.O_WINS) {
                if (state == GameStates.NOT_DEFINED || state == GameStates.NOT_FINISHED) {
                    state = states[i];
                } else if (state == GameStates.X_WINS) {
                    state = GameStates.IMPOSSIBLE;
                    break;
                }
            } else if (!(state == GameStates.X_WINS || state == GameStates.O_WINS)) {
                if (state != GameStates.NOT_FINISHED) {
                    state = states[i];
                }
            }
        }
        return state;
    }

}

class GameField {

    class Position {
        private int xMax;
        private int yMax;
        private String error;

        private boolean checkBounds() {
            return this.x <= this.xMax && this.y <= this.yMax;
        }

        public int x;
        public int y;

        Position(int x, int y, int xMax, int yMax) {
            this.x = x;
            this.y = y;
            this.xMax = xMax;
            this.yMax = yMax;
            this.error = "";
        }

        Position(String strCoordinates, int xMax, int yMax) {
            this.xMax = xMax;
            this.yMax = yMax;
            this.error = "";
            String[] strArray = strCoordinates.split("\\s");
            if (strArray[0].matches("\\d") && strArray[1].matches("\\d")) {
                this.x = Integer.parseInt(strArray[0]);
                this.y = Integer.parseInt(strArray[1]);
                if (!checkBounds()) {
                    this.error = String.format("Coordinates should be from 1 to %d!", this.xMax);
                }
            } else {
                this.error = "You should enter numbers!";
            }
        }

        public boolean isValid() {
            return "".equals(this.error.trim());
        }

        public String getError() {
            return  error;
        }
    }

    private char player;
    private char[][] cells;
    private int size;
    private Scanner scanner;

    GameField(String cellsString, int size, char player) {
        this.player = player;
        this.scanner = new Scanner(System.in);
        this.size = size;
        this.cells = new char[size][size];
        int index = 0;
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                index = i * size + j;
                cells[i][j] = cellsString.charAt(index);
            }
        }
    }

    public void move() {
        boolean stop = false;
        int x = 0, y = 0;
        while (!stop) {
            System.out.print("Enter the coordinates: ");
            String coordinates = scanner.nextLine();
            Position point = new Position(coordinates, size, size);
            if (point.isValid()) {
                stop = setCell(point.x, point.y, player);
            } else {
                System.out.println(point.getError());
            }
        }
        draw();
        player = player == 'X' ? 'O' : 'X';
    }

    public void draw() {
        String hzLine = "";
        for (int i = 0; i < size * size; ++i) {
            hzLine += "-";
        }
        System.out.println(hzLine);
        for(int i = 0; i < size ; ++i) {
            System.out.print("| ");
            for (int j = 0; j < size; ++j) {
                System.out.printf("%c ", cells[i][j]);
            }
            System.out.println("|");
        }
        System.out.println(hzLine);
    }

    public char getValueOf(int x, int y) {
        return this.cells[size - y][x - 1];
    }

    public int getSize() {
        return size;
    }

    private boolean setCell(int x, int y, char ch) {
        boolean result = false;
        char c = cells[size - y][x - 1];
        if (c == '_') {
            cells[size - y][x - 1] = ch;
            result = true;
        } else {
            System.out.println("This cell is occupied! Choose another one!");
        }
        return result;
    }

}

public class TicTacToe {

    public static void main(String[] args) {
        GameTicTacToe.playGame();
    }

}