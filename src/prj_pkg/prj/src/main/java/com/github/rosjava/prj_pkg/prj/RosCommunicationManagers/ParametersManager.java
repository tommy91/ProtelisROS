package com.github.rosjava.prj_pkg.prj.RosCommunicationManagers;

import java.util.List;

import org.ros.exception.ParameterNotFoundException;
import org.ros.internal.node.parameter.DefaultParameterTree;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;

import com.github.rosjava.prj_pkg.prj.PrjNode;

public class ParametersManager {
	
	private final PrjNode prjNode;
	private final ConnectedNode connectedNode;
	private final DefaultParameterTree params;
	
	public ParametersManager(PrjNode prjNode) {
		this.prjNode = prjNode;
		this.connectedNode = prjNode.getConnectedNode();
		params = (DefaultParameterTree) connectedNode.getParameterTree();
	}
	
	public void printAllParameters() {
		List<GraphName> paramsNames = params.getNames();
		for (int i = 0; i < paramsNames.size(); i++) {
			prjNode.logInfo("parametro " + Integer.toString(i) + ": " + paramsNames.get(i));
		}
	}
	
	public Integer getIntegerParam(String parameterName) {
		try {
			return params.getInteger(connectedNode.resolveName(parameterName).toString());
		} catch (ParameterNotFoundException e) {
			prjNode.logError("ParameterNotFoundException: " + e);
			printAllParameters();
			return null;
		}
	}
	
	public Boolean getBooleanParam(String parameterName) {
		try {
			return params.getBoolean(connectedNode.resolveName(parameterName).toString());
		} catch (ParameterNotFoundException e) {
			prjNode.logError("ParameterNotFoundException: " + e);
			printAllParameters();
			return null;
		}
	}
	
	public String getStringParam(String parameterName) {
		try {
			return params.getString(connectedNode.resolveName(parameterName).toString());
		} catch (ParameterNotFoundException e) {
			prjNode.logError("ParameterNotFoundException: " + e);
			printAllParameters();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getStringListParam(String parameterName) {
		try {
			return (List<String>) params.getList(connectedNode.resolveName(parameterName).toString());
		} catch (ParameterNotFoundException e) {
			prjNode.logError("ParameterNotFoundException: " + e);
			printAllParameters();
			return null;
		}
	}
	
	public Double getDoubleParam(String parameterName) {
		try {
			return params.getDouble(connectedNode.resolveName(parameterName).toString());
		} catch (ParameterNotFoundException e) {
			prjNode.logError("ParameterNotFoundException: " + e);
			printAllParameters();
			return null;
		}
	}
	
	public void setParam(String paramName, int val) {
		params.set(paramName, val);
	}
	
	public void setParam(String paramName, boolean val) {
		params.set(paramName, val);
	}
	
	public void setParam(String paramName, String val) {
		params.set(paramName, val);
	}
	
	public void setParam(String paramName, double val) {
		params.set(paramName, val);
	}
	
	public void setParam(String paramName, List<?> val) {
		params.set(paramName, val);
	}

}
