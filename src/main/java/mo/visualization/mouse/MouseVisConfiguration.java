package mo.visualization.mouse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mo.capture.mouse.MouseCaptureConfiguration;
import mo.organization.Configuration;
import mo.visualization.VisualizableConfiguration;

public class MouseVisConfiguration implements VisualizableConfiguration {
    
    String id;
    List<String> compatibleCreators;
    List<File> files;
    MousePlayer player;

    public MouseVisConfiguration() {
        compatibleCreators = new ArrayList<>();
        compatibleCreators.add("mo.capture.mouse.MouseRecorder");
        files = new ArrayList<>();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public File toFile(File parent) {
        File f = new File(parent, "mouse-visualization_"+id+".xml");
        try {
            f.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(MouseVisConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }
        return f;
    }

    @Override
    public Configuration fromFile(File file) {
        String fileName = file.getName();

        if (fileName.contains("_") && fileName.contains(".")) {
            String name = fileName.substring(fileName.indexOf("_")+1, fileName.lastIndexOf("."));
            MouseVisConfiguration config = new MouseVisConfiguration();
            config.id = name;
            return config;
        }
        return null;
    }

    @Override
    public List<String> getCompatibleCreators() {
       return compatibleCreators;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setSpeed(double factor) {
        ensurePlayerCreated();
        player.setSpeed(factor);
    }

    @Override
    public void pause() {
        ensurePlayerCreated();
        player.pause();
    }

    @Override
    public void seek(long millis) {
        ensurePlayerCreated();
        player.seek(millis);
    }

    @Override
    public long getStart() {
        ensurePlayerCreated();
        return player.getStart();
    }

    @Override
    public long getEnd() {
        ensurePlayerCreated();
        return player.getEnd();
    }

    @Override
    public void play() {
        ensurePlayerCreated();
        player.play();
    }
    
    private void ensurePlayerCreated() {
        if (player == null && !files.isEmpty()) {
            player = new MousePlayer(files.get(0));
        }
    }

    @Override
    public void addFile(File file) {
        if ( !files.contains(file) ) {
            this.files.add(file);
        }
    }

    @Override
    public void removeFile(File file) {
        File toRemove = null;
        
        for (File f : files) {
            if (f.equals(file)) {
                toRemove = f;
                break;
            }
        }
        
        if (toRemove != null) {
            files.remove(toRemove);
        }
    }

}
