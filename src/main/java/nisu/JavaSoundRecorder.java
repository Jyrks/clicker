package nisu;


import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class JavaSoundRecorder {

    AudioFormat getAudioFormat() {
        float sampleRate = 8000;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
        return format;
    }

    void start() throws LineUnavailableException, AWTException {
        AudioFormat audioFormat = getAudioFormat();
        TargetDataLine targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
        targetDataLine.open();
        targetDataLine.start();

        JFrame jf = new JFrame();
        jf.add(new JLabel("Label"));
        JPanel jp = new MotionPanel(jf);
        jf.setUndecorated(true);
        jp.setPreferredSize(new Dimension(110, 110));
        jp.setOpaque(false);
        jf.getContentPane().add(jp);
        jf.pack();
        jf.setVisible(true);
        jf.setBackground(new Color(0, 0, 0, 1));

        byte[] buffer = new byte[2000];
        while (true) {
            int bytesRead = targetDataLine.read(buffer, 0, buffer.length);

            short max;


            if (bytesRead >= 0) {
                max = (short) (buffer[0] + (buffer[1] << 8));
                for (int p = 2; p < bytesRead - 1; p += 2) {
                    short thisValue = (short) (buffer[p] + (buffer[p + 1] << 8));
                    if (thisValue > max) max = thisValue;
                }
                System.out.println(max);
                System.out.println(jf.getLocationOnScreen());
                if (max > 2000) {
                    Robot bot = new Robot();
                    moveMouse(jf.getLocationOnScreen().x + 50, jf.getLocationOnScreen().y + 50, 4, bot);
                    jf.hide();
                    bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                    jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    jf.dispatchEvent(new WindowEvent(jf, WindowEvent.WINDOW_CLOSING));
                    return;
                }
            }
        }
    }

    public static void moveMouse(int x, int y, int maxTimes, Robot screenWin) {
        for(int count = 0;(MouseInfo.getPointerInfo().getLocation().getX() != x ||
                MouseInfo.getPointerInfo().getLocation().getY() != y) &&
                count < maxTimes; count++) {
            screenWin.mouseMove(x, y);
        }
    }
}
