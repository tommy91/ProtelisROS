package com.github.rosjava.prj_pkg.prj;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ros.RosCore;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;

import com.google.common.base.Preconditions;

public class MainExecutor {
	
	// Output to either standard out or a string
	public static PrintStream out = System.out;
	public static ByteArrayOutputStream outBuffer = null;

	private static RosCore core;
	private static NodeMainExecutor executor;
	// How many nodes to run
	private static int numNodes = 3;
	// Address variables
	private static String publicHostAddress = "0.0.0.0";

	private static void execute(String nodeName, NodeMain node, URI coreURI) {
		NodeConfiguration config = NodeConfiguration.newPublic(publicHostAddress, coreURI);
		config.setNodeName(nodeName);
		executor.execute(node, config);
	}

	private static void runNodes() {
		URI coreURI = core.getUri();
		out.println("Core URI: " + coreURI.toString());
		String basename = "prjnode";
		List<PrjNode> nodes = new ArrayList<>();
		for (int i = 0; i < numNodes; i++) {
			Map <String, Object> protelisEnvVars = new LinkedHashMap<>();
			if (i == 0) {
				protelisEnvVars.put("leader", true);
			}
			String nodeName = basename + Integer.toString(i);
			out.println("Executing node '" + nodeName + "'.. ");
			PrjNode node = new PrjNode(i, protelisEnvVars);
			Preconditions.checkState(node != null);
			for (PrjNode neighbor: nodes) {
				node.addNeighbor(neighbor.getDeviceUID());
				neighbor.addNeighbor(node.getDeviceUID());
			}
			nodes.add(node);
			execute(nodeName, node, coreURI);
			out.println("Completed execution node '" + nodeName + "'" );
		}
	}

	public static void main(String[] args) {

		// If the first argument is "string", then log to a string; otherwise, log to standard out
		if (args.length > 0 && args[0].equals("string")) {
			outBuffer = new ByteArrayOutputStream();
			out = new PrintStream(outBuffer);
		}
		
		out.println("Running RosCore.. ");
		
		// Setup roscore as public node
		// meaning accessible from the external network
		// on the default port: 11311
		core = RosCore.newPublic(publicHostAddress, NodeConfiguration.DEFAULT_MASTER_URI.getPort());
		core.start();
		try {
			core.awaitStart();
		} catch (Exception e) {
			e.printStackTrace();
		}
		executor = DefaultNodeMainExecutor.newDefault();
		runNodes();
	}

}
