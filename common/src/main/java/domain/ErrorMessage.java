package domain;

public class ErrorMessage {

	private String reason;

	public ErrorMessage(String message) {
		this.reason = message;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}
