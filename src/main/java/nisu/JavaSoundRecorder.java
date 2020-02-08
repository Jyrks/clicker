package nisu;


import org.apache.commons.lang3.time.StopWatch;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;

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

        StopWatch stopwatch = new StopWatch();
        stopwatch.start();

        JFrame jf = new JFrame();
        jf.add(new JLabel("Label"));
        JPanel jp = new MotionPanel(jf);
//        jp.setBorder(new LineBorder(Color.BLUE));
        jf.setUndecorated(true);
        jp.setPreferredSize(new Dimension(110,110));// changed it to preferredSize, Thanks!
        jp.setOpaque(false);
        jf.getContentPane().add( jp );
        jf.pack();
        jf.setVisible(true);
        jf.setBackground(new Color(0,0,0,0));

        boolean isHidden = false;
        byte [] buffer = new byte[2000];
        while (true) {

            int bytesRead = targetDataLine.read(buffer,0,buffer.length);

            short max;

            if (bytesRead >=0) {
                max = (short) (buffer[0] + (buffer[1] << 8));
                for (int p=2;p<bytesRead-1;p+=2) {
                    short thisValue = (short) (buffer[p] + (buffer[p+1] << 8));
                    if (thisValue>max) max=thisValue;
                }
                if (max > 3000) {
                    System.out.println("You clapped");
                    Robot bot = new Robot();
                    bot.mouseMove(jf.getLocationOnScreen().x + 50, jf.getLocationOnScreen().y + 50);
                    jf.hide();
                    Thread.sleep(60);
                    System.out.println("Click");
                    bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                    throw new RuntimeException();
                }
            }
        }
    }

    public static void main(String[] args) throws LineUnavailableException, AWTException, InterruptedException {
        final JavaSoundRecorder recorder = new JavaSoundRecorder();
        recorder.start();
    }
}
