package com.github.rosjava.prj_pkg.prj;

import org.protelis.lang.datatype.DeviceUID;

/** Simple integer UIDs */
public class PrjUID implements DeviceUID {
	
	/** If you make big changes to the class, you should also change its serialVersionUID value **/
	private static final long serialVersionUID = 7168671027263227202L;
	private final String uid;
	
	PrjUID(final String uid) { 
		this.uid = uid;
	}
	
	public String getUID() {
		return uid;
	}
	
	@Override
	public boolean equals(Object alt) {
		return uid == ((PrjUID) alt).getUID();
	}
	
	@Override
	public int hashCode() {
		return uid.hashCode();
	}
	
	@Override
	public String toString() {
		return uid;
	}
}
