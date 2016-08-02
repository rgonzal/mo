package mo.core.ui.menubar;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import mo.core.plugin.Plugin;
import mo.core.plugin.PluginRegistry;
import static mo.core.ui.menubar.IMenuBarItem.AFTER;
import static mo.core.ui.menubar.IMenuBarItem.BEFORE;
import static mo.core.ui.menubar.IMenuBarItem.UNDER;

/**
 *
 * @author Celso Gutiérrez <celso.gutierrez@usach.cl>
 */
public class MenuBar extends JMenuBar {
    
    private static MenuBar mb;
    
    private final List<JMenuItem> menuItems;
    
    private MenuBar() {
        super();
        menuItems = new ArrayList<>();
    }
    
    private void init() {
        JMenu m = new JMenu("File");
        addItem(m, 0, null);
        
        m = new JMenu("Window");
        addItem(m, 1, null);
        
        m = new JMenu("Plugins");
        addItem(m, 2, null);
    }
    
    public void setup() {
    
        List<Plugin> plugins = 
                PluginRegistry
                        .getInstance()
                        .getPluginsFor("mo.core.ui.menubar.IMenuBarItem");
        
        plugins.stream().forEach((plugin) -> {
            
            IMenuBarItem i;
            try {
                
                i = (IMenuBarItem) plugin.getClazz().newInstance();
                
                addItem(i.getItem(),
                        i.getRelativePosition(),
                        i.getRelativeTo().toLowerCase());
                
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(MenuBar.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        });
    }
    
    public static MenuBar getInstance() {
        if (mb == null) {
            mb = new MenuBar();
            mb.init();
            mb.setup();
        }
        return mb;
    }
    
    public JMenuItem getNamedItem(String name) {

        for (JMenuItem menuItem : menuItems) {
            if (menuItem.getName().compareTo(name) == 0) {
                System.out.println(menuItem.getName());
                return menuItem;
            }
        }
        
        return null;
    }
    
    private void addItem(JMenuItem item, int position, String relativeTo) {
        
        if (item.getName() == null || item.getName().isEmpty())
            if (item.getText() != null && !item.getText().isEmpty())
                item.setName(item.getText().toLowerCase());
        
        JMenuItem relative = 
                relativeTo != null ? getNamedItem(relativeTo) : null;
        
        
        if (relative == null) {
            mb.add(item);
        } else {
            switch (position){
                case UNDER: {
                    //only JMenu can have items
                    if (relative instanceof JMenu) {
                        relative.add(item);
                    }
                    break;
                }
                case BEFORE: {
                    int i = relative.getParent().getComponentZOrder(relative);
                    relative.getParent().add(item, i);
                    break;
                }
                case AFTER: {
                    int i = relative.getParent().getComponentZOrder(relative);
                    relative.getParent().add(item, i + 1);
                    break;
                }
                default: {
                    if (position >= -1)
                        relative.add(item, position);
                    else
                        relative.add(item);
                    break;
                }
            }
        }
        menuItems.add(item);
    }
    
    @Override
    public String toString() {
        String result = "";
        return result;
    }
    
    //Test
    public static void main(String[] args) {

        JFrame f = new JFrame("asd");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.setSize(300, 300);

        //MenuBar m = new MenuBar1();
        System.out.println(PluginRegistry.getInstance().getPlugins().size());
        for (Plugin arg : PluginRegistry.getInstance().getPlugins()) {
            System.out.println(arg);
        }
        //MenuBar.getInstance().add(m);
        f.setJMenuBar(MenuBar.getInstance());
        //f.setJMenuBar(MenuBar1.getInstance());
        f.setVisible(true);
    }
}