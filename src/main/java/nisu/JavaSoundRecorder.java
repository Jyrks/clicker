package nisu;


import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;

import static org.apache.commons.lang3.math.NumberUtils.isCreatable;

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

    void start() throws LineUnavailableException, AWTException, InterruptedException {
        AudioFormat audioFormat = getAudioFormat();
        TargetDataLine targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
        targetDataLine.open();
        targetDataLine.start();

        JFrame jf = new JFrame();

        JLabel label  = new JLabel("Delay");
        label.setBounds(160, 0, 50, 30);
        jf.add(label);

        JTextField textField = new JTextField(30);
        textField.setBounds(120, 0, 35, 30);
        jf.add(textField);

        JToggleButton enableButton = new JToggleButton("Enable");
        enableButton.setBounds(120,50,80,30);
        jf.add(enableButton);

        JPanel jp = new MotionPanel(jf);
        jf.setUndecorated(true);
        jp.setPreferredSize(new Dimension(200, 102));
        jp.setOpaque(false);
        jf.getContentPane().add(jp);
        jf.pack();
        jf.setVisible(true);
        jf.setBackground(new Color(0, 0, 0, 1));
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

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
                if (max > 2000 && enableButton.isSelected()) {
                    Thread.sleep(getDelay(textField));
                    Robot bot = new Robot();
                    moveMouse(jf.getLocationOnScreen().x + 50, jf.getLocationOnScreen().y + 50, 4, bot);
                    jf.setVisible(false);
                    bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                    jf.setVisible(true);
                    enableButton.setSelected(false);
                }
            }
        }
    }

    public static Long getDelay(JTextField textField) {
        String text = textField.getText().replace(",", ".");
        if (!isCreatable(text)) {
            return 0L;
        }
        return new Double(Double.parseDouble(text) * 1000).longValue();
    }

    public static void moveMouse(int x, int y, int maxTimes, Robot screenWin) {
        for(int count = 0;(MouseInfo.getPointerInfo().getLocation().getX() != x ||
                MouseInfo.getPointerInfo().getLocation().getY() != y) &&
                count < maxTimes; count++) {
            screenWin.mouseMove(x, y);
        }
    }
}
