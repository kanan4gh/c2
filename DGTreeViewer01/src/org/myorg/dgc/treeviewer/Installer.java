/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.myorg.dgc.treeviewer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dg.util.DGLogger;
import org.myorg.dgc.api.Camera;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.modules.ModuleInstall;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        DGLogger.getDefault().println("インストーラのrestoredが呼ばれました。");
        //FileObject[] cs = EquipmentManager.getCameraFileObjects();
        List<FileObject> fos = EquipmentManager.getCameraFileObjects2();
        List<Camera> cameras = new ArrayList(fos.size());

        for (Iterator<FileObject> ir = fos.iterator(); ir.hasNext(); ) {
            FileObject fobj = ir.next();
            DataObject dobj = null;
            try {
                dobj = DataObject.find(fobj);
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
            Node node = dobj.getNodeDelegate();
            InstanceCookie ck = node.getCookie(InstanceCookie.class);
            if (ck == null) {
                throw new IllegalStateException("Bogus file in equips folder:" + node.getLookup().lookup(FileObject.class));
            }
            try {
                cameras.add( (Camera) ck.instanceCreate() );
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ClassNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }

        }
    }

}
