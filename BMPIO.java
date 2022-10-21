
import java.awt.Color;
import java.io.*;
import edu.du.dudraw.DUDraw;

public class BMPIO 
{
	public Color[][] readBMPFile(String fileName) throws IOException
	{
		/* Essential information on BMP files format:
		   See https://en.wikipedia.org/wiki/BMP_file_format for more information

		   The first byte of the file must be 'B' == 0x42
		   The second byte of the file must be 'M' == 0x4D
		   At byte 10 of  the file is a four-byte integer giving the offset
		       where the pixel map starts. We require this value to be 54.
		       If it's not, we will report an error in the bitmap format
		       and fail to read the file. 
		       Note: integers in BMP files are stored in little-endian form,
		          which means that the least-significant byte comes first
		          But RandomAccessFile assumes big-endian form.
		          After reading an integer from the file, use Integer.reverseBytes(num)
		          to convert from little-endian to big-endian      
		   At byte 18 of the file are two four-byte integers giving the width
		      and height (in that order) of the image. Again, these are in little-endian form.
		      We require that each of these numbers be divisible by 4.
		      If either are not, we will report an error in the bitmap format
		      and fail to read the file.
		   At byte 28 of the file is a two-byte integer giving the number of bits per pixel
		      We require this number must be 24 (one byte for each color)
		      If it's not, we will report an error in the bitmap format
		      and fail to read the file.
		      Note: this two-byte integer is also in little-endian form,
		      so use Short.reverseBytes() after reading the number from the file.
		      
		   At byte 54, the pixel map data begins. Each pixel is represented
		      by three bytes (blue, then green, then red). The image usually starts at the
		      lower left and proceeds upwards. Recall that colors are always positive, so
		      read these from the file as unsigned bytes.
		 */
		
		//instantiate a RandomAccessFile to read the file
		RandomAccessFile raf = new RandomAccessFile(fileName, "r");
		
		//checking to make sure B and M start the file
		byte char1 = raf.readByte();
		byte char2 = raf.readByte();
		if(char1 != 'B' || char2 != 'M') 
		{
			raf.close();
			return null;
		}
		
		//gather the offset and make sure its 54
		raf.seek(10);
		int offset = raf.readInt();
		offset = Integer.reverseBytes(offset);
		if(offset != 54)
		{
			raf.close();
			return null;
		}
		
		//store the width and height of the image
		raf.seek(18);
		int width = raf.readInt();
		width = Integer.reverseBytes(width);
		raf.seek(22);
		int height = raf.readInt();
		height = Integer.reverseBytes(height);
		raf.seek(28);
		
		//store number of bits per pixel
		short bitsPerPixel = raf.readShort();
		bitsPerPixel = Short.reverseBytes(bitsPerPixel);
		if(bitsPerPixel != 24)
		{
			raf.close();
			return null;
		}
		
		//pixels begin here so we set the Color array to every pixel starting from bit 54
		raf.seek(54);
		Color[][] pixels = new Color[width][height];
		int blue, green, red;
		for(int r = 0; r < height; r++)
		{
			for(int c = 0; c < width; c++)
			{
				blue = (int) raf.readUnsignedByte();
				green = (int) raf.readUnsignedByte();
				red = (int) raf.readUnsignedByte();
				pixels[c][r] = new Color(red, green, blue);
			}
		}
		
		//set canvas size
		DUDraw.setXscale(0,width);
		DUDraw.setYscale(0,height);
		DUDraw.enableDoubleBuffering();
		
		//close file
		raf.close();
		return pixels;
	}
}
