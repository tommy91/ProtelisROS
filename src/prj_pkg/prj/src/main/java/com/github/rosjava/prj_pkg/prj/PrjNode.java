package com.github.rosjava.prj_pkg.prj;

import java.util.List;
import java.util.Objects;

import java8.util.function.Function;

import org.apache.commons.logging.Log;

import org.protelis.lang.ProtelisLoader;
import org.protelis.lang.datatype.Field;
import org.protelis.lang.datatype.Tuple;
import org.protelis.lang.datatype.impl.ArrayTupleImpl;
import org.protelis.vm.LocalizedDevice;
import org.protelis.vm.ProtelisProgram;
import org.protelis.vm.ProtelisVM;
import org.protelis.vm.SpatiallyEmbeddedDevice;
import org.protelis.vm.impl.AbstractExecutionContext;
import org.protelis.vm.impl.SimpleExecutionEnvironment;
import org.ros.namespace.GraphName;
import org.ros.node.Node;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;

import com.github.rosjava.prj_pkg.prj.Controllers.CopterController;
import com.github.rosjava.prj_pkg.prj.Controllers.VehicleController;
import com.github.rosjava.prj_pkg.prj.RosCommunicationManagers.MavrosParametersManager;
import com.github.rosjava.prj_pkg.prj.RosCommunicationManagers.ParametersManager;
import com.github.rosjava.prj_pkg.prj.RosCommunicationManagers.PublishersManager;
import com.github.rosjava.prj_pkg.prj.RosCommunicationManagers.ServicesManager;
import com.github.rosjava.prj_pkg.prj.RosCommunicationManagers.SubscribersManager;

import org.ros.concurrent.CancellableLoop;

/**
 * A ROS node implementation which extends a Protelis AbstractExecutionContext
 */
public class PrjNode extends AbstractExecutionContext implements NodeMain, SpatiallyEmbeddedDevice, LocalizedDevice {
	
	/** Variables assigned in the configuration file **/
	private String protelisProgramFilename;
	private Integer sleepRunCycleMs;	// Milliseconds to sleep between two protelis run cycles
	
	/* Default variables */
	private final int default_system_id = 1;
	private final int default_sleepRunCycleMs = 1000;
	
	private ConnectedNode connectedNode;
	private Integer system_id;
	private String vehicleType;
	
	// Where we will print
	private Log log;
	
	// My Protelis device variables
	private ProtelisVM vm;
	private PrjUID deviceUID;
	
	private MavrosParametersManager mavrosParametersManager;
	private ParametersManager parametersManager;
	private SubscribersManager subscribersManager;
	private PublishersManager publishersManager;
	private ServicesManager servicesManager;
	private VehicleController vehicleController;

	
	public PrjNode() {
		// Calling AbstractExecutionContext constructor
		super(new SimpleExecutionEnvironment(), new PrjNetworkManager());
		
	}
	
	private void setupProtelis() {
		ProtelisProgram program = ProtelisLoader.parse(protelisProgramFilename);
		vm = new ProtelisVM(program, this);
	}
	 
	/** 
	 * Needed by Protelis AbstractExecutionContext: Internal-only lightweight constructor to support "instance" 
	 */
	private PrjNode(PrjUID deviceUID) {
		super(new SimpleExecutionEnvironment(), new PrjNetworkManager());
		this.deviceUID = deviceUID;
		vm = null;
	}
	
	/** 
	 * Requested by Protelis AbstractExecutionContext
	 */
	@Override
	protected AbstractExecutionContext instance() {
		return new PrjNode(deviceUID);
	}
	
	/** 
	 * Requested by Protelis AbstractExecutionContext
	 */
	@Override
	public PrjUID getDeviceUID() {
		return deviceUID;
	}
	
	/** 
	 * Requested by Protelis AbstractExecutionContext
	 */
	@Override
	public Number getCurrentTime() {
		return System.currentTimeMillis();
	}

