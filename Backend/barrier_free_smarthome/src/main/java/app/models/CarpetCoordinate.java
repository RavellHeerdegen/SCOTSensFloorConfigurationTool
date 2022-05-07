package app.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Represents coordinates on the carpet
 * 
 * @author Florian Jungermann
 *
 */
@Entity
public class CarpetCoordinate {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	public int x;
	public int y;
	
	public CarpetCoordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Do not use this constructor directly
	 */
	protected CarpetCoordinate() {
		// Constructor needed for hibernate
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CarpetCoordinate other = (CarpetCoordinate) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return String.format("CarpetCoordinate[id=%l, x=%i, y=%i]", id, x, y);
	}
}
