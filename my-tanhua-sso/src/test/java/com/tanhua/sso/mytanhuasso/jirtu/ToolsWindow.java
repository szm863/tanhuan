package com.tanhua.sso.mytanhuasso.jirtu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/*
 * 操作窗口
 */
class ToolsWindow extends JWindow {
    private ScreenShotWindow parent;

    public ToolsWindow(ScreenShotWindow parent, int x, int y) {
        this.parent = parent;
        this.init();
        this.setLocation(x, y);
        this.pack();
        this.setVisible(true);
    }

    private void init() {

        this.setLayout(new BorderLayout());
        JToolBar toolBar = new JToolBar("Java 截图");

//保存按钮
        JButton saveButton = new JButton(new ImageIcon("images/A.jpg"));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    parent.saveImage();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        toolBar.add(saveButton);

//关闭按钮
        JButton closeButton = new JButton(new ImageIcon("images/close.gif"));
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        toolBar.add(closeButton);

        this.add(toolBar, BorderLayout.NORTH);
    }

}