package test1;

import com.sun.org.apache.bcel.internal.generic.BREAKPOINT;
import test1.getpoints.*;
import test1.getpoints.GetPointsImpl.GetComplexPoints;
import test1.getpoints.GetPointsImpl.GetCosPoints;
import test1.getpoints.GetPointsImpl.GetSinPoints;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class DrawLineFrame extends JFrame
{
    //    add button
    private final JCheckBox SinCheckBox = new JCheckBox("Sin(x)", false);
    private final JCheckBox CosCheckBox = new JCheckBox("Cos(x)", false);
    private final JCheckBox ComplexCheckBox = new JCheckBox("Sin(x) + Cos(x)", false);

    public DrawLineFrame() //throws HeadlessException
    {
//        get screen size
        Toolkit toolKit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolKit.getScreenSize();

//        setting Frame size
        setSize(screenSize.width / 3, screenSize.height / 3);

//        initialize CheckBox Panel
        JPanel checkBoxPanel = new JPanel();

//        add CheckBox into Panel
        checkBoxPanel.add(SinCheckBox);
        checkBoxPanel.add(CosCheckBox);
        checkBoxPanel.add(ComplexCheckBox);

//        initialize component
        DrawLineComponent drawLineComponent = new DrawLineComponent();

//        associate actions with buttons
        SinCheckBox.addItemListener(e ->
        {
            String gName = "Sin";
            if (((JCheckBox) e.getItem()).isSelected())
            {
                drawLineComponent.addGraph(gName, new GetSinPoints());
            } else
            {
                drawLineComponent.subGraph(gName);
            }
        });
        CosCheckBox.addItemListener(e ->
        {
            String gName = "Cos";
            if (((JCheckBox) e.getItem()).isSelected())
            {
                drawLineComponent.addGraph(gName, new GetCosPoints());
            } else
            {
                drawLineComponent.subGraph(gName);
            }
        });
        ComplexCheckBox.addItemListener(e ->
        {
            String gName = "Sin+Cos";
            if (((JCheckBox) e.getItem()).isSelected())
            {
                drawLineComponent.addGraph(gName, new GetComplexPoints());
            } else
            {
                drawLineComponent.subGraph(gName);
            }
        });

//        add Panel into Frame
        add(checkBoxPanel, BorderLayout.NORTH);
        add(drawLineComponent, BorderLayout.CENTER);

    }
}

class DrawLineComponent extends JComponent
{
    private HashMap<String, List<Point2D>> graphs;//save graph
    private Point2D curPoint;
//    private HashMap<String, List<Line2D>> graphs;

    public DrawLineComponent()
    {
        graphs = new HashMap<>();
        /* add component mouse event */
        MouseHandler handler = new MouseHandler();
        addMouseListener(handler);
        addMouseMotionListener(handler);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        for (List<Point2D> x : graphs.values())
        {
            for (Line2D it : makeLineFromPoint(x))
            {
                g2.draw(it);
            }
        }
    }

    public void subGraph(String gName)
    {
        graphs.remove(gName);
        repaint();
    }

 /*   private String findGraphByPoint(Point2D p)
    {

    }*/
    public void moveGraph(List<Point2D> graph, double moveX, double moveY)
    {
        double lastX, lastY;
        for (Point2D it : graph)
        {
            lastX = it.getX();
            lastY = it.getY();
            it.setLocation(lastX + moveX, lastY + moveY);
        }
    }

    public void addGraph(String gName, GetPoints getPoints)
    {
        List<Point2D> points = getPoints.createPoint(100, 200);
        graphs.put(gName, points);
        repaint();
    }

    private List<Line2D> makeLineFromPoint(List<Point2D> points)
    {
        if (points.size() < 2)
        {
            throw new IllegalArgumentException("点的数小于2，size=" + points.size());
        }

        List<Line2D> lines = new ArrayList<>();
        Line2D tempLine;
        for (int i = 0; i < points.size() - 1; i++)
        {
            Point2D p1 = points.get(i);
            Point2D p2 = points.get(i + 1);
            tempLine = new Line2D.Double(p1, p2);
            lines.add(tempLine);
        }
        return lines;
    }

    private String findPointInGraph(Point2D p)
    {
        for (Map.Entry<String, List<Point2D>> x : graphs.entrySet())
        {
            for (Point2D r : x.getValue())
            {
                if ((Math.abs(r.getX() - p.getX()) < 10) &&
                        (Math.abs(r.getY() - p.getY())) < 10)
                {
                    return x.getKey();
                }
            }
        }
        return null;
    }
    private Point2D find(Point2D p)
    {
        for (List<Point2D> x : graphs.values())
        {
            for (Point2D r : x)
            {
                if ((Math.abs(r.getX() - p.getX()) < 10) &&
                        (Math.abs(r.getY() - p.getY())) < 10)
                {
                    return r;
                }
            }
        }
        return null;
    }


    /*private class MouseHandler extends MouseAdapter
    {
        @Override
        public void mousePressed(MouseEvent e)
        {
//            System.out.println(e.getButton());
            Point2D point = e.getPoint();
            curPoint = find(point);
        }
    }*/


    //    private class MouseMotionHandler extends MouseAdapter implements MouseMotionListener
    private class MouseHandler extends MouseAdapter implements MouseMotionListener
    {
        int curButton;

        int lastX;
        int lastY;
        @Override
        public void mousePressed(MouseEvent e)
        {
            System.out.println(e.getButton());
            Point2D point = e.getPoint();
            curPoint = find(point);
            curButton = e.getButton();
            lastX = e.getX();
            lastY = e.getY();
        }

        @Override
        public void mouseDragged(MouseEvent e)
        {
            if (curPoint != null)
            {
                switch (curButton)
                {
                    //left button
                    case 1:{
                        System.out.println("drag" + curButton);
                        //drag current point to point(x,y)
                        curPoint.setLocation(e.getX(), e.getY());
                        break;
                    }
                    //right button
                    case 3: {
                        System.out.println("drag" + curButton);
//                        System.out.println(findPointInGraph(e.getPoint()));
                        String tmp = findPointInGraph(e.getPoint());

                        moveGraph(graphs.get(tmp), e.getX() - lastX, e.getY() - lastY);

                        lastX = e.getX();
                        lastY = e.getY();

                        break;
                    }
                }
                repaint();
            }
        }

        public void mouseMoved(MouseEvent event)
        {
            // set the mouse cursor to cross hairs if it is inside
            // a rectangle
            if (find(event.getPoint()) == null)
            {
                setCursor(Cursor.getDefaultCursor());
            } else
            {
                setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            }
        }
    }
}