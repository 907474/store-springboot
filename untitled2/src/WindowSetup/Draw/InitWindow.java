package WindowSetup.Draw;
import UserInput.MouseInput;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InitWindow extends JFrame {
    private JPanel boardPanel;
    private JLabel statusLabel;
    private JButton resetButton;
    private JButton SuggestionDots;
    private JButton BigBearBot;
    private JButton SmartBearBot;

    public InitWindow() {
        setTitle("Xiangqi Board");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new DrawBoard());
        pack();
        setSize(600,800);
        setLocationRelativeTo(null);
        setResizable(true);

    //    boardPanel.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                MouseInput CurrentMouse = new MouseInput();
 //               CurrentMouse.MouseHandler(e);
   //         }
    //    });
      //  add(boardPanel, BorderLayout.CENTER);


        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new FlowLayout());
        statusLabel = new JLabel("Red's Move");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.NORTH);

        JPanel controlPanel = new JPanel();
        resetButton = new JButton("Reset Game");
        resetButton.addActionListener(e -> resetGame());
        SuggestionDots = new JButton("Suggestion Dots");
        SuggestionDots.addActionListener(e -> resetGame());
        BigBearBot = new JButton("Play Against Big Bear");
        BigBearBot.addActionListener(e -> resetGame());
        SmartBearBot = new JButton("Play Against Smart Bear");
        SmartBearBot.addActionListener(e -> resetGame());
        controlPanel.add(resetButton);
        controlPanel.add(SuggestionDots);
        controlPanel.add(BigBearBot);
        controlPanel.add(SmartBearBot);
        add(controlPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new InitWindow().setVisible(true);
        });
    }

    private void resetGame() {
        boardPanel.repaint();
    }

}
