import java.util.Date;

public class ConvertTest {

	public static void main(String[] args) {
		String fileSize = "0";
		String updateTime = "1533203059000";

		Long size = Long.valueOf(fileSize);
		Date date = new Date(Long.valueOf(updateTime));

		System.out.println(size);
		System.out.println(date);
	}

}
