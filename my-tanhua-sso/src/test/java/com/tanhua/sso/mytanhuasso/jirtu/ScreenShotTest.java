package com.tanhua.sso.mytanhuasso.jirtu;

import java.awt.*;

public class ScreenShotTest {
    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    ScreenShotWindow ssw = new ScreenShotWindow();
                    ssw.setVisible(true);
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}