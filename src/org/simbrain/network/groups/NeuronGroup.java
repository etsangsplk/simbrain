/*
 * Copyright (C) 2005,2007 The Authors. See http://www.simbrain.net/credits This
 * program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package org.simbrain.network.groups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.simbrain.network.interfaces.Neuron;
import org.simbrain.network.interfaces.RootNetwork;
import org.simbrain.network.interfaces.Synapse;
import org.simbrain.network.util.Comparators;

/**
 * A group of neurons.
 */
public class NeuronGroup extends Group {

    /** The neurons in this group. */
    private final List<Neuron> neuronList = new ArrayList<Neuron>();
    
    /** @see Group */
    public NeuronGroup(final RootNetwork net, final List<Neuron> neurons) {
        super(net);
        for (Neuron neuron : neurons) {
            addNeuron(neuron);
        }
        Collections.sort(neuronList, Comparators.X_ORDER);
    }

    /**
     * Create a neuron group without any initial neurons.
     * 
     * @param root parent network
     */
    public NeuronGroup(final RootNetwork root) {
        super(root);
    }
    
    @Override
    public void delete() {
        if (isMarkedForDeletion()) {
            return;
        } else {
            setMarkedForDeletion(true);
        }
        for(Neuron neuron : neuronList) {
            getParentNetwork().deleteNeuron(neuron);
        }
        if (getParentGroup() != null) {
            if (getParentGroup() instanceof Subnetwork) {
                Group parentGroup = getParentGroup();
                ((Subnetwork) parentGroup).removeNeuronGroup(this);
            }
            if (getParentGroup().isEmpty() && getParentGroup().isDeleteWhenEmpty()) {
                getParentNetwork().deleteGroup(getParentGroup());
            }            
        }
    }
    
    /**
     * @return a list of neurons
     */
    public List<Neuron> getNeuronList() {
        return Collections.unmodifiableList(neuronList);
    }
    
    /**
     * Returns true if the provided synapse is in the fan-in weight vector of
     * some node in this neuron group.
     * 
     * @param synapse the synapse to check
     * @return true if it's attached to a neuron in this group
     */
    public boolean inFanInOfSomeNode(final Synapse synapse) {
        boolean ret = false;
        for(Neuron neuron : neuronList) {
            if (neuron.getFanIn().contains(synapse)) {
                ret = true;
            }
        }
        return ret;
    }

    /**
     * Randomize fan-in for all neurons in group.
     */
    public void randomizeIncomingWeights() {
        for (Neuron neuron : neuronList) {
            neuron.randomizeFanIn();
        }
    }

    /**
     * Randomize all neurons in group.
     */
    public void randomize() {
        for (Neuron neuron : neuronList) {
            neuron.randomize();
        }
    }

    /**
     * Randomize bias for all neurons in group.
     * 
     * @param lower lower bound for randomization.
     * @param upper upper bound for randomization.
     */
    public void randomizeBiases(double lower, double upper) {
        for (Neuron neuron : neuronList) {
            neuron.randomizeBias(lower, upper);
        }
    }

    /**
     * Add neuron.
     * 
     * @param neuron neuron to add
     */
    public void addNeuron(Neuron neuron) {
        neuron.setId(getParentNetwork().getNeuronIdGenerator().getId());
        neuronList.add(neuron);
        neuron.setParentGroup(this);
    }

    /**
     * Delete the provided neuron.
     *
     * @param toDelete the neuron to delete
     */
    public void deleteNeuron(Neuron toDelete) {
  
        neuronList.remove(toDelete);
        if (isEmpty() && isDeleteWhenEmpty()) {
            delete();
        }
        //REDO
        //getParent().fireGroupChanged(this, this);
    }

    //REDO: Possibly change below to update if update goes to parent
    
    /**
     * Update all neurons.
     */
    public void updateNeurons() {
        getParentNetwork().updateNeurons(neuronList);
    }
    
    @Override
    public String toString() {
        String ret = new String();
        ret += ("Neuron Group [" + getLabel() + "] Neuron group with "
                + this.getNeuronList().size() + " neuron(s)");
        return ret;
    }   

    @Override
    public boolean isEmpty() {
        return neuronList.isEmpty();
    }
    
    /**
     * True if the group contains the specified neuron.
     * 
     * @param n neuron to check for.
     * @return true if the group contains this neuron, false otherwise
     */
    public boolean containsNeuron(final Neuron n) {
        return neuronList.contains(n);
    }
    
}
