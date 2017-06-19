package com.ra4king.circuitsimulator.gui.peers.gates;

import com.ra4king.circuitsimulator.gui.ComponentManager.ComponentManagerInterface;
import com.ra4king.circuitsimulator.gui.GuiUtils;
import com.ra4king.circuitsimulator.gui.Properties;
import com.ra4king.circuitsimulator.simulator.CircuitState;
import com.ra4king.circuitsimulator.simulator.components.gates.AndGate;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Pair;

/**
 * @author Roi Atalla
 */
public class AndGatePeer extends GatePeer<AndGate> {
	public static void installComponent(ComponentManagerInterface manager) {
		manager.addComponent(new Pair<>("Gates", "AND"),
		                     new Image(AndGatePeer.class.getResourceAsStream("/resources/AndGate.png")),
		                     new Properties());
	}
	
	public AndGatePeer(Properties properties, int x, int y) {
		super(properties, x, y);
	}
	
	@Override
	protected void ensureProperties(Properties properties) {
		properties.ensureProperty(Properties.BITSIZE);
		properties.ensureProperty(Properties.NUM_INPUTS);
	}
	
	@Override
	public AndGate buildGate(Properties properties) {
		int numInputs;
		return new AndGate(properties.getValue(Properties.LABEL),
		                   properties.getValue(Properties.BITSIZE),
		                   numInputs = properties.getValue(Properties.NUM_INPUTS),
		                   parseNegatedInputs(numInputs, properties));
	}
	
	@Override
	public void paint(GraphicsContext graphics, CircuitState circuitState) {
		super.paint(graphics, circuitState);
		
		int x = getScreenX();
		int y = getScreenY();
		int width = 4 * GuiUtils.BLOCK_SIZE;
		int height = 4 * GuiUtils.BLOCK_SIZE;
		
		graphics.beginPath();
		graphics.moveTo(x, y);
		graphics.lineTo(x, y + height);
		graphics.arc(x + width * 0.5, y + height * 0.5, width * 0.5, height * 0.5, 270, 180);
		graphics.closePath();
		
		graphics.setFill(Color.WHITE);
		graphics.setStroke(Color.BLACK);
		graphics.fill();
		graphics.stroke();
	}
}
