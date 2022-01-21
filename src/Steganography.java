import java.awt.*;
import java.util.ArrayList;

import javafx.scene.image.PixelReader;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Steganography {

    private Image image;
    String[] encodedText;

    public Steganography (){
    }

    public void setImage(Image image) {
        this.image = image;
        encodedText = new String[(int)(image.getHeight() * image.getWidth())];
    }

    public Color[][] getPixelData() {
        PixelReader pr = image.getPixelReader();
        int rows = (int) image.getHeight();
        int cols = (int) image.getWidth();
        Color[][] pixelData = new Color[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                pixelData[i][j] = pr.getColor(i, j);
            }
        }
        return pixelData;
    }





    public Image applySteganography() {

        Color[][] originalPixelArray = getPixelData();
        Color[][] pixelArray = new Color[originalPixelArray.length][originalPixelArray[0].length];

        System.out.println("Hiding message");
        int counter = 0;
        for (int i=0; i<originalPixelArray.length; i++) {
            for (int j=0; j<originalPixelArray[0].length; j++) {
                    pixelArray[i][j] = hideByteInFourBytes(originalPixelArray[i][j], encodedText[counter]);
                    counter ++;
            }
        }

        WritableImage wimg = new WritableImage(pixelArray.length, pixelArray[0].length);
        PixelWriter pw = wimg.getPixelWriter();

        for (int i = 0; i < pixelArray.length; i++) {
            for (int j = 0; j < pixelArray[0].length; j++) {
                pw.setColor(i, j, pixelArray[i][j]);
            }
        }
        return wimg;
    }

    private Color hideByteInFourBytes(Color color, String binary) {
        int red = (int) (color.getRed()*255);
        int green = (int) (color.getGreen()*255);
        int blue = (int) (color.getBlue()*255);
        int alpha = (int) (color.getOpacity()*255);
        //System.out.println(binary);
        String[] twoBytes = {binary.substring(0,2), binary.substring(2,4), binary.substring(4,6), binary.substring(6,8)};
        int[] num = new int[4];
        for(int i=0; i< twoBytes.length; i++) {
            num[i] = Integer.parseInt(twoBytes[i],2);
            //System.out.println(twoBytes[i] + " = " + num[i]);
        }
        red = replaceLastTwoBitsWith(red, twoBytes[0]);
        green = replaceLastTwoBitsWith(green, twoBytes[1]);
        blue = replaceLastTwoBitsWith(blue, twoBytes[2]);
        alpha = replaceLastTwoBitsWith(alpha, twoBytes[3]);
        //System.out.println(to8bitBinary(red));
        //System.out.println(to8bitBinary(green));
        //System.out.println(to8bitBinary(blue));
        //System.out.println(to8bitBinary(alpha));
        //System.out.println();
        Color newColor = new Color((double)red/255,(double)green/255,(double)blue/255,(double)alpha/255);
        return newColor;
    }

    private int replaceLastTwoBitsWith(int intToAlter, String lastTwoBytes) {
        String binary = Integer.toBinaryString(intToAlter);

        while (binary.length() < 8) {
            binary = "0" + binary;
        }
        //System.out.println(binary);
        binary = binary.substring(0,6);
        binary = binary + lastTwoBytes;
        //System.out.println(binary);
        //System.out.println(Integer.parseInt(binary, 2) + "\n");
        return Integer.parseInt(binary, 2);
    }


    public void encodeText(String string) {

        for (int i=0; i<string.length(); i++) {
            //System.out.println(string.charAt(i) + ": " + to8bitBinary(string.charAt(i)));
            encodedText[i] = to8bitBinary(string.charAt(i));
        }
        for (int j=string.length(); j<encodedText.length;j++) {
            encodedText[j] = to8bitBinary('-');
        }

    }

    private String to8bitBinary(char toBinaryChar) {
        String binary = Integer.toBinaryString(toBinaryChar);
        while (binary.length() < 8) {
            binary = "0" + binary;
        }
        return binary;
    }

    private String to8bitBinary(int integer) {
        String binary = Integer.toBinaryString(integer);
        while (binary.length() < 8) {
            binary = "0" + binary;
        }
        return binary;
    }


    public void extract(Image image) {
        setImage(image);
        Color[][] pixelData = getPixelData();
        System.out.println("\nExtracting data:");
        for(int i=0; i<pixelData.length;i++) {
            for(int j=0; j<pixelData.length;j++) {
                Color color = pixelData[i][j];
                int red = (int) (color.getRed()*255);
                int green = (int) (color.getGreen()*255);
                int blue = (int) (color.getBlue()*255);
                int alpha = (int) (color.getOpacity()*255);

                String string = "";
                String addition;

                addition = to8bitBinary(red);
                string += addition.substring(addition.length()-2);
                //System.out.println(addition);

                addition = to8bitBinary(green);
                string += addition.substring(addition.length()-2);
                //System.out.println(addition);

                addition = to8bitBinary(blue);
                string += addition.substring(addition.length()-2);
                //System.out.println(addition);

                addition = to8bitBinary(alpha);
                string += addition.substring(addition.length()-2);
                //System.out.println(addition);

                int binary = Integer.parseInt(string, 2);
                //System.out.println(binary);
                char c = (char) binary;
                System.out.print(c);
                //System.out.println(string + ": " + c);
                //System.out.println(string);
            }
        }
    }
}
