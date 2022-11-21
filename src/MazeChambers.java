import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.lang.String;


import static java.lang.Integer.parseInt;

public class MazeChambers {

    int CHAMBER_AMOUNT;
    static int ROW_SIZE;
    static int COL_SIZE;
    static int cur_row = 0;
    static int cur_col = 0;
    static int prev_row = 0;
    static int prev_col = 0;
    static boolean flooding = false;

    static int curr_group_num = 1;
    static List<String> LOADED_MAZE = new ArrayList<String>();
    static char[][] MAZE;
    static boolean[][] is_empty;

    static boolean[][] is_flooding;

    public static void load_maze(){
        boolean is_first_line = true;
        String[] maze_size;

        try {
            File myObj = new File("src/maze.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (is_first_line) {
                    maze_size = data.split(",", 2);
                     ROW_SIZE = parseInt(maze_size[0]);
                    COL_SIZE = parseInt(maze_size[1]);
                    is_first_line = false;
                } else {
                    LOADED_MAZE.add(data);
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    public static void initialize_maze() {

        MAZE = new char[ROW_SIZE][COL_SIZE];

        int row = 0;
        for (String s : LOADED_MAZE) {
            for (int col = 0; col < ROW_SIZE; col++) {
                MAZE[row][col] = s.charAt(col);
            }
            row++;
        }
    }

    public static void initialize_is_empty() {
        is_empty = new boolean[ROW_SIZE][COL_SIZE];
        for (int row = 0; row < ROW_SIZE; row++) {
            for (int col = 0; col < COL_SIZE; col++) {
                if (MAZE[row][col] == ' ') {
                    is_empty[row][col] = true;
                } else {
                    is_empty[row][col] = false;
                }
            }
        }
    }

    public static void is_flooding() {
        is_flooding = new boolean[ROW_SIZE][COL_SIZE];
    }

    public static boolean is_above_empty() {
        if (is_empty[cur_row - 1][cur_col] && (cur_row > 0 && cur_row <= COL_SIZE-1)) {
            return true;
        }
        return false;
    }

    public static boolean is_below_empty() {
        if (is_empty[cur_row + 1][cur_col] && (cur_row >= 0 && cur_row < COL_SIZE-1)) {
            return true;
        }
        return false;
    }

    public static boolean is_left_empty() {
        if (is_empty[cur_row][cur_col - 1] && (cur_col > 0 && cur_col <= COL_SIZE-1)) {
            return true;
        }
        return false;
    }

    public static boolean is_right_empty() {
        if (is_empty[cur_row][cur_col + 1] && cur_row >= 0 && cur_row < COL_SIZE-1) {
            return true;
        }
        return false;
    }

    public static void move_up() {
        cur_row--;
    }

    public static void move_down() {
        cur_row++;
    }

    public static void move_left() {
        cur_col--;
    }

    public static void move_right() {
        cur_col++;
    }

    public static void move() {
        if (is_above_empty()) {
            move_up();
        } else if (is_right_empty()) {
            move_right();
        } else if (is_left_empty()) {
            move_left();
        } else if (is_right_empty()) {
            move_right();
        }
    }


    public static boolean check_surroundings() {
        return is_above_empty() || is_below_empty() || is_left_empty() || is_right_empty();
    }

    public static void clear_flood() {
        for (int row = 0; row < ROW_SIZE; row++) {
            for (int col = 0; col < COL_SIZE; col++) {
                is_flooding[row][col]= false;
            }
        }
    }


    public static void fill() {
        MAZE[cur_row][cur_col] = (char)curr_group_num;
        is_empty[cur_row][cur_col] = false;
        is_flooding[cur_row][cur_col] = true;
    }

    public static void flood_fill(char [][] maze, int x, int y, int new_group_num){
        int prev_group_num = (int)maze[cur_row][cur_col];
        if (prev_group_num == new_group_num) return;
        flood_fill_util(maze, x, y, prev_group_num, new_group_num);

    }

    public static void flood_fill_util(char [][] maze, int x, int y, int prev_group_num, int new_group_num ) {

        // Base Case

        if (x < 0 || x >= ROW_SIZE || y < 0 || y >= COL_SIZE) {
            return;
        }
        if (maze[x][y] != (char)prev_group_num) {
            return;
        }

        maze[x][y] = (char)new_group_num;
        is_empty[cur_row][cur_col] = false;


        flood_fill_util(maze, x+1, y, prev_group_num, new_group_num);
        flood_fill_util(maze, x-1, y, prev_group_num, new_group_num);
        flood_fill_util(maze, x, y+1, prev_group_num, new_group_num);
        flood_fill_util(maze, x, y-1, prev_group_num, new_group_num);
    }

    public static void flood() {
        flooding = true;
        while(flooding) {
            fill();
            if (check_surroundings()) {
                move();
                flood();
            }
            flooding = false;
        }
    }

    public static void update_maze() {
        for (int row = 0; row < ROW_SIZE; row++) {
            for (int col = 0; col < COL_SIZE; col++) {
                if (is_empty[row][col]) {
                        flood_fill(MAZE, row, col, curr_group_num);
                        curr_group_num++;
                }
            }
        }
    }

    public static void print_maze() {
        for (int row = 0; row < ROW_SIZE; row++) {
            for (int col = 0; col < COL_SIZE; col++) {
                System.out.print(MAZE[row][col]);
            }
            System.out.println();
        }
    }

    public static void print_loaded_maze(){
        for (String a : LOADED_MAZE) {
            System.out.println(a);
        }
    }

    public static void print_is_empty(){

        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                System.out.print(is_empty[i][j]);
                System.out.print(' ');
            }
            System.out.println();
        }
    }


    public static void main(String[] args) {
        load_maze();
        initialize_maze();
        initialize_is_empty();
        print_loaded_maze();
        System.out.println();
        print_maze();
        System.out.println();
        print_is_empty();
        update_maze();
        System.out.println();
        print_maze();
    }
}