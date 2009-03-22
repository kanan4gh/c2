/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.myorg.dgc.treeviewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.dg.util.ContentChangeEvent;
import org.dg.util.ContentChangeListener;
import org.myorg.dgc.api.Acs;
import org.myorg.dgc.api.Camera;
import org.myorg.dgc.api.Recorder;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;

/**
 *
 * @author akira
 */
public class EquipmentManager {

    private static EquipmentManager singleton = new EquipmentManager();
    public static EquipmentManager getDefault() { return singleton;}
    private EquipmentManager() {}

    public static List<FileObject> getCameraFileObjects2() {
        FileObject f = Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject(DGCNode.RootCameraPath);
        return getCameraImpl2(f);
    }

    /*
     * fの配下の葉ノードのリストを取得する。
     */
    private static List<FileObject> getCameraImpl2(FileObject f) {
        List<FileObject> ans = new ArrayList();
        if (f.isData()) {
            ans.add(f);
        } else {
            FileObject[] fs = f.getChildren();
            for (int i = 0; i < fs.length; i++) {
                ans.addAll(getCameraImpl2(fs[i]));
            }
        }
        return ans;
    }

    //PropertyChangeListener[] pcls = (PropertyChangeListener[]) listeners.toArray(new PropertyChangeListener[0]);
    private Map cameras = new HashMap();
    private Map recorders = new HashMap();
    private Map acss = new HashMap();

    public Camera[] getCameras() {
        return (Camera[])cameras.values().toArray(new Camera[]{});
    }

    public Recorder[] getRecorders() {
        return (Recorder[])recorders.values().toArray(new Recorder[]{});
    }

    public Acs[] getAcss() {
        return (Acs[])acss. values().toArray(new Acs[]{});
    }

    public void addCamera(Camera camera) {
        cameras.put(camera.getName(), camera);
        fireCameraContentChange(camera);
    }

    public void removeCamera(Camera camera) {
        cameras.remove(camera.getName());
        fireCameraContentChange(camera);
    }

    public void addRecorder(Recorder recorder) {
        recorders.put(recorder.getName(), recorder);
        fireRecorderContentChange(recorder);
    }

    public void removeRecorder(Recorder recorder) {
        recorders.remove(recorder.getName());
        fireRecorderContentChange(recorder);
    }

    public void addAcs(Acs acs) {
        acss.put(acs.getName(), acs);
        fireAcsContentChange(acs);
    }

    public void removeAcs(Acs acs) {
        acss.remove(acs.getName());
        fireAcsContentChange(acs);
    }

    private List cameraListeners = Collections.synchronizedList(new LinkedList());
    public void addCameraContentChangeListener(ContentChangeListener ccl) {
        cameraListeners.add(ccl);
    }
    public void removeCameraContentChangeListener(ContentChangeListener pcl) {
        cameraListeners.remove(pcl);
    }
    private void fireCameraContentChange(Object value) {
        ContentChangeListener[] pcls = (ContentChangeListener[]) cameraListeners.toArray(new ContentChangeListener[0]);
        for (int i = 0; i < pcls.length; i++) {
            pcls[i].contentChange(new ContentChangeEvent(this, value));
        }
    }

    private List recorderListeners = Collections.synchronizedList(new LinkedList());
    public void addRecorderContentChangeListener(ContentChangeListener ccl) {
        recorderListeners.add(ccl);
    }
    public void removeRecorderContentChangeListener(ContentChangeListener pcl) {
        cameraListeners.remove(pcl);
    }
    private void fireRecorderContentChange(Object value) {
        ContentChangeListener[] pcls = (ContentChangeListener[]) recorderListeners.toArray(new ContentChangeListener[0]);
        for (int i = 0; i < pcls.length; i++) {
            pcls[i].contentChange(new ContentChangeEvent(this, value));
        }
    }

    private List acsListeners = Collections.synchronizedList(new LinkedList());
    public void addAcsContentChangeListener(ContentChangeListener ccl) {
        acsListeners.add(ccl);
    }
    public void removeAcsContentChangeListener(ContentChangeListener pcl) {
        acsListeners.remove(pcl);
    }
    private void fireAcsContentChange(Object value) {
        ContentChangeListener[] pcls = (ContentChangeListener[]) acsListeners.toArray(new ContentChangeListener[0]);
        for (int i = 0; i < pcls.length; i++) {
            pcls[i].contentChange(new ContentChangeEvent(this, value));
        }
    }


}
