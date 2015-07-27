package timing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

public class Clock extends JFrame {

  public Clock() {
    super("Timer Demo");
    setSize(300, 100);
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    ClockLabel clock = new ClockLabel();
    getContentPane().add(clock, BorderLayout.NORTH);
  }

}
class ClockLabel extends JLabel implements ActionListener {

  public ClockLabel() {
    super("" + new Date());
    Timer t = new Timer(1000, this);
    t.start();
  }

  public void actionPerformed(ActionEvent ae) {
    setText((new Date()).toString());
  }
}