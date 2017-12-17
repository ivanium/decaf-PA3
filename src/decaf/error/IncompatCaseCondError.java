package decaf.error;

import decaf.Location;

/**
 * example：incompatible operand: - int[]<br>
 * PA2
 */
public class IncompatCaseCondError extends DecafError {

	private String type;

	public IncompatCaseCondError(Location location, String type) {
		super(location);
		this.type = type;
	}

	@Override
	protected String getErrMsg() {
		return "incompatible case expr: "+type+" given, but int expected";
	}

}
