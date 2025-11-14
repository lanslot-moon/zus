import React, { useState, useCallback, useEffect } from 'react';
import GraphVisualizer from './components/GraphVisualizer';
import SidePanel from './components/SidePanel';
import './styles/App.css';

// 初始的模型数据
const initialModelData = {
  nodes: [
    { id: 'folder#editor', label: 'folder#editor', type: 'SPECIFIC_TYPE_AND_RELATION' },
    { id: 'document#writer', label: 'document#writer', type: 'SPECIFIC_TYPE_AND_RELATION' },
    { id: 'folder', label: 'folder', type: 'SPECIFIC_TYPE' },
    { id: 'document#parentFolder', label: 'document#parentFolder', type: 'SPECIFIC_TYPE_AND_RELATION' },
    { id: 'document', label: 'document', type: 'SPECIFIC_TYPE' },
    { id: 'document#viewer', label: 'document#viewer', type: 'SPECIFIC_TYPE_AND_RELATION' },
    { id: 'folder#owner', label: 'folder#owner', type: 'SPECIFIC_TYPE_AND_RELATION' },
    { id: 'folder#viewer', label: 'folder#viewer', type: 'SPECIFIC_TYPE_AND_RELATION' }
  ],
  edges: [
    { source: 'folder#editor', target: 'folder#editor' },
    { source: 'folder#editor', target: 'folder#owner' },
    { source: 'document#writer', target: 'folder#editor' },
    { source: 'document#writer', target: 'document#writer' },
    { source: 'document#parentFolder', target: 'document#parentFolder' },
    { source: 'document#viewer', target: 'document#writer' },
    { source: 'document#viewer', target: 'document#viewer' },
    { source: 'document#viewer', target: 'folder#viewer' },
    { source: 'folder#owner', target: 'folder#owner' },
    { source: 'folder#viewer', target: 'folder#editor' },
    { source: 'folder#viewer', target: 'folder#owner' },
    { source: 'folder#viewer', target: 'folder#viewer' }
  ],
  validationResults: [
    { user: 'alice', role: 'writer', resource: 'document:99', result: true },
    { user: 'bob', role: 'viewer', resource: 'document:99', result: true },
    { user: 'carol', role: 'writer', resource: 'document:99', result: false },
    { user: 'alice', role: 'viewer', resource: 'document:99', result: true },
    { user: 'eve', role: 'viewer', resource: 'document:99', result: false }
  ]
};

function App() {
  const [modelData, setModelData] = useState(initialModelData);
  const [sidePanelOpen, setSidePanelOpen] = useState(true);
  
  // 添加调试日志
  useEffect(() => {
    console.log('模型数据初始化:', modelData);
    console.log('节点数量:', modelData.nodes.length);
    console.log('边数量:', modelData.edges.length);
  }, []);

  // 使用useCallback优化性能
  const handleModelUpdate = useCallback((newData) => {
    setModelData(newData);
  }, []);

  return (
    <div className="app">
      <header className="app-header">
        <h1>模型图可视化工具</h1>
        <button 
          className="toggle-panel-btn"
          onClick={() => setSidePanelOpen(!sidePanelOpen)}
          title={sidePanelOpen ? '关闭左侧面板' : '打开左侧面板'}
        >
          {sidePanelOpen ? '关闭面板' : '打开面板'}
        </button>
      </header>
      <div className="app-content">
        <div className={`side-panel-container ${sidePanelOpen ? 'open' : 'closed'}`}>
          {sidePanelOpen && (
            <SidePanel 
              modelData={modelData} 
              onModelUpdate={handleModelUpdate}
            />
          )}
        </div>
        <div className={`graph-container ${sidePanelOpen ? 'panel-open' : 'panel-closed'}`}>
          <div style={{ height: '100%', width: '100%' }}>
            <GraphVisualizer 
              nodes={modelData.nodes} 
              edges={modelData.edges}
            />
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;