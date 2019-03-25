import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {

	public static void main(String[] args) {
		String regex = "^([a-zA-Z0-9`~!@#$%^&*()+=|{}':;,\\[\\].<>/疑问~！@#￥%……&*（）\\-—+|{}【】‘；：”“。，、？]){6,18}$";

		Matcher matcher = Pattern.compile(regex).matcher("Nu7crNBrQ%zI-");

		System.out.println(matcher.find());
	}

}
