package csv;

public class GenerateSampleFormat {

	public static void main(String[] args) {


		Integer columnNum = 50;

		for (int i = 0; i < columnNum; i++) {
			if (i == 0) {
				// num
				System.out.print("c" + i + " " + "decimal(30,10), ");
			} else if (i == columnNum - 1) {
				// string
				System.out.print("c" + i + " " + "string, ");
			} else if (i <= columnNum / 2) {
				// num
				System.out.print("c" + i + " " + "decimal(30,10), ");
			} else {
				// string
				System.out.print("c" + i + " " + "string, ");
			}
		}


	}


}
