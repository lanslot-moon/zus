import React, { useState } from 'react';
import '../styles/SidePanel.css';

const SidePanel = ({ modelData, onModelUpdate }) => {
  const [activeTab, setActiveTab] = useState('nodes'); // 'nodes', 'edges', 'validation', 'importExport'
  const [newNode, setNewNode] = useState({ id: '', label: '', type: 'SPECIFIC_TYPE' });
  const [newEdge, setNewEdge] = useState({ source: '', target: '' });
  const [newValidation, setNewValidation] = useState({ user: '', role: '', resource: '', result: false });
  const [importJson, setImportJson] = useState('');

  // 处理节点添加
  const handleAddNode = () => {
    if (!newNode.id || !newNode.label || !newNode.type) return;
    
    const updatedNodes = [...modelData.nodes, { ...newNode }];
    onModelUpdate({
      ...modelData,
      nodes: updatedNodes
    });
    
    // 重置表单
    setNewNode({ id: '', label: '', type: 'SPECIFIC_TYPE' });
  };

  // 处理边添加
  const handleAddEdge = () => {
    if (!newEdge.source || !newEdge.target) return;
    
    const updatedEdges = [...modelData.edges, { ...newEdge }];
    onModelUpdate({
      ...modelData,
      edges: updatedEdges
    });
    
    // 重置表单
    setNewEdge({ source: '', target: '' });
  };

  // 处理验证结果添加
  const handleAddValidation = () => {
    if (!newValidation.user || !newValidation.role || !newValidation.resource) return;
    
    const updatedValidation = [...modelData.validationResults, { ...newValidation }];
    onModelUpdate({
      ...modelData,
      validationResults: updatedValidation
    });
    
    // 重置表单
    setNewValidation({ user: '', role: '', resource: '', result: false });
  };

  // 处理模型导出
  const handleExportModel = () => {
    const jsonStr = JSON.stringify(modelData, null, 2);
    const blob = new Blob([jsonStr], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'model-data.json';
    a.click();
    URL.revokeObjectURL(url);
  };

  // 处理模型导入
  const handleImportModel = () => {
    try {
      const importedData = JSON.parse(importJson);
      // 验证导入的数据格式
      if (importedData.nodes && importedData.edges && Array.isArray(importedData.nodes) && Array.isArray(importedData.edges)) {
        onModelUpdate(importedData);
        setImportJson('');
        alert('模型导入成功！');
      } else {
        alert('导入失败：数据格式不正确');
      }
    } catch (error) {
      alert('导入失败：JSON格式错误');
    }
  };

  // 处理文件上传导入
  const handleFileUpload = (event) => {
    const file = event.target.files[0];
    if (!file) return;
    
    const reader = new FileReader();
    reader.onload = (e) => {
      try {
        const importedData = JSON.parse(e.target.result);
        if (importedData.nodes && importedData.edges) {
          onModelUpdate(importedData);
          alert('模型导入成功！');
        } else {
          alert('导入失败：数据格式不正确');
        }
      } catch (error) {
        alert('导入失败：JSON格式错误');
      }
    };
    reader.readAsText(file);
    event.target.value = '';
  };

  return (
    <div className="side-panel">
      <div className="panel-header">
        <h2>模型编辑器</h2>
      </div>
      
      <div className="panel-tabs">
        <button 
          className={activeTab === 'nodes' ? 'active' : ''}
          onClick={() => setActiveTab('nodes')}
        >
          节点管理
        </button>
        <button 
          className={activeTab === 'edges' ? 'active' : ''}
          onClick={() => setActiveTab('edges')}
        >
          边管理
        </button>
        <button 
          className={activeTab === 'validation' ? 'active' : ''}
          onClick={() => setActiveTab('validation')}
        >
          权限验证
        </button>
        <button 
          className={activeTab === 'importExport' ? 'active' : ''}
          onClick={() => setActiveTab('importExport')}
        >
          导入导出
        </button>
      </div>

      <div className="panel-content">
        {/* 节点管理 */}
        {activeTab === 'nodes' && (
          <div className="tab-content">
            <h3>添加节点</h3>
            <div className="form-group">
              <label>ID:</label>
              <input 
                type="text" 
                value={newNode.id} 
                onChange={(e) => setNewNode({...newNode, id: e.target.value})}
                placeholder="输入节点ID"
              />
            </div>
            <div className="form-group">
              <label>Label:</label>
              <input 
                type="text" 
                value={newNode.label} 
                onChange={(e) => setNewNode({...newNode, label: e.target.value})}
                placeholder="输入节点标签"
              />
            </div>
            <div className="form-group">
              <label>类型:</label>
              <select 
                value={newNode.type}
                onChange={(e) => setNewNode({...newNode, type: e.target.value})}
              >
                <option value="SPECIFIC_TYPE">SPECIFIC_TYPE</option>
                <option value="SPECIFIC_TYPE_AND_RELATION">SPECIFIC_TYPE_AND_RELATION</option>
              </select>
            </div>
            <button className="add-btn" onClick={handleAddNode}>添加节点</button>

            <h3 style={{ marginTop: '20px' }}>现有节点</h3>
            <div className="node-list">
              {modelData.nodes.map((node, index) => (
                <div key={index} className="node-item">
                  <strong>{node.id}</strong> - {node.type}
                </div>
              ))}
            </div>
          </div>
        )}

        {/* 边管理 */}
        {activeTab === 'edges' && (
          <div className="tab-content">
            <h3>添加边</h3>
            <div className="form-group">
              <label>源节点:</label>
              <select 
                value={newEdge.source}
                onChange={(e) => setNewEdge({...newEdge, source: e.target.value})}
              >
                <option value="">选择源节点</option>
                {modelData.nodes.map(node => (
                  <option key={node.id} value={node.id}>{node.id}</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>目标节点:</label>
              <select 
                value={newEdge.target}
                onChange={(e) => setNewEdge({...newEdge, target: e.target.value})}
              >
                <option value="">选择目标节点</option>
                {modelData.nodes.map(node => (
                  <option key={node.id} value={node.id}>{node.id}</option>
                ))}
              </select>
            </div>
            <button className="add-btn" onClick={handleAddEdge}>添加边</button>

            <h3 style={{ marginTop: '20px' }}>现有边</h3>
            <div className="edge-list">
              {modelData.edges.map((edge, index) => (
                <div key={index} className="edge-item">
                  {edge.source} → {edge.target}
                </div>
              ))}
            </div>
          </div>
        )}

        {/* 权限验证 */}
        {activeTab === 'validation' && (
          <div className="tab-content">
            <h3>添加验证结果</h3>
            <div className="form-group">
              <label>用户:</label>
              <input 
                type="text" 
                value={newValidation.user} 
                onChange={(e) => setNewValidation({...newValidation, user: e.target.value})}
                placeholder="输入用户名"
              />
            </div>
            <div className="form-group">
              <label>角色:</label>
              <input 
                type="text" 
                value={newValidation.role} 
                onChange={(e) => setNewValidation({...newValidation, role: e.target.value})}
                placeholder="输入角色"
              />
            </div>
            <div className="form-group">
              <label>资源:</label>
              <input 
                type="text" 
                value={newValidation.resource} 
                onChange={(e) => setNewValidation({...newValidation, resource: e.target.value})}
                placeholder="输入资源标识"
              />
            </div>
            <div className="form-group">
              <label>结果:</label>
              <select 
                value={newValidation.result}
                onChange={(e) => setNewValidation({...newValidation, result: e.target.value === 'true'})}
              >
                <option value={false}>否</option>
                <option value={true}>是</option>
              </select>
            </div>
            <button className="add-btn" onClick={handleAddValidation}>添加验证结果</button>

            <h3 style={{ marginTop: '20px' }}>验证结果</h3>
            <div className="validation-list">
              {modelData.validationResults.map((validation, index) => (
                <div key={index} className={`validation-item ${validation.result ? 'success' : 'error'}`}>
                  {validation.user} 是 {validation.role} of {validation.resource}? {validation.result ? '是' : '否'}
                </div>
              ))}
            </div>
          </div>
        )}

        {/* 导入导出 */}
        {activeTab === 'importExport' && (
          <div className="tab-content">
            <h3>导出模型</h3>
            <button className="export-btn" onClick={handleExportModel}>导出为JSON</button>

            <h3 style={{ marginTop: '20px' }}>导入模型 (文件)</h3>
            <input 
              type="file" 
              accept=".json" 
              onChange={handleFileUpload}
              style={{ marginBottom: '10px' }}
            />

            <h3 style={{ marginTop: '20px' }}>导入模型 (JSON文本)</h3>
            <textarea 
              value={importJson}
              onChange={(e) => setImportJson(e.target.value)}
              placeholder="粘贴JSON格式的模型数据"
              rows={10}
              className="import-textarea"
            />
            <button className="import-btn" onClick={handleImportModel}>导入模型</button>
          </div>
        )}
      </div>
    </div>
  );
};

export default SidePanel;