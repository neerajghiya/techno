import java.io.IOException;


public class ExcepHandling {
	
	public static void main(String [ ] args) {
	     try{
	         System.out.println("1");
	         method();
	     }
	     catch(IOException e) {
	         System.out.println("4");
	     }
	}

	public static void method() throws IOException {
	     try {
	         System.out.println("2");
	         throw new IOException();
	     }
	     catch(IOException e) {
	         System.out.println("3");
	     }
	 }

}
