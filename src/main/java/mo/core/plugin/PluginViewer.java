package mo.core.plugin;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import mo.core.ui.Utils;
import mo.core.ui.dockables.DockableElement;
import mo.core.ui.dockables.DockablesRegistry;
import mo.core.ui.dockables.IDockableElementProvider;
import mo.core.ui.menubar.IMenuBarItemProvider;

@Extension(
        xtends = {
            @Extends(
                    extensionPointId = "mo.core.ui.menubar.IMenuBarItemProvider"
            ),
            @Extends(
                    extensionPointId = "mo.core.ui.dockables.IDockableElementProvider"
            )
        }
)
public class PluginViewer implements IMenuBarItemProvider, IDockableElementProvider {

    JMenuItem menuItem = new JMenuItem("Plugin Viewer");
    DockableElement dockable;
    JTree pluginsTree, extPointsTree;
    boolean registered = false;
    JTabbedPane tabbedPane = new JTabbedPane();

    public PluginViewer() {
        dockable = new DockableElement("PluginViewer");
        menuItem.addActionListener((ActionEvent e) -> {
            menuItemClicked();
        });

        PluginCellRenderer renderer = new PluginCellRenderer();
        pluginsTree = new JTree(new DefaultMutableTreeNode("Plugins"));
        pluginsTree.setCellRenderer(renderer);

        DefaultTreeModel model = (DefaultTreeModel) pluginsTree.getModel();
        List<Plugin> plugins = PluginRegistry.getInstance().getPlugins();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        for (Plugin plugin : plugins) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(plugin);
            root.add(node);
            for (Dependency dependency : plugin.getDependencies()) {
                DefaultMutableTreeNode d = new DefaultMutableTreeNode(dependency);
                node.add(d);
            }
        }

        JScrollPane scroll = new JScrollPane(pluginsTree);

        tabbedPane.addTab("Plugins", scroll);
        pluginsTree.expandRow(2);

        extPointsTree = new JTree(new DefaultMutableTreeNode("Extension Points"));
        extPointsTree.setCellRenderer(renderer);
        DefaultTreeModel modelX = (DefaultTreeModel) extPointsTree.getModel();
        List<ExtPoint> extensionPoints = PluginRegistry.getInstance().getExtPoints();
        DefaultMutableTreeNode rootX = (DefaultMutableTreeNode) modelX.getRoot();
        for (ExtPoint extPoint : extensionPoints) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(extPoint);
            rootX.add(node);
            for (Plugin plugin : extPoint.getPlugins()) {
                DefaultMutableTreeNode d = new DefaultMutableTreeNode(plugin);
                node.add(d);
            }
        }
        JScrollPane scrollX = new JScrollPane(extPointsTree);
        tabbedPane.addTab("Extension Points", scrollX);

        TreeNode r = (TreeNode) pluginsTree.getModel().getRoot();
        expandAll(pluginsTree, new TreePath(r));

        r = (TreeNode) extPointsTree.getModel().getRoot();
        expandAll(extPointsTree, new TreePath(r));

        dockable.add(tabbedPane);
    }

    private void menuItemClicked() {
        if (!registered) {
            DockablesRegistry dr = DockablesRegistry.getInstance();
            dr.addAppWideDockable(dockable);
            registered = true;
        }
    }

    @Override
    public JMenuItem getItem() {
        return menuItem;
    }

    @Override
    public int getRelativePosition() {
        return IMenuBarItemProvider.UNDER;
    }

    @Override
    public String getRelativeTo() {
        return "plugins";
    }

    @Override
    public DockableElement getElement() {
        return dockable;
    }

    private void expandAll(JTree tree, TreePath parent) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path);
            }
        }
        tree.expandPath(parent);
        // tree.collapsePath(parent);
    }

    @Override
    public String getDockableGroup() {
        return null;
    }

    private static class PluginCellRenderer implements TreeCellRenderer {

        private final DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

        private Icon interfaceIcon = Utils.createImageIcon("interface.png", getClass());
        
        private Icon pluginIcon = Utils.createImageIcon("plugin.png", getClass());

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            DefaultTreeCellRenderer returnValue = (DefaultTreeCellRenderer) defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
                Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
                if (userObject instanceof Plugin) {
                    returnValue = getTreeCellRendererComponentForPlugin(returnValue, (Plugin) userObject);
                } else if (userObject instanceof ExtPoint) {
                    returnValue = getTreeCellRendererComponentForExtPoint(returnValue, (ExtPoint) userObject);
                } else if (userObject instanceof Dependency) {
                    Dependency d = (Dependency) userObject;
                    if (d.isPresent()) {
                        returnValue = getTreeCellRendererComponentForExtPoint(returnValue, d.getExtensionPoint());
                    } else {
                        returnValue = getTreeCellRendererComponentForDependency((DefaultTreeCellRenderer) (new DefaultTreeCellRenderer()).getTreeCellRendererComponent(tree, value, leaf, expanded, leaf, row, hasFocus), d);
                    }
                }
            }

            return returnValue;
        }

        private DefaultTreeCellRenderer getTreeCellRendererComponentForPlugin(DefaultTreeCellRenderer c, Plugin o) {
            c.setText(o.getName());
            c.setIcon(pluginIcon);
            return c;
        }

        private DefaultTreeCellRenderer getTreeCellRendererComponentForExtPoint(DefaultTreeCellRenderer c, ExtPoint o) {
            c.setText(o.getName());
            c.setIcon(interfaceIcon);
            return c;
        }

        private DefaultTreeCellRenderer getTreeCellRendererComponentForDependency(DefaultTreeCellRenderer c, Dependency o) {
            c.setForeground(Color.red);
            c.setBackgroundSelectionColor(Color.white);
            c.setText(o.getId());
            return c;
        }
    }
}
