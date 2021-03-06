/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datavisualizer.threed.multiview;

import datavisualizer.DenseDistanceMatrix;
import datavisualizer.DistanceMatrix;
import datavisualizer.InstancePositions;
import datavisualizer.threed.positioncontrollers.ForceDistanceMatrixVisualization3D;
import datavisualizer.threed.positioncontrollers.InstancePositions3DController;
import datavisualizer.threed.positioncontrollers.Visualization3DPositionController;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import tsne.TSneFromDistanceMatrix;
import tsne.java.com.jujutsu.tsne.PrincipalComponentAnalysis;
import tsne.java.com.jujutsu.tsne.barneshut.BHTSne;
import util.Pair;

/**
 *
 * @author santi
 */
public class DataToolsGUI extends JFrame {
    
    Visualization3DMultiViewGUI m_parent = null;
    
    public DataToolsGUI(String name, int width, int height, Visualization3DMultiViewGUI parent) {
        super(name);
        setPreferredSize(new Dimension(width, height));
        setSize(new Dimension(width, height));
        m_parent = parent;

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        // add Euclidean distance matrix:
        JButton euclideanButton = new JButton("Add Euclidean Distance Matrix");
        euclideanButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean found = false;
                for(Pair<String, DistanceMatrix> tmp:m_parent.matrices) {
                    if (tmp.m_a.equals("Euclidean")) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    double [][]m = euclideanMatrix(m_parent.originalData);
                    DistanceMatrix dm = new DenseDistanceMatrix(m_parent.names, m);
                    m_parent.matrices.add(new Pair<>("Euclidean", dm));
                    m_parent.controllers.add(new Pair<String, Visualization3DPositionController>("Euclidean", new ForceDistanceMatrixVisualization3D(dm, m_parent.visualization)));
                    m_parent.controllerSelectionBox.addItem("Euclidean");
                }
            }
        });
        if (m_parent.originalData==null) euclideanButton.setEnabled(false);
        p.add(euclideanButton);

        // add PCA positions:
        JButton PCAButton = new JButton("Add PCA Positions");
        PCAButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean found = false;
                for(Pair<String, InstancePositions> tmp:m_parent.positions) {
                    if (tmp.m_a.equals("PCA")) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    double [][]m = PCAMatrix(m_parent.originalData, 3);
                    InstancePositions ip = new InstancePositions(m.length, m[0].length);
                    ip.positions = m;
                    m_parent.positions.add(new Pair<>("PCA", ip));
                    m_parent.controllers.add(new Pair<String, Visualization3DPositionController>("PCA", new InstancePositions3DController(ip, m_parent.visualization)));
                    m_parent.controllerSelectionBox.addItem("PCA");
                }
            }
        });
        if (m_parent.originalData==null) PCAButton.setEnabled(false);
        p.add(PCAButton);

        
        // add t-SNE positions:
        JButton tsneButton = new JButton("Add t-SNE Positions");
        tsneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean found = false;
                for(Pair<String, InstancePositions> tmp:m_parent.positions) {
                    if (tmp.m_a.equals("t-SNE")) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    double [][]m = tSNEMatrix(m_parent.originalData, 3, 20.0, 1000);
                    InstancePositions ip = new InstancePositions(m.length, m[0].length);
                    ip.positions = m;
                    m_parent.positions.add(new Pair<>("t-SNE", ip));
                    m_parent.controllers.add(new Pair<String, Visualization3DPositionController>("t-SNE", new InstancePositions3DController(ip, m_parent.visualization)));
                    m_parent.controllerSelectionBox.addItem("t-SNE");
                }
            }
        });
        if (m_parent.originalData==null) tsneButton.setEnabled(false);
        p.add(tsneButton);
        
        // add t-SNE from distance matrix positions:
        JButton tsneDButton = new JButton("Add t-SNE Positions from Distance Matrix");
        tsneDButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean found = false;
                for(Pair<String, InstancePositions> tmp:m_parent.positions) {
                    if (tmp.m_a.equals("t-SNE(from distance matrix)")) {
                        found = true;
                        break;
                    }
                }
                DenseDistanceMatrix ddm = null;
                for(Pair<String, DistanceMatrix> tmp:m_parent.matrices) {
                    if (tmp.m_b instanceof DenseDistanceMatrix) {
                        ddm = (DenseDistanceMatrix)tmp.m_b;
                        break;
                    }
                }
                if (!found) {
                    double [][]m = tSNEFromDistanceMatrix(ddm.getMatrix(), 3, 20.0, 500);
                    InstancePositions ip = new InstancePositions(m.length, m[0].length);
                    ip.positions = m;
                    m_parent.positions.add(new Pair<>("t-SNE(from distance matrix)", ip));
                    m_parent.controllers.add(new Pair<String, Visualization3DPositionController>("t-SNE(from distance matrix)", new InstancePositions3DController(ip, m_parent.visualization)));
                    m_parent.controllerSelectionBox.addItem("t-SNE(from distance matrix)");
                }
            }
        });
        p.add(tsneDButton);
        add(p);
    }
    
    
    double [][] euclideanMatrix(double [][]data)
    {
        int n = data.length;
        int nf = data[0].length;
                
        double m[][] = new double[n][n];
        
        for(int i = 0;i<n;i++) {
            m[i][i] = 0;
            for(int j = i+1;j<n;j++) {
                double d = 0;
                for(int k = 0;k<nf;k++) {
                    d += (data[i][k]-data[j][k])*(data[i][k]-data[j][k]);
                }
                d = Math.sqrt(d);
                m[i][j] = d;
                m[j][i] = d;
            }
        }
        
        return m;
    }

    
    double [][] PCAMatrix(double [][]data, int no_dims)
    {
        PrincipalComponentAnalysis pca = new PrincipalComponentAnalysis();
        double [][]Y = pca.pca(data, no_dims);
        return Y;
    }
    
    
    double [][] tSNEMatrix(double [][]data, int no_dims, double perplexity, int iterations)
    {
        BHTSne tsne = new BHTSne();
        
        double maxValue = 0;
        for(int i = 0;i<data.length;i++) {
            for(int j = 0;j<data[0].length;j++) {
                if ((i==0 && j==0) || data[i][j]>maxValue) {
                    maxValue = data[i][j];
                }
            }
        }
        
        double [][]data2 = new double[data.length][data[0].length];
        for(int i = 0;i<data.length;i++) {
            for(int j = 0;j<data[0].length;j++) {
                data2[i][j]=data[i][j]/maxValue;
            }
        }
        
        double [][]Y = tsne.tsne(data2, no_dims, data2[0].length, perplexity, iterations, false);
        return Y;
    }    


    double [][] tSNEFromDistanceMatrix(double [][]m, int no_dims, double perplexity, int iterations)
    {        
        double maxValue = 0;
        for(int i = 0;i<m.length;i++) {
            for(int j = 0;j<m[0].length;j++) {
                if ((i==0 && j==0) || m[i][j]>maxValue) {
                    maxValue = m[i][j];
                }
            }
        }
        
        double [][]data2 = new double[m.length][m[0].length];
        for(int i = 0;i<m.length;i++) {
            for(int j = 0;j<m[0].length;j++) {
                data2[i][j]=m[i][j]/maxValue;
            }
        }
        
        TSneFromDistanceMatrix tsne = new TSneFromDistanceMatrix();
        double [][]Y = tsne.tsne(data2, no_dims, perplexity, iterations);
        return Y;
    }    

}
