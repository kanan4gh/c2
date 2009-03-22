/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.myorg.dgc.treeviewer;

import java.awt.event.ActionEvent;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.dg.util.DGLogger;
import org.myorg.dgc.api.Camera;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.actions.DeleteAction;
import org.openide.actions.OpenAction;
import org.openide.actions.RenameAction;
import org.openide.cookies.InstanceCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.BeanNode;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author akira
 */
public class DGCNode extends FilterNode {

    public static final String RootFolderName = "Coord";
    public static final String CameraFolderName = "Camera";
    public static final String RecorderFolderName = "Recorder";
    public static final String AcsFolderName = "Acs";
    public static String RootCameraPath = RootFolderName + "/" + CameraFolderName;
    public static String RootRecorderPath = RootFolderName + "/" + RecorderFolderName;
    public static String RootAcsPath = RootFolderName + "/" + AcsFolderName;

    public DGCNode(Node folderNode) {
        this(folderNode, new DGCFolderChildren(folderNode));
    }

    private DGCNode(Node folderNode, Children children) {
        super(folderNode, children);
    }

    public static DGCNode createRootDGCNode() throws DataObjectNotFoundException {
        FileSystem sfs = Repository.getDefault().getDefaultFileSystem();
        DataObject data = DataObject.find(sfs.getRoot().getFileObject(RootFolderName));
        Node folderNode = data.getNodeDelegate();
        return new RootDGCNode(folderNode);
    }

    public static class RootDGCNode extends DGCNode {

        public RootDGCNode(Node folderNode) {
            super(folderNode, new DGCRootFolderChildren(folderNode));
        }

        public RootDGCNode(Node folderNode, Children children) {
            super(folderNode, children);
        }

        @Override
        public Action[] getActions(boolean context) {
            return new Action[]{};
        }
    }

    private static class DGCRootFolderChildren extends FilterNode.Children {

        DGCRootFolderChildren(Node dgcFolderNode) {
            super(dgcFolderNode);
        }

        @Override
        protected Node[] createNodes(Node n) {
            DGLogger.getDefault().println("DGCRootFolderChildren.createNode:"+n.getName());
            if (n.getLookup().lookup(DataFolder.class) != null) {
                return new Node[]{new SubRootDGCNode(n)};
            } else {
                return new Node[0];
            }
        }
    }

    public static class SubRootDGCNode extends DGCNode {

        public SubRootDGCNode(Node folderNode) {
            super(folderNode, new DGCFolderChildren(folderNode));
        }

        public SubRootDGCNode(Node folderNode, Children children) {
            super(folderNode, children);
        }

        @Override
        public Action[] getActions(boolean context) {
            Lookup lookup = getLookup();
            DataFolder df = lookup.lookup(DataFolder.class);
            return new Action[]{
                        new AddFolderAction(df),
                        new AddEquipAction(df)
                    };
        }
    }

    @Override
    public Action[] getActions(boolean context) {
        Lookup lookup = getLookup();
        DataFolder df = lookup.lookup(DataFolder.class);
        return new Action[]{
                    new AddFolderAction(df),
                    new AddEquipAction(df),
                    SystemAction.get(RenameAction.class),
                    SystemAction.get(DeleteAction.class)
                };
    }

    private static class DGCFolderChildren extends FilterNode.Children {

        DGCFolderChildren(Node dgcFolderNode) {
            super(dgcFolderNode);
        }

        @Override
        protected Node[] createNodes(Node n) {
            DGLogger.getDefault().println("DGCFolderChildren.createNode:"+n.getName());
            if (n.getLookup().lookup(DataFolder.class) != null) {
                return new Node[]{new DGCNode(n)};
            } else {
                Camera camera = getCamera(n);
                if (camera != null) {
                    try {
                        Node[] answer = new Node[]{new EntryBeanNode(camera)};
                        DGLogger.getDefault().println("カメラ:" + camera.getName() + "を復元し、ノードを生成しました。");
                        return answer;
                    } catch (IntrospectionException ex) {
                        assert false : ex;
                        return new Node[0];
                    }
                }
            }
            // fail but best effort.
            return new Node[]{new FilterNode(n)};
        }
    }

    public static class EntryBeanNode extends FilterNode {

        private final Camera camera;

        public EntryBeanNode(Camera camera) throws IntrospectionException {
            super(new BeanNode<Camera>(camera),
                    Children.LEAF,
                    Lookups.fixed(
                    camera,
                    new EntryOpenCookie(camera)));
            this.camera = camera;
        }

