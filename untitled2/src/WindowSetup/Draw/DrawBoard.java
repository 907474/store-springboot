package WindowSetup.Draw;
import javax.swing.*;
import java.awt.*;
import Customizables.Colours;

class DrawBoard extends JPanel {

    // --- Constants for drawing ---
    private static final int GRID_COLS = 9;
    private static final int GRID_ROWS = 10;
    static int MARGIN = 0;

    // --- Colors ---

    public DrawBoard() {
        setBackground(Colours.BOARD_COLOR);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // BackgroundColor

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // To maintain aspect ratio, calculate cell size based on the limiting dimension
        // We use floating point division for accuracy
        double cellW = (double) panelWidth / (GRID_COLS + 1); // Add padding cols
        double cellH = (double) panelHeight / (GRID_ROWS + 1); // Add padding rows
        int CELL_SIZE = (int) Math.min(cellW, cellH);

        // Recalculate board dimensions and margins to center it
        int BOARD_WIDTH = (GRID_COLS - 1) * CELL_SIZE;
        int BOARD_HEIGHT = (GRID_ROWS - 1) * CELL_SIZE;
        int marginX = (panelWidth - BOARD_WIDTH) / 2;
        int marginY = (panelHeight - BOARD_HEIGHT) / 2;
        g2d.setColor(Colours.Line_Color);
        g2d.setStroke(new BasicStroke(2));
        MARGIN = Math.min(marginX, marginY);

        // Top/Bottom Horizontals
        g2d.drawLine(MARGIN, MARGIN, MARGIN + BOARD_WIDTH, MARGIN); // Top line
        g2d.drawLine(MARGIN, MARGIN + BOARD_HEIGHT, MARGIN + BOARD_WIDTH, MARGIN + BOARD_HEIGHT); // Bottom line

        g2d.drawLine(MARGIN, MARGIN, MARGIN, MARGIN+ BOARD_WIDTH); // Top line
        g2d.drawLine(MARGIN + BOARD_WIDTH, MARGIN, MARGIN + BOARD_WIDTH, MARGIN + BOARD_HEIGHT); // Bottom line
        //Vertical Lines
        for (int i = 0; i < GRID_COLS; i++) {
            int x = MARGIN + i * CELL_SIZE;
            g2d.drawLine(x, MARGIN, x, MARGIN + 4 * CELL_SIZE); // Top half
            g2d.drawLine(x, MARGIN + 5 * CELL_SIZE, x, MARGIN + BOARD_HEIGHT); // Bottom half
        }

        //Horizontal Lines
        for (int i = 1; i < GRID_ROWS - 1; i++) {
            int y = MARGIN + i * CELL_SIZE;
            g2d.drawLine(MARGIN, y, MARGIN + BOARD_WIDTH, y);
        }
        //Palace Diagonals
        for(int RedBlack = 0; RedBlack<=1; RedBlack ++){
            for(int ThreeFive = 3,FiveThree = 5; ThreeFive<=5; ThreeFive+=2, FiveThree -= 2){
                g2d.drawLine(MARGIN + ThreeFive * CELL_SIZE, MARGIN + (7 * CELL_SIZE)*RedBlack, MARGIN + FiveThree * CELL_SIZE, MARGIN + (7*RedBlack+2) * CELL_SIZE);
            }
        }
        g2d.setFont(new Font("Serif", Font.BOLD, 36));
        g2d.setColor(Colours.Line_Color.brighter());
        String riverTextLeft = "楚 河";
        String riverTextRight = "漢 界";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(riverTextRight);
        int textY = MARGIN + 4 * CELL_SIZE + (CELL_SIZE - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(riverTextLeft, MARGIN+5, textY);
        g2d.drawString(riverTextRight, MARGIN+BOARD_WIDTH-textWidth, textY);
    }
}
