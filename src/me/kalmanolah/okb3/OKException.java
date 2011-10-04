package me.kalmanolah.okb3;

public class OKException extends Exception {

	public OKException() {}

	public OKException(String string) {
		super(string);
	}

	public OKException(String string, Throwable cause) {
		super(string, cause);
	}

	public OKException(Throwable cause) {
		super(cause);
	}

	private static final long serialVersionUID = 1L;
}
