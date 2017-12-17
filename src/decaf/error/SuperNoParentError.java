package decaf.error;

import decaf.Location;

/**
 * example：test expression must have bool type<br>
 * PA2
 */
public class SuperNoParentError extends DecafError {

	private String type;
	public SuperNoParentError(Location location, String type) {
		super(location);
		this.type = type;
	}

	@Override
	protected String getErrMsg() {
		return "no parent class exist for "+this.type;
	}

}
