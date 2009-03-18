/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.myorg.dgc.treeviewer;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import org.myorg.dgc.api.Camera;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 *
 * @author akira
 */
public class EditorTopComponent extends TopComponent {

    /** The cache of opened browser components. */
    private static Map<String,EditorTopComponent> editorComponents = new HashMap<String,EditorTopComponent>();

    private final JScrollPane scrollPane;
    private String name;
    private Camera camera;
    
    private EditorTopComponent(String name) {
        this.name = name;
        setName(name);
        setToolTipText(NbBundle.getMessage(EditorTopComponent.class, "HINT_EditorTopComponent"));
        
        scrollPane = new javax.swing.JScrollPane();
        
        setLayout(new java.awt.BorderLayout());
        add(scrollPane, java.awt.BorderLayout.CENTER);
    }
    
    
    public static synchronized EditorTopComponent getEditorComponent(String name) {
        EditorTopComponent win = editorComponents.get(name);
        if (win == null) {
            win = new EditorTopComponent(name);
            editorComponents.put(name, win);
        }
        return win;
    }
    
    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }
    
    @Override
    public synchronized void componentClosed() {
        editorComponents.remove(name);
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
        this.name =camera.getName();
    }
}
