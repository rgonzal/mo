package mo.organization;

import javax.swing.JMenuItem;
import mo.core.plugin.ExtensionPoint;

@ExtensionPoint
public interface CaptureProvider {
    JMenuItem getMenu();
    
}
