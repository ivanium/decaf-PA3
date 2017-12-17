package decaf.error;

import decaf.Location;

/**
 * example：test expression must have bool type<br>
 * PA2
 */
public class CaseBodyDiffError extends DecafError {

  String std;
  String expt;
	public CaseBodyDiffError(Location location, String std, String expt) {
		super(location);
    this.std = std;
    this.expt = expt;
	}

	@Override
	protected String getErrMsg() {
		return "type: "+this.expt+" is different with other expr's type "+this.std;
	}

}
