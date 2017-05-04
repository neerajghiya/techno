public class RandomNumberGeneration {

	public static void main(String[] args) {
		int numberOfDigits = 9;
		createRandomNumber(numberOfDigits);

	}

	private static void createRandomNumber(int numberOfDigits) {
		long timeSeed = System.nanoTime(); // to get the current date time value

        double randSeed = Math.random() * 1000; // random number generation
        
       // mixing up the time and rand number. variable timeSeed will be unique
      //  variable rand will 
      //  ensure no relation between the numbers
        long midSeed = (long) (timeSeed * randSeed); 

        String s = midSeed + "";
        String subStr = s.substring(0, numberOfDigits);

        int finalSeed = Integer.parseInt(subStr);    // integer value

        System.out.println(finalSeed);
		
	}

}
