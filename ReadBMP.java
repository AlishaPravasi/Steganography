
import java.util.Scanner;
import edu.du.dudraw.DUDraw;
import java.io.*;
import java.awt.Color;

public class ReadBMP {

	public static void main(String[] args)
	{
		//get the name of the file from the user
		Scanner inputStream = new Scanner(System.in);
		System.out.println("What is the name of the .bmp file? ");
		String fileName = inputStream.next();
		
		//instantiate a 2D color array to store each pixel of the image
		Color[][] colors = null;
		
		//calling BMPIO's readBMPfile() method, passing the file name
		//checking to make sure the file exists
		BMPIO bmp = new BMPIO();
		try {
			bmp.readBMPFile(fileName);
		}catch(IOException e)
		{
			e.getMessage();
		}

		//as long as the color array is not null read the bmp file for validity
		BMPIO obj = new BMPIO();
		try {
			colors = obj.readBMPFile(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//generate the image
		for(int r = 0; r < colors[0].length; r++)
		{
			for(int c = 0; c < colors.length; c++)
			{
				DUDraw.setPenColor(colors[c][r]);
				DUDraw.filledRectangle(c,r,.5,.5);
			}
		}
		DUDraw.show();
	}

}