	/** 
	 * Requested by Protelis AbstractExecutionContext
	 * Note: this should be going away in the future, to be replaced by standard Java random
	 * @deprecated
	 */
	@Override
	public double nextRandomDouble() {
		return Math.random();
	}
	
	/** 
	 * Requested by Protelis SpatiallyEmbeddedDevice interface
	 * Note: Get the distance between the current device and its neighbors.
     *       Distance must be positive.
     */
	@Override
	public Field nbrRange() {
		return buildField(new Function<Object,Double>() {
			public Double apply(final Object otherNode) {
				return getDistance((PrjNode)otherNode);
			}
		}, this);
	}
	
	/** 
	 * Requested by Protelis LocalizedDevice interface
	 * @return field of directions to other devices
	 */
	@Override
	public Field nbrVector() {
		return buildField(new Function<Object,Tuple>() {
			@Override
			public Tuple apply(final Object otherNode) {
				return getVectorDistance((PrjNode)otherNode);
			}
		}, this);
	}
	
	public double getDistance(final PrjNode otherNode) {
        final Tuple c1 = getCoordinates();
        final Tuple c2 = otherNode.getCoordinates();
        double latComp = Math.pow((Double) c2.get(0) - (Double) c1.get(0), 2);
        double lonComp = Math.pow((Double) c2.get(1) - (Double) c1.get(1), 2);
        double altComp = Math.pow((Double) c2.get(2) - (Double) c1.get(2), 2);
        return Math.sqrt(latComp + lonComp + altComp);
    }
	
	private Tuple getVectorDistance(final PrjNode otherNode) {
		final Tuple c1 = getCoordinates();
        final Tuple c2 = otherNode.getCoordinates();
        Tuple tmp = otherNode.getCoordinates();
        for (int i = 0; i < c1.size(); i++) {
            tmp = tmp.set(i, (Double) c2.get(i) - (Double) c1.get(i));
        }
        return tmp;
    }
	
	/** 
	 * Requested by Protelis LocalizedDevice interface
	 * @return coordinates of the current device
	 */
	@Override
	public Tuple getCoordinates() {
		final List<Double> cd = vehicleController.getCoordinates();
		Tuple c = new ArrayTupleImpl(0, cd.size());
		for (int i = 0; i < cd.size(); i++) {
			c = c.set(i, cd.get(i));
	    }
	    return c;
	}
	
	/** 
	 * Accessor for virtual machine, to allow external execution triggering 
	 */
	public ProtelisVM getVM() {
		return vm;
	}

	/** 
	 * Expose the network manager, to allow external simulation of network
	 * For real devices, the NetworkManager usually runs autonomously in its own thread(s)
     */
	public PrjNetworkManager accessNetworkManager() {
		return (PrjNetworkManager)super.getNetworkManager();
	}
	
