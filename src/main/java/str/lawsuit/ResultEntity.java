package str.lawsuit;

import java.io.Serializable;

/**
 * @author chimney
 */
public class ResultEntity implements Serializable{

	private String plaintiff;

	private String defendant;

	private String lastCaseNo;

	public String getPlaintiff() {
		return plaintiff;
	}

	public void setPlaintiff(String plaintiff) {
		this.plaintiff = plaintiff;
	}

	public String getDefendant() {
		return defendant;
	}

	public void setDefendant(String defendant) {
		this.defendant = defendant;
	}

	public String getLastCaseNo() {
		return lastCaseNo;
	}

	public void setLastCaseNo(String lastCaseNo) {
		this.lastCaseNo = lastCaseNo;
	}

	@Override
	public String toString() {
		return "ResultEntity{" +
				"plaintiff='" + plaintiff + '\'' +
				", defendant='" + defendant + '\'' +
				", lastCaseNo='" + lastCaseNo + '\'' +
				'}';
	}

	public boolean isEmpty(){
		return plaintiff == null && defendant == null && lastCaseNo == null;
	}
}
