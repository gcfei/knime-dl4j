/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
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
 *   Feb 10, 2020 (Perla Gjoka, KNIME GmbH, Konstanz, Germany): created
 */
package org.knime.ext.dl4j.base.nodes.io.filehandling.dl4jmodel.writer;

import java.io.OutputStream;
import java.util.zip.ZipOutputStream;

import org.knime.core.node.ExecutionContext;
import org.knime.core.node.context.NodeCreationConfiguration;
import org.knime.core.node.port.PortObject;
import org.knime.ext.dl4j.base.DLModelPortObject;
import org.knime.ext.dl4j.base.util.DLModelPortObjectUtils;
import org.knime.filehandling.core.node.portobject.writer.PortObjectToFileWriterNodeModel;
import org.knime.filehandling.core.node.portobject.writer.PortObjectWriterNodeConfig;

/**
 * This class creates the node model for the Dl4J model writer node.
 *
 * @author Perla Gjoka, KNIME GmbH, Konstanz, Germany
 */
final class Dl4JModelWriter2NodeModel extends PortObjectToFileWriterNodeModel<PortObjectWriterNodeConfig> {

    /**
     * Constructor.
     *
     * @param creationConfig the node creation configuration
     * @param config the writer configuration
     */
    protected Dl4JModelWriter2NodeModel(final NodeCreationConfiguration creationConfig,
        final PortObjectWriterNodeConfig config) {
        super(creationConfig, config);
    }

    @Override
    protected void write(final PortObject object, final OutputStream outputStream, final ExecutionContext exec)
        throws Exception {
        try (final ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
            DLModelPortObjectUtils.saveModelToZip((DLModelPortObject)object, true, true, zipOut);
        }
    }

}
