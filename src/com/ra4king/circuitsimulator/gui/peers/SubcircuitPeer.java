package com.ra4king.circuitsimulator.gui.peers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.ra4king.circuitsimulator.gui.CircuitManager;
import com.ra4king.circuitsimulator.gui.ComponentPeer;
import com.ra4king.circuitsimulator.gui.Connection.PortConnection;
import com.ra4king.circuitsimulator.gui.GuiUtils;
import com.ra4king.circuitsimulator.gui.Properties;
import com.ra4king.circuitsimulator.gui.Properties.Direction;
import com.ra4king.circuitsimulator.gui.Properties.Property;
import com.ra4king.circuitsimulator.gui.peers.wiring.PinPeer;
import com.ra4king.circuitsimulator.simulator.CircuitState;
import com.ra4king.circuitsimulator.simulator.components.Subcircuit;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;

/**
 * @author Roi Atalla
 */
public class SubcircuitPeer extends ComponentPeer<Subcircuit> {
	public static final String SUBCIRCUIT = "Subcircuit";
	
	public SubcircuitPeer(Properties props, int x, int y) {
		super(x, y, 0, 0);
		
		Properties properties = new Properties();
		properties.ensureProperty(Properties.LABEL);
		properties.ensureProperty(Properties.LABEL_LOCATION);
		properties.mergeIfExists(props);
		
		Property<CircuitManager> subcircuitProperty = props.getProperty(SUBCIRCUIT);
		properties.setProperty(subcircuitProperty);
		
		CircuitManager subcircuitManager = subcircuitProperty.value;
		Subcircuit subcircuit = new Subcircuit(properties.getValue(Properties.LABEL), subcircuitManager.getCircuit());
		
		List<PortConnection> connections = new ArrayList<>();
		List<PinPeer> pins =
				subcircuitManager.getCircuitBoard()
				                 .getComponents().stream()
				                 .filter(componentPeer -> componentPeer instanceof PinPeer)
				                 .map(componentPeer -> (PinPeer)componentPeer)
				                 .sorted((o1, o2) -> {
					                 int diff = o1.getX() - o2.getX();
					                 if(diff == 0) {
						                 return o1.getY() - o2.getY();
					                 }
					                 return diff;
				                 })
				                 .collect(Collectors.toList());
		
		List<PinPeer> eastPins =
				pins.stream()
				    .filter(pin -> pin.getProperties().getValue(Properties.DIRECTION) == Direction.EAST)
				    .collect(Collectors.toList());
		List<PinPeer> westPins =
				pins.stream()
				    .filter(pin -> pin.getProperties().getValue(Properties.DIRECTION) == Direction.WEST)
				    .collect(Collectors.toList());
		List<PinPeer> northPins =
				pins.stream()
				    .filter(pin -> pin.getProperties().getValue(Properties.DIRECTION) == Direction.NORTH)
				    .collect(Collectors.toList());
		List<PinPeer> southPins =
				pins.stream()
				    .filter(pin -> pin.getProperties().getValue(Properties.DIRECTION) == Direction.SOUTH)
				    .collect(Collectors.toList());
		
		if(pins.size() != subcircuit.getNumPorts()) {
			throw new IllegalStateException("Pin count and ports count don't match? " + pins.size() + " vs " +
					                                subcircuit.getNumPorts());
		}
		
		setWidth(Math.max(3, Math.max(northPins.size(), southPins.size()) + 1));
		setHeight(Math.max(3, Math.max(eastPins.size(), westPins.size()) + 1));
		
		for(int i = 0; i < eastPins.size(); i++) {
			int connX = 0;
			int connY = i + 1;
			connections.add(new PortConnection(this, subcircuit.getPort(eastPins.get(i).getComponent()),
			                                   eastPins.get(i).getComponent().getName(), connX, connY));
		}
		
		for(int i = 0; i < westPins.size(); i++) {
			int connX = getWidth();
			int connY = i + 1;
			connections.add(new PortConnection(this, subcircuit.getPort(westPins.get(i).getComponent()),
			                                   westPins.get(i).getComponent().getName(), connX, connY));
		}
		
		for(int i = 0; i < northPins.size(); i++) {
			int connX = i + 1;
			int connY = getHeight();
			connections.add(new PortConnection(this, subcircuit.getPort(northPins.get(i).getComponent()),
			                                   northPins.get(i).getComponent().getName(), connX, connY));
		}
		
		for(int i = 0; i < southPins.size(); i++) {
			int connX = i + 1;
			int connY = 0;
			connections.add(new PortConnection(this, subcircuit.getPort(southPins.get(i).getComponent()),
			                                   southPins.get(i).getComponent().getName(), connX, connY));
		}
		
		init(subcircuit, properties, connections);
	}
	
	public void switchToSubcircuit(CircuitManager circuit) {
		circuit.getSimulatorWindow().switchToCircuit(getComponent().getSubcircuit());
		CircuitManager manager = circuit.getSimulatorWindow().getCircuitManager(getComponent().getSubcircuit());
		manager.getCircuitBoard()
		       .setCurrentState(getComponent().getSubcircuitState(circuit.getCircuitBoard().getCurrentState()));
	}
	
	@Override
	public List<MenuItem> getContextMenuItems(CircuitManager circuit) {
		MenuItem view = new MenuItem("View internal state");
		view.setOnAction(event -> switchToSubcircuit(circuit));
		return Collections.singletonList(view);
	}
	
	@Override
	public void paint(GraphicsContext graphics, CircuitState circuitState) {
		GuiUtils.drawName(graphics, this, getProperties().getValue(Properties.LABEL_LOCATION));
		
		graphics.setFill(Color.WHITE);
		GuiUtils.drawShape(graphics::fillRect, this);
		graphics.setStroke(Color.BLACK);
		GuiUtils.drawShape(graphics::strokeRect, this);
	}
}
