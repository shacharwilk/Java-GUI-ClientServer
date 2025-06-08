package HomeWork03;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MyAnim extends JPanel implements ActionListener 
{
    private Polygon letterS, letterW;
    private Color colorS, colorW;
    private double angle = 0;
    private Timer timer;
    private boolean growing = true;
    private int fontSize = 1;    

    public MyAnim() 
    {
        createFile("S.txt", new int[][]{
            {5, 5, 8, 14, 17, 20, 26, 29, 31, 36, 39, 43, 44, 45, 47, 48, 49, 49, 49, 48, 47, 46, 44, 37, 32, 25, 22, 16, 12, 7, 6, 4, 2, 0, 0, 0, 0, 1, 3, 6, 8, 10, 16, 22, 29, 33, 36, 43, 46, 50, 57, 61, 61, 57, 50, 47, 43, 36, 33, 31, 26, 22, 19, 17, 16, 14, 13, 12, 12, 12, 12, 13, 15, 16, 17, 20, 25, 29, 36, 39, 45, 50, 54, 55, 57, 59, 60, 61, 61, 61, 60, 58, 55, 53, 51, 46, 40, 34, 30, 27, 21, 18, 15, 8, 5},
            {85, 73, 75, 77, 78, 79, 79, 79, 79, 79, 78, 77, 76, 75, 72, 70, 67, 65, 62, 59, 57, 56, 55, 52, 52, 50, 49, 48, 45, 43, 41, 40, 36, 32, 27, 24, 21, 15, 11, 7, 5, 3, 1, -1, -2, -2, -2, -1, 0, 0, 3, 4, 16, 14, 11, 10, 9, 8, 8, 8, 8, 8, 9, 11, 12, 13, 15, 18, 21, 23, 25, 28, 30, 32, 33, 34, 36, 38, 39, 40, 41, 43, 45, 47, 48, 50, 53, 57, 62, 64, 67, 72, 77, 81, 82, 84, 86, 88, 89, 89, 88, 88, 87, 86, 85}
        });

        createFile("W.txt", new int[][]{
            {4, 16, 34, 53, 66, 84, 103, 115, 93, 78, 59, 41, 26, 4}, 
            {0, 0, 73, 0, 0, 73, 0, 0, 87, 87, 11, 87, 87, 0}
        });

        letterS = createPolygon("S.txt");
        letterW = createPolygon("W.txt");

        colorS = getRandomColor();
        colorW = getRandomColor();

        timer = new Timer(500, this);
        timer.start();
    }

    private void createFile(String fileName, int[][] coordinates) 
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) 
        {
            for (int i = 0; i < coordinates[0].length; i++) 
            {
                writer.write(coordinates[0][i] + "," + coordinates[1][i]);
                writer.newLine();
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    private Polygon createPolygon(String fileName) 
    {
        List<Integer> xPoints = new ArrayList<>();
        List<Integer> yPoints = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) 
        {
            String line;
            while ((line = reader.readLine()) != null) 
            {
                String[] parts = line.split(",");
                xPoints.add(Integer.parseInt(parts[0].trim()));
                yPoints.add(Integer.parseInt(parts[1].trim()));
            }
        } 
        catch (IOException e) 
        {
            System.out.println("Error loading file: " + fileName);
            e.printStackTrace();
        }
        return new Polygon(xPoints.stream().mapToInt(i -> i).toArray(), yPoints.stream().mapToInt(i -> i).toArray(), xPoints.size());
    }

    private Color getRandomColor() 
    {
        return new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));      
    }

    @Override
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(colorS);
        g2d.fillPolygon(letterS);

        g2d.setColor(colorW);
        g2d.rotate(angle, letterW.getBounds().getCenterX(), letterW.getBounds().getCenterY());
        g2d.fillPolygon(letterW);
        g2d.rotate(-angle, letterW.getBounds().getCenterX(), letterW.getBounds().getCenterY());
    }

    private void scalePolygon(Polygon polygon, double scaleFactor) 
    {
        // Store the original coordinates for reference
        int[] originalX = polygon.xpoints.clone();
        int[] originalY = polygon.ypoints.clone();

        for (int i = 0; i < polygon.npoints; i++) 
        {
            polygon.xpoints[i] = (int) (originalX[i] * scaleFactor);
            polygon.ypoints[i] = (int) (originalY[i] * scaleFactor);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int randomX = (int)(Math.random() * (panelWidth - letterS.getBounds().width));
        int randomY = (int)(Math.random() * (panelHeight - letterS.getBounds().height));
        letterS.translate(randomX - letterS.getBounds().x, randomY - letterS.getBounds().y);

        randomX = (int)(Math.random() * (panelWidth - letterW.getBounds().width));
        randomY = (int)(Math.random() * (panelHeight - letterW.getBounds().height));
        letterW.translate(randomX - letterW.getBounds().x, randomY - letterW.getBounds().y);

        colorS = getRandomColor();
        colorW = getRandomColor();

        angle += Math.toRadians(30);

        double scaleFactor = 0.8 + (Math.random() * 0.5);
        scalePolygon(letterS, scaleFactor);
        scalePolygon(letterW, scaleFactor);

        repaint();
    }

    public static void main(String[] args) 
    {
        JFrame frame = new JFrame("Dancing Letters: S and W");
        MyAnim myAnim = new MyAnim();
        frame.add(myAnim);
        frame.setSize(800, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
