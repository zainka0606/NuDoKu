package sudoku;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.IOException;

public class ListDragAndDrop implements DragSourceListener, DropTargetListener, DragGestureListener {
    private static final DataFlavor stepConfigDataFlavor = new DataFlavor(StepConfig.class, "sudoku.StepConfig");
    private static final DataFlavor[] supportedFlavors = new DataFlavor[]{stepConfigDataFlavor};
    private DragSource dragSource;
    private DropTarget dropTarget;
    private int draggedIndex = -1;
    private StepConfig dropTargetCell;
    private JList list;
    private ListDragAndDropChange panel;
    private JPanel cPanel;

    public ListDragAndDrop(JList list, ListDragAndDropChange panel, JPanel cPanel) {
        this.list = list;
        this.panel = panel;
        this.cPanel = cPanel;
        this.dragSource = new DragSource();
        this.dragSource.createDefaultDragGestureRecognizer(list, 2, this);
        this.dropTarget = new DropTarget(list, this);
    }

    @Override
    public void dragEnter(DragSourceDragEvent dsde) {
    }

    @Override
    public void dragOver(DragSourceDragEvent dsde) {
    }

    @Override
    public void dropActionChanged(DragSourceDragEvent dsde) {
    }

    @Override
    public void dragExit(DragSourceEvent dse) {
    }

    @Override
    public void dragDropEnd(DragSourceDropEvent dsde) {
        this.dropTargetCell = null;
        this.draggedIndex = -1;
        this.panel.setDropLocation(-1, null);
        this.list.repaint();
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        if (dtde.getSource() != this.dropTarget) {
            dtde.rejectDrag();
            this.panel.setDropLocation(-1, null);
        } else {
            dtde.acceptDrag(2);
        }
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        if (dtde.getSource() != this.dropTarget) {
            dtde.rejectDrag();
            this.panel.setDropLocation(-1, null);
        } else {
            Point dragPoint = dtde.getLocation();
            int index = this.list.locationToIndex(dragPoint);
            if (index == -1) {
                this.dropTargetCell = null;
            } else {
                this.dropTargetCell = (StepConfig) this.list.getModel().getElementAt(index);
            }

            this.panel.setDropLocation(index, this.dropTargetCell);
            this.list.repaint();
        }
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        if (dtde.getSource() != this.dropTarget) {
            dtde.rejectDrop();
            this.panel.setDropLocation(-1, null);
        } else {
            Point dropPoint = dtde.getLocation();
            int index = this.list.locationToIndex(dropPoint);
            boolean dropped = false;
            if (index != -1 && index != this.draggedIndex) {
                dtde.acceptDrop(2);
                this.panel.setDropLocation(-1, null);
                this.panel.moveStep(this.draggedIndex, index);
                dropped = true;
                dtde.dropComplete(dropped);
            } else {
                dtde.rejectDrop();
                this.panel.setDropLocation(-1, null);
            }
        }
    }

    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {
        Point clickPoint = dge.getDragOrigin();
        int index = this.list.locationToIndex(clickPoint);
        if (index != -1) {
            StepConfig target = (StepConfig) this.list.getModel().getElementAt(index);
            Transferable trans = new ListDragAndDrop.RJLTransferable(target);
            this.draggedIndex = index;
            this.dragSource.startDrag(dge, Cursor.getDefaultCursor(), trans, this);
        }
    }

    class RJLTransferable implements Transferable {
        private StepConfig object;

        RJLTransferable(StepConfig object) {
            this.object = object;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return ListDragAndDrop.supportedFlavors;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(ListDragAndDrop.stepConfigDataFlavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (this.isDataFlavorSupported(flavor)) {
                return this.object;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }
    }
}
