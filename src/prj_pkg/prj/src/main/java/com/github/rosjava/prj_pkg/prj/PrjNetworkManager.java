package com.github.rosjava.prj_pkg.prj;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.protelis.lang.datatype.DeviceUID;
import org.protelis.vm.NetworkManager;
import org.protelis.vm.util.CodePath;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Abstraction of networking used by the virtual machine: at each execution round, the VM needs
 * to be able to access the most recent state received from neighbors and to be able to update
 * the state that it is exporting to neighbors.
 * 
 * Note, however, that there is no requirement that state actually be sent or received in each
 * round: it is up to the individual implementation of a NetworkManager to best optimize in order
 * to best trade off between effective state sharing and efficiency.
 */

public class PrjNetworkManager implements NetworkManager {
	private Map<CodePath, Object> sendCache = null;
	private final Map<DeviceUID, Map<CodePath, Object>> receiveCache = new HashMap<>();
	
	// Using enableComplexMapKeySerialization() to prevent using default toString() on map keys 
	private Gson gson = (new GsonBuilder()).enableComplexMapKeySerialization().create();
	
	/** External access to sending cache */
	public String getSendCache() {
//		System.out.println("To send:");
//		System.out.println(sendCache);
		Type typeOfState = new TypeToken<LinkedHashMap<CodePath, Object>>() { }.getType();
		String gsonState = this.gson.toJson(sendCache, typeOfState);
		return gsonState;
	}
	
	/** External access to put messages into receive cache */
	public void receiveFromNeighbor(final String gsonNeighbor, final String gsonState) {
//		System.out.println("Receiving:");
//		System.out.println(gsonNeighbor);
//		System.out.println(gsonState);
		Type typeOfState = new TypeToken<LinkedHashMap<CodePath, Object>>() { }.getType();
		Map<CodePath, Object> state = this.gson.fromJson(gsonState, typeOfState);
//		Type typeOfIntegerUID = new TypeToken<IntegerUID>() { }.getType();
		IntegerUID neighbor = this.gson.fromJson(gsonNeighbor, IntegerUID.class);
		receiveCache.put(neighbor, state);
	}
	
	/** External access to note when a device is no longer a neighbor, wiping cache */
	public void removeNeighbor(final DeviceUID neighbor) {
		receiveCache.remove(neighbor);
	}
	
	/**
	 * Called by {@link ProtelisVM} during execution to collect the most recent information available 
	 * from neighbors.  The call is serial within the execution, so this should probably poll 
	 * state maintained by a separate thread, rather than gathering state during this call.
	 * @return A map associating each neighbor with its shared state.  
	 * 		The object returned should not be modified, and {@link ProtelisVM} will not change it either.
	 */
	@Override
	public Map<DeviceUID, Map<CodePath, Object>> getNeighborState() {
//		System.out.println("getNeighborState:");
//		System.out.println(receiveCache);
		return receiveCache;
	}

	/**
	 * Called by {@link ProtelisVM} during execution to send its current shared state to neighbors.
	 * The call is serial within the execution, so this should probably queue up a message to
	 * be sent, rather than actually carrying out a lengthy operations during this call.
	 * @param toSend 
	 * 		Shared state to be transmitted to neighbors.
	 */
	@Override
	public void shareState(Map<CodePath, Object> toSend) {
//		System.out.println("shareState:");
//		System.out.println(toSend);
		sendCache = toSend;
	}

}

