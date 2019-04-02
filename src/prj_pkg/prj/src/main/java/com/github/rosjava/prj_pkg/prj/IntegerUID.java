package com.github.rosjava.prj_pkg.prj;

import org.protelis.lang.datatype.DeviceUID;

/** Simple integer UIDs */
public class IntegerUID implements DeviceUID {
	private static final long serialVersionUID = 7168671027263227202L;
	private final int uid;
	
	IntegerUID(final int uid) { this.uid = uid; }
	
	public int getUID() { return uid; }
	
	@Override
	public boolean equals(Object alt) { return uid == ((IntegerUID) alt).getUID(); }
	
	@Override
	public int hashCode() { return uid; }
	
	@Override
	public String toString() { return Integer.toString(uid); }
}
