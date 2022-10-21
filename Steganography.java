import java.awt.Color;

public class Steganography 
{
	public static Color[][] extractSecretImage(Color[][] imageData) 
	{
		//initialize a Color object
		Color c = new Color(250, 15, 0);
		
		//create a 2D color array to store the secret image's pixels 
		//(same size as the original image as both should be the same size at the end)
		Color[][] secretArray = new Color[imageData.length][imageData[0].length];
		
		//get the lower 4 bits to store into secretArray
		for(int i = 0; i < imageData.length; i++) {
			for( int j = 0; j < imageData[i].length; j++) {
				c = new Color((imageData[i][j].getRed() % 16)*16, (imageData[i][j].getGreen() % 16)*16, 
						(imageData[i][j].getBlue() % 16)*16);
				secretArray[i][j] = c;
			}
		}
		
		//return the final extracted image
		return secretArray;
	}
	public static Color[][] embedSecretImage(Color[][] publicImage, Color[][] secretImage) throws Exception 
	{
		//set the rgbs for the public image and secret image to null
		int redS, blueS, greenS = 0;
		int red1, blue1, green1 = 0;
		
		//will store the final embedded image
		//same size as the public image since it should stay the same size regardless of the secret image size
		Color[][] results = new Color[publicImage.length][publicImage[0].length];
		
		//embeds the highest 4 bits from the secret image into the lowest 4 bits of public image
		for(int r = 0; r<results.length; r++) {

			for(int c = 0; c<results[0].length; c++) 
			{
				red1 = publicImage[r][c].getRed();
				red1 = red1 - (red1%16);
				green1 = publicImage[r][c].getGreen();
				green1 = green1 - (green1% 16);
				blue1 = publicImage[r][c].getBlue();
				blue1 = blue1 - (blue1%16);
				if((r<secretImage.length)&&(c<secretImage[0].length)) 
				{
					redS = secretImage[r][c].getRed();
					redS /= 16;
					greenS = secretImage[r][c].getGreen();
					greenS /= 16;
					blueS = secretImage[r][c].getBlue();
					blueS /= 16;
				}
				else 
				{
					redS = 0;
					blueS = 0;
					greenS = 0;
				}
				redS += red1;
				greenS += green1;
				blueS += blue1;
				results[r][c] = new Color (redS, greenS, blueS);
			}
		}
		
		//return the final embedded image
		return results;
  }
}
