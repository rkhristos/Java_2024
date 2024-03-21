package AWT_lab1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GeoDistance extends JFrame {

    private JTextField firstLatitudeTF, firstLongitudeTF, secondLatitudeTF, secondLongitudeTF;

    private JRadioButton degree, radian;
    private JLabel distanceLabel, distanceResult;
    private JButton resultButton, clearButton;

    public GeoDistance(){
        super("Вимірювач відстані між точками");
        super.setBounds(500,500,600,500);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container container = super.getContentPane();
        container.setLayout(new GridLayout(7,2,5,10));

        JLabel firstLatitudeL = new JLabel("Введіть географічну широту першої точки");
        firstLatitudeTF = new JTextField("",1);

        JLabel firstLongitudeL = new JLabel("Введіть географічну довготу першої точки");
        firstLongitudeTF = new JTextField("",1);

        JLabel secondLatitudeL = new JLabel("Введіть географічну широту другої точки");
        secondLatitudeTF = new JTextField("",1);

        JLabel secondLongitudeL = new JLabel("Введіть географічну довготу другої точки");
        secondLongitudeTF = new JTextField("",1);

        degree = new JRadioButton("В градусах");
        radian = new JRadioButton("В радіанах");
        ButtonGroup dimensionSelection = new ButtonGroup();
        dimensionSelection.add(degree);
        dimensionSelection.add(radian);

        resultButton = new JButton("Розрахувати відстань");
        clearButton = new JButton("Очистити поля");

        container.add(firstLatitudeL);
        container.add(firstLatitudeTF);
        container.add(firstLongitudeL);
        container.add(firstLongitudeTF);
        container.add(secondLatitudeL);
        container.add(secondLatitudeTF);
        container.add(secondLongitudeL);
        container.add(secondLongitudeTF);
        container.add(degree);
        container.add(radian);
        container.add(resultButton);
        container.add(clearButton);

        distanceLabel = new JLabel("Відстань між точками:");
        distanceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        distanceResult = new JLabel("");
        container.add(distanceLabel);
        container.add(distanceResult);

        resultButton.addActionListener(new ButtonEventManager());
        clearButton.addActionListener(new ButtonEventManager());
    }

    class ButtonEventManager implements ActionListener, java.awt.event.ActionListener {
        @Override
        public void actionPerformed(ActionEvent e){
            if (e.getSource() == resultButton){
                calculateDistance();
            } else if (e.getSource() == clearButton){
                clearFields();
            }



        }
    }

    private void clearFields() {
        firstLatitudeTF.setText("");
        firstLongitudeTF.setText("");
        secondLatitudeTF.setText("");
        secondLongitudeTF.setText("");
        distanceResult.setText("");
    }

    public void calculateDistance(){
        try {
            final double R = 6371000;
            double firstLat = Double.parseDouble(firstLatitudeTF.getText());
            double firstLong = Double.parseDouble(firstLongitudeTF.getText());
            double secondLat = Double.parseDouble(secondLatitudeTF.getText());
            double secondLong = Double.parseDouble(secondLongitudeTF.getText());

            if(degree.isSelected()){
                firstLat = Math.toRadians(firstLat);
                firstLong = Math.toRadians(firstLong);
                secondLat = Math.toRadians(secondLat);
                secondLong = Math.toRadians(secondLong);
            }

            double deltaLatitude = secondLat - firstLat;
            double deltaLongitude = secondLong - firstLong;

            double a = Math.pow(Math.sin(deltaLatitude/2),2) + Math.cos(firstLat) * Math.cos(secondLat) * Math.pow(Math.sin(deltaLongitude/2),2);
            double c =  2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = R * c;

            distanceResult.setText(String.valueOf(distance));

        } catch (NumberFormatException ex){
            JOptionPane.showMessageDialog(this, "Invalid input: Please enter valid numbers for latitude and longitude.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
