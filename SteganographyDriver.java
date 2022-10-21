
import java.util.Scanner;
import edu.du.dudraw.DUDraw;
import java.io.*;
import java.awt.Color;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.awt.Color;
import java.io.IOException;
import java.io.RandomAccessFile;

public class SteganographyDriver 
{
	public static void main(String[] args) throws BMPIOException, IOException
	{
		//stores the original image
		Color[][] colors = null;
		
		//stores the original image and secret image from embed
		Color[][] colors1 = null;
		Color[][] colors2 = null;
		
		//stores the final product from extractedImage and embeddedImage
		Color[][] extractedImage = null;
		Color[][] embeddedImage = null;
		
		//set up an object to eventually read and check incoming bmp files for validity
		BMPIO bmp = new BMPIO();
		
		//set up an object to eventually extract and embed incoming bmp files 
		Steganography obj = new Steganography();
		
		//set up scanner to ask question
		Scanner inputStream = new Scanner(System.in);
		System.out.println("Would you like to extract or embed?");
		
		//determines what method to do
		String exOrEm = inputStream.next();
		
		//different file names for embed and extract
		String publicFileName = null;
		String extractionFileName = null;
		String secretFileName = null;
		
		if(exOrEm.equals("extract"))
		{
			System.out.println("Which file would you like to extract a secret image from?");
			extractionFileName = inputStream.next();
			
			//read file to make sure it exists
			try {
				bmp.readBMPFile(extractionFileName);				
			}catch(IOException e)
			{
				SteganographyDriver.throwBMPIO("Extraction File not found");
			}
			
			//check that it's a valid bmp file
			try {
				colors = bmp.readBMPFile(extractionFileName);
				//throw new BMPIOException();
			} catch (IOException e) 
			{
				SteganographyDriver.throwBMPIO("No image here");
			}
			
			//store extracted image
			extractedImage = obj.extractSecretImage(colors);
			
			//create a large enough canvas for both images side by side
			DUDraw.setCanvasSize(extractedImage.length+colors.length,extractedImage[0].length);
			DUDraw.setXscale(0,extractedImage.length+colors.length);
			DUDraw.setYscale(0,extractedImage[0].length);
			
			//develop original image on the left
			for(int r = 0; r < colors[0].length; r++)
			{
				for(int c = 0; c < colors.length; c++)
				{
					DUDraw.setPenColor(colors[c][r]);
					DUDraw.point(c,r);
				}
			}
			
			//develop extracted image on the right
			for(int r = 0; r < extractedImage[0].length; r++)
			{
				for(int c = 0; c < extractedImage.length; c++)
				{
					DUDraw.setPenColor(extractedImage[c][r]);
					DUDraw.point(c+colors.length,r);
				}
			} 
			DUDraw.show();
		}
		
		else if(exOrEm.equals("embed"))
		{
			System.out.println("State the name of the file you would like to hide an image in: ");
			publicFileName = inputStream.next();
			System.out.println("State the name of the file you would like to hide: ");
			secretFileName = inputStream.next();

			//read public file to make sure it exists
			try {
				bmp.readBMPFile(publicFileName);				
			}catch(IOException e)
			{
				SteganographyDriver.throwBMPIO("Public image file not found");
			}
			
			//check that it's a valid bmp file
			try {
				colors1 = bmp.readBMPFile(publicFileName);
			} catch (IOException e) 
			{
				SteganographyDriver.throwBMPIO("No public image here");
			}
				
			//read secret file to make sure it exists
			try {
				bmp.readBMPFile(secretFileName);				
			}catch(IOException e)
			{
				SteganographyDriver.throwBMPIO("Secret image not found");
			}
			
			//check that it's a valid bmp file
			try {
				colors2 = bmp.readBMPFile(secretFileName);
			} catch (IOException e) 
			{
				SteganographyDriver.throwBMPIO("No secret image here");
			}
			
			//store embedded image
			try {
				embeddedImage = obj.embedSecretImage(colors1, colors2);
			} catch (Exception e)
			{
				SteganographyDriver.throwBMPIO("Null Pointer Exception");
			}
			
			//create a large enough canvas for both images side by side
			DUDraw.setCanvasSize(colors1.length+embeddedImage.length,colors1[0].length);
			DUDraw.setXscale(0,colors1.length+embeddedImage.length);
			DUDraw.setYscale(0,colors1[0].length);
			
			//develop original image on the left
			for(int r = 0; r < colors1[0].length; r++)
			{
				for(int c = 0; c < colors1.length; c++)
				{
					DUDraw.setPenColor(colors1[c][r]);
					DUDraw.filledRectangle(c,r,.5,.5);
				}
			}
			
			//develop augmented image on the right
			for(int r = 0; r < embeddedImage[0].length; r++)
			{
				for(int c = 0; c < embeddedImage.length; c++)
				{
					DUDraw.setPenColor(embeddedImage[c][r]);
					DUDraw.filledRectangle(c+colors1.length,r,.5,.5);
				}
			} 
			DUDraw.show();
			
			//implementing wrtieBMPCopy so the user can save the embedded image
			System.out.println("Would you like to save the embedded image? (Y/N)");
			String confirmation = inputStream.next();
			if(confirmation.equals("Y"))
			{
				System.out.println("What do you want to name your file? (include .bmp at the end)");
				String newName = inputStream.next();
				writeBMPCopy(publicFileName, newName, embeddedImage);
			}
			else
				System.out.println("Exiting...");
		}
		//in case the user doesn't type extract or embed
		else
		{
			System.out.println("Invalid command.");
		}
	}
	
	//this method gives a specific message for every error
	public static void throwBMPIO(String s) throws BMPIOException
	{
		//calls the BMPIOException class to do so
		throw new BMPIOException(s);
	}
	
	/* this method copies the BMP file from 'in_path' to 'out_path'
	 * and then uses the contents of the 'image' array to overwrite the
	 * image data in the appropriate place in the copy. */
	public static void writeBMPCopy(String in_path, String out_path, Color[][] image) throws IOException 
	{
	    Path src = Paths.get(in_path);
	    Path dst = Paths.get(out_path);
	    
	    // Copy the file from input to output
	    Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
	
	    RandomAccessFile out = new RandomAccessFile(out_path, "rw");
	    
	    //assign colors to 2D array
	    out.seek(54);
	    for(int r = 0; r < image[0].length; r++)
		{
			for(int c = 0; c < image.length; c++)
			{
				Color color3 = image[c][r];
				out.write(image[c][r].getBlue());
				out.write(image[c][r].getGreen());
				out.write(image[c][r].getRed());
			}
		}
	    
	    
	    //close file
	    out.close();
	}

}
