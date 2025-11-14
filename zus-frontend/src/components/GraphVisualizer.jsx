import React, { useCallback, useMemo, useEffect } from 'react';
import ReactFlow, {
    MiniMap,
    Controls,
    Background,
    useNodesState,
    useEdgesState,
    addEdge,
    MarkerType
} from 'reactflow';
import 'reactflow/dist/style.css';
import '../styles/GraphVisualizer.css';

// 生成节点位置的辅助函数
const generateNodePosition = (id, index) => {
    const columns = 3;
    const x = (index % columns) * 300 + 150;
    const y = Math.floor(index / columns) * 200 + 100;
    return { x, y };
};

const GraphVisualizer = ({ nodes, edges }) => {
    // 将节点数据转换为reactflow格式
    const initialNodes = useMemo(() => {
        return nodes.map((node, index) => {
            const position = generateNodePosition(node.id, index);
            return {
                id: node.id,
                data: {
                    label: (
                        <div className="node-content">
                            <div className="node-id">{node.id}</div>
                            <div className="node-type">{node.type}</div>
                        </div>
                    )
                },
                position,
                style: {
                    backgroundColor: node.type === 'SPECIFIC_TYPE' ? '#1890ff' : '#52c41a',
                    borderRadius: '50%',
                    padding: '4px',
                    color: 'white',
                    width: '100px',
                    height: '100px',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center'
                },
                type: 'default'
            };
        });
    }, [nodes]);

    // 将边数据转换为reactflow格式
    const initialEdges = useMemo(() => {
        return edges.map((edge, index) => ({
            id: `edge-${index}`,
            source: edge.source,
            target: edge.target,
            type: 'default',
            markerEnd: {
                type: MarkerType.ArrowClosed,
                width: 20,
                height: 20
            },
            style: {
                strokeWidth: 2,
                stroke: '#999'
            },
            animated: true
        }));
    }, [edges]);

    // 使用reactflow的状态管理
    const [reactFlowNodes, setReactFlowNodes, onNodesChange] = useNodesState(initialNodes);
    const [reactFlowEdges, setReactFlowEdges, onEdgesChange] = useEdgesState(initialEdges);

    // 简化的实时更新逻辑
    useEffect(() => {
        // 直接使用新数据，不进行复杂的比较
        const updatedNodes = nodes.map((node, index) => {
            const existingNode = reactFlowNodes.find(n => n.id === node.id);
            // 如果节点已存在，保留其位置，否则使用新位置
            const position = existingNode ? existingNode.position : generateNodePosition(node.id, index);

            return {
                id: node.id,
                data: {
                    label: (
                        <div className="node-content">
                            <div className="node-id">{node.id}</div>
                            <div className="node-type">{node.type}</div>
                        </div>
                    )
                },
                position,
                style: {
                    backgroundColor: node.type === 'SPECIFIC_TYPE' ? '#1890ff' : '#52c41a',
                    borderRadius: '50%',
                    padding: '4px',
                    color: 'white',
                    width: '100px',
                    height: '100px',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center'
                },
                type: 'default'
            };
        });

        setReactFlowNodes(updatedNodes);
    }, [nodes]);

    useEffect(() => {
        // 直接使用新的边数据
        const updatedEdges = edges.map((edge, index) => ({
            id: `edge-${edge.source}-${edge.target}-${index}`,
            source: edge.source,
            target: edge.target,
            type: 'default',
            markerEnd: {
                type: MarkerType.ArrowClosed,
                width: 20,
                height: 20
            },
            style: {
                strokeWidth: 2,
                stroke: '#999'
            },
            animated: true
        }));

        setReactFlowEdges(updatedEdges);
    }, [edges]);

    // 处理新边的连接
    const onConnect = useCallback(
        (params) => setReactFlowEdges((eds) => addEdge(params, eds)),
        [setReactFlowEdges]
    );

    return (
        <div className="graph-visualizer" style={{ height: '100%', width: '100%' }}>
            <ReactFlow
                nodes={reactFlowNodes}
                edges={reactFlowEdges}
                onNodesChange={onNodesChange}
                onEdgesChange={onEdgesChange}
                onConnect={onConnect}
                fitView
                attributionPosition="bottom-right"
                style={{ height: '100%', width: '100%' }}
            >
                <Controls />
                <MiniMap />
                <Background variant="dots" gap={12} size={1} />
            </ReactFlow>
        </div>
    );
};

export default GraphVisualizer;