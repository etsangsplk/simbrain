/*
 * Part of Simbrain--a java-based neural network kit
 * Copyright (C) 2005,2007 The Authors.  See http://www.simbrain.net/credits
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.simbrain.network.gui.dialogs.synapse;

import java.awt.GridLayout;
import java.util.Collection;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.simbrain.network.core.Synapse;
import org.simbrain.network.groups.SynapseGroup;
import org.simbrain.network.gui.NetworkUtils;
import org.simbrain.util.SimbrainConstants;
import org.simbrain.util.SimbrainConstants.Polarity;
import org.simbrain.util.Utils;
import org.simbrain.util.widgets.YesNoNull;

/**
 * Panel which displays the specific parameters of a particular synapse learning
 * rule.
 *
 * @author Jeff Yoshimi
 * @author Zoë Tosi
 *
 */
public class SynapsePropertiesExtended extends JPanel {


    /** Freeze synapse field. */
    private YesNoNull frozenDD = new YesNoNull();
    
    // TODO: Implement...
    // private TristateDropDown clippingDD = new TristateDropDown();

    /** Increment field. */
    private JFormattedTextField tfIncrement = new JFormattedTextField();

    /** Upper bound field. */
    private JFormattedTextField tfUpBound = new JFormattedTextField();

    /** Lower bound field. */
    private JFormattedTextField tfLowBound = new JFormattedTextField();

    /** Delay field. */
    private JFormattedTextField tfDelay = new JFormattedTextField();

    /**
     *
     * @param synapseList
     *            The list of synapses being edited.
     */
    public SynapsePropertiesExtended(final Collection<Synapse> synapseList) {
        fillFieldValues(synapseList);
        initializeLayout();
    }

    /**
     * Lays out the panel.
     */
    private void initializeLayout() {
        GridLayout gl = new GridLayout(0, 2);
        gl.setVgap(5);
        setLayout(gl);
        add(new JLabel("Frozen: "));
        add(frozenDD);
        // add(new JLabel("Clipping: "));
        // add(clippingDD);
        add(new JLabel("Upper Bound:"));
        add(tfUpBound);
        add(new JLabel("Lower Bound"));
        add(tfLowBound);
        add(new JLabel("Increment:"));
        add(tfIncrement);
        add(new JLabel("Delay:"));
        add(tfDelay);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    /**
     * Fills the values of the text fields based on the corresponding values of
     * the synapses to be edited.
     * @param synapseCollection
     */
    public void fillFieldValues(Collection<Synapse> synapseCollection) {
        Synapse synapseRef = synapseCollection.iterator().next();
        // Handle Upper Bound
        if (!NetworkUtils.isConsistent(synapseCollection, Synapse.class,
                "getUpperBound")) {
            tfUpBound.setValue(SimbrainConstants.NULL_STRING);
        } else {
            tfUpBound.setValue(synapseRef.getUpperBound());
        }
        // Handle Lower Bound
        if (!NetworkUtils.isConsistent(synapseCollection, Synapse.class,
                "getLowerBound")) {
            tfLowBound.setValue(SimbrainConstants.NULL_STRING);
        } else {
            tfLowBound.setValue(synapseRef.getLowerBound());
        }
        // Handle Increment
        if (!NetworkUtils.isConsistent(synapseCollection, Synapse.class,
                "getIncrement")) {
            tfIncrement.setValue(SimbrainConstants.NULL_STRING);
        } else {
            tfIncrement.setValue(synapseRef.getIncrement());
        }
        // Handle Delay
        if (synapseRef.getDelay() < 0
                || !NetworkUtils.isConsistent(synapseCollection, Synapse.class,
                        "getDelay"))
        {
            tfDelay.setValue(SimbrainConstants.NULL_STRING);
        } else {
            tfDelay.setValue(synapseRef.getDelay());
        }
        // Handle Frozen
        if (!NetworkUtils.isConsistent(synapseCollection, Synapse.class,
                "isFrozen")) {
            frozenDD.setNull();
        } else {
            frozenDD.setSelectedIndex(synapseRef.isFrozen() ? 0 : 1);
        }

    }

    /**
     * 
     * @param synapseGroup
     * @param polarity
     */
    public void fillFieldValues(SynapseGroup synapseGroup, Polarity polarity) {
        frozenDD.setSelected(synapseGroup.isFrozen(polarity));
        double increment = synapseGroup.getIncrement(polarity);
        if (!Double.isNaN(increment)) {
            tfIncrement.setValue(Double.toString(increment));
        } else {
            tfIncrement.setValue(SimbrainConstants.NULL_STRING);
        }
        double upBound = synapseGroup.getUpperBound(polarity);
        if (!Double.isNaN(upBound)) {
            tfUpBound.setValue(Double.toString(upBound));
        } else {
            tfUpBound.setValue(SimbrainConstants.NULL_STRING);
        }
        double lowBound = synapseGroup.getLowerBound(polarity);
        if (!Double.isNaN(lowBound)) {
            tfLowBound.setValue(Double.toString(lowBound));
        } else {
            tfLowBound.setValue(SimbrainConstants.NULL_STRING);
        }
        Integer delay = synapseGroup.getDelay(polarity);
        if (delay != null && delay != -1) {
            tfDelay.setValue(delay.toString());
        } else {
            tfDelay.setValue(SimbrainConstants.NULL_STRING);
        }
    }

    /**
     * Uses the values from text fields to alter corresponding values in the
     * synapse(s) being edited. Called externally to apply changes.
     * @param synapses
     */
    public void commitChanges(Collection<Synapse> synapses) {
        // Upper Bound
        double uB = Utils.doubleParsable(tfUpBound);
        if (!Double.isNaN(uB)) {
            for (Synapse s : synapses) {
                s.setUpperBound(uB);
            }
        }
        // Lower Bound
        double lB = Utils.doubleParsable(tfLowBound);
        if (!Double.isInfinite(lB)) {
            for (Synapse s : synapses) {
                s.setLowerBound(lB);
            }
        }
        // Increment
        double increment = Utils.doubleParsable(tfIncrement);
        if (!Double.isNaN(increment)) {
            for (Synapse s : synapses) {
                s.setIncrement(increment);
            }
        }
        // Delay
        double delay = Utils.doubleParsable(tfDelay);
        if (!Double.isNaN(delay)) {
            int dly = (int) delay;
            for (Synapse s : synapses) {
                s.setDelay(dly);
            }
        }
        // Frozen ?
        boolean frozen = frozenDD.getSelectedIndex() == YesNoNull
                .getTRUE();
        if (frozenDD.getSelectedIndex() != YesNoNull.getNULL()) {
            for (Synapse s : synapses) {
                s.setFrozen(frozen);
            }
        }
    }

}
