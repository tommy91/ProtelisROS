package com.github.rosjava.prj_pkg.prj;

import java.util.Map;

import org.protelis.lang.ProtelisLoader;
import org.protelis.lang.datatype.DeviceUID;
import org.protelis.vm.ProtelisProgram;
import org.protelis.vm.ProtelisVM;
import org.protelis.vm.impl.AbstractExecutionContext;
import org.protelis.vm.impl.SimpleExecutionEnvironment;

/**
 * Implementation of a Protelis-based device, encapsulating
 * a ProtelisVM and a network interface
 */
public class PrjExecutionContext extends AbstractExecutionContext {
	/** Device numerical identifier */
	private final IntegerUID uid;
	/** The Protelis VM to be executed by the device */
	private final ProtelisVM vm;
	/** The ROS node **/
	private final PrjNode prjNode;
	
	/**
	 * Standard constructor
	 */
	public PrjExecutionContext(PrjNode prjNode, Map<String, Object> protelisEnvVars, String protelisProgramFilename, int uid) {
		super(new SimpleExecutionEnvironment(), new PrjNetworkManager());
		// Parse a new copy of the program
		ProtelisProgram program = ProtelisLoader.parse(protelisProgramFilename);
		this.prjNode = prjNode;
		this.uid = new IntegerUID(uid);
		
		// Finish making the new device and add it to our collection
		vm = new ProtelisVM(program, this);
		
		// Put needed data into the environment
		for (String k : protelisEnvVars.keySet()) {
			getExecutionEnvironment().put(k, protelisEnvVars.get(k));
		}
	}
	
	/** 
	 * Internal-only lightweight constructor to support "instance"
	 */
	private PrjExecutionContext(PrjNode prjNode, IntegerUID uid) {
		super(new SimpleExecutionEnvironment(), new PrjNetworkManager());
		this.prjNode = prjNode;
		this.uid = uid;
		vm = null;
	}
	
	/** 
	 * Accessor for virtual machine, to allow external execution triggering 
	 */
	public ProtelisVM getVM() {
		return vm;
	}
	/** 
	 * Test actuator that dumps a string message to the output
	 */
	public void announce(String message) {
		prjNode.printLog(message);
	}

	/** 
	 * Expose the network manager, to allow external simulation of network
	 * For real devices, the NetworkManager usually runs autonomously in its own thread(s)
     */
	public PrjNetworkManager accessNetworkManager() {
		return (PrjNetworkManager)super.getNetworkManager();
	}
	
	@Override
	public DeviceUID getDeviceUID() {
		return uid;
	}

	@Override
	public Number getCurrentTime() {
		return System.currentTimeMillis();
	}

	@Override
	protected AbstractExecutionContext instance() {
		return new PrjExecutionContext(prjNode,uid);
	}

	/** 
	 * Note: this should be going away in the future, to be replaced by standard Java random
	 * @deprecated
	 */
	@Override
	public double nextRandomDouble() {
		return Math.random();
	}
	
	public void sendToNeighbors() {
		// Setup message to send
		String state = accessNetworkManager().getSendCache();
		prjNode.sendToNeighbors(state);
	}
}