	/**
	 * Requested by ROS NodeMain interface 
	 */
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("rosjava/prjnode");
	}
	
	/**
	 * Requested by ROS NodeMain interface 
	 */
	@Override
	public void onStart(final ConnectedNode connectedNode) {
		
		this.connectedNode = connectedNode;
		
		// Get ROS default logger
		log = connectedNode.getLog();
		
		logInfo("Node name: " + getNodeName().toString());
		logInfo("Namespace: " + getNamespace().toString());
		
		mavrosParametersManager = new MavrosParametersManager(this);
		parametersManager = new ParametersManager(this);
		
		// Read the input parameters from the Parameter Server
		readInputParameters();
		
		subscribersManager = new SubscribersManager(this);
		publishersManager = new PublishersManager(this);
		servicesManager = new ServicesManager(this);
		
		
		// Setup the controller corresponding to the current vehicle type
		setupVehicle();
		
		// Blocking here until the ardupilot device state is connected
		// ardupilotManager.waitArdupilotSystemsOnline();
				
		// Setup mavros listeners and service callers
//		mavrosSubscribersManager.subscribeInterestedTopics();
		vehicleController.waitVehicleReady();
		servicesManager.setupPrjServices();
		publishersManager.setupPrjPublishers();

		
		deviceUID = accessNetworkManager().setup(this);
		
		setupProtelis();

		// Execute in loop the protelis program
		logInfo("run synchronizer");
		runSynchronizer();
	}
	
	/** 
	 * Requested by ROS NodeMain interface 
	 */
	@Override
	public void onShutdown(Node node) {
	}
	
	/** 
	 * Requested by ROS NodeMain interface
	 */
	@Override
	public void onShutdownComplete(Node node) {
	}
	
	/** 
	 * Requested by ROS NodeMain interface
	 */
	@Override
	public void onError(Node node, Throwable throwable) {
	}
	
	private void readInputParameters() {
		logInfo("Reading the parameters:");
		system_id = parametersManager.getIntegerParam("~system_id");
		if (system_id != null) {
			logInfo("System ID: " + Integer.toString(system_id));
		}
		else {
			system_id = default_system_id;
			logError("Default System ID: " + Integer.toString(system_id));
		}
		protelisProgramFilename = parametersManager.getStringParam("~protelis/protelisProgramFilename");
		if (protelisProgramFilename == null) {
			exitWithError("Cannot run without a protelisProgramFilename.. review the prj_configuration file.");
		}
		logInfo("Protelis program filename: " + protelisProgramFilename);
		sleepRunCycleMs = parametersManager.getIntegerParam("~protelis/sleepRunCycleMs");
		if (sleepRunCycleMs != null) {
			logInfo("Protelis sleep run cycle: " + Integer.toString(sleepRunCycleMs));
		}
		else {
			sleepRunCycleMs = default_sleepRunCycleMs;
			logError("Default protelis sleep run cycle: " + Integer.toString(sleepRunCycleMs));
		}
	}
	
	/**
	 * Here you need to create the appropriate ArdupilotManager instance of your vehicle
	 * based on the vehicle_type in the parameters
	 */
	private void setupVehicle() {
		vehicleType = parametersManager.getStringParam("~vehicle_type");
		if (vehicleType == null) {
			exitWithError("The vehicle type is not present in the parameters.. cannot execute without it. Abort and exit.");
		}
		else if (Objects.equals(vehicleType, "copter")) {
			logInfo("Correctly setup vehicle type to '" + vehicleType + "'");
			vehicleController = new CopterController(this);
		}
		// TODO add other vehicles
		else {
			exitWithError("The vehicle type '" + vehicleType + "' is unknown. Abort and exit.");
		}
	}
	
	public void exitWithError(String message) {
		logError(message);
		connectedNode.shutdown();
		System.exit(1);
	}
	
	/**
	 * NOTE: The method getExecutionEnvironment() is implemented in AbstractExecutionContext
	 */
	
	public MavrosParametersManager getMavrosParametersManager() {
		return mavrosParametersManager;
	}
	
	public ParametersManager getParametersManager() {
		return parametersManager;
	}
	
	public ServicesManager getServicesManager() {
		return servicesManager;
	}
	
	public SubscribersManager getSubscribersManager() {
		return subscribersManager;
	}
	
	public PublishersManager getPublishersManager() {
		return publishersManager;
	}
	
	public ConnectedNode getConnectedNode() {
		return connectedNode;
	}
	
	public GraphName getNodeName() {
		return connectedNode.getName();
	}
	
	public GraphName getNamespace() {
		return connectedNode.getResolver().getNamespace();
	}
	
	public VehicleController getVehicleController() {
		return vehicleController;
	}

	public void logInfo(String message) {
		log.info(message);
	}
	
	public void logError(String message) {
		log.error(message);
	}
	
	private void runSynchronizer() {
		CancellableLoop synchronizer = new CancellableLoop() {
			@Override
			protected void loop() throws InterruptedException {
				getVM().runCycle();
				Thread.sleep(sleepRunCycleMs);
			}
		};
		connectedNode.executeCancellableLoop(synchronizer);
	}

}
