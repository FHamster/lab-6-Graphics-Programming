package test3;

import test1.DrawLineFrame;

import javax.swing.*;
import java.awt.*;

public class DraggableTest
{
    public static void main(String[] args)
    {
        EventQueue.invokeLater(()->{
            DrawLineFrame frame = new DrawLineFrame();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
