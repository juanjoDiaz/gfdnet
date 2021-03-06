package org.cytoscape.gfdnet.view.resultPanel;

import java.awt.CardLayout;
import java.awt.Component;
import javax.swing.Icon;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.gfdnet.controller.ResultPanelController;
import org.cytoscape.gfdnet.model.businessobjects.GeneInput;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class MainResultsView extends javax.swing.JPanel implements CytoPanelComponent {
    private final ResultPanelController resultPanelController;
    
    /**
     * Creates new form MainResultsView
     */
    public MainResultsView(ResultPanelController resultPanelController) {
        this.resultPanelController = resultPanelController;
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerPanel = new javax.swing.JPanel();
        dissimilarityLabel = new javax.swing.JLabel();
        dissimilarityValueLabel = new javax.swing.JLabel();
        organismLabel = new javax.swing.JLabel();
        organismValueLabel = new javax.swing.JLabel();
        ontologyLabel = new javax.swing.JLabel();
        ontologyValueLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        contentPanel = new javax.swing.JLayeredPane();
        summaryPanel = new org.cytoscape.gfdnet.view.resultPanel.SummaryPanel(resultPanelController);
        nodePanel = new org.cytoscape.gfdnet.view.resultPanel.NodeResultsPanel();
        edgePanel = new org.cytoscape.gfdnet.view.resultPanel.EdgeResultsPanel(resultPanelController);
        closeResultsButton = new javax.swing.JButton();

        dissimilarityLabel.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        dissimilarityLabel.setText("Dissimilarity:");

        dissimilarityValueLabel.setText(String.format("%.6f", resultPanelController.getResult().getSimilarity()));

        organismLabel.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        organismLabel.setText("Organism:");

        organismValueLabel.setText(resultPanelController.getResult().getOrganism().toString());

        ontologyLabel.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        ontologyLabel.setText("Ontology:");

        ontologyValueLabel.setText(resultPanelController.getResult().getOntology().getDescription());

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dissimilarityLabel)
                    .addComponent(organismLabel)
                    .addComponent(ontologyLabel))
                .addGap(18, 18, 18)
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ontologyValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(organismValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dissimilarityValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ontologyLabel)
                    .addComponent(ontologyValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(organismLabel)
                    .addComponent(organismValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dissimilarityLabel)
                    .addComponent(dissimilarityValueLabel)))
        );

        contentPanel.setLayout(new java.awt.CardLayout());

        summaryPanel.setPreferredSize(new java.awt.Dimension(274, 366));
        contentPanel.add(summaryPanel, "summaryPanel");
        contentPanel.add(nodePanel, "nodePanel");
        contentPanel.add(edgePanel, "edgePanel");

        closeResultsButton.setText("Close results");
        closeResultsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeResultsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(contentPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(closeResultsButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contentPanel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeResultsButton))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void closeResultsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeResultsButtonActionPerformed
        resultPanelController.dispose();
    }//GEN-LAST:event_closeResultsButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeResultsButton;
    private javax.swing.JLayeredPane contentPanel;
    private javax.swing.JLabel dissimilarityLabel;
    private javax.swing.JLabel dissimilarityValueLabel;
    private javax.swing.JPanel edgePanel;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel nodePanel;
    private javax.swing.JLabel ontologyLabel;
    private javax.swing.JLabel ontologyValueLabel;
    private javax.swing.JLabel organismLabel;
    private javax.swing.JLabel organismValueLabel;
    private javax.swing.JPanel summaryPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public CytoPanelName getCytoPanelName() {
         return CytoPanelName.EAST;
    }

    @Override
    public String getTitle() {
        return "GFD-Net";
    }

    @Override
    public Icon getIcon() {
        return null;
    }
    
    public void showSummarytInfo() {
        getCardLayout().show(contentPanel, "summaryPanel");
    }
    
    public void showGeneInfo(GeneInput gene) {
        ((NodeResultsPanel)nodePanel).showGeneDetails(gene, resultPanelController.getResult().getOntology());
        getCardLayout().show(contentPanel, "nodePanel");
    }
    
    public void showEdgeInfo(GeneInput gene1, GeneInput gene2, double similarity) {
        ((EdgeResultsPanel)edgePanel).showEdgeDetails(gene1, gene2, similarity);
        getCardLayout().show(contentPanel, "edgePanel");
    }
    
    private CardLayout getCardLayout() {
        return (CardLayout)(contentPanel.getLayout());
    }
}
