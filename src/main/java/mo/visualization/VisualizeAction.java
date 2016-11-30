package mo.visualization;

import java.util.ArrayList;
import mo.core.plugin.Extends;
import mo.core.plugin.Extension;
import mo.organization.Configuration;
import mo.organization.Participant;
import mo.organization.ProjectOrganization;
import mo.organization.Stage;
import mo.organization.StageAction;
import mo.organization.StagePlugin;

public class VisualizeAction implements StageAction {

    public static void main(String[] args) {
        
    }
    
    @Override
    public String getName() {
        return "Visualize";
    }

    @Override
    public void init(ProjectOrganization organization, Participant participant, Stage stage) {
        
        ArrayList<Configuration> configs = new ArrayList<>();
        for (StagePlugin plugin : stage.getPlugins()) {
            for (Configuration configuration : plugin.getConfigurations()) {
                configs.add(configuration);
            }
        }
        
        VisualizationDialog2 d = new VisualizationDialog2(configs, organization.getLocation());
        boolean accept = d.show();
        
        if (accept) {
            VisualizationPlayer p = new VisualizationPlayer(d.getConfigurations());
        } else {
            System.out.println("nou");
        }
    }
    
}