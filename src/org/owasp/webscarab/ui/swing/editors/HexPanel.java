/*
 * HexEditor.java
 *
 * Created on November 4, 2003, 8:23 AM
 */

package org.owasp.webscarab.ui.swing.editors;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.Font;

/**
 *
 * @author  rdawes
 */
public class HexPanel extends javax.swing.JPanel implements ByteArrayEditor {
    
    private HexTableModel _tableModel = null;
    
    private boolean _editable = false;
    int _columns = 16;
    
    /** Creates new form HexEditor */
    public HexPanel() {
        initComponents();
        
        _tableModel = new HexTableModel(_columns);
        hexTable.setModel(_tableModel);
        hexTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
        hexTable.getTableHeader().setReorderingAllowed(false);
        TableColumnModel colModel = hexTable.getColumnModel();
        // FIXME : use FontMetrics to get the real width of the font
        for (int i=0; i<_columns+2; i++) {
            colModel.getColumn(i).setPreferredWidth(2*9);
            colModel.getColumn(i).setResizable(false);
        }
        colModel.getColumn(0).setPreferredWidth(8*9);
        colModel.getColumn(_columns+1).setPreferredWidth(_columns*9);
    }
    
    public String getName() {
        return "Hex";
    }
    
    public String[] getContentTypes() {
        return new String[] { ".*" };
    }
    
    public void setEditable(boolean editable) {
        _editable = editable;
        _tableModel.setEditable(editable);
        // we could do things like make buttons visible and invisible here
    }
    
    public void setBytes(byte[] bytes) {
        _tableModel.setBytes(bytes);
    }
    
    public boolean isModified() {
        return false;
    }
    
    public byte[] getBytes() {
        return _tableModel.getBytes();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        hexTable = new javax.swing.JTable();

        setLayout(new java.awt.GridBagLayout());

        hexTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        hexTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(hexTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable hexTable;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    
    
    public static void main(String[] args) {
        javax.swing.JFrame top = new javax.swing.JFrame("Hex Editor");
        top.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                System.exit(0);
            }
        });
        
        HexPanel hp = new HexPanel();
        top.getContentPane().add(hp);
        top.setBounds(100,100,600,400);
        try {
            hp.setBytes(new byte[] {0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2a, 0x2b, 0x2c, 0x2d, 0x2e, 0x2f});
            hp.setEditable(true);
            // he.setModel(new DefaultHexDataModel(new byte[0], true));
            top.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private class HexTableModel extends AbstractTableModel {

        private byte[] _data = new byte[0];
        private int _columns = 8;
        private boolean _editable = false;
        
        public HexTableModel() {
        }
        
        public HexTableModel(int columns) {
            _columns = columns;
        }
        
        public void setBytes(byte[] bytes) {
            _data = bytes;
            fireTableDataChanged();
        }
        
        public byte[] getBytes() {
            return _data;
        }
        
        public void setEditable(boolean editable) {
            if (editable != _editable) {
                _editable = editable;
                fireTableDataChanged();
            }
        }
        
        public String getColumnName(int columnIndex) {
            if (columnIndex == 0) {
                return "Position";
            } else if (columnIndex-1 < _columns) {
                return Integer.toHexString(columnIndex-1).toUpperCase();
            } else {
                return "String";
            }
        }
        
        public int getColumnCount() {
            return _columns + 2;
        }
        
        public int getRowCount() {
            if (_data == null || _data.length == 0) {
                return 0;
            }
            if (_data.length % _columns == 0) {
                return (int) (_data.length / _columns);
            } else {
                return (int) (_data.length / _columns) + 1;
            }
        }
        
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return pad(Integer.toHexString(rowIndex * _columns).toUpperCase(), '0', 8);
            } else if (columnIndex-1 < _columns) {
                int position = rowIndex * _columns + columnIndex-1;
                if (position < _data.length) {
                    int i = _data[position];
                    if (i<0) { i = i + 256; }
                    return pad(Integer.toString(i, 16).toUpperCase(),'0',2);
                } else {
                    return "";
                }
            } else {
                int start = rowIndex * _columns;
                StringBuffer buff = new StringBuffer();
                for (int i=0; i < _columns; i++) {
                    int pos = start + i;
                    if (pos >= _data.length) { return buff.toString(); }
                    if (_data[pos] < 32 || _data[pos] > 126) {
                        buff.append(".");
                    } else {
                        buff.append((char) _data[pos]);
                    }
                }
                return buff.toString();
            }
        }
        
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (columnIndex == 0 || columnIndex>_columns) {
                return false;
            }
            int position = rowIndex * _columns + columnIndex-1;
            if (position < _data.length) {
                return _editable;
            }
            return false;
        }
        
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            int position = rowIndex * _columns + columnIndex-1;
            if (position >= _data.length) {
                System.out.println("Out of range");
                return;
            }
            if (aValue instanceof String) {
                try {
                    _data[position] = new Integer(Integer.parseInt((String) aValue, 16)).byteValue();
                    fireTableCellUpdated(rowIndex, _columns + 1);
                } catch (NumberFormatException nfe) {
                    System.out.println("Number format error : " + nfe);
                }
            } else {
                System.out.println("Value is a " + aValue.getClass().getName());
            }
        }
        
        private String pad(String initial, char padchar, int length) {
            if (initial.length() >= length) {
                return initial;
            }
            StringBuffer buff = new StringBuffer(length);
            for (int i=0; i<length - initial.length(); i++) {
                buff.append(padchar);
            }
            buff.append(initial);
            return buff.toString();
        }
    }
    
}
