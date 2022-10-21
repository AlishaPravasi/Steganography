//all exception classes are an extension of Exception()
public class BMPIOException extends Exception
{
	//one argument constructor for the specific error messages
	public BMPIOException(String s) 
	{ 
		super(s);
	}
}
