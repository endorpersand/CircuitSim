package com.ra4king.circuitsimulator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ra4king.circuitsimulator.utils.Pair;

/**
 * @author Roi Atalla
 */
public class Simulator {
	private Set<Circuit> circuits;
	private List<Pair<Port, CircuitState>> linksToUpdate, temp;
	private final Set<List<Pair<Port, CircuitState>>> history;
	
	public Simulator() {
		circuits = new HashSet<>();
		linksToUpdate = new ArrayList<>();
		temp = new ArrayList<>();
		history = new HashSet<>();
	}
	
	public void addCircuit(Circuit circuit) {
		circuits.add(circuit);
	}
	
	public synchronized void valueChanged(Port port, CircuitState state) {
		linksToUpdate.add(new Pair<>(port, state));
	}
	
	public synchronized void step() {
		List<Pair<Port, CircuitState>> tmp = linksToUpdate;
		linksToUpdate = temp;
		temp = tmp;
		
		temp.forEach(pair -> pair.second.propagateSignal(pair.first));
		temp.clear();
	}
	
	public synchronized void stepAll() {
		history.add(new ArrayList<>(linksToUpdate));
		step();
		while(!linksToUpdate.isEmpty()) {
			history.add(new ArrayList<>(linksToUpdate));
			step();
			
			if(history.contains(linksToUpdate)) {
				throw new IllegalStateException("Oscillation apparent.");
			}
		}
		
		history.clear();
	}
}
