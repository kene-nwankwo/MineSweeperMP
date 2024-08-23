package SPMS;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class TimerLabel extends JLabel {

    public boolean timerRunning = false;
    private Timer timer;
    private int seconds = 0;

    public TimerLabel() {
        setText("Time: 0s");

        // Create a timer that updates every second
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timerRunning) {
                    seconds++;
                    setText("Time: " + seconds + "s");
                }
            }
        });
    }

    // Method to start or stop the timer based on the boolean flag
    public void setTimerRunning(boolean running) {
        timerRunning = running;
        if (timerRunning) {
            timer.start(); // Start the timer if not already running
        } else {
            timer.stop();  // Stop the timer
        }
    }

//    // Method to reset the timer
//    public void resetTimer() {
//        seconds = 0;
//        setText("Time: 0s");
//    }
//
//    public static void main(String[] args) {
//        JFrame frame = new JFrame("Timer JLabel Example");
//        TimerLabel timerLabel = new TimerLabel();
//
//        // Example: Start the timer when the frame is visible
//        frame.add(timerLabel);
//        frame.setSize(300, 200);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setVisible(true);
//
//        // Simulate changing the boolean variable after 2 seconds
//        Timer controlTimer = new Timer(2000, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                timerLabel.setTimerRunning(true);  // Start the timer
//            }
//        });
//        controlTimer.setRepeats(false); // Ensure this timer runs only once
//        controlTimer.start();
//    }
}
