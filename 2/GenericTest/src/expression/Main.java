package expression;

import expression.generic.GenericTabulator;

public class Main {
    public static void main(String[] args) throws Exception {
        String expr = "(25 + 3) * (x + y)";
        Object[][][] table = new GenericTabulator().tabulate("-d", expr, 4, 8, 1, 5, 0, 4);
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                for (int k = 0; k < table[i][j].length; k++) {
                    System.out.print(table[i][j][k] + " ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}
