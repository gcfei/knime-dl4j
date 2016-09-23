/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * History
 *   20.09.2016 (David): created
 */
package org.knime.ext.dl4j.base.settings.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelDouble;
import org.knime.core.node.defaultnodesettings.SettingsModelDoubleBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.ext.dl4j.base.settings.IParameterSettingsModels;

/**
 *
 * @author David
 */
public abstract class AbstractMapSetParameterSettingsModels<E extends Enum<?>> implements IParameterSettingsModels<E> {

    private final Map<E, SettingsModel> m_settingsModels = new HashMap<E, SettingsModel>();

    public SettingsModel getParameter(final E key) {
        return m_settingsModels.get(key);
    }

    private void addToSet(final E key, final SettingsModel model) {
        if (!m_settingsModels.containsKey(key)) {
            m_settingsModels.put(key, model);
        }
    }

    @Override
    public List<SettingsModel> getAllInitializedSettings() {
        return new ArrayList<SettingsModel>(m_settingsModels.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setParameter(final E enumerate) throws IllegalArgumentException {
        addToSet(enumerate, createParameter(enumerate));
    }

    public boolean getBoolean(final E enumerate) {
        try {
            SettingsModelBoolean settings = (SettingsModelBoolean)m_settingsModels.get(enumerate);
            return settings.getBooleanValue();
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(
                "Settings corresponding to " + enumerate + " available but not of type boolean.", e);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("No boolean settings corresponding to " + enumerate + " available.", e);
        }
    }

    public boolean getBoolean(final E enumerate, final boolean def) {
        try {
            return getBoolean(enumerate);
        } catch (IllegalArgumentException e) {
            return def;
        }
    }

    public String getString(final E enumerate) {
        try {
            SettingsModelString settings = (SettingsModelString)m_settingsModels.get(enumerate);
            return settings.getStringValue();
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(
                "Settings corresponding to " + enumerate + " available but not of type String.", e);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("No String settings corresponding to " + enumerate + " available.", e);
        }
    }

    public String getString(final E enumerate, final String def) {
        try {
            return getString(enumerate);
        } catch (IllegalArgumentException e) {
            return def;
        }
    }

    public Integer getInteger(final E enumerate) {
        try {
            SettingsModel settings = m_settingsModels.get(enumerate);
            if (settings instanceof SettingsModelIntegerBounded) {
                SettingsModelIntegerBounded intSettings = (SettingsModelIntegerBounded)settings;
                return intSettings.getIntValue();
            } else {
                SettingsModelInteger intSettings = (SettingsModelInteger)settings;
                return intSettings.getIntValue();
            }
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(
                "Settings corresponding to " + enumerate + " available but not of type Integer.", e);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("No Integer settings corresponding to " + enumerate + " available.", e);
        }
    }

    public Integer getInteger(final E enumerate, final Integer def) {
        try {
            return getInteger(enumerate);
        } catch (IllegalArgumentException e) {
            return def;
        }
    }

    public Double getDouble(final E enumerate) {
        try {
            SettingsModel settings = m_settingsModels.get(enumerate);
            if (settings instanceof SettingsModelDoubleBounded) {
                SettingsModelDoubleBounded doubleSettings = (SettingsModelDoubleBounded)settings;
                return doubleSettings.getDoubleValue();
            } else {
                SettingsModelDouble doubleSettings = (SettingsModelDouble)settings;
                return doubleSettings.getDoubleValue();
            }
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(
                "Settings corresponding to " + enumerate + " available but not of type Double.", e);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("No Double settings corresponding to " + enumerate + " available.", e);
        }
    }

    public Double getDouble(final E enumerate, final Double def) {
        try {
            return getDouble(enumerate);
        } catch (IllegalArgumentException e) {
            return def;
        }
    }
}
