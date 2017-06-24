package org.cytoscape.gfdnet.controller.tasks;

import java.net.URL;
import java.util.Set;
import org.cytoscape.io.read.VizmapReader;
import org.cytoscape.io.read.VizmapReaderManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class ImportGFDNetVisualStylesTask extends AbstractTask {
    private final String GFDNET_VIZMAP_FILE = "gfdnetstyle.xml";
    private final VisualMappingManager visualMappingManager;
    private final VizmapReaderManager vizmapReaderManager;

    public ImportGFDNetVisualStylesTask(VisualMappingManager visualMappingManager, VizmapReaderManager vizmapReaderManager) {
        this.visualMappingManager = visualMappingManager;
        this.vizmapReaderManager = vizmapReaderManager;
    }

    @Override
    public void run(final TaskMonitor taskMonitor) throws Exception {
        for (VisualStyle style : visualMappingManager.getAllVisualStyles()) {
            if (style.getTitle().equals("GFD-Net")) {
                return;
            }
        }
        URL url = this.getClass().getClassLoader().getResource(GFDNET_VIZMAP_FILE);
        VizmapReader reader = vizmapReaderManager.getReader(url.toURI(), url.getPath());

        if (reader == null) {
            throw new NullPointerException("Failed to find Default Vizmap loader.");
        }

        insertTasksAfterCurrentTask(reader, new AddVisualStylesTask(reader));
    }

    private final class AddVisualStylesTask extends AbstractTask {

        private final VizmapReader reader;

        public AddVisualStylesTask(final VizmapReader reader) {
            this.reader = reader;
        }

        @Override
        public void run(final TaskMonitor taskMonitor) throws Exception {
            taskMonitor.setTitle("Loading GFD-Net Style...");
            final Set<VisualStyle> styles = reader.getVisualStyles();

            if (styles != null) {
                int count = 1;
                int total = styles.size();

                for (final VisualStyle vs : styles) {
                    if (cancelled) {
                        break;
                    }

                    visualMappingManager.addVisualStyle(vs);
                    taskMonitor.setProgress(count / total);
                    count++;
                }

                if (cancelled) {
                    for (final VisualStyle vs : styles) {
                        visualMappingManager.removeVisualStyle(vs);
                    }
                }

                taskMonitor.setProgress(1.0);
            }
        }
    }
}
