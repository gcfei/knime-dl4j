package org.knime.ext.dl4j.testing.nodes.model;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.nn.conf.layers.Layer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.ext.dl4j.base.AbstractDLNodeModel;
import org.knime.ext.dl4j.base.DLModelPortObject;
import org.knime.ext.dl4j.base.DLModelPortObjectSpec;
import org.knime.ext.dl4j.base.util.DLModelPortObjectUtils;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * This is the model implementation of DL4JModelTester.
 * 
 *
 * @author KNIME
 */
public class DL4JModelTesterNodeModel extends AbstractDLNodeModel {

	// the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(DL4JModelTesterNodeModel.class);
	
    private final static int IN_PORT1 = 0;
    private final static int IN_PORT2 = 1;
    
    private SettingsModelBoolean m_compareModels;
    private SettingsModelBoolean m_outputModels;
    
	/**
     * Constructor for the node model.
     */
    protected DL4JModelTesterNodeModel() {   
    	super(new PortType[] { DLModelPortObject.TYPE , DLModelPortObject.TYPE }, new PortType[] {
    			BufferedDataTable.TYPE, BufferedDataTable.TYPE});   	
    }
	
    @Override
    protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec) throws Exception {
    	DLModelPortObject model1 = (DLModelPortObject)inObjects[IN_PORT1];
    	DLModelPortObject model2 = (DLModelPortObject)inObjects[IN_PORT2];
    	
    	boolean appendOutput = m_outputModels.getBooleanValue();
    	boolean compareModels = m_compareModels.getBooleanValue();
    	List<BufferedDataTable> outputTables = new ArrayList<>();
    	
    	if(compareModels){
    		compareModels(model1,model2);
    	}
    	
    	if(appendOutput){
    		outputTables.add(convertDNNModelToTable(model1, exec));
    		outputTables.add(convertDNNModelToTable(model2, exec));
    	} else {
    		BufferedDataContainer container = exec.createDataContainer(createOutputSpec());
    		container.close();
    		outputTables.add(container.getTable());
    		outputTables.add(container.getTable());
    	}
    	
    	return outputTables.toArray(new BufferedDataTable[outputTables.size()]);
    }
    
    @Override
    protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs) throws InvalidSettingsException {    	
    	return new PortObjectSpec[]{createOutputSpec(),createOutputSpec()};
    }
    
	@Override
	protected List<SettingsModel> initSettingsModels() {
		List<SettingsModel> settings = new ArrayList<>();
		m_compareModels = createCompareModelsModel();
		m_outputModels = createOutputModelsModel();
		settings.add(m_compareModels);
		settings.add(m_outputModels);
		
		return settings;
	}
	
	public static SettingsModelBoolean createCompareModelsModel(){
		return new SettingsModelBoolean("compate_models", false);
	}
	public static SettingsModelBoolean createOutputModelsModel(){
		return new SettingsModelBoolean("output_models", false);
	}
    
	private DataTableSpec createOutputSpec(){
		boolean appendOutput = m_outputModels.getBooleanValue();	
		if(appendOutput){	
			DataColumnSpecCreator listColSpecCreator = new DataColumnSpecCreator("Model", DataType.getType(StringCell.class));
			DataColumnSpec colSpecs = listColSpecCreator.createSpec();
			return new DataTableSpec(colSpecs);
		}
		return new DataTableSpec();
	}
	
	private void compareModels(DLModelPortObject model1, DLModelPortObject model2) throws Exception{		
		compareLayerLists(model1.getLayers(), model2.getLayers());
		compareMLN(model1.getMultilayerLayerNetwork(), model2.getMultilayerLayerNetwork());
		compareSpec((DLModelPortObjectSpec)model1.getSpec(), (DLModelPortObjectSpec)model2.getSpec());		
	}
	
	private void compareSpec(DLModelPortObjectSpec s1, DLModelPortObjectSpec s2) throws Exception{
		if(s1 != null && s2 != null){
			if(!s1.equals(s2)){
				logger.error("Spec of model1 is different than Spec of model2");
			}
		}
	}
	
	private void compareMLN(MultiLayerNetwork m1, MultiLayerNetwork m2) throws Exception{
		if(m1 != null && m2 != null){
			String m1Conf = m1.getLayerWiseConfigurations().toJson();
			String m2Conf = m2.getLayerWiseConfigurations().toJson();
			if(!m1Conf.equals(m2Conf)){
				logger.error("MultiLayerNetwork Configuration of model1 is different from MultiLayerNetwork "
						+ "Configuration of model2");
				}
			
			
			boolean m1ContainsParams = true;
			INDArray m1params = null;
			try {
				m1params = m1.params();
			} catch (Exception e) {
				m1ContainsParams = false;
			}
			
			boolean m2ContainsParams = true;
			INDArray m2params = null;
			try {
				m2params = m2.params();
			} catch (Exception e) {
				m2ContainsParams = false;
			}
			if(m1ContainsParams && m2ContainsParams){
				if(!m1params.equals(m2params)){
					logger.error("Parameters of model1 are different from Parameter of model2");	
				}
			} else if(!m1ContainsParams && !m2ContainsParams){
				//nothing to check here
			} else if(m1ContainsParams && !m2ContainsParams){
				logger.error("model1 contains parameters but model2 doesnt");
			} else if(!m1ContainsParams && m2ContainsParams){
				logger.error("model2 contains parameters but model1 doesnt");
			}
		}
	}
	
	private void compareLayerLists(List<Layer> l1, List<Layer> l2) throws Exception{
		if(l1 != null && l2 != null){
			if(l1.size() != l2.size()){
				logger.error("Different number of Layers. Number of Layers model1: " + l1.size()
							+ " Number of Layers model2: " + l2.size());
			}
			for(int i = 0; i < l1.size(); i++){
				if(!l1.get(i).equals(l2.get(i))){
					logger.error("Layer " + (i+1) + " of model1 is different from Layer " + (i+1) 
							+ " of model2");
				}
			}
		}
	}
	
	private BufferedDataTable convertDNNModelToTable(DLModelPortObject model, ExecutionContext exec){
		BufferedDataContainer container = exec.createDataContainer(createOutputSpec());
		
		List<String> jsons = DLModelPortObjectUtils.convertLayersToJSONs(model.getLayers());
		for(int i = 0; i < jsons.size(); i++){
			StringCell c = new StringCell(jsons.get(i));
			container.addRowToTable(new DefaultRow("Layer" + i, c));
		}
		
		if(model.getMultilayerLayerNetwork() != null){
			String modelJson = model.getMultilayerLayerNetwork().getLayerWiseConfigurations().toJson();
			StringCell c = new StringCell(modelJson);
			container.addRowToTable(new DefaultRow("Model", c));	
		}
		container.close();
		return container.getTable();
	}

}

