/**
 * 
 */
package app.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

/**
 * A carpet configuration which can be loaded from the database.
 * 
 * @author Florian Jungermann
 *
 */
@Entity
public class CarpetConfiguration {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id; // The uid of this configuration
	
	private String userID; // The user id to whom the carpet configuration belongs

	@JsonIgnoreProperties("parentConfiguration")
	@OneToMany(fetch = FetchType.EAGER, targetEntity = CarpetGroup.class, cascade = CascadeType.ALL, mappedBy = "parentConfiguration", orphanRemoval = true)
	private List<CarpetGroup> carpetGroups = new ArrayList<CarpetGroup>();
	

	public CarpetConfiguration(String userID) {
		this.userID = userID;
	}
	
	/**
	 * Do not use this constructor
	 */
	protected CarpetConfiguration() {
		
	}


	public void addCarpetGroup(CarpetGroup group) {
		this.carpetGroups.add(group);
		group.setParentConfiguration(this);
	}
	
	/** Removes the CarpetGroup from this configuration. Also sets the parentCarpetConfiguration of the CarpetGroup to null.
	 *  Does nothing if the given CarpetGroup is not found in this CarpetConfiguration.
	 * @param group
	 */
	public void removeCarpetGroup(CarpetGroup group) {
		if (this.carpetGroups.remove(group)) {
			group.setParentConfiguration(null);
		}
	}
	
	public Long getId() {
		return id;
	}


	public String getUserID() {
		return userID;
	}
	
	public void setUserID(String userID) {
		this.userID = userID;
	}


	/**
	 * Get the list of Carpet Groups. Do not use this to add a carpet Group to this configuration. Use the addCarpetGroup methode instead.
	 * @return
	 */
	public List<CarpetGroup> getCarpetGroups() {
		return carpetGroups;
	}
	
	public void setCarpetGroups(List<CarpetGroup> carpetGroups) {
		this.carpetGroups = carpetGroups;
		for (CarpetGroup group: carpetGroups) {
			group.setParentConfiguration(this);
		}
	}
	
	
}