        @Override
        public Action[] getActions(boolean context) {
            Lookup lookup = getLookup();
            Camera dobj = lookup.lookup(Camera.class);
            return new Action[]{
                        SystemAction.get(OpenAction.class),
                        new DeleteEquipAction(dobj)
                    };
        }

        @Override
        public Action getPreferredAction() {
            return getActions(false)[0];
        }

        @Override
        public String getHtmlDisplayName() {
            return camera.toString();
        }

        @Override
        public String getShortDescription() {
            return camera.toString();
        }
    }

    private static class EntryOpenCookie implements OpenCookie {

        private final Camera camera;

        EntryOpenCookie(Camera camera) {
            this.camera = camera;
        }

        public void open() {

            EditorTopComponent2 etc = EditorTopComponent2.getEditorComponent(camera.getName());
            etc.open();
            etc.requestActive();
            etc.setCamera(this.camera);
        }
    }

    private static Camera getCamera(Node node) {
        InstanceCookie ck = node.getCookie(InstanceCookie.class);
        if (ck == null) {
            throw new IllegalStateException("Bogus file in equips folder:" + node.getLookup().lookup(FileObject.class));
        }
        try {
            return (Camera) ck.instanceCreate();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    private static class AddFolderAction extends AbstractAction {

        private final DataFolder folder;

        public AddFolderAction(DataFolder df) {
            super(NbBundle.getMessage(DGCNode.class, "FN_addfolderbutton"));
            folder = df;
        }

        public void actionPerformed(ActionEvent e) {
            NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine(
                    NbBundle.getMessage(DGCNode.class, "FN_askfolder_msg"),
                    NbBundle.getMessage(DGCNode.class, "FN_askfolder_title"),
                    NotifyDescriptor.OK_CANCEL_OPTION,
                    NotifyDescriptor.PLAIN_MESSAGE);
            Object result = DialogDisplayer.getDefault().notify(nd);
            if (result.equals(NotifyDescriptor.OK_OPTION)) {
                final String folderString = nd.getInputText();
                try {
                    DataFolder.create(folder, folderString);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    private static class AddEquipAction extends AbstractAction {

        private final DataFolder folder;

        public AddEquipAction(DataFolder df) {
            super(NbBundle.getMessage(DGCNode.class, "FN_addequipbutton"));
            folder = df;
        }

        public void actionPerformed(ActionEvent e) {
            NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine(
                    NbBundle.getMessage(DGCNode.class, "FN_askequip_msg"),
                    NbBundle.getMessage(DGCNode.class, "FN_askequip_title"),
                    NotifyDescriptor.OK_CANCEL_OPTION,
                    NotifyDescriptor.PLAIN_MESSAGE);
            Object result = DialogDisplayer.getDefault().notify(nd);
            if (result.equals(NotifyDescriptor.OK_OPTION)) {
                Camera camera = new Camera(nd.getInputText());
                FileObject fld = folder.getPrimaryFile();
                try {
                    FileObject writeTo = fld.createData(camera.getName(), "ser");

                    FileLock lock = writeTo.lock();
                    try {
                        ObjectOutputStream str = new ObjectOutputStream(writeTo.getOutputStream(lock));
                        try {
                            camera.setPath(writeTo.getPath());
                            str.writeObject(camera);
                            DGLogger.getDefault().println("カメラ:" + camera.getName() + "を生成しました。");
                            EquipmentManager.getDefault().addCamera(camera);
                        } finally {
                            str.close();
                        }
                    } finally {
                        lock.releaseLock();
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }

            }
        }
    }

    public static class DeleteEquipAction extends AbstractAction {

        private final Camera camera;

        public DeleteEquipAction(Camera camera) {
            super("削除");
            this.camera = camera;
        }

        public void actionPerformed(ActionEvent e) {
            FileSystem sfs = Repository.getDefault().getDefaultFileSystem();
            FileObject fobj = sfs.findResource(camera.getPath());
            NotifyDescriptor cf = new NotifyDescriptor.Confirmation(
                    NbBundle.getMessage(DGCNode.class, "FN_confirmdelete_msg"),
                    NbBundle.getMessage(DGCNode.class, "FN_confirmdelete_title"),
                    NotifyDescriptor.OK_CANCEL_OPTION);
            if (DialogDisplayer.getDefault().notify(cf) == NotifyDescriptor.OK_OPTION) {
                try {
                    FileLock lock = fobj.lock();
                    fobj.delete(lock);
                    EquipmentManager.getDefault().removeCamera(camera);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }
}
