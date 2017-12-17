package decaf.error;

import decaf.Location;

/**
 * example：test expression must have bool type<br>
 * PA2
 */
public class BadCopyAssignError extends DecafError {

	String ltype;
	String etype;
	public BadCopyAssignError(Location location, String ltype, String etype) {
		super(location);
		this.ltype = ltype;
		this.etype = etype;
	}

	@Override
	protected String getErrMsg() {
		return "For copy expr, the source "+this.etype+" and the destination "+this.ltype+" are not same";
	}

}
